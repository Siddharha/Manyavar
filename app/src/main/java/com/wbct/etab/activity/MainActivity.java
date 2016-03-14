package com.wbct.etab.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wbct.etab.R;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.interfaces.BackgroundActionInterface;
import com.wbct.etab.utils.CallServiceAction;
import com.wbct.etab.utils.ConstantClass;
import com.wbct.etab.utils.CustomToast;
import com.wbct.etab.utils.ManyavarApplication;
import com.wbct.etab.utils.NetworkConnectionCheck;
import com.wbct.etab.utils.Pref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Siddhartha Maji on 12/31/2015.
 */
public class MainActivity extends AppCompatActivity implements BackgroundActionInterface{
    private Button btnEnter;
    private EditText etStoreId;
    TelephonyManager telephonyManager;
    private CallServiceAction _serviceAction;
    private ProgressBar pgrLoad;
    private NetworkConnectionCheck networkConnectionCheck;
    CustomToast customToast;
    private String APP_UPDATE_URL = "registerDevice";
    String imeiNumber;
    DbAdapter dbAdapter;
    private Pref _pref;
    private String id;
    GoogleCloudMessaging gcm;
    String regId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        obtainDeviceIMEI();
        displayId();
        registerDevice();
        onClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbAdapter.open();
        dbAdapter.deleteallComp();
        dbAdapter.close();

    }

    private void registerDevice() {

        if(!_pref.getGCMRegId().equals("")){

            Log.v("GCM RegId: ", regId);
            sendGCMRegId();
        }
        else{

            Log.v("GCM RegId: ", regId);
            regId = registerGCM();

        }

            JSONObject parentJsonObj0 = new JSONObject();
            JSONObject childJsonObj0 = new JSONObject();
        try {
            childJsonObj0.put("imeiNo", imeiNumber);
            childJsonObj0.put("regId", regId);
            parentJsonObj0.put("data", childJsonObj0);
            Log.e("Input JSON: ", parentJsonObj0.toString());

            _serviceAction.actionInterface = MainActivity.this;
            _serviceAction.requestVersionApi(parentJsonObj0, APP_UPDATE_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    private void obtainDeviceIMEI() {
        imeiNumber = telephonyManager.getDeviceId();
        Log.e("Device IEMI No.- ", imeiNumber);

        if(_pref.getimeiID().isEmpty()) {
            _pref.saveimeiID(imeiNumber);
        }
    }

    public String registerGCM() {

        registerInBackground();
        return regId;

    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regId = gcm.register(ConstantClass.GOOGLE_PROJECT_ID);
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                Log.v("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                _pref.saveGCMRegId(regId);
                sendGCMRegId();

            }
        }.execute(null, null, null);
    }

    private void sendGCMRegId(){

        if (networkConnectionCheck.isNetworkAvailable()) {

            try {


                JSONObject jsonObject0 = new JSONObject();
                jsonObject0.put("imeiNo", _pref.getimeiID());
                jsonObject0.put("regId", _pref.getGCMRegId());

                JSONObject data = new JSONObject();
                data.put("data", jsonObject0);

                String jsonInput = data.toString();
                Log.e("jsonInput0", jsonInput);
                _serviceAction.actionInterface = MainActivity.this;
                _serviceAction.requestVersionApi(data,"registerDevice");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } else {

            networkConnectionCheck.getNetworkActiveAlert().show();
        }

    }

    private void displayId() {

        if(!_pref.getDeviceID().isEmpty())
        {
            etStoreId.setText(_pref.getDeviceID());
        }
    }

    private void onClick() {
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = etStoreId.getText().toString();
                try {
                    JSONObject parentJsonObj = new JSONObject();
                    JSONObject childJsonObj = new JSONObject();
                    childJsonObj.put("imeiNo", imeiNumber);
                    childJsonObj.put("storeId", id);
                    parentJsonObj.put("data", childJsonObj);
                    Log.e("Input JSON: ", parentJsonObj.toString());


                    if(id.equals("DEVw"))
                    {
                        Toast.makeText(getBaseContext(),"Devoloper : Siddhartha Maji",Toast.LENGTH_LONG).show();
                        _serviceAction.requestVersionApi(parentJsonObj, APP_UPDATE_URL);
                        Intent intent = new Intent(getBaseContext(),MenuActivity.class);
                        startActivity(intent);
                    }
                    else {
                        _serviceAction.actionInterface = MainActivity.this;
                        _serviceAction.requestVersionApi(parentJsonObj, "login");
                        pgrLoad.setVisibility(View.VISIBLE);
                    }


                }
                catch (Exception e){
                    //e.printStackTrace();
                }
                //Implement Condition for IMEI Matches Server database?? if yes Grand Entered PIN and allow to move to Menu Activity.

            }
        });
    }

    private void initialize() {
        btnEnter = (Button)findViewById(R.id.btnEnter);
        etStoreId = (EditText)findViewById(R.id.etStoreId);
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        _serviceAction = new CallServiceAction(this);
        pgrLoad = (ProgressBar)findViewById(R.id.pgrLoad);
        pgrLoad.setVisibility(View.GONE);
        dbAdapter = new DbAdapter(this);
        networkConnectionCheck = new NetworkConnectionCheck(this);
        _pref = new Pref(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {


        Volley.newRequestQueue(this).getCache().clear(); // Clear Cache for recheck Response. :-) Siddhartha Maji
        Log.e("RESP:", response.toString());
//--------------------------------------------------------------------------------

            try {

                JSONObject jsonObj = response.getJSONObject("errNode");

                if (jsonObj.getInt("errCode") == 0) {
                    JSONObject jsonObjData = response.getJSONObject("data");

                    if (jsonObjData.getBoolean("success")) {
                        String msg = jsonObjData.getString("msg");
                        Log.e("MSG >> ", msg);
                        pgrLoad.setVisibility(View.GONE);
                        if(!id.equals("DEVw")) {
                            _pref.saveDeviceID(id);
                        }

                        if(msg.equals("Store is Valid.")) {
                            Intent intent = new Intent(getBaseContext(), SplashVideoActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {



                    customToast = new CustomToast(this,this.getWindow().getDecorView(),jsonObj.getString("errMsg"));
                    //Toast.makeText(this, jsonObj.getString("errMsg"), Toast.LENGTH_SHORT).show();
                    pgrLoad.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }




        //--------------------------------------------------------------------------------------



    }

}
