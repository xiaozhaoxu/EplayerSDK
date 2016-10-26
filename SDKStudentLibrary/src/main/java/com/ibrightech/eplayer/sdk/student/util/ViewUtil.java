package com.ibrightech.eplayer.sdk.student.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ViewUtil {

    private static Float displayDensity;


    public static int roundUp(float paramFloat) {
        return (int) (0.5F + paramFloat);
    }

    public static int dipToPx(Context paramContext, float paramFloat) {
        return roundUp(getDisplayDensity(paramContext) * paramFloat);
    }

    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static float getDisplayDensity(Context paramContext) {
        if (displayDensity == null)
            displayDensity = Float.valueOf(paramContext.getResources().getDisplayMetrics().density);
        return displayDensity.floatValue();
    }

    public static int getDisplayHeight(Context paramContext) {
        return paramContext.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(Context paramContext) {
        return paramContext.getResources().getDisplayMetrics().widthPixels;
    }


    public static void setViewMargins(View paramView, int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams) paramView.getLayoutParams();
        localMarginLayoutParams.leftMargin = leftMargin;
        localMarginLayoutParams.topMargin = topMargin;
        localMarginLayoutParams.rightMargin = rightMargin;
        localMarginLayoutParams.bottomMargin = bottomMargin;
        paramView.setLayoutParams(localMarginLayoutParams);
    }
}