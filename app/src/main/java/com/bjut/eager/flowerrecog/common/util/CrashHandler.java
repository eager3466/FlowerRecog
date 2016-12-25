/**
 * @(#)CrashHandler.java, 2016-3-7.
 * 
 * Copyright 2013 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.bjut.eager.flowerrecog.common.util;

import android.content.Context;
import android.util.Log;

import com.bjut.eager.flowerrecog.BuildConfig;
import com.bjut.eager.flowerrecog.common.constant.Consts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Crash Handler
 *
 * <p>record crash info into local file(root dir + log/crash.txt)
 * And if you config the crash server, you can send the crash to the server.
 * for analysing later.
 *
 * Created by yuym on 16/10/27.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    
    private static final String TAG = CrashHandler.class.getSimpleName();
    
    private static CrashHandler instance = new CrashHandler();

    static File reportFile;

    private UncaughtExceptionHandler mDefaultHandler;
    
    private Context mContext = null;
    
    public static CrashHandler getInstance() {
        return instance;
    }
    
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        reportFile = new File(mContext.getFilesDir(), "crash_report");
        boolean crashed = isAvailable();

        if (PreferenceUtils.checkFirstShow("init" + BuildConfig.VERSION_NAME
                + "crash", mContext)) {
            if (crashed) {
                deleteReport();
            }
        }

        if (crashed) {
            sendAndDelete();
        }

        // 创建log路径
        try {
            File f = new File(logPath(), "crash.txt");
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String logPath() {
        return Consts.MAIN_FILE_PATH + "log/";
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(!handleException(ex) && mDefaultHandler != null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else{
//            Utils.restartApp(mContext);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        StringBuffer crashInfo = new StringBuffer();

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm ", Locale.getDefault());

        crashInfo.append("\n\n************* Crash INFO AT " + df.format(now) + "*************\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        crashInfo.append(result);

        crashInfo.append("************* Crash INFO *************\n");
        Log.d(TAG, "Crash INFO : " + crashInfo.toString());
        try {
            FileWriter fw = new FileWriter(reportFile, true);
            fw.append(crashInfo.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter fw = new FileWriter(logPath() + "crash.txt", true);
            fw.append(crashInfo.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static String getReport() {
        if (reportFile == null)
            return null;
        try {
            FileInputStream fis = new FileInputStream(reportFile);
            if (fis.available() > 64 * 1000) {
                fis.close();
                return null;
            }
            byte[] buf = new byte[fis.available()];
            fis.read(buf);
            fis.close();
            String str = new String(buf);
            return str;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean deleteReport() {
        if (reportFile == null)
            return false;
        return reportFile.delete();
    }

    public static boolean isAvailable() {
        if (reportFile == null)
            return false;
        return reportFile.exists();
    }

    public static void sendAndDelete() {
        if (!isAvailable())
            return;
        final String report = getReport();
        deleteReport();
        if (report == null)
            return;
//        Stats.doCrashStatistics(report);
    }
}
