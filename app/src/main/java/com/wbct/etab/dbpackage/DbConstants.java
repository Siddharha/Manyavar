package com.wbct.etab.dbpackage;

/**
 * Created by Siddhartha Maji on 1/22/2016.
 */
public class DbConstants {
    public static final String TAG_DB_NAME = "db_manyavar";
    public static final String TAG_TB_COMPARE = "tb_compare";
    public static final String TAG_TB_STORE = "tb_store";
    public static final String TAG_TB_MENU = "tb_menu";
    public static final String TAG_TB_ITEM_IMG = "tb_imgs";
    public static final String TAG_TB_ITEM_ACC = " tb_acc";


    public static final int TAG_DB_VERSION = 1;

    ///////////////////////////////////////////////////////////////////////////
    public static final String TAG_QUERY_COMPARE_PRODUCT_CODE = "SELECT * FROM tb_compare ORDER BY id DESC";
    public static final String TAG_QUERY_STORE_PRODUCT_CODE = "SELECT * FROM tb_store ORDER BY id DESC";
    public static final String TAG_QUERY_MENU_PRODUCT_CODE = "SELECT * FROM tb_menu ORDER BY id DESC";
    public static final String TAG_QUERY_PRODUCT_IMG_CODE = "SELECT * FROM tb_imgs ORDER BY id DESC";
    public static final String TAG_QUERY_PRODUCT_ACC_CODE = "SELECT * FROM tb_acc ORDER BY id DESC";

    public static final String TAG_QUERY_GET_STORE_ITEM = "SELECT * FROM tb_store"; // added by Rahul Das 8/3/16
    ///////////////////////////////////////////////////////////////////////////

    /*******COMPARE TABLE*******/
    public static final String TAG_COMP_ID = "id";
    public static final String TAG_MENU_ITEM = "manuItem";
    public static final String TAG_POSITION_ITEM = "position";
    public static final String TAG_PRICE = "price";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_CODE = "productCode";
    public static final String TAG_COLOR = "color";
    public static final String TAG_VIDEO = "video";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_ID_STR = "storeId";

    /*******STORE TABLE*******/
    public static final String TAG_STORE_ID = "id";
    public static final String TAG_S_MENU_ITEM = "manuItem";
    public static final String TAG_S_POSITION_ITEM = "position";
    public static final String TAG_S_PRICE = "price";
    public static final String TAG_S_IMAGE = "image";
    public static final String TAG_S_CODE = "productCode";
    public static final String TAG_S_COLOR = "color";
    public static final String TAG_S_VIDEO = "video";
    public static final String TAG_S_DESCRIPTION = "description";
    public static final String TAG_S_STORE_ID = "storeId";

    /*******MENU TABLE*******/
    public static final String TAG_MENU_ID = "id";
    public static final String TAG_M_MENU_ITEM = "manuItem";
    public static final String TAG_M_HIDEN_STATUS = "isHidden";
    public static final String TAG_M_MODIFY_DATE = "modifyedDate";
    public static final String TAG_M_MENU_ID = "menuId";

    /*******IMGS TABLE*******/
    public static final String TAG_ITEM_IMG_ID = "id";
    public static final String TAG_IT_CO_ITEM_ID = "itmId";
    public static final String TAG_IT_IM_ITEM = "manuItem";

    /*******ACCESSORIES TABLE*******/
    public static final String TAG_ITEM_ACC_ID = "id";
    public static final String TAG_CO_ITEM = "itmCode";
    public static final String TAG_IT_ACC_CO_ITEM = "itmAccCode";
    public static final String TAG_ACC_PR_ITEM = "accPrice";
    public static final String TAG_ACC_IMG_ITEM = "accImgs";
    public static final String TAG_ACC_IMG_ID = "storeId";

    public static final String
            TAG_CREATE_COMPARE_TABLE = "create table tb_compare" +
            "(id integer primary key autoincrement," +
            " manuItem text not null," +
            " position int not null," +
            " price text not null," +
            " image text not null," +
            " productCode text not null," +
            " color text not null," +
            " video text not null," +
            " description text not null," +
            " storeId text not null)";

    public static final String
            TAG_CREATE_STORE_TABLE = "create table tb_store" +
            "(id integer primary key autoincrement," +
            " manuItem text not null," +
            " position int not null," +
            " video text not null," +
            " description text not null," +
            " price text not null," +
            " image text not null," +
            " productCode text not null," +
            " color text not null," +
            " storeId text not null)";

    public static final String
            TAG_CREATE_MENU_TABLE = "create table tb_menu" +
            "(id integer primary key autoincrement," +
            " manuItem text not null," +
            " isHidden text not null," +
            " modifyedDate text not null," +
            " menuId text not null)";

    public static final String
            TAG_CREATE_IMGS_TABLE = "create table tb_imgs" +
            "(id integer primary key autoincrement," +
            " itmId text not null," +
            " manuItem text not null)";

    public static final String
            TAG_CREATE_ACC_TABLE = "create table tb_acc" +
            "(id integer primary key autoincrement," +
            " itmCode text not null," +
            " itmAccCode text not null," +
            " accPrice text not null," +
            " accImgs text not null," +
            " storeId text not null)";

    public static final String TAG_FETCH_DETAILS =
            "SELECT * FROM tb_compare";

    public static final String TAG_DROP_USER_TABLE =
            "DROP TABLE IF EXISTS tb_compare";

    public static final String TAG_FETCH_ITEM_IMGS =
            "SELECT * FROM tb_imgs";

    public static final String TAG_FETCH_ITEM_STORE =
            "SELECT * FROM tb_store";

    public static final String TAG_FETCH_ITEM_MENU =
            "SELECT * FROM tb_menu";

    public static final String TAG_FETCH_ITEM_ACC =
            "SELECT * FROM tb_acc";
}
