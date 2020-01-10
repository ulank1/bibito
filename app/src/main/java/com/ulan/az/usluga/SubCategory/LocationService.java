package com.ulan.az.usluga.SubCategory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.ulan.az.usluga.helpers.Shared;

public class LocationService extends Service implements LocationListener {
    public LocationService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SUKA", "SUKA");
        Shared.location = new Location("Loc");
        Shared.location.setLatitude(42.8629);
        Shared.location.setLongitude(74.6059);

        LocationManager locationManager;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Loh", "Loh");
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10, 0, this);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 10, 0,
                this);
        Log.e("Loh", "ne_loh");


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();

        }

    }

    private void showGPSDisabledAlertToUser() {

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("SUKA", "SUKA");
        Shared.location = new Location("Loc");
        Shared.location.setLatitude(42.8629);
        Shared.location.setLongitude(74.6059);

        LocationManager locationManager;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Loh", "Loh");
            return super.onStartCommand(intent, flags, startId);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10, 0, this);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 10, 0,
                this);
        Log.e("Loh", "ne_loh");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Shared.location = location;
        Log.e("LOCATION", location.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
