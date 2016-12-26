package com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.momskitchen.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hitesh.singh on 7/12/2016.
 */
public class Offer_Adapter extends PagerAdapter {

    private Context mContext;
    ArrayList<HashMap<String, String>> array_offer;
    AppPreferences mPrefs;
    ;

    public Offer_Adapter(Context context, ArrayList<HashMap<String, String>> array_offer) {
        this.mContext = context;
        this.array_offer = array_offer;
        mPrefs = AppPreferences.getAppPreferences(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        HashMap<String, String> map = new HashMap<String, String>();
        map = array_offer.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_offer_layout, collection, false);
        layout.setTag(position);
        final ImageView image = (ImageView) layout.findViewById(R.id.item_image_vp);

        Glide.with(mContext).load(map.get("ImageName")).asBitmap().placeholder(R.mipmap.app_icon).fitCenter().into(new BitmapImageViewTarget(image) {
            @Override
            protected void setResource(Bitmap resource) {
                image.setImageBitmap(resource);
            }
        });
        collection.addView(layout);
        return layout;
    }

        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

    @Override
    public int getCount() {
        return array_offer.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // ModelObject customPagerEnum = ModelObject.values()[position];
        return null;
    }
}
