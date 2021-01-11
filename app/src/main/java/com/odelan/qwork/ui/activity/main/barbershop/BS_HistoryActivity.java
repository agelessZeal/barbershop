package com.odelan.qwork.ui.activity.main.barbershop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.History;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BS_HistoryActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.listView)
    ListView listView;

    ListAdapter listAdapter;

    List<History> mHistories = new ArrayList<>();

    DateTimeUtils dateTimeUtils;

    int page = 1;
    int limit = 50;
    private ArrayAdapter barberAdapter;
    private List<User> mBarbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs__history);

        mContext = this;
        ButterKnife.bind(this);
        dateTimeUtils = new DateTimeUtils();

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

        Spinner spBarbers = (Spinner) findViewById(R.id.sp_barbers);
        spBarbers.setVisibility(View.VISIBLE);
        barberAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList());
        spBarbers.setAdapter(barberAdapter);
        spBarbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listAdapter.clear();
                final String itemAtPosition = (String) adapterView.getItemAtPosition(i);
                if (itemAtPosition.equalsIgnoreCase("All")) {
                    for (User user : mBarbers) getHistoryByBarberId(user.userid);
                } else {
                    for (User user : mBarbers) {
                        if (user.username.equalsIgnoreCase(itemAtPosition)) {
                            getHistoryByBarberId(user.userid);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listAdapter = new BS_HistoryActivity.ListAdapter(mContext);
        listView.setAdapter(listAdapter);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 1;
                mHistories = new ArrayList<History>();
                getBarbers();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBarbers();
    }

    public void getBarbers() {
        listAdapter.clear();

        User me = getUser(getValueFromKey("me"));
        showCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getBarbersInOneBarbershop")
                .addBodyParameter("bs_id", me.userid)
                .setTag("getBarbersInOneBarbershop")
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
                                mBarbers = LoganSquare.parseList(response.getString("data"), User.class);
                                barberAdapter.clear();
                                barberAdapter.add("All");
                                for (User user : mBarbers) {
                                    barberAdapter.add(user.username);
                                    getHistoryByBarberId(user.userid);
                                }
                                barberAdapter.notifyDataSetChanged();
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

    public void getHistoryByBarberId(String barberId) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getBarberCuttingHistory")
                .addBodyParameter("barber_id", barberId)
                .addBodyParameter("limit", String.valueOf(limit))
                .addBodyParameter("page", String.valueOf(page))
                .setTag("getHistoryWithBarberId")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if (status.equals("1")) {
                                if (page == 1) {
                                    mHistories = new ArrayList<>();
                                    mHistories = LoganSquare.parseList(response.getString("data"), History.class);
                                    listAdapter.addAll(mHistories);
                                    listAdapter.notifyDataSetChanged();
                                } else {
                                    List<History> histories = new ArrayList<>();
                                    histories = LoganSquare.parseList(response.getString("data"), History.class);
                                    listAdapter.addAll(histories);
                                    listAdapter.notifyDataSetChanged();
                                }
                            } else if (status.equals("0")) {
                                showToast("Fail");
                            } else {
                                showToast("Network Error!");
                            }
                        } catch (Exception e) {
                            // handle error
                            if (swipeContainer.isRefreshing()) {
                                swipeContainer.setRefreshing(false);
                            }
                            e.printStackTrace();
                            showToast("Network Error!");
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        hideCustomLoadingView();
                        showToast(error.getErrorBody().toString());
                    }
                });
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
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, BS_NotificationActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
//            startActivity(new Intent(mContext, BS_NotificationActivity.class));
//            finish();
        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_logout) {
            logout();
        }else if (id == R.id.nav_customer_signed) {
            startActivity(new Intent(mContext, BS_CustomerSignedUp.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ListAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater = null;
        List<History> histories;

        public ListAdapter(Context con) {
            context = con;
            histories = new ArrayList<>();

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return histories.size();
        }

        public Object getItem(int position) {
            return histories.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public void addAll(List<History> mHistories) {
            histories.addAll(mHistories);
        }

        public void clear() {
            histories.clear();
            notifyDataSetChanged();
        }

        public class ViewHolder {
            View mView;
            TextView nameTV;
            TextView timeTV;
            TextView dateTV, tv_cut_start;
            CircleImageView photoIV;

            public ViewHolder(View v) {
                mView = v;
                nameTV = (TextView) v.findViewById(R.id.nameTV);
                timeTV = (TextView) v.findViewById(R.id.timeTV);
                dateTV = (TextView) v.findViewById(R.id.dateTV);
                photoIV = (CircleImageView) v.findViewById(R.id.photoIV);
            }
        }

        @Override
        public View getView(final int index, final View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;

            View vi = convertView;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.list_item_history, null);
                holder = new ViewHolder(vi);

                vi.setTag(holder);
            } else
                holder = (ViewHolder) vi.getTag();

            final History item = (History) getItem(index);
            if (item.customer.username != null) {
                holder.nameTV.setText(item.customer.username);
            }

//            if (item.order.start_time !=null) {
//                holder.tv_cut_start.setText(dateTimeUtils.getDate(Long.parseLong(item.order.start_time), "hh:mm a"));
//            }

            String date = item.order.created_on;
            if (date != null) {
                holder.dateTV.setText(dateTimeUtils.convertFullDateStringToDateStringHistory(date));
            }

            try {
                int endtime = Integer.parseInt(item.order.end_time);
                int starttime = Integer.parseInt(item.order.start_time);
                holder.timeTV.setText((endtime - starttime) / 60 + "min");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (item.customer.photo != null && !item.customer.photo.equals("")) {
                Picasso.with(context).load(MyApplication.ASSET_BASE_URL + item.customer.photo).into(holder.photoIV);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    C_HistoryDetailActivity.mHistory = item;
//                    startActivity(new Intent(context, C_HistoryDetailActivity.class));
                }
            });

            if (index == page * limit - 1) {
                page++;
//                getBarbers();
            }
            return vi;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(mContext, BS_HomeActivity.class));
        }
    }
}
