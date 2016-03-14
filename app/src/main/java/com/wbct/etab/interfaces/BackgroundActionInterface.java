package com.wbct.etab.interfaces;

import org.json.JSONObject;

/**
 * Created by Siddhartha Maji on 1/5/2016.
 */
public interface BackgroundActionInterface {
    public void onStarted();
    public void onCompleted(JSONObject response);
}