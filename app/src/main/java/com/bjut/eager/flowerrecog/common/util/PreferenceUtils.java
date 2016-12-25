package com.bjut.eager.flowerrecog.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bjut.eager.flowerrecog.BaseApplication;

/**
 * SharePreference Utils
 *
 * Created by yuym on 16/10/27.
 */
public class PreferenceUtils {
    public static Context CONTEXT = BaseApplication.getContext();

    //====================int====================
    public static synchronized int getInt(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(CONTEXT)
                .getInt(key, defValue);
    }

    public static synchronized void putInt(String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(CONTEXT).edit()
                .putInt(key, value).commit();
    }

    //====================String====================
    public static synchronized String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(CONTEXT)
                .getString(key, defValue);
    }

    public static synchronized String getString(String preferenceName, String key, String defValue) {
        SharedPreferences preferences = CONTEXT.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    public static synchronized void putString(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(CONTEXT).edit()
                .putString(key, value).commit();
    }

    public static synchronized void putString(String preferenceName, String key, String value) {
        SharedPreferences.Editor editor = CONTEXT.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE).edit();
        editor.putString(key, value).commit();
    }

    //====================long====================
    public static synchronized long getLong(String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(CONTEXT)
                .getLong(key, defValue);
    }

    public static synchronized void putLong(String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(CONTEXT).edit()
                .putLong(key, value).commit();
    }

    public static synchronized long addLong(String key) {
        long value = PreferenceManager.getDefaultSharedPreferences(CONTEXT).getLong(key, 0);
        ++value;
        PreferenceManager.getDefaultSharedPreferences(CONTEXT).edit().putLong(key, value).commit();
        return value;
    }

    //====================boolean====================
    public static synchronized boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(CONTEXT)
                .getBoolean(key, defValue);
    }

    public static synchronized void putBoolean(String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(CONTEXT).edit()
                .putBoolean(key, value).commit();
    }

    //====================remove====================
    public static synchronized void remove(String key) {
        PreferenceManager.getDefaultSharedPreferences(CONTEXT)
                .edit().remove(key).commit();
    }

    public static synchronized void remove(String preferenceName, String key) {
        SharedPreferences.Editor editor = CONTEXT.getSharedPreferences(preferenceName, Activity.MODE_PRIVATE).edit();
        editor.remove(key).commit();
    }

    public static synchronized boolean contains(String key) {
        return PreferenceManager
                .getDefaultSharedPreferences(CONTEXT).contains(key);
    }

    public static boolean checkFirstShow(String event, Context context) {
        boolean isFirst;
        isFirst = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(event, true);
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(event, false).commit();
        return isFirst;
    }
}
