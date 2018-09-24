package com.ulan.az.usluga.forum;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForumActivity extends AppCompatActivity {

    MaterialSearchView searchView;

    RecyclerView mRecyclerView;

    ArrayList<Forum> serviceArrayList;
    RVForum1CategoryAdapter adapter;
    FloatingActionButton btnAdd;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForumActivity.this,AddForumActivity.class));
            }
        });

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        final Category category = (Category) getIntent().getSerializableExtra("category");

        getSupportActionBar().setTitle(category.getCategory());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
      /*  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                llm.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
*/
        final ClientApiListener listener = new ClientApiListener() {
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

        ClientApi.requestGet(URLS.forum/* + "&sub_category=" + category.getId()*/, listener);


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

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        //Log.e("DDD", item.toString());
        searchView.setMenuItem(item);

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return true;
    }
}
