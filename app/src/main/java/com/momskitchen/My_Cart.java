package com.momskitchen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;
import com.utils.MyCart_Adapter;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class My_Cart extends Activity implements View.OnClickListener {

    Context context = My_Cart.this;
    AppPreferences mPrefs;
    ArrayList<HashMap<String, String>> array;
    MyCart_Adapter adapter;
    ListView listview;
    DBHandler db;
   public static  TextView total_txtview,promo_txtview,tax_textview,gTotal_txtview,homecharge;
    String mock_object = "" ;
    public static View footerView;
    public static BigDecimal taxRate = new BigDecimal("0.00");
    public static BigDecimal grandTotal = new BigDecimal("0.00");
    public static BigDecimal promoRtae = new BigDecimal("0.00");
    public static BigDecimal shipping_charge = new BigDecimal("0.00");
    public static BigDecimal total = new BigDecimal("0.00");

    int i=0;
    RadioButton rd_cod,rb_card;
    boolean paymentBoolean=false;
    LayoutInflater layoutinflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycart_layout);
        listview = (ListView) findViewById(R.id.cartlist);
        findViewById(R.id.iv_back).setOnClickListener(this);
        db = new DBHandler(My_Cart.this);
        mPrefs = AppPreferences.getAppPreferences(My_Cart.this);

        if (Utility.isNetworkConnected(context)) {
           retriveData();
            if (array != null && array.size() > 0) {
                adapter = new MyCart_Adapter(My_Cart.this, array);
                layoutinflater  = getLayoutInflater();
               // footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cart_footer, null, false);
                footerView= (ViewGroup)layoutinflater.inflate(R.layout.cart_footer,listview,false);
                listview.addFooterView(footerView);
                listview.setAdapter(adapter);
                total_txtview=(TextView)footerView.findViewById(R.id.txt_rate);
                footerView.findViewById(R.id.code).setOnClickListener(this);
                footerView.findViewById(R.id.btn_next).setOnClickListener(this);
                promo_txtview=(TextView)footerView.findViewById(R.id.txt_promo);
                homecharge=(TextView)footerView.findViewById(R.id.charge_txtview);
                tax_textview=(TextView)footerView.findViewById(R.id.taxRate_textview);
                gTotal_txtview=(TextView)footerView.findViewById(R.id.grand_total_txtview);
                for (int j = 0; j < array.size(); j++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map = array.get(j);
                    BigDecimal rate = new BigDecimal(map.get("rate"));
                    BigDecimal qty = new BigDecimal(map.get("quantity"));
                    BigDecimal finalrate = rate.multiply(qty);

                    total = total.add(finalrate);
                    Log.e("Total",""+total);
                    total_txtview.setText("Rs " + total);
                    String string_tax = (map.get("taxRate"));
                    taxRate = taxRate.add(new BigDecimal(string_tax));
                    tax_textview.setText("Rs " + taxRate);
                    String promoCode = promo_txtview.getText().toString().trim();
                    promoCode = promoCode.substring(3, promoCode.length());
                    if (promoCode.equalsIgnoreCase("")) {
                        promoRtae = new BigDecimal("0.00");
                    } else {
                        promoRtae = new BigDecimal(promoCode);
                    }
                    grandTotal = total.add(taxRate);
                    float str=Float.parseFloat(""+grandTotal);
                    int obj=(int)str;
                    getDelivery(obj);
                  //  grandTotal = grandTotal.add(shipping_charge);
                    grandTotal = grandTotal.subtract(promoRtae);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(My_Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //((TextView)footerView.findViewById(R.id.charge_txtview)).setText("Rs " + 0.00);
       // Shipping_Address.flag=0;
    }

    private void retriveData() {
            Log.e("Reading: ", "Reading all shops..");
            String use_ = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            if (use_.equalsIgnoreCase("")) {
                //total = 0.0;
            } else {
                List<MyCart_property> shops = db.getAllProduct(use_);
                if (shops != null && shops.size() > 0) {
                /*    v_flag = 1;*/
                    array = new ArrayList<HashMap<String, String>>();
                   // total = 0.0;
                    for (MyCart_property shop : shops) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("id", "" + shop.getId());
                        map.put("name", shop.getP_name());
                        map.put("quantity", shop.getOrder_qty());
                        map.put("rate", shop.getP_rate());
                        map.put("image", shop.getImage());
                        map.put("taxRate", shop.getTax_charge());
                        map.put("foodId", shop.getFoodId());
                        if(shop.getSubFoodId().equalsIgnoreCase(""))
                        {
                            map.put("subFoodId","-1");
                        }
                        else {
                            map.put("subFoodId", shop.getSubFoodId());
                        }
                        map.put("regular",shop.getRegular());
                        map.put("spice", shop.getSpice());

                        array.add(map);

                    }

                    Log.e("my pack", "" + array);
                    db.close();

                } else {
                    Toast.makeText(My_Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                }


            }





    }
    public static void my_total() {

        total_txtview.setText("" + total);
        Log.e("Totals",""+total);
        tax_textview.setText("" + taxRate);
        grandTotal = grandTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
        Log.e("withoutdecimal",""+grandTotal);
        gTotal_txtview.setText("" + grandTotal+""+" .00");
    }
	
	class promoCode extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                res = Utility.GetRequest(My_Cart.this, App_Constant.PROMO_CODE+params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(context);
            try {

                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("promo code available")) {
                    mock_object =  new JSONObject(res).getJSONObject("SuccesModels").getString("Amount") ;
                     promo_txtview.setText("Rs "+mock_object);
                    String promoCode=promo_txtview.getText().toString().trim();
                    promoCode=promoCode.substring(3, promoCode.length());
                    promoRtae = new BigDecimal(promoCode);
                    grandTotal = total.add(taxRate);
                    float str=Float.parseFloat(""+grandTotal);
                    int obj=(int)str;
                    if(Home_Category_Fragment.pickFlag == 0) {
                        getDelivery(obj);
                    }
                    //  grandTotal = grandTotal.add(shipping_charge);
                    grandTotal = grandTotal.subtract(promoRtae);
                    grandTotal = grandTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
                    gTotal_txtview.setText("" + grandTotal+""+" .00");
                }else{
                   Toast.makeText(context,new JSONObject(res).getString("Msg"),Toast.LENGTH_LONG).show();
                    promo_txtview.setText("0.00");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("pickup",""+Home_Category_Fragment.pickFlag);
        if(Home_Category_Fragment.pickFlag == 1){
            Log.e("pickup",""+Home_Category_Fragment.pickFlag);
            withoutpickup();
        }
        else {
            withpickup();
        }
    }

    private void withpickup() {
        // shipping_charge=Shipping_Address.charge;
        // ((TextView)footerView.findViewById(R.id.charge_txtview)).setText("Rs "+shipping_charge);
        String promoCode=promo_txtview.getText().toString().trim();
        promoCode=promoCode.substring(3,promoCode.length());
        if (promoCode.equalsIgnoreCase("")) {
            //promoRtae = 0.0;
            promoRtae = new BigDecimal("0.00");
        } else {
            promoRtae = new BigDecimal(promoCode);

        }

        grandTotal = total.add(taxRate);
        float str=Float.parseFloat(""+grandTotal);
        int obj=(int)str;
        getDelivery(obj);
        //  grandTotal = grandTotal.add(shipping_charge);
        grandTotal = grandTotal.subtract(promoRtae);
        grandTotal = grandTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
        gTotal_txtview.setText("" + grandTotal+""+" .00");
        if(mPrefs.getStringValue(AppPreferences.FlAG).equalsIgnoreCase("1")){
            ( (Button)footerView.findViewById(R.id.btn_next)).setText("Buy");
            ( (Button)footerView.findViewById(R.id.btn_next)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            ( (Button)footerView.findViewById(R.id.btn_next)).setText("Next");
            ( (Button)footerView.findViewById(R.id.btn_next)).setBackgroundColor(getResources().getColor(R.color.btn_color                                                                                                       ));
        }
    }
    private void withoutpickup() {
        // shipping_charge=Shipping_Address.charge;
        // ((TextView)footerView.findViewById(R.id.charge_txtview)).setText("Rs "+shipping_charge);
        String promoCode=promo_txtview.getText().toString().trim();
        promoCode=promoCode.substring(3,promoCode.length());
        if (promoCode.equalsIgnoreCase("")) {
            //promoRtae = 0.0;
            promoRtae = new BigDecimal("0.00");
        } else {
            promoRtae = new BigDecimal(promoCode);

        }

        grandTotal = total.add(taxRate);
        float str=Float.parseFloat(""+grandTotal);
        int obj=(int)str;
        getPickDelivery(obj);
        //  grandTotal = grandTotal.add(shipping_charge);
        grandTotal = grandTotal.subtract(promoRtae);
        grandTotal = grandTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
        gTotal_txtview.setText("" + grandTotal+""+" .00");
        ( (Button)footerView.findViewById(R.id.btn_next)).setText("Buy");
        ( (Button)footerView.findViewById(R.id.btn_next)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                My_Cart. total = new BigDecimal("0.00");
                My_Cart.grandTotal=new BigDecimal("0.00");
               // My_Cart.shipping_charge=new BigDecimal("0.00");
                My_Cart.promoRtae=new BigDecimal("0.00");
                My_Cart.taxRate=new BigDecimal("0.00");
              finish();
                break;
            case R.id.code:
               String uID = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
               new promoCode().execute(uID);
                break;
            case R.id.btn_next:
                  if(((Button)footerView.findViewById(R.id.btn_next)).getText().toString().trim().equalsIgnoreCase("Next")) {
                      startActivity(new Intent(context, Shipping_Address.class));
                  }else {
                     openDialog();
                  }

                break;
        }

    }
    public void getDelivery(int value)
    {
        int shipping_charges = 0;
        if (value <= 250)
        {
            shipping_charges = 80;
        }
        if (value > 250 && value < 500)
        {
            shipping_charges = 100;
        }
        else if (value > 500 && value < 1000)
        {
            shipping_charges = 150;
        }
        else if (value > 1000 && value < 1500)
        {
            shipping_charges = 200;
        }
        String val=""+shipping_charges;
        shipping_charge=new BigDecimal(val);
         grandTotal=grandTotal.add(BigDecimal.valueOf(shipping_charges));

        homecharge.setText("Rs"+" "+BigDecimal.valueOf(shipping_charges));

    }
    public void getPickDelivery(int value)
    {
        int shipping_charges = 00;
        if (value <= 250)
        {
            shipping_charges = 00;
        }
        if (value > 250 && value < 500)
        {
            shipping_charges = 00;
        }
        else if (value > 500 && value < 1000)
        {
            shipping_charges = 00;
        }
        else if (value > 1000 && value < 1500)
        {
            shipping_charges = 00;
        }
        String val=""+shipping_charges;
        shipping_charge=new BigDecimal(val);
        grandTotal=grandTotal.add(BigDecimal.valueOf(shipping_charges));

        homecharge.setText("Rs"+" "+BigDecimal.valueOf(shipping_charges));

    }
  /*  public void onBackPressed() {
        My_Cart. total = new BigDecimal("0.00");
        My_Cart.grandTotal=new BigDecimal("0.00");
        //   My_Cart.shipping_charge=new BigDecimal("0.00");
        My_Cart.promoRtae=new BigDecimal("0.00");
        My_Cart.taxRate=new BigDecimal("0.00");
        return;
    }*/
  @Override
  public void onBackPressed()
  {
      My_Cart. total = new BigDecimal("0.00");
      My_Cart.grandTotal=new BigDecimal("0.00");
      //   My_Cart.shipping_charge=new BigDecimal("0.00");
      My_Cart.promoRtae=new BigDecimal("0.00");
      My_Cart.taxRate=new BigDecimal("0.00");
      finish();
  }

    public void openDialog() {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.payment_method);
         dialog.setTitle("Payment Method");
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        rd_cod=(RadioButton)dialog.findViewById(R.id.rb_cod);
        rb_card=(RadioButton)dialog.findViewById(R.id.rb_card);
        dialog.show();
        dialog.findViewById(R.id.checkout_txtview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 mPrefs.putStringValue(AppPreferences.FlAG,"" + 3);
                if(rd_cod.isChecked())
                    paymentBoolean=true;
                if(rb_card.isChecked())
                    paymentBoolean=false;

                dialog.dismiss();
                if (Utility.isNetworkConnected(context)) {
                        JSONArray jArray = null;
                        String user_= mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                    String isVerify= mPrefs.getStringValue(AppPreferences.IsVerify);
                        List<MyCart_property> shops = db.getAllProduct(user_);
                        Log.e("shopSize",""+shops.size());
                        if (shops.size() > 0) {
                            if (shops != null && shops.size() > 0) {
                                jArray = new JSONArray();
                                for (MyCart_property shop : shops) {
                                    JSONObject obj = new JSONObject();
                                    try {
                                        int brandID = Integer.parseInt(shop.getBrand_id());
                                        int p_id = Integer.parseInt(shop.getP_id());
                                        int q_id = Integer.parseInt(shop.getQuantity());
                                        int c_id = Integer.parseInt(shop.getCategory_id());
                                        int user_id = Integer.parseInt(mPrefs.getStringValue(AppPreferences.MEMBER_ID));
                                        obj.put("CategoryId",c_id);
                                        obj.put("BrandId",brandID);
                                        obj.put("ProductId",p_id);
                                        obj.put("ProductName",""+shop.getP_name());
                                        obj.put("Quantity",q_id);
                                        obj.put("Rate",Double.parseDouble(shop.getP_rate()));
                                        obj.put("OrderQty",Integer.parseInt(shop.getOrder_qty()));
                                        obj.put("TaxId",Integer.parseInt(shop.getTax_id()));
                                        obj.put("FoodId",Integer.parseInt(shop.getFoodId()));
                                        if(shop.getSubFoodId().equalsIgnoreCase("")){
                                            obj.put("SubFoodId","-1");
                                        }else {
                                            obj.put("SubFoodId", "" + shop.getSubFoodId());
                                        }
                                        obj.put("RegularN",""+shop.getRegular());
                                        obj.put("SpicesN",""+shop.getSpice());
                                        obj.put("Description",""+shop.getDesc());
                                        obj.put("ClientId",user_id);
                                        obj.put("CompanyId", 1);
                                        obj.put("COD",paymentBoolean);
                                        obj.put("Type",""+shop.getTypevalue());
                                        obj.put("IsVerify",""+isVerify);

                                        Log.e("objects", "" + obj);

                                        jArray.put(obj);
                                        Log.e("JaRRAY", "" + jArray.get(0));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                db.close();
                            }
                        }
                    else {
                            Toast.makeText(My_Cart.this, "Cart is empty.", Toast.LENGTH_SHORT).show();
                        }

                        if (Utility.isNetworkConnected(context)) {
                            new paymentTask().execute(""+jArray);
                        } else {
                            Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                        }
                }else {


                }


            }
        });
        dialog.findViewById(R.id.cancle_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    class paymentTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", params[0]));
                // nameValuePairs.add(new BasicNameValuePair("Password", params[1]));
                res = Utility.getJson(nameValuePairs, context, App_Constant.ORDER);
                Log.e("asa", "" + nameValuePairs);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Response",""+res);
            return res;
        }


        protected void onPostExecute(String res) {
            MyProgressDialog.close(context);
            try {

                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                     total=new BigDecimal(0.0);
                    if(paymentBoolean==true) {
                        String val = new JSONObject(res).getJSONObject("SuccesModels").getString("value");
                        Toast.makeText(My_Cart.this, new JSONObject(res).getJSONObject("SuccesModels").getString("msg"), Toast.LENGTH_LONG).show();
                        db = new DBHandler(My_Cart.this);
                        db.deleteAll();
                        //Shipping_Address.charge=new BigDecimal("0.00");
                        startActivity(new Intent(My_Cart.this, Product_Listing.class));
                        finishAffinity();
                    }else{

                        Toast.makeText(My_Cart.this, new JSONObject(res).getJSONObject("SuccesModels").getString("msg"), Toast.LENGTH_LONG).show();
                        db = new DBHandler(My_Cart.this);
                        db.deleteAll();
                        //Shipping_Address.charge=new BigDecimal("0.00");
                        Log.e("grandTotal",""+grandTotal);
                        startActivity(new Intent(My_Cart.this, MyPaymentActivity.class).putExtra("grandTotal",""+grandTotal));
                        finishAffinity();
                    }
                }
                else{
                    Toast.makeText(My_Cart.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    }
