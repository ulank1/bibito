package com.ulan.az.usluga.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.MapActivity;
import com.ulan.az.usluga.MapViewO;
import com.ulan.az.usluga.Profile.RVMyServiceAdapter;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class ServiceMoreInfoActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;

    ArrayList<Service> serviceArrayList;

    RVAdditionalServiceAdapter adapter;

    DataHelper dataHelper;

    ClientApiListener listener;
    ProgressBar progressBar;
    Context context;
    TextView name;
    ImageView avatar,star;
    MapViewO mapView;
    Button call;
    Service service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_more_info);
        final DataHelper dataHelper = new DataHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        star = findViewById(R.id.star);


        name = findViewById(R.id.name);
        avatar = findViewById(R.id.avatar);
        TextView description = findViewById(R.id.desc);
        call = findViewById(R.id.btn_call);

        mapView = findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);


         service = (Service) getIntent().getSerializableExtra("service");
        getSupportActionBar().setTitle(service.getCategory());
        if (service.getDescription()!=null) {
            description.setText(service.getDescription());
        }
        mapView.getController().setCenter(service.getGeoPoint());
        addMarker(service.getGeoPoint(),0);
        name.setText(service.getUser().getName());

        star.setVisibility(View.VISIBLE);
        Glide.with(this).load("http://145.239.33.4:5555"+service.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                avatar.setImageDrawable(circularBitmapDrawable);
            }
        });
        if (dataHelper.isFavorite(service.getId()))
        Glide.with(this).load(R.drawable.ic_action_star_active).into(star);
        else Glide.with(this).load(R.drawable.ic_action_star_none_active).into(star);
        
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (dataHelper.isFavorite(service.getId())){
                    dataHelper.delete(service.getId());
                    Glide.with(ServiceMoreInfoActivity.this).load(R.drawable.ic_action_star_none_active).into(star);
                }else {
                    dataHelper.addService(service);
                    Glide.with(ServiceMoreInfoActivity.this).load(R.drawable.ic_action_star_active).into(star);
                }
            }
        });


        ImageView btnAddress = findViewById(R.id.btn_address);
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceMoreInfoActivity.this, MapActivity.class);
                intent.putExtra("lat",service.getGeoPoint().getLatitude());
                intent.putExtra("lon",service.getGeoPoint().getLongitude());
                startActivity(intent);
            }
        });


        context = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 1);

        FloatingActionButton btnAdd = findViewById(R.id.btn_add);
       /* btnAdd.setVisibility(View.VISIBLE);

        btnAdd.setClickable(true);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("eee","eee");
                startActivity(new Intent(context, AddServiceActivity.class));
            }
        });*/
        LinearLayoutManager llm = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {

                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            boolean bool = true;
                            JSONObject object = jsonArray.getJSONObject(i);
                            Service service = new Service();
                            service.setAddress(object.getString("address"));
                            if (!object.isNull("experience"))
                                service.setExperience(object.getDouble("experience"));
                            if (!object.isNull("lat"))
                                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            else service.setGeoPoint(new GeoPoint(0, 0));
                            service.setImage(object.getString("image"));
                            if (object.has("description"))
                                service.setDescription(object.getString("description"));
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category")+" -> "+object.getJSONObject("sub_category").getString("sub_category"));
                            service.setId(object.getInt("id"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setId(jsonUser.getInt("id"));
                            service.setUser(user);

                            serviceArrayList.add(service);

                        }
                        Shared.serviceCategories = serviceArrayList;
                        adapter = new RVAdditionalServiceAdapter(context, serviceArrayList);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                // things to do on the main thread
                                mRecyclerView.setAdapter(adapter);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ClientApi.requestGet(URLS.services + "&user=" + service.getUser().getId(), listener);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

    public void onClickPhone(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+" + service.getUser().getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }


    public void addMarker(GeoPoint geoPoint,int index) {

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        //startMarker.showInfoWindow();
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
    }





}
