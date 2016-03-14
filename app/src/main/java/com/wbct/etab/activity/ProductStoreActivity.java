package com.wbct.etab.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wbct.etab.R;
import com.wbct.etab.adapters.FilterRangeListAdapter;
import com.wbct.etab.bean.FilterRangeItem;
import com.wbct.etab.bean.StoreItem;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.utils.CustomToast;
import com.wbct.etab.utils.ImageLoader;
import com.wbct.etab.utils.LruBitmapCache;
import com.wbct.etab.utils.MemoryCache;
import com.wbct.etab.utils.RecyclerItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddhartha Maji on 1/4/2016.
 */
public class ProductStoreActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener{
    private DbAdapter dbAdapter;
    private TextView tvTitle,tvCompNumber;
    private ImageView imgHome;
    private ImageButton imgCompNotify;
    private RecyclerView rvStoreItems;
    StoreItemAdapter storeItemAdapter;
    ArrayList<StoreItem> itemArray;
    LinearLayoutManager layoutManager;
    protected Vibrator vibrate;
    private int scroll_to,menuPosition;
    boolean arrow_trg;
    ImageLoader imageLoader;
    private MyStoreAsync myAsyncTask;
    private Handler handler;
    private Runnable runIt;
    Integer intg;
    ImageView imgStore;
    ArrayList<FilterRangeItem> al_filterArray;
    //private ImageLoader imgLoader;
    private ImageView newimgContainer;
    private String title;
    private String IMG_URL ="http://ec2-54-169-113-141.ap-southeast-1.compute.amazonaws.com/pub/media/catalog/product/r/1/r1.jpg";
   /* private String IMG_URL =  this.getFilesDir().getAbsolutePath() + "/" + "fool.jpg";*/
    private ArrayList<Integer> selectItemPositionArray,result;
    private ProgressDialog progressDialog;
    //region extra
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                result = data.getIntegerArrayListExtra("result");

                Log.e("RESULT >>", String.valueOf(result));

                for (int k = 0; k < result.size(); k++) {
                    StoreItem storeItem = new StoreItem();
                    storeItem.setImgRes(R.drawable.image_shape);
                    storeItem.setImageUrl(IMG_URL);
                    storeItem.setColor(itemArray.get(result.get(k)).getColor());
                    storeItem.setProductCode(itemArray.get(result.get(k)).getProductCode());
                    storeItem.setPrice(itemArray.get(result.get(k)).getPrice());
                    itemArray.add((result.get(k)), storeItem);
                    itemArray.remove(result.get(k)+1);
                    storeItemAdapter.notifyItemChanged(result.get(k));

                    for (int j = 0; j < selectItemPositionArray.size(); j++) {
                        if (result.get(k) == selectItemPositionArray.get(j)) {
                            selectItemPositionArray.remove(j);
                            intg--;
                        }
                    }
                    if (intg > 0) {
                        tvCompNumber.setText(String.valueOf(intg));
                    } else {
                        imgCompNotify.setImageResource(0);
                        tvCompNumber.setVisibility(View.GONE);
                    }
                }
//??? Problem.... Have To Solv
                }

                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }


        }
    }*/
