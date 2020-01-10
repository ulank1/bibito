package com.ulan.az.usluga.forum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.LocationActivity;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    LinearLayout lineSearch;
    EditText editSearch;

    boolean is_search = false;
    String search_text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressbar);

        editSearch = findViewById(R.id.edit_search);
        lineSearch = findViewById(R.id.line1);

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



       /* searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ForumActivity.this,LocationActivity.class));
            }
        });*/

       editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
           @Override
           public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
               if (i == EditorInfo.IME_ACTION_SEARCH) {
                   String query = textView.getText().toString();
                   ArrayList<Forum> categories1 = new ArrayList<>();
                   Shared.is_search = true;
                   Shared.search_text = query;

                   for (Forum category : serviceArrayList) {
                       String s = category.getTitle().toLowerCase();
                       if (s.contains(query.toLowerCase())) {
                           categories1.add(category);
                       }
                   }

                   Collections.sort(categories1, new Comparator<Forum>() {
                       @Override
                       public int compare(Forum lhs, Forum rhs) {
                           // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                           //  return Mathlhs.getGeoPoint().getLatitude() > rhs.customInt ? -1 : (lhs.customInt < rhs.customInt) ? 1 : 0;

                           double x1, x2, xe, y1, y2, ye, s1, s2;

                           x1 = lhs.getGeoPoint().getLatitude();
                           x2 = rhs.getGeoPoint().getLatitude();
                           y1 = lhs.getGeoPoint().getLongitude();
                           y2 = rhs.getGeoPoint().getLongitude();
                           xe = Shared.lat_search;
                           ye = Shared.lon_search;

                           s1 = Math.sqrt(Math.abs(x1 - xe) * Math.abs(x1 - xe) + Math.abs(y1 - ye) * Math.abs(y1 - ye));
                           s2 = Math.sqrt(Math.abs(x2 - xe) * Math.abs(x2 - xe) + Math.abs(y2 - ye) * Math.abs(y2 - ye));

                           return Double.compare(s2, s1);

                       }
                   });
                    lineSearch.setVisibility(View.GONE);
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

                   InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                   if (imm != null) {
                       imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                   }
                   return true;
               }

               return false;
           }
       });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        else if (id==R.id.action_search){
            lineSearch.setVisibility(View.VISIBLE);
            startActivity(new Intent(this,LocationActivity.class));
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shared.is_search = false;

    }


    @Override
    public void onBackPressed() {
        if (lineSearch.getVisibility() == View.VISIBLE){
            lineSearch.setVisibility(View.GONE);
        }else {
           super.onBackPressed();
        }
    }
}
