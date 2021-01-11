package com.odelan.qwork.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bluelinelabs.logansquare.LoganSquare;
import com.odelan.qwork.R;
import com.odelan.qwork.data.model.ImageModel;
import com.odelan.qwork.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.odelan.qwork.MyApplication.ASSET_BASE_URL;
import static com.odelan.qwork.MyApplication.BASE_URL;

public class ImagesActivity extends BaseActivity {

    public static String userId;
    public static boolean isEditable = false;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.allBtn)
    Button allBtn;

    @BindView(R.id.selectBtn)
    Button selectBtn;

    @BindView(R.id.delBtn)
    ImageView delBtn;

    List<ImageModel> data = new ArrayList<>();
    List<Boolean> selectedImage = new ArrayList<>();

    boolean isSelectMode = false;
    boolean isAllSelectMode = false;

    static final boolean GRID_LAYOUT = true;
    static int colomn_count = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mContext = this;
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectBtn.setText("Select");
        allBtn.setText("Select All");
        allBtn.setVisibility(View.INVISIBLE);
        delBtn.setVisibility(View.INVISIBLE);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllImages();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (isEditable) {
            selectBtn.setVisibility(View.VISIBLE);
        } else {
            selectBtn.setVisibility(View.INVISIBLE);
        }

        setLayout();

        getAllImages();
    }

    private void setLayout() {
        RecyclerView.LayoutManager layoutManager;

        if (GRID_LAYOUT) {
            layoutManager = new GridLayoutManager(mContext, colomn_count);
        } else {
            layoutManager = new LinearLayoutManager(mContext);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    public void getAllImages() {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(BASE_URL + "image_upload/getAllImagesWithUserId")
                .addBodyParameter("userid", userId)
                .setTag("getAllImagesWithUserId")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                data = new ArrayList<ImageModel>();
                                data = LoganSquare.parseList(response.getString("data"), ImageModel.class);

                                allSelect(Boolean.FALSE);
                                recyclerView.setAdapter(new RecyclerViewAdapter(data));

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

    public void allSelect(Boolean isAllSelect) {
        selectedImage = new ArrayList<>();

        for (ImageModel im : data) {
            selectedImage.add(isAllSelect);
        }
    }

    public boolean isSelected(int idx) {
        boolean result = false;

        if (idx >= selectedImage.size()) {
            return false;
        }

        Boolean isSel = selectedImage.get(idx);
        if (isSel.booleanValue()) {
            result = true;
        }

        return result;
    }

    @OnClick(R.id.backIV) public void onBack() {
        finish();
    }

    @OnClick(R.id.selectBtn) public void onSelectBtn() {
        if(isSelectMode) {
            isSelectMode = false;
            isAllSelectMode = false;

            selectBtn.setText("Select");
            allBtn.setText("Select All");
            allBtn.setVisibility(View.INVISIBLE);
            delBtn.setVisibility(View.INVISIBLE);

            allSelect(Boolean.FALSE);
            recyclerView.setAdapter(new RecyclerViewAdapter(data));
        } else {
            isSelectMode = true;
            isAllSelectMode = true;

            selectBtn.setText("Cancel");
            allBtn.setText("Select All");
            allBtn.setVisibility(View.VISIBLE);
            delBtn.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.allBtn) public void onAllBtn() {
        if(isAllSelectMode) {
            isAllSelectMode = false;
            allBtn.setText("Deselect All");

            allSelect(Boolean.TRUE);
        } else {
            isAllSelectMode = true;
            allBtn.setText("Select All");

            allSelect(Boolean.FALSE);
        }

        recyclerView.setAdapter(new RecyclerViewAdapter(data));
    }

    public void delImages(String uids) {
        showSecondaryCustomLoadingView();
        AndroidNetworking.post(BASE_URL + "image_upload/deleteImageWithIds")
                .addBodyParameter("uids", uids)
                .setTag("deleteImageWithIds")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        hideCustomLoadingView();
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        try {
                            String status = response.getString("status");
                            if(status.equals("1")) {
                                List temp = new ArrayList();
                                for (int i=0;i<data.size();i++) {
                                    if (!selectedImage.get(i).booleanValue()) {
                                        temp.add(data.get(i));
                                    }
                                }

                                data = temp;
                                allSelect(Boolean.FALSE);
                                recyclerView.setAdapter(new RecyclerViewAdapter(data));

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

    @OnClick(R.id.delBtn) public void onDel() {
        String uids = "";
        for (int i=0;i<selectedImage.size();i++) {
            ImageModel im = data.get(i);
            if(selectedImage.get(i).booleanValue()) {
                uids += "," + im.uid;
            }
        }

        if(uids.equals("")) {
            showToast("Please choose images");
        } else {
            String us = uids.substring(1);
            delImages(us);
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ImageModel> mTileList;

        public RecyclerViewAdapter(List<ImageModel> list) {
            mTileList = list;
        }

        @Override
        public int getItemCount() {
            return mTileList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_tile, parent, false);
            return new RecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder mholder, final int position) {
            final RecyclerViewAdapter.ViewHolder holder = (RecyclerViewAdapter.ViewHolder) mholder;
            holder.mTile = mTileList.get(position);

            if(holder.mTile.image_url != null && !holder.mTile.image_url.equals("")) {
                Picasso.with(mContext).load(ASSET_BASE_URL +  holder.mTile.image_url).placeholder(R.drawable.back_thumb).into(holder.iv);
            }

            if (isSelected(position)) {
                holder.markiv.setVisibility(View.VISIBLE);
            } else {
                holder.markiv.setVisibility(View.INVISIBLE);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectMode) {
                        if (isSelected(position)) {
                            selectedImage.set(position, Boolean.FALSE);
                        } else {
                            selectedImage.set(position, Boolean.TRUE);
                        }

                        recyclerView.setAdapter(new RecyclerViewAdapter(data));
                    } else {
                        ImageDetailActivity.image_url = holder.mTile.image_url;
                        startActivity(new Intent(mContext, ImageDetailActivity.class));
                    }
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView iv;
            public final ImageView markiv;

            public ImageModel mTile;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                iv = (ImageView) view.findViewById(R.id.iv);
                markiv = (ImageView) view.findViewById(R.id.markIV);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }
}
