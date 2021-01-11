package com.odelan.qwork.ui.activity.main.barbershop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BS_ContactActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.emailTV)
    TextView emailTV;
    @BindView(R.id.phoneTV)
    TextView phoneTV;
    @BindView(R.id.wv_content)
    WebView WvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs__contact);

        mContext = this;
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setSideProfile(navigationView.getHeaderView(0));

        emailTV.setText(MyApplication.SUPPORT_EMAIL);
        phoneTV.setText(MyApplication.SUPPORT_PHONE);

        WvContent.getSettings().setJavaScriptEnabled(true); // enable javascript
        WvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WvContent.loadUrl(MyApplication.URL_CONTACT_US);
    }

    @OnClick(R.id.emailTV)
    public void onEmailClick() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + MyApplication.SUPPORT_EMAIL));
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }

    @OnClick(R.id.phoneTV)
    public void onPhoneClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + MyApplication.SUPPORT_PHONE));
        startActivity(intent);
    }

    @OnClick(R.id.fbIV)
    public void onFacebookClick() {
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.SUPPORT_FB_LINK));
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            showToast("Wrong facebook profile link");
        }
    }

    @OnClick(R.id.instagramIV)
    public void onInstagramClick() {
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.SUPPORT_INSTAGRAM_LINK));
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            showToast("Wrong facebook profile link");
        }
    }

    @OnClick(R.id.twtIV)
    public void onTwitterClick() {
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.SUPPORT_TWITTER_LINK));
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            showToast("Wrong facebook profile link");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(mContext, BS_HomeActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, BS_ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_request_list) {
            startActivity(new Intent(mContext, BS_RequestListActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, BS_NotificationActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, BS_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_logout) {
            logout();
        }else if (id == R.id.nav_customer_signed) {
            startActivity(new Intent(mContext, BS_CustomerSignedUp.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(mContext, BS_HomeActivity.class));
        }
    }
}
