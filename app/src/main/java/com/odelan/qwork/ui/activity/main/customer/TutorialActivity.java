package com.odelan.qwork.ui.activity.main.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.activity.intro.ChooseUserTypeActivity;
import com.odelan.qwork.ui.activity.main.barber.B_HomeActivity;
import com.odelan.qwork.ui.activity.main.barber.BarberProfileActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BS_HomeActivity;
import com.odelan.qwork.ui.activity.main.barbershop.BarbershopProfileActivity;
import com.odelan.qwork.widget.ViewPagerIndicator;
import com.squareup.picasso.Picasso;

public class TutorialActivity extends AppCompatActivity {

    public static final String LAUNCH_HOME = "Home";
    public static final String LAUNCH_PROFILE = "Profile";
    public static final String LAUNCH_CHOOSE_USER_TYPE = "choose_user_type";
    public static final String LAUNCH_CUSTOMER_PROFILE = "launch_customer_profile";
    private ViewPagerIndicator mViewPagerIndicator;
    private Integer[] c;
    private ViewPager mViewPager;
    private Integer[] images = {
            R.drawable.screenshot_1,
            R.drawable.screenshot_2,
            R.drawable.screenshot_3,
            R.drawable.screenshot_4,
            R.drawable.screenshot_5,
            R.drawable.screenshot_6,
            R.drawable.screenshot_7
    };
    private Integer[] barber_images = {
            R.drawable.barber_0,
            R.drawable.barber_1,
            R.drawable.barber_2,
            R.drawable.barber_3,
            R.drawable.barber_4,
            R.drawable.barber_5,
            R.drawable.barber_6,
            R.drawable.barber_7,
            R.drawable.barber_8,
            R.drawable.barber_9,
            R.drawable.barber_10
    };
    private Integer[] barber_shop_images = {
            R.drawable.barbershop_0,
            R.drawable.barbershop_1,
            R.drawable.barbershop_2,
            R.drawable.barbershop_3,
            R.drawable.barbershop_5,
            R.drawable.barbershop_4,
            R.drawable.barbershop_6,
            R.drawable.barbershop_7
    };
    private Button btnSkip;
    private String launchType;
    private String userType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        getLaunchType();
        initView();
        setListeners();
        setAdapter();


    }
    private void getLaunchType(){
        launchType = getIntent().getStringExtra("launch_type");
        userType = getIntent().getStringExtra("userType");
        if (userType==null){
            userType="";
        }
    }
    private void initView(){
        mViewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.view_pager_indicator);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        btnSkip = (Button) findViewById(R.id.btn_skip);
    }
    private void setListeners(){
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (launchType.equals(LAUNCH_HOME)){

                    if (userType.equals(MyApplication.USER_BARBER)){
                        intent = new Intent(TutorialActivity.this,B_HomeActivity.class);
                    }else if (userType.equals(MyApplication.USER_BARBER_SHOP)){
                        intent = new Intent(TutorialActivity.this,BS_HomeActivity.class);
                    }else {
                        intent = new Intent(TutorialActivity.this,C_HomeActivity.class);
                    }
                }else  if (launchType.equals(LAUNCH_PROFILE)){

                    if (userType.equals(MyApplication.USER_BARBER)){
                        intent = new Intent(TutorialActivity.this,BarberProfileActivity.class);
                    }else if (userType.equals(MyApplication.USER_BARBER_SHOP)){
                        intent = new Intent(TutorialActivity.this,BarbershopProfileActivity.class);
                    }else if (userType.equals(MyApplication.USER_CUSTOMER)){
                        intent = new Intent(TutorialActivity.this,CustomerProfileActivity.class);
                    }
                }
                else  if (launchType.equals(LAUNCH_CHOOSE_USER_TYPE)){
                    intent = new Intent(TutorialActivity.this,ChooseUserTypeActivity.class);
                }
                else  if (launchType.equals(LAUNCH_CUSTOMER_PROFILE)){
                   // intent = new Intent(TutorialActivity.this,CustomerProfileActivity.class);
                    if (userType.equals(MyApplication.USER_BARBER)){
                        intent = new Intent(TutorialActivity.this,BarberProfileActivity.class);
                    }else if (userType.equals(MyApplication.USER_BARBER_SHOP)){
                        intent = new Intent(TutorialActivity.this,BarbershopProfileActivity.class);
                    }else if (userType.equals(MyApplication.USER_CUSTOMER)){
                        intent = new Intent(TutorialActivity.this,CustomerProfileActivity.class);
                    }
                }

                if (intent != null){
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void setAdapter(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewPager.setAdapter(new MyPagerAdapter(inflater));
        mViewPagerIndicator.setupWithViewPager(mViewPager);
        mViewPagerIndicator.addOnPageChangeListener(mOnPageChangeListener);
    }

    private class MyPagerAdapter extends PagerAdapter
    {
        private LayoutInflater inflater ;
        MyPagerAdapter(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        public
        int getCount() {
            switch(userType)
            {
                case "barber":
                  images = barber_images;
                    break;
                case "barber_shop":
                    images = barber_shop_images;
                    break;
                case "customer":

                    break;
                default:

            }
            return images.length;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public
        Object instantiateItem(final ViewGroup container, final int position) {
            DisplayMetrics metrics = TutorialActivity.this.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            View v = inflater.inflate(R.layout.child_tutorial,container,false);
            ImageView ivTutorial = (ImageView) v.findViewById(R.id.iv_tutorial);
           // Picasso.with(TutorialActivity.this).load(images[position]).resize(width,height).into(ivTutorial);
            switch(userType)
            {
                case "barber":
                    Picasso.with(TutorialActivity.this).load(barber_images[position]).resize(width,height).into(ivTutorial);
                    break;
                case "barber_shop":
                    Picasso.with(TutorialActivity.this).load(barber_shop_images[position]).resize(width,height).into(ivTutorial);
                    break;
                case "customer":
                    Picasso.with(TutorialActivity.this).load(images[position]).resize(width,height).into(ivTutorial);
                    break;
                default:
                    Picasso.with(TutorialActivity.this).load(images[position]).resize(width,height).into(ivTutorial);
            }
            container.addView(v);
            return v;
        }

        @Override
        public
        boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public
        void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public
        CharSequence getPageTitle(final int position) {
            return String.valueOf(position);
        }
    }

    @NonNull
    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public
        void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

        }

        @Override
        public
        void onPageSelected(final int position) {
            //Toast.makeText(MainActivity.this, "Page selected " + position, Toast.LENGTH_SHORT).show();
            switch(userType)
            {
                case "barber":
                    images = barber_images;
                    break;
                case "barber_shop":
                    images = barber_shop_images;
                    break;

            }
            if (position == images.length -1 ){
                btnSkip.setText("Finish");

            }
            else {
                btnSkip.setText("Skip Tutorial");
            }
        }

        @Override
        public
        void onPageScrollStateChanged(final int state) {

        }
    };
}
