package com.wbct.etab.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wbct.etab.R;
import com.wbct.etab.utils.NetworkConnectionCheck;


/**
 * Created by Siddhartha Maji on 1/13/2016.
 */
public class FacebookActivity extends AppCompatActivity {
    private WebView wvFb;
    String URL = "http://touch.facebook.com/Manyavar/";
    private NetworkConnectionCheck netChick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_page_layout);
        initialize();
        loadPage();

    }

    private void loadPage() {
        if(netChick.isNetworkAvailable()) {


            wvFb.setWebViewClient(new Callback());
            WebSettings w = wvFb.getSettings();
            wvFb.loadUrl(URL);
            w.setDefaultTextEncodingName("utf-8");
            wvFb.setFocusableInTouchMode(false);
            wvFb.setFocusable(false);
           // wvFb.loadData(wvFb.data, "text/html; charset=utf-8",null);



        }
        else
        {
            netChick.getNetworkActiveAlert().show();
        }
    }

    private void initialize() {
        wvFb = (WebView)findViewById(R.id.wvFb);
        netChick = new NetworkConnectionCheck(this);
    }


    private class Callback extends WebViewClient{  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);

        }


    }


}
