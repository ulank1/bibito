package com.ulan.az.usluga;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.ulan.az.usluga.Category.CategoryFragment;
import com.ulan.az.usluga.Profile.FavoritesActivity;
import com.ulan.az.usluga.helpers.ViewPagerAdapter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ViewPager viewPager;
    private TabLayout tabLayout;
    FragmentManager fm;
    ClientApiListener orderApiListener,serviceApiListener;
    MaterialSearchView searchView;

    public static int MULTIPLE_PERMISSION_REQUEST_CODE = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getActionBar().setTitle("ddd");

        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        fm = getSupportFragmentManager();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(deviceToken)) {
            //Log.e("TOKK", deviceToken);
           // sendJsonDataDeviceToken(deviceToken);

            return;
        }

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ClientApiListener listener = new ClientApiListener() {
                    @Override
                    public void onApiResponse(String id, String json, boolean isOk) {
                        orderApiListener.onApiResponse(id,json,isOk);
                        serviceApiListener.onApiResponse(id,json,isOk);
                    }
                };

                ClientApi.requestGet(URLS.category+"&category="+query,listener);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });
        //Log.e(searchView.toString(),searchView.toString());


        checkPermissionsState();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fm);
        Fragment fragment1 = new CategoryFragment();
        fragment1.setArguments(getBundle(1));
        Fragment fragment2 = new CategoryFragment();
        fragment2.setArguments(getBundle(2));
        adapter.addFragment(fragment1, "Услуги");
        adapter.addFragment(fragment2, "Задачи");
        viewPager.setAdapter(adapter);
    }

    private Bundle getBundle(int type){
        Bundle bundle = new Bundle();
        bundle.putInt("tag", type);
        bundle.putInt("tag", type);
        return bundle;
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

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      *//*  getMenuInflater().inflate(R.menu.menu_category, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);*//*

        return true;
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      /*  getMenuInflater().inflate(R.menu.main2, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        //Log.e("DDD",item.toString());
        searchView.setMenuItem(item);
*/
        return true;
    }

    public void setOrderApiListener(ClientApiListener orderApiListener) {
        this.orderApiListener = orderApiListener;
    }

    public void setServiceApiListener(ClientApiListener serviceApiListener) {
        this.serviceApiListener = serviceApiListener;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
        } else if (id == R.id.nav_order) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
