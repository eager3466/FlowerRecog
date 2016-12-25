package com.bjut.eager.flowerrecog.common.constant;

/**
 * Http Consts
 * All Http Const Should Store Here.
 *
 * Created by yuym on 4/2/15.
 */
public class HttpConsts {

    /** Default TimeOut */
    public final static int TIMEOUT = 30 * 1000;

    /**
     * Http Code
     */
    public static final int HTTP_SUCCESS = 200;

    public static final int HTTP_WRONG_PASSWORD = 201;

    /** Request Success */
    public static final int REQUEST_SUCCESS = 0;

    /** Request Fail */
    public static final int REQUEST_FAIL = 1;

    /**
     * Server Ip: 122.114.55.9
     * test url：119.57.119.58:8080
     * host：cloudclinic.strokecn.net
     */
    public static final String BASE_URL = "https://api.douban.com/v2/movie/";
}
