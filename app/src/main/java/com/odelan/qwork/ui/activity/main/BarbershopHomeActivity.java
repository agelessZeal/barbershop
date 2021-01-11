package com.odelan.qwork.ui.activity.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.DirectionObject;
import com.odelan.qwork.data.model.LegsObject;
import com.odelan.qwork.data.model.PolylineObject;
import com.odelan.qwork.data.model.RouteObject;
import com.odelan.qwork.data.model.StepsObject;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Helper;
import com.odelan.qwork.utils.TabAdapter;
import com.odelan.qwork.widget.HorizontalListView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BarbershopHomeActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.profileTV)
    TextView profileTV;

    @BindView(R.id.locationTV)
    TextView locationTV;

    @BindView(R.id.openTimeTV)
    TextView openTimeTV;

    @BindView(R.id.firstTabLL)
    LinearLayout firstTabLL;

    @BindView(R.id.secondTabLL)
    LinearLayout secondTabLL;

    @BindView(R.id.thirdTabLL)
    LinearLayout thirdTabLL;

    @BindView(R.id.photoIV)
    ImageView photoIV;

    @BindView(R.id.usernameTV)
    TextView usernameTV;

    @BindView(R.id.descriptionTV)
    TextView descriptionTV;

    @BindView(R.id.totalQueueTV)
    TextView totalQueueTV;

    @BindView(R.id.onlineTV)
    TextView onlineTV;

    @BindView(R.id.offlineTV)
    TextView offlineTV;

