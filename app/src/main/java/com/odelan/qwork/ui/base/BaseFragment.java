package com.odelan.qwork.ui.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.odelan.qwork.R;
import com.odelan.qwork.utils.Common;


/**
 * Created by Administrator on 7/20/2016.
 */
public class BaseFragment extends Fragment {
    public Context mContext;
    public View mView;
    public String TAG = "BaseFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
    }

    public View getCustomLoadingView() {
        return mView.findViewById(R.id.view_progress);
    }

    public void showCustomLoadingView() {
        View progressView = getCustomLoadingView();
        if(progressView != null) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    public void hideCustomLoadingView() {
        View progressView = getCustomLoadingView();
        if(progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }

    public void saveKeyValue (String key, String value) {
        Common.saveInfoWithKeyValue(mContext, key, value);
    }

    public String getValueFromKey (String key) {
        return Common.getInfoWithValueKey(mContext, key);
    }

    private Toast mToast;
    public void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
