package com.wbct.etab.dbpackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import com.wbct.etab.bean.AccessoriItem;
import com.wbct.etab.bean.SearchItem;
import com.wbct.etab.utils.Pref;

import java.util.ArrayList;

/**
 * Created by Siddhartha Maji on 1/22/2016.
 */
public class DbAdapter {
    private DbHandler dbHandler;
    private Context context;
    private SQLiteDatabase dbSqLiteDatabase;
    private Pref _pref;

    public DbAdapter(Context context){
        this.context = context;
        dbHandler = new DbHandler(context);
        _pref = new Pref(context);
    }

    public DbAdapter open(){
        dbHandler = new DbHandler(context);
        dbSqLiteDatabase = dbHandler.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHandler.close();
    }

    private class DbHandler extends SQLiteOpenHelper {

        private DbHandler(Context context) {
            super(context, DbConstants.TAG_DB_NAME, null, DbConstants.TAG_DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DbConstants.TAG_CREATE_COMPARE_TABLE);
            db.execSQL(DbConstants.TAG_CREATE_STORE_TABLE);
            db.execSQL(DbConstants.TAG_CREATE_MENU_TABLE);
            db.execSQL(DbConstants.TAG_CREATE_IMGS_TABLE);
            db.execSQL(DbConstants.TAG_CREATE_ACC_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DbConstants.TAG_DROP_USER_TABLE);
        }
    }

