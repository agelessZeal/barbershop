package com.odelan.qwork.ui.activity.main.customer;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.odelan.qwork.R;
import com.odelan.qwork.data.model.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by MJC_COM on 2018-01-11.
 */

public class WeekAdapter extends  RecyclerView.Adapter<WeekAdapter.ViewHolder> {
   private String [] names = {"MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN"};
   private User user;
   private Context context;
   private int width;
   private int height;

    public WeekAdapter(Context context, User user, int width, int height) {
        this.context = context;
        this.user    = user;
        this.width   = width;
        this.height  = height;
    }

    public void setSize(int wid, int hei) {
        this.width = wid;
        this.height = hei;

    }
    @Override
    public WeekAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_opening_time_hsv, parent, false);
        view.setLayoutParams(new RelativeLayout.LayoutParams(this.width, this.height));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeekAdapter.ViewHolder holder, int position) {

        String name = this.names[position];
        holder.weekDayNameTV.setText(name);

        if (!TextUtils.isEmpty(user.close_state)){
            String[] states = user.close_state.split(",");
            List<String> stringList = Arrays.asList(states);

            int i = position + 1;
            if (stringList.indexOf(Integer.toString(i + 20)) != -1) {
                holder.weekDayTimeTV.setText("Closed");
                if (i % 2 == 0) {
                    holder.weekDayNameTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                    holder.weekDayTimeTV.setBackgroundColor(R.drawable.opening_table_item_hsv_white);
                }
                return;
            }
        }

        String start, end;
        switch (position){
            case 0:
                start = user.mon_time_start;
                end = user.mon_time_end;
                break;
            case 1:
                start = user.tue_time_start;
                end = user.tue_time_end;
                holder.weekDayNameTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                holder.weekDayTimeTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                break;
            case 2:
                start = user.wed_time_start;
                end = user.wed_time_end;
                break;
            case 3:
                start = user.thr_time_start;
                end = user.thr_time_end;
                holder.weekDayNameTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                holder.weekDayTimeTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                break;
            case 4:
                start = user.fri_time_start;
                end = user.fri_time_end;
                break;
            case 5:
                start = user.sat_time_start;
                end = user.sat_time_end;
                holder.weekDayNameTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                holder.weekDayTimeTV.setBackgroundResource(R.drawable.opening_table_item_hsv_white);
                break;
            default:
                start = user.sun_time_start;
                end = user.sun_time_end;
                break;
        }
        if(!(TextUtils.isEmpty(start) || TextUtils.isEmpty(end)))
            holder.weekDayTimeTV.setText(start+" - " + end);
    }

    @Override
    public int getItemCount() {
        return this.names.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView weekDayNameTV;
        TextView weekDayTimeTV;
        public ViewHolder(View itemView) {
            super(itemView);
            intView(itemView);
        }

        private void intView(View itemView) {
            weekDayNameTV = (TextView) itemView.findViewById(R.id.weekDayNameTV);
            weekDayTimeTV = (TextView) itemView.findViewById(R.id.weekDayTimeTV);
        }
    }
}
