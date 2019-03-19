package com.example.android.shoppingpool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.fragments.AllProductsFragment;
import com.example.android.shoppingpool.fragments.RecommenderFragment;
import com.example.android.shoppingpool.models.RecommendedProducts;
import com.example.android.shoppingpool.models.detail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static com.example.android.shoppingpool.application.AppController.GOOGLE_MAPS_DISTANCE_MATRIX_API_KEY;
import static java.security.AccessController.getContext;

public class ItemDetailActivity extends AppCompatActivity {

    private String ProductKey;
    private String Category, Product, ProductURL, ProProductURL;
    private DatabaseReference ProductRef, ImageRef, ProProductRef;
    private String sellersURL = "https://shoppingpool-5c28d.firebaseio.com/sellers/sellers-list.json";
    private String baseURL = "https://shoppingpool-5c28d.firebaseio.com/categories";
    private String baseURL2 = "https://shoppingpool-5c28d.firebaseio.com/promoted_products";
    private RecyclerView recyclerView;
    HashMap<String, String> map1 = new HashMap<>();
    HashMap<String, String> map2 = new HashMap<>();
    private static final double RADIUS_OF_EARTH = 6371e3; // metres

    private List<detail> ListOfProduct;
    private detailAdapter mAdapter;
    private ProgressBar progressBar;
    private JSONObject json1, json2;
    int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        progressBar = (ProgressBar) findViewById(R.id.detailProgressBar);

        recyclerView = (RecyclerView) findViewById(R.id.detailActivityRecyclerView);

        ProductKey = getIntent().getExtras().get("Product_Key").toString();
        ListOfProduct = new ArrayList<>();
        StringTokenizer k =new StringTokenizer(ProductKey,"+");
        Category = k.nextToken().trim();

        Product = k.nextToken().trim();
        ProductRef = FirebaseDatabase.getInstance().getReference().child("categories").child(Category).child(Product);
        ProProductRef = FirebaseDatabase.getInstance().getReference().child("promoted_products").child(Category).child(Product).child("URL");
        ImageRef = ProductRef.child("URL");
        StringTokenizer p=new StringTokenizer(Product,"_");
        Product = p.nextToken().trim();

        ProductURL = baseURL + "/" + Category + ".json";
        ProProductURL = baseURL2+"/"+Category+".json";