    public void insertCompareData(String manuItem,int position,String price,String image,String productCode,String color, String video, String desc,String storeId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_MENU_ITEM, manuItem);
        contentValues.put(DbConstants.TAG_POSITION_ITEM, position);
        contentValues.put(DbConstants.TAG_PRICE, price);
        contentValues.put(DbConstants.TAG_IMAGE, image);
        contentValues.put(DbConstants.TAG_CODE, productCode);
        contentValues.put(DbConstants.TAG_COLOR, color);
        contentValues.put(DbConstants.TAG_VIDEO, video);
        contentValues.put(DbConstants.TAG_DESCRIPTION, desc);
        contentValues.put(DbConstants.TAG_ID_STR, storeId);
        dbSqLiteDatabase.insert(DbConstants.TAG_TB_COMPARE, null, contentValues);
    }

    public void insertStoreData(String manuItem,int position,String videoUrl,String description,String price,String image,String productCode,String color,String storeId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_S_MENU_ITEM, manuItem);
        contentValues.put(DbConstants.TAG_S_POSITION_ITEM, position);
        contentValues.put(DbConstants.TAG_S_VIDEO, videoUrl);
        contentValues.put(DbConstants.TAG_S_DESCRIPTION, description);
        contentValues.put(DbConstants.TAG_S_PRICE, price);
        contentValues.put(DbConstants.TAG_S_IMAGE, image);
        contentValues.put(DbConstants.TAG_S_CODE, productCode);
        contentValues.put(DbConstants.TAG_S_COLOR, color);
        contentValues.put(DbConstants.TAG_S_STORE_ID, storeId);
        dbSqLiteDatabase.insert(DbConstants.TAG_TB_STORE, null, contentValues);
    }

    public void modifyStoreData(String manuItem,int position,String videoUrl,String description,String price,String image,String productCode,String color){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_S_MENU_ITEM, manuItem);
        contentValues.put(DbConstants.TAG_S_POSITION_ITEM, position);
        contentValues.put(DbConstants.TAG_S_VIDEO, videoUrl);
        contentValues.put(DbConstants.TAG_S_DESCRIPTION, description);
        contentValues.put(DbConstants.TAG_S_PRICE, price);
        contentValues.put(DbConstants.TAG_S_IMAGE, image);
        contentValues.put(DbConstants.TAG_S_CODE, productCode);
        contentValues.put(DbConstants.TAG_S_COLOR, color);
        dbSqLiteDatabase.update(DbConstants.TAG_TB_STORE, contentValues, DbConstants.TAG_S_POSITION_ITEM + "='" + position + "'", null);
    }

    public void insertMenuData(String manuItem,String is_hidden,String cat_modified_date,String menuId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_M_MENU_ITEM, manuItem);
        contentValues.put(DbConstants.TAG_M_HIDEN_STATUS, is_hidden);
        contentValues.put(DbConstants.TAG_M_MODIFY_DATE, cat_modified_date);
        contentValues.put(DbConstants.TAG_M_MENU_ID, menuId);
        dbSqLiteDatabase.insert(DbConstants.TAG_TB_MENU, null, contentValues);
    }

    public void modifyMenuData(String menuItem,String is_hidden,String cat_modified_date, String menuId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_M_MENU_ITEM, menuItem);
        contentValues.put(DbConstants.TAG_M_HIDEN_STATUS, is_hidden);
        contentValues.put(DbConstants.TAG_M_MODIFY_DATE, cat_modified_date);
        contentValues.put(DbConstants.TAG_M_MENU_ID, menuId);
        dbSqLiteDatabase.update(DbConstants.TAG_TB_MENU, contentValues, DbConstants.TAG_M_MENU_ID + "='" + menuId + "'", null);
    }

    public void insertItemImgsData(String storeId, String itemImgs){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_IT_CO_ITEM_ID, storeId);
        contentValues.put(DbConstants.TAG_IT_IM_ITEM, itemImgs);
        dbSqLiteDatabase.insert(DbConstants.TAG_TB_ITEM_IMG, null, contentValues);
    }

    public void insertAccItemData(String storeItemCode, String accImgCode, String price, String imgs, String storeId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbConstants.TAG_CO_ITEM, storeItemCode);
        contentValues.put(DbConstants.TAG_IT_ACC_CO_ITEM, accImgCode);
        contentValues.put(DbConstants.TAG_ACC_PR_ITEM, price);
        contentValues.put(DbConstants.TAG_ACC_IMG_ITEM, imgs);
        contentValues.put(DbConstants.TAG_ACC_IMG_ID, storeId);
        dbSqLiteDatabase.insert(DbConstants.TAG_TB_ITEM_ACC, null, contentValues);
    }

    public ArrayList<SearchItem> getSearchResult(String searchItem) {
        ArrayList<SearchItem> searchItems = new ArrayList<>();
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_GET_STORE_ITEM +
                " WHERE productCode LIKE '%" + searchItem + "%' OR color LIKE '%" +
                searchItem + "%' ORDER BY id DESC", null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                SearchItem _item = new SearchItem();
                _item.setImageUrl(cursor.getString(cursor.getColumnIndex("image")));
                _item.setVideoUrl(cursor.getString(cursor.getColumnIndex("video")));
                _item.setStoreId(cursor.getString(cursor.getColumnIndex("storeId")));
                _item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                _item.setPrice(cursor.getString(cursor.getColumnIndex("price")));
                _item.setColor(cursor.getString(cursor.getColumnIndex("color")));
                _item.setProductCode(cursor.getString(cursor.getColumnIndex("productCode")));
                _item.setImgRes(cursor.getInt(cursor.getColumnIndex("id")));
                searchItems.add(_item);
                Log.e("color", "==>>" +
                        cursor.getString(cursor.getColumnIndex("color")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchItems;
    }

    public ArrayList<String> getCompareItembyColIndex(int index){
        ArrayList<String> productCode = new ArrayList<>();
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_COMPARE_PRODUCT_CODE, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                productCode.add(cursor.getString(index));
            }
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return productCode;
    }

    public ArrayList<String> getStoreItembyColIndexAndCetagiry(int index,String cetagory){
        ArrayList<String> productCode = new ArrayList<>();
        String StoreFetchQuery = DbConstants.TAG_FETCH_ITEM_STORE + " WHERE " +
                DbConstants.TAG_S_MENU_ITEM + "='" + cetagory + "'";
        Cursor cursor = dbSqLiteDatabase.rawQuery(StoreFetchQuery, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                productCode.add(cursor.getString(index));
            }
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return productCode;
    }

    public ArrayList<String> getMenuItembyColIndex(int index){
        ArrayList<String> productCode = new ArrayList<>();
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_MENU_PRODUCT_CODE, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                productCode.add(cursor.getString(index));
            }
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return productCode;
    }

    public ArrayList<String> getItemImgsbyItemCode(String storeId){

        //-------------------------------------------------
        ArrayList<String> imgs = new ArrayList<>();
            String ImgFetchQuery = DbConstants.TAG_FETCH_ITEM_IMGS + " WHERE " +
                    DbConstants.TAG_IT_CO_ITEM_ID + "='" + storeId + "'";

            Cursor cursor = dbSqLiteDatabase.rawQuery(ImgFetchQuery, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                imgs.add(cursor.getString(cursor.getColumnIndex(DbConstants.TAG_IT_IM_ITEM)));
                //cursor.moveToNext();

            }
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return imgs;
        //-------------------------------------------------
    }

    public ArrayList<String> getItemMenubyHuiddenAttribute(String isHidden){

        //-------------------------------------------------
        ArrayList<String> menuItems = new ArrayList<>();
        String MenuItemFetchQuery = DbConstants.TAG_FETCH_ITEM_MENU + " WHERE " +
                DbConstants.TAG_M_HIDEN_STATUS + "='" + isHidden + "'";
        Cursor cursor = dbSqLiteDatabase.rawQuery(MenuItemFetchQuery, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                menuItems.add(cursor.getString(cursor.getColumnIndex(DbConstants.TAG_M_MENU_ITEM)));
                //cursor.moveToNext();

            }
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return menuItems;
        //-------------------------------------------------
    }

    public ArrayList<AccessoriItem> getAccItembyItemCode(String storeId){

        //-------------------------------------------------
        ArrayList<AccessoriItem> acc = new ArrayList<>();
        String AccFetchQuery = DbConstants.TAG_FETCH_ITEM_ACC + " WHERE " +
                DbConstants.TAG_ACC_IMG_ID + "='" + storeId + "'";
        Cursor cursor = dbSqLiteDatabase.rawQuery(AccFetchQuery, null);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                AccessoriItem accessoriItem = new AccessoriItem();
                accessoriItem.setAcrProductCode(cursor.getString(cursor.getColumnIndex(DbConstants.TAG_IT_ACC_CO_ITEM)));
                accessoriItem.setAcrPrice(cursor.getString(cursor.getColumnIndex(DbConstants.TAG_ACC_PR_ITEM)));
                accessoriItem.setImgUrl(cursor.getString(cursor.getColumnIndex(DbConstants.TAG_ACC_IMG_ITEM)));
                acc.add(accessoriItem);
                //cursor.moveToNext();

            }
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return acc;
        //-------------------------------------------------
    }

    public ArrayList<String> getItemImgsbyColIndex(int index){
        ArrayList<String> productCode = new ArrayList<>();
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_PRODUCT_IMG_CODE, null);

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                productCode.add(cursor.getString(index));
            }
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return productCode;
    }

    public int CompareDataCount()
    {
    int l = 0;
    Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_COMPARE_PRODUCT_CODE, null);
        try {
            /*for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                l++;
            }*/
            l = cursor.getCount();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

    return l;
    }

    public int StoreDataCount()
    {
        int l = 0;
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_STORE_PRODUCT_CODE, null);
        try {
            /*for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                l++;
            }*/
            l = cursor.getCount();
            Log.e("store count", l + "");
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return l;
    }

    public int MenuDataCount()
    {
        int l = 0;
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_MENU_PRODUCT_CODE, null);
        try {
            /*for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                l++;
            }*/
            l = cursor.getCount();
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return l;
    }

    public int StoreImgsCount()
    {
        int l = 0;
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_PRODUCT_IMG_CODE, null);
        try {
            /*for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                l++;
            }*/
            l = cursor.getCount();
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }
        return l;
    }

    public int StoreAccCount()
    {
        int l = 0;
        Cursor cursor = dbSqLiteDatabase.rawQuery(DbConstants.TAG_QUERY_PRODUCT_ACC_CODE, null);
        try {
            /*for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                l++;
            }*/
            l = cursor.getCount();
        }finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return l;
    }

    public boolean deleteCompareDataByPosition(int  code){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_COMPARE, "id = " + code, null) > 0;
        /*return dbSqLiteDatabase.delete(DbConstants.TAG_TB_COMPARE, String.valueOf(cursor.getPosition()),null) > 0;*/
    }

    public boolean deleteallComp(){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_COMPARE, null, null) > 0;
    }

    public boolean deleteallMenu(){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_MENU, null, null) > 0;
    }
    public boolean deleteallStore(){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_STORE, null, null) > 0;
    }

    public boolean deleteallStoreImgs(){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_ITEM_IMG, null, null) > 0;
    }

    public boolean deleteallStoreAcc(){
        return dbSqLiteDatabase.delete(DbConstants.TAG_TB_ITEM_ACC, null, null) > 0;
    }

    }
