package com.odelan.qwork.ui.activity.main.customer;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class C_HistoryActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.listView)
    ListView listView;

    ListAdapter listAdapter;

    List<History> mHistories = new ArrayList<>();

    DateTimeUtils dateTimeUtils;

    int page = 1;
    int limit = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c__history);

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

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 1;
                mHistories = new ArrayList<History>();
                getHistory();
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
        getHistory();
    }

    public void getHistory() {
        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getHistoryWithCustomerId")
                .addBodyParameter("customer_id", me.userid)
                .addBodyParameter("limit", String.valueOf(limit))
                .addBodyParameter("page", String.valueOf(page))
                .setTag("getHistoryWithCustomerId")
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
                            if(status.equals("1")) {
                                if(page == 1) {
                                    mHistories = new ArrayList<History>();
                                    mHistories = LoganSquare.parseList(response.getString("data"), History.class);
                                    listAdapter = new C_HistoryActivity.ListAdapter(mContext, mHistories);
                                    listView.setAdapter(listAdapter);
                                } else {
                                    List<History> histories = new ArrayList<>();
                                    histories = LoganSquare.parseList(response.getString("data"), History.class);

                                    mHistories.addAll(histories);
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
            startActivity(new Intent(mContext, C_HomeActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, C_ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_history) {

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

    public class ListAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater = null;
        List<History> histories;

        public ListAdapter(Context con, List<History> hs) {
            context = con;
            histories = hs;

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

        public class ViewHolder {
            View mView;
            TextView nameTV;
            TextView timeTV;
            TextView dateTV;
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
            if(item.barber.username != null) {
                holder.nameTV.setText(item.barber.username);
            }

            String date = item.order.created_on;
            if(date != null) {
                holder.dateTV.setText(dateTimeUtils.convertFullDateStringToDateStringHistory(date));
            }

            try {
                int endtime = Integer.parseInt(item.order.end_time);
                int starttime = Integer.parseInt(item.order.start_time);
                holder.timeTV.setText((endtime - starttime) / 60 + "min");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(item.barber.photo != null && !item.barber.photo.equals("")) {
                Picasso.with(context).load(MyApplication.ASSET_BASE_URL + item.barber.photo).into(holder.photoIV);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    C_HistoryDetailActivity.mHistory = item;
                    startActivity(new Intent(context, C_HistoryDetailActivity.class));
                }
            });

            if(index == page*limit-1) {
                page++;
                getHistory();
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
            startActivity(new Intent(mContext, C_HomeActivity.class));
        }
    }
}
