package com.ulan.az.usluga.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.DataHelper;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.AddServiceActivity;
import com.ulan.az.usluga.service.RVServiceAdapter;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MyServiceActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;

    ArrayList<Service> serviceArrayList;

    RVMyServiceAdapter adapter;

    DataHelper dataHelper;

    ClientApiListener listener;
    ProgressBar progressBar;
    Context context;
    ArrayList<ArrayList<User>> users;
    ArrayList<Service> orders;
    int a = 0;
    ClientApiListener listener1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_services);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

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

        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        orders = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Service service = new Service();
                            service.setAddress(object.getString("address"));
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
                            user.setId(jsonUser.getInt("id"));
                            user.setPhone(jsonUser.getString("phone"));

                            ArrayList<String> images = new ArrayList<>();
                            images.add(object.getString("image1"));
                            images.add(object.getString("image2"));
                            images.add(object.getString("image3"));
                            images.add(object.getString("image4"));
                            images.add(object.getString("image5"));

                            service.setImages(images);

                            service.setUser(user);
                            orders.add(service);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener1 = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {

                            Log.e("UUU",json);

                            ArrayList<User> users1 = new ArrayList<>();

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(json);


                                JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    User user = new User();
                                    JSONObject jsonUser = object.getJSONObject("user");
                                    user.setId(jsonUser.getInt("id"));
                                    user.setId_confirm(object.getInt("id"));
                                    user.setAge(jsonUser.getString("age"));
                                    user.setImage(jsonUser.getString("image"));
                                    user.setName(jsonUser.getString("name"));
                                    user.setPhone(jsonUser.getString("phone"));
                                    user.setDeviceId(jsonUser.getString("device_id"));
                                    users1.add(user);

                                }
                                users.add(users1);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            a++;
                            if (a == orders.size()) {
                                adapter = new RVMyServiceAdapter(context,orders,users);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setAdapter(adapter);

                                    }
                                });
                            } else {
                                ClientApi.requestGet(URLS.confirm_service + "&order=" + orders.get(a).getId(), listener1);
                            }

                        }
                    };


                    users = new ArrayList<>();
                    if (orders.size()>0)
                        ClientApi.requestGet(URLS.confirm_service + "&order=" + orders.get(a).getId()+"&status=1", listener1);

                }
            }
        };

        ClientApi.requestGet(URLS.services + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context)), listener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
