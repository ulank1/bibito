package com.ulan.az.usluga.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.forum.Forum;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.service.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class RedactorForumActivity extends AppCompatActivity {
    String path = "";
    ImageView avatar;
    EditText address,desc,count;
    double lat,lon;

    ArrayList<Category> subCategoryArrayList;
    int index=0;
    ProgressBar progressBar;
    Forum service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_redactor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        address = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        count = findViewById(R.id.count);
        progressBar = findViewById(R.id.progressbar);

        service = (Forum) getIntent().getSerializableExtra("service");

        address.setText(service.getTitle());
        desc.setText(service.getDescription());
        count.setText(service.getCount()+"");

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
            avatar.setImageURI(imageUri);
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
            String countt = count.getText().toString();
            if (countt.isEmpty()){
                countt="0";
            }
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
            MultipartBody req = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                    .addFormDataPart("sub_category","/api/v1/forumsubcategory/"+ String.valueOf(subCategoryArrayList.get(subCategory.getSelectedItemPosition()-1).getId())+"/")
//                    .addFormDataPart("user","/api/v1/users/"+String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,RedactorForumActivity.this))+"/")
                    .addFormDataPart("title",address.getText().toString())
                    .addFormDataPart("count",countt)
                    .addFormDataPart("description", desc.getText().toString().isEmpty()?"-":desc.getText().toString()).build();

            ClientApi.requesPutImage(URLS.forum_put+service.getId()+"/",req,clientApiListener,this);
        }

    }

    private boolean isValidate(){
        boolean bool = true;
        if (address.getText().toString().isEmpty()){
            address.setError("добавьте название");
            bool = false;
        }
      /*  if (path.isEmpty()){
            Toast.makeText(this, "добавьте фото", Toast.LENGTH_SHORT).show();
            bool = false;
        }*/

        return bool;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}
