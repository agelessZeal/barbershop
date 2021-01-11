package com.odelan.qwork.ui.activity.main.customer;

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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class C_ProfileActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Uri mCropImageUri;
    private File mFile = null;

    int year, month, day;

    @BindView(R.id.photoIV)
    CircleImageView photoIV;
    List<User> mCustomer = new ArrayList<>();
    ArrayAdapter adapter;
    long selectedId = 0;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.barbershopET)
    EditText barbershopET;
    @BindView(R.id.usernameET)
    EditText usernameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.birthdayET)
    EditText birthdayET;

    @BindView(R.id.maleOpt)
    RadioButton maleOpt;

    @BindView(R.id.famaleOpt)
    RadioButton famaleOpt;

    NavigationView navigationView;
    private CheckBox cbAna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c__profile);

        mContext = this;
        ButterKnife.bind(this);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setSideProfile(navigationView.getHeaderView(0));

        User me = getUser(getValueFromKey("me"));

        if (me.photo != null && !TextUtils.isEmpty(me.photo)) {
            Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + me.photo).into(photoIV);
        }
        if (me.username != null && !TextUtils.isEmpty(me.username)) {
            usernameET.setText(me.username);
        }

        if (me.phone != null && !TextUtils.isEmpty(me.phone)) {
            phoneET.setText(me.phone);
        }

        if (me.birthday != null && !TextUtils.isEmpty(me.birthday)) {
            birthdayET.setText(me.birthday);
        }

        maleOpt.setChecked(true);
        if (!TextUtils.isEmpty(me.gender) && MyApplication.GENDER_FAMALE.equals(me.gender)) {
            famaleOpt.setChecked(true);
        }

        cbAna = (CheckBox) findViewById(R.id.cb_ana);
        cbAna.setChecked(me.is_ana != null && me.is_ana.equalsIgnoreCase("1"));
getAllBarbershops();
    }

    @SuppressLint("NewApi")
    @OnClick(R.id.photoIV)
    public void onPhotoClick() {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    @OnClick(R.id.birthdayET)
    public void onBirthDayETClick() {
        DatePickerDialog dlg = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                try {
                    Date date = formatter.parse(dateStr);
                    birthdayET.setText(formatter.format(date).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);
        dlg.show();
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

        if (TextUtils.isEmpty(usernameET.getText().toString())) {
            showToast("Please input username");
        } else if (TextUtils.isEmpty(phoneET.getText().toString())) {
            showToast("Please input phone number");
        } else if (TextUtils.isEmpty(birthdayET.getText().toString())) {
            showToast("Please input birthday");
        } else {
            try {
                String gender = MyApplication.GENDER_MALE;
                if (famaleOpt.isChecked()) {
                    gender = MyApplication.GENDER_FAMALE;
                }

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
               // showToast(mFile.getAbsolutePath()+"");

                User me = LoganSquare.parse(getValueFromKey("me"), User.class);
                showSecondaryCustomLoadingView();
                AndroidNetworking.upload(MyApplication.BASE_URL + "user_new/init_customer_profile")
                        .addMultipartFile("file", mFile)
                        .addMultipartParameter("userid", me.userid)
                        .addMultipartParameter("username", usernameET.getText().toString())
                        .addMultipartParameter("phone", phoneET.getText().toString())
                        .addMultipartParameter("birthday", birthdayET.getText().toString())
                        .addMultipartParameter("gender", gender)
                        .addMultipartParameter("previous_included_shop_id", "")
                        .addMultipartParameter("current_included_shop_id", mCustomer.get(((int) selectedId)).userid)
                        .addMultipartParameter("is_ana", cbAna.isChecked() ? "1" : "0")
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
                                        setSideProfile(navigationView.getHeaderView(0));
                                        showToast("Saved");
                                    } else if (status.equals("false")) {
                                        showToast("Don't saved");
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
                showToast("Faied to save");
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(mContext, C_HomeActivity.class));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(mContext, C_HomeActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, C_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, C_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(mContext, C_ContactActivity.class));
            finish();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                                mCustomer = new ArrayList<>();
                                try {
                                    mCustomer = LoganSquare.parseList(response.getString("data"), User.class);
                                    String[] barbernames = new String[mCustomer.size()];
                                    User me = getUser(getValueFromKey("me"));
                                    for (int i = 0; i < mCustomer.size(); i++) {
                                        barbernames[i] = mCustomer.get(i).username;
                                    }
                                    adapter = new ArrayAdapter<String>(mContext, R.layout.item_spinner, barbernames);
                                    spinner.setAdapter(adapter);
                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view,
                                                                   int position, long id) {
                                            selectedId = id;
                                            barbershopET.setText(mCustomer.get(((int) id)).username);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                        }
                                    });
                                   /* Log.e("barbershop_id_me",me.belongstoTV+"");
                                    showToast(me.included_shop_id+"");*/
                                    if (me.included_shop_id != null && !me.included_shop_id.equals("")) {
                                        int idx = 0;
                                        for (int i = 0; i < mCustomer.size(); i++) {
                                            User bs = mCustomer.get(i);
                                            if (me.included_shop_id.equals(bs.userid)) {
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
}
