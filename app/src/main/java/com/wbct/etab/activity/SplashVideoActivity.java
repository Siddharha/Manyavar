package com.wbct.etab.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.wbct.etab.R;
import com.wbct.etab.interfaces.BackgroundActionInterface;
import com.wbct.etab.utils.CallServiceAction;
import com.wbct.etab.utils.NetworkConnectionCheck;
import com.wbct.etab.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Siddhartha Maji on 2/2/2016.
 */
public class SplashVideoActivity extends AppCompatActivity implements BackgroundActionInterface{
    private VideoView vvSplashVideo;
    private String videoPath,appDownloadPath,VidfileName;
    private ImageView imgPlayPause;
    String STORE_SPLASH_VIDEO = "splashVideo";
    private NetworkConnectionCheck nCheck;
    private CallServiceAction csAction;
    LinearLayout llVideoContPanel;
    ProgressDialog progressDialog;
    DisplayMetrics metrics;
    Pref _pref;
    LinearLayout.LayoutParams params;
    private File dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_video_layout);
        initialize();
        loadVideo();
    }

    private void initialize() {
        csAction = new CallServiceAction(this);
        _pref = new Pref(this);
        vvSplashVideo = (VideoView)findViewById(R.id.vvSplashVideo);
        appDownloadPath = "/manyavarSplashVideos";
        VidfileName = "SplashVideo";
        nCheck = new NetworkConnectionCheck(this);
        metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params = (LinearLayout.LayoutParams) vvSplashVideo.getLayoutParams();
        llVideoContPanel = (LinearLayout)findViewById(R.id.llVideoContPanel);
        imgPlayPause = (ImageView)findViewById(R.id.imgPlayPause);
        imgPlayPause.setTag(R.drawable.pause);
    }

    private void loadVideo() {

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + appDownloadPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        final File file = new File(dir, VidfileName);
        if (file.exists()) {
            //installUpdate(file);
            playVideo(dir.getAbsolutePath() + "/" +VidfileName);
            //Toast.makeText(getBaseContext(), "Video File Exist.", Toast.LENGTH_LONG).show();
        } else {

            if(nCheck.isNetworkAvailable()) {
               callServiceForVideofromUrl();

            }
            else
            {
                nCheck.getNetworkActiveAlert().show();
            }
        }


    }

    private void callServiceForVideofromUrl() {
        csAction.actionInterface = SplashVideoActivity.this;
        csAction.requestVersionApi(null,STORE_SPLASH_VIDEO);
    }

    private void downloadUpdate(String vidUrl) {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(vidUrl));
        r.setTitle("Downloading Splash Video For First Time...");
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        //fileName = "app.apk";
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+appDownloadPath, VidfileName);

        DownloadManager d = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        d.enqueue(r);

    }

    private void playVideo(String s) {
        vvSplashVideo.setVideoURI(Uri.parse(s));
        fullScreen(vvSplashVideo);
        vvSplashVideo.start();
        videoStateWatcher();
    }


    private void fullScreen(VideoView vvItemVideo) {
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        vvItemVideo.setLayoutParams(params);
    }

    public void clkBody(View view)
    {
        if(llVideoContPanel.getVisibility() == View.VISIBLE)
        {
            llVideoContPanel.setVisibility(View.GONE);
        }
        else
        {
            llVideoContPanel.setVisibility(View.VISIBLE);
        }

    }

    public void clkSkip(View view)
    {
       startActivity(new Intent(SplashVideoActivity.this,MenuActivity.class));
        finish();
    }

    public void clkPlayPause(View view)
    {
        if(imgPlayPause.getTag().equals(R.drawable.pause))
        {
            imgPlayPause.setImageResource(R.drawable.play);
            imgPlayPause.setTag(R.drawable.play);
            vvSplashVideo.pause();
        }
        else
        {
            imgPlayPause.setImageResource(R.drawable.pause);
            imgPlayPause.setTag(R.drawable.pause);
            vvSplashVideo.start();
        }
    }

    private void videoStateWatcher() {

        vvSplashVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               /* imgPlayPause.setImageResource(R.drawable.replay);
                imgPlayPause.setTag(R.drawable.play);*/
                startActivity(new Intent(SplashVideoActivity.this,MenuActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {

        try {
            JSONObject   jsonObject = response.getJSONObject("errNode");
            if (jsonObject.getString("errCode").equals("0")) {
                JSONObject jsonObjectStore = response.getJSONObject("data");
                String s_splashVideoUrl = jsonObjectStore.getString("splashVideo");
                setUpProgressDialog();
                downloadUpdate(s_splashVideoUrl);
                Log.d("SPLASH_NAME:", s_splashVideoUrl);
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        //installUpdate(file);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                progressDialog.dismiss();
                                //Toast.makeText(getBaseContext(), "Now Video File Exist.", Toast.LENGTH_LONG).show();
                                playVideo(dir.getAbsolutePath() + "/" + VidfileName);
                            }
                        }, 3000);

                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Video Downloading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }
}