//    @BindView(R.id.workTimeTV)
//    TextView workTimeTV;

    @BindView(R.id.monTimeTV)
    TextView monTimeTV;

    @BindView(R.id.tueTimeTV)
    TextView tueTimeTV;

    @BindView(R.id.wedTimeTV)
    TextView wedTimeTV;

    @BindView(R.id.thrTimeTV)
    TextView thrTimeTV;

    @BindView(R.id.friTimeTV)
    TextView friTimeTV;

    @BindView(R.id.satTimeTV)
    TextView satTimeTV;

    @BindView(R.id.sunTimeTV)
    TextView sunTimeTV;

    @BindView(R.id.bannerimage)
    RelativeLayout bannerimage;

    @BindView(R.id.socialbtnslayout)
    LinearLayout socialbtnslayout;

    @BindView(R.id.onHScrollview)
    HorizontalListView onHListView;

    @BindView(R.id.offHScrollview)
    HorizontalListView offHListView;

    @BindView(R.id.ratingBar)
    me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;

    @BindView(R.id.ratingTV)
    TextView ratingTV;

    List<User> mBarbers;
    HorizontalListViewAdapter onlineAdapter;
    HorizontalListViewAdapter offlineAdapter;

    public static String mUserId = null;
    public static User mFromUser = null;

    public User mUser;
    Polyline mPolyLine;

    TabAdapter tabAdapter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barbershop_home);

        mContext = this;
        ButterKnife.bind(this);

        showTitleIV();
        hideTitle();

        ArrayList<TextView> mTabs = new ArrayList<>();
        mTabs.add(profileTV);
        mTabs.add(locationTV);
        mTabs.add(openTimeTV);

        ArrayList<ViewGroup> mTabContents = new ArrayList<>();
        mTabContents.add(firstTabLL);
        mTabContents.add(secondTabLL);
        mTabContents.add(thirdTabLL);

        tabAdapter = new TabAdapter(mTabs, mTabContents);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getBarbers(mUserId);
    }

    public void setLayer() {
        if (mUser != null) {
            if (mUser.photo != null && !mUser.photo.equals("")) {
                Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mUser.photo).fit().placeholder(R.drawable.ic_user).into(photoIV);
            }

            if (mUser.username != null && !mUser.username.equals("")) {
                usernameTV.setText(mUser.username);
            }

            if (mUser.desc != null && !mUser.desc.equals("")) {
                descriptionTV.setText(mUser.desc);
            }

            if (mUser.totalCustomerCount != null && !mUser.totalCustomerCount.equals("")) {
                totalQueueTV.setText(mUser.totalCustomerCount);
            }
//
//            if(mUser.work_time_start != null && mUser.work_time_end != null) {
//                workTimeTV.setText(mUser.work_time_start + " - " + mUser.work_time_end);
//            }

            List<String> closeStateList = Arrays.asList(mUser.close_state.split("\\s*,\\s*"));
            for (int i = 1; i < 8; i++) {
                if (i == 1) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        monTimeTV.setText("Closed");
                    } else {
                        monTimeTV.setText("");
                        if (mUser.mon_time_start != "" && mUser.mon_time_end != "") {
                            monTimeTV.setText(mUser.mon_time_start + " - " + mUser.mon_time_end);
                        }
                    }
                } else if (i == 2) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        tueTimeTV.setText("Closed");
                    } else {
                        tueTimeTV.setText("");
                        if (mUser.tue_time_start != "" && mUser.tue_time_end != "") {
                            tueTimeTV.setText(mUser.tue_time_start + " - " + mUser.tue_time_end);
                        }
                    }
                } else if (i == 3) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        wedTimeTV.setText("Closed");
                    } else {
                        wedTimeTV.setText("");
                        if (mUser.wed_time_start != "" && mUser.wed_time_end != "") {
                            wedTimeTV.setText(mUser.wed_time_start + " - " + mUser.wed_time_end);
                        }
                    }
                } else if (i == 4) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        thrTimeTV.setText("Closed");
                    } else {
                        thrTimeTV.setText("");
                        if (mUser.thr_time_start != "" && mUser.thr_time_end != "") {
                            thrTimeTV.setText(mUser.thr_time_start + " - " + mUser.sat_time_end);
                        }

                    }
                } else if (i == 5) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        friTimeTV.setText("Closed");
                    } else {
                        friTimeTV.setText("");
                        if (mUser.fri_time_start != "" && mUser.fri_time_end != "") {
                            friTimeTV.setText(mUser.fri_time_start + " - " + mUser.fri_time_end);
                        }
                    }
                } else if (i == 6) {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        satTimeTV.setText("Closed");
                    } else {
                        satTimeTV.setText("");
                        if (mUser.sat_time_start != "" && mUser.sat_time_end != "") {
                            satTimeTV.setText(mUser.sat_time_start + " - " + mUser.sat_time_end);
                        }
                    }
                } else {
                    if (closeStateList.indexOf(Integer.toString(i + 20)) != -1) {
                        sunTimeTV.setText("Closed");
                    } else {
                        sunTimeTV.setText("");
                        if (mUser.sun_time_start != null && mUser.sun_time_end != null) {
                            sunTimeTV.setText(mUser.sun_time_start + " - " + mUser.sun_time_end);
                        }
                    }
                }
            }

            if (mUser.rating != null) {
                float rating = Float.parseFloat(mUser.rating);
                ratingBar.setRating(rating);
                ratingTV.setText(mUser.rating);
            }

            if (mMap != null) {
                if (MyApplication.g_longitude != 0 && MyApplication.g_latitude != 0
                        && !TextUtils.isEmpty(mUser.lati) && !TextUtils.isEmpty(mUser.lang)) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(MyApplication.g_latitude, MyApplication.g_longitude)));
                    String directionApiPath = Helper.getUrl(this,
                            MyApplication.g_latitude + "", MyApplication.g_longitude + "",
                            mUser.lati, mUser.lang);
                    getDirectionFromDirectionApiServer(directionApiPath);
                }
            }
        }
    }

    private void getDirectionFromDirectionApiServer(String url) {

        AndroidNetworking.get(url)
                .setTag("getFaivoritBarberInfo")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        try {
                            DirectionObject directionObject = new DirectionObject(response);
                            if (directionObject != null || "OK".equals(directionObject.getStatus())) {
                                List<LatLng> mDirections = getDirectionPolylines(directionObject.getRoutes());

                                if (mMap != null) {
                                    drawRouteOnMap(mMap, mDirections);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                    }
                });
    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        options.addAll(positions);

        if (mPolyLine != null) {
            mPolyLine.remove();
        }
        mPolyLine = map.addPolyline(options);
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<LatLng>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {

                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline) {
                        directionList.add(direction);
                    }
                }
            }
        }
        return directionList;
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    @OnClick(R.id.photoIV)
    public void photoClick() {
        ImagesActivity.userId = mUser.userid;
        ImagesActivity.isEditable = false;
        startActivity(new Intent(mContext, ImagesActivity.class));
    }

    @OnClick(R.id.fbIV)
    public void onFacebookClick() {
        if (mUser != null && mUser.fb_profile_link != null && !mUser.fb_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.fb_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no facebook profile.");
        }
    }

    @OnClick(R.id.instagramIV)
    public void onInstagramClick() {
        if (mUser != null && mUser.instagram_profile_link != null && !mUser.instagram_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.instagram_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no instagram profile.");
        }
    }

    @OnClick(R.id.twtIV)
    public void onTwitterClick() {
        if (mUser != null && mUser.twt_profile_link != null && !mUser.twt_profile_link.equals("")) {
            try {
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUser.twt_profile_link));
                startActivity(viewIntent);
            } catch (ActivityNotFoundException e) {
                showToast("Wrong facebook profile link");
            }
        } else {
            showToast("There is no twitter profile.");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUserMap();
    }

    public void setUserMap() {
        if (mUser != null && mMap != null) {
            if (mUser.lati != null && mUser.lang != null) {
                try {
                    double lati = Double.parseDouble(mUser.lati);
                    double lang = Double.parseDouble(mUser.lang);

                    LatLng sydney = new LatLng(lati, lang);
                    if (mUser.address != null) {
                        mMap.addMarker(new MarkerOptions().position(sydney).title(mUser.address));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClick(R.id.profileTV)
    public void onProfileClick() {
        socialbtnslayout.setVisibility(View.VISIBLE);
        bannerimage.setVisibility(View.GONE);
        tabAdapter.onTabClick(profileTV);
    }

    @OnClick(R.id.locationTV)
    public void onLocationClick() {
        tabAdapter.onTabClick(locationTV);
    }

    @OnClick(R.id.openTimeTV)
    public void onOpenTimeClick() {
        tabAdapter.onTabClick(openTimeTV);
    }

    public void getBarbers(String userid) {
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getShopBarbersInOneBarbershop")
                .addBodyParameter("bs_id", userid)
                .setTag("getShopBarbersInOneBarbershop")
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
                                mUser = LoganSquare.parse(response.getString("barbershop"), User.class);
                                setLayer();
                                mBarbers = new ArrayList<>();
                                mBarbers = LoganSquare.parseList(response.getString("barbers"), User.class);
                                setOnlineOfflineBarbers();
                                setUserMap();
                            } else if (status.equals("false")) {
                                showToast("Fail to get barbers");
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

    public void setOnlineOfflineBarbers() {
        List<User> onlineUsers = new ArrayList<>();
        List<User> offlineUsers = new ArrayList<>();

        for (User u : mBarbers) {
            if (u.online_status.equals(MyApplication.STATUS_ONLINE)) {
                onlineUsers.add(u);
            } else {
                offlineUsers.add(u);
            }
        }

        onlineTV.setText("Online(" + onlineUsers.size() + ")");
        offlineTV.setText("Offline(" + offlineUsers.size() + ")");

        onlineAdapter = new HorizontalListViewAdapter(mContext, onlineUsers);
        onHListView.setAdapter(onlineAdapter);

        offlineAdapter = new HorizontalListViewAdapter(mContext, offlineUsers);
        offHListView.setAdapter(offlineAdapter);
    }

    private final class HorizontalListViewAdapter extends BaseAdapter {

        private String TAG = "HorizontalListViewAdapter";
        private List<User> mUsers = null;
        private Context context;

        public HorizontalListViewAdapter(Context con, List<User> users) {
            super();
            context = con;
            mUsers = users;
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final User barber = mUsers.get(position);
            if (v == null) {
                if (barber.online_status.equals(MyApplication.STATUS_ONLINE)) {
                    v = LayoutInflater.from(context).inflate(R.layout.list_item_online, parent, false);
                } else {
                    v = LayoutInflater.from(context).inflate(R.layout.list_item_offline, parent, false);
                }
            }

            if (v != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.photoIV);
                if (barber.photo != null && !barber.photo.equals("")) {
                    Picasso.with(context).load(MyApplication.ASSET_BASE_URL + barber.photo).placeholder(R.drawable.ic_user).into(iv);
                }
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mFromUser.user_type.equals(MyApplication.USER_CUSTOMER)) {
                            BarberHomeActivity.mUser = barber;
                            BarberHomeActivity.mFromUser = mFromUser;
                            startActivity(new Intent(mContext, BarberHomeActivity.class));
                        } else {
                            // from barber case: barbers can not see other barber's info
                        }
                    }
                });
                TextView countTV = (TextView) v.findViewById(R.id.countTV);
                if (barber.cur_count == null) {
                    barber.cur_count = "0";
                }
                countTV.setText(barber.cur_count);

                TextView nameTV = (TextView) v.findViewById(R.id.nameTV);
                if (barber.username != null) {
                    nameTV.setText(barber.username);
                }
                TextView timeTV = (TextView) v.findViewById(R.id.timeTV);
                if (barber.total_turn_time != null && !barber.total_turn_time.isEmpty()) {
                    timeTV.setText(Integer.parseInt(barber.total_turn_time) / 60 + " min");
                }
            }
            return v;
        }

        public void add(User u) {
            mUsers.add(u);
            notifyDataSetChanged();
        }
    }
}
