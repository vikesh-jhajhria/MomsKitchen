package com.momskitchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login_Activity extends Activity implements View.OnClickListener {

    Context context = Login_Activity.this;
    AppPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);
        mPrefs = AppPreferences.getAppPreferences(context);
        ((Button) findViewById(R.id.btn_registration)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_login)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_forgot)).setOnClickListener(this);
        findViewById(R.id.tv_signInLater).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_registration:
                startActivity(new Intent(Login_Activity.this, Registration.class));
                break;
            case R.id.tv_signInLater:
                startActivity(new Intent(Login_Activity.this, Home_Activity.class));
                break;
            case R.id.btn_login:
                if (Utility.isNetworkConnected(context)) {
                    if (((EditText) findViewById(R.id.edt_username)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Login_Activity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_username)).requestFocus();
                    } else if (((EditText) findViewById(R.id.edt_username)).getText().toString().trim().length() != 10) {

                        Toast.makeText(Login_Activity.this, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_username)).requestFocus();
                    } else if (((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equalsIgnoreCase("")) {

                        Toast.makeText(Login_Activity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                        ((EditText) findViewById(R.id.edt_password)).requestFocus();
                    } else {
                        String username = ((EditText) findViewById(R.id.edt_username)).getText().toString().trim();
                        String password = ((EditText) findViewById(R.id.edt_password)).getText().toString().trim();

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("Mobile", username);
                            obj.put("Password", password);

                            /*{"Mobile": "9413957452","Password": "123"}*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("hhgh", "" + obj);

                        new LoginNow().execute("" + obj);
                    }
                } else {
                    snackBar("No connection");
                }
                //  startActivity(new Intent(Login_Activity.this,Home_Activity.class));
                break;
            case R.id.tv_forgot:
                startActivity(new Intent(Login_Activity.this, ForgotActivity.class));
                break;


        }
    }


    class LoginNow extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(Login_Activity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", params[0]));
                // nameValuePairs.add(new BasicNameValuePair("Password", params[1]));
                res = Utility.getJson(nameValuePairs, context, App_Constant.Login);
                Log.e("asa", "" + nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(Login_Activity.this);
            try {
                if (new JSONObject(res).getString("Msg").trim().equalsIgnoreCase("Login Successfully")) {

                    String user_email = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(0).getString("Email");
                    String MID = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(0).getString("RegistrationId");
                    String name = "" + new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(0).getString("FirstName") + " " + new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(0).getString("LastName");
                    //  App_Constant.User_Email = "" + name;
                   // App_Constant.User_Email = "" + FName+" "+LName;
                   // mPrefs.putStringValue(AppPreferences.U_NAME, "" + FName);
                    mPrefs.putStringValue(AppPreferences.U_NAME, "" + name);
                    mPrefs.putStringValue(AppPreferences.MEMBER_ID, "" + MID);
                    mPrefs.putStringValue(AppPreferences.USER_NAME, "" + user_email);
                    startActivity(new Intent(Login_Activity.this, Home_Activity.class));

            finishAffinity();
                } else {
                    Toast.makeText(Login_Activity.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void snackBar(String msg) {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#00A652"));
        snackbar.show();
    }
}
