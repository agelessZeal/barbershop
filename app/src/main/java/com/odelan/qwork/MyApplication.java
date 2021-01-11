package com.odelan.qwork;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;
import com.google.gson.Gson;
import com.odelan.qwork.data.model.ReviewNotify;
import com.odelan.qwork.event.Event;
import com.odelan.qwork.ui.activity.intro.SplashActivity;
import com.odelan.qwork.ui.activity.main.NotificationActivity;
import com.odelan.qwork.ui.activity.main.ReviewNotificationActivity;
import com.odelan.qwork.ui.activity.main.barber.B_HomeActivity;
import com.odelan.qwork.ui.activity.main.customer.C_HomeActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.GPSTracker;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by Administrator on 6/28/2016.
 */
public class MyApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "wr8JCtr7971N4fbHvbHsRLWhi";
    private static final String TWITTER_SECRET = "NxYaxJpET9MKsPPUWOqpqKZoa4E0j7Mgf4jiS53xMAlaQYIHL2";
    private static final String FLURRY_API_KEY = "DH5JCGTFHK3TS3QNGFS2";


    public static final String SUPPORT_PHONE = "07960037894";
    public static final String SUPPORT_EMAIL = "Qworkhelp@gmail.com";
    public static final String SUPPORT_FB_LINK = "https://en-gb.facebook.com/people/Qwork-Hair/100019697730819";
    public static final String SUPPORT_TWITTER_LINK = "https://twitter.com/QworkSupport";
    public static final String SUPPORT_INSTAGRAM_LINK = "https://Instagram.com/qworkapp";
    public static final String URL_CONTACT_US = "http://barberq.co/BarberQ";
    public static final String NOTIFICATION_SPLIT = "";


    public static String TAG = "MyApplication";
    public static boolean isReviewNotification = false;
    public static String noti_username = "";
    public static String noti_photo = "";
    public static String noti_orderid = "";

    public static String one_id_android = "";
    public static BaseActivity curActivity = null;

    public static String g_address = "";
    public static double g_latitude = 0;
    public static double g_longitude = 0;

    public static GPSTracker g_GPSTracker = null;

    public static String BASE_URL = "http://18.130.34.135/Qwork/api/";
    public static String ASSET_BASE_URL = "http://18.130.34.135/Qwork/";

    /*public static String BASE_URL = "http://192.168.1.7/barberq/Qwork/api/";
    public static String ASSET_BASE_URL = "http://192.168.1.7/barberq/Qwork/";*/

    // user type
    public static String USER_CUSTOMER = "customer";
    public static String USER_BARBER = "barber";
    public static String USER_BARBER_SHOP = "barber_shop";

    // Gender
    public static String GENDER_MALE = "Male";
    public static String GENDER_FAMALE = "Famale";

    // Online_Offlince status
    public static String STATUS_ONLINE = "online";
    public static String STATUS_OFFLINE = "offline";

    @Override
    public void onCreate() {
        super.onCreate();

        new FlurryAgent.Builder()
                .withDataSaleOptOut(false) //CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, FLURRY_API_KEY);

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("0C8K6UzDIJ87oGWz9D0M0fLBl", "gtpApZDBlpVjeKn4XDNuRRiefI0Fs9fcXngPSgtziHAYqxTx6g"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        AndroidNetworking.initialize(getApplicationContext());

        OneSignal.startInit(this)
                .autoPromptLocation(false) // default call promptLocation later
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationReceivedHandler(new QworkNotificationReceivedHandler())
                .setNotificationOpenedHandler(new QworkNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        boolean isEnabled = status.getPermissionStatus().getEnabled();

        boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();
        boolean subscriptionSetting = status.getSubscriptionStatus().getUserSubscriptionSetting();
        String userID = status.getSubscriptionStatus().getUserId();
        String pushToken = status.getSubscriptionStatus().getPushToken();

        one_id_android = userID;
    }

    private class QworkNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            Log.v("Ahutosh", new Gson().toJson(notification));
            final JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            //BackgroundImageLayout backgroundImageLayout = notification.payload.backgroundImageLayout;
            String rawPayload = notification.payload.rawPayload;


            String customKey;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                final String type = data.optString("type");

                if (type.equals("join")) {
                    if (MyApplication.curActivity instanceof B_HomeActivity) {
                        ((B_HomeActivity) MyApplication.curActivity).refresh();
                    }
                } else if (type.equals("left")) {
                    if (MyApplication.curActivity instanceof B_HomeActivity) {
                        ((B_HomeActivity) MyApplication.curActivity).refresh();
                    }
                } else if (type.equals("review")) {
                    try {

//                        MyApplication.isReviewNotification = true;
                        noti_username = data.optString("senderName", null);
                        noti_photo = data.optString("senderPhoto", null);
                        noti_orderid = data.optString("order_id", null);


                        ReviewNotify reviewNotify = new ReviewNotify();
                        reviewNotify.userName = noti_username;
                        reviewNotify.photo = noti_photo;
                        reviewNotify.orderId = noti_orderid;
                        saveKeyValue("review", reviewNotify.toJson());

                        if (MyApplication.curActivity instanceof C_HomeActivity)
                            ((C_HomeActivity) MyApplication.curActivity).refresh();

                        Object activityToLaunch = SplashActivity.class;

                        //OneSignal.cancelNotification(notification.androidNotificationId);


                        if (!isAppForeground(getApplicationContext())) {
                            activityToLaunch = MyApplication.curActivity.getClass();

                            Intent i = new Intent(getApplicationContext(), ReviewNotificationActivity.class);
                            i.putExtra("param1", noti_username);
                            i.putExtra("param2", noti_photo);
                            i.putExtra("param3", noti_orderid);
                            i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                        } else {

                            MyApplication.curActivity.showReviewDlg(noti_username, noti_photo, noti_orderid);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (type.equals("remove")) {
                    if (MyApplication.curActivity instanceof C_HomeActivity) {
                        ((C_HomeActivity) MyApplication.curActivity).refresh();
                    }
                } else if (type.equals("cutting_time")) {
                    if (MyApplication.curActivity instanceof C_HomeActivity) {
                        ((C_HomeActivity) MyApplication.curActivity).refresh();
                    }
                } else if (type.equals("freeze") || type.equals("delete")) {

                    if (MyApplication.curActivity != null) {
                        EventBus.getDefault().post(new Event(Event.EVENT_TYPE.USER_REMOVED));
                    } else {
                        saveKeyValue("me", "");
                    }
                } else if (type.equals("delete_barbershop")) {
                    EventBus.getDefault().post(new Event(Event.EVENT_TYPE.BS_DELETED));
                }
            }
        }
    }

    private class QworkNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            final JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            String customKey;
            String openURL = null;
            Object activityToLaunch = SplashActivity.class;

            String message1 = null;

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                message1 = data.optString("message", null);
                final String message = message1;

                if (MyApplication.curActivity != null) {
                    final String type = data.optString("type", null);
                    final String senderId = data.optString("senderId");
                    if (!isAppForeground(getApplicationContext())) {
                        activityToLaunch = MyApplication.curActivity.getClass();
                        //activityToLaunch = MyApplication.curActivity.getClass();

                        //MyApplication.isReviewNotification = true;
                        noti_username = data.optString("senderName", null);
                        noti_photo = data.optString("senderPhoto", null);
                        noti_orderid = data.optString("order_id", null);

//                        Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        startActivity(intent);

                        Intent intent = new Intent(getBaseContext(), NotificationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (type.equals("review")) {
//                                    String name = data.optString("senderName", null);
//                                    String photo = data.optString("senderPhoto", null);
//                                    String orderId = data.optString("order_id", null);
//                                    MyApplication.curActivity.showReviewDlg(name, photo, orderId);
                                } else if (type.equals("message")) {
                                    MyApplication.curActivity.showNotificationDialog(senderId, message);
                                    MyApplication.curActivity.msgShow = true;
                                    MyApplication.curActivity.msg = message;
                                    MyApplication.curActivity.senderId = senderId;
                                } else {
                                    MyApplication.curActivity.showNotificationDialog(message);
                                }
                            }
                        };

                        handler.postDelayed(runnable, 500);
                    } else {
                        if (type.equals("review")) {
                        } else if (type.equals("delete")) {
                        } else if (type.equals("freeze")) {
                        } else if (type.equals("message")) {
                            MyApplication.curActivity.showNotificationDialog(senderId, message);
                        } else {
                            MyApplication.curActivity.showNotificationDialog(message);
                        }
                    }
                } else {
                    activityToLaunch = SplashActivity.class;
                    //activityToLaunch = MyApplication.curActivity.getClass();

                    //MyApplication.isReviewNotification = true;
                    noti_username = data.optString("senderName", null);
                    noti_photo = data.optString("senderPhoto", null);
                    noti_orderid = data.optString("order_id", null);

                    Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = SplashActivity.class;
                } else {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                }

            }
        }
    }

    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
            isActivityFound = true;
        }
        return isActivityFound;
    }

    /**
     * Round Drawables
     **/

    public static Drawable getRectDrawable(Activity activity, int solidColor, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(solidColor);
        drawable.setStroke(getScaledLength(activity, strokeWidth), strokeColor);
        return drawable;
    }

    public static Drawable getRoundDrawable(Activity activity, int cornerRadius, int solidColor, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(getScaledLength(activity, cornerRadius));
        drawable.setColor(solidColor);
        drawable.setStroke(getScaledLength(activity, strokeWidth), strokeColor);
        return drawable;
    }

    public static Drawable getRoundDrawable(Activity activity, int cornerRadius, int solidColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(getScaledLength(activity, cornerRadius));
        drawable.setColor(solidColor);
        return drawable;
    }

    public static Drawable getOvalDrawable(Activity activity, int solidColor, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(solidColor);
        drawable.setStroke(getScaledLength(activity, strokeWidth), strokeColor);
        return drawable;
    }

    public static Drawable getOvalDrawable(Activity activity, int solidColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(solidColor);
        return drawable;
    }

    public static int getScaledLength(Activity activity, int length) {
        int scaledLength = (int) (length * activity.getResources().getDisplayMetrics().density);
        return scaledLength;
    }


    public void saveKeyValue(String key, String value) {
        Common.saveInfoWithKeyValue(getBaseContext(), key, value);
    }
}


/**
 * Android Networking useful
 */

/* Get
AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
        .addPathParameter("pageNumber", "0")
        .addQueryParameter("limit", "3")
        .addHeaders("token", "1234")
        .setTag("test")
        .setPriority(Priority.LOW)
        .build()
        .getAsJSONArray(new JSONArrayRequestListener() {
@Override
public void onResponse(JSONArray response) {
        // do anything with response
        }
@Override
public void onError(ANError error) {
        // handle error
        }
        });
*/
/* Post
AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createAnUser")
        .addBodyParameter("firstname", "Amit")
        .addBodyParameter("lastname", "Shekhar")
        .setTag("test")
        .setPriority(Priority.MEDIUM)
        .build()
        .getAsJSONObject(new JSONObjectRequestListener() {
@Override
public void onResponse(JSONObject response) {
        // do anything with response
        }
@Override
public void onError(ANError error) {
        // handle error
        }
        });
*/

/*private class QworkNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            String customKey;
            String openURL = null;
            Object activityToLaunch = SplashActivity.class;

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = SplashActivity.class;
                } else {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                }

            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            Log.i("OneSignalExample", "openURL = " + openURL);
            startActivity(intent);

        }
    }*/