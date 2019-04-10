package com.ulan.az.usluga.Profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.ulan.az.usluga.forum.Forum;
import com.ulan.az.usluga.helpers.E;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyForumActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    ArrayList<ArrayList<User>> users;
    ArrayList<Forum> orders;
    int a = 0;
    ClientApiListener listener1;
    RVMyForumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        getSupportActionBar().setTitle("Мой Форум");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 1);
        users = new ArrayList<>();

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
                            Forum forum = new Forum();

                            forum.setCount(object.getInt("count"));
                            forum.setId(object.getInt("id"));
                            forum.setDescription(object.getString("description"));
                            forum.setImage(object.getString("image"));
                            forum.setTitle(object.getString("title"));
                            String date = object.getString("updated_at");

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

                            orders.add(forum);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener1 = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("JSon",json);
                            ArrayList<User> users1 = new ArrayList<>();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(json);


                                JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    User user = new User();
                                    user.setId(object.getInt("id"));
                                    JSONObject jsonUser = object.getJSONObject("user");
                                    user.setAge(jsonUser.getString("age"));
                                    user.setImage(jsonUser.getString("image"));
                                    user.setName(jsonUser.getString("name"));
                                    user.setPhone(jsonUser.getString("phone"));
                                    user.setDeviceId(jsonUser.getString("device_id"));
                                    users1.add(user);
                                }
                                Log.e("SIZE",users1.size()+"");

                                users.add(users1);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            a++;
                            if (a == orders.size()) {
                                Log.e("SIZE",users.size()+"");
                                adapter = new RVMyForumAdapter(MyForumActivity.this,orders,users);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.setAdapter(adapter);

                                    }
                                });
                            } else {
                                ClientApi.requestGet(URLS.confirmation + "&forum=" + orders.get(a).getId(), listener1);
                            }

                        }
                    };
                    if (orders.size()>0)
                    ClientApi.requestGet(URLS.confirmation + "&forum=" + orders.get(a).getId(), listener1);

                }
            }
        };

        ClientApi.requestGet(URLS.forum + "&user=" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, MyForumActivity.this)), listener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

}
