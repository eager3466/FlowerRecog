package com.bjut.eager.flowerrecog.Ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bjut.eager.flowerrecog.R;

public class SettingsActivity extends Activity {

    EditText input_size;
    TextView picSizeText;
    EditText input_channel;
    TextView picChannelText;
    Button btn_back;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        input_size = (EditText) findViewById(R.id.setPicSize);
        picSizeText = (TextView) findViewById(R.id.picSize);
        input_channel = (EditText) findViewById(R.id.setPicChannel);
        picChannelText = (TextView) findViewById(R.id.picChannel);

        sharedPreferences = getSharedPreferences("NET_CONFIG", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int picSize = sharedPreferences.getInt("PicSize", 227);
        int picChannel = sharedPreferences.getInt("PicChannel", 3);
        picSizeText.setText(picSize + "*" + picSize);
        picChannelText.setText(picChannel + "");

        findViewById(R.id.btn_CfmPicSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_size.getText() != null) {
                    String size = input_size.getText().toString();
                    picSizeText.setText(size + "*" + size);
                    editor.putInt("PicSize", Integer.parseInt(size));
                    editor.commit();
                }
            }
        });

        findViewById(R.id.btn_CfmPicChannel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_channel.getText() != null) {
                    String channel = input_channel.getText().toString();
                    picChannelText.setText(channel);
                    editor.putInt("PicChannel", Integer.parseInt(channel));
                    editor.commit();
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SettingsActivity.this.finish();
            }
        });
    }
}
