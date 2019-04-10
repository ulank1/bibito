package com.ulan.az.usluga.forum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.PagerImageFragment;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.User;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.OrderMoreInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForumInfoActivity extends AppCompatActivity {
    TextView title, description, count, name, date;
    Button add;
    LinearLayout line;
    Forum forum;
    RelativeLayout send;
    EditText editSend;
    RecyclerView mRecyclerView;
    RVCommentAdapter adapter;
    ArrayList<Comment> comments;
    ImageView avatar;

    ImageView share,like;
    TextView textLike;
    Context context;

    ViewPager pager;

    int isLike = 0;
    int likeCount;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_info);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        avatar = findViewById(R.id.avatar);
        title = findViewById(R.id.title);
        description = findViewById(R.id.desc);
        count = findViewById(R.id.count);
        line = findViewById(R.id.line1 );
        send = findViewById(R.id.btn_send );
        editSend = findViewById(R.id.edit_comment);
        mRecyclerView = findViewById(R.id.rv);
        add = findViewById(R.id.btn_add);

        forum = (Forum) getIntent().getSerializableExtra("forum");

        init();
        RelativeLayout relativeLayout = findViewById(R.id.relative);

        pager = (ViewPager) findViewById(R.id.viewpager);
        if (forum.getImages()!=null&&forum.getImages().size()>0) {
            relativeLayout.setVisibility(View.VISIBLE);
            MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(pagerAdapter);

            CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
            indicator.setViewPager(pager);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final int pos;
                    if (pager.getCurrentItem()+1==forum.getImages().size())
                        pos = 0;
                    else pos = pager.getCurrentItem()+1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(pos);
                        }
                    });
                }
            },10000,10000);
        }else {
            ImageView imageView = findViewById(R.id.placeholder);
            imageView.setVisibility(View.VISIBLE);
        }
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!editSend.getText().toString().isEmpty()){

                    ClientApiListener listener = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("JSON",json);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getComments();
                                    editSend.setText("");
                                }
                            });
                        }
                    };

                    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
                    MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("forum","/api/v1/forum1/"+ forum.getId()+"/")
                            .addFormDataPart("user","/api/v1/users/"+String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,ForumInfoActivity.this))+"/")
                            .addFormDataPart("comment",editSend.getText().toString()).build();

                    ClientApi.requestPostImage(URLS.comment,req,listener);

                }
            }
        });


        name.setText(forum.getUser().getName() + "");

        Glide.with(this).load("http://145.239.33.4:5555"+forum.getUser().getImage()).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                avatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        if (forum.getUser().getId()==E.getAppPreferencesINT(E.APP_PREFERENCES_ID,this)){
            add.setVisibility(View.GONE);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        //Log.e("tag", getArguments().getInt("tag") + "");

        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);


        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray jsonArray = object.getJSONArray("objects");
                    if (jsonArray.length() > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               add.setVisibility(View.GONE);

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        getComments();

        getSupportActionBar().setTitle(forum.title);
        ClientApi.requestGet(URLS.confirmation + "&forum=" + forum.getId() + "&user=" + E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ForumInfoActivity.this), listener);

        date.setText(forum.getDate()+"");

        title.setText(forum.getTitle());
        description.setText(forum.getDescription());





        if (forum.getCount() == 0) {
            line.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
        } else {
            count.setText(forum.getCount() + "");
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ClientApiListener listener = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            if (isOk && json.isEmpty()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        add.setVisibility(View.GONE);
                                        Toast.makeText(ForumInfoActivity.this, "Отправлено", Toast.LENGTH_SHORT).show();
                                        send();

                                    }
                                });
                            }
                        }
                    };
                    MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("user", "/api/v1/users/" + String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID, ForumInfoActivity.this)) + "/")
                            .addFormDataPart("forum", "/api/v1/forum1/" + String.valueOf(forum.getId()) + "/").build();

                    ClientApi.requestPostImage(URLS.confirmation, req, listener);
                }
            });
        }
    }


    private void init() {

        share = findViewById(R.id.share);
        like = findViewById(R.id.like);
        textLike = findViewById(R.id.text_like);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text ="Категория : " + forum.getTitle()+
                        "\nTелефон : " +forum.getUser().getPhone()+
                        "\nОписание : "+ forum.getDescription()+"";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });




        getLikes();




        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike!=0){

                    ClientApi.requestDelete1(URLS.like_forum_put,isLike,context);
                    like.setImageResource(R.drawable.like_inactive);
                    likeCount--;
                    textLike.setText("Нравится "+likeCount);
                    isLike=0;

                }else {
                    ClientApiListener listener1 = new ClientApiListener() {
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            Log.e("dddsssdcx",json);
                            if (json.isEmpty()){
                                getLikes();
                            }

                        }
                    };

                    MultipartBody.Builder req = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    req.addFormDataPart("user_id_owner", E.getAppPreferencesINT(E.APP_PREFERENCES_ID,context)+"");
                    req.addFormDataPart("type_id", forum.getId()+"");
                    RequestBody requestBody = req.build();
                    ClientApi.requestPostImage(URLS.like_forum_put,requestBody,listener1);

                }
            }
        });


    }


    public void getLikes(){
        ClientApiListener listener = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                    likeCount = jsonArray.length();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textLike.setText("Нравится "+likeCount);

                        }
                    });
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int id_own = E.getAppPreferencesINT(E.APP_PREFERENCES_ID,context);
                        if (id_own == object.getInt("user_id_owner")){
                            isLike = object.getInt("id");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    like.setImageResource(R.drawable.like_active);

                                }
                            });
                            break;
                        }
                        if (i==jsonArray.length()-1){
                            isLike = 0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    like.setImageResource(R.drawable.like_inactive);

                                }
                            });
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Log.e("ID",forum.getId()+"");

        ClientApi.requestGet(URLS.like_forum+"&type_id="+forum.getId(),listener);

    }


    public String getJson() {
        JSONObject jsonObject = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            notification.put("title", "Форум");
            notification.put("body", "Хотят забронировать!");
            notification.put("id", 1);
            data.put("title", "Форум");
            data.put("body", "Хотят забронировать!");
            data.put("id", 2);
            jsonObject.put("notification", notification);
            jsonObject.put("data", data);
            jsonObject.put("to", forum.getUser().getDeviceId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void send() {

        OkHttpClient client = new OkHttpClient();


        RequestBody body = RequestBody.create(JSON, getJson());
        Request request = new Request.Builder()
                .header("Authorization", Shared.key)
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e("Error", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                //Log.e("RESPONSE_Finish", json);


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void getComments(){
        ClientApiListener listener1 = new ClientApiListener() {
            @Override
            public void onApiResponse(String id, String json, boolean isOk) {

                if (isOk){
                    try {
                        comments = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Comment comment  = new Comment();
                            comment.setComment(object.getString("comment"));

                            String date = object.getString("updated_at");


                            comment.setDate(parseDate(date));

                            User user = new User();
                            JSONObject jsonUser = object.getJSONObject("user");
                            user.setAge(jsonUser.getString("age"));
                            user.setImage(jsonUser.getString("image"));
                            user.setName(jsonUser.getString("name"));
                            comment.setName(jsonUser.getString("name"));
                            user.setPhone(jsonUser.getString("phone"));
                            user.setId(jsonUser.getInt("id"));
                            comment.setAuthor(jsonUser.getInt("id"));
                            user.setDeviceId(jsonUser.getString("device_id"));

                            comment.setUser(user);

                            comments.add(comment);

                        }

                        adapter = new RVCommentAdapter(ForumInfoActivity.this,comments);
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

        ClientApi.requestGet(URLS.comment+"&forum="+forum.getId(),listener1);

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

    public void onClickPhone(View view) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+" + forum.getUser().getPhone()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PagerImageFragment.newInstance(position,forum.getImages().get(position));
        }

        @Override
        public int getCount() {
            return forum.getImages().size();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }
}
