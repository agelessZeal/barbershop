package com.odelan.qwork.utils;

import android.content.Context;

/**
 * Created by Administrator on 12/13/2016.
 */

public class SessionUtils {

    public Context mContext;

    public static final String LOGIN_EMAIL = "login_email";
    public static final String LOGIN_FACEBOOK = "login_facebook";
    public static final String LOGIN_TWITTER = "login_twitter";

    public static final String KEY_LOGIN_TYPE = "login_type";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USERID = "userid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FB_ID = "fb_id";
    public static final String KEY_TWT_ID = "twt_id";
    public static final String KEY_USER_TYPE = "user_type";
    public static final String KEY_CUST_COUNT = "cust_count";

    public SessionUtils(){}

    public SessionUtils(Context context) {
        this.mContext = context;
    }

    public void saveEmailLoginSession(String email, String username, String password, String userid, String user_type) {

        Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, LOGIN_EMAIL);
        Common.saveInfoWithKeyValue(mContext, KEY_EMAIL, email);
        Common.saveInfoWithKeyValue(mContext, KEY_USERNAME, username);
        Common.saveInfoWithKeyValue(mContext, KEY_PASSWORD, password);
        Common.saveInfoWithKeyValue(mContext, KEY_USERID, userid);
        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, user_type);
    }

    public void saveFBLoginSession(String email, String userid, String user_type, String fb_id) {

        Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, LOGIN_FACEBOOK);
        Common.saveInfoWithKeyValue(mContext, KEY_EMAIL, email);
        Common.saveInfoWithKeyValue(mContext, KEY_USERID, userid);
        Common.saveInfoWithKeyValue(mContext, KEY_FB_ID, fb_id);
        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, user_type);
    }

    public void saveTwtLoginSession(String username, String userid, String user_type, String tw_id) {

        Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, LOGIN_TWITTER);
        Common.saveInfoWithKeyValue(mContext, KEY_USERNAME, username);
        Common.saveInfoWithKeyValue(mContext, KEY_USERID, userid);
        Common.saveInfoWithKeyValue(mContext, KEY_TWT_ID, tw_id);
        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, user_type);
    }

    public boolean isLoggedin() {
        boolean result = true;
        String loginType = checkLoginType();
        if(loginType.equals("")) {
            result = false;
        }
        return result;
    }

    public String checkLoginType() {
        return Common.getInfoWithValueKey(mContext, KEY_LOGIN_TYPE);
    }

    public String getEmail() {
        return Common.getInfoWithValueKey(mContext, KEY_EMAIL);
    }

    public String getPassword() {
        return Common.getInfoWithValueKey(mContext, KEY_PASSWORD);
    }

    public String getUsername() {
        return Common.getInfoWithValueKey(mContext, KEY_USERNAME);
    }

    public String getUserId() {
        return Common.getInfoWithValueKey(mContext, KEY_USERID);
    }

    public String getUsertype() {
        return Common.getInfoWithValueKey(mContext, KEY_USER_TYPE);
    }

    public String getFacebookId() {
        return Common.getInfoWithValueKey(mContext, KEY_FB_ID);
    }

    public String getTwitterId() {
        return Common.getInfoWithValueKey(mContext, KEY_TWT_ID);
    }

    public void logout(String loginType) {
        if(loginType.equals(LOGIN_EMAIL)) {
            Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, "");
            Common.saveInfoWithKeyValue(mContext, KEY_EMAIL, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USERNAME, "");
            Common.saveInfoWithKeyValue(mContext, KEY_PASSWORD, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USERID, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, "");
        } else if (loginType.equals(LOGIN_FACEBOOK)) {
            Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, "");
            Common.saveInfoWithKeyValue(mContext, KEY_EMAIL, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USERID, "");
            Common.saveInfoWithKeyValue(mContext, KEY_FB_ID, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, "");
        } else if (loginType.equals(LOGIN_TWITTER)) {
            Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USERNAME, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USERID, "");
            Common.saveInfoWithKeyValue(mContext, KEY_TWT_ID, "");
            Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, "");
        }
    }
}
