package com.bjut.eager.flowerrecog;

import android.app.Application;

import com.bjut.eager.flowerrecog.common.util.CrashHandler;

public class BaseApplication extends Application {

    private static BaseApplication application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //init crash handler
        CrashHandler.getInstance().init(this);
    }

    public static BaseApplication getContext() {
        return application;
    }
}
