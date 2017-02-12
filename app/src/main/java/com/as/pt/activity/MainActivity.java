package com.as.pt.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.as.pt.R;
import com.as.pt.util.IntentManager;
import com.as.pt.util.LocalActivityManager;
import com.as.pt.view.GameView;

public class MainActivity extends Activity {
    private LinearLayout startGameLv;
    private LinearLayout settingLv;
    private LinearLayout endGameLv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGameLv = (LinearLayout) findViewById(R.id.startGameLv);
        settingLv = (LinearLayout) findViewById(R.id.settingLv);
        endGameLv = (LinearLayout) findViewById(R.id.exitGameLv);
        startGameLv.setOnClickListener(new onGameControlListener());
        settingLv.setOnClickListener(new onGameControlListener());
        endGameLv.setOnClickListener(new onGameControlListener());
        LocalActivityManager.getInstance(MainActivity.this).pushActivity(this);
    }

    private class onGameControlListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.startGameLv:
                    IntentManager.getInstance(MainActivity.this).startAnotherActivity(GameActivity.class,null);
                    break;
                case R.id.settingLv:
                    IntentManager.getInstance(MainActivity.this).startAnotherActivity(SettingActivity.class,null);
                    break;
                case R.id.exitGameLv:
                    LocalActivityManager.getInstance(MainActivity.this).clearActivity();
                    break;
            }
        }
    }


}
