package com.odelan.qwork.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.facebook.login.LoginManager;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.ReviewNotify;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.event.Event;
import com.odelan.qwork.ui.activity.intro.LoginActivity;
import com.odelan.qwork.ui.activity.main.barber.B_ContactActivity;
import com.odelan.qwork.ui.activity.main.barber.B_HomeActivity;
import com.odelan.qwork.ui.activity.main.barber.B_NotificationActivity;
import com.odelan.qwork.ui.activity.main.barber.B_ProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_ContactActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_HomeActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_NotificationActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_ProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity;
import com.odelan.qwork.ui.activity.main.customer.C_ContactActivity;
import com.odelan.qwork.ui.activity.main.customer.C_HistoryActivity;
import com.odelan.qwork.ui.activity.main.customer.C_HomeActivity;
import com.odelan.qwork.ui.activity.main.customer.C_ProfileActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.GPSTracker;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.odelan.qwork.utils.SessionUtils.KEY_CUST_COUNT;
import static com.odelan.qwork.utils.SessionUtils.KEY_USER_TYPE;


/**
 * Created by Administrator on 7/18/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public BaseActivity mContext;
    public String TAG = "BaseActivity";

    /**
     * UI
     */
    private ProgressDialog mProgressDialog;
    public ImageView leftIV, rightIV, titleIV;
    public TextView titleTV, rightTV;

    /**
     * Default TopBar ResID
     */
    final int DEFAULT_RES_ID_TOP_LEFT = R.drawable.ic_prev_white;
    final int DEFAULT_RES_ID_TOP_RIGHT = R.drawable.ic_next_white;

    /**
     * Topbar ResID
     */
    int RES_ID_TOP_LEFT = -1;
    int RES_ID_TOP_RIGHT = -1;


    /**
     * State of Top bar is get or not
     */
    boolean isGetTopbar = false;

    Dialog reviewDlg = null;

    boolean isAnony;

    public boolean msgShow = false;
    public String msg = "";
    public String senderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        EventBus.getDefault().register(this);

        if (this instanceof C_HomeActivity) {
            User me = getUser(getValueFromKey("me"));
            if (me != null || "barber".equals(me.user_type)) {
                try {
                    ReviewNotify notify = LoganSquare.parse(getValueFromKey("review"), ReviewNotify.class);
                    if (notify != null) {
                        showReviewDlg(notify.userName, notify.photo, notify.orderId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        MyApplication.curActivity = mContext;
        getTopBar();

        if (msgShow && !TextUtils.isEmpty(msg)) {
            showNotificationDialog(senderId, msg);
            msgShow = false;
        }
    }

    private AlertDialog addDlg;

    public void showNotificationDialog(String message) {

        new MaterialDialog.Builder(mContext).title("Qwork")
                .content(message)
//                .customView(R.layout.reply_edittext, false)
//                .negativeText("CANCEL")
                .positiveText("OK")
                .show();
    }

    public void showNotificationDialog(final String senderId, String message) {
//        if (!(message != null && message.startsWith("A message from "))) return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //inflate view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dlg_reply, null);

        ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
        TextView tv_message = (TextView) customView.findViewById(R.id.tv_message);
        tv_message.setText(message);
        final EditText tv_reply_msg = (EditText) customView.findViewById(R.id.tv_reply_msg);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });
        customView.findViewById(R.id.btn_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyForMessage(senderId, tv_reply_msg.getText().toString().trim());
            }
        });

        customView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });

        //dialog.show();

        builder.setView(customView);
        addDlg = builder.show();

