package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    Context context = ForgotActivity.this;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //findViewById(R.id.iv_back).setOnClickListener(this);
      /*  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");*/

        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        ((ImageView)findViewById(R.id.btn_back)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_submit:
            if (Utility.isNetworkConnected(context)) {
                if (((EditText) findViewById(R.id.edt_username)).getText().toString().trim().equalsIgnoreCase("")) {
                    ((EditText) findViewById(R.id.edt_username)).setText("");
                    snackBar("Please enter email ID");
                } else if (!Utility.isEmailValid(((EditText) findViewById(R.id.edt_username)).getText().toString().trim())) {
                    ((EditText) findViewById(R.id.edt_username)).setText("");
                    snackBar("Please enter valid email ID");
                } else {
                    String email = ((EditText) findViewById(R.id.edt_username)).getText().toString().trim();
                    JSONObject obj = null;
                    try{
                        obj = new JSONObject();
                        obj.put("Email",email);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    new Forgot().execute(""+obj);
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

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ForgotActivity.this.finish();
                break;
        }
        return true;
    }

*/
    class Forgot extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(ForgotActivity.this);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", params[0]));
                res = Utility.getJson(nameValuePairs, context, App_Constant.Forgot);
                Log.e("asa", "" + nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(ForgotActivity.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    snackBar("Please change your password.");
                    startActivity(new Intent(ForgotActivity.this,ChangePassword.class));
                    ForgotActivity.this.finish();
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
