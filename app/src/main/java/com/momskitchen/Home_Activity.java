package com.momskitchen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Vo.MyCart_property;
import com.utils.AppPreferences;
import com.utils.App_Constant;
import com.utils.DBHandler;

import java.util.List;

public class Home_Activity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {
    Context context = Home_Activity.this;
    NavigationView navigationView;
    AppPreferences mPrefs;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        mPrefs = AppPreferences.getAppPreferences(context);
        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((LinearLayout) findViewById(R.id.linear_logout)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.linear_about)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.linear_contact)).setOnClickListener(this);
        findViewById(R.id.ll_services).setOnClickListener(this);
        findViewById(R.id.linear_order).setOnClickListener(this);
        // ((ImageView) toolbar.findViewById(R.id.ic_search)).setOnClickListener(this);
        ((ImageView) toolbar.findViewById(R.id.ic_cart)).setOnClickListener(this);
        // mPrefs = AppPreferences.getAppPreferences(context);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nvView);
        View header = navigationView.getHeaderView(0);
        //Log.e("fdgdfdfg", App_Constant.User_Email);
        //App_Constant.User_Email = mPrefs.getStringValue(AppPreferences.U_NAME);
        //  ((TextView) header.findViewById(R.id.nav_title)).setText("" + App_Constant.User_Email);
        //hideItem();
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            Home_Category_Fragment fragment = new Home_Category_Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flContent, fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        cart_batch();

    }

    public void cart_batch() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DBHandler db = new DBHandler(Home_Activity.this);
        if ((mPrefs.getStringValue(AppPreferences.USER_NAME).equals(""))) {

        } else {
            String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            List<MyCart_property> shops = db.getAllProduct(U_id);
            if (shops.size() > 0) {
                ((TextView) toolbar.findViewById(R.id.cart_count)).setVisibility(View.VISIBLE);
                ((TextView) toolbar.findViewById(R.id.cart_count)).setText("" + shops.size());
            } else {
                ((TextView) toolbar.findViewById(R.id.cart_count)).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.linear_logout) {
            drawer.closeDrawer(Gravity.LEFT);
            mPrefs.putStringValue(AppPreferences.USER_NAME, "");
            mPrefs.putStringValue(AppPreferences.MEMBER_ID, "");
            mPrefs.putStringValue(AppPreferences.U_NAME, "");
            startActivity(new Intent(Home_Activity.this, Login_Activity.class));
            System.exit(0);
        }
        if(v.getId() == R.id.ll_services) {
            drawer.closeDrawer(Gravity.LEFT);
            App_Constant.Title = "Services";
            startActivity(new Intent(Home_Activity.this, ServicesActivity.class));
        }
        if(v.getId() == R.id.linear_order) {
            drawer.closeDrawer(Gravity.LEFT);
            App_Constant.Title = "Orders";
            startActivity(new Intent(Home_Activity.this, OrderCancleActivity.class));
        }

        if (v.getId() == R.id.linear_contact) {
            Log.e("contact", "in");
            drawer.closeDrawer(Gravity.LEFT);
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9929053285"));
                startActivity(intent);
            } catch (Exception e) {

            }
        }

        if (v.getId() == R.id.linear_about) {
            drawer.closeDrawer(Gravity.LEFT);
            Intent intent = new Intent(Home_Activity.this, AboutUsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.linear_locate) {
            drawer.closeDrawer(Gravity.LEFT);
        }
        if (v.getId() == R.id.ic_cart) {

            DBHandler db = new DBHandler(Home_Activity.this);
            String U_id = mPrefs.getStringValue(AppPreferences.MEMBER_ID);
            if (U_id.equalsIgnoreCase("")) {
                Toast.makeText(Home_Activity.this, "You are not Login User", Toast.LENGTH_SHORT).show();
            } else {
                List<MyCart_property> shops = db.getAllProduct(U_id);
                if (shops.size() > 0) {
                    startActivity(new Intent(Home_Activity.this, My_Cart.class));
                } else {
                    Toast.makeText(Home_Activity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int position = menuItem.getItemId();

      /*  switch (position) {
            case R.id.nav_logout:
                mPrefs.putStringValue(AppPreferences.USER_NAME, "");
                mPrefs.putStringValue(AppPreferences.MEMBER_ID, "");
                mPrefs.putStringValue(AppPreferences.U_NAME, "");
                startActivity(new Intent(Home_Activity.this, Login_Activity.class));
                finishAffinity();
                break;
            case R.id.nav_contact:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9929053285"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return true ;
                }
                startActivity(callIntent);

                break;
        }*/

        return false;
    }
}
