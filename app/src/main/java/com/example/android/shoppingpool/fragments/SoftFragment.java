package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.JSONParser;
import com.example.android.shoppingpool.activities.PostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.

 */
public class SoftFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FirebaseAuth mAuth;
    private DatabaseReference budgetReference, perUserReference, boughtProducts, bookedProducts, UsersRef;
    private Button budgetButton;
    private TextView budget, expenditure, savings, expExpenditure, expSavings, budgetLeft, budgetReq;
    private String current_user_id;
    private String bookedURL = "https://shoppingpool-5c28d.firebaseio.com/sales/buyer_wise";
    private String bookedURL2 = "https://shoppingpool-5c28d.firebaseio.com/Booked_Products";



    public SoftFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoftFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoftFragment newInstance(String param1, String param2) {
        SoftFragment fragment = new SoftFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_soft, container, false);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("AppUsers");
        budgetReference = FirebaseDatabase.getInstance().getReference().child("AppUsers").child("BudgetDetails");
        perUserReference = FirebaseDatabase.getInstance().getReference().child("sales").child("buyer_wise").child(current_user_id);
        boughtProducts = FirebaseDatabase.getInstance().getReference();
        budgetButton = (Button) view.findViewById(R.id.budgetButton);
        budget = (TextView) view.findViewById(R.id.budget_set);
        expenditure = (TextView) view.findViewById(R.id.expenditureValue);
        savings = (TextView) view.findViewById(R.id.savingsValue);
        expExpenditure = (TextView) view.findViewById(R.id.bookingExpValue);
        expSavings = (TextView) view.findViewById(R.id.expectedSavingsValue);
        budgetLeft = (TextView) view.findViewById(R.id.budgetLeft);
        budgetReq = (TextView) view.findViewById(R.id.budgetReq);
        bookedURL = bookedURL+"/"+current_user_id + ".json";
        bookedURL2 = bookedURL2+"/"+current_user_id+".json";
        budgetReference.child(current_user_id).child("Budget").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                budget.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        budgetReference.child(current_user_id).child("Expenditure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenditure.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Savings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savings.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expExpenditure.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Booked Savings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expSavings.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBudget();
            }
        });

        budgetReference.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double val = Double.valueOf(dataSnapshot.child("Budget").getValue(String.class))-Double.valueOf(dataSnapshot.child("Expenditure").getValue(String.class));
                budgetLeft.setText(String.valueOf(val));
                double addVal = Double.valueOf(dataSnapshot.child("Bookings").getValue(String.class))-Double.valueOf(dataSnapshot.child("Budget").getValue(String.class));
                budgetReq.setText(String.valueOf(addVal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        createFieldsInDatabase();
        return view;
    }

    private void updateBudget(){

        LayoutInflater li = requireActivity().getLayoutInflater();
        View promptsView = li.inflate(R.layout.budget_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Set Budget",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String budgetEntered = userInput.getText().toString();
                                String regex = "[0-9]+";
                                if(budgetEntered.matches(regex) && Double.parseDouble(budgetEntered)>0)
                                {
                                    budget.setText(budgetEntered);
                                    updateBudgetinDatabase(budgetEntered);

                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Wrong Value for Budget.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void updateBudgetinDatabase(String budgetEntered) {
        budgetReference.child(current_user_id).child("Budget").setValue(budgetEntered);
        updateFields();
    }

    private void updateFields()
    {
        JSON_HTTP_CALL();
        JSON_HTTP_CALL2();
    }

    private void JSON_HTTP_CALL2(){
        new JSONParse().execute();
    }

    private void JSON_HTTP_CALL() {


        new JSONParse2().execute();


    }

    private class JSONParse2 extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            try {
                ParseJSonResponse2(json);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(bookedURL2);
            return json;
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
            JSONObject json = jParser.getJSONFromUrl(bookedURL);
            return json;
        }

    }

    private void ParseJSonResponse2(JSONObject json) throws JSONException{
        System.out.println(json);
        Iterator<String> keys1 = json.keys();
        double amount_sum=0, init_cost=0.0;
        String discount="10.0";
        while(keys1!=null && keys1.hasNext())
        {
            discount="10.0";
            String key = (String)keys1.next();
            String l = json.opt(key).toString();
            String proId, proStatus, payAmt, category, qty;
            JSONObject p = new JSONObject(l);
            proId = p.optString("productId");
            proStatus = p.optString("PROMOTED");
            payAmt = p.optString("Amount_to_pay");
            category = p.optString("CATEGORY");
            qty = p.optString("QTY");
            if(payAmt!=null && payAmt!="" && !(key.equalsIgnoreCase("not_bought")))
                amount_sum = amount_sum+ Double.parseDouble(payAmt);
            final String[] temp_amt = new String[1];
            if(proStatus.equalsIgnoreCase("NO"))
            {
                boughtProducts = boughtProducts.child("categories");
                discount = "0.0";
            }
            else
            {
                boughtProducts = boughtProducts.child("promoted_products");
                boughtProducts.child(category).child(proId).child("DISCOUNT").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        temp_amt[0] = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                discount = temp_amt[0];
            }
            try{

                init_cost = init_cost+(Double.parseDouble(payAmt)+Double.parseDouble(discount)*Double.parseDouble(payAmt));
            }
            catch (Exception e)
            {

            }

        }
        budgetReference.child(current_user_id).child("Bookings").setValue(String.valueOf(Math.round(amount_sum)));
        budgetReference.child(current_user_id).child("Booked Savings").setValue(String.valueOf(Math.round(amount_sum-init_cost)));
        displyBudgetFields();

    }

    private void ParseJSonResponse1(JSONObject json) throws JSONException{
        System.out.println(json);
        Iterator<String> keys1 = json.keys();
        double amount_sum=0, init_cost=0.0;
        String discount="10.0";
        while(keys1!=null && keys1.hasNext())
        {
            discount="10.0";
            String key = (String)keys1.next();
            String l = json.opt(key).toString();
            String proId, proStatus, payAmt, category, qty;
            JSONObject p = new JSONObject(l);
            proId = p.optString("productId");
            proStatus = p.optString("PROMOTED");
            payAmt = p.optString("Amount_paid");
            category = p.optString("CATEGORY");
            qty = p.optString("QTY");
            if(payAmt!=null && payAmt!="" && !(key.equalsIgnoreCase("not_bought")))
                amount_sum = amount_sum+ Double.parseDouble(payAmt);
            final String[] temp_amt = new String[1];
            if(proStatus.equalsIgnoreCase("NO"))
            {
                boughtProducts = boughtProducts.child("categories");
                discount = "0.0";
            }
            else
            {
                boughtProducts = boughtProducts.child("promoted_products");
                boughtProducts.child(category).child(proId).child("DISCOUNT").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        temp_amt[0] = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                discount = temp_amt[0];
            }
            try{

                init_cost = init_cost+(Double.parseDouble(payAmt)+Double.parseDouble(discount)*Double.parseDouble(payAmt));
            }
            catch (Exception e)
            {

            }

        }
        budgetReference.child(current_user_id).child("Expenditure").setValue(String.valueOf(Math.round(amount_sum)));
        budgetReference.child(current_user_id).child("Savings").setValue(String.valueOf(Math.round(amount_sum-init_cost)));
        displyBudgetFields();

    }

    public void displyBudgetFields(){
        budgetReference.child(current_user_id).child("Expenditure").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenditure.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Savings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savings.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Bookings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expExpenditure.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        budgetReference.child(current_user_id).child("Booked Savings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expSavings.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createFieldsInDatabase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    budgetReference.child(current_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                System.out.println("Already Initialized");
                            else {
                                HashMap h=new HashMap();
                                h.put("Budget", "0.0");
                                h.put("Expenditure", "0.0");
                                h.put("Savings", "0.0");
                                h.put("Bookings", "0.0");
                                h.put("Booked Savings", "0.0");
                                budgetReference.child(current_user_id).updateChildren(h)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    System.out.println("Initialized First time");
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
