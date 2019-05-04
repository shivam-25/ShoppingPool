package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.ItemDetailActivity;
import com.example.android.shoppingpool.activities.JSONParser;
import com.example.android.shoppingpool.models.bookedProducts;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private DatabaseReference bookedProductRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    SharedPreferences sp ;
    private RecyclerView bookedList;
    private ProgressBar progressBar;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private TextView shopBooked, dateBooked, timeBooked, variantBooked, amountBooked, itemBooked;
    private Button itemDelete, getDirections;
    HashMap<String, String> map1 = new HashMap<>();
    HashMap<String, String> map2 = new HashMap<>();
    private String sellersURL = "https://shoppingpool-5c28d.firebaseio.com/sellers/sellers-list.json";
    private static final double RADIUS_OF_EARTH = 6371e3; // metres
    int mok=0;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        JSON_HTTP_CALL3();
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        sp = this.getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        bookedProductRef = FirebaseDatabase.getInstance().getReference().child("Booked_Products").child(currentUserID);
        bookedList = (RecyclerView) view.findViewById(R.id.bookingRecyclerView);
        bookedList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        bookedList.setLayoutManager(linearLayoutManager);
        progressBar = view.findViewById(R.id.bookingProgressBar);

        DisplayAllProducts();
        return view;
    }

    private  void JSON_HTTP_CALL3() {
        new JSONParse3().execute();
    }

    private class JSONParse3 extends AsyncTask<String,String,JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            try {
                ParseJSonResponse3(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(sellersURL);
            return json;
        }

    }

    private void ParseJSonResponse3(JSONObject json) throws JSONException {
        Iterator<String> keys=json.keys();

        while(keys.hasNext()){
            String key = (String)keys.next();
            String l = json.get(key).toString();
            JSONObject e=new JSONObject(l);
            JSONObject loc=new JSONObject(e.optJSONObject("location").toString());
            map1.put(key,loc.optString("lat"));
            map2.put(key,loc.optString("lng"));
        }
    }

    private void DisplayAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        bookedList.setVisibility(View.INVISIBLE);
        Query query = bookedProductRef;

        FirebaseRecyclerOptions<bookedProducts> options =
                new FirebaseRecyclerOptions.Builder<bookedProducts>()
                    .setQuery(query, new SnapshotParser<bookedProducts>() {
                        @NonNull
                        @Override
                        public bookedProducts parseSnapshot(@NonNull DataSnapshot snapshot) {
                            return new bookedProducts(snapshot.child("Product_Name").getValue().toString(),
                                    snapshot.child("CATEGORY").getValue().toString(),
                                    snapshot.child(snapshot.child("CATEGORY").getValue().toString().equalsIgnoreCase("Books")?"EDITION":snapshot.child("CATEGORY").getValue().toString().equalsIgnoreCase("Groceries")?"WEIGHT":"SIZE").getValue().toString(),
                                    snapshot.child("Amount_to_pay").getValue().toString(),
                                    snapshot.child("Date_Of_Booking").getValue().toString(),
                                    snapshot.child("Time_Of_Booking").getValue().toString(),
                                    snapshot.child("SOLD_BY").getValue().toString(),
                                    snapshot.child("QTY").getValue().toString(),
                                    snapshot.child("productId").getValue().toString(),
                                    snapshot.child("PROMOTED").getValue().toString());
                        }
                    })
                    .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bookedProducts, bookedProductsViewHolder>(options) {

            @NonNull
            @Override
            public bookedProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.booked_product, parent, false);
                return new bookedProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull bookedProductsViewHolder holder, int position, @NonNull final bookedProducts model) {

                final String bookKey = getRef(position).getKey();

                holder.setProductName(model.getProductName());
                holder.setAmount(model.getAmount());
                holder.setVariant(model.getVariant());
                holder.setShopName(model.getSoldBy());
                holder.setDateOfBooking(model.getDateOfBooking());
                holder.setTimeOfBooking(model.getTimeOfBooking());
                holder.setDistance(model.getSoldBy());
                holder.setProductImage(getContext(), model.getProductId(), model.getPromoted(), model.getCategory());
                String user = sp.getString("current_user", null);
                JSONObject s;
                try {
                    s = new JSONObject(user);
                }
                catch (Exception e)
                {
                    s=null;
                }
                JSONObject loc=s.optJSONObject("location");
                final String lat = loc.optString("latitude").trim();
                final String lng = loc.optString("longitude").trim();

                try {
                    String latShop = map1.get(model.getSoldBy()).trim();
                    String lngShop = map2.get(model.getSoldBy()).trim();

                    double deltaLat = Math.toRadians(Double.valueOf(lat) - Double.valueOf(latShop));
                    double deltaLong = Math.toRadians(Double.valueOf(lng) - Double.valueOf(lngShop));

                    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(Double.valueOf(lat)) * Math.cos(Double.valueOf(latShop)) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                    final double distance = RADIUS_OF_EARTH * c;
                    String mode = "d";
                    if ((int) distance < 1000)
                        mode = "w";

                    final String uri = "google.navigation:q=" + latShop + "," + lngShop + "&mode=" + mode;

                    holder.DirectionsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Uri gmmIntentUri = Uri.parse(uri);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });

                }
                catch (Exception e)
                {
                    JSON_HTTP_CALL3();
                }

                final DatabaseReference delRefUsers = FirebaseDatabase.getInstance().getReference().child("Booked_Products").child(currentUserID);
                final DatabaseReference delShopUse = FirebaseDatabase.getInstance().getReference().child("Booking").child(model.getSoldBy()).child(model.getCategory()).child(currentUserID);
                holder.DeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateDatabase(model.getProductId(), model.getProductName(), model.getQty(), model.getSoldBy(), model.getPromoted(), model.getCategory(), model.getVariant());
                        delRefUsers.child(bookKey).removeValue();
                        delShopUse.child(bookKey).removeValue();
                    }
                });

            }
        };
        bookedList.setAdapter(firebaseRecyclerAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        bookedList.setVisibility(View.VISIBLE);

    }

    public void updateDatabase(final String productId, String name, final String qty, String seller, String promoted, String category, final String variant)
    {
        final DatabaseReference productRef, sellerRef;
        if(promoted.equalsIgnoreCase("NO")) {
            productRef = FirebaseDatabase.getInstance().getReference().child("categories").child(category).child(productId);
            sellerRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("seller_wise").child(seller).child(category).child(name);
        }
        else {
            productRef = FirebaseDatabase.getInstance().getReference().child("promoted_products").child(category).child(productId);
            sellerRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("seller_wise").child(seller).child("promoted").child(category).child(name);
        }

        final String variantType;
        if(category.equalsIgnoreCase("Books"))
            variantType = "EDITION";
        else if(category.equalsIgnoreCase("Groceries"))
            variantType = "WEIGHT";
        else
            variantType = "SIZE";

        productRef.child(variantType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String var=dataSnapshot.getValue(String.class);
                StringTokenizer k=new StringTokenizer(var,",");
                int pos=0, tokco = k.countTokens();
                for(int i=0;i<tokco;i++)
                {
                    if(k.nextToken().trim().equalsIgnoreCase(variant.trim()))
                        break;
                    else
                        pos++;
                }

                final int posCheck = pos;



                productRef.child("QTY").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String qtyList = dataSnapshot.getValue(String.class);
                        String newQtyList="";
                        StringTokenizer m=new StringTokenizer(qtyList,",");
                        int toco = m.countTokens();

                        for(int i=0;i<toco;i++)
                        {

                            if(i==posCheck)
                            {
                                if(i!=0)
                                    newQtyList = newQtyList+","+((Integer.parseInt(m.nextToken().trim())+Integer.parseInt(qty)));
                                else
                                    newQtyList = newQtyList+((Integer.parseInt(m.nextToken().trim())+Integer.parseInt(qty)));
                            }
                            else {
                                if(i!=0)
                                    newQtyList = newQtyList +","+ m.nextToken().trim();
                                else
                                    newQtyList = newQtyList + m.nextToken().trim();
                            }

                        }


                        productRef.child("QTY").setValue(newQtyList);
                        sellerRef.child("QTY").setValue(newQtyList);
                        productRef.child("QTY").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                productRef.child(variantType).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    public class bookedProductsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button DeleteButton, DirectionsButton;

        public  bookedProductsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            DeleteButton = (Button) mView.findViewById(R.id.buttonDelete);
            DirectionsButton = (Button) mView.findViewById(R.id.buttonDirections);
        }

        public void setShopName(final String shopName)
        {
            DatabaseReference ShopRef;
            final TextView shopname = (TextView) mView.findViewById(R.id.bookedShopName);
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

        public void setProductImage(final Context ctxl, String productId, String promoted, String category)
        {
            final ImageView productimage = (ImageView) mView.findViewById(R.id.bookedProductImage);
            DatabaseReference productRef;
            if(promoted.equalsIgnoreCase("NO")) {
                productRef = FirebaseDatabase.getInstance().getReference().child("categories").child(category).child(productId).child("URL");
                }
            else {
                productRef = FirebaseDatabase.getInstance().getReference().child("promoted_products").child(category).child(productId).child("URL");
                }
            productRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Picasso.with(ctxl).load(dataSnapshot.getValue(String.class)).into(productimage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setProductName(String productName)
        {
            TextView productname = (TextView) mView.findViewById(R.id.bookingProductName);
            productname.setText(productName);
        }

        public void setVariant(String variant)
        {
            TextView variantName = (TextView) mView.findViewById(R.id.bookingProductVariant);
            variantName.setText(variant);
        }

        public void setAmount(String amount)
        {
            TextView amountToPay = (TextView) mView.findViewById(R.id.bookingAmount);
            amountToPay.setText((""+(int)Double.parseDouble(amount)));
        }

        public void setDateOfBooking(String dateOfBooking)
        {
            TextView dateofbooking = (TextView) mView.findViewById(R.id.bookingDateValue);
            dateofbooking.setText(dateOfBooking);
        }

        public void setTimeOfBooking(String timeOfBooking)
        {
            TextView timeofbooking = (TextView) mView.findViewById(R.id.bookingTimeValue);
            timeofbooking.setText(timeOfBooking);
        }

        public void setDistance(String soldBy)
        {
            TextView distanceView = (TextView) mView.findViewById(R.id.bookingDistanceValue);
            boolean lp=true;

                if (map1 != null && map2 != null) {
                    String latShop, lngShop;

                    try{
                        latShop = map1.get(soldBy).trim();
                        lngShop = map2.get(soldBy).trim();
                        String user = sp.getString("current_user", null);
                        JSONObject s;
                        try {
                            s = new JSONObject(user);
                        } catch (Exception e) {
                            s = null;
                        }
                        JSONObject loc = s.optJSONObject("location");
                        final String lat = loc.optString("latitude").trim();
                        final String lng = loc.optString("longitude").trim();

                        double deltaLat = Math.toRadians(Double.valueOf(lat) - Double.valueOf(latShop));
                        double deltaLong = Math.toRadians(Double.valueOf(lng) - Double.valueOf(lngShop));

                        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(Double.valueOf(lat)) * Math.cos(Double.valueOf(latShop)) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                        final double distance = RADIUS_OF_EARTH * c;
                        distanceView.setText(("" + ((int) distance) + " meters away"));
                    }
                    catch (Exception e) {
                        JSON_HTTP_CALL3();
                    }

                } else {
                    JSON_HTTP_CALL3();
                }

        }




    }


}
