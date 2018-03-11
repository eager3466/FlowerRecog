package com.bjut.eager.flowerrecog.Ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bjut.eager.flowerrecog.R;
import com.bjut.eager.flowerrecog.common.constant.Consts;
import com.bjut.eager.flowerrecog.common.constant.PreferenceConsts;
import com.bjut.eager.flowerrecog.common.util.PreferenceUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    public final static int REQUEST_CODE_ASK_CAMERA = 1;
    public final static int REQUEST_CODE_ASK_ALBUM  = 2;

    private String mFilePath;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String address = PreferenceUtils.getString(PreferenceConsts.SERVER_ADDRESS, Consts.SERVER_URL_OUTER);
        ((TextView)findViewById(R.id.net_addr_text)).setText(address);

        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_album).setOnClickListener(this);
        findViewById(R.id.net_inner).setOnClickListener(this);
        findViewById(R.id.net_outer).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ASK_ALBUM) {
                photoUri = data.getData();
            }
            if (photoUri != null) {
                Intent intent = new Intent(this, ClipActivity.class);
                intent.putExtra("uri", photoUri.toString());
                startActivity(intent);
            }
        }
    }

    private void choosePhotoFromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_ASK_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    capturePicture();
                } else {
                    Toast.makeText(getApplication(), R.string.camera_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_ALBUM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_CODE_ASK_ALBUM);
                } else {
                    Toast.makeText(getApplication(), R.string.album_denied, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.exit_confirm, Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_camera:
                capturePicture();
                break;
            case R.id.btn_album:
                choosePhoto();
                break;
            case R.id.net_inner:
                ((TextView)findViewById(R.id.net_addr_text)).setText(Consts.SERVER_URL_INNER);
                PreferenceUtils.putString(Consts.SERVER_ADDRESS, Consts.SERVER_URL_INNER);
                break;
            case R.id.net_outer:
                ((TextView)findViewById(R.id.net_addr_text)).setText(Consts.SERVER_URL_OUTER);
                PreferenceUtils.putString(Consts.SERVER_ADDRESS, Consts.SERVER_URL_OUTER);
                break;
            case R.id.btn_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void capturePicture() {
        File file = new File(Consts.IMAGE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        String temp = file.getAbsolutePath() + "/" + name;
        mFilePath = temp;
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCameraPermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA);
            if (checkCameraPermission != PackageManager.PERMISSION_GRANTED) {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_CAMERA);
                } else {
                    Toast.makeText(getApplication(), R.string.camera_denied, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_CAMERA);
                }
            } else {
                takePhotoFromSystem();
            }
        } else {
            takePhotoFromSystem();
        }
    }

    private void takePhotoFromSystem() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        // not finish,ready to do another thing
        startActivityForResult(intent, REQUEST_CODE_ASK_CAMERA);
    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_ALBUM);
        } else {
            choosePhotoFromAlbum();
        }
    }

}