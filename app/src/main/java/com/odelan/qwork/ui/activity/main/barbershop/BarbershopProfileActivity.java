package com.odelan.qwork.ui.activity.main.barbershop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
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
import com.odelan.qwork.widget.google_place_api.AutoComplete;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class BarbershopProfileActivity extends BaseActivity {

    public static String mUsername = "";
    public static String mPhoto = "";

    public static String mEmail = "";
    public static String mPwd = "";

    private Uri mCropImageUri;
    private File mFile = null;

    Calendar c;
    int hour, min;

    @BindView(R.id.photoIV)
    CircleImageView photoIV;

    @BindView(R.id.usernameET)
    EditText usernameET;

    @BindView(R.id.phoneET)
    EditText phoneET;

    @BindView(R.id.addressET)
    EditText addressET;

    @BindView(R.id.descriptionET)
    EditText descriptionET;

    @BindView(R.id.fbLinkET)
    EditText fbLinkET;

    @BindView(R.id.instagramLinkET)
    EditText instagramLinkET;

    @BindView(R.id.twtLinkET)
    EditText twtLinkET;

    @BindView(R.id.startMonTimeET)
    EditText startMonTimeET;

    @BindView(R.id.endMonTimeET)
    EditText endMonTimeET;

    @BindView(R.id.startTueTimeET)
    EditText startTueTimeET;

    @BindView(R.id.endTueTimeET)
    EditText endTueTimeET;

    @BindView(R.id.startWedTimeET)
    EditText startWedTimeET;

    @BindView(R.id.endWedTimeET)
    EditText endWedTimeET;

    @BindView(R.id.startThrTimeET)
    EditText startThrTimeET;

    @BindView(R.id.endThrTimeET)
    EditText endThrTimeET;

    @BindView(R.id.startFriTimeET)
    EditText startFriTimeET;

    @BindView(R.id.endFriTimeET)
    EditText endFriTimeET;

    @BindView(R.id.startSatTimeET)
    EditText startSatTimeET;

    @BindView(R.id.endSatTimeET)
    EditText endSatTimeET;

    @BindView(R.id.startSunTimeET)
    EditText startSunTimeET;



    @BindView(R.id.endSunTimeET)
    EditText endSunTimeET;

    private String closeState="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barbershop_profile);

        mContext = this;
        ButterKnife.bind(this);

        showTitleIV();
        hideTitle();
        hideLeftIV();
        showRightTV();

        usernameET.setText(mUsername);
        if(mPhoto != null && !mPhoto.equals("")) {
            Picasso.with(mContext).load(mPhoto).into(photoIV);
        }

        c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);

        readyGPS();


        CompoundButton.OnCheckedChangeListener multiListener = new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton v, boolean isChecked) {

                ArrayList<String> closeStateList = new ArrayList<String>();
                if (closeState != "" && closeState != null) {
                    String[] states = closeState.split(",");
                    for(int i=0;i<states.length;i++){
                        closeStateList.add(states[i]);
                    }
                }

                switch (v.getId()){
                    case R.id.switch1:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "21");
                        } else {
                            closeStateList = removeAll(closeStateList, "21");
                            closeStateList.add("21");

                        }
                        break;
                    case R.id.switch2:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "22");
                        } else {
                            closeStateList = removeAll(closeStateList, "22");
                            closeStateList.add("22");
                        }
                        break;
                    case R.id.switch3:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "23");
                        } else {
                            closeStateList= removeAll(closeStateList, "23");
                            closeStateList.add("23");
                        }
                        break;
                    case R.id.switch4:
                        if (isChecked) {
                            closeStateList= removeAll(closeStateList, "24");
                        } else {
                            closeStateList= removeAll(closeStateList, "24");
                            closeStateList.add("24");
                        }
                        break;
                    case R.id.switch5:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "25");
                        } else {
                            closeStateList = removeAll(closeStateList, "25");
                            closeStateList.add("25");
                        }
                        break;
                    case R.id.switch6:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "26");
                        } else {
                            closeStateList = removeAll(closeStateList, "26");
                            closeStateList.add("26");
                        }
                        break;
                    case R.id.switch7:
                        if (isChecked) {
                            closeStateList = removeAll(closeStateList, "27");
                        } else {
                            closeStateList = removeAll(closeStateList, "27");
                            closeStateList.add("27");
                        }
                        break;
                }
                closeState = TextUtils.join(",", closeStateList);
            }
        };

        ((Switch) findViewById(R.id.switch1)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch2)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch3)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch4)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch5)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch6)).setOnCheckedChangeListener(multiListener);
        ((Switch) findViewById(R.id.switch7)).setOnCheckedChangeListener(multiListener);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(MyApplication.g_address != null) {
            addressET.setText(MyApplication.g_address);
        }
    }

    public ArrayList<String> removeAll(ArrayList<String> list, String val) {
        ArrayList<String> tmp = new ArrayList<String>();
        for(int i=0;i<list.size();i++){
            if(!val.equals(list.get(i))){
                tmp.add(list.get(i));
            }
        }
        return tmp;
    }

    @SuppressLint("NewApi")
    @OnClick(R.id.photoIV) public void onPhotoClick() {
        //CropImage.startPickImageActivity(this);
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
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

    /** Start crop image activity for the given image. **/
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @OnClick(R.id.addressET) public void onClickAddressET () {
        startActivity(new Intent(mContext, AutoComplete.class));
    }

    @OnClick(R.id.startMonTimeET) public void onStartMonTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startMonTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endMonTimeET) public void onEndMonTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endMonTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }  @OnClick(R.id.startTueTimeET) public void onStartTueTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startTueTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endTueTimeET)
    public void onEndTueTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endTueTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.startWedTimeET)
    public void onStartWedTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startWedTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endWedTimeET)
    public void onEndWedTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endWedTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.startThrTimeET)
    public void onStartThrTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startThrTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endThrTimeET)
    public void onEndThrTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endThrTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.startFriTimeET)
    public void onStartFriTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if (tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if (minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startFriTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endFriTimeET)
    public void onEndFriTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endFriTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.startSatTimeET) public void onStartSatTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startSatTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endSatTimeET) public void onEndSatTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endSatTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.startSunTimeET) public void onStartSunTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            startSunTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @OnClick(R.id.endSunTimeET) public void onEndSunTimeClick() {
        TimePickerDialog td = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String hh, mm;
                            int tmp = hourOfDay % 12;
                            if(tmp < 10) {
                                hh = "0" + tmp;
                            } else {
                                hh = String.valueOf(tmp);
                            }

                            if(minute < 10) {
                                mm = "0" + minute;
                            } else {
                                mm = String.valueOf(minute);
                            }
                            String amPm = hh + ":" + mm + " " + ((hourOfDay >= 12) ? "PM" : "AM");
                            endSunTimeET.setText(amPm);
                        } catch (Exception ex) {
                            showToast("Time parse error");
                        }
                    }
                },
                hour, min,
                DateFormat.is24HourFormat(mContext)
        );
        td.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightTV :
                if(usernameET.getText().toString().equals("")) {
                    showToast("Please input username");
                } else if (phoneET.getText().toString().equals("")) {
                    showToast("Please input phone number");
//                } else if (addressET.getText().toString().equals("")) {
//                    showToast("Please input address");
                } /*else if (descriptionET.getText().toString().equals("")) {
                    showToast("Please input description");
                } else if (fbLinkET.getText().toString().equals("")) {
                    showToast("Please input facebook profile link");
                } else if (instagramLinkET.getText().toString().equals("")) {
                    showToast("Please input instagram profile link");
                } else if (twtLinkET.getText().toString().equals("")) {
                    showToast("Please input twitter profile link");
         }*/ else if (startMonTimeET.getText().toString().equals("") && !closeState.contains("21")) {
                    showToast("Please input opening time");
                } else if (endMonTimeET.getText().toString().equals("") && !closeState.contains("21")) {
                    showToast("Please input opening time");
                } else if (startTueTimeET.getText().toString().equals("") && !closeState.contains("22")) {
                    showToast("Please input opening time");
                } else if (endTueTimeET.getText().toString().equals("") && !closeState.contains("22")) {
                    showToast("Please input opening time");
                } else if (startWedTimeET.getText().toString().equals("") && !closeState.contains("23")) {
                    showToast("Please input opening time");
                } else if (endWedTimeET.getText().toString().equals("") && !closeState.contains("23")) {
                    showToast("Please input opening time");
                } else if (startThrTimeET.getText().toString().equals("") && !closeState.contains("24")) {
                    showToast("Please input opening time");
                } else if (endThrTimeET.getText().toString().equals("") && !closeState.contains("24")) {
                    showToast("Please input opening time");
                } else if (startFriTimeET.getText().toString().equals("") && !closeState.contains("25")) {
                    showToast("Please input opening time");
                } else if (endFriTimeET.getText().toString().equals("") && !closeState.contains("25")) {
                    showToast("Please input opening time");
                } else if (startSatTimeET.getText().toString().equals("") && !closeState.contains("26")) {
                    showToast("Please input opening time");
                } else if (endSatTimeET.getText().toString().equals("") && !closeState.contains("26")) {
                    showToast("Please input opening time");
                } else if (startSunTimeET.getText().toString().equals("") && !closeState.contains("27")) {
                    showToast("Please input opening time");
                } else if (endSunTimeET.getText().toString().equals("") && !closeState.contains("27")) {
                    showToast("Please input opening time");
                } else {
                    try {
                        if(mFile == null) {
                            try {
                                Bitmap bmp = Common.getBitmapFromImageView(photoIV);
                                String filepath = Common.creatAppInnerDir(mContext, "tmp") + "/" + Common.generateTimeRand("") + ".png";
                                if (Common.saveBmpToFile(bmp, filepath)) {
                                    mFile = new File(filepath);
                                } else {
                                    showToast("Please input photo");
                                    return;
                                }
                            }catch (Exception e){
                                showToast("Please input photo");
                                return;
                            }
                        }

//                        User me = getUser()
                        User me = LoganSquare.parse(getValueFromKey("me"), User.class);
                        showSecondaryCustomLoadingView();
                        AndroidNetworking.upload(MyApplication.BASE_URL + "user/init_barbershop_profile")
                                .addMultipartFile("file", mFile)
                                .addMultipartParameter("userid", me.userid)
                                .addMultipartParameter("email", mEmail)
                                .addMultipartParameter("password", mPwd)
                                .addMultipartParameter("username", usernameET.getText().toString())
                                .addMultipartParameter("phone", phoneET.getText().toString())
                                .addMultipartParameter("address", addressET.getText().toString())
                                .addMultipartParameter("latitude", String.valueOf(MyApplication.g_latitude))
                                .addMultipartParameter("longitude", String.valueOf(MyApplication.g_longitude))
                                .addMultipartParameter("mon_time_start", startMonTimeET.getText().toString())
                                .addMultipartParameter("mon_time_end", endMonTimeET.getText().toString())
                                .addMultipartParameter("tue_time_start", startTueTimeET.getText().toString())
                                .addMultipartParameter("tue_time_end", endTueTimeET.getText().toString())
                                .addMultipartParameter("wed_time_start", startWedTimeET.getText().toString())
                                .addMultipartParameter("wed_time_end", endWedTimeET.getText().toString())
                                .addMultipartParameter("thr_time_start", startThrTimeET.getText().toString())
                                .addMultipartParameter("thr_time_end", endThrTimeET.getText().toString())
                                .addMultipartParameter("fri_time_start", startFriTimeET.getText().toString())
                                .addMultipartParameter("fri_time_end", endFriTimeET.getText().toString())
                                .addMultipartParameter("sat_time_start", startSatTimeET.getText().toString())
                                .addMultipartParameter("sat_time_end", endSatTimeET.getText().toString())
                                .addMultipartParameter("sun_time_start", startSunTimeET.getText().toString())
                                .addMultipartParameter("sun_time_end", endSunTimeET.getText().toString())
                                .addMultipartParameter("close_state", closeState)
                                .addMultipartParameter("description", descriptionET.getText().toString())
                                .addMultipartParameter("fb_profile_link", fbLinkET.getText().toString())
                                .addMultipartParameter("instagram_profile_link", instagramLinkET.getText().toString())
                                .addMultipartParameter("twt_profile_link", twtLinkET.getText().toString())
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
                                                startActivity(new Intent(mContext, BS_HomeActivity.class));
                                                finish();
                                            } else if (status.equals("false")) {
                                                showToast("Don't saved");
                                            } else {
                                                showToast("Network Error!");
                                            }
                                        } catch (Exception e) {
//                                            e.printStackTrace();
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
//                        e.printStackTrace();
                        showToast("Faied to save");
                    }
                }
                break;
        }
    }
}
