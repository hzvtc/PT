package com.as.pt.activity;

import android.app.Activity;
import android.os.Bundle;

import com.as.pt.view.GameView;

public class MainActivity extends Activity {
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        gameView = new GameView(this);
        setContentView(gameView);
    }
}
