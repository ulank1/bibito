package com.ulan.az.usluga;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.rilixtech.CountryCodePicker;

public class VerifyActivity extends AppCompatActivity {


    private EditText editTextMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        final CountryCodePicker countryCodePicker = findViewById(R.id.ccp);

        editTextMobile = findViewById(R.id.editTextMobile);

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile =countryCodePicker.getSelectedCountryCode()+editTextMobile.getText().toString().trim();
//
//                if(mobile.isEmpty() || mobile.length() < 10){
//                    editTextMobile.setError("Enter a valid mobile");
//                    editTextMobile.requestFocus();
//                    return;
//                }

                Intent intent = new Intent(VerifyActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
                finish();
            }
        });
    }

}