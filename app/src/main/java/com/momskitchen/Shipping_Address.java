package com.momskitchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shipping_Address extends Activity implements View.OnClickListener {
    Context context = Shipping_Address.this;
    AppPreferences mPrefs;
    List<String> list, list_charge, area_list;
    static ArrayList<HashMap<String, String>> array;
    Spinner spinner;
  //  static double charge = 0.0;
  static BigDecimal charge = new BigDecimal("0.00");

    static int flag = 0;
    static boolean ship_true = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shipping__address);
        mPrefs = AppPreferences.getAppPreferences(Shipping_Address.this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.edt_area);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ic_cart).setOnClickListener(this);
        list = new ArrayList<String>();
       // area_list = new ArrayList<String>();
        list_charge = new ArrayList<String>();
       // new Area().execute();
       /* spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (list.size() > 0) {
                    int areaId = ((Spinner) findViewById(R.id.edt_area)).getSelectedItemPosition();
                    charge = new BigDecimal(list_charge.get(areaId));
                    flag = 1;
                    mPrefs.putStringValue(AppPreferences.FlAG, "" + flag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ic_cart:
                startActivity(new Intent(Shipping_Address.this, My_Cart.class));
                // start
                break;
            case R.id.btn_next:
                if (Utility.isNetworkConnected(context)) {
                    if (((EditText) findViewById(R.id.edt_name)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Shipping_Address.this, "Please enter Name", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_name)).requestFocus();
                    } else if (((EditText) findViewById(R.id.edt_address1)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Shipping_Address.this, "Please enter address", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_address1)).requestFocus();
                    } else if (((EditText) findViewById(R.id.edt_address2)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Shipping_Address.this, "Please enter address second", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_address2)).requestFocus();
                    } else if (((EditText) findViewById(R.id.edt_landmark)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Shipping_Address.this, "Please enter landmark", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_landmark)).requestFocus();

                    } else if (((EditText) findViewById(R.id.edt_contactNo)).getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(Shipping_Address.this, "Please enter contact number", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_contactNo)).requestFocus();

                    } else if (((EditText) findViewById(R.id.edt_contactNo)).getText().toString().trim().length() != 10) {
                        Toast.makeText(Shipping_Address.this, "Please enter valid contact number.", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_contactNo)).requestFocus();
                    } else {
                        String username = ((EditText) findViewById(R.id.edt_name)).getText().toString().trim();
                        String adrs1 = ((EditText) findViewById(R.id.edt_address1)).getText().toString().trim();
                        String adrs2 = ((EditText) findViewById(R.id.edt_address2)).getText().toString().trim();
                        String landmark = ((EditText) findViewById(R.id.edt_landmark)).getText().toString().trim();
                        String contactNo = ((EditText) findViewById(R.id.edt_contactNo)).getText().toString().trim();
                     //   int areaId = ((Spinner) findViewById(R.id.edt_area)).getSelectedItemPosition();
//                        String aID = area_list.get(areaId);
                        String uID = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("TransactionId", 100);
                            obj.put("ClientId", uID);
                            obj.put("Address", adrs1);
                            obj.put("AddressII", adrs2);
                            obj.put("LandMark", landmark);
                           // obj.put("AreaId", aID);
                            mPrefs.putStringValue(AppPreferences.CONTACT_NUMBER, "" +contactNo);
                            obj.put("ContactNo", contactNo);
                            obj.put("CompanyId", 1);
                           /* {"Msg":"Success","value":"200","SuccesModels":[{"TransactionId":100,"ClientId":1,
                                    "Address":"B road","AddressII":"B Road","LandMark":"App Classs",
                                    "AreaId":0,"ContactNo":"941395742","CompanyId":1}]}*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("hhgh", "" + obj);

                        new shipping_Adress().execute("" + obj);

                    }
                } else {
                    Toast.makeText(Shipping_Address.this, "No connection", Toast.LENGTH_SHORT).show();

                }


                break;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        cart_batch();

    }

    public void cart_batch() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DBHandler db = new DBHandler(Shipping_Address.this);
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

    class Area extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(Shipping_Address.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {


                res = Utility.GetRequest(Shipping_Address.this, App_Constant.AREA);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(Shipping_Address.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    //Toast.makeText(Shipping_Address.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                    array = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", mock_object.getString("AreaId"));
                        map.put("AreaName", mock_object.getString("AreaName"));
                        map.put("Charge", mock_object.getString("Charge"));
                        list.add(mock_object.getString("AreaName"));
                        area_list.add(mock_object.getString("AreaId"));
                        list_charge.add(mock_object.getString("Charge"));

                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                            (Shipping_Address.this, android.R.layout.simple_spinner_item, list);

                    dataAdapter.setDropDownViewResource
                            (android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(dataAdapter);


                } else {
                    Toast.makeText(Shipping_Address.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class shipping_Adress extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(Shipping_Address.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", params[0]));
                res = Utility.getJson(nameValuePairs, Shipping_Address.this, App_Constant.SHIPPING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(Shipping_Address.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {

                    String val = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(0).getString("IsVerify");
                    mPrefs.putStringValue(AppPreferences.IsVerify, "" + val);
                    Toast.makeText(Shipping_Address.this,new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                    flag = 1;
                    mPrefs.putStringValue(AppPreferences.FlAG, "" + flag);
                    finish();
                } else {
                    flag = 0;
                    mPrefs.putStringValue(AppPreferences.FlAG, "" + flag);
                    Toast.makeText(Shipping_Address.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
