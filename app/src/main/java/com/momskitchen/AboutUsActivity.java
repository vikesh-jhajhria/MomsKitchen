package com.momskitchen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;

import java.util.List;

/**
 * Created by saloni.bhansali on 11/23/2016.
 */
public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView title;
    AppPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus_activity);
        mPrefs = AppPreferences.getAppPreferences(AboutUsActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title=(TextView)findViewById(R.id.tv_title);
        title.setText("About Us");
        ((TextView) findViewById(R.id.tv_marque)).setText("" + App_Constant.Advt_title);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ic_cart).setOnClickListener(this);
        ((TextView) findViewById(R.id.txt_info)).setText("Welcome to Mom's Kitchen , a new way to order food online and get the order that you really want at prices you like! Well, the price and the quality will be much better than anywhere else you can find on the internet or in a other res tau's.\n" +
                "\n" +
                " More often than not, we will be bringing you products that aren't even available in at your favorite restaurant. Just because our prices are amazing, doesn't mean the quality of the products we sell is also cheap!We have an unbelievable network of suppliers that enable us to bring you great deals.\n"+" \n"+"ADDRESS.\n"+"\n"+"Mom's Kitchen ,Plot No. 40-A \n" +
                "opp. Rajpurohit Hostel, Madan ji's chakki,\n" +
                "9th 'E' road Sardarpura, JODHPUR(Raj.)\n" +
                "Pin code - 342003.\n"+" \n"+"Email : momskitchenjodhpur@gmail.com\n"+"\n"+
                "info@momskitchenjodhpur.com\n" +"\n"+
                "Support :support@momskitchenjodhpur.com\n" +"\n"+
                "Sales : sales@momskitchenjodhpur.com.\n"+" ");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ic_cart:
                DBHandler db = new DBHandler(AboutUsActivity.this);
                String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
                if (U_id.equalsIgnoreCase("")) {
                    Toast.makeText(AboutUsActivity.this, "You are not Login User", Toast.LENGTH_SHORT).show();
                } else {
                    List<MyCart_property> shops = db.getAllProduct(U_id);
                    if (shops.size() > 0) {
                        startActivity(new Intent(AboutUsActivity.this, My_Cart.class));
                    } else {
                        Toast.makeText(AboutUsActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }
}
