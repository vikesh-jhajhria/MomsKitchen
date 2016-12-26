package com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.momskitchen.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hitesh.singh on 6/22/2016.
 */
public class ProductList_Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, String>> array;
    private ArrayList<HashMap<String, String>> filter_array;
    ProductFilter filter;

    public ProductList_Adapter(Context localContext, ArrayList<HashMap<String, String>> array) {
        this.mContext = localContext;
        this.array = array;
        this.filter_array = array;
        getFilter();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View Itemview = null;
        final ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Itemview = inflater.inflate(R.layout.item_home_deliver, null);

        } else {
            Itemview = convertView;
        }
        Itemview.setTag(position);
        holder.item_image = (ImageView) Itemview.findViewById(R.id.item_product);
        holder.txt_title = (TextView) Itemview.findViewById(R.id.item_name);

        HashMap<String, String> map = new HashMap<String, String>();
        map = array.get(position);
        holder.txt_title.setText(map.get("name"));
        Glide.with(mContext).load(map.get("image")).asBitmap().placeholder(R.mipmap.app_icon).fitCenter().into(new BitmapImageViewTarget(holder.item_image) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.item_image.setImageBitmap(resource);
            }
        });

       /* holder.item_image = (ImageView) Itemview.findViewById(R.id.item_product);
        holder.txt_title = (TextView) Itemview.findViewById(R.id.item_name);
        holder.txt_price = (TextView) Itemview.findViewById(R.id.tv_p_price);
       // holder.txt_unit = (TextView) Itemview.findViewById(R.id.tv_unit);


        HashMap<String, String> map = new HashMap<String, String>();
        map = array.get(position);
        holder.txt_title.setText(map.get("name"));
        String quan = map.get("quantity");
        String weight_type = map.get("weight");
        holder.txt_price.setText("₹ " + map.get("price") + "/-" + " (" + quan + " " + weight_type + " )");
     //   holder.txt_unit.setText("" + quan + " unit ( " + weight_type + " )");
        Glide.with(mContext).load(map.get("image")).asBitmap().placeholder(R.mipmap.logo).fitCenter().into(new BitmapImageViewTarget(holder.item_image) {
            @Override
            protected void setResource(Bitmap resource) {
                holder.item_image.setImageBitmap(resource);
            }
        });
       */
        return Itemview;
    }

    static class ViewHolder {
        public TextView txt_title, txt_price, txt_unit;
        public ImageView item_image;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new ProductFilter();
        }
        return filter;
    }

    private class ProductFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<HashMap<String, String>> filteredItems = new ArrayList<HashMap<String, String>>();
                for (int i = 0, l = array.size(); i < l; i++) {
                    HashMap<String, String> p = array.get(i);
                    if (p.toString().toLowerCase().contains(constraint)) {
                        filteredItems.add(p);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                result.values = filter_array;
                result.count = filter_array.size();
                //notifyDataSetChanged();
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else
                array = (ArrayList<HashMap<String, String>>) results.values;
            notifyDataSetChanged();
        }
    }
}
