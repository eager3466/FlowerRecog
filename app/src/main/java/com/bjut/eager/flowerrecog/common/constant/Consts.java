package com.bjut.eager.flowerrecog.common.constant;

import android.os.Environment;

import com.bjut.eager.flowerrecog.BaseApplication;

/**
 * Common Consts
 * All Common Const Should Store Here.
 *
 * Created by yuym on 4/2/15.
 */
public class Consts {

    /**
     * default log tag
     */
    public static String DEFAULT_LOG_TAG = BaseApplication.class.getSimpleName();

    /**
     * File Path *
     */
    public static final String ASSET_PATH_PREFIX = "file:///android_asset/";

    public static final String SDCARD_PATH_PREFIX = "file:///mnt/sdcard/";

    /**
     * local file path
     */
    public static final String MAIN_FILE_PATH = Environment.getExternalStorageDirectory() + "/CloudClinic/";

    public static final String TEMP_FILE_PATH = MAIN_FILE_PATH + "temp/";

    public static final String PLATFORM_SHARE_NAME = "share_temp.png";

    public static final String AVATAR_CACHE_FILE =  "avatar.png";

    public static final String SERVER_ADDRESS = "server_address";
    public static final String SERVER_INNER = "http://172.21.15.159:8086";
    public static final String SERVER_OUTER = "http://mpccl.bjut.edu.cn/paperretrieval/transmit";

}
