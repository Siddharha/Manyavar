package com.wbct.etab.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wbct.etab.R;
import com.wbct.etab.adapters.SearchItemAdapter;
import com.wbct.etab.bean.SearchItem;
import com.wbct.etab.dbpackage.DbAdapter;
import com.wbct.etab.utils.RecyclerItemClickListener;

import java.util.ArrayList;

/**
 * Created by Siddhartha Maji on 1/19/2016.
 */
public class SearchActivity extends AppCompatActivity
        implements RecyclerItemClickListener.OnItemClickListener{

    private EditText etSearch;
     RecyclerView rvSearchStoreItems;
     SearchItemAdapter searchItemAdapter;
     ArrayList<SearchItem> searchArray;
    LinearLayoutManager layoutManager;
    ProgressDialog progressDialog;
    DbAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_store_layout);
        initialize();
        // new MySearchAsync().execute();
        loadList();
        onClick();
    }

    private void loadList() {

    }

    private void getSearchResult(String searchItem) {
        dbAdapter.open();
        searchArray = new ArrayList<>();
        searchArray = dbAdapter.getSearchResult(searchItem);
        Log.e("search item count", searchArray.size() + "");
        searchItemAdapter = new SearchItemAdapter(searchArray,
                R.layout.search_item_layout);
        rvSearchStoreItems.setAdapter(searchItemAdapter);
        dbAdapter.close();
    }

    private void initialize() {
        dbAdapter = new DbAdapter(this);
        etSearch = (EditText)findViewById(R.id.etSearch);
        rvSearchStoreItems = (RecyclerView)findViewById(R.id.rvSearchStoreItems);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSearchStoreItems.setLayoutManager(layoutManager);
    }

    private void onClick() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(textView);
                  /*  if (!textView.getText().toString().equals("")) {
                        getSearchResult(textView.getText().toString());
                    } else {
                        Toast.makeText(SearchActivity.this, "Please enter search item", Toast.LENGTH_LONG).show();
                    }*/
                    return true;
                }
                return false;
            }
        });
    }

    public void clkSearch(View view)
    {
        performSearch(etSearch);
    }



    private void performSearch(TextView v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        //searchItemAdapter.getFilter().filter(v.getText());
        dbAdapter.open();
        searchArray = dbAdapter.getSearchResult(v.getText().toString());
        searchItemAdapter = new SearchItemAdapter(searchArray,
                R.layout.search_item_layout);
        rvSearchStoreItems.setAdapter(searchItemAdapter);
            Log.e("SEARCH_RESULT: ", searchArray.toString());

        if(searchArray.isEmpty()) {
            aleartForNoResult().show();
        }

        dbAdapter.close();
    }

    private AlertDialog aleartForNoResult() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Search Items Available!");
        builder.setMessage("Do you want to search it again?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                       finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

    public void HomeClick(View view) {
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
    protected void onStart() {
        super.onStart();
        rvSearchStoreItems.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent i = new Intent(SearchActivity.this,ProductDetailsActivity.class);
        i.putExtra("img_price", searchItemAdapter.items.get(position).getPrice());
        i.putExtra("img_color", searchItemAdapter.items.get(position).getColor());
        i.putExtra("img_pricep_code",searchItemAdapter.items.get(position).getProductCode());
        i.putExtra("img_url",searchItemAdapter.items.get(position).getImageUrl());
        i.putExtra("img_product_description",searchItemAdapter.items.get(position).getDescription());
        i.putExtra("img_product_video", searchItemAdapter.items.get(position).getVideoUrl());
        i.putExtra("storeId", searchItemAdapter.items.get(position).getStoreId());
        startActivity(i);

        Log.e("-->",searchArray.get(position).getColor());
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }


}
