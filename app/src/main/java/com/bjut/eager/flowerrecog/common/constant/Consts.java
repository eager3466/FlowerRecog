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

    public static final String SERVER_URL_INNER = "http://172.21.15.117:8086";
    public static final String SERVER_URL_OUTER = "http://mpccl.bjut.edu.cn/paperretrieval/transmit";
    public static final String PICTURE_GET_URL_INNER = "http://172.21.15.117:8086?type_code=";
    public static final String PIC_APPENDIX = ".jpg";

}
