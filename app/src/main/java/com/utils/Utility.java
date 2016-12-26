package com.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static Typeface getFontType(Context context, int num) {
        try {
            switch (num) {
                case 1:
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
                case 2:
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                default:
                    return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
    }


    public static boolean isNetworkConnected(Context context2) {
        ConnectivityManager conManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static String GetRequest(Context applicationContext, String url) {
        InputStream inputStream = null;
        Log.e("ur", url);
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            Log.e("asdf", "" + inputStream);
            // convert inputstream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.e("out", "" + result);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String convertInputStreamToString(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";


            while ((line = bufferedReader.readLine()) != null)
                result += line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getJson(List<NameValuePair> nameValuePairs,
                                 Context applicationContext, String url) {
        InputStream is = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            Log.e("", String.valueOf(nameValuePairs));
            Log.v("URL:", "......................................................");
            Log.e("URL:", url);
            Log.v("Parameters:", String.valueOf(nameValuePairs));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        try {
            if (is != null) {
                result = convertInputStreamToString(is);
                Log.e("Result:", result);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return result;
    }


    public static String GetDelete(Context applicationContext, String url) {
        String result = "";
        InputStream inputStream = null;
        Log.e("ur", url);

        try {
            URL url1 = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection) url1.openConnection();
            httpCon.setDoOutput(true);

            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            String temp = null;
            StringBuilder sb = new StringBuilder();
            while((temp = in.readLine()) != null){
                sb.append(temp).append(" ");
            }
            result = sb.toString();
            in.close();

          Log.e("log",result);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

}