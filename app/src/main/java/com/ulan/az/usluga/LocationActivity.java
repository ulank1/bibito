package com.ulan.az.usluga;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.OrderMoreInfoActivity;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

public class LocationActivity extends AppCompatActivity {
    Context context;
    MapView mapView;
    ClientApiListener listener;
    ArrayList<Service> serviceArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Выберите Локацию для поиска");
        context = this;
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setCenter(new GeoPoint(Shared.location.getLatitude(), Shared.location.getLongitude()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();

        }

    }

    private void showGPSDisabledAlertToUser() {

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
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
                Shared.lat_search = mapView.getMapCenter().getLatitude();
                Shared.lon_search = mapView.getMapCenter().getLongitude();
                Intent intent = new Intent();
                intent.putExtra("lat", mapView.getMapCenter().getLatitude());
                intent.putExtra("lon", mapView.getMapCenter().getLongitude());
                setResult(RESULT_OK, intent);
                finish();


        }else {
            finish();
        }

        return true;
    }

}
