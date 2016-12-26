package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Adapter.BrandAdapter;
import com.Vo.MyCart_property;
import com.Vo.brandVo;
import com.bumptech.glide.Glide;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saloni.bhansali on 8/28/2016.
 */

public class brandActivity extends AppCompatActivity implements View.OnClickListener
{
    private RecyclerView recyclerView;
    private List<brandVo> brandList;
    private BrandAdapter orderAdapter;
    Context context = brandActivity.this;
    TextView product_name,title;
    ImageView productImg;
    AppPreferences mPrefs;
    private String ProductId,img,name;
    private  SearchTask searchTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brand_activity);
        mPrefs = AppPreferences.getAppPreferences(brandActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        brandList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        orderAdapter = new BrandAdapter(brandList, this, "");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderAdapter);
        title=(TextView)findViewById(R.id.tv_title);
        ((TextView) findViewById(R.id.tv_marque)).setText("" + App_Constant.Advt_title);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ic_cart).setOnClickListener(this);
        findViewById(R.id.ic_search).setOnClickListener(this);
        product_name = (TextView) findViewById(R.id.name_txtview);
        productImg = (ImageView) findViewById(R.id.img_product);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ProductId = bundle.getString("ID");
           // img = bundle.getString("IMAGE");
            name = bundle.getString("NAME");
            product_name.setText(name);
            title.setText(name);
            /*Glide.with(context).load(img).asBitmap()
                    .placeholder(R.mipmap.app_icon).fitCenter().into(productImg);*/
        }

        new BrandTask().execute(""+ProductId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ic_cart:
                DBHandler db = new DBHandler(brandActivity.this);
                String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                if(U_id.equalsIgnoreCase("")){
                    Toast.makeText(brandActivity.this,"You are not Login User",Toast.LENGTH_SHORT).show();
                }else {
                    List<MyCart_property> shops = db.getAllProduct(U_id);
                    if (shops.size() > 0) {
                        startActivity(new Intent(brandActivity.this, My_Cart.class));
                    } else {
                        Toast.makeText(brandActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
                // start
                break;
            case R.id.ic_search:
                changeUI();
                    //startActivity(new Intent(getApplicationContext(), brandActivity.class));

                break;
        }

    }
    public void changeUI() {
     /*   findViewById(R.id.linear_img).setVisibility(View.GONE);
        findViewById(R.id.recycle_view).setVisibility(View.GONE);*/
        if (((RelativeLayout) findViewById(R.id.relative_search)).isShown()) {
            ((RelativeLayout) findViewById(R.id.relative_search)).setVisibility(View.GONE);
        } else {
            ((RelativeLayout) findViewById(R.id.relative_search)).setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.edt_search)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(count>=3) {
                        String txt = ((EditText) findViewById(R.id.edt_search)).getText().toString().trim();

                        searchTask = new SearchTask();
                        searchTask.execute(txt);
                    }
                   

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }
    }
    class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(brandActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {

                res = Utility.GetRequest(brandActivity.this, App_Constant.SEARCH + params[0]);
                Log.e("asa", "" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(brandActivity.this);
            try {
                JSONObject object = new JSONObject(res);
                if (object.getString("Msg").equalsIgnoreCase("Success")) {
                    // Toast.makeText(brandActivity.this ,object.getString("message"), Toast.LENGTH_LONG).show();
                    JSONArray bundledata = object.getJSONArray("SuccesModels");
                    if (bundledata.length() > 0) {
                        brandList.clear();
                        for (int i = 0; i < bundledata.length(); i++) {
                            brandVo bundleHistory = new brandVo();
                            bundleHistory.id = ((JSONObject) bundledata.get(i)).getInt("BrandId");
                            bundleHistory.name = ((JSONObject) bundledata.get(i)).getString("brandName");
                            bundleHistory.price = ((JSONObject) bundledata.get(i)).getString("Rate");
                            bundleHistory.thruml = ((JSONObject) bundledata.get(i)).getString("ImageName");

                            findViewById(R.id.recycle_view).setVisibility(View.VISIBLE);
                            brandList.add(bundleHistory);

                        }

                        orderAdapter.notifyDataSetChanged();


                    } else {

                        findViewById(R.id.linear_brand).setVisibility(View.GONE);
                        findViewById(R.id.layout_no_collection).setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_add_new).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(brandActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {

        super.onResume();

        cart_batch();

    }
    public void cart_batch() {

        DBHandler db = new DBHandler(brandActivity.this);
        if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {

        } else {
            String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            List<MyCart_property> shops = db.getAllProduct(U_id);
            if (shops.size() > 0) {
                ((TextView)findViewById(R.id.cart_count)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.cart_count)).setText("" + shops.size());
            } else {
                ((TextView)findViewById(R.id.cart_count)).setVisibility(View.GONE);
            }
        }
    }

    class BrandTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.show(brandActivity.this);
            }

            @Override
            protected String doInBackground(String... params) {
                String res = null;
                try {

                    res = Utility.GetRequest(brandActivity.this, App_Constant.BRAND+params[0]);
                    Log.e("asa", "" + res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }

            protected void onPostExecute(String res) {
                MyProgressDialog.close(brandActivity.this);
                try {
                    JSONObject object = new JSONObject(res);
                    if (object.getString("Msg").equalsIgnoreCase("Success")) {
                        // Toast.makeText(brandActivity.this ,object.getString("message"), Toast.LENGTH_LONG).show();
                        JSONArray bundledata = object.getJSONArray("SuccesModels");
                        if (bundledata.length() > 0) {
                            brandList.clear();
                            for (int i = 0; i < bundledata.length(); i++) {
                                brandVo bundleHistory = new brandVo();
                                bundleHistory.id = ((JSONObject) bundledata.get(i)).getInt("BrandId");
                                bundleHistory.name = ((JSONObject) bundledata.get(i)).getString("brandName");
                                /*String rat=((JSONObject) bundledata.get(i)).getString("Rate");
                                BigDecimal.valueOf(Double.parseDouble(rat));
                                Log.e("valueRate",rat);*/
                                bundleHistory.price = ((JSONObject) bundledata.get(i)).getString("Rate");
                                bundleHistory.thruml = ((JSONObject) bundledata.get(i)).getString("ImageName");

                                brandList.add(bundleHistory);

                            }

                            orderAdapter.notifyDataSetChanged();
                            brandVo productlist = brandList.get(0);
                            Glide.with(context).load(productlist.thruml).asBitmap()
                                    .placeholder(R.mipmap.default_icon).fitCenter().into(productImg);
                        } else {

                            findViewById(R.id.linear_brand).setVisibility(View.GONE);
                            findViewById(R.id.layout_no_collection).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_add_new).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
                        }
                    }
                    else {
                        Toast.makeText(brandActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }



