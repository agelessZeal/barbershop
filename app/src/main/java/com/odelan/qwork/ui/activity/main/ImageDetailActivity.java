package com.odelan.qwork.ui.activity.main;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;

import com.odelan.qwork.R;
import com.odelan.qwork.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.odelan.qwork.MyApplication.ASSET_BASE_URL;

public class ImageDetailActivity extends BaseActivity {

    public static String image_url;

    @BindView(R.id.iv)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        mContext = this;
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Picasso.with(mContext).load(ASSET_BASE_URL + image_url).placeholder(R.drawable.back_thumb).into(iv);
    }

    @OnClick (R.id.backIV) public void onBack() {
        finish();
    }
}
