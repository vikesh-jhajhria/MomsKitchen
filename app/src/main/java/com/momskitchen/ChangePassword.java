package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.utils.App_Constant;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    Context context = ChangePassword.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((ImageView)findViewById(R.id.btn_back)).setOnClickListener(this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (Utility.isNetworkConnected(context)) {
                    if (((EditText) findViewById(R.id.edt_username)).getText().toString().trim().equalsIgnoreCase("")) {
                        ((EditText) findViewById(R.id.edt_username)).setText("");
                        snackBar("Please enter email ID");
                    } else if (!Utility.isEmailValid(((EditText) findViewById(R.id.edt_username)).getText().toString().trim())) {
                        ((EditText) findViewById(R.id.edt_username)).setText("");
                        snackBar("Please enter valid email ID");
                    } else if (((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equalsIgnoreCase("")) {
                        ((EditText) findViewById(R.id.edt_password)).setText("");
                        snackBar("Please enter password");
                    } else if (((EditText) findViewById(R.id.edt_c_pass)).getText().toString().trim().equalsIgnoreCase("")) {
                        ((EditText) findViewById(R.id.edt_c_pass)).setText("");
                        snackBar("Please enter confirm password.");
                    } else if (!((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equalsIgnoreCase(((EditText) findViewById(R.id.edt_c_pass)).getText().toString().trim())) {
                        ((EditText) findViewById(R.id.edt_c_pass)).setText("");
                        ((EditText) findViewById(R.id.edt_c_pass)).requestFocus();
                        snackBar("Password not matched.");
                    } else {
                        String email = ((EditText) findViewById(R.id.edt_username)).getText().toString().trim();
                        String pass = ((EditText) findViewById(R.id.edt_password)).getText().toString().trim();
                        String cpass = ((EditText) findViewById(R.id.edt_c_pass)).getText().toString().trim();

                        JSONObject obj = null;
                        try{
                            obj = new JSONObject();
                            obj.put("Email",email);
                            obj.put("Password",pass);
                            obj.put("CPassword",cpass);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        new Change_Pass().execute(""+obj);
                    }
                } else {
                    snackBar("No connection");
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }


    class Change_Pass extends AsyncTask<String, Void, String> {
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
                res = Utility.getJson(nameValuePairs, context, App_Constant.ChangePass);
                Log.e("asa", "" + nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(context);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    snackBar("Congratulation! Password succesfully changed.");
                    startActivity(new Intent(context, Login_Activity.class));
                    System.exit(0);
                } else {
                    snackBar("Oops! failure");
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
