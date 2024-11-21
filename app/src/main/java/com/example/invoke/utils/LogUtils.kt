package com.example.invoke.utils

import android.util.Log
import com.example.invoke.BuildConfig


object LogUtils {

    private var openLog = BuildConfig.DEBUG

    public fun setLog(tag: String, msg: String) {
        if (openLog as Boolean) {
            Log.e(tag, ":$msg")
        }
    }

}