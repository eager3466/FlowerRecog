package com.bjut.eager.flowerrecog.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.LruCache;
import android.widget.ImageView;

import com.bjut.eager.flowerrecog.common.constant.Consts;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by eager-mbp on 2017/7/6.
 */

public class LoadImageManager {

    private static LoadImageManager sManager;
    private ImageView mIvShow;
    private ExecutorService mThreadPool;
    private static final int MAX_MEMORY = (int) Runtime.getRuntime().maxMemory();
    private static final int CACHE_SIZE = MAX_MEMORY / 3;
    private static LruCache<String, Bitmap> mLruCache = new LruCache<>(CACHE_SIZE);
    private final int MAX_POOL_SIZE = 10;
    private String mTypeCode;

    private Handler mHandler;

    public static LoadImageManager getInstance() {
        if (sManager == null) {
            return new LoadImageManager(new Handler());
        }
        return sManager;
    }

    private LoadImageManager(Handler handler) {
        this.mHandler = handler;
        mThreadPool = Executors.newFixedThreadPool(MAX_POOL_SIZE);
    }

    public void loadPic(ImageView ivShow, String typeCode) {
        this.mIvShow = ivShow;
        this.mTypeCode = typeCode;
        Bitmap bitmap = getPicFromCache();
        if (bitmap != null) {
            setImageBitmap(bitmap);
            return ;
        }
        bitmap = getPicFromStorage();
        if (bitmap != null) {
            setImageBitmap(bitmap);
            return ;
        }
        getPicFromServer();

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

    public Bitmap getPicFromCache() {
        // get cache
        return mLruCache.get(mTypeCode);
    }

    public Bitmap getPicFromStorage() {
        // get storage
        File cacheDir = new File(Consts.PIC_CACHE_PATH);
        if (!cacheDir.exists())
            cacheDir.mkdir();
        File picFile = new File(cacheDir, mTypeCode + Consts.PIC_APPENDIX);
        Bitmap bitmap= BitmapFactory.decodeFile(picFile.getAbsolutePath());
        return bitmap;
    }

    public void getPicFromServer() {
        mThreadPool.execute(new DownloadImageTask(mHandler, mIvShow, mTypeCode, mLruCache));
    }
}
