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
import android.os.Handler;
import android.os.Message;
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
import com.bjut.eager.flowerrecog.anno;
import com.bjut.eager.flowerrecog.common.util.PreferenceUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

import static com.bjut.eager.flowerrecog.common.constant.Consts.SERVER_INNER;
import static com.bjut.eager.flowerrecog.common.constant.Consts.SERVER_OUTER;
import static com.bjut.eager.flowerrecog.common.constant.PreferenceConsts.SERVER_ADDRESS;

public class MainActivity extends Activity {

    final public static int REQUEST_CODE_ASK_CAMERA = 1;
    final public static int REQUEST_CODE_ASK_ALBUM  = 2;

    Button btn_camera;
    private String mFilePath;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    void parse(Object object) {
        final Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields) {
            if (field.isAnnotationPresent(anno.class)) {
                anno  aannn = field.getAnnotation(anno.class);
                String result = aannn.value();
                try {
                    field.set(object, result);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String address = PreferenceUtils.getString(SERVER_ADDRESS, SERVER_INNER);
        parse(this);
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
                PreferenceUtils.putString(SERVER_ADDRESS, SERVER_INNER);
            }
        });

        findViewById(R.id.net_outer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.net_addr_text)).setText(SERVER_OUTER);
                PreferenceUtils.putString(SERVER_ADDRESS, SERVER_OUTER);
            }
        });

        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
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

//                Uri selectedImage = data.getData();
                Uri selectedImage = geturi(data);
                Log.i("Yhqtest", selectedImage.toString());
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

    private static boolean isExit = false;
    Handler mHandler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再次点击返回键退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }

    }

}