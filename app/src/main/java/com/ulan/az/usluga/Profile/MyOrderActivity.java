package com.ulan.az.usluga.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MyOrderActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<ArrayList<User>> users;
    ArrayList<Service> orders;
    int a = 0;
    ClientApiListener listener1;
    RVMyOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        getSupportActionBar().setTitle("Мои задачи");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);


        LinearLayoutManager llm = new LinearLayoutManager(this);
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
                            user.setPhone(jsonUser.getString("phone"));
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
                                adapter = new RVMyOrderAdapter(MyOrderActivity.this,orders,users);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setAdapter(adapter);

                                    }
                                });
                            } else {
                                ClientApi.requestGet(URLS.confirm + "&order=" + orders.get(a).getId(), listener1);
                            }

                        }
                    };


                    users = new ArrayList<>();
                    if (orders.size()>0)
                    ClientApi.requestGet(URLS.confirm + "&order=" + orders.get(a).getId()+"&status=1", listener1);

                }
            }
        };

        ClientApi.requestGet(URLS.order + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, MyOrderActivity.this)), listener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
