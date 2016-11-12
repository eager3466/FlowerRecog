package com.bjut.eager.flowerrecog.Ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bjut.eager.flowerrecog.R;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {

    final public static int REQUEST_CODE_ASK_CAMERA = 1;
    final public static int REQUEST_CODE_ASK_ALBUM  = 2;

    private static final String SERVER_INNER = "http://172.21.15.159:8086";
    private static final String SERVER_OUTER = "http://mpccl.bjut.edu.cn/paperretrieval/transmit";

    Button btn_camera;
    private String mFilePath;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("NET_CONFIG", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String address = sharedPreferences.getString("ServerAddress","http://172.21.15.159:8086");
        ((TextView)findViewById(R.id.net_addr_text)).setText(address);

        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/sdcard/AImage/");
                if (!file.exists())
                    file.mkdir();
                String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
                mFilePath = "/sdcard/AImage/"+name;
                String temp = file.getAbsolutePath() + "/" + name;
                Uri uri = Uri.fromFile(new File(temp));
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCameraPermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA);
                    if (checkCameraPermission != PackageManager.PERMISSION_GRANTED) {
                        if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_CAMERA);
                        else {
                            Toast.makeText(getApplication(), "Denied just now", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_CAMERA);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, REQUEST_CODE_ASK_CAMERA);
                    }
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, REQUEST_CODE_ASK_CAMERA);
                }
            }
        });

        findViewById(R.id.btn_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_ALBUM);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_ASK_ALBUM);
                }
            }
        });

        findViewById(R.id.net_inner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.net_addr_text)).setText(SERVER_INNER);
                editor.putString("ServerAddress", SERVER_INNER);
                editor.commit();
            }
        });

        findViewById(R.id.net_outer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.net_addr_text)).setText(SERVER_OUTER);
                editor.putString("ServerAddress", SERVER_OUTER);
                editor.commit();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ASK_CAMERA) {
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                    Log.i("TestFile",
                            "SD card is not avaiable/writeable right now.");
                    return;
                }
                Intent intent = new Intent(this, ClipActivity.class);
                intent.putExtra("path", mFilePath);
                startActivity(intent);
            } else if (requestCode == REQUEST_CODE_ASK_ALBUM) {
                if (data == null)
                    return;

                Uri selectedImage = data.getData();
                selectedImage = geturi(data);
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                if(c != null) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    mFilePath = c.getString(columnIndex);
                    Intent intent = new Intent(this, ClipActivity.class);
                    intent.putExtra("path", mFilePath);
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     * @param intent
     * @return
     */
    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_ASK_CAMERA);
                } else {
                    Toast.makeText(getApplication(), "CAMREA Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_ALBUM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_ASK_ALBUM);
                } else {
                    Toast.makeText(getApplication(), "ALBUM Denied", Toast.LENGTH_SHORT).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}


