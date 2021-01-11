package com.odelan.qwork.ui.activity.main.customer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.android.gms.maps.model.Marker;
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
import com.odelan.qwork.ui.activity.main.BarberHomeActivity;
import com.odelan.qwork.ui.activity.main.BarbershopHomeActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class C_HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, TextWatcher
        /*GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener*/{

    final int REQUEST_PERMISSION = 999;
    static final boolean GRID_LAYOUT = false;
    private List<User> mBarbershops = new ArrayList<>();
    private List<User> mSearchResults = new ArrayList<>();
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;

    @BindView(R.id.searchET) EditText searchET;
    @BindView(R.id.joinStatusLL) RelativeLayout joinStatusLL;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.photoIV) CircleImageView photoIV;
    @BindView(R.id.orderTV) TextView orderTV;
    @BindView(R.id.timeTV) TextView timeTV;
    @BindView(R.id.barberNameTV) TextView barberNameTV;
    @BindView(R.id.curQueTV) TextView curQueTV;
    @BindView(R.id.tabView) RelativeLayout tabView;
    @BindView(R.id.btn) Button btn;
    @BindView(R.id.tabTitleTV) TextView tabTitleTV;
    @BindView(R.id.tvDistance) TextView tvDistance;
    @BindView(R.id.tvCartime) TextView tvCartime;
    @BindView(R.id.tvBicycletime) TextView tvBicycletime;
    @BindView(R.id.tvWalktime) TextView tvWalkTime;

    public User mBarber;
    public User mFavUser;

    Marker myMarker;
    Marker myFavMarker;
    Marker myBaberMarker;
    Polyline mPolyLine;


    private HashMap<Marker, User> hashMaker;
    private HashMap<String, Marker> hashUserMaker;

    private int isJoined = 0;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c__home);

        mContext = this;
        ButterKnife.bind(this);

        hashMaker = new HashMap<Marker, User>();
        hashUserMaker = new HashMap<>();

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

        searchET.addTextChangedListener(this);

        RecyclerView.LayoutManager layoutManager;

        if (GRID_LAYOUT) {
            layoutManager = new GridLayoutManager(mContext, 2);
        } else {
            layoutManager = new LinearLayoutManager(mContext);
        }
        recyclerView.setLayoutManager(layoutManager);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        fragment.getMapAsync(this);

//        if(MyApplication.isReviewNotification) {
//            MyApplication.isReviewNotification = false;
//
//            showReviewDlg(MyApplication.noti_username, MyApplication.noti_photo, MyApplication.noti_orderid);
//        }

        //getAllBarbershops();

        readyGPS();
        /** Check Permission **/

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

        startTimer();
    }

    private void startTimer(){
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 100, 60000); //
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            User me = getUser(getValueFromKey("me"));
                            String barberId, customerId = me.userid;
                            if (me.faivorit_barber_id != null && me.faivorit_barber_id != "")
                                barberId = me.faivorit_barber_id;
                            else
                                return;

                            if (barberId.equals("") || customerId.equals("")) return;

                            AndroidNetworking.post(MyApplication.BASE_URL + "order/getWaitingTime")
                                    .addBodyParameter("barber_id", barberId)
                                    .addBodyParameter("customer_id", customerId)
                                    .setTag("getWaitingTime")
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            // do anything with response
                                            try {
                                                String status = response.getString("status");
                                                if (status.equals("1")) {
                                                    String nowWaitingTime = response.getString("now_waiting_time");
                                                    timeTV.setText(nowWaitingTime);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError anError) {

                                        }
                                    });
                        } catch (java.lang.NullPointerException e) {

                        }
                    }
                });
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mBarbershops == null || mBarbershops.size() < 1) {
            User me = getUser(getValueFromKey("me"));
            getUserAndFavBarberWithId(me.userid);
            startTimer();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermissions();
            return;
        }


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        try {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                onLocationChanged(location);
            }
        } catch (Exception e){}
        readyGPS();

