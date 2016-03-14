package com.wbct.etab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.bean.MenuItem;

import java.util.ArrayList;


/**
 * Created by Siddhartha Maji on 12/31/2015.
 */
public class MenuListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MenuItem> arrayList;
    private LayoutInflater inflater;

    public MenuListAdapter(Context context, ArrayList<MenuItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public MenuItem getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = view;
        if(rootView==null)
        {
            rootView = inflater.inflate(R.layout.menu_list_layout,viewGroup,false);

            ItemHolder itemHolder = new ItemHolder();
            itemHolder.tvName = (TextView)rootView.findViewById(R.id.tvName);

            rootView.setTag(itemHolder);
        }

        ItemHolder myItemHolder = (ItemHolder)rootView.getTag();
        myItemHolder.tvName.setText(arrayList.get(i).getItem_name());

        return rootView;
    }

    private class ItemHolder
    {
        TextView tvName;
    }

}
