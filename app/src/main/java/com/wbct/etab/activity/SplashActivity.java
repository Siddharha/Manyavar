package com.wbct.etab.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.wbct.etab.R;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.interfaces.BackgroundActionInterface;
import com.wbct.etab.utils.CallServiceAction;
import com.wbct.etab.utils.CustomToast;
import com.wbct.etab.utils.ItemGetFromService;
import com.wbct.etab.utils.NetworkConnectionCheck;
import com.wbct.etab.utils.Pref;

import org.json.JSONObject;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity implements BackgroundActionInterface{

    private CallServiceAction _serviceAction;
    private NetworkConnectionCheck networkConnectionCheck;
    private ArrayList<String> MenuItemArray;
    private DbAdapter dbAdapter;
    private ProgressBar pbLoading;
    private Pref _pref;
    String STORE_API_URL = "getCategoryItems";
    ItemGetFromService itemGetFromService;
    String s_img,s_code,s_color,s_price,s_description,s_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_splash);
        initialize();

        if(networkConnectionCheck.isNetworkAvailable()) {
            setUpProgress();
            pursePresess();
        }
        else
        {
            new CustomToast(this,this.getWindow().getDecorView(),"Working on Offline!");
            if(!_pref.getDeviceID().isEmpty()) {
                startActivity(new Intent(SplashActivity.this, SplashVideoActivity.class));
            }
            else
            {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            dbAdapter.open();
            dbAdapter.deleteallComp();
            dbAdapter.close();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    private void pursePresess() {
        _serviceAction = new CallServiceAction(SplashActivity.this);
        _serviceAction.actionInterface = SplashActivity.this;
        _serviceAction.requestVersionApi(null, STORE_API_URL);
       //pbLoading.setVisibility(View.GONE);
    }

    private void initialize() {
        _serviceAction = new CallServiceAction(this);
        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
        networkConnectionCheck = new NetworkConnectionCheck(this);
        MenuItemArray = new ArrayList<>();
        dbAdapter = new DbAdapter(this);
        _pref = new Pref(this);

    }


    private void setUpProgress(){
       pbLoading.setVisibility(View.VISIBLE);

    }


    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        new MyAsync().execute(response);


    }


    class MyAsync extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            Log.e("param[0]",params[0].toString());
            new ItemGetFromService(SplashActivity.this,params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                  /*  if (_pref.getDeviceID().isEmpty()) {*/

                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                  /*  } else {
                        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }*/

                    dbAdapter.open();
                    dbAdapter.deleteallComp();
                    dbAdapter.close();

                }
            }, 3000);
        }
    }

}
