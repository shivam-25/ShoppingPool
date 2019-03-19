package com.example.android.shoppingpool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.shoppingpool.R;

public class SplashActivity extends AppCompatActivity {

    private static final int WAIT_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getSharedPreferences("FIRST_LAUNCH", Context.MODE_PRIVATE);
        final int firstLaunch = sp.getInt("firstLaunch", 1);

        if (firstLaunch == 1) {
            setContentView(R.layout.activity_splash);

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("firstLaunch", 0);
            editor.apply();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }, WAIT_TIME);


        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
