package com.odelan.qwork.ui.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.ValidationUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.emailET)
    EditText emailET;

    /*@BindView(R.id.passwordET)
    EditText passwordET;

    @BindView(R.id.confirmPasswordET)
    EditText confirmPasswordET;*/

    ValidationUtils validationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mContext = this;
        ButterKnife.bind(this);

        validationUtils = new ValidationUtils(mContext);
        hideTitleIV();
        showTitle();
        setTitle("Forgot Password");
    }

    @OnClick(R.id.sendBtn) public void onSend() {
        if(emailET.getText().toString() == null || emailET.getText().toString().equals("")) {
            showToast("Please input email");
            return;
        }
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/forgot_password")
                .addBodyParameter("email", emailET.getText().toString())
                .setTag("forgot_password")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                showToast("Sent successfully");
                                ResetPasswordActivity.email = emailET.getText().toString();
                                startActivity(new Intent(mContext, ResetPasswordActivity.class));
                                finish();
                            } else if (status.equals("false")) {
                                showToast("failed");
                            } else {
                                showToast(response.getString("error"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("Network Error!");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        hideCustomLoadingView();
                        showToast(error.getErrorBody().toString());
                    }
                });
        /*if(validationUtils.checkForgotPassValid(emailET, passwordET, confirmPasswordET)) {

        }*/
    }
}
