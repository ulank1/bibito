package com.ulan.az.usluga.order;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ulan.az.usluga.R;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;

public class AddressActivity extends AppCompatActivity implements LocationListener {
    MapView mapView;
    EditText address;
    ProgressBar progressBar;

    LocationManager locationManager;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        address = findViewById(R.id.address);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setCenter(new GeoPoint(42.8629, 74.6059));
        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {


                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                MapView mapView = (MapView) findViewById(R.id.mapView);

                reverseGeocoding(mapView.getMapCenter().getLatitude(),mapView.getMapCenter().getLongitude());

                return true;
            }
        }, 1000));



    }

     LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here





        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Включить Gps")
                    .setCancelable(false)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1 * 1000, 30,
                this);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1*1000, 30,
                this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();

        }
    }

    private void showGPSDisabledAlertToUser() {

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }

    public void reverseGeocoding(final double lat, final double lon) {
        progressBar.setVisibility(View.VISIBLE);
        address.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GeocoderNominatim geocoder = new GeocoderNominatim("ulankarimovv@gmail.com");
                try {
                    if (geocoder.getFromLocation(lat, lon,1).size()>0) {
                        Address adress = geocoder.getFromLocation(lat, lon, 1).get(0);
                        final String finalAddressFull = adress.getExtras().get("display_name").toString();
                        String[] s = finalAddressFull.split(",");
                        final StringBuilder address1 = new StringBuilder();

                        if (adress.getPostalCode()!=null) {
                            for (String value : s) {
                                if (!value.contains(adress.getPostalCode())) {
                                    address1.append(value).append(",");
                                }
                            }
                        }else {
                            address1.append(finalAddressFull).append(",");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                address.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                address.setText(address1.toString().substring(0,address1.length()-1));
                            }
                        });


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        if (id==R.id.action_search) {
            if (progressBar.getVisibility() == View.GONE && !address.getText().toString().isEmpty()) {

                Intent intent = new Intent();
                intent.putExtra("address", address.getText().toString());
                intent.putExtra("lat", mapView.getMapCenter().getLatitude());
                intent.putExtra("lon", mapView.getMapCenter().getLongitude());
                setResult(RESULT_OK, intent);
                finish();

            } else {

                Toast.makeText(this, "Подождите!", Toast.LENGTH_SHORT).show();

            }
        }else {
            finish();
        }

        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("sd","sdsadsa");
        mapView.getController().setCenter(new GeoPoint(location.getLatitude(),location.getLongitude()));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
