package com.bjut.eager.flowerrecog.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import com.bjut.eager.flowerrecog.common.constant.Consts;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by eager-mbp on 2017/7/6.
 */

public class DownloadImageTask implements Runnable {

    private Handler mHandler;
    private ImageView mIvShow;
    private String mTypeCode;
    private LruCache<String, Bitmap> mLruCache;

    public DownloadImageTask(Handler handler, ImageView ivShow, String typeCode,
                             LruCache<String, Bitmap> lruCache) {
        this.mHandler = handler;
        this.mIvShow = ivShow;
        this.mTypeCode = typeCode;
        this.mLruCache = lruCache;
    }

    @Override
    public void run() {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Consts.PICTURE_GET_URL_INNER+mTypeCode)
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Deal with fail
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                saveInMemoryCache(bitmap);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    private void saveInMemoryCache(Bitmap bitmap) {
        File cacheDir = new File(Consts.PIC_CACHE_PATH);
        if (!cacheDir.exists())
            cacheDir.mkdir();
        File picFile = new File(cacheDir, mTypeCode + Consts.PIC_APPENDIX);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("save pic broken");
        }
    }

    private void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null)
            return ;
        if (mIvShow.getTag().equals(mTypeCode)) {
            mIvShow.setImageBitmap(bitmap);
            if (mLruCache.get(mTypeCode) == null) {
                mLruCache.put(mTypeCode, bitmap);
            }
        }
    }
}