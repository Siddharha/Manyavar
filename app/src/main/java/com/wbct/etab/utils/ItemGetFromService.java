package com.wbct.etab.utils;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.wbct.etab.dbpackage.DbAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Siddhartha Maji on 1/28/2016.
 */
public class ItemGetFromService {
    private DbAdapter dbAdapter;
    private String imgDownloadPath, ImgfileName,s_img,s_code,s_color,s_price,s_description,s_video,s_id;

    public ItemGetFromService(Context context, JSONObject response) {

        initialize(context);
        try {
            JSONObject jsonObject = response.getJSONObject("errNode");
            if (jsonObject.getString("errCode").equals("0")) {
                JSONObject jsonObjectStore = response.getJSONObject("data");
                JSONArray categoryItems = jsonObjectStore.getJSONArray("categoryItems");
                //region CategoryItem[]

                dbAdapter.open();
                if (dbAdapter.MenuDataCount() < categoryItems.length()) {
                    for (int i = dbAdapter.MenuDataCount(); i < categoryItems.length(); i++) {
                        JSONObject jsonObjectStoreItems = categoryItems.getJSONObject(i);
                        JSONObject jsonObject1 = jsonObjectStoreItems.getJSONObject("category");
                        String s_cat = jsonObject1.getString("category_name");
                        String s_hide = jsonObject1.getString("is_hide");
                        String cat_modified_date = jsonObject1.getString("cat_modified_date");
                        String menu_id = jsonObject1.getString("menuId");
                        Log.w("s_cat:",s_cat);
                        dbAdapter.open();

                        if (dbAdapter.MenuDataCount() < categoryItems.length()) {
                            dbAdapter.insertMenuData(s_cat,s_hide,cat_modified_date,menu_id);
                        }

                        dbAdapter.close();

                        JSONArray productItemsArray = jsonObjectStoreItems.getJSONArray("productItems");

                        //region ProductItems[]
                        dbAdapter.open();
                        for (int l = 0; l < productItemsArray.length(); l++) {
                            JSONObject JsStoreArguments = productItemsArray.getJSONObject(l);
                            JSONArray JsStoreImgArray = JsStoreArguments.getJSONArray("images");
                            JSONArray JsStoreAccArray = JsStoreArguments.getJSONArray("accessories");
                            s_img = JsStoreArguments.getString("image");
                            s_code = JsStoreArguments.getString("code");
                            s_color = JsStoreArguments.getString("color");
                            s_price = JsStoreArguments.getString("price");
                            s_description = JsStoreArguments.getString("description");
                            s_video = JsStoreArguments.getString("video");
                            s_id = JsStoreArguments.getString("productId");

                            Log.w("Store_item",s_id);
                            //region images[]
                            for (int m = 0; m < JsStoreImgArray.length(); m++)

                            {
                                JSONObject jsonObject2 = JsStoreImgArray.getJSONObject(m);
                                String i_S_imgs = jsonObject2.getString("img");

                                String s_imgs = manageImagesInStore(context, i_S_imgs);

                                dbAdapter.open();
                                if (dbAdapter.getItemImgsbyItemCode(s_id).size() < JsStoreImgArray.length()) {
                                    dbAdapter.insertItemImgsData(s_id, s_imgs);
                                    Log.e("IMG: ", s_id + "," + s_imgs);
                                }

                                Log.e("ST_L: ", String.valueOf(dbAdapter.StoreDataCount()));
                                dbAdapter.close();


                            }
//endregion
                            //region accessories[]
                            for (int m = 0; m < JsStoreAccArray.length(); m++)

                            {
                                JSONObject jsonObject3 = JsStoreAccArray.getJSONObject(m);
                                String acc_s_code = jsonObject3.getString("code");
                                String acc_s_img = jsonObject3.getString("img");
                                String acc_img = manageImagesInStore(context, acc_s_img);
                                String acc_s_price = jsonObject3.getString("price");

                                dbAdapter.open();
                                if (dbAdapter.getAccItembyItemCode(s_id).size() < JsStoreAccArray.length()) {
                                    dbAdapter.insertAccItemData(s_code, acc_s_code, acc_s_price, acc_img,s_id);
                                }
                                  Log.w("ACC_IMG: ", s_code + "," + acc_s_img);
                                dbAdapter.close();


                            }
//endregion
                            String Store_img = manageImagesInStore(context,s_img);
                            dbAdapter.open();
                            dbAdapter.insertStoreData(s_cat, i, s_video, s_description, s_price, Store_img, s_code, s_color,s_id);
                            // Toast.makeText(context, "Data Insurted Sucessfully!", Toast.LENGTH_LONG).show();
                            Log.e("Size:", String.valueOf(dbAdapter.StoreDataCount()));

                            dbAdapter.close();
                        }
                        dbAdapter.close();
//endregion

                    }
                }

               /* else
                {

                    for (int i = 0; i < categoryItems.length(); i++) {
                        JSONObject jsonObjectStoreItems = categoryItems.getJSONObject(i);
                        JSONObject jsonObject1 = jsonObjectStoreItems.getJSONObject("category");
                        String s_cat = jsonObject1.getString("category_name");
                        String s_hide = jsonObject1.getString("is_hide");
                        String cat_modified_date = jsonObject1.getString("cat_modified_date");
                        String menu_id = jsonObject1.getString("menuId");
                        // MenuItemArray.add(s_cat);


                        dbAdapter.open();

                        if(!dbAdapter.getMenuItembyColIndex(3).get(i).equals(cat_modified_date))
                        {
                            Log.w(">>", s_cat);
                            dbAdapter.modifyMenuData(s_cat, s_hide, cat_modified_date,menu_id);
                        }
                        dbAdapter.close();

                        //region ProductItems[]
                        JSONArray productItemsArray = jsonObjectStoreItems.getJSONArray("productItems");
                        dbAdapter.open();
                        for (int l = 0; l < productItemsArray.length(); l++) {
                            JSONObject JsStoreArguments = productItemsArray.getJSONObject(l);
                            JSONArray JsStoreImgArray = JsStoreArguments.getJSONArray("images");
                            JSONArray JsStoreAccArray = JsStoreArguments.getJSONArray("accessories");
                            s_img = JsStoreArguments.getString("image");
                            s_code = JsStoreArguments.getString("code");
                            s_color = JsStoreArguments.getString("color");
                            s_price = JsStoreArguments.getString("price");
                            s_description = JsStoreArguments.getString("description");
                            s_video = JsStoreArguments.getString("video");

                            Log.w("Store_item",String.valueOf(productItemsArray.length()));


                            String Store_img = manageImagesInStore(context,s_img);
                            dbAdapter.open();
                            for(int k = 0;k<dbAdapter.getStoreItembyColIndex(0).size();k++) {
                                if(dbAdapter.getStoreItembyColIndex(0).get(k).equals(s_cat)) {
                                    dbAdapter.modifyStoreData(s_cat, k, s_video, s_description, s_price, Store_img, s_code, s_color);
                                }
                                // Toast.makeText(context, "Data Insurted Sucessfully!", Toast.LENGTH_LONG).show();
                                Log.e("Size:", String.valueOf(dbAdapter.StoreDataCount()));
                            }
                            dbAdapter.close();
                        }
                        dbAdapter.close();
//endregion
                    }
                }*/
//endregion

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //delay();
        //endregion
    }

    private String manageImagesInStore(Context context, String imageUrl) {
        ImgfileName = URLUtil.guessFileName(imageUrl, null, MimeTypeMap.getFileExtensionFromUrl(imageUrl));
        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + imgDownloadPath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        final File file = new File(dir, ImgfileName);
        if (file.exists()) {
            //installUpdate(file);
            // playVideo(dir.getAbsolutePath()+"/"+ImgfileName);
            //Toast.makeText( context, "Image File Exist.", Toast.LENGTH_LONG).show();
            return (dir.getAbsolutePath() + "/" + ImgfileName);
        } else {
            downloadImage(context, imageUrl);
            Log.e("FILE_NAME::", ImgfileName);
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    //installUpdate(file);
                    //  Toast.makeText(context, "Now Image File Exist.", Toast.LENGTH_LONG).show();
                    // playVideo(dir.getAbsolutePath()+"/"+ImgfileName);
                }
            };
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return (dir.getAbsolutePath() + "/" + ImgfileName);
        }


    }

    private void downloadImage(Context context, String imgUrl) {
        DownloadManager.Request r = new DownloadManager.Request(Uri.parse(imgUrl));
        r.setTitle("Downloading Updates");
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        //fileName = "app.apk";
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+imgDownloadPath, ImgfileName);

        DownloadManager d = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        d.enqueue(r);

    }

    private void initialize(Context context) {
        dbAdapter = new DbAdapter(context);
        imgDownloadPath = "/manyavarImges";
    }
}
