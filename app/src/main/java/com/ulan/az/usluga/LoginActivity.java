package com.ulan.az.usluga;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LoginActivity extends Activity {

    String path = "";
    ImageView avatar;
    EditText name, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkPermissionsState();
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(LoginActivity.this,
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


       /* avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromCard();
            }
        });*/

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


    public void onClick(View view) {
        //Log.e("DDDD", "DDD");
        register(1);
    }

    public void register(int f) {
        final int gg = f;
        if (f < 3) {
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
                                            //Log.e("Json",json);
                                            JSONObject jsonObject = new JSONObject(json);
                                            JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                            if (jsonArray.length() > 0) {

                                                JSONObject object = jsonArray.getJSONObject(0);
                                                E.setAppPreferences(E.APP_PREFERENCES_PHONE, LoginActivity.this, object.getString("phone"));
                                                E.setAppPreferences(E.APP_PREFERENCES_ID, LoginActivity.this, object.getInt("id"));
                                                E.setAppPreferences(E.APP_PREFERENCES_NAME, LoginActivity.this, object.getString("name"));
                                                E.setAppPreferences(E.APP_PREFERENCES_PHOTO, LoginActivity.this, object.getString("image"));
                                                E.setAppPreferences(E.APP_PREFERENCES_AGE, LoginActivity.this, object.getString("age"));

                                                startActivity(new Intent(LoginActivity.this, Main2Activity.class));
                                                finish();
                                            }
                                        } catch (JSONException | NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                        };

                        ClientApi.requestGet(URLS.users + "&phone=" + Shared.mobile, listener);
                    }
                }
            };
            //Log.e("path", path);
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
            MultipartBody.Builder req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("phone", Shared.mobile)
                    .addFormDataPart("name", name.getText().toString())
                    .addFormDataPart("age", age.getText().toString());



            RequestBody requestBody;
            if (!path.isEmpty())
             req.addFormDataPart("image", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path)));


            requestBody = req.build();
            ClientApi.requestPostImage(URLS.users, requestBody, clientApiListener,this);
        }
    }

    private void checkPermissionsState() {
        int internetPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int networkStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_NETWORK_STATE);

        int writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int wifiStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_WIFI_STATE);

        int cameraStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        int callPhoneStatePermissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE);


        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                networkStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
                cameraStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                callPhoneStatePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                wifiStatePermissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Log.e("CheckPermission","TRUE");

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.CALL_PHONE,
                            android.Manifest.permission.ACCESS_WIFI_STATE},
                    4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 4: {
                if (grantResults.length > 0) {
                    boolean somePermissionWasDenied = false;
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            somePermissionWasDenied = true;
                        }
                    }

                    if (somePermissionWasDenied) {
                        // Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                    }
                } else {
                    // Toast.makeText(this, "Cant load maps without all the permissions granted", Toast.LENGTH_SHORT).show();
                }


                return;
            }

        }
    }

    public void onClickPhoto(View view) {

        FromCard();

    }
}
