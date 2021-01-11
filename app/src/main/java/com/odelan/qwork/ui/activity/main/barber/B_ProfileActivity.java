package com.odelan.qwork.ui.activity.main.barber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Common;
import com.odelan.qwork.utils.FileUtils;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.odelan.qwork.MyApplication.GENDER_FAMALE;
import static com.odelan.qwork.MyApplication.GENDER_MALE;
import static com.odelan.qwork.utils.SessionUtils.KEY_LOGIN_TYPE;
import static com.odelan.qwork.utils.SessionUtils.LOGIN_EMAIL;

public class B_ProfileActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Uri mCropImageUri;
    private File mFile = null;

    @BindView(R.id.photoIV)
    CircleImageView photoIV;

    @BindView(R.id.usernameET)
    EditText usernameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.birthdayET)
    EditText birthdayET;

    @BindView(R.id.descriptionET)
    EditText descriptionET;

    @BindView(R.id.barbershopET)
    EditText barbershopET;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.fbLinkET)
    EditText fbLinkET;

    @BindView(R.id.instagramLinkET)
    EditText instagramLinkET;

    @BindView(R.id.twtLinkET)
    EditText twtLinkET;

    @BindView(R.id.upadtepasswordET)
    EditText upadtepasswordET;

    @BindView(R.id.updatepasswordTitle)
    TextView updatepasswordTitle;

    @BindView(R.id.maleOpt)
    RadioButton maleOpt;

    @BindView(R.id.femaleOpt)
    RadioButton femaleOpt;

    Calendar c;
    int year, month, day;

    List<User> mBarbershops = new ArrayList<>();
    ArrayAdapter adapter;
    long selectedId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b__profile);

        mContext = this;
        ButterKnife.bind(this);
        Log.v("islogintype",Common.getInfoWithValueKey(mContext, KEY_LOGIN_TYPE));