//        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                User user = hashMaker.get(marker);
                User me = getUser(getValueFromKey("me"));
                if(!user.userid.equals(me.userid)) {
                    BarbershopHomeActivity.mUserId = user.userid;
                    BarbershopHomeActivity.mFromUser = me;
                    startActivity(new Intent(mContext, BarbershopHomeActivity.class));
                }
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(final Marker marker) {
                User user = hashMaker.get(marker);
                User me = getUser(getValueFromKey("me"));

                if(!user.userid.equals(me.userid)) {
                    View v = getLayoutInflater().inflate(
                            R.layout.map_pin_info, null);

                    TextView nameTV = (TextView) v
                            .findViewById(R.id.nameTV);

                    TextView addressTV = (TextView) v
                            .findViewById(R.id.addressTV);

                    ImageView photoIV = (ImageView) v
                            .findViewById(R.id.photoIV);

                    if(user.photo != null && !user.photo.equals("")) {
                        Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + user.photo).placeholder(R.drawable.back_thumb).into(photoIV, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                                if (marker != null && marker.isInfoWindowShown()) {
                                    marker.hideInfoWindow();
                                    marker.showInfoWindow();
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }

                    if(user.username != null) {
                        nameTV.setText(user.username);
                    }
                    if(user.address != null) {
                        addressTV.setText(user.address);
                    }
                    return v;
                } else {
                    View v = getLayoutInflater().inflate(
                            R.layout.map_pin_info_me, null);

                    TextView nameTV = (TextView) v
                            .findViewById(R.id.nameTV);

                    if(user.username != null) {
                        nameTV.setText(user.username);
                    }

                    ImageView photoIV = (ImageView) v
                            .findViewById(R.id.photoIV);

                    if(user.photo != null && !user.photo.equals("")) {
                        Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + user.photo).placeholder(R.drawable.ic_user).into(photoIV, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                                if (marker != null && marker.isInfoWindowShown()) {
                                    marker.hideInfoWindow();
                                    marker.showInfoWindow();
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                    return v;
                }
            }
        });
        try {

        } catch (Exception e){}




    }



    private void requestLocationPermissions () {
        ActivityCompat.requestPermissions((Activity)mContext,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                100);
    }

    boolean isFirst = true;
    public void setLocation(double latitude, double longitude, String address, User user, boolean isMe){
        this.latitude = latitude;
        this.longitude = longitude;
        LatLng latLng = new LatLng(latitude, longitude);
        if(isMe) {

            if(MyApplication.g_latitude == 0 || MyApplication.g_latitude == 0) return;
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            if(isFirst) {

                isFirst = false;
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }

        MarkerOptions markerOpt = new MarkerOptions().position(latLng);//.snippet("click then selected position");

        markerOpt.title(address);

        Marker marker = googleMap.addMarker(markerOpt);
        hashMaker.put(marker, user);
        hashUserMaker.put(user.userid, marker);

        if(mFavUser != null) {
            if(user.userid.equals(mFavUser.barbershop_id)){
                myFavMarker = marker;
            }
        }

        if(isMe){
            if(MyApplication.g_latitude != 0 && MyApplication.g_longitude != 0)
            myMarker = marker;
            myMarker.setPosition(new LatLng(MyApplication.g_latitude, MyApplication.g_longitude));
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(latitude == 0) {
            latitude = MyApplication.g_latitude;
        } else if(MyApplication.g_latitude == 0){
            MyApplication.g_latitude = latitude;
        }
        if(longitude == 0) {
            longitude = MyApplication.g_longitude;
        } else if(MyApplication.g_longitude == 0){
            MyApplication.g_longitude = longitude;
        }
        LatLng latLng = new LatLng(latitude, longitude);

        if(myMarker != null){
            myMarker.setPosition(latLng);
        }

        System.out.println("latitude :::: " + latitude + " longitude :::: " + longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

//        showRoad();
    }

    private void showRoad(Marker targetMarker) {
        if(myMarker == null || targetMarker == null) return;

        String directionApiPath = Helper.getUrl(this,
                MyApplication.g_latitude + "", MyApplication.g_longitude +"",
                targetMarker.getPosition().latitude + "" , targetMarker.getPosition().longitude+"");
        getDirectionFromDirectionApiServer(directionApiPath);
    }

    private void getDirectionFromDirectionApiServer(String url){

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
                            if(directionObject != null || "OK".equals(directionObject.getStatus())){
                                List<LatLng> mDirections = getDirectionPolylines(directionObject.getRoutes());

                                if(googleMap != null) {
                                    drawRouteOnMap(googleMap, mDirections);
                                    showDistanceAndTime(directionObject.getRoutes());
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

    private void showDistanceAndTime(List<RouteObject> routes) {
        String distance = "-";
        String bikeTime = "-";
        String walkTime = "-";
        String carTime = "-";
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){
                distance = leg.getDistance().getText();

                walkTime = leg.getDistance().getWalkTime();
                bikeTime = leg.getDistance().getBicycleTime();
                carTime = leg.getDuration().getText();
                break;
            }
        }

        tvDistance.setText(distance);

        tvCartime.setText(carTime);
        tvBicycletime.setText(bikeTime);
        tvWalkTime.setText(walkTime);


    }

    private void drawRouteOnMap(GoogleMap map, List<LatLng> positions){
        PolylineOptions options = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        options.addAll(positions);

        if(mPolyLine != null) {
            mPolyLine.remove();
        }
        mPolyLine = map.addPolyline(options);
    }

    private List<LatLng> getDirectionPolylines(List<RouteObject> routes){
        List<LatLng> directionList = new ArrayList<LatLng>();
        for(RouteObject route : routes){
            List<LegsObject> legs = route.getLegs();
            for(LegsObject leg : legs){

                List<StepsObject> steps = leg.getSteps();
                for(StepsObject step : steps){
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    for (LatLng direction : singlePolyline){
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



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!searchET.getText().toString().equals("")) {
            AndroidNetworking.forceCancel("searchBarber_ShopWithName");
            onSearch(charSequence.toString());
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            AndroidNetworking.forceCancel("searchBarber_ShopWithName");
            mSearchResults = new ArrayList<>();
            recyclerView.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<User> mUsers;
        Context context;

        static final int TYPE_BARBER = 0;
        static final int TYPE_BARBER_SHOP = 1;

        public RecyclerViewAdapter(Context con, List<User> users) {
            context = con;
            mUsers = users;
        }

        @Override
        public int getItemViewType(int position) {
            User user = mUsers.get(position);
            if(user.user_type.equals(MyApplication.USER_BARBER)) {
                return TYPE_BARBER;
            } else {
                return TYPE_BARBER_SHOP;
            }
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            switch (viewType) {
                case TYPE_BARBER: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_barber, parent, false);
                    return new BarberViewHolder(view);
                }
                case TYPE_BARBER_SHOP: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_barbershop, parent, false);
                    return new BarbershopViewHolder(view);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            switch (getItemViewType(position)) {
                case TYPE_BARBER:
                    BarberViewHolder bholder = (BarberViewHolder) holder;
                    bholder.mUser = mUsers.get(position);

                    if(bholder.mUser.photo != null && !bholder.mUser.photo.equals("")) {
                        Picasso.with(context).load(MyApplication.ASSET_BASE_URL+bholder.mUser.photo).placeholder(R.drawable.ic_user).into(bholder.photoIV);
                    }

                    if(bholder.mUser.username != null && !bholder.mUser.username.equals("")) {
                        bholder.nameTV.setText(bholder.mUser.username);
                    }

                    if(bholder.mUser.phone != null && !bholder.mUser.phone.equals("")) {
                        bholder.phoneTV.setText(bholder.mUser.phone);
                    }

                    if(bholder.mUser.rating != null && !bholder.mUser.rating.equals("")) {
                        bholder.ratingTV.setText(bholder.mUser.rating);
                    }

                    if(bholder.mUser.barbershop_name != null && !bholder.mUser.barbershop_name.equals("")) {
                        bholder.belongTV.setText("Belongs To: " + bholder.mUser.barbershop_name);
                    }

                    bholder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            User me = getUser(getValueFromKey("me"));
                            BarberHomeActivity.mFromUser = me;
                            BarberHomeActivity.mUser = mUsers.get(position);
                            startActivity(new Intent(mContext, BarberHomeActivity.class));
                        }
                    });
                    break;
                case TYPE_BARBER_SHOP:
                    final BarbershopViewHolder bsholder = (BarbershopViewHolder) holder;
                    bsholder.mUser = mUsers.get(position);

                    if(bsholder.mUser.photo != null && !bsholder.mUser.photo.equals("")) {
                        Picasso.with(context).load(MyApplication.ASSET_BASE_URL + bsholder.mUser.photo).placeholder(R.drawable.ic_user).into(bsholder.photoIV);
                    }

                    if(bsholder.mUser.username != null && !bsholder.mUser.username.equals("")) {
                        bsholder.nameTV.setText(bsholder.mUser.username);
                    }


                    ViewTreeObserver vto = bsholder.openingTimeRV.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            int wid = bsholder.openingTimeRV.getWidth() / 3;
                            int hei = bsholder.openingTimeRV.getHeight();

                            WeekAdapter adapter = new WeekAdapter(C_HomeActivity.this, bsholder.mUser, wid, hei);

                            if (bsholder.openingTimeRV.getViewTreeObserver().isAlive()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    bsholder.openingTimeRV.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    bsholder.openingTimeRV.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }

                            bsholder.openingTimeRV.setAdapter(adapter);
                        }
                    });
//                    int wid = bsholder.openingTimeSV.getMeasuredWidth() / 3, hei = bsholder.openingTimeSV.getMeasuredHeight();

//                    if(bsholder.mUser.work_time_start != null && !bsholder.mUser.work_time_start.equals("") && bsholder.mUser.sat_time_start != null && !bsholder.mUser.sat_time_start.equals("") && bsholder.mUser.sun_time_start != null && !bsholder.mUser.sun_time_start.equals("")) {
//                        bsholder.workTimeTV.setText(bsholder.mUser.work_time_start + " - " + bsholder.mUser.work_time_end);
//                        bsholder.satTimeTV.setText(bsholder.mUser.sat_time_start + " - " + bsholder.mUser.sat_time_end);
//                        bsholder.sunTimeTV.setText(bsholder.mUser.sun_time_start + " - " + bsholder.mUser.sun_time_end);
//                    }

                    bsholder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            User me = getUser(getValueFromKey("me"));
                            BarbershopHomeActivity.mUserId = bsholder.mUser.userid;
                            BarbershopHomeActivity.mFromUser = me;
                            startActivity(new Intent(mContext, BarbershopHomeActivity.class));
                        }
                    });
                    break;
            }
        }

        public class BarberViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final de.hdodenhof.circleimageview.CircleImageView photoIV;
            public final TextView nameTV;
            public final TextView phoneTV;
            public final TextView belongTV;
            public final TextView ratingTV;

            public User mUser;

            public BarberViewHolder(View view) {
                super(view);
                mView = view;
                nameTV = (TextView) view.findViewById(R.id.nameTV);
                photoIV = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.photoIV);
                phoneTV = (TextView) view.findViewById(R.id.phoneTV);
                belongTV = (TextView) view.findViewById(R.id.belongToTV);
                ratingTV = (TextView) view.findViewById(R.id.ratingTV);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + nameTV.getText() + "'";
            }
        }

        public class BarbershopViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView nameTV;
            public final ImageView photoIV;
            public final RecyclerView openingTimeRV;
