package com.bjut.eager.flowerrecog.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.bjut.eager.flowerrecog.Bean.Item;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Eager on 2016/10/16.
 */

public class UpLoadTask extends AsyncTask<Bitmap, Integer, String> {

//    private static String REQUEST_URL = "http://mpccl.bjut.edu.cn/paperretrieval/transmit";
    private static String REQUEST_URL;

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 30 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    String PREFIX = "--", LINE_END = "\r\n";
//    String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    String CONTENT_TYPE = "text/html"; // 内容类型

    DataOutputStream outputStream = null;
    Bitmap bitmap = null;
    ImageView show = null;
    Handler mHandler;
    Context mContext;
    ArrayList<Item> items;

    public UpLoadTask(ImageView imageView, Handler handler, Context context) {
        this.show = imageView;
        this.mHandler = handler;
        this.mContext = context;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("NET_CONFIG", Activity.MODE_PRIVATE);
        REQUEST_URL = sharedPreferences.getString("ServerAddress", "http://172.21.15.159:8086");
    }

    @Override
    protected String doInBackground(Bitmap... params) {

        bitmap = params[0];

        try {
            URL url = new URL(REQUEST_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            bitmap = params[0];
            outputStream = new DataOutputStream(conn.getOutputStream());
            boolean re = params[0].compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Log.i("YhqTest", "压缩情况：" + re);
            outputStream.flush();
//            outputStream.write(6);
//            outputStream.flush();
            int responseCode = conn.getResponseCode();
            Log.i("YhqTest", "返回值：" + responseCode);
            if (responseCode == 200) {
                String result;
                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();
                if (result != null) {
                    items = JsonUtil.parse(result);
                }
            }

        } catch (ProtocolException e) {
            Log.i("YhqTest", "Do not support HTTP POST!" );
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Message message = Message.obtain();
        message.what = 1;
        message.obj = items;
        mHandler.sendMessage(message);
        show.setImageBitmap(bitmap);
        super.onPostExecute(s);
    }
}