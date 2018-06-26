package com.example.android.bakingtime.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class FixedAspectRatioFrameLayout extends FrameLayout {
    float mAspectRatio = 9/16f;

    public FixedAspectRatioFrameLayout(Context context) {
        super(context);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float height = MeasureSpec.getSize(widthMeasureSpec) * mAspectRatio;
        int heightSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}