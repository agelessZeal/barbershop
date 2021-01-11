package com.odelan.qwork.ui.activity.main;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.odelan.qwork.ui.base.BaseActivity;

/**
 * Created by LuckyMan on 2/2/2018.
 */

public class NotificationActivity extends BaseActivity {

    String param1;
    String param2;
    String param3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack

        finish();
    }

    public void showNotificationDialog(String senderId, String message) {
        new MaterialDialog.Builder(mContext)
                .title("Qwork")
                .content(message)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }
}
