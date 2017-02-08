package com.as.pt.activity;

import android.app.Activity;
import android.os.Bundle;

import com.as.pt.view.GameView;

import java.util.logging.Logger;

import cn.jesse.nativelogger.NLogger;

public class MainActivity extends Activity {
    private GameView gameView;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        NLogger.i(TAG,"onCreate");
        gameView = new GameView(this);
        setContentView(gameView);
    }
}
