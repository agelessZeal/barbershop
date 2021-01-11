package com.odelan.qwork.widget.google_place_api;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.widget.google_place_api.bean.AutoCompleteBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoComplete extends BaseActivity implements LocationListener, OnMapReadyCallback {

    private ArrayList<AutoCompleteBean> resultList;
    private ArrayList<Double> locationResult;
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;

    private static final String LOG_TAG = "ExampleApp";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBSRVHjT6G5NiCe6qjQlvuiDEE4PoQludI";

    AutoCompleteTextView autoCompView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);

        mContext = this;

        showTitleIV();
        hideTitle();

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                System.out.println("click");
                System.out.println(resultList.get(position).getDescription());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        locationResult = Details(resultList.get(position).getDescription(), resultList.get(position).getReference());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLocation(locationResult.get(0), locationResult.get(1));
                            }
                        });
                    }
                }).start();
            }
        });

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        fragment.getMapAsync(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLocation(MyApplication.g_latitude, MyApplication.g_longitude);
                        MyApplication.g_address = getAddress(MyApplication.g_latitude, MyApplication.g_longitude);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.leftIV:
                if(locationResult != null) {
                    MyApplication.g_latitude = locationResult.get(0);
                    MyApplication.g_longitude = locationResult.get(1);
                    MyApplication.g_address = getAddress(MyApplication.g_latitude, MyApplication.g_longitude);
                    Log.d("location", "latitude: " + MyApplication.g_latitude + ",\n longitude: " + MyApplication.g_longitude + ",\n address: " + MyApplication.g_address);
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(locationResult != null) {
            MyApplication.g_latitude = locationResult.get(0);
            MyApplication.g_longitude = locationResult.get(1);
            MyApplication.g_address = getAddress(MyApplication.g_latitude, MyApplication.g_longitude);
            Log.d("location", "latitude: " + MyApplication.g_latitude + ",\n longitude: " + MyApplication.g_longitude + ",\n address: " + MyApplication.g_address);
        }
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermissions();
            return;
        }

        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        // long click on map
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MyApplication.g_latitude = latLng.latitude;
                MyApplication.g_longitude = latLng.longitude;
                MyApplication.g_address = getAddress(latLng.latitude, latLng.longitude);
                setLocation(latLng.latitude, latLng.longitude);
                autoCompView.setText(MyApplication.g_address);
            }
        });
    }

    private void requestLocationPermissions () {
        ActivityCompat.requestPermissions((Activity)mContext,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                100);
    }

    public void setLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        MarkerOptions marker = new MarkerOptions().position(latLng);//.snippet("click then selected position");

        marker.title(getAddress(latLng.latitude, latLng.longitude));

        googleMap.clear();
        googleMap.addMarker(marker);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        System.out.println("latitude :::: " + latitude + " longitude :::: " + longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> result;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index).getDescription();
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        result = new ArrayList<String>(resultList.size());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};


            return filter;
        }
    }
    private ArrayList<AutoCompleteBean> autocomplete(String input) {

        ArrayList<AutoCompleteBean> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&sensor=true&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<AutoCompleteBean>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new AutoCompleteBean(predsJsonArray.getJSONObject(i).getString("description"), predsJsonArray.getJSONObject(i).getString("reference")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private ArrayList<Double> Details(String description, String reference ) {

        ArrayList<Double> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
            sb.append("?reference=" + URLEncoder.encode(reference, "utf8"));
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject jsonObjResult = jsonObj.getJSONObject("result");
            JSONObject jsonObjGemmetry = jsonObjResult.getJSONObject("geometry");
            JSONObject jsonObjLocation = jsonObjGemmetry.getJSONObject("location");

            System.out.println("jsonObj.toString() :::: " + jsonObj.toString());
            System.out.println("jsonObjLocation.toString() :::: " + jsonObjLocation.toString());

            resultList = new ArrayList<Double>(2);
            resultList.add(jsonObjLocation.getDouble("lat"));
            resultList.add(jsonObjLocation.getDouble("lng"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public String getAddress(double lat, double lng) {
        String address = "";

        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) {
            Log.d("fail", "fail");
            return null;
        }

        if (list.size() > 0) {
            Address addr = list.get(0);
            Log.d(String.valueOf(addr.getMaxAddressLineIndex()), addr.toString());
            for (int j = 0; j <= addr.getMaxAddressLineIndex(); j++) {
                address = address + addr.getAddressLine(j);
            }

        }

        return address;
    }
}
