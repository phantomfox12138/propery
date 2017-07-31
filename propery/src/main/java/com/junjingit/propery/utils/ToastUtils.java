package com.junjingit.propery.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.junjingit.propery.R;

/**
 * Created by jxy on 2017/7/31.
 */

public class ToastUtils {
    public static void showToast(Context context, int resStringParam) {
        showToast(context, context.getString(resStringParam));
    }

    public static void showToast(Context context, String stringParam) {
        showToast(context, stringParam, Gravity.BOTTOM, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String toastStr, int gravity,
                                 int duration) {
        LinearLayout toastLayout = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setMinimumHeight((int)ScreenUtils.dpToPx(context, 40));
        textView.setMinWidth((int)ScreenUtils.dpToPx(context, 100));
        textView.setMaxWidth((int)ScreenUtils.dpToPx(context, 200));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding((int)ScreenUtils.dpToPx(context, 15),
                (int)ScreenUtils.dpToPx(context, 5), (int)ScreenUtils.dpToPx(context, 15),
                (int)ScreenUtils.dpToPx(context, 5));
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setTextSize(14);
        textView.setText(toastStr);
        toastLayout.addView(textView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        toastLayout.setBackgroundResource(R.mipmap.circle_bg_hint);

        Toast toast = new Toast(context);
        switch (gravity) {// 设置Toast在页面中的显示位置
            case Gravity.CENTER_VERTICAL:
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                break;
            case Gravity.BOTTOM:
                toast.setGravity(Gravity.BOTTOM, 0, (int)ScreenUtils.dpToPx(context, 100));
                break;
            case Gravity.TOP:
                toast.setGravity(Gravity.TOP, 0, 0);
                break;
            case Gravity.CENTER:
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
        }
        if (duration == Toast.LENGTH_LONG) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setView(toastLayout);
        if (toastStr.length() > 0) {
            toast.show();
        }
    }
}
