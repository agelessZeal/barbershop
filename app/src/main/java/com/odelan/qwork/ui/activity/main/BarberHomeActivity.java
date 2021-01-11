package com.odelan.qwork.ui.activity.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.widget.HorizontalListView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BarberHomeActivity extends BaseActivity {

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

    @BindView(R.id.barbar_openBtn)
    Button barbarOpenBtn;

    @BindView(R.id.waitTV)
    TextView waitTV;

    @BindView(R.id.addTV)
    TextView addTV;

    @BindView(R.id.ratingBar)
    me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;

    @BindView(R.id.onlineStatusIV)
    de.hdodenhof.circleimageview.CircleImageView onlineStatusIV;

    @BindView(R.id.hListView)
    HorizontalListView hListView;

    @BindView(R.id.favIV)
    ImageView faveIV;

    public static User mUser = null;
    public static User mFromUser = null;
    public int isJoined;

    List<User> mCustomers = new ArrayList<>();
    HorizontalListViewAdapter adapter;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private Dialog customerProcessDlg;
    private AlertDialog addDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_home);

        mContext = this;
        ButterKnife.bind(this);

        //showTitleIV();
        hideTitleIV();
        setRightIV(R.drawable.ic_star_empty1);
        showRightIV();
        //hideTitle();

        LinearLayout rlAdd = (LinearLayout) findViewById(R.id.ll_add);
        LinearLayout rlJoin = (LinearLayout) findViewById(R.id.ll_join);

        if (mFromUser.user_type.equals(MyApplication.USER_CUSTOMER)) {
            openBtn.setEnabled(true);
            getCustomersAndAverageCuttingTimeIsJoined();
            rightIV.setVisibility(View.VISIBLE);

            rlJoin.setVisibility(View.VISIBLE);
            rlAdd.setVisibility(View.INVISIBLE);

        } else if (mFromUser.user_type.equals(MyApplication.USER_BARBER_SHOP)) {

            rlJoin.setVisibility(View.INVISIBLE);
            rlAdd.setVisibility(View.VISIBLE);

            rightIV.setVisibility(View.INVISIBLE);
            getCustomersAndAverageCuttingTime();
            openBtn.setEnabled(true);
            if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                openBtn.setText("Queue is opened");
                barbarOpenBtn.setText("Close Queue");
            } else {
                openBtn.setText("Queue is closed");
                barbarOpenBtn.setText("Open Queue");
            }
        }

        rightIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFav();
            }
        });

        startTimer();
