package com.ulan.az.usluga.forum;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ulan.az.usluga.helpers.Shared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForumActivity extends AppCompatActivity {

    MaterialSearchView searchView;

    RecyclerView mRecyclerView;

    ArrayList<Forum> serviceArrayList;
    RVForum1CategoryAdapter adapter;
    Button btnAdd;

    ProgressBar progressBar;
    ClientApiListener listener;
    Category category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        btnAdd =  findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForumActivity.this,AddForumActivity.class));
            }
        });

        ImageView map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForumActivity.this, ForumMapActivity.class).putExtra("category_id",category.getId()));
            }
        });

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        category = (Category) getIntent().getSerializableExtra("category");

        getSupportActionBar().setTitle(category.getCategory());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
      /*  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
*/
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
                            Forum forum = new Forum();

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

                            forum.setDate(parseDate(date));

                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setId(jsonUser.getInt("id"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setDeviceId(jsonUser.getString("device_id"));
                            forum.setUser(user);
                            ArrayList<String> images = new ArrayList<>();

                            if (!object.getString("image1").equals("null")){
                                images.add(object.getString("image1"));
                            } if (!object.getString("image2").equals("null")){
                                images.add(object.getString("image2"));
                            }if (!object.getString("image3").equals("null")){
                                images.add(object.getString("image3"));
                            } if (!object.getString("image4").equals("null")){
                                images.add(object.getString("image4"));
                            } if (!object.getString("image5").equals("null")){
                                images.add(object.getString("image5"));
                            }
                            forum.setImages(images);
                            serviceArrayList.add(forum);
                        }
                        //Log.e("size", serviceArrayList.size() + "");
                        adapter = new RVForum1CategoryAdapter(ForumActivity.this, serviceArrayList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);

                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };
        progressBar.setVisibility(View.VISIBLE);

        ClientApi.requestGet(URLS.forum + "&sub_category=" + category.getId(), listener);


        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                ArrayList<Forum> categories1 = new ArrayList<>();

                for (Forum category : serviceArrayList) {
                    String s = category.getTitle().toLowerCase();
                    if (s.contains(query.toLowerCase())) {
                        categories1.add(category);
                    }
                }


                if (categories1.size() == 0) {
                    adapter = new RVForum1CategoryAdapter(ForumActivity.this, serviceArrayList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(adapter);
                        }
                    });
                } else {

                    adapter = new RVForum1CategoryAdapter(ForumActivity.this, categories1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(adapter);
                        }
                    });
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        //Log.e("DDD", item.toString());
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return true;
    }


    public String parseDate(String inputDate) {
        String DATE_FORMAT_I = "yyyy-MM-dd'T'HH:mm:ss";
        String DATE_FORMAT_O = "yyyy-MM-dd. HH:mm";

        SimpleDateFormat formatInput = new SimpleDateFormat(DATE_FORMAT_I);
        SimpleDateFormat formatOutput = new SimpleDateFormat(DATE_FORMAT_O);
        Date date = null;
        try {
            date = formatInput.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateString = formatOutput.format(date);
        return dateString;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ClientApi.requestGet(URLS.forum + "&sub_category=" + category.getId(), listener);
    }
}
