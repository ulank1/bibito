package com.ulan.az.usluga.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.AddressActivity;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RedactorServiceActivity extends AppCompatActivity {
    String path = "";
    EditText address,experience,desc;
    double lat,lon;
    ArrayList<String> serviceArrayList;
    ArrayList<Category> categoryArrayList,subCategoryArrayList;
    int index=0;
    ProgressBar progressBar;
    Service service;
    MultipartBody.Builder build;
    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redactor_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        address = findViewById(R.id.address);
        experience = findViewById(R.id.experience);
        desc = findViewById(R.id.desc);
        progressBar = findViewById(R.id.progressbar);

        build = new MultipartBody.Builder().setType(MultipartBody.FORM);

        desc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RedactorServiceActivity.this,AddressActivity.class),123);
            }
        });

        service = (Service) getIntent().getSerializableExtra("service");

        ArrayList<String> images = service.getImages();

        for (int i = 0; i < images.size(); i++) {
            String image = images.get(i);
            if (!image.equals("null")){
                if (i==0){
                    ImageView imageView = findViewById(R.id.image);
                    Glide.with(this).load("http://145.239.33.4:5555"+image).placeholder(R.drawable.placeholder_add_image).into(imageView);
                }
                if (i==1){
                    ImageView imageView = findViewById(R.id.image2);
                    Glide.with(this).load("http://145.239.33.4:5555"+image).placeholder(R.drawable.placeholder_add_image).into(imageView);
                }
                if (i==2){
                    ImageView imageView = findViewById(R.id.image3);
                    Glide.with(this).load("http://145.239.33.4:5555"+image).placeholder(R.drawable.placeholder_add_image).into(imageView);
                }
                if (i==3){
                    ImageView imageView = findViewById(R.id.image4);
                    Glide.with(this).load("http://145.239.33.4:5555"+image).placeholder(R.drawable.placeholder_add_image).into(imageView);
                }
                if (i==4){
                    ImageView imageView = findViewById(R.id.image5);
                    Glide.with(this).load("http://145.239.33.4:5555"+image).placeholder(R.drawable.placeholder_add_image).into(imageView);
                }
            }
        }


        desc.setText(service.getDescription());
        experience.setText(service.getExperience()+"");
        address.setText(service.getAddress());
        lat = service.getGeoPoint().getLatitude();
        lon = service.getGeoPoint().getLongitude();

    }

    public void FromCard(int pos) {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, pos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
            build.addFormDataPart("image1", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();
            ImageView imageView = findViewById(R.id.image);
            imageView.setImageURI(imageUri);
        }

        else  if (requestCode==2 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
            build.addFormDataPart("image2", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();
            ImageView imageView = findViewById(R.id.image2);
            imageView.setImageURI(imageUri);
        }

        else  if (requestCode==3 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
            build.addFormDataPart("image3", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();
            ImageView imageView = findViewById(R.id.image3);
            imageView.setImageURI(imageUri);
        }

        else  if (requestCode==4 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
            build.addFormDataPart("image4", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();
            ImageView imageView = findViewById(R.id.image4);
            imageView.setImageURI(imageUri);
        }

        else  if (requestCode==5 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
            build.addFormDataPart("image5", path.split("/")[path.split("/").length - 1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();
            ImageView imageView = findViewById(R.id.image5);
            imageView.setImageURI(imageUri);
        }
        if (requestCode==123&&resultCode==RESULT_OK&&data!=null){
            address.setText(data.getStringExtra("address"));
            lat = data.getDoubleExtra("lat",0);
            lon = data.getDoubleExtra("lon",0);
        }

    }

    public void onClick(View view) {

        if (isValidate()){
            progressBar.setVisibility(View.VISIBLE);
            ClientApiListener clientApiListener = new ClientApiListener() {
                @Override
                public void onApiResponse(String id, String json,boolean isOk) {
                    Log.e("EEE",json);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           progressBar.setVisibility(View.GONE);
                       }
                   });
                    if(json.isEmpty()){
                        finish();
                    }
                }
            };

            MultipartBody req;
            build
                   /* .addFormDataPart("sub_category","/api/v1/subcategory/"+ String.valueOf(subCategoryArrayList.get(subCategory.getSelectedItemPosition()-1).getId())+"/")
                    .addFormDataPart("user","/api/v1/users/"+String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,RedactorServiceActivity.this))+"/")*/
                    .addFormDataPart("address",address.getText().toString())
                    .addFormDataPart("lat", String.valueOf(lat))
                    .addFormDataPart("experience", experience.getText().toString())
                    .addFormDataPart("description", desc.getText().toString().isEmpty()?"-":desc.getText().toString())
                    .addFormDataPart("lng", String.valueOf(lon));
                    //.addFormDataPart("image",path.split("/")[path.split("/").length-1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();

            req = build.build();

            ClientApi.requesPutImage(URLS.services_delete + service.getId()+"/",req,clientApiListener,this);
        }

    }

    private boolean isValidate(){
        boolean bool = true;
        if (address.getText().toString().isEmpty()){
            address.setError("добавьте адрес");
            bool = false;
        }

        if (experience.getText().toString().isEmpty()){
            experience.setError("добавьте опыт");
            bool = false;
        }

        return bool;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
    public void onClickPhoto(View view) {
        int id = view.getId();
        if (id == R.id.avatar){
            FromCard(1);
        }else if (id == R.id.avatar2){
            FromCard(2);
        }else if (id == R.id.avatar3){
            FromCard(3);
        }else if (id == R.id.avatar4){
            FromCard(4);
        }else if (id == R.id.avatar5){
            FromCard(5);
        }
    }
}
