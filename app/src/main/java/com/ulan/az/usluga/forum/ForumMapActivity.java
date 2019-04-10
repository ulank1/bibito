package com.ulan.az.usluga.forum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.service.ServiceMoreInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;

public class ForumMapActivity extends AppCompatActivity {
    Context context;
    MapView mapView;
    ClientApiListener listener;
    ArrayList<Forum> serviceArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Услуги");
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
                //Log.e("SSSS", "SSSS");
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            final Forum forum = new Forum();

                            forum.setCount(object.getInt("count"));
                            forum.setId(object.getInt("id"));
                            forum.setDescription(object.getString("description"));
                            forum.setImage(object.getString("image"));
                            forum.setTitle(object.getString("title"));
                            String date = object.getString("updated_at");

                            if (!object.isNull("address"))
                                forum.setAddress(object.getString("address"));

                            if (!object.isNull("lat"))
                                forum.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));

                            forum.setDate(E.parseDate(date));

                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setId(jsonUser.getInt("id"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setDeviceId(jsonUser.getString("device_id"));
                            forum.setUser(user);
                            serviceArrayList.add(forum);
                            final int finalI = i;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (forum.getGeoPoint()!=null)
                                    addMarker(forum.getGeoPoint(), finalI);
                                }
                            });
                        }
                        //Log.e("size", serviceArrayList.size() + "");


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        ClientApi.requestGet(URLS.forum, listener);


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

            final Forum service = serviceArrayList.get(index);
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
                    startActivity(new Intent(context, ForumInfoActivity.class).putExtra("forum", service));

                }
            });


        }

        @Override
        public void onClose() {

        }
    }
}
