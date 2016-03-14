package com.wbct.etab.utils;

import com.wbct.etab.bean.StoreItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Siddhartha Maji on 1/11/2016.
 */
public class DataWrapper implements Serializable {

    private ArrayList<StoreItem> storeItems;

    public DataWrapper(ArrayList<StoreItem> data) {
        this.storeItems = data;
    }

    public ArrayList<StoreItem> getStoreItems() {
        return this.storeItems;
    }

}