        final ImageView productImage = (ImageView) findViewById(R.id.coverphoto);
        ImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).into(productImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ProProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class)!=null)
                    Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).into(productImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView productNameView = (TextView) findViewById(R.id.detailActivityProductName);
        productNameView.setText(("Nearby Shops selling "+Product));

        JSON_HTTP_CALL3();
        JSON_HTTP_CALL();
        JSON_HTTP_CALL2();


    }

    private  void JSON_HTTP_CALL3() {
        new JSONParse3().execute();
    }

    private void JSON_HTTP_CALL() {


        new JSONParse().execute();


    }

    private void JSON_HTTP_CALL2() {
        new JSONParse2().execute();
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

    private void ParseJSonResponse3(JSONObject json) throws JSONException{
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

    private class JSONParse extends AsyncTask<String,String,JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            try {
                ParseJSonResponse1(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(ProductURL);
            return json;
        }

    }

    private class JSONParse2 extends AsyncTask<String,String,JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            try {
                progressBar.setVisibility(View.INVISIBLE);
                ParseJSonResponse2(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            progressBar.setVisibility(View.VISIBLE);
            JSONObject json = jParser.getJSONFromUrl(ProProductURL);
            return json;
        }

    }

    private void ParseJSonResponse1(JSONObject json) throws JSONException{
        json1 = json;
    }

    private void ParseJSonResponse2(JSONObject json) throws JSONException{
        json2 = json;

        ParseJSonResponse(json1, json2);
    }


    private void ParseJSonResponse(JSONObject json1, JSONObject json2) throws JSONException{

        if(ListOfProduct!=null){
            ListOfProduct.clear();
            mAdapter = new detailAdapter(ListOfProduct);
            recyclerView.setAdapter(mAdapter);

        }
        //ArrayClear();
        if (ListOfProduct != null) {
            ListOfProduct.clear();
        }

        if(json1.equals(json2)){
            json2=null;
        }


        progressBar.setVisibility(View.VISIBLE);
        Iterator<String> keys1 = json1.keys();
        Iterator<String> keys2;
        if(json2!=null) {
            keys2 = json2.keys();
        }
        else
            keys2=null;

        int listSize=0;
        while(keys1!=null && keys1.hasNext()){
            String key = (String)keys1.next();

            StringTokenizer c=new StringTokenizer(key,"_");
            key=c.nextToken().trim();
            String shopId = c.nextToken().trim();
            String color, discount, offer, gender, name, price, qty, size, soldBy, url, edition, weight, water;
            if(key.equals(Product)) {
                listSize++;
                String l = json1.opt((key+"_"+shopId)).toString();
                JSONObject p = new JSONObject(l);
                color = p.optString("COLOR");
                discount = p.optString("DISCOUNT");
                offer = p.optString("OFFER");
                gender = p.optString("GENDER");
                name = p.optString("NAME");
                price = p.optString("PRICE");
                qty = p.optString("QTY");
                size = p.optString("SIZE");
                soldBy = p.optString("SOLD_BY");
                url = p.optString("URL");
                edition = p.optString("EDITION");
                weight = p.optString("WEIGHT");
                water = p.optString("WATER");

                detail eachProduct = new detail(color, discount, offer, gender, name, price, qty, size, soldBy, url, edition, weight, water);
                ListOfProduct.add(eachProduct);
            }

        }

        while(keys2!=null && keys2.hasNext()){
            String key = (String)keys2.next();

            StringTokenizer c=new StringTokenizer(key,"_");
            key=c.nextToken().trim();
            String shopId = c.nextToken().trim();

            String color, discount, offer, gender, name, price, qty, size, soldBy, url, edition, weight, water;
            if(key.equals(Product)) {
                listSize++;
                String l = json2.opt((key+"_"+shopId)).toString();
                JSONObject p = new JSONObject(l);
                color = p.optString("COLOR");
                discount = p.optString("DISCOUNT");
                offer = p.optString("OFFER");
                gender = p.optString("GENDER");
                name = p.optString("NAME");
                price = p.optString("PRICE");
                qty = p.optString("QTY");
                size = p.optString("SIZE");
                soldBy = p.optString("SOLD_BY");
                url = p.optString("URL");
                edition = p.optString("EDITION");
                weight = p.optString("WEIGHT");
                water = p.optString("WATER");

                detail eachProduct = new detail(color, discount, offer, gender, name, price, qty, size, soldBy, url, edition, weight, water);
                ListOfProduct.add(eachProduct);
            }

        }


        progressBar.setVisibility(View.INVISIBLE);
        mAdapter = new detailAdapter(ListOfProduct);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getParent());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    public class detailAdapter extends RecyclerView.Adapter<detailAdapter.ProductsViewHolder>{

        private List<detail> productsList;

        public detailAdapter(List<detail> products){
            productsList = products;
        }

        @NonNull
        @Override
        public detailAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.each_shop_detail, parent ,false);
            return new ProductsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull detailAdapter.ProductsViewHolder holder, int position) {

            detail model = productsList.get(position);
            holder.setShopName(model.getSoldBy());
            holder.setColor(model.getColor());
            holder.setGender(model.getGender());
            holder.setWater(model.getWater());
            holder.setEdition(model.getEdition());
            holder.setDiscount(model.getDiscount());
            holder.setOffer(model.getOffer());
            holder.setDistance(model.getSoldBy());
            holder.setVariantDetails(Category, model.getSize(), model.getEdition(), model.getWeight(), model.getQty(), model.getPrice());

            final String cat = Category;
            final String prodName = model.getName();
            final String sellerId = model.getSoldBy();
            final String qty = model.getQty();
            final String variant;
            final String price=model.getPrice();
            if(Category.equalsIgnoreCase("Books"))
                variant = model.getEdition();
            else if(Category.equalsIgnoreCase("Clothing") || Category.equalsIgnoreCase("Footwear") || Category.equalsIgnoreCase("Luggage"))
                variant = model.getSize();
            else if(Category.equalsIgnoreCase("Groceries"))
                variant = model.getWeight();
            else
                variant = model.getSize();
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent clickProductIntent = new Intent(getApplicationContext(), BookingActivity.class);
                    clickProductIntent.putExtra("Product_Key_Booked", (sellerId+"#"+cat+"#"+prodName+"#"+variant+"#"+qty+"#"+price));
                    startActivity(clickProductIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return productsList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position){
            return position;
        }


        public class ProductsViewHolder extends RecyclerView.ViewHolder {

            View mView;

            public ProductsViewHolder(View itemView){
                super(itemView);
                mView = itemView;
            }



            public void setShopName(final String shopName) {
                DatabaseReference ShopRef;
                final TextView shopname = (TextView) mView.findViewById(R.id.detailProductShopName);
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

            public void setColor(String color){
                TextView heading = (TextView) mView.findViewById(R.id.detailColor);
                TextView detail = (TextView) mView.findViewById(R.id.detailColorValue);
                if(color.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(color);
                }
            }

            public void setGender(String gender){
                TextView heading = (TextView) mView.findViewById(R.id.detailGender);
                TextView detail = (TextView) mView.findViewById(R.id.detailGenderValue);
                if(gender.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(gender);
                }
            }

            public void setEdition(String edition){
                TextView heading = (TextView) mView.findViewById(R.id.detailEdition);
                TextView detail = (TextView) mView.findViewById(R.id.detailEditionValue);
                if(edition.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(edition);
                }
            }

            public void setWater(String water){
                TextView heading = (TextView) mView.findViewById(R.id.detailWater);
                TextView detail = (TextView) mView.findViewById(R.id.detailWaterValue);
                if(water.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(water);
                }
            }

            public void setDiscount(String discount){
                TextView heading = (TextView) mView.findViewById(R.id.detailDiscountHeading);
                TextView detail = (TextView) mView.findViewById(R.id.detailDiscount);
                if(discount.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(discount);
                }
            }

            public void setOffer(String offer){
                TextView heading = (TextView) mView.findViewById(R.id.detailOfferHeading);
                TextView detail = (TextView) mView.findViewById(R.id.detailOffer);
                if(offer.equalsIgnoreCase("")){
                    heading.setVisibility(View.GONE);
                    detail.setVisibility(View.GONE);
                }
                else {
                    detail.setText(offer);
                }
            }



            public void setDistance(String soldBy) {
                TextView distanceView = (TextView) mView.findViewById(R.id.detailDistance);
                String latShop = map1.get(soldBy).trim();
                String lngShop = map2.get(soldBy).trim();

                SharedPreferences sp = getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
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

//                String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json"
//                        + "?units=metric&origins=" + lat + "," + lng
//                        + "&destinations=" + latShop + "," + lngShop
//                        + "&language=en"
//                        + "&key=" + GOOGLE_MAPS_DISTANCE_MATRIX_API_KEY;
//
//                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                        urlString, null,
//                        new Response.Listener<JSONObject>() {
//
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                int flag = 0;
//                                try {
//                                    String status = response.getString("status");
//                                    if (status.equals("OK")) {
//                                        final double distance;
//                                        JSONObject element = response.getJSONArray("rows").getJSONObject(0)
//                                                .getJSONArray("elements").getJSONObject(0);
//                                        if (element.getString("status").equals("OK")) {
//                                            // retrieve the distance
//                                            JSONObject distanceObj = element.getJSONObject("distance");
//                                            distance = distanceObj.getDouble("value");
//                                            distanceView.setText((""+distance));
//
//                                            // find helper's name and send message if he/she is nearby
//
//                                        } else {
//                                            flag = 1;
//                                        }
//
//                                    } else {
//                                        flag = 1;
//                                    }
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//
//                                    flag =1;
//                                }
//
//                                if (flag == 1) {
//                                    double deltaLat = Math.toRadians(Double.valueOf(lat) - Double.valueOf(latShop));
//                                    double deltaLong = Math.toRadians(Double.valueOf(lng) - Double.valueOf(lngShop));
//
//                                    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
//                                            Math.cos(Double.valueOf(lat)) * Math.cos(Double.valueOf(latShop)) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
//                                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//                                    final double distance = RADIUS_OF_EARTH * c;
//                                    distanceView.setText((""+distance));
//                                }
//
//                            }
//                        }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        double deltaLat = Math.toRadians(Double.valueOf(lat) - Double.valueOf(latShop));
//                        double deltaLong = Math.toRadians(Double.valueOf(lng) - Double.valueOf(lngShop));
//
//                        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
//                                Math.cos(Double.valueOf(lat)) * Math.cos(Double.valueOf(latShop)) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
//                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//                        final double distance = RADIUS_OF_EARTH * c;
//                        distanceView.setText((""+distance));
//                    }
//                });
                double deltaLat = Math.toRadians(Double.valueOf(lat) - Double.valueOf(latShop));
                double deltaLong = Math.toRadians(Double.valueOf(lng) - Double.valueOf(lngShop));

                double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(Double.valueOf(lat)) * Math.cos(Double.valueOf(latShop)) * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                final double distance = RADIUS_OF_EARTH * c;
                distanceView.setText((""+((int)distance)+"meters away"));
            }

            public void setVariantDetails(String category, String size, String edition, String weight, String qty, String price)
            {
                RelativeLayout varDet1 = (RelativeLayout) mView.findViewById(R.id.variantDetails1);
                RelativeLayout varDet2 = (RelativeLayout) mView.findViewById(R.id.variantDetails2);
                RelativeLayout varDet3 = (RelativeLayout) mView.findViewById(R.id.variantDetails3);
                RelativeLayout varDet4 = (RelativeLayout) mView.findViewById(R.id.variantDetails4);
                RelativeLayout varDet5 = (RelativeLayout) mView.findViewById(R.id.variantDetails5);
                RelativeLayout varDet6 = (RelativeLayout) mView.findViewById(R.id.variantDetails6);
                RelativeLayout varDet7 = (RelativeLayout) mView.findViewById(R.id.variantDetails7);

                TextView detVar1 = (TextView) mView.findViewById(R.id.detailVar1);
                TextView detVar2 = (TextView) mView.findViewById(R.id.detailVar2);
                TextView detVar3 = (TextView) mView.findViewById(R.id.detailVar3);
                TextView detVar4 = (TextView) mView.findViewById(R.id.detailVar4);
                TextView detVar5 = (TextView) mView.findViewById(R.id.detailVar5);
                TextView detVar6 = (TextView) mView.findViewById(R.id.detailVar6);
                TextView detVar7 = (TextView) mView.findViewById(R.id.detailVar7);

                TextView detStock1 = (TextView) mView.findViewById(R.id.detailStock1);
                TextView detStock2 = (TextView) mView.findViewById(R.id.detailStock2);
                TextView detStock3 = (TextView) mView.findViewById(R.id.detailStock3);
                TextView detStock4 = (TextView) mView.findViewById(R.id.detailStock4);
                TextView detStock5 = (TextView) mView.findViewById(R.id.detailStock5);
                TextView detStock6 = (TextView) mView.findViewById(R.id.detailStock6);
                TextView detStock7 = (TextView) mView.findViewById(R.id.detailStock7);

                TextView detCost1 = (TextView) mView.findViewById(R.id.detailCost1);
                TextView detCost2 = (TextView) mView.findViewById(R.id.detailCost2);
                TextView detCost3 = (TextView) mView.findViewById(R.id.detailCost3);
                TextView detCost4 = (TextView) mView.findViewById(R.id.detailCost4);
                TextView detCost5 = (TextView) mView.findViewById(R.id.detailCost5);
                TextView detCost6 = (TextView) mView.findViewById(R.id.detailCost6);
                TextView detCost7 = (TextView) mView.findViewById(R.id.detailCost7);


                String variant = size;
                if(category.equalsIgnoreCase("Books"))
                    variant = edition;
                if(category.equalsIgnoreCase("Groceries"))
                    variant = weight;

                StringTokenizer k=new StringTokenizer(variant,",");
                int count = k.countTokens();
                StringTokenizer k2=new StringTokenizer(qty,",");
                StringTokenizer k3=new StringTokenizer(price,",");
                int i=0;
                for(i=count; i<7;i++){
                    if(i==0)
                        varDet1.setVisibility(View.GONE);
                    if(i==1)
                        varDet2.setVisibility(View.GONE);
                    if(i==2)
                        varDet3.setVisibility(View.GONE);
                    if(i==3)
                        varDet4.setVisibility(View.GONE);
                    if(i==4)
                        varDet5.setVisibility(View.GONE);
                    if(i==5)
                        varDet6.setVisibility(View.GONE);
                    if(i==6)
                        varDet7.setVisibility(View.GONE);
                }

                for(i=0;i<count;i++)
                {
                    if(i==0)
                    {
                        detVar1.setText(k.nextToken().trim());
                        detStock1.setText(k2.nextToken().trim());
                        detCost1.setText(k3.nextToken().trim());
                    }
                    if(i==1)
                    {
                        detVar2.setText(k.nextToken().trim());
                        detStock2.setText(k2.nextToken().trim());
                        detCost2.setText(k3.nextToken().trim());
                    }
                    if(i==2)
                    {
                        detVar3.setText(k.nextToken().trim());
                        detStock3.setText(k2.nextToken().trim());
                        detCost3.setText(k3.nextToken().trim());
                    }
                    if(i==3)
                    {
                        detVar4.setText(k.nextToken().trim());
                        detStock4.setText(k2.nextToken().trim());
                        detCost4.setText(k3.nextToken().trim());
                    }
                    if(i==4)
                    {
                        detVar5.setText(k.nextToken().trim());
                        detStock5.setText(k2.nextToken().trim());
                        detCost5.setText(k3.nextToken().trim());
                    }
                    if(i==5)
                    {
                        detVar6.setText(k.nextToken().trim());
                        detStock6.setText(k2.nextToken().trim());
                        detCost6.setText(k3.nextToken().trim());
                    }
                    if(i==6)
                    {
                        detVar7.setText(k.nextToken().trim());
                        detStock7.setText(k2.nextToken().trim());
                        detCost7.setText(k3.nextToken().trim());
                    }
                }
            }


        }
    }
}
