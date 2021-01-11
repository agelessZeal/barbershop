package com.odelan.qwork.ui.activity.main;

import android.os.Bundle;
import android.os.Handler;

import com.odelan.qwork.MyApplication;
import com.odelan.qwork.ui.base.BaseActivity;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class ReviewNotificationActivity extends BaseActivity {

    String param1;
    String param2;
    String param3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack

        param1 = getIntent().getStringExtra("param1");
        param2 = getIntent().getStringExtra("param2");
        param3 = getIntent().getStringExtra("param3");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                MyApplication.curActivity.showReviewDlg(param1, param2, param3);
            }
        };
        final Handler handler = new Handler();
        handler.postDelayed(runnable, 50);
        finish();
    }
}