//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_store_layout);
        initialize();
        setStoreItems();
        onClick();
        TitleNameSet();

    }

    @Override
    protected void onResume() {
        super.onResume();

      /*  if(!itemArray.isEmpty())
        {*/
            imageSetupforSelect();
       // }

    }

    private void imageSetupforSelect()
    {
        for(int i = 0; i<itemArray.size();i++)                  ///For transform all images un select through recourse.
        {
            itemArray.get(i).setImgRes(R.drawable.image_shape);
        }


        displayComItems();                                      // dynamically set specific item select.
        storeItemAdapter.notifyItemRangeChanged(0, storeItemAdapter.getItemCount());
    }



    private void displayComItems() {

        dbAdapter.open();
            intg = dbAdapter.CompareDataCount();
            Log.e("intg --> ", String.valueOf(intg));
            storeItemAdapter.notifyDataSetChanged();
            for (int k = 0; k < dbAdapter.CompareDataCount(); k++) {

                if(dbAdapter.getCompareItembyColIndex(1).get(k).equals(title)) {
                    StoreItem storeItem = new StoreItem();
                    storeItem.setImageUrl(dbAdapter.getCompareItembyColIndex(4).get(k));
                    storeItem.setColor(dbAdapter.getCompareItembyColIndex(6).get(k));
                    storeItem.setProductCode(dbAdapter.getCompareItembyColIndex(5).get(k));
                    storeItem.setPrice(dbAdapter.getCompareItembyColIndex(3).get(k));
                    storeItem.setVideoUrl(dbAdapter.getCompareItembyColIndex(7).get(k));
                    storeItem.setDescription(dbAdapter.getCompareItembyColIndex(8).get(k));
                    storeItem.setStoreId(dbAdapter.getCompareItembyColIndex(9).get(k));
                    storeItem.setImgRes(R.drawable.image_check);
                    itemArray.add(Integer.parseInt(dbAdapter.getCompareItembyColIndex(2).get(k)), storeItem);
                    itemArray.remove(Integer.parseInt(dbAdapter.getCompareItembyColIndex(2).get(k)) + 1);
                }
            }

            dbAdapter.close();

            if (intg > 0) {
                tvCompNumber.setText(String.valueOf(intg));
                imgCompNotify.setImageResource(R.drawable.comp_products_shape);
                tvCompNumber.setVisibility(View.VISIBLE);
            } else {
                imgCompNotify.setImageResource(0);
                tvCompNumber.setVisibility(View.GONE);
            }


    }

    @Override
    protected void onStart() {
        super.onStart();
        rvStoreItems.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    private void setStoreItems() {
        loadDataItems();

    }


    private void loadDataItems() {

        Log.e("Title:", title);
       /* if (title.equals("Menu Item 0")) {*/
        dbAdapter.open();
        if(dbAdapter.getStoreItembyColIndexAndCetagiry(1,title).size()>3) {
            preBuffer();

        }
        else
        {
            for (int i = 3; i < dbAdapter.getStoreItembyColIndexAndCetagiry(1,title).size(); i++) {


                //  if(title.equals(dbAdapter.getStoreItembyColIndexAndCetagiry(1,title).get(i))) {
                StoreItem storeItem = new StoreItem();
                storeItem.setProductCode(dbAdapter.getStoreItembyColIndexAndCetagiry(7,title).get(i));
                storeItem.setColor(dbAdapter.getStoreItembyColIndexAndCetagiry(8,title).get(i));
                storeItem.setPrice(dbAdapter.getStoreItembyColIndexAndCetagiry(5,title).get(i));
                storeItem.setImgRes(R.drawable.image_shape);
                storeItem.setImageUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(6,title).get(i));
                storeItem.setDescription(dbAdapter.getStoreItembyColIndexAndCetagiry(4,title).get(i));
                storeItem.setVideoUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(3,title).get(i));
                storeItem.setStoreId(dbAdapter.getStoreItembyColIndexAndCetagiry(9,title).get(i));
                itemArray.add(storeItem);
                //   }
            }
        }

        dbAdapter.close();
        //}

        storeItemAdapter = new StoreItemAdapter(itemArray, R.layout.store_item_layout);
        storeItemAdapter.notifyItemRangeChanged(0, storeItemAdapter.getItemCount());
        rvStoreItems.setAdapter(storeItemAdapter);

    }

    private void loadFullData() {
        dbAdapter.open();
        for (int i = 3; i < dbAdapter.getStoreItembyColIndexAndCetagiry(1,title).size(); i++) {


          //  if(title.equals(dbAdapter.getStoreItembyColIndexAndCetagiry(1,title).get(i))) {
                StoreItem storeItem = new StoreItem();
                storeItem.setProductCode(dbAdapter.getStoreItembyColIndexAndCetagiry(7,title).get(i));
                storeItem.setColor(dbAdapter.getStoreItembyColIndexAndCetagiry(8,title).get(i));
                storeItem.setPrice(dbAdapter.getStoreItembyColIndexAndCetagiry(5,title).get(i));
                storeItem.setImgRes(R.drawable.image_shape);
                storeItem.setImageUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(6,title).get(i));
                storeItem.setDescription(dbAdapter.getStoreItembyColIndexAndCetagiry(4,title).get(i));
                storeItem.setVideoUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(3,title).get(i));
                storeItem.setStoreId(dbAdapter.getStoreItembyColIndexAndCetagiry(9,title).get(i));
                itemArray.add(storeItem);
         //   }
        }
        dbAdapter.close();

    }

    private void preBuffer() {
        dbAdapter.open();
        for (int i = 0; i < 3; i++) {


         //   if(title.equals(dbAdapter.getStoreItembyColIndexAndCetagiry(1, title).get(i))) {
                StoreItem storeItem = new StoreItem();
                storeItem.setProductCode(dbAdapter.getStoreItembyColIndexAndCetagiry(7,title).get(i));
                storeItem.setColor(dbAdapter.getStoreItembyColIndexAndCetagiry(8,title).get(i));
                storeItem.setPrice(dbAdapter.getStoreItembyColIndexAndCetagiry(5,title).get(i));
                storeItem.setImgRes(R.drawable.image_shape);
                storeItem.setImageUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(6,title).get(i));
                storeItem.setDescription(dbAdapter.getStoreItembyColIndexAndCetagiry(4,title).get(i));
                storeItem.setVideoUrl(dbAdapter.getStoreItembyColIndexAndCetagiry(3,title).get(i));
                storeItem.setStoreId(dbAdapter.getStoreItembyColIndexAndCetagiry(9,title).get(i));
                itemArray.add(storeItem);
           // }
        }
        dbAdapter.close();

        handler.postDelayed(runIt = new Runnable() {
            @Override
            public void run() {
                myAsyncTask.execute();
            }
        }, 1000);

    }

    private void TitleNameSet() {

        tvTitle.setText(title);
    }

    private void initialize() {
        Intent i = getIntent();
        title = i.getStringExtra("catagory_name");
        menuPosition = i.getIntExtra("catagory_pos", 0);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        imgHome = (ImageView)findViewById(R.id.imgHome);
        rvStoreItems = (RecyclerView) findViewById(R.id.rvStoreItems);
        imgCompNotify = (ImageButton)findViewById(R.id.imgCompNotify);
        tvCompNumber = (TextView)findViewById(R.id.tvCompNumber);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvStoreItems.setLayoutManager(layoutManager);
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        itemArray = new ArrayList<>();
        arrow_trg = false;
        imgCompNotify.setImageResource(0);
        tvCompNumber.setVisibility(View.GONE);
        dbAdapter = new DbAdapter(this);
        imageLoader = new ImageLoader(this);
        selectItemPositionArray = new ArrayList<>();
        al_filterArray = new ArrayList<>();
        result = new ArrayList<>();
        myAsyncTask = new MyStoreAsync();
        handler = new Handler();
       // imgLoader = new ImageLoader(this);
        intg =  Integer.parseInt(tvCompNumber.getText().toString());

    }

    private void onClick() {
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void r_arrowOnClick(View view)
    {
        scroll_to = layoutManager.findLastCompletelyVisibleItemPosition()+1;
        rvStoreItems.smoothScrollToPosition(scroll_to);
        if(layoutManager.findLastVisibleItemPosition() >=itemArray.size()) {
            scroll_to = layoutManager.findLastVisibleItemPosition();
        }
    }

    public void l_arrowOnClick(View view)
    {
        if(layoutManager.findFirstVisibleItemPosition() <=0) {
            scroll_to = layoutManager.findLastCompletelyVisibleItemPosition();
        }
        else {
            scroll_to = layoutManager.findFirstCompletelyVisibleItemPosition() - 1;
            rvStoreItems.smoothScrollToPosition(scroll_to);
        }
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent i = new Intent(ProductStoreActivity.this,ProductDetailsActivity.class);

        i.putExtra("img_price",itemArray.get(position).getPrice());
        i.putExtra("img_color", itemArray.get(position).getColor());
        i.putExtra("img_pricep_code",itemArray.get(position).getProductCode());
        i.putExtra("img_url", itemArray.get(position).getImageUrl());
        i.putExtra("img_product_description", itemArray.get(position).getDescription());
        i.putExtra("img_product_video", itemArray.get(position).getVideoUrl());
        i.putExtra("storeId", itemArray.get(position).getStoreId());
        startActivity(i);

        Log.e("-->",itemArray.get(position).getImageUrl());
    }

    @Override
    public void onItemLongPress(View childView, int position) {

        imgStore = (ImageView) childView.findViewById(R.id.imgProduct);
        newimgContainer = (ImageView)childView.findViewById(R.id.imgContainer);

       /* Log.e("TAG", String.valueOf((Integer) imgStore.getTag()));
        Log.e("TAG_2", String.valueOf((R.drawable.image_shape)));*/


    //region Select...
    if ((Integer) imgStore.getTag() == R.drawable.image_shape) {

        if(intg <3) {       //intg <3 for Max 3 Item to Select. :-) Siddhartha
        //imgStore.setImageResource(R.drawable.image_check);
            try {

              //  newimgContainer.setBackgroundResource(R.drawable.image_check);
                imageLoader.clearCache();
                imageLoader.DisplayImage(String.valueOf(R.drawable.image_check),newimgContainer);
            }catch (OutOfMemoryError outOfMemoryError)
            {

              // Toast.makeText(getBaseContext(),"OutOf Memory Exception!",Toast.LENGTH_LONG).show();
            }
            //imgContainer.background

       // rvStoreItems.setItemViewCacheSize(itemArray.size());
        imgStore.setTag(R.drawable.image_check);
        tvCompNumber.setVisibility(View.VISIBLE);
        imgCompNotify.setImageResource(R.drawable.comp_products_shape);
        vibrate.vibrate(190);
            selectItemPositionArray.add(position);

            dbAdapter.open();
            dbAdapter.insertCompareData(tvTitle.getText().toString(), position, itemArray.get(position).getPrice(), itemArray.get(position).getImageUrl(), itemArray.get(position).getProductCode(), itemArray.get(position).getColor(),itemArray.get(position).getVideoUrl(),itemArray.get(position).getDescription(),itemArray.get(position).getStoreId());
            intg = dbAdapter.CompareDataCount();
            dbAdapter.close();
            imageSetupforSelect();

    }
        else
        {
            new CustomToast(this,this.getWindow().getDecorView(),"You Cannot Select More than three Product!");
        }

    } else {
       // imgStore.setImageResource(R.drawable.image_shape);
        newimgContainer.setBackgroundResource(R.drawable.image_shape);
        imgStore.setTag(R.drawable.image_shape);
      //  rvStoreItems.setItemViewCacheSize(itemArray.size());
        vibrate.vibrate(190);

        for(int j = 0;j<selectItemPositionArray.size();j++) {
            if (position == selectItemPositionArray.get(j)) {
                selectItemPositionArray.remove(j);
            }
        }

        dbAdapter.open();

        if(dbAdapter.CompareDataCount() >=0) {

            for(int k = 0;k<dbAdapter.CompareDataCount();k++)
            {
                if(itemArray.get(position).getProductCode().equals(dbAdapter.getCompareItembyColIndex(5).get(k)))
                {
                    int code = Integer.parseInt(dbAdapter.getCompareItembyColIndex(0).get(k));      //Get Product Code for Delete the product containing That code. Siddhartha maji.
                    Log.e("CODE --> ", String.valueOf(code));
                    dbAdapter.deleteCompareDataByPosition(code);
                }
            }

        }
        else
        {
            dbAdapter.deleteallComp();


        }
        //intg = dbAdapter.CompareDataCount();
        dbAdapter.close();
        intg--;
    }
    if (intg > 0) {
        tvCompNumber.setText(String.valueOf(intg));
    } else {
        imgCompNotify.setImageResource(0);
        tvCompNumber.setVisibility(View.GONE);
       // rvStoreItems.setItemViewCacheSize(0);
        imageSetupforSelect();

    }
//endregion


    }


    public void onClickCompare(View view)
    {
        /// Activity for Compare.
        if(Integer.parseInt(tvCompNumber.getText().toString()) >1)
        {
             Intent i = new Intent(getBaseContext(),CompareActivity.class);

           /* i.putIntegerArrayListExtra("selected_itemIndex", selectItemPositionArray);
            i.putExtra("data", new DataWrapper(itemArray));*/
            startActivityForResult(i, 1);
        }
    }

    public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.ViewHolder> {
        //region Var dec.
        private List<StoreItem> items;
        private int itemLayout;
        //endregion
//region Constrictors
        public StoreItemAdapter(List<StoreItem> items, int itemLayout) {
            this.items = items;
            this.itemLayout = itemLayout;
        }
        //endregion
//region Override methods for RecyclerView.Adapter
        @Override
        public StoreItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final StoreItemAdapter.ViewHolder holder, int position) {
            StoreItem item = items.get(position);
            holder.tvProductCode.setText(item.getProductCode());
            holder.tvColor.setText(item.getColor());
            holder.tvInr.setText(item.getPrice());
            try {
                Bitmap b = BitmapFactory.decodeFile(item.getImageUrl());

                if (b != null) {
                    holder.imgProduct.setImageBitmap(b);
                } else {
                  //  Toast.makeText(getBaseContext(), "File Not Found!", Toast.LENGTH_LONG).show();
                    holder.imgProduct.setImageResource(R.drawable.imagenotavailable);

                }

                holder.imgContainer.setBackgroundResource(item.getImgRes());
                holder.imgProduct.setTag(item.getImgRes());
            }
            catch (OutOfMemoryError outOfMemoryError)
            {
               // Toast.makeText(getBaseContext(),"Out Of Memory!",Toast.LENGTH_LONG).show();
            }


          /*  try {
                File file = new File(item.getImageUrl());

                if(file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    Bitmap bm = BitmapFactory.decodeStream(fileInputStream);
                    holder.imgProduct.setImageBitmap(bm);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }*/



            /*Bitmap Bfile = BitmapFactory.decodeFile(item.getImageUrl());
            holder.imgProduct.setImageBitmap(Bfile);

            try {
                holder.imgProduct.setImageDrawable(Drawable.createFromPath(item.getImageUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
           // holder.imgProduct.setImageResource(item.getImgRes());

          /*  dbAdapter.open();

            if(dbAdapter.CompareDataCount() >0) {
                if (position == Integer.parseInt(dbAdapter.getCompareItembyColIndex(2).get(position))) {
                    holder.imgProduct.setTag(R.drawable.image_check);
                } else {
                    holder.imgProduct.setTag(R.drawable.image_shape);
                }
            }
            else
            {*/

          /*  }
            dbAdapter.close();*/

            holder.itemView.setTag(item);

           /*  holder.imgProduct.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.imgProduct.setImageResource(R.drawable.image_check);
                    return true;
                }
            });*/


        }

        @Override
        public int getItemCount() {
            return items.size();
        }
        //endregion
//region View Holder for Recycler Adapter
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvProductCode,tvColor,tvInr;
            public ImageView imgProduct;
            private ImageView imgContainer;
            public ViewHolder(View itemView) {
                super(itemView);
                tvProductCode = (TextView) itemView.findViewById(R.id.tvProductCode);
                tvColor = (TextView) itemView.findViewById(R.id.tvColor);
                tvInr = (TextView) itemView.findViewById(R.id.tvInr);
                imgProduct = (ImageView)itemView.findViewById(R.id.imgProduct);
                imgContainer = (ImageView)itemView.findViewById(R.id.imgContainer);
            }

        }


