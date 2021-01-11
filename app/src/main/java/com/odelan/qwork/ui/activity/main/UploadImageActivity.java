package com.odelan.qwork.ui.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.barber.B_ContactActivity;
import com.odelan.qwork.ui.activity.main.barber.B_HistoryActivity;
import com.odelan.qwork.ui.activity.main.barber.B_HomeActivity;
import com.odelan.qwork.ui.activity.main.barber.B_NotificationActivity;
import com.odelan.qwork.ui.activity.main.barber.B_ProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_ContactActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_HistoryActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_HomeActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_NotificationActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_ProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.odelan.qwork.MyApplication.USER_BARBER;
import static com.odelan.qwork.MyApplication.USER_BARBER_SHOP;

public class UploadImageActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Uri mCropImageUri;
    private File mFile = null;

    @BindView(R.id.iv)
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

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


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.iconIV) public void onIconClick() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @OnClick(R.id.uploadIV) public void onUpload() {
        try {
            if(mFile == null) {
                Bitmap bmp = Common.getBitmapFromImageView(iv);
                String filepath = Common.creatAppInnerDir(mContext, "tmp")+"/"+Common.generateTimeRand("")+".png";
                if(Common.saveBmpToFile(bmp, filepath)) {
                    mFile = new File(filepath);
                } else {
                    showToast("Please input photo");
                    return;
                }
            }

            User me = LoganSquare.parse(getValueFromKey("me"), User.class);

            showSecondaryCustomLoadingView();
            AndroidNetworking.upload(MyApplication.BASE_URL + "image_upload/uploadImageWithUserId")
                    .addMultipartFile("file", mFile)
                    .addMultipartParameter("userid", me.userid)
                    .setPriority(Priority.LOW)
                    .setTag("uploadImageWithUserId")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            hideCustomLoadingView();
                            try {
                                String status = response.getString("status");
                                if (status.equals("1")) {
                                    showToast("Successfully Uploaded");
                                } else if (status.equals("false")) {
                                    showToast("Didn't uploaded, please try upload again");
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
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                iv.setImageURI(result.getUri());
                mFile = new File(FileUtils.saveImageToInternalStorageFromUri(mContext, result.getUri()));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showToast("Cropping failed: " + result.getError());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            showToast("Cancelling, required permissions are not granted");
        }*/

        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    /** Start crop image activity for the given image. **/
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        User me = getUser(getValueFromKey("me"));
        if(me.user_type.equals(USER_BARBER)) {
            if (id == R.id.nav_home) {
                startActivity(new Intent(mContext, B_HomeActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(mContext, B_ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_notification) {
                startActivity(new Intent(mContext, B_NotificationActivity.class));
                finish();
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(mContext, B_HistoryActivity.class));
                finish();
            }else if (id == R.id.nav_contact_us) {
                startActivity(new Intent(mContext, B_ContactActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
            }
        } else if (me.user_type.equals(USER_BARBER_SHOP)) {
            if (id == R.id.nav_home) {
                startActivity(new Intent(mContext, BS_HomeActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(mContext, BS_ProfileActivity.class));
                finish();
            } else if (id == R.id.nav_request_list) {
                startActivity(new Intent(mContext, BS_RequestListActivity.class));
                finish();
            } else if (id == R.id.nav_notification) {
                startActivity(new Intent(mContext, BS_NotificationActivity.class));
                finish();
            }else if (id == R.id.nav_history) {
                startActivity(new Intent(mContext, BS_HistoryActivity.class));
                finish();
            } else if (id == R.id.nav_contact_us) {
                startActivity(new Intent(mContext, BS_ContactActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                logout();
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        User me = getUser(getValueFromKey("me"));
        if(me.user_type.equals(USER_BARBER)) {
            startActivity(new Intent(mContext, B_HomeActivity.class));
        }else if (me.user_type.equals(USER_BARBER_SHOP)) {
            startActivity(new Intent(mContext, BS_HomeActivity.class));
        }

    }
}
