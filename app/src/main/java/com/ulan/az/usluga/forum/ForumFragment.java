//package com.ulan.az.usluga.forum;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ProgressBar;
//
//import com.miguelcatalan.materialsearchview.MaterialSearchView;
//import com.ulan.az.usluga.Category.Category;
//import com.ulan.az.usluga.ClientApi;
//import com.ulan.az.usluga.ClientApiListener;
//import com.ulan.az.usluga.R;
//import com.ulan.az.usluga.URLS;
//import com.ulan.az.usluga.User;
//import com.ulan.az.usluga.helpers.E;
//import com.ulan.az.usluga.helpers.Shared;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class ForumFragment extends Fragment {
//    RecyclerView mRecyclerView;
//
//    ArrayList<Forum> serviceArrayList;
//    RVForum1CategoryAdapter adapter;
//    Button btnAdd;
//
//    ClientApiListener listener;
//    Category category;
//    ProgressBar progressBar;
//    public ForumFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.activity_forum, container, false);
//        progressBar = view.findViewById(R.id.progressbar);
//
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv);
//        btnAdd =  view.findViewById(R.id.btn_add);
//        btnAdd.setVisibility(View.VISIBLE);
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ForumActivity.this,AddForumActivity.class));
//            }
//        });
//
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//
//        category = (Category) getIntent().getSerializableExtra("category");
//
//        getSupportActionBar().setTitle(category.getCategory());
//
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(llm);
//        mRecyclerView.setHasFixedSize(true);
//      /*  DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                llm.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
//*/
//        listener = new ClientApiListener() {
//            @Override
//            public void onApiResponse(String id, String json, boolean isOk) {
//                //Log.e("SSSS", "SSSS");
//                if (isOk) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(json);
//                        serviceArrayList = new ArrayList<>();
//                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject object = jsonArray.getJSONObject(i);
//                            Forum forum = new Forum();
//
//                            forum.setCount(object.getInt("count"));
//                            forum.setId(object.getInt("id"));
//                            forum.setDescription(object.getString("description"));
//                            forum.setImage(object.getString("image"));
//                            forum.setTitle(object.getString("title"));
//                            String date = object.getString("updated_at");
//
//                            forum.setDate(parseDate(date));
//
//                            User user = new User();
//                            JSONObject jsonUser = object.getJSONObject("user");
//                            user.setAge(jsonUser.getString("age"));
//                            user.setId(jsonUser.getInt("id"));
//                            user.setImage(jsonUser.getString("image"));
//                            user.setName(jsonUser.getString("name"));
//                            user.setPhone(jsonUser.getString("phone"));
//                            user.setDeviceId(jsonUser.getString("device_id"));
//                            forum.setUser(user);
//                            serviceArrayList.add(forum);
//                        }
//                        //Log.e("size", serviceArrayList.size() + "");
//                        adapter = new RVForum1CategoryAdapter(getContext(), serviceArrayList);
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressBar.setVisibility(View.GONE);
//
//                                mRecyclerView.setAdapter(adapter);
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//
//                    }
//                }
//
//            }
//        };
//        progressBar.setVisibility(View.VISIBLE);
//
//        ClientApi.requestGet(URLS.forum + "&sub_category=" + category.getId(), listener);
//
//
//
//
//        return view;
//    }
//
//
//    @Override
//    public void onFilter(int id) {
//        progressBar.setVisibility(View.VISIBLE);
//        if (E.getAppPreferencesBoolean(E.APP_PREFERENCES_FILTER_IS_CHECKED,getContext()))
//            ClientApi.requestGet(URLS.order+"&status=1&sub_category="+ Shared.category_id,listener);
//        else         ClientApi.requestGet(URLS.order+"&status=1",listener);
//
//    }
//    public String parseDate(String inputDate) {
//        String DATE_FORMAT_I = "yyyy-MM-dd'T'HH:mm:ss";
//        String DATE_FORMAT_O = "yyyy-MM-dd. HH:mm";
//
//        SimpleDateFormat formatInput = new SimpleDateFormat(DATE_FORMAT_I);
//        SimpleDateFormat formatOutput = new SimpleDateFormat(DATE_FORMAT_O);
//        Date date = null;
//        try {
//            date = formatInput.parse(inputDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String dateString = formatOutput.format(date);
//        return dateString;
//    }
//}
