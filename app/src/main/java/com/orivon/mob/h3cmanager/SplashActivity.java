package com.orivon.mob.h3cmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    TextView textWel;
    H3CApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        application = (H3CApplication) getApplication();
        application.stopNotice();

        textWel = (TextView) findViewById(R.id.textWel);
        textWel.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (application.readCookie().isEmpty()) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, CaseInfoListActivity.class));
                    finish();
                }

            }
        }, 1500);


    }
}