//        t.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        String barberId = mUser.userid;
//                        String customerId = mFromUser.userid;
//
//                        if (barberId.equals("") || customerId.equals("")) return;
//
//                        AndroidNetworking.post(MyApplication.BASE_URL + "order/getWaitingTime")
//                                .addBodyParameter("barber_id", barberId)
//                                .addBodyParameter("customer_id", customerId)
//                                .setTag("getWaitingTime")
//                                .setPriority(Priority.MEDIUM)
//                                .build()
//                                .getAsJSONObject(new JSONObjectRequestListener() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        // do anything with response
//                                        try {
//                                            String status = response.getString("status");
//                                            if (status.equals("1")) {
//                                                String nowWaitingTime = response.getString("now_waiting_time");
//                                                waitTV.setText("WaitTime: " + nowWaitingTime);
//                                            }
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onError(ANError anError) {
//
//                                    }
//                                });
//                    }
//                });
//            }
//        }, 0, 60000);
    }

    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 100, 60000); //
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            String barberId = mUser.userid;
                            String customerId = mFromUser.userid;

                            if (barberId.equals("") || customerId.equals("")) return;

                            AndroidNetworking.post(MyApplication.BASE_URL + "order/getWaitingTime")
                                    .addBodyParameter("barber_id", barberId)
                                    .addBodyParameter("customer_id", customerId)
                                    .setTag("getWaitingTime")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // do anything with response
                                            try {
                                                String status = response.getString("status");
                                                if (status.equals("1")) {
                                                    String nowWaitingTime = response.getString("now_waiting_time");
                                                    waitTV.setText(" " + nowWaitingTime);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError anError) {

                                        }
                                    });
                        } catch (java.lang.NullPointerException e) {

                        }
                    }
                });
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
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

    @OnClick(R.id.addQueueBtn)
    public void onAddGuestCustomer1() {

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

    public void showUserInfo() {
        if (mUser != null) {

            if (mFromUser.faivorit_barber_id.equals(mUser.userid)) {
                faveIV.setImageResource(R.drawable.ic_star_full1);
                setRightIV(R.drawable.ic_star_full1);
            } else {
                faveIV.setImageResource(R.drawable.ic_star_empty1);
                setRightIV(R.drawable.ic_star_empty1);
            }

            if (mUser.photo != null && !mUser.photo.equals("")) {
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mUser.photo).placeholder(R.drawable.ic_user).into(photoIV);
            }

            if (mUser.username != null && !mUser.username.equals("")) {
                //usernameTV.setText(mUser.username);
                setTitle(mUser.username);
                showTitle();
            }

            if (mUser.desc != null && !mUser.desc.equals("")) {
                //descriptionTV.setText(mUser.desc);
            }

            if (mUser.barbershop_name != null && !mUser.barbershop_name.equals("")) {
                barbershopNameTV.setText(mUser.barbershop_name);
                //bsIV.setVisibility(View.VISIBLE);
            } else {
                //bsIV.setVisibility(View.GONE);
            }

            ratingTV.setText("(" + mUser.rating + ")");

            float rating = Float.parseFloat(mUser.rating);
            ratingBar.setRating(rating);

            if (mFromUser.user_type.equals(MyApplication.USER_CUSTOMER)) {
                waitTV.setVisibility(View.VISIBLE);
//                addTV.setVisibility(View.GONE);
                openBtn.setEnabled(true);
            } else if (mFromUser.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
//                waitTV.setVisibility(View.VISIBLE);
                addTV.setVisibility(View.VISIBLE);
                openBtn.setEnabled(true);
                if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                    openBtn.setText("Queue is opened");
                    barbarOpenBtn.setText("Close Queue");
                } else {
                    openBtn.setText("Queue is closed");
                    barbarOpenBtn.setText("Open Queue");
                }
            }

            if (mUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                onlineStatusIV.setBackgroundResource(R.drawable.bg_green_status);
                photoIV.setBorderColor(ContextCompat.getColor(BarberHomeActivity.this, R.color.colorGreen));//Dharm
            } else {
                onlineStatusIV.setImageResource(R.drawable.back_offline);
                photoIV.setBorderColor(ContextCompat.getColor(BarberHomeActivity.this, R.color.tw__composer_red));//Dharm
            }
        }
    }

    public void setCustomers() {
        adapter = new HorizontalListViewAdapter(mContext, mCustomers);
        hListView.setAdapter(adapter);
    }

    public void getCustomersAndAverageCuttingTimeIsJoined() {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getCustomersAndAverageCuttingTimeIsJoined")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", mFromUser.userid)
                .setTag("getCustomersAndAverageCuttingTimeIsJoined")
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

                                mCustomers = new ArrayList<>();
                                mCustomers = LoganSquare.parseList(response.getString("customers"), User.class);
                                String total = response.getString("total_count");
                                //totalCountTV.setText("Total " + total);
                                int avg = response.getInt("average_cutting_time");
                                timePerCutTV.setText(" " + (avg / 60) + "min");
                                queueCountTV.setText("(" + mCustomers.size() + ")");
                                isJoined = response.getInt("isJoined");
                                int myOrder = 0;
                                int i = 0;
                                for (User u : mCustomers) {
                                    if (u.userid.equals(mFromUser.userid)) {
                                        myOrder = i;
                                    }
                                    i++;
                                }

                                int nowCuttingTime = response.getInt("now_cutting_time");
                                int wtime = 0;
                                if (myOrder > 0) {
                                    wtime = avg * (myOrder - 1) + (avg - nowCuttingTime);
                                } else {
                                    wtime = avg * (mCustomers.size() - 1) + (avg - nowCuttingTime);
                                }

                                if (wtime < 0) wtime = 0;

                                int h = 0;
                                int m = wtime;
                                if (wtime >= 3600) {
                                    h = wtime / 3600;
                                    m = (wtime % 3600) / 60;
                                    waitTV.setText(" " + h + "h " + m + "min");
                                } else {
                                    m = wtime / 60;
                                    waitTV.setText(" " + m + "min");
                                }

                                if (isJoined == 1) {
                                    openBtn.setText("Leave Queue");
                                    barbarOpenBtn.setText("Close Queue");
                                } else {
                                    openBtn.setText("Join Queue");
                                    barbarOpenBtn.setText("Open Queue");
                                }

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
                                    showToast("Not exist no longer");
                                    return;
                                }

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

    public void onFav() {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "Faivorit/setFaivorit")
                .addBodyParameter("from_id", mFromUser.userid)
                .addBodyParameter("to_id", mUser.userid)
                .setTag("setFaivorit")
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

                                int isFaivorit = response.getInt("isFaivorit");
                                if (isFaivorit == 1) {
                                    faveIV.setImageResource(R.drawable.ic_star_full1);
                                    setRightIV(R.drawable.ic_star_full1);
                                } else {
                                    faveIV.setImageResource(R.drawable.ic_star_empty1);
                                    setRightIV(R.drawable.ic_star_empty1);
                                }

                                JSONObject user = response.getJSONObject("user");
                                saveKeyValue("me", user.toString());
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

    @OnClick(R.id.favRL)
    public void onFavClick() {
        onFav();
    }

    @OnClick(R.id.barbershopNameTV)
    public void onBarbershopInfoClick() {
        if (mUser.barbershop_id != null && !mUser.barbershop_id.equals("")) {
            BarbershopHomeActivity.mUserId = mUser.barbershop_id;
            BarbershopHomeActivity.mFromUser = mFromUser;
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
                showToast("Wrong facebook profile link " + mUser.instagram_profile_link);
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
    public void onJoinLeaveClick() {
        if (mFromUser.user_type.equals(MyApplication.USER_CUSTOMER)) {
            if (isJoined == 1) {
                new MaterialDialog.Builder(this)
                        .title("Warning")
                        .content("You won’t be able to join a queue again today if you leave this queue after 2 minutes. Are you sure you want to leave queue?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                                joinOrLeave();
                            }
                        })
                        .show();
            } else {
                joinOrLeave();
            }
        } else {
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
    }

    @OnClick(R.id.barbar_openBtn)
    public void onJoinLeaveClick1() {
        if (mFromUser.user_type.equals(MyApplication.USER_CUSTOMER)) {
            if (isJoined == 1) {
                new MaterialDialog.Builder(this)
                        .title("Warning")
                        .content("You won’t be able to join a queue again today if you leave this queue after 2 minutes. Are you sure you want to leave queue?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                                joinOrLeave();
                            }
                        })
                        .show();
            } else {
                joinOrLeave();
            }
        } else {
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
                                        barbarOpenBtn.setText("Close Queue");
                                    } else {
                                        onlineStatusIV.setImageResource(R.drawable.back_offline);
                                        openBtn.setText("Open Queue");
                                        barbarOpenBtn.setText("Open Queue");
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

    @OnClick(R.id.photoIV)
    public void onPhotoClick() {
        ImagesActivity.userId = mUser.userid;
        ImagesActivity.isEditable = false;
        startActivity(new Intent(mContext, ImagesActivity.class));
    }

    @OnClick(R.id.ratingLL)
    public void onRatingClick() {
        BarberReviewsActivity.mUser = mUser;
        startActivity(new Intent(mContext, BarberReviewsActivity.class));
    }

    public void joinOrLeave() {
        if (mUser.online_status.equals(MyApplication.STATUS_ONLINE) || (mUser.online_status.equals(MyApplication.STATUS_OFFLINE) && isJoined == 1)) {
            showSecondaryCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "order/joinLeaveQueue")
                    .addBodyParameter("barber_id", mUser.userid)
                    .addBodyParameter("customer_id", mFromUser.userid)
                    .setTag("joinLeaveQueue")
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
                                    mCustomers = new ArrayList<>();
                                    mCustomers = LoganSquare.parseList(response.getString("customers"), User.class);
                                    String total = response.getString("total_count");
                                    //totalCountTV.setText("Total " + total);
                                    int avg = response.getInt("average_cutting_time");
                                    timePerCutTV.setText(" " + (avg / 60) + "min");
                                    queueCountTV.setText("(" + mCustomers.size() + ")");
                                    isJoined = response.getInt("isJoined");
                                    int myOrder = 0;
                                    int i = 0;
                                    for (User u : mCustomers) {
                                        if (u.userid.equals(mFromUser.userid)) {
                                            myOrder = i;
                                        }
                                        i++;
                                    }
                                    int nowCuttingTime = response.getInt("now_cutting_time");
                                    int wtime = 0;
                                    if (myOrder > 0) {
                                        wtime = avg * (myOrder - 1) + (avg - nowCuttingTime);
                                    } else {
                                        wtime = avg * (mCustomers.size() - 1) + (avg - nowCuttingTime);
                                    }

                                    if (wtime < 0) wtime = 0;

                                    int h = 0;
                                    int m = wtime;
                                    if (wtime >= 3600) {
                                        h = wtime / 3600;
                                        m = (wtime % 3600) / 60;
                                        waitTV.setText(" " + h + "h " + m + "min");
                                    } else {
                                        m = wtime / 60;
                                        waitTV.setText(" " + m + "min");
                                    }

                                    if (isJoined == 1) {
                                        openBtn.setText("Leave Queue");
                                        barbarOpenBtn.setText("Close Queue");
                                    } else {
                                        openBtn.setText("Join Queue");
                                        barbarOpenBtn.setText("Open Queue");
                                    }

                                    showUserInfo();
                                    setCustomers();
                                } else if (status.equals("0")) {
                                    String error = response.getString("error");
                                    showToast(error);
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
        } else {
            showToast("Queue is closed");
        }
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

                boolean isAna = user.is_ana != null
                        && user.is_ana.equalsIgnoreCase("1")
                        && mFromUser.user_type.equalsIgnoreCase(MyApplication.USER_CUSTOMER)
                        && !mFromUser.userid.equalsIgnoreCase(user.userid);
                usernameTV.setText(isAna ? "Anonymous" : user.username);

                final TextView tvUserStatus = (TextView) v.findViewById(R.id.tv_user_status);
                final boolean isStarted = user.online_status.equals("start");

                int position1 = position;

//                photoIV.setBorderColor(ContextCompat.getColor(BarberHomeActivity.this, position == 0 ? isStarted ? R.color.colorGreen : R.color.colorAmber : R.color.white));//Dharm
//                photoIV.setBorderColor(Color.parseColor("#F7EC51"));

                tvUserStatus.setText(position == 0 ? isStarted ? "  Cutting  " : "  Waiting  " : "");
                tvUserStatus.setTextColor(ContextCompat.getColor(BarberHomeActivity.this, R.color.white));

                if (position == 0) {
                    getUserStatusAtPosition(tvUserStatus, user, position);
                }

                ImageView iv = (ImageView) v.findViewById(R.id.photoIV);
                if (!isAna && user.photo != null && !user.photo.equals("")) {
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
                        if (mFromUser.user_type.equalsIgnoreCase(MyApplication.USER_BARBER_SHOP)
                                || mFromUser.user_type.equalsIgnoreCase(MyApplication.USER_BARBER))
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
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setRemove")
                .addBodyParameter("barber_id", mUser.userid)
                .addBodyParameter("customer_id", c_id)
                .addBodyParameter("reason", reason)
                .setTag("setCutStart")
//                .setTag("setRemove")
                .setPriority(Priority.MEDIUM).build()
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

                            Log.d("Dharm", response.toString());
                            Log.v("Dharm", response.toString());

                            String status = response.getString("status");
                            if (status.equals("1")) {
                                String order_status = response.getString("order_status");
                                final boolean isStarted = order_status.equalsIgnoreCase("start");
//                                photoIV.setBorderColor(ContextCompat.getColor(BarberHomeActivity.this, isStarted ? R.color.colorGreen  : R.color.tw__composer_red));
                                tvUserStatus.setText(position == 0 ? isStarted ? "  Cutting  " : "  Waiting  " : "");
                                tvUserStatus.setTextColor(ContextCompat.getColor(BarberHomeActivity.this, R.color.white));
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
