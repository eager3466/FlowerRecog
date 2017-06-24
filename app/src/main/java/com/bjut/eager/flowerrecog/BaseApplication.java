package com.bjut.eager.flowerrecog;

import android.app.Application;

public class BaseApplication extends Application {

    private static BaseApplication application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static BaseApplication getContext() {
        return application;
    }
}
