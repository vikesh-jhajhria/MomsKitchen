package com.momskitchen;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.utils.App_Constant;
import com.utils.Offer_Adapter;
import com.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

/**
 * Created by hitesh.singh on 7/11/2016.
 */
public class Offer_fragment extends Fragment implements View.OnClickListener {

    ArrayList<HashMap<String, String>> array_offer;
    Offer_Adapter adapter;
    View v_offer;
    // ViewPager pager;
    ViewPagerCustomDuration pager;
    int pos = 0;
    Timer timer;
    int page = 0;

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        public void run() {
            if (page < array_offer.size()) {
                page++;
                Log.e("hand true :", "" + page);
                pager.setCurrentItem(page, true);
                handler.postDelayed(r, 3000);
            } else {
                page = 0;
                pager.setCurrentItem(0, true);
                Log.e("hand false :", "" + page);
                handler.postDelayed(r, 3000);
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v_offer = inflater.inflate(R.layout.offer_fragment, container, false);
        pager = (ViewPagerCustomDuration) v_offer.findViewById(R.id.viewpager);
         pager.setScrollDurationFactor(4);
        new get_offer().execute();
        return v_offer;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //  case R.id.btn_left:
            //    pager.setCurrentItem(pager.getCurrentItem() - 1);
            //  break;
            //case R.id.btn_right:
            // pager.setCurrentItem(pager.getCurrentItem() + 1);
            //  break;
        }
    }

    class get_offer extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String res = null;
            try {
                res = Utility.GetRequest(getActivity(), App_Constant.Offer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {

            try {

               /*{"Msg":"Success","value":"200","SuccesModels":[{"OfferId":1,"OfferNo":1,
                       "OfferName":"Offer","ImageName":"http://momskitchenjodhpur.com/DBSweetImages/Sweet_Corn_Soup.jpg"}]}
               */
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    array_offer = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < new JSONObject(res).getJSONArray("SuccesModels").length(); i++) {
                        JSONObject mock_object = new JSONObject(res).getJSONArray("SuccesModels").getJSONObject(i);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("OfferNo", mock_object.getString("OfferNo"));
                        map.put("OfferId", mock_object.getString("OfferId"));
                        map.put("OfferName", mock_object.getString("OfferName"));
                        map.put("ImageName", mock_object.getString("ImageName"));

                        array_offer.add(map);
                    }
                    Log.e("sizxe", "" + array_offer.size());

                    // page = array_offer.size() ;
                    adapter = new Offer_Adapter(getActivity(), array_offer);
                    pager.setAdapter(adapter);

                    handler.postDelayed(r, 3000);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       handler.removeCallbacks(r);
    }

}
