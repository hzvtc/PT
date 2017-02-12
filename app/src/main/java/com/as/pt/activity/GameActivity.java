package com.as.pt.activity;

import android.app.Activity;
import android.os.Bundle;

import com.as.pt.bean.PuzzleCell;
import com.as.pt.bean.PuzzleCellState;
import com.as.pt.config.CommonVar;
import com.as.pt.config.SettingVar;
import com.as.pt.util.BgMusicManager;
import com.as.pt.util.PalLog;
import com.as.pt.util.SPManager;
import com.as.pt.view.GameView;

/**
 * 自定义View的实现
 * Canvas绘图 Bitmap对象 像素单位转换 触屏事件处理 音效播放 SharedPreferences数据保存
 */
public class GameActivity extends Activity {
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


    }





    private void loadGameProgress() {
        PalLog.d(TAG,"loadGameProgress");
        gameView.puzzleCellStates.clear();
        SPManager.getInstance(this).setFileName(CommonVar.FILE_NAME);
        String progress = SPManager.getInstance(this).get("PROGRESS","").toString();
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
        if (SettingVar.isOpenMusic){
            if (BgMusicManager.getInstance(this).isBackgroundMusicPlaying()){
                BgMusicManager.getInstance(this).pauseBackgroundMusic();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SettingVar.isOpenMusic){
            if (BgMusicManager.getInstance(this).isBackgroundMusicPlaying()){
                BgMusicManager.getInstance(this).end();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 加载assets下的音乐文件 无限循环播放音乐
        if (SettingVar.isOpenMusic){
            BgMusicManager.getInstance(this).playBackgroundMusic("music/bg_music.mp3",true);
        }

    }

    private void saveGameProcess() {
        String process="";
        for (PuzzleCell cell:gameView.puzzleCells){
            String s = String.format("%d|%d|%d|%d|%s",cell.imgId,cell.x,cell.y,cell.zOrder,Boolean.toString(cell.isFixed));
            process=process+s+"#";
        }
        SPManager.getInstance(this).setFileName(CommonVar.FILE_NAME);
        SPManager.getInstance(this).put("PROGRESS",process);
    }
}
