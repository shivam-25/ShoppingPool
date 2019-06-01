package com.example.android.shoppingpool.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.shoppingpool.R;
import com.example.android.shoppingpool.fragments.CartFragment;
import com.example.android.shoppingpool.fragments.SettingsFragment;
import com.example.android.shoppingpool.fragments.SocialFragment;
import com.example.android.shoppingpool.fragments.SoftFragment;
import com.example.android.shoppingpool.fragments.store;
import com.example.android.shoppingpool.models.AppUser;
import com.example.android.shoppingpool.services.LocationTrackerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getSimpleName();
    private static final int PERMISSION_ALL = 1;
    private static final double RADIUS_OF_EARTH = 6371e3; // metres
    private static final double NEARBY_DISTANCE = 2000; // metres
    private ActionBar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;
    private DatabaseReference UsersRef, PostsRef;

    private FirebaseAuth mAuth;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), LocationTrackerService.class));

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Shopping Pool");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("AppUsers");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("username"))
                    {
                        String fullname = dataSnapshot.child("username").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        String[] PERMISSIONS = new String[0];
        try {
            PERMISSIONS = getPermissions(getApplicationContext());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        SharedPreferences sp = getSharedPreferences("LOGGED_USER", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", null);
        Log.d(TAG, "Current User: " + user);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String prefName = sp.getString("prefName", null);
        String prefPhone = sp.getString("prefPhone", null);
        boolean prefAlert = sp.getBoolean("prefAlert", true);
        Log.d(TAG, "Current Settings User: " + "Name: " + prefName + " Phone: " + prefPhone + " Alert: " + prefAlert);

        toolbar = getSupportActionBar();
        // load the store fragment by default

        loadFragment(new store());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            SendUserToLoginActivity();
        }
    }

    protected void SendUserToLoginActivity(){
        Intent loginIntent=new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                SendUserToSetupActivity();
                break;

            case R.id.nav_settings:
                break;

            case R.id.nav_logout:
                logOut();
                mAuth.signOut();
                break;

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_shop:

                    fragment = new store();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_gifts:

                    fragment = new SoftFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_social:

                    fragment = new SocialFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_cart:

                    fragment = new CartFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_settings:

                    fragment = new SettingsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ALL: {

                if (grantResults.length > 0) {

                    List<Integer> indexesOfPermissionsNeededToShow = new ArrayList<>();
                    ArrayList<String> permissionsRequired = new ArrayList<>();
                    for(int i = 0; i < permissions.length; ++i) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            indexesOfPermissionsNeededToShow.add(i);
                        }
                    }

                    int size = indexesOfPermissionsNeededToShow.size();
                    if(size != 0) {
                        int i = 0;
                        boolean isPermissionGranted = true;

                        while(i < size && isPermissionGranted) {
                            isPermissionGranted = grantResults[indexesOfPermissionsNeededToShow.get(i)]
                                    == PackageManager.PERMISSION_GRANTED;
                            i++;
                        }

                        if(!isPermissionGranted) {
                            String title = "Permissions mandatory";
                            String message = "All the permissions are required for this app. Please grant the permissions to proceed.";

                            new AlertDialog.Builder(this)
                                    .setTitle(title)
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            try {
                                                if(! hasPermissions(getApplicationContext(),
                                                        getPermissions(getApplicationContext()))) {
                                                    ActivityCompat.requestPermissions(MainActivity.this,
                                                            getPermissions(getApplicationContext()), PERMISSION_ALL);
                                                }
                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        }
    }

    public static String[] getPermissions(Context context)
            throws PackageManager.NameNotFoundException {
        PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(), PackageManager.GET_PERMISSIONS);

        return info.requestedPermissions;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.container);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logOut() {
        SharedPreferences sp1 = getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        SharedPreferences sp2 = getSharedPreferences("FIRST_LAUNCH", MODE_PRIVATE);



        String currentUser = sp1.getString("current_user", null);
        if (currentUser != null) {
            Gson gson = new Gson();
            AppUser user = gson.fromJson(currentUser, AppUser.class);
            user.getLocation().setAlertAllowed(false);
            // un-subscribe from SMS alerts when the user logs out
            DatabaseReference databaseRef;
            databaseRef = FirebaseDatabase.getInstance().getReference();
            // update the un-subscription online
            databaseRef.child("location").child(user.getPhone()).setValue(user.getLocation());
        }


        SharedPreferences.Editor editor1 = sp1.edit();
        SharedPreferences.Editor editor2 = sp2.edit();
        editor1.clear();
        editor1.commit();
        editor2.clear();
        editor2.commit();
        mAuth.signOut();

        // un-register from the services
        stopService(new Intent(getApplicationContext(), LocationTrackerService.class));

        finish();
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void SendUserToPostActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    private void SendUserToSetupActivity() {
        Intent addNewPostIntent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(addNewPostIntent);
    }
}
