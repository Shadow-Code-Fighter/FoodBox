package com.example.foodbox.AdminFile.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodbox.R;

public class A_MainActivitySp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_main_sp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iHome = new Intent(A_MainActivitySp.this, A_Admin_Login.class);
                startActivity(iHome);
                finish();
            }
        },1000);


    }
}