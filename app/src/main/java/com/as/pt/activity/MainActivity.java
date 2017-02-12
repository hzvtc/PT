package com.as.pt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.as.pt.R;
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

    }

    private class onGameControlListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.startGameLv:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, GameActivity.class);
                    startActivity(intent);
                    //设置切换动画，从右边进入，左边退出
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    break;
                case R.id.settingLv:
                    break;
                case R.id.exitGameLv:
                    break;
            }
        }
    }
}
