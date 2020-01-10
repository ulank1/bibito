package com.ulan.az.usluga.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.BuildConfig;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;

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

public class ServiceMapActivity extends AppCompatActivity implements LocationListener {
    Context context;
    MapView mapView;
    ClientApiListener listener;
    ArrayList<Service> serviceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_map);
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Услуги");
        context = this;
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        if (Shared.lat_search == 0) {
            mapView.getController().setCenter(new GeoPoint(42.8629, 74.6059));
        } else {
            mapView.getController().setCenter(new GeoPoint(Shared.lat_search, Shared.lon_search));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("Loh", "Loh");
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10, 0, this);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 10, 0,
                    this);
            Log.e("Loh", "ne_loh");
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                showGPSDisabledAlertToUser();
            }
        }

        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                if (isOk) {
                    try {
                        Log.e("FFFF", json);
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            final Service service = new Service();
                            service.setAddress(object.getString("address"));
                            service.setExperience(object.getDouble("experience"));
                            if (!object.isNull("lat"))
                                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            else service.setGeoPoint(new GeoPoint(0, 0));
                            service.setImage(object.getString("image"));
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category") + " -> " + object.getJSONObject("sub_category").getString("sub_category"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            service.setUser(user);
                            serviceArrayList.add(service);
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Shared.is_search) {
                                        String s = service.getDescription().toLowerCase();
                                        if (s.contains(Shared.search_text.toLowerCase())) {
                                            addMarker(service.getGeoPoint(), finalI);
                                        }
                                    }else {
                                        addMarker(service.getGeoPoint(), finalI);
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED, context)) {
            Log.e("Category_id", Shared.category_id + "");
            ClientApi.requestGet(URLS.services + "&sub_category=" + Shared.category_id, listener);
        } else ClientApi.requestGet(URLS.services, listener);


    }
    private void showGPSDisabledAlertToUser() {

        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void addMarker(GeoPoint geoPoint, int index) {

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        ServiceInfoWindow serviceInfoWindow = new ServiceInfoWindow(R.layout.info_service, mapView, index);
        startMarker.setInfoWindow(serviceInfoWindow);
        //startMarker.showInfoWindow();
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
    }

    @Override
    public void onLocationChanged(Location location) {
        Shared.lat_search = location.getLatitude();
        Shared.lon_search = location.getLongitude();
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

    public class ServiceInfoWindow extends InfoWindow {

        int index;

        public ServiceInfoWindow(int layoutResId, MapView mapView, int index) {
            super(layoutResId, mapView);
            this.index = index;
        }

        @Override
        public void onOpen(Object item) {
            final ImageView avatar = mView.findViewById(R.id.avatar);
            TextView name = mView.findViewById(R.id.name);
            final Button btn = mView.findViewById(R.id.btn_call);
            final ImageView clear = mView.findViewById(R.id.clear);
            TextView desc = mView.findViewById(R.id.desc);

            final Service service = serviceArrayList.get(index);
            desc.setText(service.getDescription());
            name.setText(service.getUser().getName());
            Glide.with(context).load("http://145.239.33.4:5555" + service.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    avatar.setImageDrawable(circularBitmapDrawable);
                }
            });
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ServiceMoreInfoActivity.class).putExtra("service", service));

                }
            });


        }

        @Override
        public void onClose() {

        }
    }
}
