package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.ItemDetailActivity;
import com.example.android.shoppingpool.activities.JSONParser;
import com.example.android.shoppingpool.adapter.RecyclerViewAdapter;
import com.example.android.shoppingpool.models.AllProducts;
import com.example.android.shoppingpool.models.RecommendedProducts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.

 */
public class RecommenderFragment extends Fragment {
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

    private String HTTP_JSON_URL;

    private String iniURL = "http://18.218.190.44/recomendItemtoUser?userID=";
    private FirebaseAuth mAuth;
    String currentUserID;
    private String fullname;
    private DatabaseReference UsersRef, RecommedRef;
    private RecyclerView productsList;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private DatabaseReference ProductsRef;
    private ProgressBar progressBar;
    private String category;
    String name;
    private List<RecommendedProducts> ListOfProduct;
    String variantId;
    private String name1=null,color=null, edition=null, gender=null, price=null, qty=null, size=null, soldby=null, url=null, water=null, weight=null;

    private RecyclerView recyclerView;
    private RecommenderAdapter mAdapter;
    private List<RecommendedProducts> recommendList = new ArrayList<>();
    public RecommenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommenderFragment newInstance(String param1, String param2) {
        RecommenderFragment fragment = new RecommenderFragment();
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
        View view =  inflater.inflate(R.layout.fragment_recommender, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        ListOfProduct = new ArrayList<>();
        progressBar = view.findViewById(R.id.allProductsProgressBar3);


        SharedPreferences sp = getActivity().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", null);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefName = sp.getString("prefName", null);
        fullname = prefName;
        System.out.println(fullname);
        StringTokenizer k=new StringTokenizer(fullname);
        fullname=k.nextToken().trim();
        HTTP_JSON_URL = iniURL+fullname+"&count=30";
        RecommedRef = FirebaseDatabase.getInstance().getReference().child("RecommendedProducts").child(fullname);
        JSON_HTTP_CALL();
        recyclerView = (RecyclerView) view.findViewById(R.id.allProductsRecyclerView3);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        return view;
    }


    private void JSON_HTTP_CALL() {


        new JSONParse().execute();


    }

    private class JSONParse extends AsyncTask<String,String,JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            try {
                progressBar.setVisibility(View.INVISIBLE);
                ParseJSonResponse(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            progressBar.setVisibility(View.VISIBLE);
            JSONObject json = jParser.getJSONFromUrl(HTTP_JSON_URL);
            return json;
        }

    }

    private void ParseJSonResponse(JSONObject array) throws JSONException {

        if(ListOfProduct!=null){
            ListOfProduct.clear();
            mAdapter = new RecommenderAdapter(ListOfProduct);
            recyclerView.setAdapter(mAdapter);

        }
        //ArrayClear();
        if (ListOfProduct != null) {
            ListOfProduct.clear();
        }

        progressBar.setVisibility(View.VISIBLE);
        JSONObject jarr = array;
        Iterator<String> keys = jarr.keys();
        JSONArray recList = jarr.optJSONArray("recomms");
        for(int i=0;i<recList.length();i++)
        {
            String discount=null, offer=null, rating=null;
            JSONObject ep = recList.getJSONObject(i);
            name = ep.optString("id");
            JSONObject values = ep.getJSONObject("values");
            category = values.getString("CATEGORY");
            name = values.optString("NAME");
            if(values.optString("Discount")!=null)
                discount = values.optString("Discount");
            if(values.optString("Offer")!=null)
                offer = values.optString("Offer");
            if(values.optString("Rating")!=null)
                rating = values.optString("Rating");
            if(category.equalsIgnoreCase("Clothing") || category.equalsIgnoreCase("Footwear")){
                color = values.getString("COLOR");
                gender = values.getString("GENDER");
                name1 = name;
                price = values.getString("PRICE");
                qty = values.getString("QTY");
                size = values.getString("SIZE");
                soldby = values.getString("SOLD_BY");
                url = values.getString("URL");
                variantId = size;


            }

            if(category.equalsIgnoreCase("Groceries")){
                name1 = name;
                price = values.getString("PRICE");
                qty = values.getString("QTY");
                soldby = values.getString("SOLD_BY");
                url = values.getString("URL");
                weight = values.getString("WEIGHT");
                variantId = weight;


            }
            if(category.equalsIgnoreCase("Luggage")){
                name1 = name;
                price = values.getString("PRICE");
                qty = values.getString("QTY");
                size = values.getString("SIZE");
                soldby = values.getString("SOLD_BY");
                url = values.getString("URL");
                water = values.getString("WATER_RESSISTANT");
                variantId = size;


            }
            if(category.equalsIgnoreCase("Books")){
                name1 = name;
                price = values.getString("PRICE");
                qty = values.getString("QTY");
                soldby = values.getString("SOLD_BY");
                url = values.getString("URL");
                edition = values.getString("EDITION");
                variantId = edition;

            }

            RecommendedProducts eachProduct=new RecommendedProducts(category, soldby, name1, variantId, url, qty, discount, price, offer, rating);
            ListOfProduct.add(eachProduct);

        }

        progressBar.setVisibility(View.INVISIBLE);
        mAdapter = new RecommenderAdapter(ListOfProduct);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    private void DisplayAllProducts() {


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public class RecommenderAdapter extends RecyclerView.Adapter<RecommenderAdapter.ProdutsViewHolder> {


        private List<RecommendedProducts> productsList;

        public RecommenderAdapter(List<RecommendedProducts> recommendList) {
            productsList = recommendList;
        }


        @NonNull
        @Override
        public RecommenderAdapter.ProdutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_products_layout, parent, false);

            return new ProdutsViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull RecommenderAdapter.ProdutsViewHolder holder, int position) {
            RecommendedProducts model = productsList.get(position);
            holder.setShopName(model.getShopName());
            holder.setProductName(model.getProductName());
            holder.setProductVariant(model.getProductVariant());
            holder.setProductImage(getContext(), model.getProductImage());
            holder.setStockValue(model.getStockValue());
            holder.setPrice(model.getDiscountPercent(),model.getPrice(),model.getOffer());

            final String cat = model.getCategory();
            final String soldBy = model.getShopName();
            final String pro = model.getProductName();
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clickProductIntent = new Intent(getContext(), ItemDetailActivity.class);
                    clickProductIntent.putExtra("Product_Key", (cat+"+"+pro+"_"+soldBy));
                    startActivity(clickProductIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return productsList.size();
        }

        public class ProdutsViewHolder extends RecyclerView.ViewHolder {

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
                if((discountPercent==null  || discountPercent.equalsIgnoreCase("")) && (Offer==null || Offer.equalsIgnoreCase(""))) {
                    StringTokenizer p=new StringTokenizer(Price,",");
                    String s="";
                    s="₹"+p.nextToken().trim();
                    price.setText(("Cost: "+s));
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
    }

    // TODO: Rename method, update argument and hook method into UI event
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
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
