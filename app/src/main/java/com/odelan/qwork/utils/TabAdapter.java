package com.odelan.qwork.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 12/9/2015.
 */
public class TabAdapter {

    ArrayList<TextView> mViews = null;
    ArrayList<ViewGroup> mViewGroup = null;
    int mCount = 0;
    int active_back_color = 0xFF2FABBA;
    int nonactive_back_color = 0xFF40BECB;
    int active_text_color = 0xFFFFFFFF;
    int nonactive_text_color = 0xFFCCCCCC;
    int selectedID = 0;
    int selectedResID = -1;

    public TabAdapter(){}

    public TabAdapter(ArrayList<TextView> views, ArrayList<ViewGroup> viewGroups){
        this.mViews = views;
        this.mViewGroup = viewGroups;
        mCount = this.mViews.size();
        if(mCount!=0){
            mViews.get(0).setBackgroundColor(active_back_color);
            mViews.get(0).setTextColor(active_text_color);
            mViewGroup.get(0).setVisibility(View.VISIBLE);
            selectedID = 0;
            selectedResID = mViews.get(selectedID).getId();
            for(int i=1;i<mCount;i++){
                mViews.get(i).setBackgroundColor(nonactive_back_color);
                mViews.get(i).setTextColor(nonactive_text_color);
                mViewGroup.get(i).setVisibility(View.GONE);
            }
        }
    }

    public TabAdapter(ArrayList<TextView> views, ArrayList<ViewGroup> viewGroups, int abc, int nabc, int atc, int natc){
        this.active_back_color = abc;
        this.nonactive_back_color = nabc;
        this.active_text_color = atc;
        this.nonactive_text_color = natc;
        this.mViews = views;
        this.mViewGroup = viewGroups;
        mCount = this.mViews.size();
        if(mCount!=0){
            mViews.get(0).setBackgroundColor(active_back_color);
            mViews.get(0).setTextColor(active_text_color);
            mViewGroup.get(0).setVisibility(View.VISIBLE);
            selectedID = 0;
            selectedResID = mViews.get(selectedID).getId();
            for(int i=1;i<mCount;i++){
                mViews.get(i).setBackgroundColor(nonactive_back_color);
                mViews.get(i).setTextColor(nonactive_text_color);
                mViewGroup.get(i).setVisibility(View.GONE);
            }
        }
    }

    public void onTabClick(TextView v){
        unSelectAll();
        for(int i=0;i<mCount;i++){
            if(mViews.get(i).getId() == v.getId()){
                selectedID = i;
                selectedResID = mViews.get(selectedID).getId();
                selectView(mViews.get(i));
            }
        }
    }

    private void unSelectAll(){
        for(int i=0;i<mCount;i++){
            mViews.get(i).setBackgroundColor(nonactive_back_color);
            mViews.get(i).setTextColor(nonactive_text_color);
            mViewGroup.get(i).setVisibility(View.GONE);
        }
    }

    private void selectView(TextView v){
        v.setBackgroundColor(active_back_color);
        v.setTextColor(active_text_color);
        mViewGroup.get(selectedID).setVisibility(View.VISIBLE);
    }

    public void setActiveBackgroundColor(int c){
        active_back_color = c;
    }

    public void setNonActiveBackGroundColor(int c){
        nonactive_back_color = c;
    }

    public void setActiveTextColor(int c){
        active_text_color = c;
    }

    public void setNonActiveTextColor(int c){
        nonactive_text_color = c;
    }

    public int getSelectedTabID(){
        return selectedID;
    }

    public int getSelectedTabResID(){
        return selectedResID;
    }
}
