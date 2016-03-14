package com.wbct.etab.activity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wbct.etab.R;
import com.wbct.etab.adapters.MenuListAdapter;
import com.wbct.etab.bean.CompareItem;
import com.wbct.etab.bean.MenuItem;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.interfaces.BackgroundActionInterface;
import com.wbct.etab.utils.CallServiceAction;
import com.wbct.etab.utils.ConstantClass;
import com.wbct.etab.utils.CustomToast;
import com.wbct.etab.utils.ItemGetFromService;
import com.wbct.etab.utils.NetworkConnectionCheck;
import com.wbct.etab.utils.ObtainAndSaveAppUpdate;
import com.wbct.etab.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Siddhartha Maji on 12/31/2015.
 */
public class MenuActivity extends AppCompatActivity implements BackgroundActionInterface{

    ArrayList<MenuItem> menuArray;
    ListView lvMenuItem;
    MenuListAdapter menuAdapter;
    ImageView imgArrowUp,imgArrowDown,imgNotify,imgSync,imgAbout;
    ImageButton imgbASync;
    LinearLayout llUpdate,llDownloadPanel;
    float newVersion,oldVersion;
    TextView txtNotify,tvVersion,txtDownStatus;
    private String fileName,myUrl,appDownloadPath;
    private DbAdapter dbAdapter;
    private String STORE_API_URL = "getCategoryItems";
    private String APP_UPDATE = "updateApp";
    private PackageInfo pInfo;
    private ProgressBar pbDownload;
    private ProgressDialog progressDialog;
    private Boolean chkResponse;
    private CallServiceAction _serviceAction;
    private TelephonyManager telephonyManager;
    private Dialog dialog;
    private Chronometer chTimer;
    int countTime;


    ItemGetFromService itemGetFromService;
    NetworkConnectionCheck networkConnectionCheck;
    private String s_img,s_code,s_color,s_price,s_description,s_video;
    private Pref _pref;


    @Override
    public void onBackPressed() {
        showAleartForExitApp().show();
    }

    private AlertDialog showAleartForExitApp() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(" Exit the app");
        builder.setMessage("Do you really want to Exit ? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initialize();
        checkVersionData();
        populateList();
        deleteUpdateFile();         //Delete Update app.apk if exist on first Start of The Activity
    }




    @Override
    protected void onResume() {
        super.onResume();
        chackNotificationCompareDisplay();
        manageSync();
        manageTimerLock();
    }

    private void manageTimerLock() {
        countTime = 0;


        chTimer.setBase(SystemClock.elapsedRealtime());
        chTimer.start();
        chTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (countTime > ( 60)) {
                    chTimer.stop();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
                countTime++;
                Log.w("COUNT: ",String.valueOf(countTime));

            }
        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        countTime = 0;
        chTimer.setBase(SystemClock.elapsedRealtime());
        chTimer.start();
    }

    private void manageSync() {


        if(_pref.getSyncValue()) {
            //Do Sync Visible....
            imgSync.setVisibility(View.GONE);
        }
        else
        {
            //Do Sync invisible....
            imgSync.setVisibility(View.VISIBLE);
        }
    }


    private void chackNotificationCompareDisplay() {

            dbAdapter.open();
            if(dbAdapter.CompareDataCount()>0)
            {
                imgNotify.setImageResource(R.drawable.comp_products_shape);
                txtNotify.setVisibility(View.VISIBLE);
                txtNotify.setText(String.valueOf(dbAdapter.CompareDataCount()));
            } else
            {
                imgNotify.setImageResource(0);
                txtNotify.setVisibility(View.GONE);
            }
            dbAdapter.close();
    }

    private void checkVersionData() {

        if(networkConnectionCheck.isNetworkAvailable()) {
            _serviceAction.actionInterface = MenuActivity.this;
            _serviceAction.requestVersionApi(null, "versionCheck");
            _serviceAction.requestVersionApi(null, APP_UPDATE);


        }


    }





