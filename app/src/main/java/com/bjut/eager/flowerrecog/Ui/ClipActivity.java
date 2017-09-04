package com.bjut.eager.flowerrecog.Ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjut.eager.flowerrecog.Bean.Item;
import com.bjut.eager.flowerrecog.R;
import com.bjut.eager.flowerrecog.Utils.JsonUtil;
import com.bjut.eager.flowerrecog.View.DividerItemDecoration;
import com.bjut.eager.flowerrecog.View.RecyclerAdapter;
import com.bjut.eager.flowerrecog.common.constant.Consts;
import com.bjut.eager.flowerrecog.common.constant.PreferenceConsts;
import com.bjut.eager.flowerrecog.common.util.PreferenceUtils;
import com.githang.clipimage.ClipImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClipActivity extends Activity implements View.OnClickListener {

    private ClipImageView clipImageView;
    private Button btn_confirm;
    private ImageView show;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private TextView recog_title;
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private String REQUEST_URL = PreferenceUtils.getString(Consts.SERVER_ADDRESS, Consts.SERVER_URL_OUTER);
    private ArrayList<Item> items;
    private long startTime;
    private String filePath;
    private Bitmap bitmap;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        if (getIntent() != null) {
            filePath = getIntent().getStringExtra("path");
            bitmap = BitmapFactory.decodeFile(filePath);
        }
        recog_title = (TextView) findViewById(R.id.recog_text);
        recog_title.setVisibility(View.INVISIBLE);

        clipImageView = (ClipImageView) findViewById(R.id.clip_image_view);
        if (bitmap != null) {
            clipImageView.setImageBitmap(bitmap);
        }

        show = (ImageView) findViewById(R.id.imageView2);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setVisibility(View.GONE);

    }

    private void uploadPic(Bitmap result) {
        if (result == null) {
            return ;
        }
        try {
            final Bitmap bitmap = result;
            File folder = new File(Consts.IMAGE_PATH);
            if (!folder.exists())
                folder.mkdir();
            File file = new File(folder, Consts.TEMP_FILE_NAME);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos);
            bos.flush();
            bos.close();
            Log.i(Consts.LOG_TAG, "pic size " + (file.length() / 1024) + "kb");
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            builder.addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file));
            final Request request = new Request.Builder()
                    .url(REQUEST_URL)
                    .post(builder.build())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(Consts.LOG_TAG, "request failed: " + e.toString());
                    Log.i(Consts.LOG_TAG, "response time: " + (System.currentTimeMillis() - startTime) + "ms");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Log.i(Consts.LOG_TAG, "response time: " + (System.currentTimeMillis() - startTime) + "ms");
                    if (result != null) {
                        Log.i(Consts.LOG_TAG, "request successful, result: " + result);
                        items = JsonUtil.parse(result);
                        doUIRefresh(bitmap);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            Log.w(Consts.LOG_TAG, "FileNotFoundException while generating pic.");
        } catch (IOException e) {
            Log.w(Consts.LOG_TAG, "IOException while generating pic.");
        }
    }

    private void doUIRefresh(final Bitmap bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (items == null)
                    Toast.makeText(getApplication(), R.string.list_empty, Toast.LENGTH_SHORT).show();
                else if (items.size() > 0) {
                    RecyclerAdapter adapter = new RecyclerAdapter(getApplication(), items);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                }
                show.setImageBitmap(bitmap);
                recog_title.setVisibility(View.VISIBLE);
                btn_confirm.setText(R.string.recog_finish);
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_confirm:
                int picSize = PreferenceUtils.getInt(PreferenceConsts.PIC_SIZE, PreferenceConsts.DEFAULT_PIC_SIZE);
                if(btn_confirm.getText().equals(getString(R.string.clip_confirm))) {
                    btn_confirm.setText(R.string.uploading);
                    startTime = System.currentTimeMillis();
                    clipImageView.setVisibility(View.INVISIBLE);
                    Bitmap result = clipImageView.clip();
                    result = Bitmap.createScaledBitmap(result, picSize, picSize, true);
                    Log.i(Consts.LOG_TAG, "Result width:" +  result.getWidth());
                    Log.i(Consts.LOG_TAG, "Result height:" + result.getHeight());
                    Log.i(Consts.LOG_TAG, "Result size:" + result.getByteCount()/1024 + "kb");
                    uploadPic(result);
                } else if (btn_confirm.getText().equals(getString(R.string.recog_finish))){
                    finish();
                }
            break;
        }
    }
}