package com.as.pt.activity;

import android.app.Activity;
import android.os.Bundle;

import com.as.pt.bean.PuzzleCell;
import com.as.pt.bean.PuzzleCellState;
import com.as.pt.util.BgMusicManager;
import com.as.pt.util.PalLog;
import com.as.pt.util.SPUtils;
import com.as.pt.view.GameView;

/**
 * 自定义View的实现
 */
public class MainActivity extends Activity {
    private GameView gameView;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        PalLog.d(TAG,"onCreate");
        gameView = new GameView(this);
        loadGameProgress();
        setContentView(gameView);
        // 加载assets下的音乐文件 无限循环播放音乐
        BgMusicManager.getInstance(this).playBackgroundMusic("music/bg_music.mp3",true);
    }



    private void loadGameProgress() {
        PalLog.d(TAG,"loadGameProgress");
        gameView.puzzleCellStates.clear();
        String progress = SPUtils.get(this,"PROGRESS","").toString();
        if (!progress.equals("")){
            String[] states = progress.split("[#]");
            for (String state:states){
                String[] pros = state.split("[|]");
                PuzzleCellState pcs = new PuzzleCellState();
                pcs.ImgId = Integer.parseInt(pros[0]);
                pcs.posX = Integer.parseInt(pros[1]);
                pcs.posY = Integer.parseInt(pros[2]);
                pcs.zOrder = Integer.parseInt(pros[3]);
                pcs.isFixed = Boolean.parseBoolean(pros[4]);
                gameView.puzzleCellStates.add(pcs);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGameProcess();
        if (BgMusicManager.getInstance(this).isBackgroundMusicPlaying()){
            BgMusicManager.getInstance(this).pauseBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BgMusicManager.getInstance(this).isBackgroundMusicPlaying()){
            BgMusicManager.getInstance(this).end();
        }
    }

    private void saveGameProcess() {
        String process="";
        for (PuzzleCell cell:gameView.puzzleCells){
            String s = String.format("%d|%d|%d|%d|%s",cell.imgId,cell.x,cell.y,cell.zOrder,Boolean.toString(cell.isFixed));
            process=process+s+"#";
        }

        SPUtils.put(this,"PROGRESS",process);
    }
}
