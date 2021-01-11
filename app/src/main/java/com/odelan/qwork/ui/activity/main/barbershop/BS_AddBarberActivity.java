package com.odelan.qwork.ui.activity.main.barbershop;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.ValidationUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BS_AddBarberActivity extends BaseActivity {
    ValidationUtils validationUtils;
    @BindView(R.id.emailBarber)
    EditText emailBarber;
    @BindView(R.id.namrBarber)
    EditText namrBarber;
    User me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs_add_barber);
        mContext = this;
         me = getUser(getValueFromKey("me"));

       // Log.v("Barbershopid",me.userid+"");
        ButterKnife.bind(this);

        validationUtils = new ValidationUtils(mContext);

    }
    @OnClick(R.id.close) public void onClose() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
    @OnClick(R.id.createBTN) public void onSignup() {

        if(validationUtils.checkBarberValid(emailBarber, namrBarber)) {
            showCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "user_new/barber_signup")
                    .addBodyParameter("email", emailBarber.getText().toString())
                    .addBodyParameter("barber_name", namrBarber.getText().toString())
                    .addBodyParameter("creater_id", me.userid)
                    .addBodyParameter("user_type", MyApplication.USER_BARBER)
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
                                Log.v("createdstatus",status+"");
                                if(status.equals("1")) {
                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK,returnIntent);
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
}
