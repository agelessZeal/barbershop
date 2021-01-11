package com.odelan.qwork.ui.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.activity.main.barber.BarberProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BarbershopProfileActivity;
import com.odelan.qwork.ui.activity.main.customer.CustomerProfileActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseUserTypeActivity extends BaseActivity {

    public static String mUsername = "";
    public static String mPhoto = "";

    @BindView(R.id.barbershopOpt)
    RadioButton barbershopOpt;

    @BindView(R.id.barberOpt)
    RadioButton barberOpt;

    @BindView(R.id.customerOpt)
    RadioButton customerOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type);

        mContext = this;
        ButterKnife.bind(this);

        showTitleIV();
        hideTitle();
        hideLeftIV();
    }

    @OnClick(R.id.nextBtn) public void onNextClick() {
        if(barbershopOpt.isChecked()) {
            BarbershopProfileActivity.mUsername = mUsername;
            BarbershopProfileActivity.mPhoto = mPhoto;
            startActivity(new Intent(mContext, TutorialActivity.class).putExtra("launch_type",TutorialActivity.LAUNCH_PROFILE)
                    .putExtra("userType",MyApplication.USER_BARBER_SHOP));
            finish();
        } else if (barberOpt.isChecked()) {
            BarberProfileActivity.mUsername = mUsername;
            BarberProfileActivity.mPhoto = mPhoto;
            startActivity(new Intent(mContext, TutorialActivity.class).putExtra("launch_type",TutorialActivity.LAUNCH_PROFILE)
                    .putExtra("userType",MyApplication.USER_BARBER));
            finish();
        } else if (customerOpt.isChecked()) {
            CustomerProfileActivity.mUsername = mUsername;
            CustomerProfileActivity.mPhoto = mPhoto;
            startActivity(new Intent(mContext, TutorialActivity.class).putExtra("launch_type",TutorialActivity.LAUNCH_PROFILE)
                    .putExtra("userType",MyApplication.USER_CUSTOMER));
            finish();
        } else {
            showToast("Please select user type");
        }


    }
}
