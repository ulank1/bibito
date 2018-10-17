package com.ulan.az.usluga.order;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.MapActivity;
import com.ulan.az.usluga.MapViewO;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.AddServiceActivity;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderMoreInfoActivity extends AppCompatActivity {

    TextView name;
    ImageView avatar;
    Button button,buttonCall;
    AlertDialog.Builder ad;
    Service service;
    MapViewO mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_more_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name);
        avatar = findViewById(R.id.avatar);
        button = findViewById(R.id.btn_add);
        buttonCall = findViewById(R.id.btn_call);
        TextView description = findViewById(R.id.desc);
        mapView = findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        ad = new AlertDialog.Builder(this);
        ad.setTitle("Чтобы предложить услугу, вы должны создать услугу");  // заголовок
        ad.setMessage("Хотите добавить услугу");
        ad.setPositiveButton("да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(OrderMoreInfoActivity.this, AddServiceActivity.class));
            }
        });
        ad.setNegativeButton("нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(false);
        service = (Service) getIntent().getSerializableExtra("service");
        mapView.getController().setCenter(service.getGeoPoint());
        addMarker(service.getGeoPoint(),0);
        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(final String id, String json, boolean isOk) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                    if (jsonArray.length() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button.setVisibility(View.GONE);
                            }
                        });
                    }

                    } catch(JSONException e){
                        e.printStackTrace();
                    }

            }
        };

        ClientApi.requestGet(URLS.confirm+"&order="+service.getId()+"&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, OrderMoreInfoActivity.this)),listener);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientApiListener listener = new ClientApiListener() {
                    @Override
                    public void onApiResponse(String id, String json, boolean isOk) {

                        if (!json.isEmpty()&&isOk){
                            try {
                                Log.e("LAN",json);
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                if (jsonArray.length()>0){

                                    ClientApiListener listener1 = new ClientApiListener() {
                                        @Override
                                        public void onApiResponse(String id, String json, boolean isOk) {
                                            if (isOk && json.isEmpty()) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        button.setVisibility(View.GONE);
                                                        Toast.makeText(OrderMoreInfoActivity.this, "Отправлено", Toast.LENGTH_SHORT).show();
                                                        send();

                                                    }
                                                });
                                            }
                                        }
                                    };
                                    MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("user", "/api/v1/users/" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, OrderMoreInfoActivity.this)) + "/")
                                            .addFormDataPart("order", "/api/v1/order/" + String.valueOf(service.getId()) + "/").build();

                                    ClientApi.requestPostImage(URLS.confirm, req, listener1);

                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            ad.show();
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                };
                Log.e("DDDDDD","&sub_category="+Shared.category_id+"&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, OrderMoreInfoActivity.this)));
                ClientApi.requestGet(URLS.services+"&sub_category="+Shared.category_id_order+"&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, OrderMoreInfoActivity.this)),listener);
            }
        });


        getSupportActionBar().setTitle(service.getCategory());
        if (service.getDescription() != null) {
            description.setText(service.getDescription());
        }
        name.setText(service.getUser().getName() + "");

        Glide.with(this).load("http://145.239.33.4:5555"+service.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                avatar.setImageDrawable(circularBitmapDrawable);
            }
        });
        ImageView btnAddress = findViewById(R.id.btn_address);
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderMoreInfoActivity.this, MapActivity.class);
                intent.putExtra("lat",service.getGeoPoint().getLatitude());
                intent.putExtra("lon",service.getGeoPoint().getLongitude());
                startActivity(intent);
            }
        });


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

    public String getJson() {
        JSONObject jsonObject = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("title", "Задачи");
            notification.put("body", "предлагают свою услугу");
            notification.put("id", 1);
            data.put("title", "Задачи");
            data.put("body", "предлагают свою услугу");
            data.put("id", 1);
            jsonObject.put("notification", notification);
            jsonObject.put("data", data);
            jsonObject.put("to", service.getUser().getDeviceId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void send() {

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, getJson());
        Request request = new Request.Builder()
                .header("Authorization", Shared.key)
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e("Error", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("RESPONSE_Finish", json);


            }
        });
    }

    public void addMarker(GeoPoint geoPoint, int index) {

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(geoPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        //startMarker.showInfoWindow();
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_action_location));
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();
    }

}
