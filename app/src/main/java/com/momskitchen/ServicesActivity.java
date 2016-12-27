package com.momskitchen;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyProgressDialog;
import com.utils.ProductList_Adapter;
import com.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicesActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<HashMap<String, String>> array;
    AppPreferences mPrefs;
    ProductList_Adapter adapter;
    GridView grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        mPrefs = AppPreferences.getAppPreferences(ServicesActivity.this);
        grid = (GridView) findViewById(R.id.main_grid);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ic_cart).setOnClickListener(this);
        findViewById(R.id.ic_search).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("" + App_Constant.Title);
        ((TextView) findViewById(R.id.tv_marque)).setText("" + App_Constant.Advt_title);
        ((GridView) findViewById(R.id.main_grid)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (array != null) {
                    startActivity(new Intent(ServicesActivity.this, ServiceDetailActivity.class)
                            .putExtra("ID", array.get(position).get("id"))
                            .putExtra("NAME", array.get(position).get("name"))
                            .putExtra("IMAGE", array.get(position).get("image")));
                }
            }
        });

        load_view();

    }



    public void changeUI() {
        if (((RelativeLayout) findViewById(R.id.relative_search)).isShown()) {
            ((RelativeLayout) findViewById(R.id.relative_search)).setVisibility(View.GONE);
        } else {
            ((RelativeLayout) findViewById(R.id.relative_search)).setVisibility(View.VISIBLE);
        }
    }


    private void load_view() {
        if (Utility.isNetworkConnected(ServicesActivity.this)) {
            ServicesActivity.Load_Brand lp = new ServicesActivity.Load_Brand();
            lp.execute();
        } else {
            Toast.makeText(ServicesActivity.this, "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cart_batch();
    }

    public void cart_batch() {

        DBHandler db = new DBHandler(ServicesActivity.this);
        if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {

        } else {
            String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            List<MyCart_property> shops = db.getAllProduct(U_id);
            if (shops.size() > 0) {
                ((TextView) findViewById(R.id.cart_count)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.cart_count)).setText("" + shops.size());
            } else {
                ((TextView) findViewById(R.id.cart_count)).setVisibility(View.GONE);
            }
        }
    }

    class Load_Brand extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(ServicesActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                res = Utility.GetRequest(ServicesActivity.this, App_Constant.SERVICES);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(ServicesActivity.this);
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
                    adapter = new ProductList_Adapter(ServicesActivity.this, array);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ic_cart:
                DBHandler db = new DBHandler(ServicesActivity.this);
                String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                if(U_id.equalsIgnoreCase("")){
                    Toast.makeText(ServicesActivity.this,"You are not Login User",Toast.LENGTH_SHORT).show();
                }else {
                    List<MyCart_property> shops = db.getAllProduct(U_id);
                    if (shops.size() > 0) {
                        startActivity(new Intent(ServicesActivity.this, My_Cart.class));
                    } else {
                        Toast.makeText(ServicesActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.ic_search:
                changeUI();
                break;
        }
    }
}
