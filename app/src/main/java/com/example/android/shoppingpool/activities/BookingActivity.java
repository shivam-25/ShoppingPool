package com.example.android.shoppingpool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingpool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

public class BookingActivity extends AppCompatActivity {

    private String ProductKey;
    private ImageView detailImage;
    private TextView detailCost, detailDiscount, originalCost, stockDetail, bookingValue;
    private Button varButton1, varButton2, varButton3, varButton4, varButton5, varButton6, varButton7, varButton8;
    private Button incButton, decButton, bookButton, bookAndPayButton;
    private int quantity;
    private String variant;
    private String sentVariant, sentQuantity, sentPrice;
    private double discountIfAny=0.0, finalPriceCalc, newCost;
    private String productId;
    private String sellerId, category, productName, variantDetail, qty, cost;
    private DatabaseReference productRef1, productRef2;
    private DatabaseReference updateCatRef, updateSellerRef, userBookRef, usersRef, bookSellerRef;
    private String variantType;
    private int finalCount;
    private String newQuantityList;
    private String[] sentVariantArray;
    private int[] sentQuantityArray;
    private String[] sentPriceArray;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String updateQty;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    private String promotedIndication;
    private String fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        userBookRef = FirebaseDatabase.getInstance().getReference().child("Booked_Products").child(currentUserId);
        bookSellerRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        ProductKey = getIntent().getExtras().get("Product_Key_Booked").toString();
        quantity=1;
        StringTokenizer k=new StringTokenizer(ProductKey, "#");
        sellerId = k.nextToken().trim();
        category = k.nextToken().trim();
        productName = k.nextToken().trim();
        sentVariant = k.nextToken().trim();
        sentQuantity = k.nextToken().trim();
        sentPrice = k.nextToken().trim();
        newQuantityList="";
        RelativeLayout loanFullerton = findViewById(R.id.loanButton);
        SharedPreferences sp = getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", null);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String prefName = sp.getString("prefName", null);
        fullname = prefName;

        StringTokenizer vari = new StringTokenizer(sentVariant,",");
        sentVariantArray = new String[vari.countTokens()];
        StringTokenizer quati = new StringTokenizer(sentQuantity,",");
        sentQuantityArray = new int[quati.countTokens()];
        StringTokenizer pri=new StringTokenizer(sentPrice,",");
        sentPriceArray = new String[pri.countTokens()];
        finalCount = quati.countTokens();
        for(int i=0;i<finalCount;i++){
            sentVariantArray[i] = vari.nextToken().trim();
            sentQuantityArray[i] = Integer.parseInt(quati.nextToken().trim());
            sentPriceArray[i] =  pri.nextToken().trim();
        }
        newCost = Double.parseDouble(sentPriceArray[0]);

        productId = productName + "_" + sellerId;
        detailImage = (ImageView) findViewById(R.id.itemDetailImage);
        varButton1 = (Button) findViewById(R.id.bookVariant1);
        varButton2 = (Button) findViewById(R.id.bookVariant2);
        varButton3 = (Button) findViewById(R.id.bookVariant3);
        varButton4 = (Button) findViewById(R.id.bookVariant4);
        varButton5 = (Button) findViewById(R.id.bookVariant5);
        varButton6 = (Button) findViewById(R.id.bookVariant6);
        varButton7 = (Button) findViewById(R.id.bookVariant7);
        varButton8 = (Button) findViewById(R.id.bookVariant8);
        bookButton = (Button) findViewById(R.id.confirmAddToCart);
        bookAndPayButton = (Button) findViewById(R.id.checkoutNowButton);

        incButton = (Button) findViewById(R.id.increaseQtyBut);
        decButton = (Button) findViewById(R.id.buttondecreaseQtyBut);
        bookingValue = (TextView) findViewById(R.id.selectedQuantity);
        stockDetail = (TextView) findViewById(R.id.itemDetailQuantityText);
        detailCost = (TextView) findViewById(R.id.itemDetailDiscountedPriceText);
        detailDiscount = (TextView) findViewById(R.id.itemDetailDiscountPriceText);
        originalCost = (TextView) findViewById(R.id.itemDetailPriceText);

        productRef1 = FirebaseDatabase.getInstance().getReference().child("categories").child(category).child(productId);
        productRef2 = FirebaseDatabase.getInstance().getReference().child("promoted_products").child(category).child(productId);
        productRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    updateCatRef = productRef1;
                    promotedIndication = "NO";
                    updateSellerRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("seller_wise").child(sellerId).child(category).child(productName);
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child("URL").getValue(String.class)).into(detailImage);
                    cost = dataSnapshot.child("PRICE").getValue(String.class);
                    StringTokenizer p = new StringTokenizer(cost, ",");
                    double iniCost = Double.parseDouble(p.nextToken().trim());
                    if(dataSnapshot.child("DISCOUNT").exists()) {


                        String dis = dataSnapshot.child("DISCOUNT").getValue(String.class);
                        if(dis.charAt(dis.length()-1)=='%')
                            dis = dis.substring(0,dis.length()-1);
                        else
                            dis = dis;
                        Double discount = Double.parseDouble(dis);
                        discountIfAny = discount;
                        double disCost = iniCost - iniCost * discount / 100;
                        newCost = disCost;
                        detailCost.setText(("" + disCost));
                        originalCost.setText(("" + iniCost));
                        detailDiscount.setText(dataSnapshot.child("DISCOUNT").getValue(String.class));
                    }
                    else{
                        newCost = iniCost;
                        detailCost.setText((""+iniCost));
                        originalCost.setVisibility(View.INVISIBLE);
                        detailDiscount.setVisibility(View.INVISIBLE);
                    }
                        qty = dataSnapshot.child("QTY").getValue(String.class);
                        StringTokenizer l=new StringTokenizer(qty,",");
                        int count=0;
                        while(l.hasMoreTokens()){
                            count=count+Integer.parseInt(l.nextToken().trim());
                        }
                        stockDetail.setText((""+count));

                        variantType = "SIZE";
                        if(category.equalsIgnoreCase("Books"))
                            variantType = "EDITION";
                        if(category.equalsIgnoreCase("Groceries"))
                            variantType = "WEIGHT";
                        if(dataSnapshot.child(variantType).exists()){
                            variantDetail = dataSnapshot.child(variantType).getValue(String.class);
                            StringTokenizer b=new StringTokenizer(variantDetail,",");
                            int countVar = b.countTokens();
                            for(int i=0;i<countVar;i++)
                            {
                                if(i==0)
                                {
                                    varButton1.setText(b.nextToken().trim());
                                    varButton1.setBackgroundResource(R.drawable.my_button_bg2);
                                    varButton1.setTextColor(Color.rgb(255,255,255));
                                }
                                if(i==1)
                                    varButton2.setText(b.nextToken().trim());
                                if(i==2)
                                    varButton3.setText(b.nextToken().trim());
                                if(i==3)
                                    varButton4.setText(b.nextToken().trim());
                                if(i==4)
                                    varButton5.setText(b.nextToken().trim());
                                if(i==5)
                                    varButton6.setText(b.nextToken().trim());
                                if(i==6)
                                    varButton7.setText(b.nextToken().trim());
                                if(i==7)
                                    varButton8.setText(b.nextToken().trim());

                            }
                            for(int i=countVar;i<8;i++)
                            {
                                if(i==0)
                                    varButton1.setVisibility(View.INVISIBLE);
                                if(i==1)
                                    varButton2.setVisibility(View.INVISIBLE);
                                if(i==2)
                                    varButton3.setVisibility(View.INVISIBLE);
                                if(i==3)
                                    varButton4.setVisibility(View.INVISIBLE);
                                if(i==4)
                                    varButton5.setVisibility(View.INVISIBLE);
                                if(i==5)
                                    varButton6.setVisibility(View.INVISIBLE);
                                if(i==6)
                                    varButton7.setVisibility(View.INVISIBLE);
                                if(i==7)
                                    varButton8.setVisibility(View.INVISIBLE);
                            }
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        productRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    updateCatRef = productRef2;
                    updateSellerRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("seller_wise").child(sellerId).child("promoted").child(category).child(productName);
                    promotedIndication = "YES";
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child("URL").getValue(String.class)).into(detailImage);
                    cost = dataSnapshot.child("PRICE").getValue(String.class);
                    StringTokenizer p = new StringTokenizer(cost, ",");
                    double iniCost = Double.parseDouble(p.nextToken().trim());
                    if(dataSnapshot.child("DISCOUNT").exists()) {

                        String dis = dataSnapshot.child("DISCOUNT").getValue(String.class);
                        if(dis.charAt(dis.length()-1)=='%')
                            dis = dis.substring(0,dis.length()-1);
                        else
                            dis = dis;
                        Double discount = Double.parseDouble(dis);
                        discountIfAny = discount;
                        double disCost = iniCost - iniCost * discount / 100;
                        newCost = disCost;
                        detailCost.setText(("" + disCost));
                        originalCost.setText(("" + iniCost));
                        detailDiscount.setText(dataSnapshot.child("DISCOUNT").getValue(String.class));
                    }
                    else{
                        newCost = iniCost;
                        detailCost.setText((""+iniCost));
                        originalCost.setVisibility(View.INVISIBLE);
                        detailDiscount.setVisibility(View.INVISIBLE);
                    }
                        qty = dataSnapshot.child("QTY").getValue(String.class);
                        StringTokenizer l=new StringTokenizer(qty,",");
                        int count=0;
                        while(l.hasMoreTokens()){
                            count=count+Integer.parseInt(l.nextToken().trim());
                        }
                        stockDetail.setText((""+count));

                        variantType = "SIZE";
                        if(category.equalsIgnoreCase("Books"))
                            variantType = "EDITION";
                        if(category.equalsIgnoreCase("Groceries"))
                            variantType = "WEIGHT";
                        if(dataSnapshot.child(variantType).exists()){
                            variantDetail = dataSnapshot.child(variantType).getValue(String.class);
                            StringTokenizer b=new StringTokenizer(variantDetail,",");
                            int countVar = b.countTokens();
                            for(int i=0;i<countVar;i++)
                            {
                                if(i==0)
                                {
                                    varButton1.setText(b.nextToken().trim());
                                    varButton1.setBackgroundResource(R.drawable.my_button_bg2);
                                    varButton1.setTextColor(Color.rgb(255,255,255));
                                }
                                if(i==1)
                                    varButton2.setText(b.nextToken().trim());
                                if(i==2)
                                    varButton3.setText(b.nextToken().trim());
                                if(i==3)
                                    varButton4.setText(b.nextToken().trim());
                                if(i==4)
                                    varButton5.setText(b.nextToken().trim());
                                if(i==5)
                                    varButton6.setText(b.nextToken().trim());
                                if(i==6)
                                    varButton7.setText(b.nextToken().trim());
                                if(i==7)
                                    varButton8.setText(b.nextToken().trim());

                            }
                            for(int i=countVar;i<8;i++)
                            {
                                if(i==0)
                                    varButton1.setVisibility(View.INVISIBLE);
                                if(i==1)
                                    varButton2.setVisibility(View.INVISIBLE);
                                if(i==2)
                                    varButton3.setVisibility(View.INVISIBLE);
                                if(i==3)
                                    varButton4.setVisibility(View.INVISIBLE);
                                if(i==4)
                                    varButton5.setVisibility(View.INVISIBLE);
                                if(i==5)
                                    varButton6.setVisibility(View.INVISIBLE);
                                if(i==6)
                                    varButton7.setVisibility(View.INVISIBLE);
                                if(i==7)
                                    varButton8.setVisibility(View.INVISIBLE);
                            }
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StringTokenizer d=new StringTokenizer(sentVariant,",");
        varButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[0]);
                    detailCost.setText((""+sentPriceArray[0]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[0]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton1.setBackgroundResource(R.drawable.my_button_bg2);
                varButton1.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[1]);
                    detailCost.setText((""+sentPriceArray[1]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[1]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton2.setBackgroundResource(R.drawable.my_button_bg2);
                varButton2.setTextColor(Color.rgb(255,255,255));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[2]);
                    detailCost.setText((""+sentPriceArray[2]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[2]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton3.setBackgroundResource(R.drawable.my_button_bg2);
                varButton3.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[3]);
                    detailCost.setText((""+sentPriceArray[3]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[3]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton4.setBackgroundResource(R.drawable.my_button_bg2);
                varButton4.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });


        varButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[4]);
                    detailCost.setText((""+sentPriceArray[4]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[4]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton5.setBackgroundResource(R.drawable.my_button_bg2);
                varButton5.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[5]);
                    detailCost.setText((""+sentPriceArray[5]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[5]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton6.setBackgroundResource(R.drawable.my_button_bg2);
                varButton6.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[6]);
                    detailCost.setText((""+sentPriceArray[6]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[6]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton7.setBackgroundResource(R.drawable.my_button_bg2);
                varButton7.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
                varButton8.setBackgroundResource(R.drawable.my_button_bg);
                varButton8.setTextColor(Color.rgb(255,140,0));
            }
        });

        varButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b=(Button)v;
                variant = b.getText().toString();
                quantity = 1;
                bookingValue.setText(("1"));
                if(discountIfAny==0.0)
                {
                    newCost = Double.parseDouble(sentPriceArray[7]);
                    detailCost.setText((""+sentPriceArray[7]));
                }
                else {
                    double k=Double.parseDouble(sentPriceArray[7]);
                    originalCost.setText((""+k));
                    newCost = k-k*discountIfAny/100;
                    detailCost.setText(""+newCost);
                }
                varButton8.setBackgroundResource(R.drawable.my_button_bg2);
                varButton8.setTextColor(Color.rgb(255,255,255));
                varButton2.setBackgroundResource(R.drawable.my_button_bg);
                varButton2.setTextColor(Color.rgb(255,140,0));
                varButton3.setBackgroundResource(R.drawable.my_button_bg);
                varButton3.setTextColor(Color.rgb(255,140,0));
                varButton4.setBackgroundResource(R.drawable.my_button_bg);
                varButton4.setTextColor(Color.rgb(255,140,0));
                varButton5.setBackgroundResource(R.drawable.my_button_bg);
                varButton5.setTextColor(Color.rgb(255,140,0));
                varButton6.setBackgroundResource(R.drawable.my_button_bg);
                varButton6.setTextColor(Color.rgb(255,140,0));
                varButton7.setBackgroundResource(R.drawable.my_button_bg);
                varButton7.setTextColor(Color.rgb(255,140,0));
                varButton1.setBackgroundResource(R.drawable.my_button_bg);
                varButton1.setTextColor(Color.rgb(255,140,0));
            }
        });


        final HashMap variantsMap = new HashMap();
        StringTokenizer t=new StringTokenizer(sentVariant,",");
        StringTokenizer h=new StringTokenizer(sentQuantity,",");
        int y=0;
        while (t.hasMoreTokens()){
            if(y==0) {
                variant = t.nextToken().trim();
                variantsMap.put(variant,h.nextToken().trim());
                y++;
            }
            else
                variantsMap.put(t.nextToken().trim(),h.nextToken().trim());
        }

        incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity<Integer.parseInt(variantsMap.get(variant).toString())) {
                    quantity = quantity + 1;
                    bookingValue.setText(("" + quantity));
                }
                else
                    Toast.makeText(BookingActivity.this, "Order exceeding stock limit..", Toast.LENGTH_LONG).show();

            }
        });

        decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1)
                {
                    quantity=quantity-1;
                    bookingValue.setText((""+quantity));
                }
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateQty = ""+quantity;
                int k=0;
                for(int i=0;i<finalCount;i++)
                {
                    if(variant.equals(sentVariantArray[i]))
                    {
                        sentQuantityArray[i]=sentQuantityArray[i]-quantity;

                    }
                }
                finalPriceCalc = newCost*quantity;
                newQuantityList = ""+sentQuantityArray[0];
                for(int i=1;i<finalCount;i++)
                {
                    newQuantityList = newQuantityList+","+sentQuantityArray[i];
                }
                final String finalPriceUpdate = ""+finalPriceCalc;

                updateCatRef.child("QTY").setValue(newQuantityList);
                updateSellerRef.child("QTY").setValue(newQuantityList);

                Calendar calFordDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                saveCurrentDate = currentDate.format(calFordDate.getTime());

                Calendar calFordTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                saveCurrentTime = currentTime.format(calFordDate.getTime());

                postRandomName = saveCurrentDate + saveCurrentTime;
                usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            HashMap bookMap = new HashMap();
                            bookMap.put("productId",productId);
                            bookMap.put("Product_Name",productName);
                            bookMap.put("CATEGORY", category);
                            bookMap.put("SOLD_BY", sellerId);
                            bookMap.put(variantType, variant);
                            bookMap.put("QTY", updateQty);
                            bookMap.put("Amount_to_pay",finalPriceUpdate);
                            bookMap.put("PROMOTED", promotedIndication);
                            bookMap.put("Date_Of_Booking", saveCurrentDate);
                            bookMap.put("Time_Of_Booking", saveCurrentTime);
                            userBookRef.child((productName+"___"+postRandomName)).updateChildren(bookMap)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){

                                                Toast.makeText(BookingActivity.this, "Order Booked Successfully", Toast.LENGTH_LONG).show();
                                                HashMap bookSellDetail = new HashMap();
                                                bookSellDetail.put(variantType,variant);
                                                bookSellDetail.put("QTY", updateQty);
                                                bookSellDetail.put("NAME", productName);
                                                bookSellDetail.put("Buyer", currentUserId);
                                                bookSellDetail.put("Amount_to_retrieve",finalPriceUpdate);
                                                bookSellDetail.put("PROMOTED",promotedIndication);
                                                bookSellDetail.put("Date_Of_Booking", saveCurrentDate);
                                                bookSellDetail.put("Time_Of_Booking", saveCurrentTime);
                                                bookSellDetail.put("Buyer_Name", fullname);
                                                bookSellerRef.child(sellerId).child(category).child(currentUserId).child((productName+"___"+postRandomName)).updateChildren(bookSellDetail)
                                                        .addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    SendUserToMainActivity();
                                                                }
                                                                else {
                                                                    Toast.makeText(BookingActivity.this, "Unable to Book, Please try again later...", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                Toast.makeText(BookingActivity.this, "Unable to Book, Please try again later...", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }
        });

        loanFullerton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookingActivity.this, LoanActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(BookingActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


}
