package com.wbct.etab.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.wbct.etab.R;

/**
 * Created by Siddhartha Maji on 2/2/2016.
 */
public class ShowVideoActivity extends AppCompatActivity {
    private VideoView vvItemVideo;
    private String videoPath;
    private ImageView imgFull,imgPlayPause;
    LinearLayout llVideoContPanel;
    DisplayMetrics metrics;
    LinearLayout.LayoutParams params;
    SeekBar videoSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);
        initialize();
        loadVideo();
    }

    private void initialize() {
        videoSeekBar = (SeekBar)findViewById(R.id.videoSeekBar);
        vvItemVideo = (VideoView)findViewById(R.id.vvItemVideo);
        imgFull = (ImageView)findViewById(R.id.imgFull);
        imgFull.setTag(R.drawable.no_fullscreen);
        metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        params = (android.widget.LinearLayout.LayoutParams) vvItemVideo.getLayoutParams();
        llVideoContPanel = (LinearLayout)findViewById(R.id.llVideoContPanel);
        imgPlayPause = (ImageView)findViewById(R.id.imgPlayPause);
        imgPlayPause.setTag(R.drawable.pause);
    }

    private void loadVideo() {

        Intent i = getIntent();
        videoPath = i.getStringExtra("Video_path");
        Log.e("PATH:", videoPath);
        vvItemVideo.setVideoURI(Uri.parse(videoPath));
        fullScreen(vvItemVideo);
        vvItemVideo.start();
        videoStateWatcher();
    }



    private void orignalScreen(VideoView vvItemVideo) {
        params.width =  metrics.widthPixels/2;
        params.height = metrics.heightPixels/2;
        vvItemVideo.setLayoutParams(params);
    }

    private void fullScreen(VideoView vvItemVideo) {
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        vvItemVideo.setLayoutParams(params);
    }

    public void clkFull(View view)
    {

        if(imgFull.getTag().equals(R.drawable.fullscreen)) {
            imgFull.setImageResource(R.drawable.no_fullscreen);
            imgFull.setTag(R.drawable.no_fullscreen);
            fullScreen(vvItemVideo);
        }
        else
        {
            imgFull.setImageResource(R.drawable.fullscreen);
            imgFull.setTag(R.drawable.fullscreen);
            orignalScreen(vvItemVideo);
        }
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

    public void ClkCross(View view)
    {
        onBackPressed();
    }

    public void clkPlayPause(View view)
    {
        if(imgPlayPause.getTag().equals(R.drawable.pause))
        {
            imgPlayPause.setImageResource(R.drawable.play);
            imgPlayPause.setTag(R.drawable.play);
            vvItemVideo.pause();
        }
        else
        {
            imgPlayPause.setImageResource(R.drawable.pause);
            imgPlayPause.setTag(R.drawable.pause);
            vvItemVideo.start();
            videoSeekBar.setProgress(vvItemVideo.getCurrentPosition());
            onEverySecond.run();
            videoSeekBar.setMax(vvItemVideo.getDuration());
            videoSeekBar.postDelayed(onEverySecond, 1000);
        }
    }

    private void videoStateWatcher() {

        vvItemVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgPlayPause.setImageResource(R.drawable.replay);
                imgPlayPause.setTag(R.drawable.play);

            }
        });

        vvItemVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoSeekBar.setMax(vvItemVideo.getDuration());
                videoSeekBar.postDelayed(onEverySecond, 1000);
            }
        });

       videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           @Override
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               if(fromUser) {
                   // this is when actually seekbar has been seeked to a new position
                   vvItemVideo.seekTo(progress);
               }
           }

           @Override
           public void onStartTrackingTouch(SeekBar seekBar) {

           }

           @Override
           public void onStopTrackingTouch(SeekBar seekBar) {

           }
       });
    }

    private Runnable onEverySecond=new Runnable() {

        @Override
        public void run() {

            if(videoSeekBar != null) {
                videoSeekBar.setProgress(vvItemVideo.getCurrentPosition());
            }

            if(vvItemVideo.isPlaying()) {
                videoSeekBar.postDelayed(onEverySecond, 1000);
            }

        }
    };
}
