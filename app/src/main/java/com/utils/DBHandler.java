package com.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.Vo.MyCart_property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hitesh.singh on 7/6/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;
    // Database Name
    private static final String DATABASE_NAME = "AddCartInfo";
    // Contacts table name
    private static final String TABLE_SHOPS = "mycart";

    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PNAME = "pname";
    private static final String KEY_PRATE = "rate";
    private static final String KEY_CID = "cid";
    private static final String KEY_BID = "bid";
    private static final String KEY_DELIVERY = "delivery";
    private static final String KEY_DEC = "desc";
    private static final String KEY_ORDER = "order_qty";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_PROMO = "promo_code";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PID = "pid";
    private static final String KEY_UID = "user_id";
   private static final String Key_TAX_ID = "tax_id";
    private static final String Key_TAX_CHARGE = "tax_charge";
    private static final String KEY_FOOD = "foodId";
    private static final String KEY_SubFood = "subFoodId";
    private static final String Key_Regular = "regular";
    private static final String Key_Spice = "spice";
    private static final String Type_value = "type_value";





    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SHOPS + "("
                + " id INTEGER PRIMARY KEY,"
                + KEY_PNAME + " TEXT,"
                + KEY_CID + " TEXT,"
                + KEY_BID + " TEXT,"
                + KEY_DEC + " TEXT,"
                + KEY_PRATE + " TEXT,"
                + KEY_ORDER + " TEXT,"
                + KEY_QUANTITY + " TEXT,"
                + KEY_DELIVERY + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_PID + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_PROMO + " TEXT,"
                + Key_TAX_ID + " TEXT,"
                + Key_TAX_CHARGE + " TEXT,"
                + KEY_FOOD + " TEXT,"
                + KEY_SubFood + " TEXT,"
                + Key_Regular + " TEXT,"
                + Key_Spice+ " TEXT,"
                + Type_value+ " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPS);

        // Creating tables again
        onCreate(db);
    }

    // Adding new product details
    public void addProduct(MyCart_property property) {
        Log.e("",""+property);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PNAME, property.getP_name()); // Product name
        values.put(KEY_CID, property.getCategory_id()); // Product Category ID
        values.put(KEY_BID, property.getBrand_id()); // Product brand ID
        values.put(KEY_DEC, property.getDesc()); // Product rate
        values.put(KEY_PRATE, property.getP_rate());
        values.put(KEY_ORDER, property.getOrder_qty()); // Product order qty
        values.put(KEY_QUANTITY, property.getQuantity()); // Product quantity
        values.put(KEY_DELIVERY, property.getDelivery());
        values.put(KEY_IMAGE, property.getImage()); // Product delivery text
        values.put(KEY_PID, property.getP_id()); //product ID
        values.put(KEY_UID, property.getU_id()); //user ID
        values.put(KEY_PROMO, property.getPromo());
        values.put(Key_TAX_ID, property.getTax_id());
        values.put(Key_TAX_CHARGE, property.getTax_charge());
        values.put(KEY_FOOD, property.getFoodId());
        values.put(KEY_SubFood, property.getSubFoodId());
        values.put(Key_Regular, property.getRegular());
        values.put(Key_Spice, property.getSpice());
        values.put(Type_value, property.getTypevalue());
        //user ID
        // Inserting Row


        db.insert(TABLE_SHOPS, null, values);

        db.close(); // Closing database connection
    }

    // Getting All Product
    public List<MyCart_property> getAllProduct(String u_id) {
        List<MyCart_property> shopList = new ArrayList<MyCart_property>();
        String selectQuery = "" ;
        // Select All Query

            selectQuery = "SELECT * FROM " + TABLE_SHOPS + " where " + KEY_UID + "="+u_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MyCart_property shop = new MyCart_property();
                shop.setId(cursor.getInt(0));
                shop.setP_name(cursor.getString(1));
                shop.setCategory_id(cursor.getString(2));
                shop.setBrand_id(cursor.getString(3));
                shop.setDesc(cursor.getString(4));
                shop.setP_rate(cursor.getString(5));
                shop.setOrder_qty(cursor.getString(6));
                shop.setQuantity(cursor.getString(7));
                shop.setDelivery(cursor.getString(8));
                shop.setImage(cursor.getString(9));
                shop.setP_id(cursor.getString(10));
                shop.setU_id(cursor.getString(11));
                shop.setPromo(cursor.getString(12));
                shop.setTax_id(cursor.getString(13));
                shop.setTax_charge(cursor.getString(14));
                shop.setFoodId(cursor.getString(15));
                shop.setSubFoodId(cursor.getString(16));
                shop.setRegular(cursor.getString(17));
                shop.setSpice(cursor.getString(18));
                shop.setTypevalue(cursor.getString(19));



                Log.e("member ID is  : ",""+cursor.getString(12));
                // Adding contact to list
                shopList.add(shop);
            } while (cursor.moveToNext());
        }
        // return contact list
        return shopList;
    }

    // Deleting a shop
    public void deleteProduct(MyCart_property shop) {
        SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_SHOPS, KEY_ID + " = ?", new String[]{String.valueOf(shop.getId())});

            db.close();
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_SHOPS, null, null);

            db.close();
        }



 public int getCount(String name) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {


            String query = "" ;

                query =  "select count(*) from " + TABLE_SHOPS + " where " + KEY_UID + " = ?";


            c = db.rawQuery(query, new String[]{name});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public int getCountProduct(String name) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {


            String query = "" ;

                query =  "select count(*) from " + TABLE_SHOPS + " where " + KEY_PNAME + " = ?";


            c = db.rawQuery(query, new String[]{name});
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


}
