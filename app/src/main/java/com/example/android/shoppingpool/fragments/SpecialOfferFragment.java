package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.FindActivity;
import com.example.android.shoppingpool.activities.ItemDetailActivity;
import com.example.android.shoppingpool.models.AllProducts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.

 */
public class SpecialOfferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;

    private RecyclerView productsList;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference ProductsRef;
    private ProgressBar progressBar;
    String strtext;

    String type, variantId;

    public SpecialOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment SpecialOfferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpecialOfferFragment newInstance(String param1, String param2) {
        SpecialOfferFragment fragment = new SpecialOfferFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_special_offer, container, false);

        FindActivity activity = (FindActivity)getActivity();
        strtext=activity.getMyData();
        StringTokenizer p =new StringTokenizer(strtext,"/");
        variantId = "SIZE";
        while(p.hasMoreTokens()){
            type=p.nextToken().trim();
        }
        System.out.println(type);

        if(type.equalsIgnoreCase("Books"))
            variantId = "EDITION";

        if(type.equalsIgnoreCase("Groceries"))
            variantId = "WEIGHT";

        if(type.equalsIgnoreCase("Luggage"))
            variantId = "SIZE";

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("promoted_products").child(type);
        productsList = (RecyclerView) view.findViewById(R.id.allProductsRecyclerView2);
        productsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        productsList.setLayoutManager(linearLayoutManager);
        progressBar = view.findViewById(R.id.allProductsProgressBar2);
        DisplayAllProducts();

        return view;
    }

    private void DisplayAllProducts() {

        progressBar.setVisibility(View.VISIBLE);
        productsList.setVisibility(View.INVISIBLE);
        Query query = ProductsRef;

        FirebaseRecyclerOptions<AllProducts> options =
                new FirebaseRecyclerOptions.Builder<AllProducts>()
                        .setQuery(query, new SnapshotParser<AllProducts>() {
                            @NonNull
                            @Override
                            public AllProducts parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new AllProducts(snapshot.child("SOLD_BY").getValue().toString(),
                                        snapshot.child("NAME").getValue().toString(),
                                        snapshot.child(variantId).getValue().toString(),
                                        snapshot.child("URL").getValue().toString(),
                                        snapshot.child("QTY").getValue().toString(),
                                        snapshot.child("DISCOUNT").getValue()!=null?snapshot.child("DISCOUNT").getValue().toString():null,
                                        snapshot.child("PRICE").getValue().toString(),
                                        snapshot.child("OFFER").getValue()!=null?snapshot.child("OFFER").getValue().toString():null,
                                        snapshot.child("RATING").getValue()!=null?snapshot.child("RATING").getValue().toString():null);

                            }
                        })
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllProducts, AllProductsFragment.ProdutsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProductsFragment.ProdutsViewHolder holder, int position, @NonNull AllProducts model) {

                final String ProductKey = getRef(position).getKey();

                holder.setShopName(model.getShopName());
                holder.setProductName(model.getProductName());
                holder.setProductVariant(model.getProductVariant());
                holder.setProductImage(getContext(), model.getProductImage());
                holder.setStockValue(model.getStockValue());
                holder.setPrice(model.getDiscountPercent(),model.getPrice(),model.getOffer());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickProductIntent = new Intent(getContext(), ItemDetailActivity.class);
                        clickProductIntent.putExtra("Product_Key", (type+"+"+ProductKey));
                        startActivity(clickProductIntent);
                    }
                });
            }


            @NonNull
            @Override
            public AllProductsFragment.ProdutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_products_layout, parent, false);

                return new AllProductsFragment.ProdutsViewHolder(view);
            }
        };
        productsList.setAdapter(firebaseRecyclerAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        productsList.setVisibility(View.VISIBLE);

    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    public static class ProdutsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public  ProdutsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setShopName(final String shopName)
        {
            DatabaseReference ShopRef;
            final TextView shopname = (TextView) mView.findViewById(R.id.productShopName);
            ShopRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("sellers-list").child(shopName).child("Shop-name");
            ShopRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    shopname.setText(dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setProductName(String productName)
        {
            TextView productname = (TextView) mView.findViewById(R.id.producttxtTitle);
            productname.setText(productName);
        }

        public void setProductVariant(String productVariant)
        {
            StringTokenizer k=new StringTokenizer(productVariant,",");
            TextView productvariant = (TextView) mView.findViewById(R.id.producttxtVariant);
            TextView var1=(TextView) mView.findViewById(R.id.var1);
            TextView var2=(TextView) mView.findViewById(R.id.var2);
            TextView var3=(TextView) mView.findViewById(R.id.var3);
            TextView var4=(TextView) mView.findViewById(R.id.var4);
            TextView var5=(TextView) mView.findViewById(R.id.var5);
            TextView var6=(TextView) mView.findViewById(R.id.var6);
            TextView var7=(TextView) mView.findViewById(R.id.var7);
            CardView variant1 = (CardView) mView.findViewById(R.id.variant1);
            CardView variant2 = (CardView) mView.findViewById(R.id.variant2);
            CardView variant3 = (CardView) mView.findViewById(R.id.variant3);
            CardView variant4 = (CardView) mView.findViewById(R.id.variant4);
            CardView variant5 = (CardView) mView.findViewById(R.id.variant5);
            CardView variant6 = (CardView) mView.findViewById(R.id.variant6);
            CardView variant7 = (CardView) mView.findViewById(R.id.variant7);

            productvariant.setText(("Variants Available: "));
            final int count = k.countTokens();
            int i=0;
            for(i=0;i<count && i<7;i++)
            {
                if(i==0)
                {
                    var1.setText(k.nextToken().trim());
                }
                if(i==1)
                {
                    var2.setText(k.nextToken().trim());
                }
                if(i==2)
                {
                    var3.setText(k.nextToken().trim());
                }
                if(i==3)
                {
                    var4.setText(k.nextToken().trim());
                }
                if(i==4)
                {
                    var5.setText(k.nextToken().trim());
                }
                if(i==5)
                {
                    var6.setText(k.nextToken().trim());
                }
                if(i==6)
                {
                    var7.setText(k.nextToken().trim());
                }

            }

            for(i=7;i>count;i--){
                if(i==7)
                {
                    variant7.setVisibility(View.INVISIBLE);
                }
                if(i==6)
                {
                    variant6.setVisibility(View.INVISIBLE);
                }
                if(i==5)
                {
                    variant5.setVisibility(View.INVISIBLE);
                }
                if(i==4)
                {
                    variant4.setVisibility(View.INVISIBLE);
                }
                if(i==3)
                {
                    variant3.setVisibility(View.INVISIBLE);
                }
                if(i==2)
                {
                    variant2.setVisibility(View.INVISIBLE);
                }
                if(i==1)
                {
                    variant1.setVisibility(View.INVISIBLE);
                }


            }
        }

        public void setProductImage (Context ctxl, String productImage)
        {
            ImageView productimage = (ImageView) mView.findViewById(R.id.ProductImageView);
            Picasso.with(ctxl).load(productImage).into(productimage);
        }

        public void setStockValue (String stockValue)
        {
            int sum=0;
            StringTokenizer k=new StringTokenizer(stockValue,",");
            while(k.hasMoreTokens()){
                sum = sum+Integer.parseInt(k.nextToken().trim());
            }
            TextView stockvalue = (TextView) mView.findViewById(R.id.productStockLimitText);
            stockvalue.setText((""+sum));
        }

        public void setPrice (String discountPercent, String Price, String Offer)
        {
            TextView discountpercent = (TextView) mView.findViewById(R.id.producttxtDiscount);
            TextView originalprice = (TextView) mView.findViewById(R.id.producttxtPrice);
            TextView offer = (TextView) mView.findViewById(R.id.productOffer);
            TextView price = (TextView) mView.findViewById(R.id.productdiscountedtxtPrice);
            if(discountPercent==null  && (Offer==null || Offer.equalsIgnoreCase(""))) {
                StringTokenizer p=new StringTokenizer(Price,",");
                String s="";
                s="₹"+p.nextToken().trim();
                price.setText(("Starting Range: "+s));
                discountpercent.setVisibility(View.INVISIBLE);
                originalprice.setVisibility(View.INVISIBLE);
                offer.setVisibility(View.INVISIBLE);
            }
            else
            {
                if(Double.parseDouble(discountPercent) == 0.0)
                {
                    offer.setText(Offer);
                    StringTokenizer p=new StringTokenizer(Price,",");
                    String s="";

                    s="₹"+p.nextToken().trim();
                    price.setText(("Cost: "+s));
                    discountpercent.setVisibility(View.INVISIBLE);
                    originalprice.setVisibility(View.INVISIBLE);
                }

                else if((Offer==null || Offer.equalsIgnoreCase("")) && Double.parseDouble(discountPercent) != 0.0)
                {
                    discountpercent.setText((discountPercent+"%"));
                    StringTokenizer prices = new StringTokenizer(Price,",");
                    String s=prices.nextToken().trim();
                    Double d=Double.parseDouble(discountPercent);
                    Double p = Double.parseDouble(s);
                    Double finalPrice = p-(p*d/100);
                    String s1=""+finalPrice;



                    price.setText(("Cost: "+s1));
                    originalprice.setText((" on "+s));
                    offer.setVisibility(View.INVISIBLE);
                }

                else
                {
                    discountpercent.setText((discountPercent+"%"));
                    StringTokenizer prices = new StringTokenizer(Price,",");
                    String s=prices.nextToken().trim();
                    Double d=Double.parseDouble(discountPercent);
                    Double p = Double.parseDouble(s);
                    Double finalPrice = p-(p*d/100);
                    String s1=""+finalPrice;



                    price.setText(("Cost: "+s1));
                    originalprice.setText((" on "+s));
                    offer.setText(Offer);
                }
            }
        }




    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
