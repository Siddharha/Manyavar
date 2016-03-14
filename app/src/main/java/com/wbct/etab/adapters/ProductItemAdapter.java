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
import com.wbct.etab.bean.ProductItem;
import com.wbct.etab.utils.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * Created by Siddhartha Maji on 1/4/2016.
 */
public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ViewHolder> {
//region Var dec.
private List<ProductItem> items;
private int itemLayout;
//endregion
//region Constrictors
public ProductItemAdapter(List<ProductItem> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;
        }
//endregion
//region Override methods for RecyclerView.Adapter
@Override
public ProductItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
        }

@Override
public void onBindViewHolder(ProductItemAdapter.ViewHolder holder, int position) {
        ProductItem item = items.get(position);
      /*  holder.tvProductCode.setText(item.getProductCode());
        holder.tvColor.setText(item.getColor());
        holder.tvInr.setText(item.getPrice());*/
       // holder.tvInr.setText(String.valueOf(item.getImageUrl()));
        holder.imgProduct.setTag(R.drawable.image_shape);

    try {
        holder.imgProduct.setImageBitmap(BitmapFactory.decodeFile(item.getImgUrl()));
    }
    catch (OutOfMemoryError outOfMemoryError)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(item.getImgUrl(),options);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        holder.imgProduct.setImageBitmap(bitmap);
    }

        holder.itemView.setTag(item);
        }

@Override
public int getItemCount() {
        return items.size();
        }
//endregion
//region View Holder for Recycler Adapter
public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvProductCode,tvColor,tvInr;
    public ImageView imgProduct;
    ImageLoader imgLoader;
    public ViewHolder(View itemView) {
        super(itemView);
       /* tvProductCode = (TextView) itemView.findViewById(R.id.tvProductCode);
        tvColor = (TextView) itemView.findViewById(R.id.tvColor);
        tvInr = (TextView) itemView.findViewById(R.id.tvInr);*/
        imgProduct = (ImageView)itemView.findViewById(R.id.imgProduct);
        imgLoader = new ImageLoader(itemView.getContext());
    }
}
//endregion
}