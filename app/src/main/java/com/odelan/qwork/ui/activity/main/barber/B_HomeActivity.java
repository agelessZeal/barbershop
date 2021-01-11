package com.odelan.qwork.ui.activity.main.barber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.BarberReviewsActivity;
import com.odelan.qwork.ui.activity.main.BarbershopHomeActivity;
import com.odelan.qwork.ui.activity.main.ImagesActivity;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.widget.HorizontalListView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class B_HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.photoIV)
    com.makeramen.roundedimageview.RoundedImageView photoIV;

    @BindView(R.id.barbershopNameTV)
    TextView barbershopNameTV;

    @BindView(R.id.timePerCutTV)
    TextView timePerCutTV;

    @BindView(R.id.ratingTV)
    TextView ratingTV;

    @BindView(R.id.queueCountTV)
    TextView queueCountTV;

    @BindView(R.id.openBtn)
    Button openBtn;

    @BindView(R.id.addTV)
    TextView addTV;

    @BindView(R.id.ratingBar)
    me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;

    @BindView(R.id.onlineStatusIV)
    de.hdodenhof.circleimageview.CircleImageView onlineStatusIV;

    @BindView(R.id.hListView)
    HorizontalListView hListView;

    TextView titleTV;

    User mUser = null;
    List<User> mCustomers = new ArrayList<>();
    HorizontalListViewAdapter adapter;

    Dialog addDlg = null;
    Dialog customerProcessDlg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b__home);

//        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));



