package com.ulan.az.usluga.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.order.AddressActivity;
import com.ulan.az.usluga.service.Service;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class RedactorOrderActivity extends AppCompatActivity {
    String path = "";
    EditText address,desc;
    double lat,lon;
    ArrayList<String> serviceArrayList;
    ArrayList<Category> categoryArrayList,subCategoryArrayList;
    int index=0;
    ProgressBar progressBar;
    Service service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redactor_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        address = findViewById(R.id.address);
        desc = findViewById(R.id.desc);
        progressBar = findViewById(R.id.progressbar);


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RedactorOrderActivity.this,AddressActivity.class),1);

            }
        });

        service = (Service) getIntent().getSerializableExtra("service");

        desc.setText(service.getDescription());
        address.setText(service.getAddress());
        lat = service.getGeoPoint().getLatitude();
        lon = service.getGeoPoint().getLongitude();

    }

    public void FromCard() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2 && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            path = E.getPath(imageUri,this);
        }
        if (requestCode==1&&resultCode==RESULT_OK&&data!=null){
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

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
            MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
                   /* .addFormDataPart("sub_category","/api/v1/subcategory/"+ String.valueOf(subCategoryArrayList.get(subCategory.getSelectedItemPosition()-1).getId())+"/")
                    .addFormDataPart("user","/api/v1/users/"+String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,RedactorServiceActivity.this))+"/")*/
                    .addFormDataPart("address",address.getText().toString())
                    .addFormDataPart("lat", String.valueOf(lat))
                    .addFormDataPart("description", desc.getText().toString().isEmpty()?"-":desc.getText().toString())
                    .addFormDataPart("lng", String.valueOf(lon)).build();
                    //.addFormDataPart("image",path.split("/")[path.split("/").length-1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();

            ClientApi.requesPutImage(URLS.order_delete + service.getId()+"/",req,clientApiListener);
        }

    }

    private boolean isValidate(){
        boolean bool = true;
        if (address.getText().toString().isEmpty()){
            address.setError("добавьте адрес");
            bool = false;
        }

        return bool;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}
