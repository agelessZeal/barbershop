package com.odelan.qwork.ui.activity.main.barbershop;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.activity.main.customer.TutorialActivity;
import com.odelan.qwork.ui.base.BaseActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BS_CustomerSignedUp extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    TextView CustomerRecomCountTV;
    TextView offercountTv,openq,date,marketing,readmore,customers,percentagebt,percentagetop,count,toptile,whenhit,willbegin;
    BootstrapProgressBar progress1,progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.content_navi_signed);
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

        CustomerRecomCountTV = (TextView) findViewById(R.id.CustomerRecomCountTV);
        offercountTv = (TextView) findViewById(R.id.offercountTv);
        openq = (TextView) findViewById(R.id.openq);
        date = (TextView) findViewById(R.id.date);
        toptile = (TextView) findViewById(R.id.toptitle);
        marketing = (TextView) findViewById(R.id.marketing);
        customers = (TextView) findViewById(R.id.customer);
        readmore = (TextView) findViewById(R.id.readmore);
        percentagebt = (TextView) findViewById(R.id.percentagebt);
        percentagetop = (TextView) findViewById(R.id.percentagetop);
        whenhit= (TextView) findViewById(R.id.whenhit);
        offercountTv= (TextView) findViewById(R.id.offercountTv);
        willbegin= (TextView) findViewById(R.id.willbegin);
        count = (TextView) findViewById(R.id.count);
        progress = (BootstrapProgressBar) findViewById(R.id.progress);
        progress1 = (BootstrapProgressBar) findViewById(R.id.progress1);
        // Display the spannable text to TextView
        progress.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        progress.setBootstrapSize(5.00f);

        progress1.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        progress1.setBootstrapSize(5.00f);


        Typeface tf= Typeface.createFromAsset(getAssets(),"fonts/DoppioOne-Regular.ttf");

        toptile .setTypeface(tf);
        readmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://barberq.co/readmore.html"));
                startActivity(browserIntent);
            }
        });
setProfile();
        Typeface tf2= Typeface.createFromAsset(getAssets(),"fonts/DoHyeon-Regular.ttf");
        Typeface tf3= Typeface.createFromAsset(getAssets(),"fonts/Dhurjati-Regular.ttf");
        openq.setTypeface(tf2);
        marketing.setTypeface(tf2);
        date.setTypeface(tf2);
        count.setTypeface(tf3);
        percentagetop.setTypeface(tf3);
        percentagebt.setTypeface(tf3);
        readmore.setTypeface(tf3);
        customers.setTypeface(tf2);
        whenhit.setTypeface(tf2);
        offercountTv.setTypeface(tf2);
        willbegin.setTypeface(tf2);
        CustomerRecomCountTV.setTypeface(tf2);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

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
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, BS_HistoryActivity.class));
            finish();
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_tutorial) {
            startActivity(new Intent(mContext, TutorialActivity.class)
                    .putExtra("launch_type",TutorialActivity.LAUNCH_HOME)
                    .putExtra("userType",MyApplication.USER_BARBER_SHOP));
            finish();
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(mContext, BS_ContactActivity.class));
            finish();
        } else if (id == R.id.nav_logout) {
            logout();
        }else if (id == R.id.nav_customer_signed) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, BS_HomeActivity.class));
    }
    private void setText(String count,String text,TextView setText){
        SpannableString viewtext=  new SpannableString(count+text);
        viewtext.setSpan(new RelativeSizeSpan(2f), 0,count.length(), 0); // set size
        setText.setText(viewtext);
    }


    private void setProfile(){
        User me = getUser(getValueFromKey("me"));
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

                                int result = 0,result2 = 0;
                                int belongebarber = Integer.parseInt (jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop"));
                                int barbertarget = Integer.parseInt (jsonLegs.getJSONObject(0).getString("barbershop_target"));
                                int Marketingtarget = Integer.parseInt (jsonLegs.getJSONObject(0).getString("Marketing_target"));
                            if (!jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop").equals("0")&&
                                    !jsonLegs.getJSONObject(0).getString("barbershop_target").equals("0")){
                                result = Math.round ((belongebarber * 100)/  barbertarget);

                            }
                                if (!jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop").equals("0")&&
                                        !jsonLegs.getJSONObject(0).getString("Marketing_target").equals("0")) {
                                    result2 = Math.round((belongebarber * 100) / Marketingtarget);
                                }
                               //customers.setText("Customers: "+Integer.parseInt (jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop"))+"/"+Integer.parseInt (jsonLegs.getJSONObject(0).getString("barbershop_target"))+" "+result+"%");
                               count.setText(""+Integer.parseInt (jsonLegs.getJSONObject(0).getString("Belongs_to_barbershop"))+"/"+Integer.parseInt (jsonLegs.getJSONObject(0).getString("barbershop_target")));
                                percentagebt.setText(result+"%");



                                percentagetop.setText(result2+"%");
                                if (result>100){
                                    progress1.setProgress(100);
                                }else {
                                    progress1.setProgress(result);
                                }
                                if (result2>100){
                                    progress.setProgress(100);
                                }else {
                                    progress.setProgress(result2);
                                }

                               /* progress.setProgress(result2);
                                progress1.setProgress(result);*/

                                setText(jsonLegs.getJSONObject(0).getString("barbershop_target")+" ", getString(R.string.signedupcountTV), CustomerRecomCountTV);
                                setText(jsonLegs.getJSONObject(0).getString("Marketing_target")+" ", getString(R.string.offercountTv), offercountTv);
                                openq.setText(" "+getString(R.string.openq)+" ["+jsonLegs.getJSONObject(0).getString("barbershop_target")+"]");
                                marketing.setText(getString(R.string.marketing)+" ["+jsonLegs.getJSONObject(0).getString("Marketing_target")+"] ");

                                setText(jsonLegs.getJSONObject(0).getString("remaining_date_count")+" ", getString(R.string.date)+" "+jsonLegs.getJSONObject(0).getString("next_30_date"), date);
                           if (jsonLegs.getJSONObject(0).getString("remaining_date_count").equals("0")){
                               date.setVisibility(View.GONE);
                           }
                           if (jsonLegs.getJSONObject(0).getString("created_on").equals("0")){
                               date.setVisibility(View.GONE);
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
                        hideCustomLoadingView();
                        showToast(error.getErrorBody().toString()+"2");
                    }
                });
    }
    public static String addDay(String oldDate, int numberOfDays) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_YEAR,numberOfDays);
        dateFormat=new SimpleDateFormat("dd-MM-yyyy");
        Date newDate=new Date(c.getTimeInMillis());
        String resultDate=dateFormat.format(newDate);
        return resultDate;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

}
