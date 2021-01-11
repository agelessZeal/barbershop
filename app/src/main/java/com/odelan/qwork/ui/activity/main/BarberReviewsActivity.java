package com.odelan.qwork.ui.activity.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.gson.Gson;
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
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BarberReviewsActivity extends BaseActivity {

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.listView)
    ListView listView;

    List<History> mHistories = new ArrayList<>();
    ListAdapter listAdapter;

    DateTimeUtils dateTimeUtils;

    public static User mUser = null;

    int page = 1;
    int limit = 50;
    private AlertDialog addDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_reivew);

        mContext = this;
        ButterKnife.bind(this);

        dateTimeUtils = new DateTimeUtils();
        setTitle("Reviews");

        mHistories = new ArrayList<History>();

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
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/getHistoryWithBarberId")
                .addBodyParameter("barber_id", mUser.userid)
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
                                    mHistories = new ArrayList<History>();
                                    mHistories = LoganSquare.parseList(response.getString("data"), History.class);
                                    listAdapter = new BarberReviewsActivity.ListAdapter(mContext, mHistories);
                                    listView.setAdapter(listAdapter);
                                } else {
                                    List<History> histories = new ArrayList<>();
                                    histories = LoganSquare.parseList(response.getString("data"), History.class);

                                    mHistories.addAll(histories);
                                    listAdapter.notifyDataSetChanged();
                                }

                                //listView.setAdapter(new BarberReviewsActivity.ListAdapter(mContext, mHistories));

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

    public class ListAdapter extends BaseAdapter {
        private final User me;
        Context context;
        LayoutInflater inflater = null;
        List<History> histories;

        public ListAdapter(Context con, List<History> hs) {
            context = con;
            histories = hs;
            me = getUser(getValueFromKey("me"));
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
            MaterialRatingBar ratingBar;
            TextView ratingTV;
            TextView dateTV;
            TextView reviewTV, tv_dispute;
            CircleImageView photoIV;

            public ViewHolder(View v) {
                mView = v;
                nameTV = (TextView) v.findViewById(R.id.nameTV);
                ratingTV = (TextView) v.findViewById(R.id.ratingTV);
                dateTV = (TextView) v.findViewById(R.id.dateTV);
                reviewTV = (TextView) v.findViewById(R.id.reviewTV);
                tv_dispute = (TextView) v.findViewById(R.id.tv_dispute);
                photoIV = (CircleImageView) v.findViewById(R.id.photoIV);
                ratingBar = (MaterialRatingBar) v.findViewById(R.id.ratingBar);
            }
        }

        @Override
        public View getView(final int index, final View convertView, ViewGroup viewGroup) {
            final ListAdapter.ViewHolder holder;

            View vi = convertView;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.list_item_history_for_barber, null);
                holder = new ListAdapter.ViewHolder(vi);

                vi.setTag(holder);
            } else
                holder = (ListAdapter.ViewHolder) vi.getTag();
            try {
                final History item = (History) getItem(index);
                if (item.customer.username != null) {
                    holder.nameTV.setText(item.customer.username);
                }

                String date = item.order.created_on;
                if (date != null) {
                    holder.dateTV.setText(dateTimeUtils.convertFullDateStringToDateString(date));
                }

                if (item.customer.photo != null && !item.customer.photo.equals("")) {
                    Picasso.with(context).load(MyApplication.ASSET_BASE_URL + item.customer.photo).placeholder(R.drawable.ic_user).into(holder.photoIV);
                }

                if (item.order.comment == null || item.order.comment.equals("")) {
                    holder.reviewTV.setText("No review given");
                } else {
                    holder.reviewTV.setText(item.order.comment);
                }

                Log.v("Ahtosh", "*** " + new Gson().toJson(mUser));
//                Log.v("Ahtosh", "*** " + new Gson().toJson(item));

                holder.tv_dispute.setVisibility(me.user_type.equalsIgnoreCase(MyApplication.USER_CUSTOMER) ? View.GONE : View.VISIBLE);

                if (item.order.rating != null) {
                    holder.ratingTV.setText("(" + item.order.rating + ")");
                    float rating = Float.parseFloat(item.order.rating);
                    holder.ratingBar.setRating(rating);
                } else {
                    holder.ratingTV.setText("(0.0)");
                    holder.ratingBar.setRating(0);
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*C_HistoryDetailActivity.mHistory = item;
                        startActivity(new Intent(context, C_HistoryDetailActivity.class));*/
                    }
                });

                holder.tv_dispute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDisputeDialog(item.order.barber_id, item.order.order_id);
                    }
                });

            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            }

            if (index == page * limit - 1) {
                page++;
//                getHistory();
            }
            return vi;
        }
    }

    public void showDisputeDialog(final String barber_id, final String reviewId) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //inflate view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.dlg_dispute, null);

        ImageView closeIV = (ImageView) customView.findViewById(R.id.closeIV);
        final EditText tv_reply_msg = (EditText) customView.findViewById(R.id.tv_reply_msg);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });
        customView.findViewById(R.id.btn_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDispute(reviewId, barber_id, tv_reply_msg.getText().toString().trim());
            }
        });

        customView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDlg.dismiss();
            }
        });

        //dialog.show();

        builder.setView(customView);
        addDlg = builder.show();

    }


    public void sendDispute(String reviewID, String barber_id, String message) {
        if (message.isEmpty()) {
            showToast("Please enter message");
            return;
        }
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "user/sendDebate")
                .addBodyParameter("review_id", reviewID)
                .addBodyParameter("barber_id", barber_id)
                .addBodyParameter("message", message)
                .setTag("sendDebate")
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
                                showToast("Sent");
                                addDlg.dismiss();
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
                        hideCustomLoadingView();
                        showToast(error.getErrorBody());
                    }
                });
    }

}
