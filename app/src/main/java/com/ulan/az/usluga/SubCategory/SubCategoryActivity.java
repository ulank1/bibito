package com.ulan.az.usluga.SubCategory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.Category.RVCategoryAdapter;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    MaterialSearchView searchView;

    RecyclerView mRecyclerView;

    ArrayList<Category> serviceArrayList;
    RVCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

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
                            Category category = new Category();
                            category.setCategory(object.getString("sub_category"));
                            category.setId(object.getInt("id"));
                            serviceArrayList.add(category);
                        }
                        //Log.e("size", serviceArrayList.size() + "");
                        adapter = new RVCategoryAdapter(SubCategoryActivity.this, serviceArrayList, getIntent().getIntExtra("tag", 0) + 2);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        ClientApi.requestGet(URLS.sub_category + "&category=" + category.getId(), listener);


        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ArrayList<Category> categories1 = new ArrayList<>();

                for (Category category : serviceArrayList) {
                    String s = category.getCategory().toLowerCase();
                    if (s.contains(query.toLowerCase())) {
                        categories1.add(category);
                    }
                }


                if (categories1.size() == 0) {
                    adapter = new RVCategoryAdapter(SubCategoryActivity.this, serviceArrayList, getIntent().getIntExtra("tag", 0) + 2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(adapter);
                        }
                    });
                } else {

                    adapter = new RVCategoryAdapter(SubCategoryActivity.this, categories1, getIntent().getIntExtra("tag", 0) + 2);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        return true;
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
}
