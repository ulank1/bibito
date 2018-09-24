package com.ulan.az.usluga;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ulan.az.usluga.helpers.E;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Log.e("HHH",E.getAppPreferences(E.APP_PREFERENCES_PHONE, SplashActivity.this));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (E.getAppPreferences(E.APP_PREFERENCES_PHONE, SplashActivity.this).isEmpty())
                    startActivity(new Intent(SplashActivity.this, VerifyActivity.class));
                else {
                    startActivity(new Intent(SplashActivity.this, Main2Activity.class));
                }
                finish();
            }
        }, 2000);

    }
}
