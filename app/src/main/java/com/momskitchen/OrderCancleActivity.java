package com.momskitchen;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.Adapter.CartAdapter;
import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by saloni.bhansali on 12/23/2016.
 */

public class OrderCancleActivity extends Activity implements View.OnClickListener {
    Context context = OrderCancleActivity.this;
    AppPreferences mPrefs;
    ArrayList<MyCart_property> list;
    RecyclerView recycler_view;
    CartAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordercancle_activity);
        ((TextView) findViewById(R.id.tv_title_txt)).setText("" + App_Constant.Title);
        list = new ArrayList<>();
        recycler_view = (RecyclerView) findViewById(R.id.recycle_view);
        adapter = new CartAdapter(this, list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setAdapter(adapter);


        findViewById(R.id.iv_back).setOnClickListener(this);
        mPrefs = AppPreferences.getAppPreferences(OrderCancleActivity.this);

        new GetOrderData().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    class GetOrderData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(OrderCancleActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                res = Utility.GetRequest(OrderCancleActivity.this, App_Constant.CARD);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        MyCart_property map = new MyCart_property();
                        map.setOrder_qty(mock_object.getString("OrderQty"));
                        map.setImage(mock_object.getString("ImageName"));
                        map.setP_rate(mock_object.getString("Rate"));
                        map.setP_name(mock_object.getString("ProductName"));
                        map.setP_id(mock_object.getString("ProductId"));
                        map.setOrderId(mock_object.getInt("OrderId"));
                        map.setClientId(mock_object.getInt("ClientId"));

                        list.add(map);
                    }
                    adapter.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyProgressDialog.close(OrderCancleActivity.this);
        }
    }

}