if(Common.getInfoWithValueKey(mContext, KEY_LOGIN_TYPE).equals(LOGIN_EMAIL)){
    upadtepasswordET.setVisibility(View.VISIBLE);
    updatepasswordTitle.setVisibility(View.VISIBLE);
}
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView saveTV = (TextView) toolbar.findViewById(R.id.saveTV);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setSideProfile(navigationView.getHeaderView(0));

        c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        getAllBarbershops();

        User me = getUser(getValueFromKey("me"));
        if (me.photo != null && !me.photo.equals("")) {
            Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + me.photo).into(photoIV);
        }

        if (me.username != null && !me.username.equals("")) {
            usernameET.setText(me.username);
        }

        if (me.phone != null && !me.phone.equals("")) {
            phoneET.setText(me.phone);
        }

        if (me.birthday != null && !me.birthday.equals("")) {
            birthdayET.setText(me.birthday);
        }

        if (me.desc != null && !me.desc.equals("")) {
            descriptionET.setText(me.desc);
        }

        if (me.fb_profile_link != null) {
            fbLinkET.setText(me.fb_profile_link);
        }



        if (me.instagram_profile_link != null) {
            instagramLinkET.setText(me.instagram_profile_link);
        }

        if (me.twt_profile_link != null) {
            twtLinkET.setText(me.twt_profile_link);
        }

        maleOpt.setChecked(true);
        if (me.gender!=null&&me.gender.equals(GENDER_FAMALE)) {
            femaleOpt.setChecked(true);
        }
    }

    public void getAllBarbershops() {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getAllBarbershops")
                .setTag("getAllBarbershops")
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
                                mBarbershops = new ArrayList<>();
                                try {
                                    mBarbershops = LoganSquare.parseList(response.getString("data"), User.class);
                                    String[] barbernames = new String[mBarbershops.size()];
                                    User me = getUser(getValueFromKey("me"));
                                    for (int i = 0; i < mBarbershops.size(); i++) {
                                        barbernames[i] = mBarbershops.get(i).username;
                                    }
                                    adapter = new ArrayAdapter<String>(mContext, R.layout.item_spinner, barbernames);
                                    spinner.setAdapter(adapter);
                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view,
                                                                   int position, long id) {
                                            selectedId = id;
                                            barbershopET.setText(mBarbershops.get(((int) id)).username);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });

                                    if (me.barbershop_id != null && !me.barbershop_id.equals("")) {
                                        int idx = 0;
                                        for (int i = 0; i < mBarbershops.size(); i++) {
                                            User bs = mBarbershops.get(i);
                                            if (me.barbershop_id.equals(bs.userid)) {
                                                idx = i;
                                            }
                                        }
                                        spinner.setSelection(idx);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (status.equals("false")) {
                                showToast("Network error");
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

    @SuppressLint("NewApi")
    @OnClick(R.id.photoIV)
    public void onPhotoClick() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
        //CropImage.startPickImageActivity(this);
    }

    @OnClick(R.id.birthdayET)
    public void onBirthdayClick() {
        DatePickerDialog dd = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String dateInString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            Date date = formatter.parse(dateInString);

                            birthdayET.setText(formatter.format(date).toString());

                        } catch (Exception ex) {

                        }


                    }
                }, year, month, day);
        dd.show();
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
                photoIV.setImageURI(result.getUri());
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

    /**
     * Start crop image activity for the given image.
     **/
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    public void saveProfile() {
        if (usernameET.getText().toString().equals("")) {
            showToast("Please input username");
        } else if (phoneET.getText().toString().equals("")) {
            showToast("Please input phone number");
        } else if (birthdayET.getText().toString().equals("")) {
            showToast("Please input birthday");
        } /*else if (descriptionET.getText().toString().equals("")) {
            showToast("Please input description");
        } else if (fbLinkET.getText().toString().equals("")) {
            showToast("Please input facebook profile link");
        } else if (instagramLinkET.getText().toString().equals("")) {
            showToast("Please input instagram profile link");
        } else if (twtLinkET.getText().toString().equals("")) {
            showToast("Please input twitter profile link");
        }*/ else if (barbershopET.getText().toString().equals("")) {
            showToast("Please select barbershop");
        } else {
            try {
                if (mFile == null) {
                    try {
                        Bitmap bmp = Common.getBitmapFromImageView(photoIV);
                        String filepath = Common.creatAppInnerDir(mContext, "tmp") + "/" + Common.generateTimeRand("") + ".png";
                        if (Common.saveBmpToFile(bmp, filepath)) {
                            mFile = new File(filepath);
                        } else {
                            showToast("Please input photo");
                            return;
                        }
                    } catch (Exception e) {
                        showToast("Please input photo");
                        return;
                    }
                }

                String gender = GENDER_MALE;
                if (femaleOpt.isChecked()) {
                    gender = GENDER_FAMALE;
                }

                User me = LoganSquare.parse(getValueFromKey("me"), User.class);
                showSecondaryCustomLoadingView();
                AndroidNetworking.upload(MyApplication.BASE_URL + "user_new/init_barber_profile")
                        .addMultipartFile("file", mFile)
                        .addMultipartParameter("userid", me.userid)
                        .addMultipartParameter("username", usernameET.getText().toString())
                        .addMultipartParameter("phone", phoneET.getText().toString())
                        .addMultipartParameter("birthday", birthdayET.getText().toString())
                        .addMultipartParameter("description", descriptionET.getText().toString())
                        .addMultipartParameter("included_shop_id", mBarbershops.get((int) selectedId).userid)
                        .addMultipartParameter("fb_profile_link", fbLinkET.getText().toString())
                        .addMultipartParameter("instagram_profile_link", instagramLinkET.getText().toString())
                        .addMultipartParameter("twt_profile_link", twtLinkET.getText().toString())
                        .addMultipartParameter("gender", gender)
                        .addMultipartParameter("password", upadtepasswordET.getText().toString())
                        .setPriority(Priority.LOW)
                        .setTag("update_profile")
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // do anything with response
                                hideCustomLoadingView();
                                try {
                                    String status = response.getString("status");
                                    if (status.equals("1")) {
                                        JSONObject user = response.getJSONObject("data");
                                        saveKeyValue("me", user.toString());
                                        showToast("Saved");
                                    } else if (status.equals("false")) {
                                        showToast("Don't saved");
                                    } else {
                                        showToast(response.getString("error"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showToast("Network Error!");
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                hideCustomLoadingView();
                              //  showToast(error.getErrorBody().toString());
                            }
                        });
            } catch (Exception e) {
                showToast("Faied to save");
            }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(mContext, B_HomeActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, B_NotificationActivity.class));
            finish();
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


}
