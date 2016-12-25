package com.bjut.eager.flowerrecog.common.util;

import android.text.TextUtils;
import android.util.Log;

import com.bjut.eager.flowerrecog.common.config.Config;
import com.bjut.eager.flowerrecog.common.constant.Consts;

/**
 * Default Log Utils
 *
 * <p><strong>Warning:</strong>
 * all log should use this util instead of logcat fo system
 *
 * Created by yuym on 16/10/27.
 */
public class Logcat {

    /**
     * log.d
     * 
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.d(Consts.DEFAULT_LOG_TAG, msg);
        }
    }

    /**
     * log.e
     * 
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * log.e
     * 
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg, Throwable throwable) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    /**
     * log.w
     * 
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * Log.i
     * 
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.i(tag, msg);
        }
    }

    /**
     * log.v
     * 
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (Config.ON_DEBUG) {
            Log.v(tag, msg);
        }
    }
}
