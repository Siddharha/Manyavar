package com.wbct.etab.adapters;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.bean.AccessoriItem;
import com.wbct.etab.bean.CompareItem;
import com.wbct.etab.utils.ImageLoader;

import java.util.List;

/**
 * Created by Siddhartha Maji on 1/4/2016.
 */
public class CompareProductAdapter extends RecyclerView.Adapter<CompareProductAdapter.ViewHolder> {
//region Var dec.
private List<CompareItem> items;
private int itemLayout;
//endregion
//region Constrictors
public CompareProductAdapter(List<CompareItem> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
        }
//endregion
//region Override methods for RecyclerView.Adapter
@Override
public CompareProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(CompareProductAdapter.ViewHolder holder, int position) {
        CompareItem item = items.get(position);
        holder.tvAcrProductCode.setText(item.getProductCode());
        holder.tvAcrPrice.setText(item.getPrice());
        holder.tvColor.setText(item.getColor());
        holder.txtDescription.setText(Html.fromHtml(item.getDescription()));
    holder.imgProduct.setImageBitmap(BitmapFactory.decodeFile(item.getImageUrl()));
    if(item.getImageUrl().contains("http://")) {
        holder.imgLoader.DisplayImage(item.getImageUrl(), holder.imgProduct);
    }
       // holder.tvInr.setText(String.valueOf(item.getImageUrl()));
        holder.itemView.setTag(item);
        }

@Override
public int getItemCount() {
        return items.size();
        }
//endregion
//region View Holder for Recycler Adapter
public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvAcrProductCode,tvAcrPrice,tvColor,txtDescription;
    public ImageView imgProduct;
    public ImageLoader imgLoader;
    public ViewHolder(View itemView) {
        super(itemView);
        tvAcrProductCode = (TextView) itemView.findViewById(R.id.tvCompCode);
        tvColor = (TextView) itemView.findViewById(R.id.tvCompColor);
        tvAcrPrice = (TextView) itemView.findViewById(R.id.tvCompPrice);
        txtDescription = (TextView)itemView.findViewById(R.id.txtDescription);
        imgProduct = (ImageView)itemView.findViewById(R.id.imgProduct);
        imgLoader = new ImageLoader(itemView.getContext());
    }
}
//endregion
}