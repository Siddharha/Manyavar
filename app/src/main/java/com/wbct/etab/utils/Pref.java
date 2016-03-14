package com.wbct.etab.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * Created by Siddhartha Maji on 1/5/2016.
 */
public class Pref {
    private SharedPreferences spref;
    //private Activity _activity;
    private static final String PREF_FILE = "manyavar.sid.com.manyavar";
    private Editor _editorSpref;

    public SharedPreferences getSharedPreferencesInstance() {
        return spref;
    }

    public Editor getSharedPreferencesEditorInstance() {
        return _editorSpref;
    }

    @SuppressLint("CommitPrefEdits")
    public Pref(Context _thisContext) {
        // TODO Auto-generated constructor stub
        //this._activity = (Activity)_thisContext;
        spref = _thisContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        _editorSpref = spref.edit();
    }

    public String getSession(String key) {
        String value = spref.getString(key, "");
        return value;
    }

    public int getIntegerSession(String key) {
        int value = spref.getInt(key, Integer.MIN_VALUE);
        return value;
    }

    public void setSession(String key, String value) {
        if (key != null && value != null) {
            _editorSpref.putString(key, value);
            _editorSpref.commit();
        }
    }

    public void setSession(String key, int value) {
        if (key != null) {
            _editorSpref.putInt(key, value);
            _editorSpref.commit();
        }
    }

    public void setSession(String key, boolean value) {
        if (key != null) {
            _editorSpref.putBoolean(key, value);
            _editorSpref.commit();
        }
    }

    public boolean getBooleanSession(String key){
        boolean value=spref.getBoolean(key, false);
        return value;
    }

    public void saveDeviceID(String id) {
        _editorSpref.putString("id", id);
        _editorSpref.commit();
    }

    public void saveimeiID(String id) {
        _editorSpref.putString("imei", id);
        _editorSpref.commit();
    }

    public String getimeiID() {
        return spref.getString("imei", "");
    }


    public void saveAppURL(String url) {
        _editorSpref.putString("AppUrl", url);
        _editorSpref.commit();
    }

    public String getDeviceID() {
        return spref.getString("id", "");
    }

    public String getAppURL() {
        return spref.getString("AppUrl", "");
    }

    public void saveGCMRegId(String GCMid) {
        _editorSpref.putString("GCMid", GCMid);
        _editorSpref.commit();
    }
    public String getGCMRegId()
    {
        return spref.getString("GCMid", "");
    }

    public void saveSyncValue(Boolean b) {
        _editorSpref.putBoolean("snk_visible", b);
        _editorSpref.commit();
    }

    public Boolean getSyncValue()
    {
        return spref.getBoolean("snk_visible", true);
    }



}
