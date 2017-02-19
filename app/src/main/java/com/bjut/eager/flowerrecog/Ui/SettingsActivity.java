package com.bjut.eager.flowerrecog.Ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bjut.eager.flowerrecog.R;
import com.bjut.eager.flowerrecog.common.constant.Consts;
import com.bjut.eager.flowerrecog.common.constant.PreferenceConsts;
import com.bjut.eager.flowerrecog.common.util.PreferenceUtils;

public class SettingsActivity extends Activity {

    private EditText input_size;
    private TextView picSizeText;
    private EditText input_channel;
    private TextView picChannelText;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        input_size = (EditText) findViewById(R.id.setPicSize);
        picSizeText = (TextView) findViewById(R.id.picSize);
        input_channel = (EditText) findViewById(R.id.setPicChannel);
        picChannelText = (TextView) findViewById(R.id.picChannel);

        int picSize = PreferenceUtils.getInt(PreferenceConsts.PIC_SIZE, PreferenceConsts.DEFAULT_PIC_SIZE);
        int picChannel = PreferenceUtils.getInt(PreferenceConsts.PIC_CHANNEL, PreferenceConsts.DEFAULT_PIC_CHANNEL);
        picSizeText.setText(picSize + "*" + picSize);
        picChannelText.setText(picChannel + "");

        findViewById(R.id.btn_CfmPicSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_size.getText() != null) {
                    String size = input_size.getText().toString();
                    picSizeText.setText(size + "*" + size);
                    PreferenceUtils.putInt(PreferenceConsts.PIC_SIZE, Integer.parseInt(size));
                }
            }
        });

        findViewById(R.id.btn_CfmPicChannel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_channel.getText() != null) {
                    String channel = input_channel.getText().toString();
                    picChannelText.setText(channel);
                    PreferenceUtils.putInt(PreferenceConsts.PIC_CHANNEL, Integer.parseInt(channel));
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
