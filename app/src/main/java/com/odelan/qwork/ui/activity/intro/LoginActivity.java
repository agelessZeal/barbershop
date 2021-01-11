package com.odelan.qwork.ui.activity.intro;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.barber.B_HomeActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_HomeActivity;
import com.odelan.qwork.ui.activity.main.customer.C_HomeActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.ValidationUtils;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

import static com.odelan.qwork.utils.SessionUtils.KEY_LOGIN_TYPE;
import static com.odelan.qwork.utils.SessionUtils.KEY_USER_TYPE;
import static com.odelan.qwork.utils.SessionUtils.LOGIN_EMAIL;

public class LoginActivity extends BaseActivity implements com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.emailET)
    EditText emailET;

    @BindView(R.id.passwordET)
    EditText passwordET;

    ValidationUtils validationUtils;

    final int REQUEST_PERMISSION = 999;

    public int RC_SIGN_IN = 1;
    public static GoogleApiClient mGoogleApiClient;
    TwitterAuthClient mTwitterAuthClient;

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printHashKey();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        successFBLogin();
                    }

                    @Override
                    public void onCancel() {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Error")
                                .setMessage("Your Facebook login was canceled.")
                                .setPositiveButton(android.R.string.ok, null).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Error")
                                .setMessage(exception.getMessage())
                                .setPositiveButton(android.R.string.ok, null).show();
                    }
                });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile,
                                                   Profile currentProfile) {
            }
        };

        setContentView(R.layout.activity_login);

        mContext = this;
        ButterKnife.bind(this);

        validationUtils = new ValidationUtils(mContext);

        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mTwitterAuthClient= new TwitterAuthClient();

        User me = getUser(getValueFromKey("me"));
        if(me != null) {
            if (me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                startActivity(new Intent(mContext, C_HomeActivity.class));
            } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                startActivity(new Intent(mContext, B_HomeActivity.class));
            } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                startActivity(new Intent(mContext, BS_HomeActivity.class));
                Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, MyApplication.USER_BARBER_SHOP);
            }
            finish();
        }

        readyGPS();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_NETWORK_STATE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION);
            }
        }
    }

    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    private boolean checkPermission () {
        boolean hasPermision = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            hasPermision = false;
        }
        return hasPermision;
    }

    private void showPermissionError() {
        new MaterialDialog.Builder(this)
                .title("Warning")
                .content("Permissions were denied. In order to allow permission, first you have to turn off screen overlay from Settings > Apps.")
                .positiveText("OK")
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(checkPermission()) {
                        readyGPS();
                    } else {
                        showPermissionError();
                    }

                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                } else {
                    //showToast("Recording permission was denied");
                    showPermissionError();
                }
                return;
            }
        }
    }

    @OnClick(R.id.loginBtn) public void onLogin() {

        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
            if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
                showToast("Please try again, now you can not receive any notification because of your network issue");
                return;
            }
        }

        if(validationUtils.checkLoginValid(emailET, passwordET)) {
            showCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "user/login")
                    .addBodyParameter("email", emailET.getText().toString())
                    .addBodyParameter("password", passwordET.getText().toString())
                    .addBodyParameter("one_id_android", MyApplication.one_id_android)
                    .setTag("login")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            hideCustomLoadingView();
                            try {
                                String status = response.getString("status");
                                if(status.equals("1")) {
                                    JSONObject user = response.getJSONObject("data");
                                    saveKeyValue("me", user.toString());
                                    saveKeyValue("password", passwordET.getText().toString());
                                    User me = LoganSquare.parse(user.toString(), User.class);
                                    if(me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                                        startActivity(new Intent(mContext, C_HomeActivity.class));
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                                        startActivity(new Intent(mContext, B_HomeActivity.class));
                                        Common.saveInfoWithKeyValue(mContext, KEY_LOGIN_TYPE, LOGIN_EMAIL);
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                                        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, MyApplication.USER_BARBER_SHOP);
                                        startActivity(new Intent(mContext, BS_HomeActivity.class));

                                    }
                                    finish();
                                } else if (status.equals("false")) {
                                    String msg = response.getString("error");
                                    if(!TextUtils.isEmpty(msg) && msg.contains("inactive")){
                                        showToast("Your account has been frozen.");
                                    }else{
                                        showToast("Invalid login.");
                                    }
                                } else {
                                    showToast( response.getString("error"));
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

    @OnClick(R.id.signupBtn) public void onSignup() {
        startActivity(new Intent(mContext, SignupActivity.class));
    }

    @OnClick(R.id.fbBtn) public void onFbLogin() {
        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
            if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
                showToast("Please try again, now you can not receive any notification because of your network issue");
                return;
            }
        }
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email"));
    }

    @OnClick(R.id.twtBtn) public void onTwitterLogin() {
        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
            if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
                showToast("Please try again, now you can not receive any notification because of your network issue");
                return;
            }
        }

        mTwitterAuthClient.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                // Success
                //Creating a twitter session with result's data
                TwitterSession session = twitterSessionResult.data;

                //Getting the username from session
                //final String username = session.getUserName();
                final String tw_id = String.valueOf(session.getUserId());

                final Call<com.twitter.sdk.android.core.models.User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                user.enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                        String username = userResult.data.name;
                        String email = userResult.data.email;

                        // _normal (48x48px) | _bigger (73x73px) | _mini (24x24px)
                        String photoUrlNormalSize   = userResult.data.profileImageUrl;
                        String photoUrlBiggerSize   = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                        String photoUrlMiniSize     = userResult.data.profileImageUrl.replace("_normal", "_mini");
                        String photoUrlOriginalSize = userResult.data.profileImageUrl.replace("_normal", "");

                        twtLoginToServer(tw_id, email, photoUrlOriginalSize, username);
                    }

                    @Override
                    public void failure(TwitterException exc) {
                        Log.d("TwitterKit", "Verify Credentials Failure", exc);
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @OnClick(R.id.googleBtn) public void onGoogleLogin() {
        if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
            OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
            MyApplication.one_id_android = status.getSubscriptionStatus().getUserId();
            if(MyApplication.one_id_android == null || MyApplication.one_id_android.equals("")) {
                showToast("Please try again, now you can not receive any notification because of your network issue");
            } else {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @OnClick(R.id.forgotPasswordTV) public void onForgotPassword() {
        startActivity(new Intent(mContext, ForgotPasswordActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    private void successFBLogin() {
        final AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        if (object != null) {
                            try {
                                String fbId = object.getString("id");
                                String fbEmail = object.getString("email");
                                String photo = "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                                String username = object.getString("name");
                                if(username == null) {
                                    username = "";
                                }
                                /*String fbName = object.getString("name");
                                String first_name = object.getString("first_name");
                                String last_name = object.getString("last_name");
                                String gender = object.getString("gender");*/

                                fbLoginToServer(fbId, fbEmail, photo, username);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            showToast("Network Error");

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("GraphRequest is failed.")
                                    .setMessage("GraphicRequest is failed.")
                                    .setPositiveButton(android.R.string.ok, null).show();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,email,birthday,location,locale,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void fbLoginToServer (final String fb_id, final String email, final String photo, final String username) {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/login_fb")
                .addBodyParameter("email", email)
                .addBodyParameter("fb_id", fb_id)
                .addBodyParameter("one_id_android", MyApplication.one_id_android)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                JSONObject user = response.getJSONObject("data");
                                saveKeyValue("me", user.toString());
                                String isRegister = response.getString("isRegister");
                                if(isRegister.equals("true")) {
                                    ChooseUserTypeActivity.mUsername = username;
                                    ChooseUserTypeActivity.mPhoto = photo;
                                   startActivity(new Intent(mContext, ChooseUserTypeActivity.class));
                                    //openTutorialScreen();
                                }
                                else {
                                    User me = LoganSquare.parse(user.toString(), User.class);
                                    if (me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                                        startActivity(new Intent(mContext, C_HomeActivity.class));
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                                        startActivity(new Intent(mContext, B_HomeActivity.class));
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                                        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, MyApplication.USER_BARBER_SHOP);
                                        startActivity(new Intent(mContext, BS_HomeActivity.class));
                                    }
                                }
                                finish();
                            } else if (status.equals("false")) {
                                LoginManager.getInstance().logOut();
                                showToast("Incorrect login");
                            } else {
                                LoginManager.getInstance().logOut();
                                showToast( response.getString("error"));
                            }
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            showToast("Network Error!");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        hideCustomLoadingView();
                        showToast(error.getMessage());
                    }
                });
    }

    private void openTutorialScreen(){
        Intent intent = new Intent(mContext, TutorialActivity.class);
        intent.putExtra("launch_type",TutorialActivity.LAUNCH_CHOOSE_USER_TYPE);
        startActivity(intent);

    }
    private void twtLoginToServer (final String tw_id, final String email, final String photo, final String username) {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/login_twitter")
                .addBodyParameter("tw_id", tw_id)
                .addBodyParameter("email", email)
                .addBodyParameter("one_id_android", MyApplication.one_id_android)
                .setTag("google_login")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                JSONObject user = response.getJSONObject("data");
                                saveKeyValue("me", user.toString());
                                String isRegister = response.getString("isRegister");
                                if(isRegister.equals("true")) {
                                    ChooseUserTypeActivity.mUsername = username;
                                    ChooseUserTypeActivity.mPhoto = photo;
                                 //   openTutorialScreen();
                                    startActivity(new Intent(mContext, ChooseUserTypeActivity.class));
                                } else {
                                    User me = LoganSquare.parse(user.toString(), User.class);
                                    if (me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                                        startActivity(new Intent(mContext, C_HomeActivity.class));
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                                        startActivity(new Intent(mContext, B_HomeActivity.class));
                                    } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                                        Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, MyApplication.USER_BARBER_SHOP);
                                        startActivity(new Intent(mContext, BS_HomeActivity.class));
                                    }
                                }
                                finish();
                            } else if (status.equals("false")) {
                                showToast("Incorrect login");
                            } else {
                                showToast( response.getString("error"));
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
                        String errorStr = error.getErrorBody().toString();
                        showToast(error.getErrorBody().toString());
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google_Signin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            String googleId = account.getId();
            String email = account.getEmail();
            final String username = account.getDisplayName();
            String photoURL = "";
            if(account.getPhotoUrl() != null) {
                photoURL = account.getPhotoUrl().toString();
            }
            final String photo = photoURL;

            showCustomLoadingView();
            AndroidNetworking.post(MyApplication.BASE_URL + "user/login_google")
                    .addBodyParameter("g_id", googleId)
                    .addBodyParameter("email", email)
                    .addBodyParameter("one_id_android", MyApplication.one_id_android)
                    .setTag("google_login")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            hideCustomLoadingView();
                            try {
                                String status = response.getString("status");
                                if(status.equals("1")) {
                                    JSONObject user = response.getJSONObject("data");
                                    saveKeyValue("me", user.toString());
                                    String isRegister = response.getString("isRegister");
                                    if(isRegister.equals("true")) {
                                        ChooseUserTypeActivity.mUsername = username;
                                        ChooseUserTypeActivity.mPhoto = photo;
                                        //openTutorialScreen();
                                        startActivity(new Intent(mContext, ChooseUserTypeActivity.class));
                                    } else {
                                        User me = LoganSquare.parse(user.toString(), User.class);
                                        if (me.user_type.equals(MyApplication.USER_CUSTOMER)) {
                                            startActivity(new Intent(mContext, C_HomeActivity.class));
                                        } else if (me.user_type.equals(MyApplication.USER_BARBER)) {
                                            startActivity(new Intent(mContext, B_HomeActivity.class));
                                        } else if (me.user_type.equals(MyApplication.USER_BARBER_SHOP)) {
                                            Common.saveInfoWithKeyValue(mContext, KEY_USER_TYPE, MyApplication.USER_BARBER_SHOP);
                                            startActivity(new Intent(mContext, BS_HomeActivity.class));
                                        }
                                    }
                                    finish();
                                } else if (status.equals("false")) {
                                    showToast("Incorrect login");
                                } else {
                                    showToast( response.getString("error"));
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
                            //showToast(error.getErrorBody().toString());
                            showToast("Network Error!");
                        }
                    });

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ConnectionResult conResult = connectionResult;
        String message = conResult.getErrorMessage();
        showToast("Failed");
    }
}
