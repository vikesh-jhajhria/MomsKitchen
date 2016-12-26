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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.utils.App_Constant;
import com.utils.MyProgressDialog;
import com.utils.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Registration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Context context = Registration.this;
    String DOB = "";
    String cal_type = "";
    String l_name, f_name, password, email, address1,
            state, city, anniversary, area, landmark, mobile, dob;
    String gcm_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.registration_layout);
       gcm_id = FirebaseInstanceId.getInstance().getToken();
        Log.e("my gcm id : ", "" + gcm_id);
        ((TextView) findViewById(R.id.edt_dob)).setOnClickListener(this);
        ((TextView) findViewById(R.id.edt_aniversary)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_done)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.iv_back)).setOnClickListener(this);
    }

    private void snackBar(String msg) {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        snackbar.show();
    }


    private boolean validate() {
        if (((EditText) findViewById(R.id.edt_f_name)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_f_name)).requestFocus();
            Toast.makeText(Registration.this,"Please enter first name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_l_name)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_l_name)).requestFocus();
            Toast.makeText(Registration.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_address)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_address1)).requestFocus();
            snackBar("Please enter address");
            //Toast.makeText(RegistrationActivity.this, "A", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_password)).requestFocus();
            Toast.makeText(Registration.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_password)).requestFocus();
            Toast.makeText(Registration.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_c_password)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_c_password)).requestFocus();

            Toast.makeText(Registration.this,"Please enter confirm password", Toast.LENGTH_SHORT).show();
        } else if (!((EditText) findViewById(R.id.edt_password)).getText().toString().trim().equals(((EditText) findViewById(R.id.edt_c_password)).getText().toString().trim())) {
            ((EditText) findViewById(R.id.edt_c_password)).requestFocus();
            ((EditText) findViewById(R.id.edt_c_password)).setText("");

            Toast.makeText(Registration.this,"password do not match", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_area)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_area)).requestFocus();
            Toast.makeText(Registration.this,"Please enter your area", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_landmark)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_landmark)).requestFocus();

            Toast.makeText(Registration.this,"Please enter your nearest landmark", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((Spinner) findViewById(R.id.spn_state)).getSelectedItem().equals("Select State")) {
            // ((Spinner) findViewById(R.id.spn_state)).requestFocus();

            Toast.makeText(Registration.this,"Please select state", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((Spinner) findViewById(R.id.spn_city)).getSelectedItem().equals("Select City")) {
            //((Spinner) findViewById(R.id.spn_city)).requestFocus();

            Toast.makeText(Registration.this,"Please select city", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((TextView) findViewById(R.id.edt_dob)).getText().toString().trim().equalsIgnoreCase("")) {
            ((TextView) findViewById(R.id.edt_dob)).requestFocus();

            Toast.makeText(Registration.this,"Please enter DOB", Toast.LENGTH_SHORT).show();
            return true;
        }/* else if (((TextView) findViewById(R.id.edt_aniversary)).getText().toString().trim().equalsIgnoreCase("")) {
            ((TextView) findViewById(R.id.edt_aniversary)).requestFocus();

            Toast.makeText(Registration.this,"Please enter anniversary date", Toast.LENGTH_SHORT).show();
            return true;
        }*/ else if (((EditText) findViewById(R.id.edt_mobile)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_mobile)).requestFocus();

            Toast.makeText(Registration.this,"Please enter mobile number", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_mobile)).getText().toString().trim().length() != 10) {
            ((EditText) findViewById(R.id.edt_mobile)).requestFocus();

            Toast.makeText(Registration.this,"Please enter valid mobile number", Toast.LENGTH_SHORT).show();
            return true;
        } else if (((EditText) findViewById(R.id.edt_email)).getText().toString().trim().equalsIgnoreCase("")) {
            ((EditText) findViewById(R.id.edt_email)).requestFocus();

            Toast.makeText(Registration.this,"Please enter email ID", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!Utility.isEmailValid(((EditText) findViewById(R.id.edt_email)).getText().toString().trim())) {
            ((EditText) findViewById(R.id.edt_email)).requestFocus();

            Toast.makeText(Registration.this,"Please enter valid email ID", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_dob:
                cal_type = "dob";
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Registration.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.edt_aniversary:
                cal_type = "ani";
                Calendar now1 = Calendar.getInstance();
                DatePickerDialog dpd1 = DatePickerDialog.newInstance(
                        Registration.this,
                        now1.get(Calendar.YEAR),
                        now1.get(Calendar.MONTH),
                        now1.get(Calendar.DAY_OF_MONTH)
                );
                dpd1.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.btn_done:
                if (Utility.isNetworkConnected(Registration.this)) {

                    if (!validate()) {
                        f_name = ((EditText) findViewById(R.id.edt_f_name)).getText().toString().trim();
                        l_name = ((EditText) findViewById(R.id.edt_l_name)).getText().toString().trim();
                        password = ((EditText) findViewById(R.id.edt_password)).getText().toString().trim();
                        email = ((EditText) findViewById(R.id.edt_email)).getText().toString().trim();
                        address1 = ((EditText) findViewById(R.id.edt_address)).getText().toString().trim();
                        state = "" + ((Spinner) findViewById(R.id.spn_state)).getSelectedItemPosition();
                        city = "" + ((Spinner) findViewById(R.id.spn_city)).getSelectedItemPosition();
                        anniversary = ((TextView) findViewById(R.id.edt_aniversary)).getText().toString().trim();
                        area = ((EditText) findViewById(R.id.edt_area)).getText().toString().trim();
                        landmark = ((EditText) findViewById(R.id.edt_landmark)).getText().toString().trim();
                        mobile = ((EditText) findViewById(R.id.edt_mobile)).getText().toString().trim();
                        dob = ((TextView) findViewById(R.id.edt_dob)).getText().toString().trim();
                        JSONObject json = new JSONObject();
                        try {
                            json.put("FirstName", f_name);
                            json.put("LastName", l_name);
                            json.put("Address", address1);
                            json.put("Area", area);
                            json.put("LandMark", landmark);
                            json.put("Country", "India");
                            json.put("State", state);
                            json.put("City", city);
                            json.put("Postal", "0");
                            json.put("DOB", dob);
                            json.put("Mobile", mobile);
                            json.put("Anniversary", anniversary);
                            json.put("Email", email);
                            json.put("Password", password);
                            json.put("CPassword", password);
                            json.put("OwnerId", "11");
                            json.put("Gcm",gcm_id);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Loh ln = new Loh();
                        ln.execute("" + json);
                    }
                } else {
                    snackBar("No connection");
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public class Loh extends AsyncTask<String, Void, String> {
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

                Log.e("asa", "" + nameValuePairs);
                res = Utility.getJson(nameValuePairs, context, App_Constant.Registration);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(Registration.this);
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {

                    startActivity(new Intent(Registration.this, Login_Activity.class));
                   finishAffinity();
                } else {
                    Toast.makeText(Registration.this, new JSONObject(res).getString("Msg"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.e("Ids", "" + view.getId());
        int month = monthOfYear + 1;
        DOB = "" + year + "-" + "" + month + "-" + dayOfMonth;
        Log.e("as", "" + DOB);

        if (cal_type.equalsIgnoreCase("dob")) {
            ((TextView) findViewById(R.id.edt_dob)).setText("" + DOB);
        } else {
            ((TextView) findViewById(R.id.edt_aniversary)).setText("" + DOB);
        }
    }
}
