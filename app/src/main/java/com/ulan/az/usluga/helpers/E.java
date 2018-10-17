package com.ulan.az.usluga.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by User on 04.08.2018.
 */

public class E {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FILTER_CATEGORY = "filter_category";
    public static final String APP_PREFERENCES_FILTER_SUBCATEGORY = "filter_subcategory";
    public static final String APP_PREFERENCES_FILTER_IS_CHECKED = "filter_is_checked";
    public static final String APP_PREFERENCES_NAME = "name";
    public static final String APP_PREFERENCES_ID = "id";
    public static final String APP_PREFERENCES_ID_CATEGORY = "id_category";
    public static final String APP_PREFERENCES_PHOTO = "photo";
    public static final String APP_PREFERENCES_PHONE = "phone";
    public static final String APP_PREFERENCES_AGE = "age";

    public static void addMarker(){

    }

    public static String getAppPreferences(String s,Context context){
       SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getString(s, "");
    }
    public static int getAppPreferencesINT(String s,Context context){
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getInt(s, -1);
    }
    public static boolean getAppPreferencesBoolean(String s,Context context){
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return mSettings.getBoolean(s,false);
    }

    public static void setAppPreferences(String s,Context context,String edit) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(s, edit);
        editor.apply();
    }

    public static void setAppPreferences(String s,Context context,Boolean edit) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(s, edit);
        editor.apply();
    }

    public static void setAppPreferences(String s,Context context,int edit) {
        SharedPreferences mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(s, edit);
        editor.apply();
    }



    public static String getPath(Uri uri,Context context)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static boolean checkInternetConection(Context context){
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnected()) {
                return true;
                // Internet Available
            }else {
                return false;
                //No internet
            }
        } else {
            return false;
            //No internet
        }
    }

}
