package com.odelan.qwork.ui.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.barber.BarberProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BarbershopProfileActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.ValidationUtils;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity {

    @BindView(R.id.emailET)
    EditText emailET;

    @BindView(R.id.passwordET)
    EditText passwordET;

    @BindView(R.id.confirmPasswordET)
    EditText confrimPasswordET;

    @BindView(R.id.mobileNumET)
    EditText mobileNumET;

    @BindView(R.id.checkCodeET)
    EditText checkCodeET;

    @BindView(R.id.barbershopOpt)
    RadioButton barbershopOpt;

    @BindView(R.id.barberOpt)
    RadioButton barberOpt;

    @BindView(R.id.customerOpt)
    RadioButton customerOpt;

    ValidationUtils validationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = this;
        ButterKnife.bind(this);

        validationUtils = new ValidationUtils(mContext);
    }

    @OnClick(R.id.signupBtn) public void onSignup() {
        String user_type = "";
        if(barbershopOpt.isChecked()) {
            user_type = MyApplication.USER_BARBER_SHOP;
        } else if (barberOpt.isChecked()) {
            user_type = MyApplication.USER_BARBER;
        } else if (customerOpt.isChecked()) {
            user_type = MyApplication.USER_CUSTOMER;
        } else {
            showToast("Please choose user type");
            return;
        }

        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
            if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
                showToast("Please try again, now you can not receive any notification because of your network issue");
                return;
            }
        }

        if(validationUtils.checkSignupValid(emailET, passwordET, confrimPasswordET, mobileNumET, checkCodeET)) {
            showCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "user_new/signup")
                    .addBodyParameter("email", emailET.getText().toString())
                    .addBodyParameter("password", passwordET.getText().toString())
                    .addBodyParameter("mobile_num", mobileNumET.getText().toString())
                    .addBodyParameter("check_code", checkCodeET.getText().toString())
                    .addBodyParameter("user_type", user_type)
                    .addBodyParameter("one_id_android", MyApplication.one_id_android)
                    .setTag("signup")
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
                                    JSONObject user = response.getJSONObject("data");
                                    saveKeyValue("me", user.toString());
                                    saveKeyValue("password", passwordET.getText().toString());
                                    User me = LoganSquare.parse(user.toString(), User.class);
                                    if(me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                                        //startActivity(new Intent(mContext, CustomerProfileActivity.class));
                                        openTutorialScreen(MyApplication.USER_CUSTOMER);
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                                      //  startActivity(new Intent(mContext, BarberProfileActivity.class));
                                        openTutorialScreen(MyApplication.USER_BARBER);
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                                        BarbershopProfileActivity.mEmail = emailET.getText().toString();
                                        BarbershopProfileActivity.mPwd = passwordET.getText().toString();
                                        //startActivity(new Intent(mContext, BarbershopProfileActivity.class));
                                        openTutorialScreen(MyApplication.USER_BARBER_SHOP);
                                    }
                                    finish();
                                } else if (status.equals("0")) {
                                    showToast(response.getString("error"));
                                } else {
                                    showToast("Network Error!");
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
        }
    }
    @OnClick(R.id.loginBtn) public void onLogin() {
        finish();
    }

    @OnClick(R.id.sendBtn) public void onSendCode() {
        if(emailET.getText().toString().equals("")) {
            Common.showToast(mContext, "Please input email");
            return;
        }

        if(mobileNumET.getText().toString().equals("")) {
            Common.showToast(mContext, "Please input mobile number");
            return;
        }

//        if (!Common.validatePhoneNumber(checkCodeET.getText().toString())) {
//            Common.showToast(mContext, "Please input mobile number correctly");
//        }

        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/sendCheckCode")
                .addBodyParameter("email", emailET.getText().toString())
                .addBodyParameter("mobile_num", mobileNumET.getText().toString())
                .setTag("sendCheckCode")
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
                            } else if (status.equals("0")) {
                                showToast(response.getString("error"));
                            } else {
                                showToast("Network Error!");
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

    }
    private void openTutorialScreen(String userType){
        Intent intent = new Intent(mContext, TutorialActivity.class);
        intent.putExtra("launch_type",TutorialActivity.LAUNCH_CUSTOMER_PROFILE);
        intent.putExtra("userType",userType);
        startActivity(intent);

    }
}
