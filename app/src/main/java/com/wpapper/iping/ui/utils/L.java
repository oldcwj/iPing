package com.wpapper.iping.ui.utils;

import android.util.Log;

import iping.wpapper.com.iping.BuildConfig;


public class L {

    private static final String TAG_FORMAT = "%s-%s(%d)";

    static boolean DEBUG = BuildConfig.DEBUG;

    public static void i(String message) {
        if (DEBUG) {
            Log.i(tag(), message == null ? " NO MESSAGE " : message);
        }
    }

    public static void e(Exception e) {
        if (DEBUG) {
            Log.e(tag(), e.getMessage() == null ? " NO MESSAGE " : e.getMessage(), e);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e(tag(), message == null ? " NO MESSAGE " : message);
        }
    }

    private static String tag() {
        Throwable throwable = new Throwable();
        StackTraceElement target = throwable.getStackTrace()[2];
        if (target == null) {
            return "UNKOWN";
        }
        return String.format(TAG_FORMAT, target.getClassName().substring(target.getClassName().lastIndexOf('.') + 1), target.getMethodName(), target.getLineNumber());
    }

}
