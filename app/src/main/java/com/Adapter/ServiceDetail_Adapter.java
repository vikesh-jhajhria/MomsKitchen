package com.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.momskitchen.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hitesh.singh on 6/22/2016.
 */
public class ServiceDetail_Adapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, String>> array;
    private ArrayList<HashMap<String, String>> filter_array;
    ProductFilter filter;

    public ServiceDetail_Adapter(Context localContext, ArrayList<HashMap<String, String>> array) {
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
            Itemview = inflater.inflate(R.layout.item_service_detail, null);

        } else {
            Itemview = convertView;
        }
        Itemview.setTag(position);
        holder.item_image = (ImageView) Itemview.findViewById(R.id.item_product);

        HashMap<String, String> map = new HashMap<String, String>();
        map = array.get(position);
        //Glide.with(mContext).load(map.get("image")).asBitmap().into(new BitmapImageViewTarget(holder.item_image) );


        Glide.with(mContext)
                .load(map.get("image"))
                .asBitmap()
                .placeholder(R.mipmap.app_icon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        // you can do something with loaded bitmap here

                        // .....
                        ViewGroup.LayoutParams lp = holder.item_image.getLayoutParams();
                        lp.width = ((LinearLayout)holder.item_image.getParent()).getWidth();
                        lp.height = resource.getHeight()*(((LinearLayout)holder.item_image.getParent()).getWidth()/resource.getWidth());
                        holder.item_image.setLayoutParams(lp);

                        holder.item_image.setImageBitmap(resource);
                    }
                });
        return Itemview;
    }

    static class ViewHolder {
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