//        }
    }

    public View getTopBar() {
        View topBar = findViewById(R.id.top_bar);
        if (topBar != null && !isGetTopbar) {
            isGetTopbar = true;
            titleTV = (TextView) topBar.findViewById(R.id.titleTV);
            titleIV = (ImageView) topBar.findViewById(R.id.titleIV);
            leftIV = (ImageView) topBar.findViewById(R.id.leftIV);
            rightIV = (ImageView) topBar.findViewById(R.id.rightIV);
            rightTV = (TextView) topBar.findViewById(R.id.rightTV);
            rightTV.setOnClickListener(this);
            leftIV.setOnClickListener(this);
            rightIV.setOnClickListener(this);
        }
        return topBar;
    }

    public void showTopBar() {
        if (getTopBar() != null) {
            getTopBar().setVisibility(View.VISIBLE);
        }
    }

    public void hideTopBar() {
        if (getTopBar() != null) {
            getTopBar().setVisibility(View.INVISIBLE);
        }
    }

    public void setTitle(String str) {
        if (getTopBar() != null) {
            titleTV.setText(str);
        }
    }

    public void showTitle() {
        if (getTopBar() != null) {
            titleTV.setVisibility(View.VISIBLE);
        }
    }

    public void hideTitle() {
        if (getTopBar() != null) {
            titleTV.setVisibility(View.INVISIBLE);
        }
    }

    public void showTitleIV() {
        if (getTopBar() != null) {
            titleIV.setVisibility(View.VISIBLE);
        }
    }

    public void hideTitleIV() {
        if (getTopBar() != null) {
            titleIV.setVisibility(View.INVISIBLE);
        }
    }

    public void showRightTV() {
        if (getTopBar() != null) {
            rightTV.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightTV() {
        if (getTopBar() != null) {
            rightTV.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightText(String str) {
        if (getTopBar() != null) {
            rightTV.setText(str);
        }
    }

    public void setLeftIV(int resID) {
        if (getTopBar() != null) {
            RES_ID_TOP_LEFT = resID;
            leftIV.setImageResource(resID);
        }
    }

    public void showLeftIV() {
        if (getTopBar() != null) {
            if (leftIV.getDrawable() == null && RES_ID_TOP_LEFT == -1) {
                RES_ID_TOP_LEFT = DEFAULT_RES_ID_TOP_LEFT;
            }
            leftIV.setVisibility(View.VISIBLE);
        }
    }

    public void hideLeftIV() {
        if (getTopBar() != null) {
            leftIV.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightIV(int resID) {
        if (getTopBar() != null) {
            RES_ID_TOP_RIGHT = resID;
            rightIV.setImageResource(resID);
        }
    }

    public void showRightIV() {
        if (getTopBar() != null) {
            if (rightIV.getDrawable() == null && RES_ID_TOP_RIGHT == -1) {
                RES_ID_TOP_RIGHT = DEFAULT_RES_ID_TOP_RIGHT;
            }
            rightIV.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightIV() {
        if (getTopBar() != null) {
            rightIV.setVisibility(View.INVISIBLE);
        }
    }

    public View getCustomLoadingView() {
        return findViewById(R.id.view_progress);
    }

    public void showCustomLoadingView() {
        View progressView = getCustomLoadingView();
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
            com.github.ybq.android.spinkit.SpinKitView spinKitView1 = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
            com.github.ybq.android.spinkit.SpinKitView spinKitView2 = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit_secondary);
            spinKitView1.setVisibility(View.VISIBLE);
            spinKitView2.setVisibility(View.GONE);
        }
    }

    public void hideCustomLoadingView() {
        View progressView = getCustomLoadingView();
        if (progressView != null) {
            progressView.setVisibility(View.INVISIBLE);
        }
    }

    public void showSecondaryCustomLoadingView() {
        View progressView = getCustomLoadingView();
        if (progressView != null) {
            progressView.setVisibility(View.VISIBLE);
            com.github.ybq.android.spinkit.SpinKitView spinKitView1 = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit);
            com.github.ybq.android.spinkit.SpinKitView spinKitView2 = (com.github.ybq.android.spinkit.SpinKitView) findViewById(R.id.spin_kit_secondary);
            spinKitView1.setVisibility(View.GONE);
            spinKitView2.setVisibility(View.VISIBLE);
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null && mContext != null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void saveKeyValue(String key, String value) {
        Common.saveInfoWithKeyValue(mContext, key, value);
    }

    public String getValueFromKey(String key) {
        return Common.getInfoWithValueKey(mContext, key);
    }

    private Toast mToast;

    public void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leftIV:
                finish();
                break;
        }
    }

//    @Override
//    public void onBackPressed() {
//        //finish();
//    }

    public void logout() {
        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, "");
        final User me = getUser(getValueFromKey("me"));
        if (me != null) {

            showSecondaryCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "user/logout")
                    .addBodyParameter("userid", me.userid)
                    .setTag("logout")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideCustomLoadingView();
                            try {
                                String status = response.getString("status");
                                if (status.equals("1")) {

                                    if (me.g_id != null && !me.g_id.equals("")) {
                                        /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {
                                                        //logout();
                                                    }
                                                });*/
                                    } else if (me.fb_id != null && !me.fb_id.equals("")) {
                                        LoginManager.getInstance().logOut();
                                    } else if (me.tw_id != null && !me.tw_id.equals("")) {
                                        logoutTwitter();
                                    }

                                    saveKeyValue("me", "");
                                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                    finish();

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
                            hideCustomLoadingView();
                            showToast(error.getErrorBody().toString());
                        }
                    });
        }
    }

    public void logoutTwitter() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
        }
    }

    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void setSideProfile(View view) {
        final User me = getUser(getValueFromKey("me"));
        if (me != null) {
            ImageView sidePhotoIV = (ImageView) view.findViewById(R.id.sidePhotoIV);
            TextView sideNameTV = (TextView) view.findViewById(R.id.sideNameTV);
            TextView sidePhoneTV = (TextView) view.findViewById(R.id.sidePhoneTV);
            final TextView customerCountTV = (TextView) view.findViewById(R.id.customerCountTV);

            if (me.photo != null && !me.photo.equals("")) {
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + me.photo).into(sidePhotoIV);
            }

            if (me.username != null && !me.username.equals("")) {
                sideNameTV.setText(me.username);
            }


            if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                customerCountTV.setVisibility(View.VISIBLE);
                customerCountTV.setText("Customers signed up: "+Common.getInfoWithValueKey(mContext, KEY_CUST_COUNT));
            }

            AndroidNetworking.upload(MyApplication.BASE_URL + "user_new/getBarbershopsCount")
                    .addMultipartParameter("barber_shop_id", me.userid)
                    .setPriority(Priority.LOW)
                    .setTag("update_profile")
                    .build()


                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response

                            try {
                                String status = response.getString("status");
                                if (status.equals("1")) {
                                    //  JSONObject user = response.getJSONObject("data");

                                    JSONArray jsonLegs = response.getJSONArray("data");
                                    String custcount = jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop");
                                    if (custcount != null && !custcount.equals("")) {
                                        customerCountTV.setText("Customers signed up: "+custcount);
                                        Common.saveInfoWithKeyValue(mContext, KEY_CUST_COUNT, custcount);

                                    }
                                } else if (status.equals("false")) {
                                    showToast("Don't saved");
                                } else {
                                    showToast("Network Error!");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showToast(e.getMessage()+"1");
                            }
                        }

                        @Override
                        public void onError(ANError error) {

                            showToast(error.getErrorBody().toString()+"2");
                        }
                    });


            if (me.phone != null && !me.phone.equals("")) {
                sidePhoneTV.setText(me.phone);
            }
        }
    }

    public void readyGPS() {
        if (mContext != null) {
            MyApplication.g_GPSTracker = new GPSTracker(this);
            if (MyApplication.g_GPSTracker.canGetLocation()) {
                MyApplication.g_latitude = MyApplication.g_GPSTracker.getLatitude(); // returns latitude
                MyApplication.g_longitude = MyApplication.g_GPSTracker.getLongitude(); // returns longitude
                //showToast("lat: " + MyApplication.g_latitude + ", lang: " + MyApplication.g_longitude);
            } else {
                MyApplication.g_GPSTracker.showSettingsAlert();
            }
        }
    }

    /**
     * Customize on every project
     */

    public User getUser(String str) {
        User user = null;
        try {
            user = LoganSquare.parse(str.toString(), User.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    String ratingValue = "";

    public void showReviewDlg(final String name, final String photo, final String orderId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (reviewDlg != null && reviewDlg.isShowing()) {
                    reviewDlg.dismiss();
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                //inflate view
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.dlg_review, null);

                ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
                closeIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reviewDlg.dismiss();
                    }
                });

                TextView nameTV = (TextView) customView.findViewById(R.id.nameTV);
                de.hdodenhof.circleimageview.CircleImageView photoIV = (de.hdodenhof.circleimageview.CircleImageView) customView.findViewById(R.id.photoIV);
                me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar = (me.zhanghai.android.materialratingbar.MaterialRatingBar) customView.findViewById(R.id.ratingBar);

                nameTV.setText(name);
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + photo).into(photoIV);

                final EditText commentET = (EditText) customView.findViewById(R.id.commentET);
                ImageView sendBtn = (ImageView) customView.findViewById(R.id.sendBtn);
                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(ratingValue)) {
                            showToast("Please choose rating star");
                            return;
                        }

                        if (TextUtils.isEmpty(commentET.getText())) {
                            showToast("Please input comment");
                            return;
                        }

                        setRate(orderId, ratingValue, commentET.getText().toString());
                    }
                });

                ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                        //setRate(orderId, String.valueOf(rating));
                        //dialog.dismiss();
                        ratingValue = String.valueOf(rating);
                /*
                showSecondaryCustomLoadingView();
                AndroidNetworking.post(MyApplication.BASE_URL + "order/setRating")
                        .addBodyParameter("order_id", orderId)
                        .addBodyParameter("rating", String.valueOf(rating))
                        .setTag("setRating")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                hideCustomLoadingView();
                                try {
                                    String status = response.getString("status");
                                    if(status.equals("1")) {
                                        showToast("You have successfully rated");
                                        reviewDlg.dismiss();
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
                                hideCustomLoadingView();
                                showToast(error.getErrorBody().toString());
                            }
                        });*/
                    }
                });
                //dialog.show();

                CheckBox chk = (CheckBox) customView.findViewById(R.id.anonyChk);
                chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isAnony = isChecked;
                    }
                });

                builder.setCancelable(false);
                builder.setView(customView);
                reviewDlg = builder.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(reviewDlg.getWindow().getAttributes());
                lp.width = (int) Common.convertDpToPixel(300);
                reviewDlg.getWindow().setAttributes(lp);
            }
        });

    }

    public void setRate(String orderid, final String rate, final String comment) {
        String is_anony = "0";
        if (this.isAnony) is_anony = "1";

        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setRatingWithComment")
                .addBodyParameter("order_id", orderid)
                .addBodyParameter("rating", rate)
                .addBodyParameter("comment", comment)
                .addBodyParameter("isAnony", is_anony)
                .setTag("setRating")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                showToast("You have successfully rated");
                                reviewDlg.dismiss();

                                saveKeyValue("review", "");

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
                        hideCustomLoadingView();
                        showToast(error.getErrorBody().toString());
                    }
                });
    }

    protected void gotoLogin() {

        User me = getUser(getValueFromKey("me"));
        if (me.g_id != null && !me.g_id.equals("")) {
                                        /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {
                                                        //logout();
                                                    }
                                                });*/
        } else if (me.fb_id != null && !me.fb_id.equals("")) {
            LoginManager.getInstance().logOut();
        } else if (me.tw_id != null && !me.tw_id.equals("")) {
            logoutTwitter();
        }

        saveKeyValue("me", "");


        EventBus.getDefault().post(new Event(Event.EVENT_TYPE.USER_REMOVED));

        startActivity(new Intent(mContext, LoginActivity.class));
        finish();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event e) {
        if (e.getEvent() == Event.EVENT_TYPE.USER_REMOVED || e.getEvent() == Event.EVENT_TYPE.FREEZED) {
            if (this instanceof B_HomeActivity || this instanceof BS_HomeActivity || this instanceof C_HomeActivity
                    || this instanceof B_ContactActivity || this instanceof B_NotificationActivity || this instanceof B_ProfileActivity
                    || this instanceof BS_ContactActivity || this instanceof BS_NotificationActivity || this instanceof BS_ProfileActivity
                    || this instanceof BS_RequestListActivity
                    || this instanceof C_HistoryActivity || this instanceof C_ContactActivity || this instanceof C_ProfileActivity) {
                User me = getUser(getValueFromKey("me"));
                if (me.g_id != null && !me.g_id.equals("")) {
                                        /*Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                new ResultCallback<Status>() {
                                                    @Override
                                                    public void onResult(Status status) {
                                                        //logout();
                                                    }
                                                });*/
                } else if (me.fb_id != null && !me.fb_id.equals("")) {
                    LoginManager.getInstance().logOut();
                } else if (me.tw_id != null && !me.tw_id.equals("")) {
                    logoutTwitter();
                }

                saveKeyValue("me", "");

                startActivity(new Intent(mContext, LoginActivity.class));
            }
            finish();
        } else if (e.getEvent() == Event.EVENT_TYPE.BS_DELETED) {
            User user = getUser(getValueFromKey("me"));
            user.barbershop_id = "0";
            saveKeyValue("me", user.toJson());

            Intent it = new Intent(mContext, B_ProfileActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        }
    }

    ;

    public void onLocationChanged(Location location) {


    }

    public void replyForMessage(String senderId, String message) {
        if (message.isEmpty()) {
            showToast("Please enter message");
            return;
        }
        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "notification/sendNotificationFromBarber")
                .addBodyParameter("sender_id", me.userid)
                .addBodyParameter("rids", senderId)
                .addBodyParameter("message", me.username + ":" + MyApplication.NOTIFICATION_SPLIT + message)
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

                                showToast("Sent");
                                addDlg.dismiss();
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
                        showToast(error.getErrorBody());
                    }
                });
    }

}
