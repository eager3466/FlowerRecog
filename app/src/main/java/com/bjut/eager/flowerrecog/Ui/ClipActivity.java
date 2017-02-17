package com.bjut.eager.flowerrecog.Ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.bjut.eager.flowerrecog.Utils.UpLoadTask;
import com.bjut.eager.flowerrecog.View.DividerItemDecoration;
import com.bjut.eager.flowerrecog.View.RecyclerAdapter;
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

public class ClipActivity extends Activity {

    ClipImageView clipImageView;
    Button btn_confirm;
    ImageView show;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private TextView recog_title;
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        final String filePath = getIntent().getStringExtra("path");
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        recog_title = (TextView) findViewById(R.id.recog_text);
        recog_title.setVisibility(View.INVISIBLE);

        sharedPreferences = getSharedPreferences("NET_CONFIG", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final int picSize = sharedPreferences.getInt("PicSize", 227);

        clipImageView = (ClipImageView) findViewById(R.id.clip_image_view);
        clipImageView.setImageBitmap(bitmap);

        show = (ImageView) findViewById(R.id.imageView2);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_confirm.getText().equals("确认裁剪")) {
                    btn_confirm.setText("正在上传并识别中……");
                    clipImageView.setVisibility(View.INVISIBLE);
                    Bitmap result = clipImageView.clip();
                    result = Bitmap.createScaledBitmap(result, picSize, picSize, true);
                    uploadPic(result);
//                    UpLoadTask task = new UpLoadTask(show, handler, getApplication());
//                    task.execute(result);

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
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setVisibility(View.GONE);

    }

    private void uploadPic(final Bitmap result) {
        final Bitmap bitmap = result;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, byteArray);
        final Request request = new Request.Builder()
                .url("http://mpccl.bjut.edu.cn/paperretrieval/transmit")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                if (result != null) {
                    Log.i("YhqTest", "result" + result);
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ArrayList<Item> items = (ArrayList<Item>) msg.obj;

            }
            super.handleMessage(msg);
        }
    };


}