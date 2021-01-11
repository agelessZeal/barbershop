package com.odelan.qwork.ui.activity.main.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.MyApplication;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.History;
import com.odelan.qwork.data.model.Order;
import com.odelan.qwork.ui.activity.main.BarberHomeActivity;
import com.odelan.qwork.ui.activity.main.BarbershopHomeActivity;
import com.odelan.qwork.ui.base.BaseActivity;
import com.odelan.qwork.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class C_HistoryDetailActivity extends BaseActivity {

    @BindView(R.id.photoIV)
    CircleImageView photoIV;

    @BindView(R.id.barberNameTV)
    TextView barberNameTV;

    @BindView(R.id.barbershopNameTV)
    TextView barbershopNameTV;

    @BindView(R.id.startTV)
    TextView startTV;

    @BindView(R.id.endTV)
    TextView endTV;

    @BindView(R.id.durationTV)
    TextView durationTV;

    @BindView(R.id.ratingBar)
    MaterialRatingBar ratingBar;

    @BindView(R.id.hintTV1)
    TextView hintTV1;

    @BindView(R.id.hintTV2)
    TextView hintTV2;

    @BindView(R.id.bsIV)
    ImageView bsIV;

    @BindView(R.id.commentET)
    EditText commentET;

    @BindView(R.id.sendBtn)
    ImageView sendBtn;

    @BindView(R.id.anonyll)
    LinearLayout anonyll;

    public static History mHistory;
    DateTimeUtils dateTimeUtils;

    String ratingValue = "";

    boolean isAnony = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c__history_detail);

        mContext = this;
        ButterKnife.bind(this);

        showTitleIV();
        hideTitle();

        dateTimeUtils = new DateTimeUtils();
        if(mHistory.barber.photo != null && !mHistory.barber.photo.equals("")) {
            Picasso.with(mContext).load(MyApplication.ASSET_BASE_URL + mHistory.barber.photo).into(photoIV);
        }

        if(mHistory.barber.username != null && !mHistory.barber.username.equals("")) {
            barberNameTV.setText(mHistory.barber.username);
        }

        if(mHistory.barbershop == null) {
            barbershopNameTV.setText("Nothing");
            bsIV.setVisibility(View.GONE);
        } else {
            if (mHistory.barbershop.username != null && !mHistory.barbershop.username.equals("")) {
                barbershopNameTV.setText(mHistory.barbershop.username);
                bsIV.setVisibility(View.VISIBLE);
            }
        }

        final Order order = mHistory.order;
        try {
            long endtime = Long.parseLong(order.end_time);
            long starttime = Long.parseLong(order.start_time);
            durationTV.setText((endtime - starttime)/60 + "min");
            startTV.setText(dateTimeUtils.getDate(starttime * 1000, "yyyy-MM-dd HH:mm"));
            endTV.setText(dateTimeUtils.getDate(endtime * 1000, "yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(order.comment != null) {
            commentET.setText(order.comment);
        }

        if(order.rating == null || order.rating.equals("") || order.rating.equals("0")) {
            hintTV1.setVisibility(View.VISIBLE);
            hintTV2.setVisibility(View.VISIBLE);
            ratingBar.setIsIndicator(false);
            commentET.setEnabled(true);
            sendBtn.setVisibility(View.VISIBLE);
            anonyll.setVisibility(View.VISIBLE);
        } else {
            hintTV1.setVisibility(View.GONE);
            hintTV2.setVisibility(View.GONE);
            ratingBar.setIsIndicator(true);
            float rating = Float.parseFloat(order.rating);
            ratingBar.setRating(rating);

            /*if(order.comment == null || order.comment.equals("")) {
                commentET.setVisibility(View.GONE);
            } else {
                commentET.setVisibility(View.VISIBLE);
            }*/
            commentET.setVisibility(View.GONE);
            commentET.setEnabled(false);
            sendBtn.setVisibility(View.GONE);
            anonyll.setVisibility(View.GONE);
        }

        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratingValue = String.valueOf(rating);
                //setRate(order.order_id, String.valueOf(rating));
            }
        });
    }

    @OnClick(R.id.sendBtn) public void onSendBtn() {
        if(TextUtils.isEmpty(ratingValue)) {
            showToast("Please choose rating star");
            return;
        }

        if(TextUtils.isEmpty(commentET.getText())) {
            showToast("Please input comment");
            return;
        }

        setRate(mHistory.order.order_id, ratingValue);
    }

    @OnClick(R.id.photoIV) public void onPhotoClick() {
        BarberHomeActivity.mFromUser = getUser(getValueFromKey("me"));
        BarberHomeActivity.mUser = mHistory.barber;
        startActivity(new Intent(mContext, BarberHomeActivity.class));
    }

    @OnClick(R.id.bsIV) public void onBarbershopClick() {
        BarbershopHomeActivity.mFromUser = getUser(getValueFromKey("me"));
        BarbershopHomeActivity.mUserId = mHistory.barbershop.userid;
        startActivity(new Intent(mContext, BarbershopHomeActivity.class));
    }

    @OnClick(R.id.anonyChk) public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        isAnony = checked;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setRate(String orderid, final String rate) {
        String comment = commentET.getText().toString();
        String is_anony = "0";
        if (this.isAnony) is_anony = "1";
        if(comment == null) comment = "";
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(MyApplication.BASE_URL + "order/setRatingWithComment")
                .addBodyParameter("order_id", orderid)
                .addBodyParameter("rating", rate)
                .addBodyParameter("comment", comment)
                .addBodyParameter("isAnony", is_anony)
                .setTag("setRating")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideCustomLoadingView();
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                Order order = LoganSquare.parse(response.getString("order"), Order.class);
                                String comment = order.comment;
                                if(comment == null) {
                                    comment = "";
                                }
                                showToast("You have successfully rated");
                                ratingBar.setIsIndicator(true);
                                hintTV1.setVisibility(View.GONE);
                                hintTV2.setVisibility(View.GONE);
                                if(order.comment == null || order.comment.equals("")) {
                                    commentET.setVisibility(View.GONE);
                                } else {
                                    commentET.setVisibility(View.VISIBLE);
                                }
                                commentET.setEnabled(false);
                                sendBtn.setVisibility(View.GONE);
                                anonyll.setVisibility(View.GONE);
                                mHistory.order.rating = rate;
                                mHistory.order.comment = comment;
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
                        hideCustomLoadingView();
                        showToast(error.getErrorBody().toString());
                    }
                });
    }
}
