package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SearchActivity extends AppCompatActivity implements View.OnClickListener
{
    private RecyclerView recyclerView;
    private List<brandVo> brandList;
    private BrandAdapter orderAdapter;
    Context context = SearchActivity.this;
    String search = "";
    AppPreferences mPrefs;
    private String ProductId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mPrefs = AppPreferences.getAppPreferences(SearchActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.search_recycle_view);
        brandList = new ArrayList<>();


        findViewById(R.id.img_search).setOnClickListener(this);
        findViewById(R.id.txt_cancel).setOnClickListener(this);
        orderAdapter = new BrandAdapter(brandList, this, "");
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderAdapter);
        ((EditText) findViewById(R.id.txt_search)).addTextChangedListener(new MySearchValidation(((EditText) findViewById(R.id.txt_search))));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ProductId = bundle.getString("ID");

        }
        ((EditText) findViewById(R.id.txt_search)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    if (Utility.isNetworkConnected(SearchActivity.this))
                        new BrandTask().execute(""+ProductId);

                    return true;

                }
                return false;
            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_search:
                search = ((EditText) findViewById(R.id.txt_search)).getText().toString().trim();
                if (!search.isEmpty()) {
                    ((EditText) findViewById(R.id.txt_search)).setText("");
                    brandList.clear();
                }
                break;
            case R.id.txt_cancel:
                finish();
                break;
        }

    }




    class BrandTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MyProgressDialog.show(SearchActivity.this);
            }

            @Override
            protected String doInBackground(String... params) {
                String res = null;
                try {

                    res = Utility.GetRequest(SearchActivity.this, App_Constant.SEARCH);
                    Log.e("asa", "" + res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return res;
            }

            protected void onPostExecute(String res) {
                MyProgressDialog.close(SearchActivity.this);
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
                        Toast.makeText(SearchActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    class MySearchValidation implements TextWatcher {
        private View view;

        private MySearchValidation(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.txt_search:
                    search = ((EditText) findViewById(R.id.txt_search)).getText().toString().trim();
                    if (search.isEmpty()) {
                        ((ImageView) findViewById(R.id.img_search)).setImageResource(R.mipmap.icn_search);
                        //searchRecycler.setVisibility(View.GONE);
                        brandList.clear();

                    } else {
                        ((ImageView) findViewById(R.id.img_search)).setImageResource(R.drawable.close_icon);
                        //searchRecycler.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    }



