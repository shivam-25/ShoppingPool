package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.FindActivity;
import com.example.android.shoppingpool.activities.JSONParser;
import com.example.android.shoppingpool.activities.MainActivity;
import com.example.android.shoppingpool.activities.SplashActivity;
import com.example.android.shoppingpool.adapter.RecyclerViewAdapter;
import com.example.android.shoppingpool.adapter.UploadProcess;
import com.example.android.shoppingpool.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class store extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private final Timer timer = new Timer();
    private List<Product> ListOfProduct;

    private RecyclerView recyclerViewTop;
    private final String HTTP_JSON_URL = "https://shoppingpool-5c28d.firebaseio.com/categories/Clothing.json";
    private String uid;
    private View view ;
    private int RecyclerViewItemPosition ;
    private DatabaseReference myRef;

    private final int loadIndex=-1;

    public store() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment store.
     */
    // TODO: Rename and change types and number of parameters
    public static store newInstance(String param1, String param2) {
        store fragment = new store();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_store, container, false);
        GetFirebaseAuth();
        ListOfProduct = new ArrayList<>();
        recyclerViewTop = rootView.findViewById(R.id.rcvHome1);
        recyclerViewTop.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerOfrecyclerViewTop = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTop.setLayoutManager(layoutManagerOfrecyclerViewTop);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewTop);

        myRef = FirebaseDatabase.getInstance().getReference().child("categories");

        RelativeLayout electronicAcc = rootView.findViewById(R.id.category_electronic_accessories);
        RelativeLayout automotive = rootView.findViewById(R.id.category_automotive);
        RelativeLayout electronicDevice = rootView.findViewById(R.id.category_electronic_devices);
        RelativeLayout homeAndLifestyle = rootView.findViewById(R.id.category_home_lifestyle);
        RelativeLayout homeAppliance = rootView.findViewById(R.id.category_home_appliance);
        RelativeLayout healthAndBeauty = rootView.findViewById(R.id.category_health_and_beauty);
        RelativeLayout books = rootView.findViewById(R.id.category_baby_and_toy);
        RelativeLayout groceryAndPet = rootView.findViewById(R.id.category_grocery_pets);
        RelativeLayout womenFashion = rootView.findViewById(R.id.category_women_fashion);
        RelativeLayout menFashion = rootView.findViewById(R.id.category_men_fashion);
        RelativeLayout fashionAcc = rootView.findViewById(R.id.category_fashion_accessories);
        RelativeLayout sportAndTravel = rootView.findViewById(R.id.category_sports_and_travel);

        electronicAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("electronic-accessories");
            }
        });

        automotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("automotive");
            }
        });
        electronicDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("electronic devices");
            }
        });
        homeAndLifestyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("home-and-life-style");
            }
        });
        homeAppliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("home appliance");
            }
        });
        healthAndBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("health and beauty");
            }
        });
        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Books");
            }
        });
        groceryAndPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Groceries");
            }
        });
        womenFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Clothing");
            }
        });
        menFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Clothing");
            }
        });
        fashionAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Footwear");
            }
        });
        sportAndTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentSearch("Luggage");
            }
        });

        JSON_HTTP_CALL();

        // Implementing Click Listener on RecyclerView.
        recyclerViewTop.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(view != null && gestureDetector.onTouchEvent(motionEvent)) {

                    //Getting RecyclerView Clicked Item value.
                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);


                    itemDetailActivity(ListOfProduct.get(RecyclerViewItemPosition));
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        return rootView;
    }

    private void JSON_HTTP_CALL() {
        ListOfProduct = new ArrayList<>();
        boolean doneLoad = false;

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
                System.out.println(json);
                ParseJSonResponse(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(HTTP_JSON_URL);
            return json;
        }

    }

    private void ParseJSonResponse(JSONObject array) throws JSONException {

        JSONObject jarr = array;

        RecyclerView.Adapter recyclerViewAdapter;
        if(ListOfProduct!=null){
            ListOfProduct.clear();
            recyclerViewAdapter = new RecyclerViewAdapter(ListOfProduct, store.this.getContext(),false);
            recyclerViewTop.setAdapter(recyclerViewAdapter);

        }
        //ArrayClear();
        if (ListOfProduct != null) {
            ListOfProduct.clear();
        }

        Iterator<String> keys = jarr.keys();
        while(keys.hasNext()) {

            String key = (String)keys.next();
            //DataAdapter GetDataAdapter2 = new DataAdapter();
            final Product product=new Product();
            String l=jarr.get(key).toString();

            JSONObject json=new JSONObject(l);
//
            product.setProdCode(key);
            product.setProdName(json.getString("NAME"));
            StringTokenizer price = new StringTokenizer(json.getString("PRICE"),",");
            String pricevalue = price.nextToken();
            product.setProdPrice(Double.parseDouble(pricevalue));
            DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("sellers-list").child(json.getString("SOLD_BY")).child("Shop-name");

            final String[] sellername = {""};
            shopRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String seller = dataSnapshot.getValue(String.class);
                    sellername[0] = seller;
                    product.setShopName("Sold by: "+sellername[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





            product.setProdDiscount(0);
            product.setProductURL(json.getString("URL"));
            product.setDiscountedPrice(pricevalue);


            ListOfProduct.add(product);

            // ListOfdataAdapter.add(GetDataAdapter2);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(ListOfProduct, this.getContext(),false);

        recyclerViewTop.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


    }

    private void itemDetailActivity(Product x){

//        Intent intent = new Intent(this.getContext(),ItemDetailActivity.class);
//        //Pack Data to Send
//        intent.putExtra("prodCode",x.getProdCode());
//        //intent.putExtra("onlickListener",ClassUtils.getAllInterfaces);
//
//        //open activity
//        startActivity(intent);
    }
    private void GetFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else uid = firebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }
    private void intentSearch(String category){
        Intent myIntent2 = new Intent(getContext(), FindActivity.class);
        myIntent2.putExtra("DBRef",myRef.child(category).toString());
        startActivity(myIntent2);
    }

}