//            public final TextView workTimeTV;
//            public final TextView satTimeTV;
//            public final TextView sunTimeTV;

            public User mUser;

            public BarbershopViewHolder(View view) {
                super(view);
                mView = view;
                nameTV = (TextView) view.findViewById(R.id.nameTV);
                photoIV = (ImageView) view.findViewById(R.id.photoIV);
                openingTimeRV = (RecyclerView) view.findViewById(R.id.openingTimeRV);
//                workTimeTV = (TextView) view.findViewById(R.id.workTimeTV);
//                satTimeTV = (TextView) view.findViewById(R.id.satTimeTV);
//                sunTimeTV = (TextView) view.findViewById(R.id.sunTimeTV);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + nameTV.getText() + "'";
            }
        }
    }

    private void getFavoriteBarber() {
        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();

        AndroidNetworking.post(MyApplication.BASE_URL + "user/getFaivoritBarberInfo")
                .addBodyParameter("fid", me.faivorit_barber_id)
                .setTag("getFaivoritBarberInfo")
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
                                mFavUser = LoganSquare.parse(response.getString("barber"), User.class);
                                int count = response.getInt("count");
                                int avg = response.getInt("average_cutting_time");
                                int nowCuttingTime = response.getInt("now_cutting_time");

                                int wtime = 0;
                                if (count > 0) {
                                    wtime = (count - 1) * avg + (avg - nowCuttingTime);
                                    if (wtime < 0) wtime = 0;
                                }

                                int h = 0;
                                int min = 0;
                                String waitTime = "";

                                h = wtime / 3600;
                                min = (wtime - h * 3600) / 60;

                                if (h == 0) {
                                    waitTime = min + "min";
                                } else {
                                    waitTime = h + "h " + min + "min";
                                }

                                orderTV.setText(String.valueOf(count));
                                timeTV.setText(String.valueOf(waitTime));
                                barberNameTV.setText(mFavUser.username);

                                if (mFavUser.photo != null && !mFavUser.photo.equals("")) {
                                    Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mFavUser.photo).placeholder(R.drawable.ic_user).into(photoIV);
                                    photoIV.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            User me = getUser(getValueFromKey("me"));
                                            BarberHomeActivity.mFromUser = me;
                                            BarberHomeActivity.mUser = mFavUser;
                                            startActivity(new Intent(mContext, BarberHomeActivity.class));
                                        }
                                    });
                                }

                                joinStatusLL.setVisibility(View.VISIBLE);
                                curQueTV.setVisibility(View.VISIBLE);
                                tabView.setBackgroundResource(R.drawable.bg_favourite);
                                btn.setText("Quick Join");
                                tabTitleTV.setText("Favourite Barber");

                                Marker marker = hashUserMaker.get(mFavUser.barbershop_id);
                                if(marker != null){
                                    showRoad(marker);
                                }
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

    private void getAllBarbershops() {

        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getAllBarbershopsAndIsJoinedWithCustomerId")
                .addBodyParameter("customer_id", me.userid)
                .setTag("getAllBarbershopsAndIsJoinedWithCustomerId")
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
                                mBarbershops = new ArrayList<User>();
                                mBarbershops = LoganSquare.parseList(response.getString("barbershops"), User.class);

                                googleMap.clear();
                                hashMaker = new HashMap<Marker, User>();
                                for(User bs : mBarbershops) {
                                    if(bs.lati != null && !bs.lati.equals("") && bs.lang != null && !bs.lang.equals("")) {
                                        try {
                                            double lat = Double.parseDouble(bs.lati);
                                            double lan = Double.parseDouble(bs.lang);
                                            setLocation(lat, lan, bs.address, bs, false);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                User me = getUser(getValueFromKey("me"));
                                setLocation(MyApplication.g_latitude, MyApplication.g_longitude, me.username, me, true);

                                String xth = response.getString("xth");
                                if(xth == null || xth.equals("") || xth.equals("0")) {
                                    // not joined

                                    isJoined = 0;
                                    if (me.faivorit_barber_id.equals("")) {
                                        // there is no favourite barber
                                        joinStatusLL.setVisibility(View.GONE);
                                        curQueTV.setVisibility(View.GONE);
                                        tabView.setBackgroundResource(R.drawable.bg_queue);
                                    } else {
                                        // there is a favourite barber
                                        getFavoriteBarber();
                                    }
                                } else {
                                    // joined
                                    isJoined = 1;
                                    joinStatusLL.setVisibility(View.VISIBLE);
                                    curQueTV.setVisibility(View.GONE);
                                    tabView.setBackgroundResource(R.drawable.bg_queue);
                                    mBarber = LoganSquare.parse(response.getString("barber"), User.class);
                                    btn.setText("Leave Queue");
                                    tabTitleTV.setText("You are Queuing");
                                    String order = "";
                                    if(xth.equals("1")) {
                                        order = "1st";
                                    } else if (xth.equals("2")) {
                                        order = "2nd";
                                    } else if (xth.equals("3")) {
                                        order = "3rd";
                                    } else {
                                        order = xth + "th";
                                    }
                                    orderTV.setText(order);
                                    int waitTime = response.getInt("minute");
                                    if (waitTime < 0) waitTime = 0;
                                    int h = 0;
                                    int m = waitTime;
                                    if(waitTime > 59) {
                                        h = waitTime / 60;
                                        m = waitTime % 60;
                                        timeTV.setText(h + "h " + m + "min");
                                    } else {
                                        timeTV.setText(waitTime + "min");
                                    }
                                    if(mBarber.photo != null && !mBarber.photo.equals("")) {
                                        Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mBarber.photo).placeholder(R.drawable.ic_user).into(photoIV);
                                        photoIV.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                User me = getUser(getValueFromKey("me"));
                                                BarberHomeActivity.mFromUser = me;
                                                BarberHomeActivity.mUser = mBarber;
                                                startActivity(new Intent(mContext, BarberHomeActivity.class));
                                            }
                                        });
                                    }
                                    barberNameTV.setText(mBarber.username);

                                    Marker marker = hashUserMaker.get(mBarber.barbershop_id);
                                    if(marker != null){
                                        showRoad(marker);
                                    }
                                }

