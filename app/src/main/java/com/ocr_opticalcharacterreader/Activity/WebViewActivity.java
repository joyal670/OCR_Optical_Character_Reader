package com.ocr_opticalcharacterreader.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ocr_opticalcharacterreader.R;

public class WebViewActivity extends AppCompatActivity
{
    WebView webView;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorBlue));
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        webView = (WebView)findViewById(R.id.webView);

        searchQuery = getIntent().getExtras().getString("Query");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.com/search?q="+searchQuery);
        webView.setWebViewClient(new WebViewClient());
    }

    @Override
    public void onBackPressed()
    {
        if (webView.copyBackForwardList().getCurrentIndex() > 0) {
            webView.goBack();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}