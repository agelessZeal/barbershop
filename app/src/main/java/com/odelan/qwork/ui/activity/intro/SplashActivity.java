package com.odelan.qwork.ui.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    final int REQUEST_PERMISSION = 999;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /******************  firebase-analytics *****************/
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrash.report(new Exception("Qwork Android non-fatal error"));
        /********************************************************/

        handler.postDelayed(runnable, 3000);
    }



    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
    };
}
