package com.vvasilyev.tutu.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 *
 */
public class SwitchView extends FrameLayout {
    private ViewBinder viewBinder;

    public SwitchView(Context context) {
        super(context);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void set(Object object) {
        viewBinder.bind(getChildAt(0), object);
        getChildAt(1).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if  (getChildCount() != 2) {
            throw new IllegalStateException("Should have 2 children");
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setViewBinder(ViewBinder viewBinder) {
        this.viewBinder = viewBinder;
    }

    public interface ViewBinder<T> {

        void bind(View view, T value);
    }
}
