package com.ulan.az.usluga.service;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.MapActivity;
import com.ulan.az.usluga.MapViewO;
import com.ulan.az.usluga.PagerImageFragment;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.forum.Comment;
import com.ulan.az.usluga.forum.ForumInfoActivity;
import com.ulan.az.usluga.forum.RVCommentAdapter;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.AddOrderActivity;
import com.ulan.az.usluga.tender.TenderOrderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceMoreInfoActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;

    ArrayList<Service> serviceArrayList;

    RVAdditionalServiceAdapter adapter;

    DataHelper dataHelper;
    Button button;
    AlertDialog.Builder ad;
    ClientApiListener listener;
    ProgressBar progressBar;
    TextView name;
    ImageView avatar, star;
    MapViewO mapView;
    Button call;
    Service service;
    Timer timer;
    ViewPager pager;

    ImageView share, like, callPhone;
    TextView textLike, phone, address, title;
    int isLike = 0;
    int likeCount;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_more_info);
        context = this;
        final DataHelper dataHelper = new DataHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        star = findViewById(R.id.star);

        button = findViewById(R.id.btn_add);

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

        init();
        initReview();

        RelativeLayout relativeLayout = findViewById(R.id.relative);
        pager = (ViewPager) findViewById(R.id.viewpager);
        if (service.getImages() != null && service.getImages().size() > 0) {
            relativeLayout.setVisibility(View.VISIBLE);
            MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);

            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(pager);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final int pos;
                    if (pager.getCurrentItem() + 1 == service.getImages().size())
                        pos = 0;
                    else pos = pager.getCurrentItem() + 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(pos);
                        }
                    });
                }
            }, 10000, 10000);
        } else {

            ImageView imageView = findViewById(R.id.placeholder);
            imageView.setVisibility(View.VISIBLE);

        }

        getSupportActionBar().setTitle(getTextForTitle(service.getCategory()));
        Log.e("Desc", service.getDescription() + "");
        if (service.getDescription() != null) {
            description.setText(service.getInfo());
        } else description.setVisibility(View.GONE);


        mapView.getController().setCenter(service.getGeoPoint());
        addMarker(service.getGeoPoint(), 0);
        name.setText(service.getUser().getName());

        star.setVisibility(View.VISIBLE);
        Glide.with(this).load("http://145.239.33.4:5555" + service.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
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

                if (dataHelper.isFavorite(service.getId())) {
                    dataHelper.delete(service.getId());
                    Glide.with(ServiceMoreInfoActivity.this).load(R.drawable.ic_action_star_none_active).into(star);
                } else {
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
                intent.putExtra("lat", service.getGeoPoint().getLatitude());
                intent.putExtra("lon", service.getGeoPoint().getLongitude());
                startActivity(intent);
            }
        });


        context = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 1);

        // FloatingActionButton btnAdd = findViewById(R.id.btn_add);
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


        ad = new AlertDialog.Builder(this);
        ad.setTitle("Чтобы предложить задачу, вы должны создать задачу");  // заголовок
        ad.setMessage("Хотите добавить задачу");
        ad.setPositiveButton("да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                startActivity(new Intent(ServiceMoreInfoActivity.this, AddOrderActivity.class));
            }
        });
        ad.setNegativeButton("нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(false);

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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        ClientApi.requestGet(URLS.confirm_service + "&order=" + service.getId() + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ServiceMoreInfoActivity.this)), listener);
        if (service.getUser().getId() == E.getAppPreferencesINT(E.APP_PREFERENCES_ID, this)) {
            button.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(ServiceMoreInfoActivity.this,TenderOrderActivity.class).putExtra("service_id",service.getId()),6);

              /*  ClientApiListener listener = new ClientApiListener() {
                    @Override
                    public void onApiResponse(String id, String json, boolean isOk) {

                        if (!json.isEmpty() && isOk) {
                            try {
                                Log.e("LAN", json);
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                if (jsonArray.length() > 0) {

                                    final ClientApiListener listener1 = new ClientApiListener() {
                                        @Override
                                        public void onApiResponse(String id, String json, boolean isOk) {
                                            Log.e("DDD", json);
                                            if (isOk && json.isEmpty()) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        button.setVisibility(View.GONE);
                                                        Toast.makeText(ServiceMoreInfoActivity.this, "Отправлено", Toast.LENGTH_SHORT).show();
                                                        send();

                                                    }
                                                });
                                            }
                                        }
                                    };
                                    final MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("user", "/api/v1/users/" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ServiceMoreInfoActivity.this)) + "/")
                                            .addFormDataPart("order", "/api/v1/service/" + String.valueOf(service.getId()) + "/").build();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ClientApi.requestPostImage(URLS.confirm_service, req, listener1,ServiceMoreInfoActivity.this);

                                        }
                                    });

                                } else {
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
                Log.e("DDDDDD", "&sub_category__sub_category=" + service.getCategory().split(" -> ")[1].trim() + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ServiceMoreInfoActivity.this)));
                ClientApi.requestGet(URLS.order + "&sub_category__sub_category=" + service.getCategory().split(" -> ")[1].trim() + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ServiceMoreInfoActivity.this)), listener);*/
            }
        });


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
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category") + " -> " + object.getJSONObject("sub_category").getString("sub_category"));
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

    /*    ClientApiListener listener1 = new ClientApiListener() {
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
                            Log.e("ID",object.getInt("id")+"");
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
                            service.setCategory(object.getJSONObject("sub_category").getJSONObject("category").getString("category") + " -> " + object.getJSONObject("sub_category").getString("sub_category"));
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

                            Log.e("SHODHDJDKJDJDDIJ"," |||| "+user.getName()+" |||| "+service.getCategory());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ClientApi.requestGet(URLS.services, listener1);*/
    }


    private void init() {

        share = findViewById(R.id.share);
        title = findViewById(R.id.title);
        like = findViewById(R.id.like);
        callPhone = findViewById(R.id.btn_call_phone);
        textLike = findViewById(R.id.text_like);
        address = findViewById(R.id.location_info);
        phone = findViewById(R.id.phone_info);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = "Категория : " + service.getCategory() +
                        "\nTелефон : " + service.getUser().getPhone() +
                        "\nОписание : " + service.getDescription() + ""+
                        "\nПриложение можно скачать здесь: https://play.google.com/store/apps/details?id=com.ulan.az.usluga";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        address.setText(service.getAddress());
        phone.setText(service.getUser().getPhone());

        title.setText(service.getDescription());


        getLikes();


        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+" + service.getUser().getPhone()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike != 0) {

                    ClientApi.requestDelete1(URLS.like_service_put, isLike, context);
                    like.setImageResource(R.drawable.like_inactive);
                    likeCount--;
                    textLike.setText("Нравится " + likeCount);
                    isLike = 0;

                } else {
                    ClientApiListener listener1 = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("dddsssdcx", json);
                            if (json.isEmpty()) {
                                getLikes();
                            }

                        }
                    };

                    MultipartBody.Builder req = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    req.addFormDataPart("user_id_owner", E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context) + "");
                    req.addFormDataPart("type_id", service.getId() + "");
                    RequestBody requestBody = req.build();
                    ClientApi.requestPostImage(URLS.like_service_put, requestBody, listener1,ServiceMoreInfoActivity.this);

                }
            }
        });


    }


    RecyclerView recyclerView;
    EditText editSend;

    public void initReview() {
        getComments();
        RelativeLayout send;
        send = findViewById(R.id.btn_send );
        editSend = findViewById(R.id.edit_comment);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editSend.getText().toString().isEmpty()){

                    ClientApiListener listener = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("JSON",json);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getComments();
                                    editSend.setText("");
                                }
                            });
                        }
                    };

                    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
                    MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("service",service.getId()+"")
                            .addFormDataPart("name",service.getUser().getName()+"")
                            .addFormDataPart("author",String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,ServiceMoreInfoActivity.this))+"")
                            .addFormDataPart("comment",editSend.getText().toString()).build();

                    ClientApi.requestPostImage(URLS.comment_service,req,listener,ServiceMoreInfoActivity.this);

                }
            }
        });
    }


    public void getComments() {
        final ArrayList<Comment> comments  =new ArrayList<>();
        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("objects");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setComment(object.getString("comment"));
                        comment.setDate(E.parseDate(object.getString("updated_at")));
                        comment.setName(object.getString("name"));
                        comment.setAuthor(object.getInt("author"));
                        comments.add(comment);
                    }

                    final RVCommentAdapter adapter = new RVCommentAdapter(ServiceMoreInfoActivity.this,comments);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        ClientApi.requestGet(URLS.comment_service+"&service="+service.getId(),listener);


    }


    public void getLikes() {
        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                    likeCount = jsonArray.length();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textLike.setText("Нравится " + likeCount);

                        }
                    });
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id_own = E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context);
                        if (id_own == object.getInt("user_id_owner")) {
                            isLike = object.getInt("id");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    like.setImageResource(R.drawable.like_active);

                                }
                            });
                            break;
                        }
                        if (i == jsonArray.length() - 1) {
                            isLike = 0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    like.setImageResource(R.drawable.like_inactive);

                                }
                            });
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        ClientApi.requestGet(URLS.like_service + "&type_id=" + service.getId(), listener);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

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

    public void onClickCallPhone(View view) {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==6){
            Intent intent = getIntent();
            startActivity(intent);
            finish();
        }
    }

    public void onClickPhone(View view) {
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PagerImageFragment.newInstance(position, service.getImages().get(position));
        }

        @Override
        public int getCount() {
            return service.getImages().size();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    public String getTextForTitle(String s) {
        return s.substring(s.indexOf(">") + 1);
    }

}
