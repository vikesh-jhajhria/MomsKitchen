package com.momskitchen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.Utility;

import org.json.JSONObject;

public class
Splash_Activity extends AppCompatActivity {

    AppPreferences mPrefs;
String gcm_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_layout);
        mPrefs = AppPreferences.getAppPreferences(Splash_Activity.this);
        //GCM registration id
        gcm_id = FirebaseInstanceId.getInstance().getToken();
        Log.e("my gcm id : ", "" + gcm_id);
        callMethod();
    }


    private void callMethod() {
        if (Utility.isNetworkConnected(Splash_Activity.this)) {
            new AdvertiseClass().execute();
        } else {

            Toast.makeText(Splash_Activity.this, "No connection", Toast.LENGTH_LONG).show();
        }

    }


    class AdvertiseClass extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                res = Utility.GetRequest(Splash_Activity.this, App_Constant.Advt);
                Log.e("asa", "" + res);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {

            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    String advt = "";
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        if (advt.equalsIgnoreCase("")) {
                            advt = advt.concat(mock_object.getString("AdvertisementName"));
                        } else {
                            advt = advt.concat("   :   " + mock_object.getString("AdvertisementName"));
                        }
                    }
                    App_Constant.Advt_title = advt;

                    if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {
                        startActivity(new Intent(Splash_Activity.this, Login_Activity.class));
                        Splash_Activity.this.finish();
                    } else {
                        startActivity(new Intent(Splash_Activity.this, Home_Activity.class));
                        Splash_Activity.this.finish();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
