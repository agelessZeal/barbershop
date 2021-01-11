package com.odelan.qwork.ui.activity.main.barbershop;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
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

public class BS_NotificationActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bCB)
    CheckBox bCB;

    @BindView(R.id.bsCB)
    CheckBox bsCB;

    @BindView(R.id.hListView)
    HorizontalListView hListView;

    @BindView(R.id.messageET)
    EditText messageET;

    @BindView(R.id.sendBtn)
    Button sendBtn;

    List<NotifyUser> mBarbers;
    List<NotifyUser> mCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs__notification);

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

        bCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b;
                if (bCB.isChecked()) {
                    b = true;
                } else {
                    b = false;
                }
                for (NotifyUser u : mBarbers) {
                    u.isSelected = b;
                }
                hListView.setAdapter(new HorizontalListViewAdapter());
            }
        });

        getAllBarbers();
    }

    public void getAllBarbers() {
        User me = getUser(getValueFromKey("me"));

        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getBarbersInOneBarbershop")
                .addBodyParameter("bs_id", me.userid)
                .setTag("getBarbersInOneBarbershop")
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
                                List<User> barbers = new ArrayList<User>();
                                barbers = LoganSquare.parseList(response.getString("data"), User.class);
                                mBarbers = new ArrayList<NotifyUser>();
                                for (User u : barbers) {
                                    mBarbers.add(new NotifyUser(u));
                                }
                                setBarbers();
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

    public void setBarbers() {
        hListView.setAdapter(new HorizontalListViewAdapter());
    }

    @OnClick(R.id.sendBtn)
    public void onSend() {
        User me = getUser(getValueFromKey("me"));

        if (messageET.getText().toString() == null || messageET.getText().toString().equals("")) {
            showToast("Please input message");
            return;
        }

        String b_ids = "";
        for (NotifyUser u : mBarbers) {
            if (b_ids.equals("")) {
                b_ids = u.user.userid;
            } else {
                b_ids += "," + u.user.userid;
            }
        }

        String all_customers = "NO";
        if (bsCB.isChecked()) {
            all_customers = "YES";
        }

        if (bsCB.isChecked() == false && b_ids.equals("")) {
            showToast("Please choose users");
            return;
        }

        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "notification/sendNotificationFromBarbershop")
                .addBodyParameter("sender_id", me.userid)
                .addBodyParameter("barber_ids", b_ids)
                .addBodyParameter("all_customer", all_customers)
                .addBodyParameter("message", "A message from your BarberShop : " + MyApplication.NOTIFICATION_SPLIT + messageET.getText().toString())
                .setTag("sendNotificationFromBarbershop")
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
                                bCB.setChecked(false);
                                bsCB.setChecked(false);
                                messageET.setText("");
                                for (NotifyUser u : mBarbers) {
                                    u.isSelected = false;
                                }
                                setBarbers();
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

    private final class HorizontalListViewAdapter extends BaseAdapter {

        public HorizontalListViewAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mBarbers.size();
        }

        @Override
        public Object getItem(int position) {
            return mBarbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final NotifyUser user = mBarbers.get(position);
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
                            bCB.setChecked(false);
                        } else {
                            markIV.setVisibility(View.VISIBLE);
                        }
                        mBarbers.get(position).isSelected = !mBarbers.get(position).isSelected;
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

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, BS_HistoryActivity.class));
            finish();
        }else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(mContext, BS_ContactActivity.class));
            finish();
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
