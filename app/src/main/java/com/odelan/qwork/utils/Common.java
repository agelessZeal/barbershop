package com.odelan.qwork.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.odelan.qwork.MyApplication;
import com.odelan.qwork.ui.activity.intro.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Administrator on 6/17/2016.
 */
public class Common {
    public static String TAG = "Common";
    public static ProgressDialog progressDialog;

    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static String getInfoWithValueKey(Context context, String key) {

        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static void saveInfoWithKeyValue(Context context, String key, String username) {

        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, username);
        editor.commit();
    }

    public static Bitmap getBitmapFromImageView(ImageView iv) {
        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable .getBitmap();
        return bitmap;
    }

    public void readyGPS (Context con) {
        MyApplication.g_GPSTracker = new GPSTracker(con);
        if (MyApplication.g_GPSTracker.canGetLocation()) {
            MyApplication.g_latitude = MyApplication.g_GPSTracker.getLatitude(); // returns latitude
            MyApplication.g_longitude = MyApplication.g_GPSTracker.getLongitude(); // returns longitude
        } else {
            MyApplication.g_GPSTracker.showSettingsAlert();
        }
    }

    public static void showProgressDialog(Context context, boolean flag) {

        if (flag) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

        } else {
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

    public static String getJSONStringWithKey(JSONObject obj, String key){
        String result;
        try{
            result = obj.getString(key);

        } catch (JSONException e){
            Log.e(TAG, e.toString());
            return "";
        }

        return result;
    }

    public static JSONObject getJSONObjectWithKey(JSONObject obj, String key){
        JSONObject result;
        try{
            result = obj.getJSONObject(key);

        } catch (JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }

        return result;
    }

    public static JSONArray getJSONArrayWithKey(JSONObject obj, String key){
        JSONArray result;
        try{
            result = obj.getJSONArray(key);

        } catch (JSONException e){
            Log.e(TAG, e.toString());
            return null;
        }

        return result;
    }

    public static int getDeviceWidth(Activity activity) {
        Point point;
        WindowManager wm;

        wm = activity.getWindowManager();
        point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wm.getDefaultDisplay().getSize(point);
            return point.x;
        } else {
            return wm.getDefaultDisplay().getWidth();
        }
    }

    public static int getDeviceHeight(Activity activity) {
        Point point;
        WindowManager wm;

        wm = activity.getWindowManager();
        point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wm.getDefaultDisplay().getSize(point);
            return point.y;
        } else {
            return wm.getDefaultDisplay().getHeight();
        }
    }

    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            if(params.height>300){
                params.height = 300;
            }
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static String convertMillisecondToTimeString(int millis) {

        int hour = millis / (3600 * 1000);
        int min = (millis % (3600 * 1000)) / (60 * 1000);
        int second = (millis % (60 * 1000)) / 1000;
        int mil = millis % 1000 / 10;

        return String.format("%02d:%02d:%02d.%02d", hour, min, second, mil);
    }

    public static String convertMillisecondToSecondTimeString(int millis) {

        int hour = millis / (3600 * 1000);
        int min = (millis % (3600 * 1000)) / (60 * 1000);
        int second = (millis % (60 * 1000)) / 1000;

        return String.format("%02d:%02d:%02d", hour, min, second);
    }

    public static String generateTimeRand(String suffix) {
        if(suffix==null || suffix.equals("")) {
            return System.currentTimeMillis()+"";
        } else {
            return suffix + "_" + System.currentTimeMillis();
        }
    }

    public static String[] convertArrayListToArray (ArrayList<String> list) {
        String[] array = new String[list.size()];
        array = list.toArray(array);
        return array;
    }

    public static String creatAppInnerDir (Context con, String dirName) {
        String dirPath = "";
        PackageManager m = con.getPackageManager();
        String s = con.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            //String dirName = Common.generateTimeRand("");
            File dir = new File(s + "/files/" + dirName);
            if (!(dir.exists() && dir.isDirectory())) {
                dir.mkdirs();
            }
            dirPath = s + "/files/" + dirName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
        return dirPath;
    }

    public static boolean saveBmpToFile(Bitmap bmp, String path) {
        boolean result = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void savefile(Uri sourceuri){

        String sourceFilename= sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+ File.separatorChar+"audioTemp.mp3";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }
    }

    public static File getSavefile(Uri sourceuri){

        String sourceFilename= sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+ File.separatorChar+"audioTemp.mp3";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(destinationFilename);
    }

    public static boolean copyFile(String from, String to) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end+1, from.length());
                File source = new File(str1, str2);
                File destination= new File(to, str2);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }

}
