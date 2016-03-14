package com.wbct.etab.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by Siddhartha Maji on 1/13/2016.
 */
public class NetworkConnectionCheck {
    private Context context;
    public NetworkConnectionCheck(Context context){
        this.context = context;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

    public AlertDialog getNetworkActiveAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Status");
        builder.setMessage("Network connection not available. Please connect the network.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                       /* WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(true);*/
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                       /* Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(startMain);*/
                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }
}