    private void initialize() {

        _pref = new Pref(this);

        chTimer = (Chronometer)findViewById(R.id.chTimer);
        myUrl = _pref.getAppURL();
        appDownloadPath = "/manyavarApp";
        chkResponse = false;
        txtDownStatus = (TextView)findViewById(R.id.txtDownStatus);
        pbDownload = (ProgressBar)findViewById(R.id.pbDownload);
        imgAbout = (ImageView)findViewById(R.id.imgAbout);
        imgAbout.setColorFilter(Color.parseColor("#f36715"));
        menuArray = new ArrayList<>();
        networkConnectionCheck = new NetworkConnectionCheck(MenuActivity.this);
        telephonyManager =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imgNotify = (ImageView)findViewById(R.id.imgNotify);
        txtNotify = (TextView)findViewById(R.id.txtNotify);
        imgSync = (ImageView)findViewById(R.id.imgSync);
        dbAdapter = new DbAdapter(this);
        progressDialog = new ProgressDialog(this);
        lvMenuItem = (ListView) findViewById(R.id.lvMenuItem);
        imgArrowUp = (ImageView)findViewById(R.id.imgArrowUp);
        imgArrowDown = (ImageView)findViewById(R.id.imgArrowDown);
        _serviceAction = new CallServiceAction(this);
        llDownloadPanel = (LinearLayout)findViewById(R.id.llDownloadPanel);
        llDownloadPanel.setVisibility(View.GONE);
        imgArrowDown.setVisibility(View.VISIBLE);
        imgArrowUp.setVisibility(View.INVISIBLE);
        checkMenuStateforArrow();
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //Initialy This will be Hidden unless Update apk comes...
        llUpdate = (LinearLayout)findViewById(R.id.llUpdate);
        llUpdate.setVisibility(View.GONE);
    }
    private void checkMenuStateforArrow() {
        lvMenuItem.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (lvMenuItem.getFirstVisiblePosition() == 0) {
                    imgArrowDown.setVisibility(View.VISIBLE);
                    imgArrowUp.setVisibility(View.INVISIBLE);
                } else {
                    imgArrowDown.setVisibility(View.VISIBLE);
                    if (menuArray.size() > 3) {
                        imgArrowUp.setVisibility(View.VISIBLE);
                    }
                }

                if (lvMenuItem.getLastVisiblePosition() == totalItemCount - 1) {
                    imgArrowDown.setVisibility(View.INVISIBLE);
                    if (menuArray.size() > 3) {
                        imgArrowUp.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        imgArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMenuItem.smoothScrollToPosition(0);
            }
        });

        imgArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMenuItem.smoothScrollToPosition(lvMenuItem.getScrollBarSize());
            }
        });
    }
    private void populateList() {

      /*  for(int i=0;i<10;i++)
        {
            MenuItem menuItem = new MenuItem();
            menuItem.setItem_name("Menu Item "+i);
            menuArray.add(menuItem);
        }*/
        dbAdapter.open();

        if(menuArray.size()<dbAdapter.getItemMenubyHuiddenAttribute("0").size())
        {
        for(int j =0;j<dbAdapter.getItemMenubyHuiddenAttribute("0").size();j++) {


                MenuItem menuItem = new MenuItem();
                menuItem.setItem_name(dbAdapter.getItemMenubyHuiddenAttribute("0").get(j));
            menuArray.add(menuItem);

        }

        }
        dbAdapter.close();
        loadData();

    }


    private void loadData() {

        menuAdapter = new MenuListAdapter(MenuActivity.this, menuArray);
        lvMenuItem.setAdapter(menuAdapter);

        lvMenuItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MenuActivity.this, ProductStoreActivity.class);
                intent.putExtra("catagory_name", (menuArray.get(position).getItem_name()));
                intent.putExtra("catagory_pos", position);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }
    public void onClickSearch(View view) {
        //Do Search....
        Intent j  = new Intent(MenuActivity.this,SearchActivity.class);
        startActivity(j);

    }
    public void OnFbClick(View view) {
Intent i = new Intent(this,FacebookActivity.class);
        startActivity(i);
    }

    public void onClickUpdate(View view) {

        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+appDownloadPath);
            if (!dir.exists()){
                dir.mkdir();
            }

            final File file = new File(dir, "app.apk");
            if(file.exists())
            {
                installUpdate(file);
            }
            else
            {
                downloadUpdate();
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            //llDownloadPanel.setVisibility(View.GONE);
                            installUpdate(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void downloadUpdate() {

        if(!myUrl.isEmpty()) {
            llDownloadPanel.setVisibility(View.VISIBLE);
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(myUrl));
            r.setTitle("Downloading Updates");
            r.allowScanningByMediaScanner();
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        /*fileName = URLUtil.guessFileName(myUrl, null, MimeTypeMap.getFileExtensionFromUrl(myUrl));*/

            fileName = "app.apk";
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + appDownloadPath, fileName);
            final DownloadManager d = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = d.enqueue(r);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean downloading = true;
                    while (downloading) {
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadId);
                        Cursor cursor = d.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }

                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                pbDownload.setProgress((int) dl_progress);
                                txtDownStatus.setText(pbDownload.getProgress() + " %");
                            }
                        });

                        cursor.close();
                    }
                }
            }).start();

        }



    }

    private void installUpdate( File file) throws IOException {
        llDownloadPanel.setVisibility(View.GONE);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(String.valueOf(file))), "application/vnd.android.package-archive");
        startActivity(intent);


    }
    @Override
    public void onStarted() {
       // imgSync.animate().rotation(180);
    }
    @Override
    public void onCompleted(JSONObject response) {
        Log.i("version Response:", response.toString());

        webResponse(response);

        if(_pref.getAppURL().isEmpty()) {
            new ObtainAndSaveAppUpdate(MenuActivity.this, response);

        }
        try{
            JSONObject jsonObj = response.getJSONObject("errNode");

            if(jsonObj.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                newVersion = Float.parseFloat(jsonObjData.getString("version"));
                oldVersion = Float.parseFloat(pInfo.versionName);
                Log.e("VER>>",String.valueOf(newVersion));
                if(oldVersion <newVersion)
                {
                    llUpdate.setVisibility(View.VISIBLE);

                }
                else
                {
                    llUpdate.setVisibility(View.GONE);
                }
            } else {

                new CustomToast(this,this.getWindow().getDecorView(),jsonObj.getString("errMsg"));
                //Toast.makeText(this, jsonObj.getString("errMsg"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }


        //-----------------------------------------------------------------
      /*  handler.removeCallbacks(runnable);
        imgSync.animate().rotation(0);*/
        imgSync.animate().rotation(0);
        imgSync.clearAnimation();
        imgSync.setColorFilter(Color.TRANSPARENT);
        populateList();

    }

    private void webResponse(JSONObject response) {

        if(chkResponse) {



            if (!response.toString().isEmpty()) {
                Volley.newRequestQueue(this).getCache().clear(); // Clear Cache to recheck Response. :-) Siddhartha Maji
                dbAdapter.open();
                Log.e("MENU_COUNT:", String.valueOf(dbAdapter.MenuDataCount()));
                checkVersionData();
                new MySyncAsync().execute(response);
                chkResponse = false;

            } else {
                chkResponse = false;
                imgSync.animate().rotation(0);
                imgSync.clearAnimation();
                imgSync.setColorFilter(Color.TRANSPARENT);
            }
        }
    }

    private void deleteUpdateFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+appDownloadPath);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public void onClickCompare(View view)
    {
        dbAdapter.open();

                if(dbAdapter.CompareDataCount()>1) {
                    Intent i = new Intent(getBaseContext(), CompareActivity.class);
                    startActivity(i);
                }

        dbAdapter.close();
        }

    public void clkManyavar(View view) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.manyavar_about_popup_layout);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.manyavar_title_layout);
       // dialog.setTitle("About Manyavar");
        dialog.setCancelable(true);
        tvVersion = (TextView)dialog.findViewById(R.id.tvVersion);
        TextView txtAboutVersion = (TextView)dialog.findViewById(R.id.txtAboutVersion);
        ImageButton imgbDownload = (ImageButton)dialog.findViewById(R.id.imgbDownload);
        imgbASync = (ImageButton)dialog.findViewById(R.id.imgbASync);
        imgbASync.setColorFilter(Color.WHITE);
        tvVersion.setText("Version v" + pInfo.versionCode);

        if(imgSync.getVisibility() == View.VISIBLE)
        {
            imgbASync.setVisibility(View.VISIBLE);
            imgbASync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgSync.performClick();
                }
            });
        }
        else
        {
            imgbASync.setVisibility(View.GONE);
        }

        if(newVersion>oldVersion)
        {
            txtAboutVersion.setText("A New Version is available "+"( v"+newVersion+")"+" to Download.");
            imgbDownload.setVisibility(View.VISIBLE);
            imgbDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llUpdate.performClick();
                }
            });
        }
        else
        {
            if(networkConnectionCheck.isNetworkAvailable()) {
                txtAboutVersion.setText("App is up to date.");
            }
            else
            {
                String s = "<font color=\"red\">Network is not Available!</font>";
                txtAboutVersion.setText(Html.fromHtml(s));
            }
            imgbDownload.setVisibility(View.GONE);
        }

        int divierId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);
        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        dialog.show();
        /*lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etFoodName.setText(foodAdapter.filteredData.get(position));
                dialog.dismiss();
            }
        });*/

        /*if(!_pref.getSession("Serached_food").isEmpty()) {
            String foodString = _pref.getSession("Serached_food");
            edSearch.setText(foodString);
        }*/
    }

    public void clkSync(View v) {

                if(!networkConnectionCheck.isNetworkAvailable())
                {
                    networkConnectionCheck.getNetworkActiveAlert().show();


                }
                else {


                    imgSync.animate().rotation(180);
                    imgSync.setColorFilter(Color.BLACK);
                    chkResponse = true;
                    _serviceAction.actionInterface = MenuActivity.this;
                    _serviceAction.requestVersionApi(null, STORE_API_URL);
                }


            }

    private class MySyncAsync extends AsyncTask<JSONObject,Void,Void>{
        @Override
        protected Void doInBackground(JSONObject... params) {
            Log.e("param[0]", params[0].toString());

            new ItemGetFromService(MenuActivity.this,params[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chTimer.stop();
          setUpProgressDialog();
            dbAdapter.deleteallComp();
            dbAdapter.deleteallMenu();
            dbAdapter.deleteallStore();
            dbAdapter.deleteallStoreImgs();
            dbAdapter.deleteallStoreAcc();
            dbAdapter.close();
            chTimer.stop();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    progressDialog.dismiss();
                }
            }, 3000);
        dbAdapter.open();
            if(!menuArray.equals("")) {
                _pref.saveSyncValue(true);
                imgSync.setVisibility(View.GONE);
            }
            dbAdapter.close();
            chTimer.start();
            super.onPostExecute(aVoid);
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sync Process in running... Please keep patience ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }


}


