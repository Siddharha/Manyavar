package com.wbct.etab.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wbct.etab.R;

/**
 * Created by Siddhartha Maji on 1/28/2016.
 */
public class CustomToast {



    LayoutInflater inflater;

    public CustomToast(Context context, View view, String message) {
        inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup)view.findViewById(R.id.llToastContainer));
        // set a message
        TextView txtToast = (TextView) layout.findViewById(R.id.txtToast);
        txtToast.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }
}
