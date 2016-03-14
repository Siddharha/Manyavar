package com.wbct.etab.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BLUEHORSE 123 on 2/16/2016.
 */
public class ObtainAndSaveAppUpdate {
    private Pref _pref;

    public ObtainAndSaveAppUpdate(Context context, JSONObject response) {
        initialize(context);
        try {
            JSONObject jsonObject = response.getJSONObject("errNode");

            if (jsonObject.getString("errCode").equals("0")) {
                JSONObject jsonObjectStore = response.getJSONObject("data");
                String myUrl = jsonObjectStore.getString("appUrl");
                _pref.saveAppURL(myUrl);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }


    }

    private void initialize(Context context) {
        _pref = new Pref(context);
    }
}