//        Integer i = null;
//
//        i.byteValue();


        //mContext = this;
        ButterKnife.bind(this);

        titleTV = (TextView) findViewById(R.id.titleTV);

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
        mUser = getUser(getValueFromKey("me"));
        getCustomersAndAverageCuttingTime();


    }

    public void showUserInfo() {
        if (mUser != null) {
            if (mUser.photo != null && !mUser.photo.equals("")) {
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mUser.photo).placeholder(R.drawable.ic_user).into(photoIV);
            }

            if (mUser.username != null && !mUser.username.equals("")) {
                //usernameTV.setText(mUser.username);
                titleTV.setText(mUser.username);
            }

            if (mUser.desc != null && !mUser.desc.equals("")) {
                //descriptionTV.setText(mUser.desc);
            }

            if (mUser.barbershop_name != null && !mUser.barbershop_name.equals("")) {
                barbershopNameTV.setText(mUser.barbershop_name);
            } else {
            }

            ratingTV.setText("(" + mUser.rating + ")");

            float rating = Float.parseFloat(mUser.rating);
            ratingBar.setRating(rating);

            if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                onlineStatusIV.setBackgroundResource(R.drawable.bg_green_status);
                photoIV.setBorderColor(ContextCompat.getColor(B_HomeActivity.this, R.color.colorGreen));//Dharm
                openBtn.setText("Close Queue");
            } else {
                onlineStatusIV.setImageResource(R.drawable.back_offline);
                photoIV.setBorderColor(ContextCompat.getColor(B_HomeActivity.this, R.color.tw__composer_red));//Dharm
                openBtn.setText("Open Queue");
            }
        }
    }

    public void setCustomers() {
        adapter = new HorizontalListViewAdapter(mContext, mCustomers);
        hListView.setAdapter(adapter);
    }

    @OnClick(R.id.refreshIV)
    public void onRefresh() {
        refresh();
    }

    public void refresh() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getCustomersAndAverageCuttingTime();
            }
        });

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
            startActivity(new Intent(mContext, B_ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, B_NotificationActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, B_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_tutorial) {
            startActivity(new Intent(mContext, TutorialActivity.class)
                    .putExtra("launch_type",TutorialActivity.LAUNCH_HOME)
                    .putExtra("userType",MyApplication.USER_BARBER));
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

    public void getCustomersAndAverageCuttingTime() {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getCustomersAndAverageCuttingTime")
                .addBodyParameter("barber_id", mUser.userid)
                .setTag("getCustomersAndAverageCuttingTime")
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
                                mUser = LoganSquare.parse(response.getString("barber"), User.class);
                                if (mUser == null || TextUtils.isEmpty(mUser.userid)) {
                                    gotoLogin();
                                    return;
                                }

                                saveKeyValue("me", response.getString("barber"));
                                mCustomers = new ArrayList<>();
                                mCustomers = LoganSquare.parseList(response.getString("customers"), User.class);
                                String total = response.getString("total_count");
                                //totalCountTV.setText("Total " + total);
                                int avg = response.getInt("average_cutting_time");
                                timePerCutTV.setText(" " + (avg / 60) + "min");
                                queueCountTV.setText("(" + mCustomers.size() + ")");
                                showUserInfo();
                                setCustomers();
                            } else if (status.equals("false")) {
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

    @OnClick(R.id.barbershopNameTV)
    public void onBarbershopClick() {
        User me = getUser(getValueFromKey("me"));
        if (me.barbershop_id != null && !me.barbershop_id.equals("")) {
            BarbershopHomeActivity.mUserId = me.barbershop_id;
            BarbershopHomeActivity.mFromUser = me;
            startActivity(new Intent(mContext, BarbershopHomeActivity.class));
        }
    }

    @OnClick(R.id.fbIV)
    public void onFacebookClick() {
        if (mUser != null && mUser.fb_profile_link != null && !mUser.fb_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.fb_profile_link));
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
        if (mUser != null && mUser.instagram_profile_link != null && !mUser.instagram_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.instagram_profile_link));
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
        if (mUser != null && mUser.twt_profile_link != null && !mUser.twt_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.twt_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no twitter profile.");
        }
    }

    @OnClick(R.id.openBtn)
    public void onOpenTimeBtnClick() {
        if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
            new MaterialDialog.Builder(this)
                    .title("Warning")
                    .content("Are you sure you want to close queue?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                            openOrClose();
                        }
                    })
                    .show();
        } else {
            openOrClose();
        }
    }

    @OnClick(R.id.photoIV)
    public void onPhotoClick() {
        ImagesActivity.userId = mUser.userid;
        ImagesActivity.isEditable = true;
        startActivity(new Intent(mContext, ImagesActivity.class));
    }

    public void openOrClose() {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/online_offline")
                .addBodyParameter("barber_id", mUser.userid)
                .setTag("online_offline")
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
                                String online_status = response.getString("online_status");
                                String userStr = getValueFromKey("me");
                                try {
                                    JSONObject userObject = new JSONObject(userStr);
                                    userObject.put("online_status", online_status);
                                    userStr = userObject.toString();
                                    saveKeyValue("me", userStr);
                                    mUser = getUser(getValueFromKey("me"));
                                    if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                                        onlineStatusIV.setBackgroundResource(R.drawable.bg_green_status);
                                        openBtn.setText("Close Queue");
                                    } else {
                                        onlineStatusIV.setImageResource(R.drawable.back_offline);
                                        openBtn.setText("Open Queue");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (status.equals("false")) {
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



    @OnClick(R.id.addToQueueBtn)
    public void onAddToQueueGuestCustomer() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //inflate view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dlg_customer_add, null);

        /*final MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mContext)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Qwork")
                .withDialogAnimation(true)
                .setHeaderColor(R.color.colorPrimary)
                .setCustomView(customView, 20, 20, 20, 20)
                .setCancelable(true)
                .build();*/

        ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });

        Button addBtn = (Button) customView.findViewById(R.id.addBtn);
        final EditText usernameET = (EditText) customView.findViewById(R.id.usernameET);
        final TextView warningTV = (TextView) customView.findViewById(R.id.warningTV);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameET.getText().toString().equals("")) {
                    warningTV.setVisibility(View.VISIBLE);
                } else {
                    addDlg.dismiss();
                    addGuestCustomer(usernameET.getText().toString());
                }
            }
        });

        //dialog.show();

        builder.setView(customView);
        addDlg = builder.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reviewDlg.getWindow().getAttributes());
        lp.width = (int) Common.convertDpToPixel(300);
        reviewDlg.getWindow().setAttributes(lp);*/
    }

    @OnClick(R.id.addTV)
    public void onAddGuestCustomer() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //inflate view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dlg_customer_add, null);

        /*final MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mContext)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Qwork")
                .withDialogAnimation(true)
                .setHeaderColor(R.color.colorPrimary)
                .setCustomView(customView, 20, 20, 20, 20)
                .setCancelable(true)
                .build();*/

        ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });

        Button addBtn = (Button) customView.findViewById(R.id.addBtn);
        final EditText usernameET = (EditText) customView.findViewById(R.id.usernameET);
        final TextView warningTV = (TextView) customView.findViewById(R.id.warningTV);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usernameET.getText().toString().equals("")) {
                    warningTV.setVisibility(View.VISIBLE);
                } else {
                    addDlg.dismiss();
                    addGuestCustomer(usernameET.getText().toString());
                }
            }
        });

        //dialog.show();

        builder.setView(customView);
        addDlg = builder.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reviewDlg.getWindow().getAttributes());
        lp.width = (int) Common.convertDpToPixel(300);
        reviewDlg.getWindow().setAttributes(lp);*/
    }

    public void addGuestCustomer(String name) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/addCustomerManually")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("username", name)
                .setTag("addCustomerManually")
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
                                mCustomers = new ArrayList<>();
                                mCustomers = LoganSquare.parseList(response.getString("customers"), User.class);
                                queueCountTV.setText("(" + mCustomers.size() + ")");
                                setCustomers();
                            } else if (status.equals("false")) {
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

    @OnClick(R.id.ratingLL)
    public void onRatingClick() {
        BarberReviewsActivity.mUser = mUser;
        startActivity(new Intent(mContext, BarberReviewsActivity.class));
    }


    public void getUserStatus(final User customer, final int position) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getOrderStatus")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", customer.userid)
                .setTag("getOrderStatus")
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
                                String order_status = response.getString("order_status");
                                customer.online_status = order_status;
                                //if(order_status.equals("waiting") || order_status.equals("start")) {
                                showCustomerProcessDlg(customer, position);
