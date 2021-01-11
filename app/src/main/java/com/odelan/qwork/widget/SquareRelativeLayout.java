package com.odelan.qwork.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.odelan.qwork.utils.Common;

/**
 * Created by Administrator on 6/20/2016.
 */
public class SquareRelativeLayout extends RelativeLayout {

    Context mContext;

    public SquareRelativeLayout(Context context) {
        super(context);
        mContext = context;
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // We have to manually resize the child views to match the parent.

        int curWidth, curHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredWidth() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            /*//Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();

            if(child instanceof ImageView) {
                child.layout(childLeft, childTop, childRight, childBottom);
            } else {
                int c_top = childTop + ((childBottom - childTop) - (curHeight)) / 2;
                int c_left = childLeft + ((childRight - childLeft) - (curWidth)) / 2;
                int c_right = c_left + curWidth;
                int c_bottom = c_top + curHeight;
                child.layout(c_left, c_top, c_right, c_bottom);
            }*/

            if (i == 0) {
                child.layout(childLeft, childTop, childRight, childBottom);
            } else {
                /*child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.UNSPECIFIED));
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();*/

                curWidth = (int)Common.convertDpToPixel(25);
                curHeight = curWidth;

                int c_top = childTop + ((childBottom - childTop) - (curHeight)) / 2;
                int c_left = childLeft + ((childRight - childLeft) - (curWidth)) / 2;
                int c_right = c_left + curWidth;
                int c_bottom = c_top + curHeight;
                child.layout(c_left, c_top, c_right, c_bottom);
            }
        }
    }
}