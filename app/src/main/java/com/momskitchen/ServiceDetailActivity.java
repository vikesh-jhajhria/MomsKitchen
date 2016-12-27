package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Adapter.BrandAdapter;
import com.Adapter.ServiceDetail_Adapter;
import com.Vo.MyCart_property;
import com.Vo.brandVo;
import com.bumptech.glide.Glide;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyProgressDialog;
import com.utils.ProductList_Adapter;
import com.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceDetailActivity extends AppCompatActivity implements View.OnClickListener
{
    private GridView grid;
    ArrayList<HashMap<String, String>> array;
    private ServiceDetail_Adapter adapter;
    Context context = ServiceDetailActivity.this;
    TextView product_name,title;
    ImageView productImg;
    AppPreferences mPrefs;
    private String ProductId,img,name;
    private ServiceDetailActivity.SearchTask searchTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        mPrefs = AppPreferences.getAppPreferences(ServiceDetailActivity.this);
        grid = (GridView) findViewById(R.id.main_grid);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new ServiceDetail_Adapter( this, array);
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

        new ServiceDetailActivity.BrandTask().execute(""+ProductId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ic_cart:
                DBHandler db = new DBHandler(ServiceDetailActivity.this);
                String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                if(U_id.equalsIgnoreCase("")){
                    Toast.makeText(ServiceDetailActivity.this,"You are not Login User",Toast.LENGTH_SHORT).show();
                }else {
                    List<MyCart_property> shops = db.getAllProduct(U_id);
                    if (shops.size() > 0) {
                        startActivity(new Intent(ServiceDetailActivity.this, My_Cart.class));
                    } else {
                        Toast.makeText(ServiceDetailActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
                // start
                break;
            case R.id.ic_search:
                changeUI();
                //startActivity(new Intent(getApplicationContext(), ServiceDetailActivity.class));

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

                        searchTask = new ServiceDetailActivity.SearchTask();
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
            MyProgressDialog.show(ServiceDetailActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {

                res = Utility.GetRequest(ServiceDetailActivity.this, App_Constant.SEARCH + params[0]);
                Log.e("asa", "" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(ServiceDetailActivity.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    array = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", mock_object.getString("ServicesId"));
                        map.put("name", mock_object.getString("ServicesName"));
                        map.put("image", mock_object.getString("ImageName"));
                        array.add(map);

                    }
                    // Log.e("dsa ",""+product_array.size());
                    //.setAdapter(new ProductList_Adapter(ServicesActivity.this, array));
                    adapter = new ServiceDetail_Adapter(ServiceDetailActivity.this, array);
                    grid.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    ((EditText) findViewById(R.id.edt_search)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // String txt = ((EditText)view.findViewById(R.id.edt_search)).getText().toString().trim();
                            adapter.getFilter().filter(s.toString());

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                }
            } catch (Exception e) {
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

        DBHandler db = new DBHandler(ServiceDetailActivity.this);
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
            MyProgressDialog.show(ServiceDetailActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {

                res = Utility.GetRequest(ServiceDetailActivity.this, App_Constant.SERVICES_BY_ID+"?id="+params[0]);
                Log.e("asa", "" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(ServiceDetailActivity.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    array = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", mock_object.getString("ServicesId"));
                        map.put("name", mock_object.getString("ServicesName"));
                        map.put("image", mock_object.getString("ImageName"));
                        array.add(map);

                    }
                    // Log.e("dsa ",""+product_array.size());
                    //.setAdapter(new ProductList_Adapter(ServicesActivity.this, array));
                    adapter = new ServiceDetail_Adapter(ServiceDetailActivity.this, array);
                    grid.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    ((EditText) findViewById(R.id.edt_search)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // String txt = ((EditText)view.findViewById(R.id.edt_search)).getText().toString().trim();
                            adapter.getFilter().filter(s.toString());

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}