package com.odelan.qwork.ui.activity.main.barber;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.NotifyUser;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.widget.HorizontalListView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class B_NotificationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.checkbox)
    CheckBox checkBox;

    @BindView(R.id.hListView)
    HorizontalListView hListView;

    @BindView(R.id.messageET)
    EditText messageET;

    @BindView(R.id.sendBtn)
    Button sendBtn;

    List<NotifyUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b__notification);

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

        getAllCustomers();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b;
                if (checkBox.isChecked()) {
                    b = true;
                } else {
                    b = false;
                }
                for (NotifyUser u : mUsers) {
                    u.isSelected = b;
                }
                hListView.setAdapter(new HorizontalListViewAdapter());
            }
        });
    }

    public void getAllCustomers() {
        User me = getUser(getValueFromKey("me"));

        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getCustomersWithBarberId")
                .addBodyParameter("barber_id", me.userid)
                .setTag("getCustomersWithBarberId")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                List<User> mCustomers = new ArrayList<User>();
                                mCustomers = LoganSquare.parseList(response.getString("data"), User.class);
                                mUsers = new ArrayList<NotifyUser>();
                                for (User u : mCustomers) {
                                    mUsers.add(new NotifyUser(u));
                                }
                                setCustomers();
                            } else if (status.equals("0")) {
                                showToast("Fail");
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

    public void setCustomers() {
        hListView.setAdapter(new HorizontalListViewAdapter());
    }

    @OnClick(R.id.sendBtn)
    public void onSend() {
        if (messageET.getText().toString() == null && messageET.getText().toString().equals("")) {
            showToast("Please input message");
        } else {
            String rids = "";
            int i = 0;
            for (NotifyUser u : mUsers) {
                if (u.isSelected) {
                    if (TextUtils.isEmpty(rids)) {
                        rids += u.user.userid;
                    } else {
                        rids += "," + u.user.userid;
                    }
                }
                i++;
            }

            if (rids.equals("")) {
                showToast("Please choose users");
            } else {
                User me = getUser(getValueFromKey("me"));
                showSecondaryCustomLoadingView();
                AndroidNetworking.post(MyApplication.BASE_URL + "notification/sendNotificationFromBarber")
                        .addBodyParameter("sender_id", me.userid)
                        .addBodyParameter("rids", rids)
                        .addBodyParameter("message", "A message from your Barber : " + MyApplication.NOTIFICATION_SPLIT + messageET.getText().toString())
                        .setTag("sendNotificationFromBarber")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                hideCustomLoadingView();
                                try {
                                    String status = response.getString("status");
                                    if (status.equals("1")) {
                                        checkBox.setChecked(false);
                                        messageET.setText("");
                                        for (NotifyUser u : mUsers) {
                                            u.isSelected = false;
                                        }
                                        setCustomers();
                                        showToast("Sent");
                                    } else if (status.equals("0")) {
                                        showToast("Fail");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(mContext, B_HomeActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, B_ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, B_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(mContext, B_ContactActivity.class));
            finish();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private final class HorizontalListViewAdapter extends BaseAdapter {

        public HorizontalListViewAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final NotifyUser user = mUsers.get(position);
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.list_item_notification, parent, false);
            }

            if (v != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.photoIV);
                if (user.user.photo != null && !user.user.photo.equals("")) {
                    Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + user.user.photo).into(iv);
                }

                final ImageView markIV = (ImageView) v.findViewById(R.id.markIV);

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user.isSelected) {
                            markIV.setVisibility(View.GONE);
                            checkBox.setChecked(false);
                        } else {
                            markIV.setVisibility(View.VISIBLE);
                        }
                        mUsers.get(position).isSelected = !mUsers.get(position).isSelected;
                    }
                });

                if (user.isSelected) {
                    markIV.setVisibility(View.VISIBLE);
                } else {
                    markIV.setVisibility(View.GONE);
                }
                TextView usernameTV = (TextView) v.findViewById(R.id.usernameTV);
                usernameTV.setText(user.user.username);
            }
            return v;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(mContext, B_HomeActivity.class));
        }


    }

}
