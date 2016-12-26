package com.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

	private SharedPreferences mPrefs;
	private static final String SHARED_PREFERENCE_NAME = "Quality";
	public static final String API_KEY ="APIKEY";
	public static final String MEMBER_ID="MEMBERID";
	public static final String USER_NAME = "USERNAME";
	public static final String U_NAME = "U_NAME";
	public static final String PNR_NUMBER = "pnr";
	public static final String CONTACT_NUMBER = "no";
	public static final String FlAG = "flag";
	public static final String IsVerify = "IsVerify";
	public static  final String PICK_FLAG = "pick_flag";

	private AppPreferences(Context context) {
		mPrefs = context.getSharedPreferences(SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	public static AppPreferences getAppPreferences(Context context) {
		return new AppPreferences(context);

	}

	public String getStringValue(String Key) {
		return mPrefs.getString(Key, "");
	}

	public void putStringValue(String Key, String value) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(Key, value);
		editor.commit();
	}

	public int getIntValue(String key) {
		return mPrefs.getInt(key, 0);
	}

	public void putIntValue(String key, int value) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public boolean getBooleanValue(String key) {

		return mPrefs.getBoolean(key, false);
	}

	public void putBooleanValue(String key, boolean value) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void removeFromPreferences(String key) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(key);
		editor.commit();
	}
}
