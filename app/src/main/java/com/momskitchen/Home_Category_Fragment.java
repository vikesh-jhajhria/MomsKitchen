package com.momskitchen;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;


public class Home_Category_Fragment extends Fragment implements View.OnClickListener {

    ArrayList<HashMap<String, String>> array;
    // Context context;
    View view;
    AppPreferences mPrefs;
    private RecyclerView recyclerView;
    String pn_no = "";
public  static int pickFlag=0;
    public static int typeValue=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        ((Button) view.findViewById(R.id.btn_train)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btn_pick_up)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btn_home)).setOnClickListener(this);
        mPrefs = AppPreferences.getAppPreferences(getActivity());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                typeValue=2;
                pickFlag=0;
                App_Constant.Title = "Home Delivery";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create((View) view.findViewById(R.id.btn_home), "main"));
                    startActivity(new Intent(getActivity(), Product_Listing.class)
                            .putExtra("type", "Home Delivery"), options.toBundle());
                } else {
                    startActivity(new Intent(getActivity(), Product_Listing.class));
                }
                break;
            case R.id.btn_train:
                typeValue=3;
                App_Constant.Title = "Train Delivery";
                pickFlag=1;
                Pnr_Dialog();

                break;
            case R.id.btn_pick_up:
                typeValue=1;
                App_Constant.Title = "Pick Up";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pickFlag=1;
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create((View) view.findViewById(R.id.btn_train), "main"));
                    startActivity(new Intent(getActivity(), Product_Listing.class)
                            .putExtra("type", "Pick Up"), options.toBundle());
                } else {
                    pickFlag=1;
                    startActivity(new Intent(getActivity(), Product_Listing.class));

                }
                break;

        }
    }

    private void Pnr_Dialog() {
        final Dialog pnr_dialog = new Dialog(getActivity());
        pnr_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        pnr_dialog.setContentView(R.layout.pnr_dialog);
        pnr_dialog.setCanceledOnTouchOutside(false);
        pnr_dialog.setCancelable(false);
        // Set dialog title
        pnr_dialog.setTitle("");

        // set values for custom dialog components - text, image and button
        ImageView close = (ImageView) pnr_dialog.findViewById(R.id.iv_close);
        Button btn_pNR = (Button) pnr_dialog.findViewById(R.id.btn_pnr);

        final EditText edt_pnr = (EditText) pnr_dialog.findViewById(R.id.edt_pnr);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pnr_dialog.dismiss();
            }
        });

        btn_pNR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFlag=1;
                if (mPrefs.getStringValue(AppPreferences.MEMBER_ID).equalsIgnoreCase("")) {
                    startActivity(new Intent(getActivity(),Login_Activity.class));
                    getActivity().finishAffinity();
                } else {
                    if (edt_pnr.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Please enter PNR no.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Utility.isNetworkConnected(getActivity())) {
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject();
                                String pnr_no = edt_pnr.getText().toString().trim();
                                pn_no = pnr_no;
                                obj.put("RegistrationId", "" + mPrefs.getStringValue(AppPreferences.MEMBER_ID));
                                obj.put("TrainPNRNo", "" + pnr_no);
                            } catch (Exception e) {

                            }
                            pnr_dialog.dismiss();
                            new AddPnR().execute("" + obj);
                        } else {
                            Toast.makeText(getActivity(), "No connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        pnr_dialog.show();

    }


    class AddPnR extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", params[0]));
                res = Utility.getJson(nameValuePairs, getActivity(), App_Constant.Train_delivery);
                Log.e("asa", "" + nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            MyProgressDialog.close(getActivity());
          /*  {"Msg":"Success","value":"200","SuccesModels":{"msg":"Train delivery successfully","value":"100"}}*/

            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    String msg = new JSONObject(res).getJSONObject("SuccesModels").getString("msg");
                    Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();

                    mPrefs.putStringValue(AppPreferences.PNR_NUMBER, "" + pn_no);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                Pair.create((View) view.findViewById(R.id.btn_train), "main"));
                        startActivity(new Intent(getActivity(), Product_Listing.class)
                                .putExtra("type", "Train Delivery"), options.toBundle());
                    } else {
                        startActivity(new Intent(getActivity(), Product_Listing.class));
                    }

                } else {
                    Toast.makeText(getActivity(), "" + new JSONObject(res).getString("Msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
