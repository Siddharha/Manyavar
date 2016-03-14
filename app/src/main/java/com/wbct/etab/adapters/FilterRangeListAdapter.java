package com.wbct.etab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.bean.FilterRangeItem;
import com.wbct.etab.bean.MenuItem;

import java.util.ArrayList;


/**
 * Created by Siddhartha Maji on 12/31/2015.
 */
public class FilterRangeListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FilterRangeItem> arrayList;
    private LayoutInflater inflater;

    public FilterRangeListAdapter(Context context, ArrayList<FilterRangeItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public FilterRangeItem getItem(int i) {
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
            rootView = inflater.inflate(R.layout.filter_item_componetn_layout,viewGroup,false);

            ItemHolder itemHolder = new ItemHolder();
            itemHolder.txtRange = (TextView)rootView.findViewById(R.id.txtRange);
            itemHolder.cbChecked = (CheckBox)rootView.findViewById(R.id.cbChecked);
            rootView.setTag(itemHolder);
        }

        ItemHolder myItemHolder = (ItemHolder)rootView.getTag();
        myItemHolder.txtRange.setText(arrayList.get(i).getText_range());

        if(arrayList.get(i).getChecked()) {
            myItemHolder.cbChecked.setChecked(true);
        }
        else
        {
            myItemHolder.cbChecked.setChecked(false);
        }

        return rootView;
    }

    private class ItemHolder
    {
        TextView txtRange;
        CheckBox cbChecked;
    }

}
