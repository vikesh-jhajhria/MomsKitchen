package com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.momskitchen.R;
import com.utils.App_Constant;
import com.utils.MyProgressDialog;
import com.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vikesh on 26-Dec-16.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    ArrayList<MyCart_property> list;

    public CartAdapter(Context context, ArrayList<MyCart_property> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load(list.get(position).getImage()).asBitmap()
                .placeholder(R.mipmap.default_icon).fitCenter().into(new BitmapImageViewTarget(holder.cir_image) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.cir_image.setImageBitmap(resource);
            }
        });

        holder.txt_price.setText(list.get(position).getP_rate());
        holder.txt_title.setText(list.get(position).getP_name());
        holder.txt_quantity.setText(list.get(position).getOrder_qty());
        holder.txt_desc.setText(list.get(position).getDesc());
        holder.txt_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetOrderData().execute(position, list.get(position).getOrderId(), list.get(position).getClientId(), Integer.parseInt(list.get(position).getP_id()));
            }
        });


    }


    class GetOrderData extends AsyncTask<Integer, Void, String> {
        int selectedIndex;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyProgressDialog.show(context);
        }

        @Override
        protected String doInBackground(Integer... params) {
            selectedIndex = params[0];
            String res = null;
            JSONObject obj = null;
            try {
                obj = new JSONObject();
                obj.put("OrderId", params[1]);
                obj.put("ClientId", "" + params[2]);
                obj.put("ProductId", "" + params[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("JsonModel", "" + obj));
                res = Utility.getJson(nameValuePairs, context, App_Constant.CARD);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String res) {
            try {
                if (new JSONObject(res).getString("Msg").equalsIgnoreCase("Success")) {
                    list.remove(selectedIndex);
                    Toast.makeText(context, "Item removed successfully!", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyProgressDialog.close(context);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView cir_image;
        public TextView txt_title, txt_desc;
        public TextView txt_quantity;
        public TextView txt_price;
        public Button txt_delet;


        public ViewHolder(View itemView) {
            super(itemView);
            cir_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            txt_title = (TextView) itemView.findViewById(R.id.item_name);
            txt_quantity = (TextView) itemView.findViewById(R.id.item_quntity);
            txt_price = (TextView) itemView.findViewById(R.id.textView_price);
            txt_desc = (TextView) itemView.findViewById(R.id.item_description);
            txt_delet = (Button) itemView.findViewById(R.id.txtview_delt);
        }
    }
}
