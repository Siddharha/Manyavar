package com.wbct.etab.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wbct.etab.R;
import com.wbct.etab.bean.AccessoriItem;
import com.wbct.etab.utils.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Siddhartha Maji on 1/4/2016.
 */
public class AccessoriProductAdapter extends RecyclerView.Adapter<AccessoriProductAdapter.ViewHolder> {
//region Var dec.
private List<AccessoriItem> items;
private int itemLayout;
//endregion
//region Constrictors
public AccessoriProductAdapter(List<AccessoriItem> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
        }
//endregion
//region Override methods for RecyclerView.Adapter
@Override
public AccessoriProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(AccessoriProductAdapter.ViewHolder holder, int position) {
        AccessoriItem item = items.get(position);
        holder.tvAcrProductCode.setText(item.getAcrProductCode());
        holder.tvAcrPrice.setText(item.getAcrPrice());

    try {
        holder.imgAcrImg.setImageBitmap(BitmapFactory.decodeFile(item.getImgUrl()));
    }
    catch (OutOfMemoryError outOfMemoryError)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(item.getImgUrl(),options);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 2, out);
        holder.imgAcrImg.setImageBitmap(bitmap);
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
    public TextView tvAcrProductCode,tvAcrPrice;
    public ImageView imgAcrImg;
    private ImageLoader imgLoader;
    public ViewHolder(View itemView) {
        super(itemView);
        tvAcrProductCode = (TextView) itemView.findViewById(R.id.tvAcrProductCode);
        tvAcrPrice = (TextView) itemView.findViewById(R.id.tvAcrPrice);
        imgAcrImg = (ImageView)itemView.findViewById(R.id.imgAcrImg);
        imgLoader = new ImageLoader(itemView.getContext());
    }
}
//endregion
}