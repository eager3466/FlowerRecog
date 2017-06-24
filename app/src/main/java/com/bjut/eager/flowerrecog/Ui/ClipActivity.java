package com.bjut.eager.flowerrecog.Ui;

import android.app.Activity;
import android.content.SharedPreferences;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    private String REQUEST_URL = PreferenceUtils.getString(Consts.SERVER_ADDRESS, Consts.SERVER_OUTER);;
    private long startTime;
    private String filePath;
    private Bitmap bitmap;

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
        final Bitmap bitmap = result;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
        byte[] byteArray = stream.toByteArray();
        Log.i("YhqTest", "pic size " + (byteArray.length/1024) + "kb");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, byteArray);
        final Request request = new Request.Builder()
                .url(REQUEST_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("YhqTest", "request failed: " + e.toString());
                Log.i("YhqTest", "response time: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i("YhqTest", "response time: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
                if (result != null) {
                    Log.i("YhqTest", "request successful, result: " + result);
                    items = JsonUtil.parse(result);
                    doUIRefresh(bitmap);
                }
            }
        });
    }

    private void doUIRefresh(final Bitmap bitmap) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (items == null)
                    Toast.makeText(getApplication(), "返回列表为空", Toast.LENGTH_SHORT).show();
                else if (items.size() > 0) {
                    RecyclerAdapter adapter = new RecyclerAdapter(getApplication(), items);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                }
                show.setImageBitmap(bitmap);
                recog_title.setVisibility(View.VISIBLE);
                btn_confirm.setText("完成识别");
            }
        });

    }

    private ArrayList<Item> items;

    Handler handler = new Handler();

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_confirm:
                int picSize = PreferenceUtils.getInt(PreferenceConsts.PIC_SIZE, PreferenceConsts.DEFAULT_PIC_SIZE);
                if(btn_confirm.getText().equals("确认裁剪")) {
                    btn_confirm.setText("正在上传并识别中……");
                    startTime = System.currentTimeMillis();
                    clipImageView.setVisibility(View.INVISIBLE);
                    Bitmap result = clipImageView.clip();
                    result = Bitmap.createScaledBitmap(result, picSize, picSize, true);
                    uploadPic(result);

                    Log.i("YhqTest", result.getWidth()  + "");
                    Log.i("YhqTest", result.getHeight() + "");
                    Log.i("YhqTest", result.getByteCount() + "");
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(filePath + ".tmp");
                        result.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (btn_confirm.getText().equals("完成识别")){
                    finish();
                }
            break;
        }
    }
}