//                                } else {
//                                    showToast("This user was left");
                                //}
                            } else if (status.equals("false")) {
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

    public void cutStart(String c_id) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setCutStart")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", c_id)
                .setTag("setCutStart")
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
                                showToast("Started");
                                setCustomers();
                            } else if (status.equals("false")) {
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

    public void cutEnd(String c_id) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setCutEnd")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", c_id)
                .setTag("setCutStart")
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
                                showToast("End");
                                int avg = response.getInt("avg_time") / 60;
                                timePerCutTV.setText(" " + avg + "min");
                                String total = response.getString("total_count");
                                //totalCountTV.setText("Total " + total);
                                List<User> customers = new ArrayList<User>();
                                boolean isFirst = true;
                                for (User u : mCustomers) {
                                    if (!isFirst) {
                                        customers.add(u);
                                    } else {
                                        isFirst = false;
                                    }
                                }
                                mCustomers = customers;
                                queueCountTV.setText(String.valueOf(mCustomers.size()));
                                setCustomers();
                            } else if (status.equals("false")) {
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

    public void removeCustomer(String c_id, String reason) {
        Log.v("Ashutosh", "barber_id : " + mUser.userid + " customer_id : " + c_id + " reason : " + reason);
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setRemove")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", c_id)
                .addBodyParameter("reason", reason)
                .setTag("setCutStart")
//                .setTag("setRemove")
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
                                showToast("Removed");
                                List<User> customers = new ArrayList<User>();
                                boolean isFirst = true;
                                for (User u : mCustomers) {
                                    if (!isFirst) {
                                        customers.add(u);
                                    } else {
                                        isFirst = false;
                                    }
                                }
                                mCustomers = customers;
                                queueCountTV.setText(String.valueOf(mCustomers.size()));
                                setCustomers();
                            } else if (status.equals("false")) {
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

    public void showCustomerProcessDlg(final User customer, int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //inflate view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dlg_customer_processing, null);

        /*final MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(mContext)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setTitle("Qwork")
                .withDialogAnimation(true)
                .setHeaderColor(R.color.colorPrimary)
                .setCustomView(customView, 20, 20, 20, 20)
                .setCancelable(true)
                .build();*/

        ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerProcessDlg.dismiss();
            }
        });
        TextView statusTV = (TextView) customView.findViewById(R.id.statusTV);
        Button startBtn = (Button) customView.findViewById(R.id.startBtn);
        Button endBtn = (Button) customView.findViewById(R.id.endBtn);
        Button removeBtn = (Button) customView.findViewById(R.id.removeBtn);
        final EditText reasonET = (EditText) customView.findViewById(R.id.reasonET);
        final TextView warningTV = (TextView) customView.findViewById(R.id.warningTV);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerProcessDlg.dismiss();
                cutStart(customer.userid);
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerProcessDlg.dismiss();
                cutEnd(customer.userid);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reasonET.getText().toString().equals("")) {
                    warningTV.setVisibility(View.VISIBLE);
                } else {
                    customerProcessDlg.dismiss();
                    removeCustomer(customer.userid, reasonET.getText().toString());
                }
            }
        });

        if (customer.online_status.equals("waiting")) {
            statusTV.setText("Waiting");

            if (position == 0) {
                startBtn.setEnabled(true);
                startBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                startBtn.setEnabled(false);
                startBtn.setBackgroundColor(getResources().getColor(R.color.disable_color));
            }

            endBtn.setEnabled(false);
            endBtn.setBackgroundColor(getResources().getColor(R.color.disable_color));
            removeBtn.setEnabled(true);
            removeBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            reasonET.setEnabled(true);
        } else if (customer.online_status.equals("start")) {
            statusTV.setText("Cutting");
            startBtn.setEnabled(false);
            startBtn.setBackgroundColor(getResources().getColor(R.color.disable_color));
            endBtn.setEnabled(true);
            endBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            removeBtn.setEnabled(false);
            removeBtn.setBackgroundColor(getResources().getColor(R.color.disable_color));
            reasonET.setEnabled(false);
        }
        //dialog.show();
        builder.setView(customView);
        customerProcessDlg = builder.show();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final User user = mUsers.get(position);
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.list_item_customer, parent, false);
            }

            if (v != null) {
                TextView orderTV = (TextView) v.findViewById(R.id.orderTV);
                if (position != 0)
                    orderTV.setText(String.valueOf(position));

                //~~~ Ashutosh - Updating user status
                TextView usernameTV = (TextView) v.findViewById(R.id.usernameTV);
                usernameTV.setText(user.username);

                final TextView tvUserStatus = (TextView) v.findViewById(R.id.tv_user_status);
                final boolean isStarted = user.online_status.equals("start");

//                photoIV.setBorderColor(ContextCompat.getColor(B_HomeActivity.this, position == 0 ? isStarted ? R.color.colorGreen : R.color.colorAmber : R.color.white));
                tvUserStatus.setText(position == 0 ? isStarted ? "   Cutting   " : "   Waiting   " : "");
                tvUserStatus.setTextColor(ContextCompat.getColor(B_HomeActivity.this, R.color.white));

                if (position == 0) {
                    getUserStatusAtPosition(tvUserStatus, user, position);
                }

                ImageView iv = (ImageView) v.findViewById(R.id.photoIV);
                if (user.photo != null && !user.photo.equals("")) {
                    Picasso.with(context).load(MyApplication.ASSET_BASE_URL + user.photo).placeholder(R.drawable.ic_user).into(iv);
                }

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        showToast(user.username);
                    }
                });

                iv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        getUserStatus(mUsers.get(position), position);
                        return false;
                    }
                });
            }
            return v;
        }

        public void add(User u) {
            mUsers.add(u);
            notifyDataSetChanged();
        }
    }

    private void getUserStatusAtPosition(final TextView tvUserStatus, final User user, final int position) {
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getOrderStatus")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", user.userid)
                .setTag("getOrderStatus")
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
                                String order_status = response.getString("order_status");
                                final boolean isStarted = order_status.equalsIgnoreCase("start");
//                                photoIV.setBorderColor(ContextCompat.getColor(B_HomeActivity.this, position == 0 ? isStarted ? R.color.colorGreen : R.color.colorAmber : R.color.white));
                                tvUserStatus.setText(position == 0 ? isStarted ? "   Cutting   " : "   Waiting   " : "");
                                tvUserStatus.setTextColor(ContextCompat.getColor(B_HomeActivity.this, R.color.white));
                                //if(order_status.equals("waiting") || order_status.equals("start")) {
//                                showCustomerProcessDlg(customer, position);
//                                } else {
//                                    showToast("This user was left");
                                //}
                            } else if (status.equals("false")) {
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
