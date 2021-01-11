package com.odelan.qwork.ui.activity.main.barbershop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.BarberHomeActivity;
import com.odelan.qwork.ui.activity.main.ImagesActivity;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.TabAdapter;
import com.odelan.qwork.widget.HorizontalListView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BS_HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    @BindView(R.id.profileTV)
    TextView profileTV;

    @BindView(R.id.locationTV)
    TextView locationTV;

    @BindView(R.id.openTimeTV)
    TextView openTimeTV;

    @BindView(R.id.firstTabLL)
    LinearLayout firstTabLL;

    @BindView(R.id.secondTabLL)
    LinearLayout secondTabLL;

    @BindView(R.id.thirdTabLL)
    LinearLayout thirdTabLL;

    @BindView(R.id.photoIV)
    ImageView photoIV;

    @BindView(R.id.usernameTV)
    TextView usernameTV;

    @BindView(R.id.descriptionTV)
    TextView descriptionTV;

    @BindView(R.id.totalQueueTV)
    TextView totalQueueTV;

    @BindView(R.id.onlineTV)
    TextView onlineTV;

    @BindView(R.id.offlineTV)
    TextView offlineTV;

//    @BindView(R.id.workTimeTV)
//    TextView workTimeTV;

    @BindView(R.id.monTimeTV)
    TextView monTimeTV;

    @BindView(R.id.tueTimeTV)
    TextView tueTimeTV;

    @BindView(R.id.wedTimeTV)
    TextView wedTimeTV;

    @BindView(R.id.thrTimeTV)
    TextView thrTimeTV;

    @BindView(R.id.friTimeTV)
    TextView friTimeTV;

    @BindView(R.id.satTimeTV)
    TextView satTimeTV;

    @BindView(R.id.sunTimeTV)
    TextView sunTimeTV;

    @BindView(R.id.onHScrollview)
    HorizontalListView onHListView;

    @BindView(R.id.offHScrollview)
    HorizontalListView offHListView;

    @BindView(R.id.ratingBar)
    me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;

    @BindView(R.id.ratingTV)
    TextView ratingTV;
    @BindView(R.id.bannerimage)
    RelativeLayout bannerimage;

    @BindView(R.id.socialbtnslayout)
    LinearLayout socialbtnslayout;

    List<User> mBarbers;
    HorizontalListViewAdapter onlineAdapter;
    HorizontalListViewAdapter offlineAdapter;

    TabAdapter tabAdapter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs__home);

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

        ArrayList<TextView> mTabs = new ArrayList<>();
        mTabs.add(profileTV);
        mTabs.add(locationTV);
        mTabs.add(openTimeTV);

        ArrayList<ViewGroup> mTabContents = new ArrayList<>();
        mTabContents.add(firstTabLL);
        mTabContents.add(secondTabLL);
        mTabContents.add(thirdTabLL);

        tabAdapter = new TabAdapter(mTabs, mTabContents);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        User me = getUser(getValueFromKey("me"));
        setLayout(me);
    }

    public void setLayout(User me) {
        if (me != null) {
            if (me.photo != null && !me.photo.equals("")) {
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + me.photo).fit().placeholder(R.drawable.ic_user).into(photoIV);
            }

            if (me.username != null && !me.username.equals("")) {
                usernameTV.setText(me.username);
            }

            if (me.desc != null && !me.desc.equals("")) {
                descriptionTV.setText(me.desc);
            }

            if (me.totalCustomerCount != null && !me.totalCustomerCount.equals("")) {
                totalQueueTV.setText(me.totalCustomerCount);
            }

            List<String> closeStateList = Arrays.asList(me.close_state.split("\\s*,\\s*"));
            for (int i = 1; i < 8; i++) {
                if (i == 1) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        monTimeTV.setText("Closed");
                    } else {
                        monTimeTV.setText("");
                        if (me.mon_time_start != "" && me.mon_time_end != "") {
                            monTimeTV.setText(me.mon_time_start + " - " + me.mon_time_end);
                        }
                    }
                } else if (i == 2) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        tueTimeTV.setText("Closed");
                    } else {
                        tueTimeTV.setText("");
                        if (me.tue_time_start != "" && me.tue_time_end != "") {
                            tueTimeTV.setText(me.tue_time_start + " - " + me.tue_time_end);
                        }
                    }
                } else if (i == 3) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        wedTimeTV.setText("Closed");
                    } else {
                        wedTimeTV.setText("");
                        if (me.wed_time_start != "" && me.wed_time_end != "") {
                            wedTimeTV.setText(me.wed_time_start + " - " + me.wed_time_end);
                        }
                    }
                } else if (i == 4) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        thrTimeTV.setText("Closed");
                    } else {
                        thrTimeTV.setText("");
                        if (me.thr_time_start != "" && me.thr_time_end != "") {
                            thrTimeTV.setText(me.thr_time_start + " - " + me.sat_time_end);
                        }

                    }
                } else if (i == 5) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        friTimeTV.setText("Closed");
                    } else {
                        friTimeTV.setText("");
                        if (me.fri_time_start != "" && me.fri_time_end != "") {
                            friTimeTV.setText(me.fri_time_start + " - " + me.fri_time_end);
                        }
                    }
                } else if (i == 6) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        satTimeTV.setText("Closed");
                    } else {
                        satTimeTV.setText("");
                        if (me.sat_time_start != "" && me.sat_time_end != "") {
                            satTimeTV.setText(me.sat_time_start + " - " + me.sat_time_end);
                        }
                    }
                } else {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        sunTimeTV.setText("Closed");
                    } else {
                        sunTimeTV.setText("");
                        if (me.sun_time_start != null && me.sun_time_end != null) {
                            sunTimeTV.setText(me.sun_time_start + " - " + me.sun_time_end);
                        }
                    }
                }
            }

            if (me.rating != null) {
                float rating = Float.parseFloat(me.rating);
                ratingBar.setRating(rating);
                ratingTV.setText(me.rating);
            }
        }

        getBarbers(me.userid);
    }

    @OnClick(R.id.photoIV)
    public void photoClick() {
        User me = getUser(getValueFromKey("me"));
        ImagesActivity.userId = me.userid;
        ImagesActivity.isEditable = false;
        startActivity(new Intent(mContext, ImagesActivity.class));
    }
    @OnClick(R.id.addbarberbtn)
    public void photoClick1() {
        Intent i = new Intent(mContext, BS_AddBarberActivity.class);
        startActivityForResult(i, 101);
    }

    @OnClick(R.id.fbIV)
    public void onFacebookClick() {
        User me = getUser(getValueFromKey("me"));
        if (me != null && me.fb_profile_link != null && !me.fb_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(me.fb_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no facebook profile.");
        }
    }

    @OnClick(R.id.instagramIV)
    public void onInstagramClick() {
        User me = getUser(getValueFromKey("me"));
        if (me != null && me.instagram_profile_link != null && !me.instagram_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(me.instagram_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no instagram profile.");
        }
    }

    @OnClick(R.id.twtIV)
    public void onTwitterClick() {
        User me = getUser(getValueFromKey("me"));
        if (me != null && me.twt_profile_link != null && !me.twt_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(me.twt_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no twitter profile.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        User me = getUser(getValueFromKey("me"));
        if (me != null) {
            if (me.lati != null && me.lang != null) {
                try {
                    double lati = Double.parseDouble(me.lati);
                    double lang = Double.parseDouble(me.lang);

                    LatLng sydney = new LatLng(lati, lang);
                    if (me.address != null) {
                        mMap.addMarker(new MarkerOptions().position(sydney).title(me.address));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.profileTV)
    public void onProfileClick() {
        socialbtnslayout.setVisibility(View.VISIBLE);
        bannerimage.setVisibility(View.GONE);
        tabAdapter.onTabClick(profileTV);
    }

    @OnClick(R.id.locationTV)
    public void onLocationClick() {
        tabAdapter.onTabClick(locationTV);
    }

    @OnClick(R.id.openTimeTV)
    public void onOpenTimeClick() {
        tabAdapter.onTabClick(openTimeTV);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, BS_ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_request_list) {
            startActivity(new Intent(mContext, BS_RequestListActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, BS_NotificationActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, BS_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_tutorial) {
            startActivity(new Intent(mContext, TutorialActivity.class)
                    .putExtra("launch_type",TutorialActivity.LAUNCH_HOME)
                    .putExtra("userType",MyApplication.USER_BARBER_SHOP));
            finish();
        } else if (id == R.id.nav_contact_us) {
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

    @OnClick(R.id.refreshIV)
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        User me = getUser(getValueFromKey("me"));
        getUserWithId(me.userid);
    }

    public void getUserWithId(String userid) {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getUserWithId")
                .addBodyParameter("user_id", userid)
                .setTag("getUserWithId")
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
                                User me = LoganSquare.parse(response.getString("data"), User.class);
                                if (me == null || TextUtils.isEmpty(me.userid)) {
                                    gotoLogin();
                                    return;
                                }

                                saveKeyValue("me", response.getString("data"));
                                setLayout(me);
                            } else if (status.equals("false")) {
                                showToast("Fail to refresh");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
Log.v("requestCode",requestCode+"");
        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK){
              //  String result=data.getStringExtra("result");
                showToast("Successfully Created");
                User me = getUser(getValueFromKey("me"));
                getBarbers(me.userid);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public void getBarbers(String userid) {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getBarbersInOneBarbershop")
                .addBodyParameter("bs_id", userid)
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
                                mBarbers = new ArrayList<>();
                                mBarbers = LoganSquare.parseList(response.getString("data"), User.class);
                                setOnlineOfflineBarbers();
                            } else if (status.equals("false")) {
                                showToast("Fail to get barbers");
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

    public void setOnlineOfflineBarbers() {
        List<User> onlineUsers = new ArrayList<>();
        List<User> offlineUsers = new ArrayList<>();

        for (User u : mBarbers) {
            if (u.online_status.equals(MyApplication.STATUS_ONLINE)) {
                onlineUsers.add(u);
            } else {
                offlineUsers.add(u);
            }
        }

        onlineTV.setText("Online(" + onlineUsers.size() + ")");
        offlineTV.setText("Offline(" + offlineUsers.size() + ")");

        onlineAdapter = new HorizontalListViewAdapter(mContext, onlineUsers);
        onHListView.setAdapter(onlineAdapter);

        offlineAdapter = new HorizontalListViewAdapter(mContext, offlineUsers);
        offHListView.setAdapter(offlineAdapter);
    }

    private final class HorizontalListViewAdapter extends BaseAdapter {

        private String TAG = "HorizontalListViewAdapter";
        private List<User> mUsers = null;
        private Context context;

        public HorizontalListViewAdapter(Context con, List<User> users) {
            super();
            context = con;
            mUsers = users;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final User barber = mUsers.get(position);
            if (v == null) {
                if (barber.online_status.equals(MyApplication.STATUS_ONLINE)) {
                    v = LayoutInflater.from(context).inflate(R.layout.list_item_online, parent, false);
                } else {
                    v = LayoutInflater.from(context).inflate(R.layout.list_item_offline, parent, false);
                }
            }

            if (v != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.photoIV);
                if (barber.photo != null && !barber.photo.equals("")) {
                    Picasso.with(context).load(MyApplication.ASSET_BASE_URL + barber.photo).placeholder(R.drawable.ic_user).into(iv);
                }
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //showToast(barber.username);
                        User me = getUser(getValueFromKey("me"));
                        BarberHomeActivity.mUser = barber;
                        BarberHomeActivity.mFromUser = me;
                        startActivity(new Intent(mContext, BarberHomeActivity.class));
                    }
                });

                iv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        new MaterialDialog.Builder(mContext)
                                .title("Confirm")
                                .content("Are you sure remove this user?")
                                .positiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        // TODO
                                        User me = getUser(getValueFromKey("me"));
                                        showCustomLoadingView();
                                        AndroidNetworking.post(MyApplication.BASE_URL + "user/removeBarberFromBarbershop")
                                                .addBodyParameter("b_id", barber.userid)
                                                .addBodyParameter("bs_id", me.userid)
                                                .setTag("removeBarberFromBarbershop")
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
                                                                mBarbers = new ArrayList<>();
                                                                mBarbers = LoganSquare.parseList(response.getString("data"), User.class);
                                                                setOnlineOfflineBarbers();
                                                            } else if (status.equals("false")) {
                                                                showToast("Fail to get barbers");
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
                                })
                                .negativeText("Cancel")
                                .show();
                        return true;
                    }
                });

                TextView countTV = (TextView) v.findViewById(R.id.countTV);
                if (barber.cur_count == null) {
                    barber.cur_count = "0";
                }
                countTV.setText(barber.cur_count);

                TextView nameTV = (TextView) v.findViewById(R.id.nameTV);
                if (barber.username != null) {
                    nameTV.setText(barber.username);
                }

                TextView timeTV = (TextView) v.findViewById(R.id.timeTV);
                if (barber.total_turn_time != null && !barber.total_turn_time.isEmpty()) {
                    timeTV.setText(Integer.parseInt(barber.total_turn_time) / 60 + " min");
                }
            }
            return v;
        }

        public void add(User u) {
            mUsers.add(u);
            notifyDataSetChanged();
        }
    }
}
