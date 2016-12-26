package com.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.momskitchen.My_Cart;
import com.momskitchen.Product_Details;
import com.momskitchen.R;
import com.momskitchen.Shipping_Address;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.utils.Utility.convertInputStreamToString;


public class MyCart_Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, String>> array;
    private ArrayList<HashMap<String, String>> filter_array;
    AppPreferences mPrefs;

    public MyCart_Adapter(Context localContext, ArrayList<HashMap<String, String>> array) {
        this.mContext = localContext;
        this.array = array;
        this.filter_array = array;
        mPrefs = AppPreferences.getAppPreferences(mContext);
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View Itemview = null;
        final ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Itemview = inflater.inflate(R.layout.cart_item_layout, null);

        } else {
            Itemview = convertView;
        }

        holder.cir_image = (CircleImageView) Itemview.findViewById(R.id.profile_image);
        holder.txt_title = (TextView) Itemview.findViewById(R.id.item_name);
        holder.txt_quantity = (TextView) Itemview.findViewById(R.id.item_quntity);
        holder.txt_price = (TextView) Itemview.findViewById(R.id.textView_price);
        holder.txt_delet = (TextView) Itemview.findViewById(R.id.txtview_delt);
        holder.txt_delet.setTag(position);
        HashMap<String, String> map = new HashMap<String, String>();
        map = array.get(position);

        holder.txt_title.setText(map.get("name"));
        holder.txt_quantity.setText(map.get("quantity"));
/*
        String string_rate=(map.get("rate"));
        double rate = Double.parseDouble(string_rate);
        String string_quty=  (map.get("quantity"));
        double qty = Double.parseDouble(string_quty);
        double finalrate = rate*qty;
*/
        BigDecimal rate = new BigDecimal(map.get("rate"));
        BigDecimal qty = new BigDecimal(map.get("quantity"));
        BigDecimal finalrate = rate.multiply(qty);

        holder.txt_price.setText("" + finalrate);

        Glide.with(mContext).load(map.get("image")).asBitmap()
                .placeholder(R.mipmap.default_icon).fitCenter().into(new BitmapImageViewTarget(holder.cir_image) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.cir_image.setImageBitmap(resource);
            }
        });
        holder.txt_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vo = String.valueOf(view.getTag());
                int pos = Integer.parseInt(vo);
                HashMap<String, String> map = new HashMap<String, String>();
                map = array.get(pos);

                //String string_rate = (map.get("rate"));
                //String string_quty = (map.get("quantity"));

                BigDecimal rate = new BigDecimal(map.get("rate"));
                BigDecimal qty = new BigDecimal(map.get("quantity"));
                BigDecimal finalrate = rate.multiply(qty);

              /*  grandTotal = total.add(taxRate);
                float str=Float.parseFloat(""+grandTotal);
                int obj=(int)str;
                get(obj);
                //  grandTotal = grandTotal.add(Shipping_Address.charge);
                grandTotal = grandTotal.subtract(promoRtae);*/

                My_Cart.total = My_Cart.total.subtract(finalrate);

                //String string_tax = (map.get("taxRate"));
                BigDecimal tax_rate = new BigDecimal(map.get("taxRate"));
                My_Cart.taxRate = My_Cart.taxRate.subtract(tax_rate);

                My_Cart.grandTotal = My_Cart.total.add(My_Cart.taxRate);
                float str=Float.parseFloat(""+My_Cart.grandTotal);
                int obj=(int)str;
                getDeliveryAmt(obj);

                My_Cart.grandTotal = My_Cart.grandTotal.subtract(My_Cart.promoRtae);

                My_Cart.my_total();
                DBHandler db = new DBHandler(mContext);
                String tem_id = array.get(pos).get("id");

                int id = Integer.parseInt(tem_id);
                MyCart_property my = new MyCart_property();
                my.setId(id);
                db.deleteProduct(my);
                array.remove(pos);
                if (array.size() <= 0) {
                    My_Cart.footerView.setVisibility(View.GONE);
                    My_Cart.total = new BigDecimal("0.00");
                    My_Cart.grandTotal = new BigDecimal("0.00");
                    //   My_Cart.shipping_charge=new BigDecimal("0.00");
                    My_Cart.promoRtae = new BigDecimal("0.00");
                    My_Cart.taxRate = new BigDecimal("0.00");
                   /* mPrefs.putStringValue(AppPreferences.FlAG,"8");*/
                } else {
                    My_Cart.footerView.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();

                           /* String tem_id = array.get(i).get("id");
                            if (s_id.equalsIgnoreCase(tem_id)) {
                                String price = array.get(i).get("offer");
                                String pack = array.get(i).get("pack");
                                double pric = Double.parseDouble(price);

                                String qua = array.get(i).get("quantity");
                                int quant = Integer.parseInt(qua);
                                double cost;
                                if (pack.equalsIgnoreCase("")) {
                                    cost = pric * quant;
                                } else {
                                    float pc = Float.valueOf(pack);
                                    int packOf = (int) pc;
                                    quant = packOf * quant;
                                    cost = pric * quant;
                                }
                                double realcost = My_Cart.total;
                                My_Cart.total = (realcost - cost);

                                My_Cart.my_total();
                                array.remove(i);
                                DBHandler db = new DBHandler(mContext);
                                int id = Integer.parseInt(tem_id);
                                MyCart_property my = new MyCart_property();
                                my.setId(id);
                                db.deleteProduct(my);

                                String uID = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                                int coun = db.getCount(uID);
                                if(coun == 0){
                                    My_Cart.v_flag = 0;
                                }
                                break;
                            }*/
            }


        });

        return Itemview;
    }


    public void getDeliveryAmt(int value) {
        int rate = 0;
        if (value <= 250) {
            rate = 80;
        }
        if (value > 250 && value < 500) {
            rate = 100;
        } else if (value > 500 && value < 1000) {
            rate = 150;
        } else if (value > 1000 && value < 1500) {
            rate = 200;
        }
      My_Cart.  grandTotal=My_Cart.grandTotal.add(BigDecimal.valueOf(rate));
       My_Cart.homecharge.setText("Rs"+""+BigDecimal.valueOf(rate));

    }

    static class ViewHolder {
        public CircleImageView cir_image;
        public TextView txt_title;
        public TextView txt_quantity;
        public TextView txt_price;
        public TextView txt_delet;
    }
}

