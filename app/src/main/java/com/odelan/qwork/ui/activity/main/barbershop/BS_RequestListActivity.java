package com.odelan.qwork.ui.activity.main.barbershop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.RequestItem;
import com.odelan.qwork.data.model.User;
import com.odelan.qwork.ui.activity.main.UploadImageActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MJC_COM on 2018-01-12.
 */

public class BS_RequestListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.listView)
    ListView listView;

    List<RequestItem> mRequestList = new ArrayList<>();

    int page = 1;
    int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs_request_list);

        mContext = this;
        ButterKnife.bind(this);

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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().removeCallbacks(cancelRefresh);
                    }
                }, 500);

                page = 1;
                getRequestList();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if(firstVisibleItem + visibleItemCount + 3 > totalItemCount && totalItemCount>0){
//                    swipeContainer.setRefreshing(false);
//                    page++;
//                    getRequestList();
//                }
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();

        getRequestList();
    }

    private Runnable cancelRefresh = new Runnable() {
        @Override
        public void run() {
            if (listView!= null) {
                swipeContainer.setRefreshing(false);
            }
        }
    };
    public void getRequestList() {
        User me = getUser(getValueFromKey("me"));
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/getRequestItem")
                .addBodyParameter("userid", me.userid)
                .addBodyParameter("page", Integer.toString(page))
                .addBodyParameter("limit", Integer.toString(limit))
                .setTag("getRequestItem")
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
                                mRequestList = new ArrayList<RequestItem>();
                                mRequestList = LoganSquare.parseList(response.getString("data"), RequestItem.class);
                                listView.setAdapter(new com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity.ListAdapter(mContext, mRequestList));
                            } else if (status.equals("0")) {
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
        } else if (id == R.id.nav_upload) {
            startActivity(new Intent(mContext, UploadImageActivity.class));
            finish();
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(mContext, BS_NotificationActivity.class));
            finish();
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(mContext, BS_HistoryActivity.class));
            finish();
        }else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(mContext, BS_ContactActivity.class));
            finish();
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
        List<RequestItem> requestItemList;

        public ListAdapter(Context con, List<RequestItem> rt) {
            context = con;
            requestItemList = rt;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return requestItemList.size();
        }

        public Object getItem(int position) {
            return requestItemList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            View mView;
            TextView nameTV;
            CircleImageView photoIV;
            Button allowBtn;
            Button denyBtn;

            public ViewHolder(View v) {
                mView = v;
                nameTV = (TextView) v.findViewById(R.id.nameTV);
                photoIV = (CircleImageView) v.findViewById(R.id.photoIV);
                allowBtn = (Button) v.findViewById(R.id.allowBtn);
                denyBtn = (Button) v.findViewById(R.id.denyBtn);
            }
        }

        @Override
        public View getView(final int index, final View convertView, ViewGroup viewGroup) {
            final com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity.ListAdapter.ViewHolder holder;

            View vi = convertView;

            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_request_item, null);
                holder = new com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity.ListAdapter.ViewHolder(vi);

                vi.setTag(holder);
            } else
                holder = (com.odelan.qwork.ui.activity.main.barbershop.BS_RequestListActivity.ListAdapter.ViewHolder) vi.getTag();

            final RequestItem item = (RequestItem) getItem(index);
            if(item.userName!= null) {
                holder.nameTV.setText(item.userName);
            }

            if(item.userPhoto != null) {
                Picasso.with(context).load(MyApplication.ASSET_BASE_URL + item.userPhoto).into(holder.photoIV);
            }

            if (item.state.equals("0")) {
                holder.allowBtn.setVisibility(View.VISIBLE);
                holder.denyBtn.setVisibility(View.VISIBLE);
            } else if (item.state.equals("1")) {
                holder.allowBtn.setVisibility(View.GONE);
                holder.denyBtn.setVisibility(View.VISIBLE);
            } else if (item.state.equals("2")) {
                holder.allowBtn.setVisibility(View.GONE);
                holder.denyBtn.setVisibility(View.GONE);
            }

            holder.allowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSecondaryCustomLoadingView();
                    AndroidNetworking.post(MyApplication.BASE_URL + "user/processRequest")
                            .addBodyParameter("requestID", item.requestID)
                            .addBodyParameter("type", "1")
                            .setTag("processRequest")
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
                                    showToast("Success!");
                                    getRequestList();
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
            });

            holder.denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSecondaryCustomLoadingView();
                    AndroidNetworking.post(MyApplication.BASE_URL + "user/processRequest")
                            .addBodyParameter("requestID", item.requestID)
                            .addBodyParameter("type", "2")
                            .setTag("processRequest")
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
                                    showToast("Success!");
                                    getRequestList();
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
            });
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
