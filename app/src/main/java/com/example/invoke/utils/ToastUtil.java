package com.example.invoke.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */

public class ToastUtil {

    private static Toast toast;

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, CharSequence message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, CharSequence message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    private static void show(Context context, CharSequence message, int duration) {
//        if (toast == null) {
        toast = Toast.makeText(context.getApplicationContext(), message, duration);
//        }
//        toast.setGravity(Gravity.CENTER, 0, 380);
        toast.setDuration(duration);
        toast.setText(message);
        toast.show();
    }
}