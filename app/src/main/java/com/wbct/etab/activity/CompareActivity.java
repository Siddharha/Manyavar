package com.wbct.etab.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.wbct.etab.R;
import com.wbct.etab.adapters.CompareProductAdapter;
import com.wbct.etab.bean.CompareItem;
import com.wbct.etab.bean.StoreItem;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.utils.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by Siddhartha Maji on 1/9/2016.
 */
public class CompareActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener{
    private RecyclerView rvCompareProduct;
    private CompareProductAdapter compareProductAdapter;
    ArrayList<StoreItem> list;
    protected Vibrator vibrate;
    ArrayList<CompareItem> comparelist;
    ArrayList<Integer> itm,unselactableList;
    LinearLayoutManager layoutManager;
    private int ItemPosition,newposition;
    DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare_product_layout);
        initialize();
        displayMsg();
        loadList();

    }

    @Override
    protected void onStart() {
        super.onStart();
        rvCompareProduct.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    private void loadList() {
        compareProductAdapter =new CompareProductAdapter(comparelist,R.layout.compare_item_layout);
        rvCompareProduct.setAdapter(compareProductAdapter);
    }

    private void displayMsg() {
       /* Intent i = getIntent();
        itm = i.getIntegerArrayListExtra("selected_itemIndex"); //all touched Item position in Array... for future implementation..
        Log.e("Array>", itm.toString());
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data"); // Retriving Serizable Data.. :-) Sid.
        list = dw.getStoreItems();
        for(int j=0;j<itm.size();j++)
        {
            CompareItem compareItem = new CompareItem();
            compareItem.setColor(list.get(itm.get(j)).getColor());
            compareItem.setPrice(list.get(itm.get(j)).getPrice());
            compareItem.setProductCode(list.get(itm.get(j)).getProductCode());
            compareItem.setImageUrl(list.get(itm.get(j)).getImageUrl());
            comparelist.add(compareItem);
        }*/

        dbAdapter.open();
        for(int j =0;j<dbAdapter.CompareDataCount();j++) {
            CompareItem compareItem = new CompareItem();
            compareItem.setColor(dbAdapter.getCompareItembyColIndex(6).get(j));
            compareItem.setPrice(dbAdapter.getCompareItembyColIndex(3).get(j));
            compareItem.setProductCode(dbAdapter.getCompareItembyColIndex(5).get(j));
            compareItem.setDescription(dbAdapter.getCompareItembyColIndex(8).get(j));
            compareItem.setImageUrl(dbAdapter.getCompareItembyColIndex(4).get(j));
            compareItem.setVideoUrl(dbAdapter.getCompareItembyColIndex(7).get(j));
            compareItem.setStoreId(dbAdapter.getCompareItembyColIndex(9).get(j));
            comparelist.add(compareItem);

        }
        dbAdapter.close();



    }

    private void initialize() {
        itm = new ArrayList<>();
        comparelist = new ArrayList<>();
        rvCompareProduct = (RecyclerView)findViewById(R.id.rvCompareProduct);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCompareProduct.setLayoutManager(layoutManager);
        unselactableList = new ArrayList<>();
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        dbAdapter = new DbAdapter(this);
    }

    public void HomeClick(View view)
    {
        Intent i = new Intent(getBaseContext(),MenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    public void OnFbClick(View view)
    {
        Intent i = new Intent(this,FacebookActivity.class);
        startActivity(i);
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent i = new Intent(CompareActivity.this,ProductDetailsActivity.class);

        i.putExtra("img_price", comparelist.get(position).getPrice());
        i.putExtra("img_color", comparelist.get(position).getColor());
        i.putExtra("img_pricep_code", comparelist.get(position).getProductCode());
        i.putExtra("img_url", comparelist.get(position).getImageUrl());
        i.putExtra("img_product_description", comparelist.get(position).getDescription());
        i.putExtra("img_product_video", comparelist.get(position).getVideoUrl());
        i.putExtra("storeId", comparelist.get(position).getStoreId());

        startActivity(i);

    }

    @Override
    public void onItemLongPress(View childView, int position) {



       /* compareProductAdapter.notifyItemRemoved(position);
            String po = String.valueOf(itm.get(position));
            ItemPosition = Integer.parseInt(po);
            unselactableList.add(ItemPosition);
            itm.remove(position);
            comparelist.remove(position);*/

        dbAdapter.open();

        int code = Integer.parseInt(dbAdapter.getCompareItembyColIndex(0).get(position));      //Get Product Code for Delete the product containing That code. Siddhartha maji.
        dbAdapter.deleteCompareDataByPosition(code);
        compareProductAdapter.notifyItemRemoved(position);
        comparelist.remove(position);
        dbAdapter.close();
            vibrate.vibrate(190);


    }

    @Override
    public void onBackPressed() {

        sendStoreUnselect(unselactableList);
        Log.e("Item_position:", unselactableList.toString());
        super.onBackPressed();



    }

    private void sendStoreUnselect(ArrayList<Integer> ul) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", ul);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void clkClearAll(View view)
    {
        showAleartForDelete().show();

    }

    private AlertDialog showAleartForDelete() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This will delete the products selected by you.");
        builder.setMessage("Do you want to delete them? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbAdapter.open();
                        dbAdapter.deleteallComp();
                        dbAdapter.close();

                        rvCompareProduct.animate().translationX(-rvCompareProduct.getWidth());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                finish();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

