package com.ulan.az.usluga.Profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.E;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity {
    Context context;
    String path = "";
    ImageView avatar;
    EditText name, age;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Профиль");
        context = this;
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        Button button = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressbar);
        button.setText("Сохранить");
        name.setText(E.getAppPreferences(E.APP_PREFERENCES_NAME,context));
        age.setText(E.getAppPreferences(E.APP_PREFERENCES_AGE,context));
        Glide.with(this).load("http://145.239.33.4:5555"+E.getAppPreferences(E.APP_PREFERENCES_PHOTO,context)).asBitmap().centerCrop().into(new BitmapImageViewTarget(avatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                avatar.setImageDrawable(circularBitmapDrawable);
            }
        });

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                age.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, 1990, 1, 1);
                datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
                datePickerDialog.show();
            }
        });
    }

    public void onClick(View view) {
        //Log.e("DDDD", "DDD");
        register(0);
    }

    public void register(int f) {
        progressBar.setVisibility(View.VISIBLE);
        final int gg = f;
        if (f < 1) {
            ClientApiListener clientApiListener = new ClientApiListener() {
                @Override
                public void onApiResponse(String id, String json, boolean isOk) {
                    if (json.isEmpty() || json == null) {
                        ClientApiListener listener = new ClientApiListener() {
                            @Override
                            public void onApiResponse(String id, final String json, boolean isOk) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {


                                            Log.e("Json",json);
                                            JSONObject jsonObject = new JSONObject(json);
                                            JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                            if (jsonArray.length() > 0) {

                                                JSONObject object = jsonArray.getJSONObject(0);
                                                E.setAppPreferences(E.APP_PREFERENCES_PHONE, context, object.getString("phone"));
                                                E.setAppPreferences(E.APP_PREFERENCES_ID, context, object.getInt("id"));
                                                E.setAppPreferences(E.APP_PREFERENCES_NAME, context, object.getString("name"));
                                                E.setAppPreferences(E.APP_PREFERENCES_PHOTO, context, object.getString("image"));
                                                E.setAppPreferences(E.APP_PREFERENCES_AGE, context, object.getString("age"));


                                            }
                                            finish();
                                        } catch (JSONException | NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        };

                        ClientApi.requestGet(URLS.users + "&phone=" + E.getAppPreferences(E.APP_PREFERENCES_PHONE,context), listener);
                    }
                }
            };
            //Log.e("path", path);
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
            MultipartBody.Builder req = new MultipartBody.Builder().setType(MultipartBody.FORM);
            req.addFormDataPart("name", name.getText().toString());
            req.addFormDataPart("age", age.getText().toString());
            if (!path.isEmpty())
            req.addFormDataPart("image", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();

            RequestBody requestBody = req.build();

            ClientApi.requesPutImage(URLS.users_put + E.getAppPreferencesINT(E.APP_PREFERENCES_ID, context) + "/", requestBody, clientApiListener);
        }
    }

    public void onClickPhoto(View view) {

        FromCard();

    }


    public void FromCard() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            path = E.getPath(imageUri, this);
            //Log.e("path", path);
            avatar.setImageURI(imageUri);

        }

    }

}
