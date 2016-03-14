package com.wbct.etab.activity;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.adapters.AccessoriProductAdapter;
import com.wbct.etab.adapters.ProductItemAdapter;
import com.wbct.etab.bean.AccessoriItem;
import com.wbct.etab.bean.ProductItem;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.utils.NetworkConnectionCheck;
import com.wbct.etab.utils.RecyclerItemClickListener;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.BlurMaskFilter.*;

/**
 * Created by Siddhartha Maji on 1/5/2016.
 */
public class ProductDetailsActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {
    private static String IMG_URL;
   // private static final String IMG_URL_2 = "http://www.jnvu.edu.in/images/no_image.jpg";
    LinearLayout llAccessories, llDescription, llVideo;
    ProductItemAdapter productItemAdapter;
    AccessoriProductAdapter accessoriProductAdapter;
    ArrayList<ProductItem> productList;
    ArrayList<AccessoriItem> accessoriList;
    private RecyclerView rvProductDetailse, rvAccessories;
    LinearLayoutManager layoutManager, layoutManagerAccessories;
    CardView cvAccessories, cvDescription;
    TextView tvProductDetailsCode, tvColor, tvPrice;
    TextView tvDelailseDescription;
    DbAdapter dbAdapter;
    ImageView imgVideoPlay,imgAcces,imgDescn;
    private NetworkConnectionCheck networkConnectionCheck;
    private ProgressDialog progressDialog;
    String s_code,s_id, VidfileName, appDownloadPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_layout);
        initialize();
        loadProductDetails();
        loadProducts();
        onClick();

    }




    private void checkVideoOffline(String s) {
        String nameVid = URLUtil.guessFileName(s, null, MimeTypeMap.getFileExtensionFromUrl(s));
        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + appDownloadPath);
        final File file = new File(dir, nameVid);
        if (file.exists()) {
            imgVideoPlay.setColorFilter(Color.parseColor("#009600"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        rvProductDetailse.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        rvAccessories.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    private void loadProductDetails() {
        Intent i = getIntent();
        s_code = i.getStringExtra("img_pricep_code");
        String s_color = i.getStringExtra("img_color");
        String s_price = i.getStringExtra("img_price");
        String s_description = i.getStringExtra("img_product_description");
        String s_video = i.getStringExtra("img_product_video");
        s_id = i.getStringExtra("storeId");

       // IMG_URL = i.getStringExtra("img_url");
        tvDelailseDescription.setText(Html.fromHtml(s_description));
        tvProductDetailsCode.setText(s_code);
        tvColor.setText(s_color);
        tvPrice.setText(s_price);
        initializeVideo(s_video);
        checkVideoOffline(s_video);

    }

    private void initializeVideo(final String s_vid) {

        if (s_vid.isEmpty()) {
            llVideo.setVisibility(View.INVISIBLE);
        } else {
            llVideo.setVisibility(View.VISIBLE);
            llVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VidfileName = URLUtil.guessFileName(s_vid, null, MimeTypeMap.getFileExtensionFromUrl(s_vid));

                    manageVideo(s_vid);
                }
            });
        }
    }

    private void manageVideo(String s) {
            final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + appDownloadPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            final File file = new File(dir, VidfileName);
            if (file.exists()) {
                //installUpdate(file);
                playVideo(dir.getAbsolutePath() + "/" +VidfileName);
                //Toast.makeText(getBaseContext(), "Video File Exist.", Toast.LENGTH_LONG).show();
            } else {

                if(networkConnectionCheck.isNetworkAvailable()) {
                    setUpProgressDialog();
                    downloadUpdate(s);
                    Log.e("FILE_NAME::", s);
                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            //installUpdate(file);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    progressDialog.dismiss();
                                    imgVideoPlay.setColorFilter(Color.parseColor("#009600"));
                                    //Toast.makeText(getBaseContext(), "Now Video File Exist.", Toast.LENGTH_LONG).show();
                                    playVideo(dir.getAbsolutePath() + "/" + VidfileName);
                                }
                            }, 3000);

                        }
                    };
                    registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                }
                else
                {
                    networkConnectionCheck.getNetworkActiveAlert().show();
                }
            }


    }
    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Video Downloading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }
    private void playVideo(String vidfilepath) {

        Intent i = new Intent(ProductDetailsActivity.this,ShowVideoActivity.class);
        i.putExtra("Video_path",vidfilepath);
        startActivity(i);
    }
    private void downloadUpdate(String vidUrl) {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(vidUrl));
        r.setTitle("Downloading Updates");
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        //fileName = "app.apk";
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+appDownloadPath, VidfileName);

        DownloadManager d = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        d.enqueue(r);

    }
    private void loadProducts() {
        generateTemporaryData();
        displayData();
    }
    private void generateTemporaryData() {

     /*   for(int i = 0; i<5 ; i++)
        {

            AccessoriItem _accessoriItem = new AccessoriItem();
            _accessoriItem.setAcrProductCode("COD_ACCR_"+i);
            _accessoriItem.setAcrPrice("Price: " + i + i * 2);
            _accessoriItem.setImgUrl(IMG_URL_2);
            accessoriList.add(_accessoriItem);
        }*/

        dbAdapter.open();

        for(int v = 0;v<dbAdapter.getAccItembyItemCode(s_id).size();v++) {
            AccessoriItem _productItem = new AccessoriItem();
            _productItem.setImgUrl(dbAdapter.getAccItembyItemCode(s_id).get(v).getImgUrl());
            _productItem.setAcrPrice(dbAdapter.getAccItembyItemCode(s_id).get(v).getAcrPrice());
            _productItem.setAcrProductCode(dbAdapter.getAccItembyItemCode(s_id).get(v).getAcrProductCode());
            accessoriList.add(_productItem);
        }
        Log.e("ACCESSORIES",String.valueOf(dbAdapter.getItemImgsbyItemCode(s_id)));
        Log.e("CODE",s_code);
        dbAdapter.close();

        dbAdapter.open();

        for(int v = 0;v<dbAdapter.getItemImgsbyItemCode(s_id).size();v++) {
            ProductItem _productItem = new ProductItem();
            _productItem.setImgUrl(dbAdapter.getItemImgsbyItemCode(s_id).get(v));
            productList.add(_productItem);
        }
        Log.e("PRODUCT_LIST",String.valueOf(dbAdapter.getItemImgsbyItemCode(s_id)));
        dbAdapter.close();



    }
    private void displayData() {
        productItemAdapter = new ProductItemAdapter(productList,R.layout.product_item_layout);
        rvProductDetailse.setAdapter(productItemAdapter);
        accessoriProductAdapter = new AccessoriProductAdapter(accessoriList,R.layout.product_item_accessories);
        rvAccessories.setAdapter(accessoriProductAdapter);
    }
    private void onClick() {
        llAccessories.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v) {

                if(cvDescription.getVisibility() == View.VISIBLE)
                {
                    llDescription.performClick();
                }
                if(cvAccessories.getVisibility() == View.GONE)
                {
                    cvAccessories.setVisibility(View.VISIBLE);
                    imgAcces.animate().rotation(225);
                    imgAcces.setColorFilter(Color.BLACK);
                    cvAccessories.animate().translationY(0).alpha(1.0f);

                }
                else
                {

                    cvAccessories.animate().translationY(-50).alpha(0.0f);
                    cvAccessories.setVisibility(View.GONE);
                    imgAcces.animate().rotation(0);
                    imgAcces.clearColorFilter();
                    imgAcces.clearAnimation();
                }

            }
        });

        llDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cvAccessories.getVisibility() == View.VISIBLE) {
                    llAccessories.performClick();
                }
                if (cvDescription.getVisibility() == View.GONE) {
                    cvDescription.setVisibility(View.VISIBLE);
                    imgDescn.animate().rotation(225);
                    imgDescn.setColorFilter(Color.BLACK);
                    cvDescription.animate().translationY(0).alpha(1.0f);

                } else {


                    cvDescription.animate().translationY(-70).alpha(0.0f);
                    cvDescription.setVisibility(View.GONE);
                    imgDescn.animate().rotation(0);
                    imgDescn.clearColorFilter();
                    imgDescn.clearAnimation();
                }
            }
        });
    }
    private void initialize() {
        networkConnectionCheck = new NetworkConnectionCheck(this);
        rvAccessories = (RecyclerView)findViewById(R.id.rvAccessories);
        llAccessories = (LinearLayout)findViewById(R.id.llAccessories);
        llDescription = (LinearLayout)findViewById(R.id.llDescription);
        cvAccessories = (CardView)findViewById(R.id.cvAccessories);
        cvDescription = (CardView)findViewById(R.id.cvDescription);
        llVideo = (LinearLayout)findViewById(R.id.llVideo);
        imgVideoPlay = (ImageView)findViewById(R.id.imgVideoPlay);
        imgAcces = (ImageView)findViewById(R.id.imgAcces);
        imgDescn = (ImageView)findViewById(R.id.imgDescn);
        appDownloadPath = "/manyavarVideos";
        dbAdapter = new DbAdapter(this);
        productList = new ArrayList<>();
        accessoriList = new ArrayList<>();
        rvProductDetailse = (RecyclerView)findViewById(R.id.rvProductDetailse);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        layoutManagerAccessories = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvProductDetailse.setLayoutManager(layoutManager);
        rvAccessories.setLayoutManager(layoutManagerAccessories);
        tvProductDetailsCode = (TextView)findViewById(R.id.tvProductDetailsCode);
        tvDelailseDescription = (TextView)findViewById(R.id.tvDelailseDescription);
        tvColor = (TextView)findViewById(R.id.tvColor);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        cvAccessories.animate().translationY(-50).alpha(0.0f);
        rvAccessories.animate().translationY(0);
        cvDescription.animate().translationY(-50).alpha(0.0f);
        cvAccessories.setVisibility(View.GONE);
        cvDescription.setVisibility(View.GONE);

    }
    public void HomeClick(View view) {
        Intent i = new Intent(getBaseContext(),MenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }
    @Override
    public void onItemClick(View childView, int position) {

        Intent i = new Intent(this,FullImageActivity.class);
        Log.e("CONTEXT:",childView.getParent().toString());
        if(childView.getParent().toString().contains("rvProductDetailse"))
        {
            i.putExtra("Img_url",productList.get(position).getImgUrl());
        } else {
            i.putExtra("Img_url",accessoriList.get(position).getImgUrl());
        }

        startActivity(i);
    }
    @Override
    public void onItemLongPress(View childView, int position) {

    }
    public void OnFbClick(View view)
    {
        Intent i = new Intent(this,FacebookActivity.class);
        startActivity(i);
    }
}
