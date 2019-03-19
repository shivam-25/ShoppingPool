package com.example.android.shoppingpool.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.activities.JSONParser;
import com.example.android.shoppingpool.models.Jobs;
import com.example.android.shoppingpool.models.RecommendedProducts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.


 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private DatabaseReference jobsRef;
    private FirebaseAuth mAuth;
    String currentUserID;
    SharedPreferences sp ;
    private RecyclerView jobsList;
    private ProgressBar progressBar;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    HashMap<String, String> map1 = new HashMap<>();
    HashMap<String, String> map2 = new HashMap<>();
    private String sellersURL = "https://shoppingpool-5c28d.firebaseio.com/sellers/sellers-list.json";
    private static final double RADIUS_OF_EARTH = 6371e3;
    private String HTTP_JSON_URL = "https://shoppingpool-5c28d.firebaseio.com/classifieds/job_vaccancies.json";
    private List<Jobs> ListOfJobs;
    private RecyclerView recyclerView;
    private JobAdapter mAdapter;
    private String nameUser;



    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        JSON_HTTP_CALL3();
        sp = this.getContext().getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", null);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefName = sp.getString("prefName", null);
        nameUser = prefName;
        ListOfJobs = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        jobsRef = FirebaseDatabase.getInstance().getReference().child("classifieds");
        progressBar = view.findViewById(R.id.jobsProgressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.all_jobs);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        JSON_HTTP_CALL();
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


    private void ParseJSonResponse(JSONObject array) throws JSONException{
        progressBar.setVisibility(View.VISIBLE);
        if(ListOfJobs!=null){
            ListOfJobs.clear();
            mAdapter = new SettingsFragment.JobAdapter(ListOfJobs);
            recyclerView.setAdapter(mAdapter);

        }
        //ArrayClear();
        if (ListOfJobs != null) {
            ListOfJobs.clear();
        }

        JSONObject jarr = array;

        Iterator<String> keys = jarr.keys();
        while(keys.hasNext())
        {
            String key = (String)keys.next();
            System.out.println("#################################");
            System.out.println(key);
            System.out.println("#################################");

            String l=jarr.get(key).toString();

            JSONObject json=new JSONObject(l);

            Iterator<String> job_ids=json.keys();

            while(job_ids.hasNext())
            {

                Jobs job=new Jobs();
                String job_key = (String)job_ids.next();
                String h=json.get(job_key).toString();
                JSONObject job_json=new JSONObject(h);

                job.setShopName(key.trim());
                job.setId(job_key);
                job.setDate(job_json.getString("DATE"));
                job.setExperience(job_json.getString("EXPERIENCE"));
                job.setPost(job_json.getString("POST"));
                job.setPostings(job_json.getString("POSTINGS"));
                job.setSalary(job_json.getString("SALARY"));

                ListOfJobs.add(job);
            }

        }

        progressBar.setVisibility(View.INVISIBLE);
        mAdapter = new JobAdapter(ListOfJobs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobsHolder>
    {
        private List<Jobs> jobsList;

        public JobAdapter(List<Jobs> jobsList)
        {
            this.jobsList = jobsList;
        }


        @NonNull
        @Override
        public JobAdapter.JobsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.each_job_detail, parent, false);

            return new JobsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JobAdapter.JobsHolder holder, int position) {

            final Jobs model = jobsList.get(position);
            holder.setShopName(model.getShopName());
            holder.setDatePosted(model.getDate());
            holder.setExperience(model.getExperience());
            holder.setPostPosition(model.getPost());
            holder.setVacancies(model.getPostings());
            holder.setSalary(model.getSalary());

            holder.ApplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference ShopRef;
                    ShopRef = FirebaseDatabase.getInstance().getReference().child("sellers").child("sellers-list").child(model.getShopName()).child("email");
                    ShopRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                            intent.putExtra(Intent.EXTRA_EMAIL,dataSnapshot.getValue(String.class));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Job Application By: " + nameUser);
                            intent.putExtra(Intent.EXTRA_TEXT, "Attach Your CV");
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });

            String latShop = map1.get(model.getShopName()).trim();
            String lngShop = map2.get(model.getShopName()).trim();
            final String uri = "geo:"+latShop+","+lngShop+"?q="+latShop+","+lngShop;
            holder.LocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse(uri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return jobsList.size();
        }

        public class JobsHolder extends RecyclerView.ViewHolder {

            View mView;
            Button ApplyButton, LocationButton;

            public JobsHolder(View itemView)
            {
                super(itemView);
                mView = itemView;
                ApplyButton = (Button) mView.findViewById(R.id.jobApplyButton);
                LocationButton = (Button) mView.findViewById(R.id.buttonDirectionsToShop);
            }

            public void setShopName(final String shopName)
            {
                DatabaseReference ShopRef;
                final TextView shopname = (TextView) mView.findViewById(R.id.jobShopName);
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

            public void setDatePosted(String datePosted)
            {
                TextView dateposted = (TextView) mView.findViewById(R.id.jobDateValue);
                dateposted.setText(datePosted);
            }

            public void setExperience(String experience)
            {
                TextView exp = (TextView) mView.findViewById(R.id.jobExperienceValue);
                exp.setText(experience);
            }

            public void setPostPosition(String post)
            {
                TextView po = (TextView) mView.findViewById(R.id.jobPostValue);
                po.setText(post);
            }

            public void setVacancies(String vacancies)
            {
                TextView vac = (TextView) mView.findViewById(R.id.jobVacancyValue);
                vac.setText(vacancies);
            }

            public void setSalary(String salary)
            {
                TextView sal = (TextView) mView.findViewById(R.id.jobSalaryValue);
                sal.setText(salary);
            }

        }
    }


}