//                                showRoad();
                            } else if (status.equals("false")) {
                                showToast("Fail");
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



    private void onSearch(String input) {

        AndroidNetworking.post(MyApplication.BASE_URL + "user/searchBarber_ShopWithName")
                .addBodyParameter("username", input)
                .setTag("searchBarber_ShopWithName")
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
                                mSearchResults = new ArrayList<User>();
                                mSearchResults = LoganSquare.parseList(response.getString("data"), User.class);
                                recyclerView.setAdapter(new RecyclerViewAdapter(mContext, mSearchResults));
                            } else if (status.equals("false")) {
                                //showToast("Fail to get barbers");
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

    @OnClick(R.id.btn) public void onLeaveQueue() {
        leave();
    }

    private void leave() {
        User me = getUser(getValueFromKey("me"));

        if (isJoined == 1) {
            new MaterialDialog.Builder(this)
                    .title("Warning")
                    .content("You won’t be able to join a queue again today if you leave this queue after 2 minutes. Are you sure you want to leave queue?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                            joinOrLeave(mBarber.userid);
                        }
                    })
                    .show();
        } else {
            if (mFavUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                showSecondaryCustomLoadingView();
                AndroidNetworking.post(MyApplication.BASE_URL + "order/joinLeaveQueue")
                        .addBodyParameter("barber_id", mFavUser.userid)
                        .addBodyParameter("customer_id", me.userid)
                        .setTag("joinLeaveQueue")
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
                                        isJoined = response.getInt("isJoined");
                                        getAllBarbershops();
                                    } else if (status.equals("0")) {
                                        showToast(response.getString("error"));
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
            } else {
                showToast("Queue is closed.");
            }
        }

        /*if (me.faivorit_barber_id == null || me.faivorit_barber_id.equals("")) {

            new MaterialDialog.Builder(this)
                    .title("Warning")
                    .content("Are you sure you want to leave queue?")
                    .positiveText("Yes")
                    .negativeText("No")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            // TODO
                            joinOrLeave(mBarber.userid);
                        }
                    })
                    .show();
        } else {
            if (mFavUser.online_status.equals(MyApplication.STATUS_ONLINE)) {
                showSecondaryCustomLoadingView();
                AndroidNetworking.post(MyApplication.BASE_URL + "order/joinLeaveQueue")
                        .addBodyParameter("barber_id", mFavUser.userid)
                        .addBodyParameter("customer_id", me.userid)
                        .setTag("joinLeaveQueue")
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
                                        isJoined = response.getInt("isJoined");
                                        getAllBarbershops();
                                    } else if (status.equals("0")) {
                                        showToast(response.getString("error"));
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
            } else {
                showToast("Queue is closed.");
            }
        }*/
    }

    public void joinOrLeave(String barberid) {
        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/joinLeaveQueue")
                .addBodyParameter("barber_id", barberid)
                .addBodyParameter("customer_id", me.userid)
                .setTag("joinLeaveQueue")
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
                                User me = getUser(getValueFromKey("me"));
                                getUserAndFavBarberWithId(me.userid);
                                isJoined = response.getInt("isJoined");
                            } else if (status.equals("0")) {
                                showToast(response.getString("error"));
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

    private void getUserAndFavBarberWithId(String userid) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getCustomerAndFavouriteBarverWithCustomerId")
                .addBodyParameter("user_id", userid)
                .setTag("getCustomerAndFavouriteBarverWithCustomerId")
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
                                User me = LoganSquare.parse(response.getString("user"), User.class);

                                if(me == null || TextUtils.isEmpty(me.userid)){
                                    gotoLogin();
                                    return;
                                }

                                if (response.getString("favBarber") != null && !response.getString("favBarber").equals("")) {
                                    mFavUser = LoganSquare.parse(response.getString("favBarber"), User.class);
                                }
                                saveKeyValue("me", response.getString("user"));
                                getAllBarbershops();
                            } else if (status.equals("0")) {
                                showToast(response.getString("error"));
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

    @OnClick(R.id.refreshIV) public void onRefresh() {
        refresh();
    }

    public void refresh() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                User me = getUser(getValueFromKey("me"));
                getUserAndFavBarberWithId(me.userid);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, C_ProfileActivity.class));
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
        else if (id == R.id.nav_tutorial) {
            Intent intent = new Intent(mContext, TutorialActivity.class);
            intent.putExtra("launch_type",TutorialActivity.LAUNCH_HOME);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ////////////////
}
