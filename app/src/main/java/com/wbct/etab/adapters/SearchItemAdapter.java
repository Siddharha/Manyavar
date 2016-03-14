package com.wbct.etab.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.bean.ProductItem;
import com.wbct.etab.bean.SearchItem;
import com.wbct.etab.utils.ImageLoader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Siddhartha Maji on 1/4/2016.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder> /*implements Filterable*/{
//region Var dec.
    public List<SearchItem> items;
    private int itemLayout;
   // public List<SearchItem> filteredUserList;

    //endregion
//region Constrictors
public SearchItemAdapter(List<SearchItem> items, int itemLayout) {
        this.items = items;
       // this.filteredUserList = new ArrayList<>();
        this.itemLayout = itemLayout;
        }
//endregion
//region Override methods for RecyclerView.Adapter
@Override
public SearchItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(SearchItemAdapter.ViewHolder holder, int position) {
        SearchItem item = items.get(position);
        holder.tvSProductCode.setText(item.getProductCode());
        holder.tvSColor.setText(item.getColor());
        holder.tvSInr.setText(item.getPrice());
       // holder.tvInr.setText(String.valueOf(item.getImageUrl()));
        holder.imgSProduct.setTag(R.drawable.image_shape);

    holder.imgSProduct.setImageBitmap(BitmapFactory.decodeFile(item.getImageUrl()));

        holder.itemView.setTag(item);
        }

@Override
public int getItemCount() {
        return items.size();
        }

/*@Override
public Filter getFilter() {
    return new SearchItemFilter(this, items);
}*/


    //endregion
//region View Holder for Recycler Adapter
public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvSProductCode,tvSColor,tvSInr;
    public ImageView imgSProduct;
    ImageLoader imgLoader;
    public ViewHolder(View itemView) {
        super(itemView);
        tvSProductCode = (TextView) itemView.findViewById(R.id.tvSProductCode);
        tvSColor = (TextView) itemView.findViewById(R.id.tvSColor);
        tvSInr = (TextView) itemView.findViewById(R.id.tvSInr);
        imgSProduct = (ImageView)itemView.findViewById(R.id.imgSProduct);
        imgLoader = new ImageLoader(itemView.getContext());
    }
}

   /* private class SearchItemFilter extends Filter {
        private final SearchItemAdapter adapter;
        private final List<SearchItem> originalList;
        private final List<SearchItem> filteredList;
        public SearchItemFilter(SearchItemAdapter searchItemAdapter, List<SearchItem> items) {
            super();
            this.adapter = searchItemAdapter;
            this.originalList = new LinkedList<>(items);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
                //filteredList.clear();
            } else {
                final String filterPattern = constraint.toString().toUpperCase().trim();

                for (final SearchItem searchItem : originalList) {
                    if ((searchItem.getColor().contains(filterPattern))||(searchItem.getProductCode().contains(filterPattern))) {
                        filteredList.add(searchItem);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredUserList.clear();
            adapter.filteredUserList.addAll((ArrayList<SearchItem>) results.values);
            Log.e("FList >> ", adapter.filteredUserList.toString());
            adapter.notifyDataSetChanged();
        }
    }*/
}
//endregion
