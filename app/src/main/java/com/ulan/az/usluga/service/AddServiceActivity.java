package com.ulan.az.usluga.service;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ulan.az.usluga.Category.Category;
import com.ulan.az.usluga.ClientApi;
import com.ulan.az.usluga.ClientApiListener;
import com.ulan.az.usluga.R;
import com.ulan.az.usluga.URLS;
import com.ulan.az.usluga.helpers.E;
import com.ulan.az.usluga.helpers.Shared;
import com.ulan.az.usluga.order.AddressActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

public class AddServiceActivity extends AppCompatActivity {
    String path = "";
    EditText address,experience,desc;
    double lat,lon;
    Spinner category,subCategory;
    ArrayList<String> serviceArrayList;
    ArrayList<Category> categoryArrayList,subCategoryArrayList;
    int index=0;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        address = findViewById(R.id.address);
        experience = findViewById(R.id.experience);
        desc = findViewById(R.id.desc);
        progressBar = findViewById(R.id.progressbar);

        desc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddServiceActivity.this,AddressActivity.class),1);
            }
        });
       category = (Spinner)findViewById(R.id.category);

        subCategory = (Spinner)findViewById(R.id.sub_category);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position>0) {
                    progressBar.setVisibility(View.VISIBLE);
                    ClientApiListener listener = new ClientApiListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onApiResponse(String id, String json, boolean isOk) {
                            //Log.e("SSSS", "SSSS");
                            if (isOk) {
                                try {
                                    JSONObject jsonObject = new JSONObject(json);
                                    ArrayList<String> serviceArrayList = new ArrayList<>();
                                    subCategoryArrayList = new ArrayList<>();

                                    serviceArrayList.add("Подкатегория");
                                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Category category1 = new Category();
                                        category1.setCategory(object.getString("sub_category"));
                                        category1.setId(object.getInt("id"));
                                        subCategoryArrayList.add(category1);

                                        serviceArrayList.add(object.getString("sub_category"));
                                        // Настраиваем адаптер

                                    }

                                    String[] s = serviceArrayList.toArray(new String[serviceArrayList.size()]);

                                    final ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter<String>(AddServiceActivity.this, android.R.layout.simple_spinner_item, s);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Вызываем адаптер
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            subCategory.setAdapter(adapter);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }

                        }
                    };

                    ClientApi.requestGet(URLS.sub_category_service + "&category=" + categoryArrayList.get(position - 1).getId(), listener);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar.setVisibility(View.VISIBLE);

        ClientApiListener listener = new ClientApiListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onApiResponse(String id, String json,boolean isOk) {

                if (isOk) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        serviceArrayList = new ArrayList<>();
                        categoryArrayList = new ArrayList<>();
                        serviceArrayList.add("Категория");
                        JSONArray jsonArray = jsonObject.getJSONArray("objects");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (Shared.category_id==object.getInt("id")){
                                index = i;
                            }
                            Category category1 = new Category();
                            category1.setCategory(object.getString("category"));
                            category1.setId(object.getInt("id"));
                            categoryArrayList.add(category1);

                            serviceArrayList.add(object.getString("category"));
                            // Настраиваем адаптер

                        }

                        String[] s = serviceArrayList.toArray(new String[serviceArrayList.size()]);

                        final ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(AddServiceActivity.this, android.R.layout.simple_spinner_item, s);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Вызываем адаптер
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               progressBar.setVisibility(View.GONE);
                               category.setAdapter(adapter);
                               category.setSelection(index);
                           }
                       });

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
        };

        ClientApi.requestGet(URLS.category_service,listener);



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
                    .addFormDataPart("sub_category","/api/v1/subcategory_service/"+ String.valueOf(subCategoryArrayList.get(subCategory.getSelectedItemPosition()-1).getId())+"/")
                    .addFormDataPart("user","/api/v1/users/"+String.valueOf(E.getAppPreferencesINT(E.APP_PREFERENCES_ID,AddServiceActivity.this))+"/")
                    .addFormDataPart("address",address.getText().toString())
                    .addFormDataPart("lat", String.valueOf(lat))
                    .addFormDataPart("experience", experience.getText().toString())
                    .addFormDataPart("description", desc.getText().toString().isEmpty()?"-":desc.getText().toString())
                    .addFormDataPart("lng", String.valueOf(lon)).build();
                    //.addFormDataPart("image",path.split("/")[path.split("/").length-1], RequestBody.create(MEDIA_TYPE_PNG, new File(path))).build();

            ClientApi.requestPostImage(URLS.services,req,clientApiListener);
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
        if (subCategory.getSelectedItemPosition()==0){
            Toast.makeText(this, "Выберите подкатегорию", Toast.LENGTH_SHORT).show();
            bool = false;
        }
        if (category.getSelectedItemPosition()==0){
            Toast.makeText(this, "Выберите категорию", Toast.LENGTH_SHORT).show();
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