//endregion
    }

    public void OnFbClick(View view)
    {
        Intent i = new Intent(this,FacebookActivity.class);
        startActivity(i);
    }

    public void onClickSearch(View view)
    {
        //Do Search....
        Intent j  = new Intent(getBaseContext(),SearchActivity.class);
        startActivity(j);

    }

    public void clkFilter(View view)
    {
        filterPopup();

    }

    private void filterPopup() {
        //Toast.makeText(this, "Filter not Implemented yet!!!", Toast.LENGTH_SHORT).show();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.filter_item_popup_layout);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.filter_popup_layout);
        dialog.setCancelable(true);
        ListView lvFilterList = (ListView)dialog.findViewById(R.id.lvFilterList);
        for(int i = 0;i<10;i++) {
            FilterRangeItem flItem = new FilterRangeItem();
            flItem.setText_range("Rs. 100 Upto 400");
            flItem.setChecked(false);
            al_filterArray.add(flItem);
        }
        FilterRangeListAdapter flAdapter = new FilterRangeListAdapter(dialog.getContext(),al_filterArray);
        lvFilterList.setAdapter(flAdapter);

        int divierId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);
        divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runIt);
        finish();
        if(!myAsyncTask.isCancelled()){
// Do your stuff
            myAsyncTask.cancel(true);
        }

        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
       // super.onBackPressed();
    }


    private class MyStoreAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            loadFullData();
            return null;
        }

        @Override
        protected void onPreExecute() {

            showStoreLoadingProgress();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvStoreItems.setAdapter(storeItemAdapter);
                    imageSetupforSelect();
                    progressDialog.dismiss();
                }
            }, 3000);

            super.onPostExecute(aVoid);
        }
    }

    private void showStoreLoadingProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Products...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}

