package com.ulan.az.usluga.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

public class OrderMapActivity extends AppCompatActivity {
    Context context;
    MapView mapView;
    ClientApiListener listener;
    ArrayList<Service> serviceArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Задачи");
        context = this;
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getController().setCenter(new GeoPoint(42.8629, 74.6059));



        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                if (isOk) {
                    try {
                        Log.e("JSON",json);
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            final Service service = new Service();
                            service.setAddress(object.getString("address"));
                            if (!object.isNull("lat"))
                                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            else service.setGeoPoint(new GeoPoint(0,0));                            service.setImage(object.getString("image"));
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            service.setCategory(object.getJSONObject("sub_category").getString("sub_category"));
                            Shared.category_id_order =object.getJSONObject("sub_category").getInt("id");
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

                                    addMarker(service.getGeoPoint(), finalI);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED,context))
            ClientApi.requestGet(URLS.order+"sub_category="+ Shared.category_id,listener);
        else         ClientApi.requestGet(URLS.order,listener);

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
            final TextView btn = mView.findViewById(R.id.btn_call);
            final ImageView clear = mView.findViewById(R.id.clear);
            TextView desc = mView.findViewById(R.id.desc);

            final Service service = serviceArrayList.get(index);
            desc.setText(service.getDescription());
            name.setText(service.getUser().getName());
            Glide.with(context).load("http://145.239.33.4:5555"+service.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
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
                    startActivity(new Intent(context, OrderMoreInfoActivity.class).putExtra("service", service));

                }
            });


        }

        @Override
        public void onClose() {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
