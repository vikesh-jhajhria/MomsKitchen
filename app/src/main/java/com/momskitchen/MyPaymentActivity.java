
package com.momskitchen;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.DBHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MyPaymentActivity extends Activity {

    //private Button button;

    private static final String TAG = "MainActivity";
    WebView webviewPayment;
    WebView mwebview;
    TextView  txtview;
    AppPreferences mPrefs;
    DBHandler db;
    private String GrandTotalAmount;
    String user_name,user_email,user_contactno;
	/*
	protected  void writeStatus(String str){
		txtview.setText(str);
	}*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHandler(MyPaymentActivity.this);
        mPrefs = AppPreferences.getAppPreferences(MyPaymentActivity.this);
        //button=(Button)findViewById(R.id.button1);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            GrandTotalAmount = bundle.getString("grandTotal");
        }
        Log.e("GrandTotalAmount",""+GrandTotalAmount);
        webviewPayment = (WebView) findViewById(R.id.webView1);
        webviewPayment.getSettings().setJavaScriptEnabled(true);
        webviewPayment.getSettings().setDomStorageEnabled(true);
        webviewPayment.getSettings().setLoadWithOverviewMode(true);
        webviewPayment.getSettings().setUseWideViewPort(true);
        webviewPayment.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webviewPayment.getSettings().setSupportMultipleWindows(true);
        webviewPayment.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webviewPayment.addJavascriptInterface(new PayUJavaScriptInterface(), "PayUMoney");

        StringBuilder url_s = new StringBuilder();

        url_s.append("https://secure.payu.in/_payment");

        Log.e(TAG, "call url " + url_s);


        //	webviewPayment.postUrl(url_s.toString(),EncodingUtils.getBytes(getPostString(), "utf-8"));

        webviewPayment.postUrl(url_s.toString(),getPostString().getBytes(Charset.forName("UTF-8")));

        webviewPayment.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @SuppressWarnings("unused")
            public void onReceivedSslError(WebView view, SslErrorHandler handler) {
                Log.e("Error", "Exception caught!");
                handler.cancel();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });
    }

    private final class PayUJavaScriptInterface {
        PayUJavaScriptInterface() {
        }

        @JavascriptInterface
        public void success( long id, final String paymentId) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(MyPaymentActivity.this, "Status is txn is success "+" payment id is "+paymentId, 8000).show();
                    //String str="Status is txn is success "+" payment id is "+paymentId;
                    // new MainActivity().writeStatus(str);

                    TextView  txtview;
                    txtview = (TextView) findViewById(R.id.textView1);
                    txtview.setText("Status is txn is success "+" payment id is "+paymentId);

                }
            });
        }
        @JavascriptInterface
        public void failure( long id, final String paymentId) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(MyPaymentActivity.this, "Status is txn is failed "+" payment id is "+paymentId, 8000).show();
                    //String str="Status is txn is failed "+" payment id is "+paymentId;
                    // new MainActivity().writeStatus(str);

                    TextView  txtview;
                    txtview = (TextView) findViewById(R.id.textView1);
                    txtview.setText("Status is txn is failed "+" payment id is "+paymentId);

                }
            });
        }

    }

        //

    private String getPostString()
    {
        user_name= mPrefs.getStringValue(AppPreferences.U_NAME);
        user_email =mPrefs.getStringValue(AppPreferences.USER_NAME);
        user_contactno =mPrefs.getStringValue(AppPreferences.CONTACT_NUMBER);
        String Product= ""+getProductInfo();

        String key  = "PCGgQeuG";
        String salt  = "MB2ZUg0TPx";
        String txnid = "TXN_1";
        String amount = GrandTotalAmount;
        String firstname = user_name;
        String email = user_email;
        String productInfo = Product;

        StringBuilder post = new StringBuilder();
        post.append("key=");
        post.append(key);
        post.append("&");
        post.append("txnid=");
        post.append(txnid);
        post.append("&");
        post.append("amount=");
        post.append(amount);
        post.append("&");
        post.append("productinfo=");
        post.append(productInfo);
        post.append("&");
        post.append("firstname=");
        post.append(firstname);
        post.append("&");
        post.append("email=");
        post.append(email);
        post.append("&");
        post.append("phone=");
        post.append(user_contactno);
        post.append("&");
        post.append("surl=");
        post.append("https://www.payumoney.com/mobileapp/payumoney/success.php");
        //https://www.payumoney.com/mobileapp/payumoney/success.php
        //https://www.payumoney.com/mobileapp/payumoney/failure.php
        post.append("&");
        post.append("furl=");
        post.append("https://www.payumoney.com/mobileapp/payumoney/failure.php");
        post.append("&");

        StringBuilder checkSumStr = new StringBuilder();
		/* =sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||salt) */
        MessageDigest digest=null;
        String hash;
        try {
            digest = MessageDigest.getInstance("SHA-512");// MessageDigest.getInstance("SHA-256");

            checkSumStr.append(key);
            checkSumStr.append("|");
            checkSumStr.append(txnid);
            checkSumStr.append("|");
            checkSumStr.append(amount);
            checkSumStr.append("|");
            checkSumStr.append(productInfo);
            checkSumStr.append("|");
            checkSumStr.append(firstname);
            checkSumStr.append("|");
            checkSumStr.append(email);
            checkSumStr.append("|||||||||||");
            checkSumStr.append(salt);

            digest.update(checkSumStr.toString().getBytes());

            hash = bytesToHexString(digest.digest());
            post.append("hash=");
            post.append(hash);
            post.append("&");
            Log.i(TAG, "SHA result is " + hash);
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        post.append("service_provider=");
        post.append("payu_paisa");
        return post.toString();
    }

    private JSONObject getProductInfo()
    {
        try {
            JSONObject productInfo = new JSONObject();
            JSONArray jsonPaymentPartsArr = null;
            String user_ = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            List<MyCart_property> shops = db.getAllProduct(user_);
            Log.e("shopSize", "" + shops.size());
            if (shops.size() > 0) {
                if (shops != null && shops.size() > 0) {
                    jsonPaymentPartsArr = new JSONArray();
                    for (MyCart_property shop : shops) {
                        JSONObject jsonPaymentPart = new JSONObject();
                        try {
                            BigDecimal rate = new BigDecimal(shop.getP_rate());
                            BigDecimal qty = new BigDecimal(shop.getOrder_qty());
                            BigDecimal finalrate = rate.multiply(qty);
                            //create payment part object
                            jsonPaymentPart.put("name", "" + shop.getP_name());
                            jsonPaymentPart.put("description", "" + shop.getDesc());
                            jsonPaymentPart.put("value", ""+finalrate);
                            jsonPaymentPart.put("isRequired", "true");
                            jsonPaymentPart.put("settlementEvent", "arihantsalesjdp@gmail.com");


                            //create payment part array

                            jsonPaymentPartsArr.put(jsonPaymentPart);

                            //paymentIdentifiers
                            JSONObject jsonPaymentIdent = new JSONObject();
                            jsonPaymentIdent.put("field", "CompletionDate");
                            jsonPaymentIdent.put("value", "31/10/2012");

                            //create payment part array
                            JSONArray jsonPaymentIdentArr = new JSONArray();
                            jsonPaymentIdentArr.put(jsonPaymentIdent);

                            productInfo.put("paymentParts", jsonPaymentPartsArr);
                            productInfo.put("paymentIdentifiers", jsonPaymentIdentArr);

                            Log.e(TAG, "product Info = " + productInfo.toString());
                            return productInfo;


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }
            }
        }catch (Exception e){

        }
        return null;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');

            }
            sb.append(hex);
        }
        return sb.toString();
    }




}
