package com.bjut.eager.flowerrecog.common.constant;

import android.os.Environment;

/**
 * Common Consts
 * All Common Const Should Store Here.
 *
 * Created by Eager.
 */
public class Consts {

    public static final String LOG_TAG = "YhqTest";
    public static final String SERVER_ADDRESS = "server_address";

    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String IMAGE_PATH = ROOT_DIR + "/AImage/";
    public static final String PIC_CACHE_PATH = IMAGE_PATH + "cache/";
    public static final String TEMP_FILE_NAME = "uploadTemp.jpg";

    public static final String SERVER_URL_DEFAULT = "http://172.21.15.98";
    public static final int SERVER_PORT_DEFAULT = 8086;
    public static final String SEPARATOR = ":";
    public static final String SERVER_URL_OUTER = "http://mpccl.bjut.edu.cn/paperretrieval/transmit";
    public static final String PICTURE_GET_URL_SUFFIX = "?type_code=";
    public static final String PIC_APPENDIX = ".jpg";
    public static final String PIC_TITLE = "image";

    // Intent Consts
    public static final String INTENT_IMAGE_URI = "uri";

}
