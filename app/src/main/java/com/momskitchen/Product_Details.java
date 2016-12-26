package com.momskitchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Product_Details extends Activity implements View.OnClickListener {

    private ArrayList<HashMap<String, String>> array;
    Context context = Product_Details.this;
    int quantit = 0;
    int ProductId;
    AppPreferences mPrefs;
    DBHandler db;
    Button btn_later;
    TextView text_time;
    CheckBox regular, spice;
    private ArrayList<String> rgArray;
    int Time_Flag;
    RadioGroup rg;
    ScrollView scroll;
    public static String regularN = "", spiceN = "", subFoodId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_details);
        mPrefs = AppPreferences.getAppPreferences(context);
        db = new DBHandler(Product_Details.this);
        findViewById(R.id.btn_add_cart).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ic_cart)).setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ProductId = bundle.getInt("ID");
        }
        text_time = (TextView) findViewById(R.id.txt_no_collection);
        btn_later = (Button) findViewById(R.id.btn_add_new);
        regular = (CheckBox) findViewById(R.id.rb_regular);
        spice = (CheckBox) findViewById(R.id.rb_spice);
        rg = (RadioGroup) findViewById(R.id.rg_retailer);
        ((TextView) findViewById(R.id.tv_marque)).setText("" + App_Constant.Advt_title);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_decrement).setOnClickListener(this);
        findViewById(R.id.iv_increment).setOnClickListener(this);
        if (Utility.isNetworkConnected(context)) {
            new ProductDetailTask().execute(ProductId + "");
        } else {
            Toast.makeText(context, "No connection", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isTimeWithinInterval() {


        Date time_1,time_2;

        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance();
        try {
            //Lower limit
            if (Build.VERSION.SDK_INT >= 23){
                 time_1 = new SimpleDateFormat("hh:mm a").parse("10:00 a.m.");
            }else{
                 time_1 = new SimpleDateFormat("hh:mm a").parse("10:00 AM");
            }

            Calendar calendar_1 = Calendar.getInstance();
            calendar_1.setTime(time_1);

            //Upper limit
            if (Build.VERSION.SDK_INT >= 23){
                time_2 = new SimpleDateFormat("hh:mm a").parse("10:00 p.m.");
            }else{
                time_2 = new SimpleDateFormat("hh:mm a").parse("10:00 PM");
            }

            Calendar calendar_2 = Calendar.getInstance();
            calendar_2.setTime(time_2);

            // Current Time
            Date d = new SimpleDateFormat("hh:mm a").parse(dateFormat.format(cal.getTime()));
            Calendar calendar_3 = Calendar.getInstance();
            calendar_3.setTime(d);

            Date x = calendar_3.getTime();
            if (x.after(calendar_1.getTime()) && x.before(calendar_2.getTime())) {
                return true;
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.e("EXCEPTION",""+e);
        }

        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_cart:
                DBHandler db = new DBHandler(Product_Details.this);
                String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                if(U_id.equalsIgnoreCase("")){
                    Toast.makeText(Product_Details.this,"You are not Login User",Toast.LENGTH_SHORT).show();
                }else {
                    List<MyCart_property> shops = db.getAllProduct(U_id);
                    if (shops.size() > 0) {
                        if (Time_Flag == 0) {

                            startActivity(new Intent(Product_Details.this, My_Cart.class));
                        } else {
                            Toast.makeText(context, "Order Timing is 10 AM to 10 PM", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(Product_Details.this, "Your cart is empty", Toast.LENGTH_LONG).show();
                    }
                }
                // start
                break;
            case R.id.btn_add_cart:
                if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {
                    startActivity(new Intent(Product_Details.this, Login_Activity.class));
                    finishAffinity();
                } else {
                    if (isTimeWithinInterval()) {
                        Time_Flag = 0;
                        add_to_cart();
                    } else {
                        Time_Flag = 1;
                        findViewById(R.id.scroll).setVisibility(View.GONE);
                        findViewById(R.id.layout_no_collection).setVisibility(View.VISIBLE);
                        text_time.setText("Order Timing is 10 AM to 10 PM");
                        btn_later.setText("Please Try Later");
                        findViewById(R.id.btn_add_new).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(context, Home_Activity.class));
                            }
                        });
                    }
                }
                break;
            case R.id.iv_decrement:
                String realval = ((EditText) findViewById(R.id.edt_quanity)).getText().toString().trim();
                int real_vl = Integer.parseInt(realval);
                if (real_vl == quantit) {

                } else {
                    real_vl--;
                    ((EditText) findViewById(R.id.edt_quanity)).setText("" + real_vl);
                }
                break;
            case R.id.iv_increment:
                String realv = ((EditText) findViewById(R.id.edt_quanity)).getText().toString().trim();
                int real_v = Integer.parseInt(realv);

                real_v++;
                ((EditText) findViewById(R.id.edt_quanity)).setText("" + real_v);

                break;
            case R.id.iv_back:
                onBackPressed();
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (isTimeWithinInterval()) {
            cart_batch();
        }
    }

    public void add_to_cart() {

        String quantity = ((EditText) findViewById(R.id.edt_quanity)).getText().toString().trim();
        int current_qua = Integer.parseInt(quantity);
        if (current_qua >= 0) {
            int count = db.getCountProduct("" + array.get(0).get("ProductName"));
            Log.e("row _count : ", "" + count);
            if (count == 0) {
                Toast.makeText(Product_Details.this, "Product added.", Toast.LENGTH_SHORT).show();

                int i = rg.getCheckedRadioButtonId();

                if (array.get(0).get("FoodId").equalsIgnoreCase("2")) {

                } else {
                    if (i == R.id.rb1) {
                        subFoodId = rgArray.get(0);
                        Log.e("subFood", "" + subFoodId);
                        Log.e("array", "" + rgArray);
                    } else if (i == R.id.rb2) {
                        subFoodId = rgArray.get(1);
                        Log.e("subFood2", "" + subFoodId);
                        Log.e("array2", "" + rgArray);
                    } else if (i == R.id.rb3) {
                        subFoodId = rgArray.get(2);
                    } else if (i == R.id.rb4) {
                        subFoodId = rgArray.get(3);
                    } else if (i == R.id.rb5) {
                        subFoodId = rgArray.get(4);
                    }

                    if (regular.isChecked()) {
                        regularN = "yes";

                    } else {
                        regularN = "No";
                    }
                    if (spice.isChecked()) {
                        spiceN = "yes";

                    } else {
                        spiceN = "No";
                    }
                }
                String oderqut = ((EditText) findViewById(R.id.edt_quanity)).getText().toString();
                int oq = Integer.parseInt(oderqut);
                BigDecimal qytRate=new BigDecimal(array.get(0).get("TaxRate"));
                BigDecimal finalTax= qytRate.multiply(BigDecimal.valueOf(oq));
                Log.e("FinalTax",""+finalTax);
                db.addProduct(new MyCart_property
                        (
                                "" + array.get(0).get("ProductName"),
                                "" + array.get(0).get("cid"),
                                "" + array.get(0).get("id"),
                                "" + array.get(0).get("Description"),
                                "" + array.get(0).get("Rate"),
                                "" + oq,
                                "" + array.get(0).get("Quantity"),
                                "" + "fgfg",
                                "" + array.get(0).get("ImageName"),
                                "" + array.get(0).get("pid"),
                                "" + mPrefs.getStringValue(AppPreferences.MEMBER_ID),
                                "" + "gdgd",
                                "" + array.get(0).get("TaxId"),
                                "" + finalTax,

                                "" + array.get(0).get("FoodId"),
                                "" + subFoodId,
                                "" + regularN,
                                "" + spiceN,
                                ""+ Home_Category_Fragment.typeValue
                                ));
                Log.e("tax",""+array.get(0).get("TaxRate"));
                Log.e("values : ", "" + subFoodId + "  :  " + regularN + " : " + spiceN);

                db.close();
                subFoodId="";
                regularN="";
                spiceN="";
            } else {
                db.close();
                Toast.makeText(Product_Details.this, "Product already exist in cart", Toast.LENGTH_SHORT).show();
            }
            cart_batch();
        } else {
            Toast.makeText(Product_Details.this, "Minimum quantity should be " + getIntent().getExtras().getString("quant"), Toast.LENGTH_SHORT).show();
        }

    }

    public void cart_batch() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DBHandler db = new DBHandler(Product_Details.this);
        if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {

        } else {
            String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            List<MyCart_property> shops = db.getAllProduct(U_id);
            if (shops.size() > 0) {
                ((TextView) toolbar.findViewById(R.id.cart_count)).setVisibility(View.VISIBLE);
                ((TextView) toolbar.findViewById(R.id.cart_count)).setText("" + shops.size());
            } else {
                ((TextView) toolbar.findViewById(R.id.cart_count)).setVisibility(View.GONE);
            }
        }
    }

    class ProductDetailTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(Product_Details.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {

                res = Utility.GetRequest(Product_Details.this, App_Constant.PRODUCT_DETAIL + params[0]);
                Log.e("asa", "" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(Product_Details.this);
            try {
                JSONObject object = new JSONObject(res);
                array = new ArrayList<HashMap<String, String>>();
                if (object.getString("Msg").equalsIgnoreCase("Success")) {
                    scroll = (ScrollView) findViewById(R.id.scroll);
                    scroll.setVisibility(View.VISIBLE);
                    JSONArray bundledata = object.getJSONArray("SuccesModels");
                    for (int i = 0; i < bundledata.length(); i++) {
                        JSONObject obj = bundledata.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("pid", obj.getString("ProductId"));
                        map.put("cid", obj.getString("CategoryId"));
                        map.put("id", obj.getString("BrandId"));
                        map.put("ProductName", obj.getString("ProductName"));
                        map.put("Rate", obj.getString("Rate"));
                        map.put("Quantity", obj.getString("Quantity"));
                        map.put("FoodName", obj.getString("FoodName"));
                        map.put("Description", obj.getString("Description"));
                        map.put("ImageName", obj.getString("ImageName"));
                        map.put("RegularN", obj.getString("RegularN"));
                        map.put("SpicesN", obj.getString("SpicesN"));
                        map.put("Description", obj.getString("Description"));
                        map.put("FoodId", obj.getString("FoodId"));
                        map.put("TaxId", obj.getString("TaxId"));
                        map.put("TaxRate", obj.getString("TaxRate"));
                        map.put("ProductLists", "" + obj.getJSONArray("ProductLists"));

                        array.add(map);
                    }
                    Log.e("size", "" + array);
                    if (array.size() > 0) {

                        HashMap<String, String> map = new HashMap<String, String>();
                        map = array.get(0);
                        ((CheckBox) findViewById(R.id.rb_spice)).setText(map.get("SpicesN"));
                        ((CheckBox) findViewById(R.id.rb_regular)).setText(map.get("RegularN"));
                        ((TextView) findViewById(R.id.r)).setText(map.get("FoodName"));
                        ((TextView) findViewById(R.id.pname)).setText(map.get("ProductName"));
                        ((TextView) findViewById(R.id.pRate)).setText(map.get("Rate"));
                        Glide.with(Product_Details.this).load(map.get("ImageName")).asBitmap().placeholder(R.mipmap.app_icon).fitCenter().into(new BitmapImageViewTarget(((ImageView) findViewById(R.id.product_image))) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                ((ImageView) findViewById(R.id.product_image)).setImageBitmap(resource);
                            }
                        });
                        if (map.get("FoodId").equalsIgnoreCase("2")) {
                            ((RelativeLayout) findViewById(R.id.rlative_detail)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.detail_txtview)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.desc)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.desc_detail)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.desc_detail)).setText(map.get("Description"));
                        }

                        ((EditText) findViewById(R.id.edt_quanity)).setText(map.get("Quantity"));

                        String qua = map.get("Quantity");
                        float v = Float.parseFloat(qua);
                        quantit = (int) v;

                        ((EditText) findViewById(R.id.edt_quanity)).setText("" + quantit);
                        String list = map.get("ProductLists");
                        JSONArray arr = new JSONArray(list);
                        rgArray = new ArrayList<String>();
                        if (arr.length() == 1) {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.GONE);
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                ((RadioButton) findViewById(R.id.rb1)).setText(obj.getString("ProductListName"));
                                rgArray.add(obj.getString("ProductListId"));
                            }
                        } else if (arr.length() == 2) {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.GONE);

                            ((RadioButton) findViewById(R.id.rb1)).setText(arr.getJSONObject(0).getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb2)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            rgArray.add(arr.getJSONObject(0).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(1).getString("ProductListId"));
                        } else if (arr.length() == 3) {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.GONE);

                            ((RadioButton) findViewById(R.id.rb1)).setText(arr.getJSONObject(0).getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb2)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb3)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            rgArray.add(arr.getJSONObject(0).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(1).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(2).getString("ProductListId"));

                        } else if (arr.length() == 4) {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.GONE);

                            ((RadioButton) findViewById(R.id.rb1)).setText(arr.getJSONObject(0).getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb2)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb3)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb4)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            rgArray.add(arr.getJSONObject(0).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(1).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(2).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(3).getString("ProductListId"));

                        } else if (arr.length() == 5) {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.VISIBLE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.VISIBLE);

                            ((RadioButton) findViewById(R.id.rb1)).setText(arr.getJSONObject(0).getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb2)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb3)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb4)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            ((RadioButton) findViewById(R.id.rb5)).setText(arr.getJSONObject(1).getString("ProductListName"));//obj.getString("ProductListName"));
                            rgArray.add(arr.getJSONObject(0).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(1).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(2).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(3).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(4).getString("ProductListId"));
                            rgArray.add(arr.getJSONObject(5).getString("ProductListId"));
                        } else {
                            ((RadioButton) findViewById(R.id.rb1)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb2)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb3)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb4)).setVisibility(View.GONE);
                            ((RadioButton) findViewById(R.id.rb5)).setVisibility(View.GONE);
                        }
                    } else {
                        findViewById(R.id.scroll).setVisibility(View.GONE);
                        findViewById(R.id.layout_no_collection).setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_add_new).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });

                    }
                } else {
                    findViewById(R.id.scroll).setVisibility(View.GONE);
                    findViewById(R.id.layout_no_collection).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_add_new).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    Toast.makeText(Product_Details.this, object.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
