package com.ulan.az.usluga.tender.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.order.AddOrderActivity;
import com.ulan.az.usluga.service.AddServiceActivity;
import com.ulan.az.usluga.service.Service;
import com.ulan.az.usluga.tender.RVTenderOrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class TenderServiceActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<ArrayList<User>> users;
    ArrayList<Service> orders;
    int a = 0;
    ClientApiListener listener1;
    RVTenderServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tender_order_activity);
        getSupportActionBar().setTitle("Выберите Услуги");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);
        Button btn = findViewById(R.id.add_order);

        btn.setText("создать новую услуги");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(TenderServiceActivity.this, AddServiceActivity.class),3);
            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        getOrder();
    }

    void getOrder(){
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
                            user.setId(jsonUser.getInt("id"));
                            user.setName(jsonUser.getString("name"));
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
                            adapter = new RVTenderServiceAdapter(TenderServiceActivity.this,orders,getIntent().getIntExtra("service_id",1));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.setAdapter(adapter);

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        ClientApi.requestGet(URLS.services + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, TenderServiceActivity.this)), listener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3){
            getOrder();
        }

    }
}
