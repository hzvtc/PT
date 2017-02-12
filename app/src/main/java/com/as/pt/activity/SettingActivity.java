package com.as.pt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.as.pt.R;
import com.as.pt.config.CommonVar;
import com.as.pt.config.SettingVar;
import com.as.pt.util.SPManager;

public class SettingActivity extends Activity {
    private Switch isOpenMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LoadConfigInfo();
        isOpenMusic = (Switch) findViewById(R.id.isOpenMusic);
        isOpenMusic.setChecked(SettingVar.isOpenMusic);
        isOpenMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    SettingVar.isOpenMusic = true;
                }
                else {
                    SettingVar.isOpenMusic = false;
                }
            }
        });
    }

    private void LoadConfigInfo() {
        SPManager.getInstance(this).setFileName(CommonVar.FILE_NAME_CONFIG);
        SettingVar.isOpenMusic = (Boolean) SPManager.getInstance(this).get(CommonVar.FILE_NAME_CONFIG,false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveConfigInfo();
    }

    private void saveConfigInfo() {
        SPManager.getInstance(this).setFileName(CommonVar.FILE_NAME_CONFIG);
        SPManager.getInstance(this).put(CommonVar.FILE_NAME_CONFIG,SettingVar.isOpenMusic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
