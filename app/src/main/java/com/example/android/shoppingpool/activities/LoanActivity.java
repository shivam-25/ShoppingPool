
package com.example.android.shoppingpool.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.android.shoppingpool.R;

public class LoanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);



        WebView webView=new WebView(this);
        setContentView(webView);
        webView.loadUrl("https://instantloan.fullertonindia.com/personal-loan?utm_source=FICCL_Website&utm_medium=Website_Organic&utm_campaign=website_salaried_PL_topban&se=FICCL_Website&cp=Website_Organic&ag=website_salaried_PL_topban&_ga=2.174334886.520132283.1556879915-429482617.1556879915");
    }
}
