package com.wbct.etab.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaCryptoException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wbct.etab.R;
import com.wbct.etab.utils.ImageLoader;
import com.wbct.etab.utils.TouchImageView;

import java.io.ByteArrayOutputStream;
import java.net.URL;

/**
 * Created by Siddhartha Maji on 1/11/2016.
 */
public class FullImageActivity extends AppCompatActivity{
    private TouchImageView imgFullImage;
    private ImageLoader imgLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_img_layout);
        initialize();
        attachImage();
        onClick();

    }

    private void attachImage() {
        Intent i = getIntent();
       String s = i.getStringExtra("Img_url");
        imgLoader.clearCache();

        try {
            imgFullImage.setImageBitmap(BitmapFactory.decodeFile(s));
        }catch (OutOfMemoryError outOfMemoryError)
        {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(s,options);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 2, out);
                imgFullImage.setImageBitmap(bitmap);

        }


        //Bitmap bm =  BitmapFactory.decodeResource(getResources(), R.drawable.home_new_old);
        //imgFullImage.setImageBitmap(bm);

    }

    private void initialize() {
        imgFullImage = (TouchImageView)findViewById(R.id.imgFullImage);
        imgLoader = new ImageLoader(this);
    }

    public void ClkCross(View view)
    {
        finish();
    }

    private void onClick() {

    }

}
