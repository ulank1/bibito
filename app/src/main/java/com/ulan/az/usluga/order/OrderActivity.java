package com.ulan.az.usluga.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ulan.az.usluga.R;
import com.ulan.az.usluga.helpers.ViewPagerAdapter;

public class OrderActivity extends AppCompatActivity {

    ViewPager viewPager;
    private TabLayout tabLayout;
    FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fm = getSupportFragmentManager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton btnAdd = findViewById(R.id.btn_add);

        btnAdd.setClickable(true);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("eee","eee");
                startActivity(new Intent(OrderActivity.this,AddOrderActivity.class));
            }
        });


        getSupportActionBar().setTitle(getIntent().getStringExtra("category"));

       /* mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);


        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);


        String json = "{\"meta\": {\"limit\": 0, \"offset\": 0, \"total_count\": 1}, \"objects\": [{\"address\": \"dsfsdf\", \"category\": {\"category\": \"dsfs\", \"id\": 1, \"resource_uri\": \"/api/v1/category/1/\"}, \"experience\": 5.0, \"id\": 2, \"image\": \"/media/images/5.8.0_yd5LyuC.PNG\", \"lat\": 4.0, \"lng\": 4.0, \"resource_uri\": \"/api/v1/service/2/\", \"user\": {\"age\": \"233...5555\", \"created_at\": \"2018-07-28T11:15:40.985000\", \"id\": 1, \"image\": \"/media/images/5.8.0.PNG\", \"name\": \"sdfsdfsdf\", \"phone\": \"+996701991855\", \"resource_uri\": \"/api/v1/users/1/\", \"updated_at\": \"2018-07-28T11:15:40.985000\"}}]}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            serviceArrayList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("objects");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Service service = new Service();
                service.setAddress(object.getString("address"));
                service.setExperience(object.getDouble("experience"));
                service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                service.setImage(object.getString("image"));
                service.setCategory(object.getJSONObject("category").getString("category"));
                User user = new User();
                JSONObject jsonUser = object.getJSONObject("user");
                user.setAge(jsonUser.getString("age"));
                user.setImage(jsonUser.getString("image"));
                user.setName(jsonUser.getString("name"));
                user.setPhone(jsonUser.getString("phone"));
                service.setUser(user);
                serviceArrayList.add(service);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json,boolean isOk) {
                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Service service = new Service();
                            service.setAddress(object.getString("address"));
                            service.setExperience(object.getDouble("experience"));
                            service.setGeoPoint(new GeoPoint(object.getDouble("lat"), object.getDouble("lng")));
                            service.setImage(object.getString("image"));
                            service.setCategory(object.getJSONObject("category").getString("category"));
                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            service.setUser(user);
                            serviceArrayList.add(service);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        ClientApi.requestGet(URLS.services,listener);*/
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fm);

        adapter.addFragment(new OrderFragment(), "Список");
        adapter.addFragment(new MapOrderFragment(), "Карта");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }

}
