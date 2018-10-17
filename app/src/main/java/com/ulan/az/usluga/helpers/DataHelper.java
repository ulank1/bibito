package com.ulan.az.usluga.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ulan.az.usluga.User;
import com.ulan.az.usluga.service.Service;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by User on 08.08.2018.
 */

public class DataHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mydatabase.db";
    public static final String SERVICE = "service";
    public static final String SERVICE_CATEGORY = "service_category";
    public static final String SERVICE_ADDRESS = "service_address";
    public static final String SERVICE_EXPERIENCE = "service_experience";
    public static final String SERVICE_NAME = "service_name";
    public static final String SERVICE_PHONE = "service_phone";
    public static final String SERVICE_PHOTO = "service_photo";
    public static final String SERVICE_ID = "service_id";
    public static final String SERVICE_LAT = "service_lat";
    public static final String SERVICE_LON = "service_lon";


    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + SERVICE + "(" +
                 SERVICE_NAME+ " text," +
                 SERVICE_EXPERIENCE+ " integer," +
                 SERVICE_ADDRESS+ " text," +
                 SERVICE_CATEGORY+ " text," +
                 SERVICE_PHONE+ " text," +
                 SERVICE_PHOTO+ " text," +
                 SERVICE_LAT+ " double," +
                 SERVICE_LON+ " double," +
                SERVICE_ID + " integer);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addService(Service service){
        ContentValues contentValues = new ContentValues();

        contentValues.put(SERVICE_ID, service.getUser().getId());
        contentValues.put(SERVICE_LAT, service.getGeoPoint().getLatitude());
        contentValues.put(SERVICE_LON, service.getGeoPoint().getLongitude());
        contentValues.put(SERVICE_EXPERIENCE, service.getExperience());
        contentValues.put(SERVICE_CATEGORY, service.getCategory());
        contentValues.put(SERVICE_NAME, service.getUser().getName());
        contentValues.put(SERVICE_ADDRESS, service.getAddress());
        contentValues.put(SERVICE_PHONE, service.getUser().getPhone());


        SQLiteDatabase db = getWritableDatabase();

        db.insert(SERVICE, null, contentValues);
        db.close();
    }
    public ArrayList<Service> getService() {
        ArrayList<Service> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor =db.query(SERVICE, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                categories.add(getService(cursor));
            }
        }
        db.close();
        return categories;
    }

    public Service getService(Cursor cursor){
        Service service = new Service();
        User user = new User();
        service.setId(cursor.getInt(cursor.getColumnIndex(SERVICE_ID)));
        service.setImage(cursor.getString(cursor.getColumnIndex(SERVICE_PHOTO)));
        service.setCategory(cursor.getString(cursor.getColumnIndex(SERVICE_CATEGORY)));
        service.setExperience(cursor.getInt(cursor.getColumnIndex(SERVICE_EXPERIENCE)));
        service.setAddress(cursor.getString(cursor.getColumnIndex(SERVICE_ADDRESS)));
        service.setGeoPoint(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(SERVICE_LAT)),cursor.getDouble(cursor.getColumnIndex(SERVICE_LON))));
        user.setName(cursor.getString(cursor.getColumnIndex(SERVICE_NAME)));
        user.setPhone(cursor.getString(cursor.getColumnIndex(SERVICE_PHONE)));
        service.setUser(user);

        return service;
    }

    public boolean isFavorite(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(SERVICE, null, SERVICE_ID + " =? ", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.getCount()>0){
            return true;
        }else return false;
    }

    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SERVICE, SERVICE_ID + "=" + String.valueOf(id), null);
    }

}
