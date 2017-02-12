package com.as.pt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.as.pt.R;
import com.as.pt.bean.Ball;
import com.as.pt.bean.PuzzleCell;
import com.as.pt.bean.PuzzleCellState;
import com.as.pt.util.DensityUtils;
import com.as.pt.util.PalLog;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by FJQ on 2017/2/7.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG="GameView";
    private Bitmap background;
    private Bitmap puzzleImage; //背景图片 拼图
    private Paint paint;
    private double pw;
    private double ph;
    private Rect puzzRect;
    private Rect thumbRect;
    private Rect cellRect;//拼图区域 缩略图区域 打乱拼图区
    private Context mContext;
    public List<PuzzleCell> puzzleCells = new ArrayList<>();
    //游戏精度保存的拼图块状态动态数组
    public List<PuzzleCellState> puzzleCellStates = new ArrayList<>();
    //触摸的拼图
    private PuzzleCell touchCell;
    private Bitmap backDrawing;//界面后台背景
    private Canvas backCanvas;//界面后台画布
    private int scrrenW;
    private int scrennH;

    private SoundPool soundPool;
    private HashMap<Integer,Integer> soundIdMap;

    private SurfaceHolder holder;
    private boolean finished;

    private Ball ball = new Ball();
    //音频的时间
    private long soundPoolTime;

    private int fixedNum;

    private boolean isTouchDownOrUp;

    private Thread gameUIShowThread;
    public GameView(Context context) {
        super(context);
        Log.d(TAG,"GameView");
        this.mContext=context;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //游戏启动动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(5,1,3,1);
        scaleAnimation.setDuration(800);
        startAnimation(scaleAnimation);
        //拼图归位音效
        initSoundPool();

        holder = this.getHolder();
        holder.addCallback(this);
        //在触摸模式下得到焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        ball.image = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ball);

        isTouchDownOrUp = false;
    }

    private void update(){
        int x = (int)(6*Math.random());
        int y = (int)(4*Math.random());

        ball.x0 = ball.x0+ball.direction*x;
        ball.y0 = ball.y0+ball.direction*y;

        if (ball.x0>scrrenW||ball.y0>scrennH){
            ball.direction = -1;
        }

        if (ball.x0<0||ball.y0<0){
            ball.direction = 1;
        }
    }


    private class GameRender implements Runnable{
        @Override
        public void run() {
            Canvas canvas=null;
            //绘制界面中的线程循环
            long startTime = 0,endTime = 0,frame;
            while (!finished){
                //锁定画布 即当前的绘图对象
                try {
//                    Log.e("GameRender",""+startTime);
                    startTime = System.currentTimeMillis();
                    canvas = holder.lockCanvas();
//                    //解决按下和弹起拼图块一瞬间一边画东西一边显示的问题
//                    // 这句话必须放在canvas = holder.lockCanvas();之后
                    if (isTouchDownOrUp){
                        continue;
                    }
                    if (canvas!=null){
                        //重后台缓存中绘制当前除移动拼图块的图像
                        canvas.drawBitmap(backDrawing, 0, 0, null);
                        if (touchCell != null) {
                            touchCell.onDraw(canvas);
                        }

                        update();
//                        canvas.drawBitmap(ball.image,ball.x0,ball.y0,null);
                    }

                    endTime = System.currentTimeMillis();
                    frame = 1000/(endTime-startTime);
                    canvas.drawText("FPS:"+frame,scrrenW/2,scrennH-10,paint);


                }
                catch (Exception e){

                }
                finally {
                    if (canvas!=null){
                        holder.unlockCanvasAndPost(canvas);
                    }
                    //考虑到刷新UI的时间
                    endTime = System.currentTimeMillis();
                    //让线程每一帧刷新的事件都在17秒 即频率达到60
                    if (endTime-startTime<17){
                        long sleepTime = 17-(endTime-startTime);
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG,"surfaceChanged");
        gameUIShowThread = new Thread(new GameRender());
        finished = false;
        gameUIShowThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        finished = true;
    }

    private void initSoundPool() {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundIdMap = new HashMap<Integer, Integer>();
        soundIdMap.put(1, soundPool.load(mContext, R.raw.ir_begin, 1));
//        soundPoolTime = getSoundTime();
    }

    private int getSoundTime(){
        Field[] fields = R.raw.class.getDeclaredFields();
        int rawId;
        String rawName;
        int duration = 0;
        MediaPlayer mediaPlayer = null;
        try {
            rawId = fields[0].getInt(R.raw.class);
            rawName = fields[0].getName();
            Log.i("getSoundTime", "-----------rawId="+rawId+"----------");
            Uri uri = Uri.parse("android.resource://"+getContext().getPackageName()+"/"+ rawId);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            Log.i("getSoundTime", "-----------duration="+duration+"----------");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (mediaPlayer!=null){
                mediaPlayer = null;
                mediaPlayer.release();
            }
        }
        return duration;
    }

    private void play(int sound, int loop) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = currVolume / maxVolume;
        soundPool.play(soundIdMap.get(sound), volume, volume, 1, loop, 1.0f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG,"onSizeChanged");
        //计算屏幕宽度
        scrrenW = (w > h) ? w : h;
        scrennH = (w > h) ? h : w;
        initGames();
        if (puzzleCellStates.size()>0){
            loadPuzzleCell();
        }
        else {
            makePuzzles();
        }

        drawPuzzle(backCanvas, null);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void loadPuzzleCell() {
        int col,row;
        Rect puzzleRect;
        for (PuzzleCellState pcs:puzzleCellStates){
            row = pcs.ImgId/4;
             col= pcs.ImgId%4;
            puzzleRect = new Rect((int) (col*pw),(int)(row*ph),(int) ((col+1)*pw),(int) ((row+1)*ph));
            PuzzleCell puzzleCell = new PuzzleCell();
            puzzleCell.image = Bitmap.createBitmap(puzzleImage,puzzleRect.left,puzzleRect.top,puzzleRect.width(),puzzleRect.height());
            puzzleCell.imgId = pcs.ImgId;
            puzzleCell.width = (int)pw;
            puzzleCell.height = (int)ph;
            puzzleCell.x = pcs.posX;
            puzzleCell.y = pcs.posY;
            puzzleCell.zOrder = pcs.zOrder;
            puzzleCell.homeX0 = puzzleRect.left+DensityUtils.dp2px(mContext,10);
            puzzleCell.homeY0 = puzzleRect.top+DensityUtils.dp2px(mContext,20);
            puzzleCell.isFixed = pcs.isFixed;
            puzzleCells.add(puzzleCell);
        }

        sortPuzzles();
    }

    private void drawPuzzle(Canvas canvas, PuzzleCell ignoreCell) {
        canvas.drawBitmap(background,0,0,null);
        Paint p = new Paint();
        p.setAlpha(120);
        canvas.drawBitmap(puzzleImage,null,puzzRect,p);
        canvas.drawBitmap(puzzleImage,null,thumbRect,p);
//        canvas.drawRect(cellRect,paint);
//        for (PuzzleCell puzzleCell:puzzleCells){
//            canvas.drawBitmap(puzzleCell.image,cellRect.left,cellRect.top,null);
//        }

        for (int j=puzzleCells.size()-1;j>=0;j--){
            PuzzleCell cell = puzzleCells.get(j);
            //不绘制被触摸的拼图块
           if (cell!=ignoreCell){
               cell.onDraw(canvas);
           }

        }
        canvas.drawRect(puzzRect,paint);
        for (int i=1;i<=2;i++){
            canvas.drawLine(puzzRect.left,(int)(puzzRect.top+ph*i),puzzRect.right,(int)(puzzRect.top+ph*i),paint);
        }

        for (int j=1;j<=3;j++){
            canvas.drawLine((int)(puzzRect.left+pw*j),puzzRect.top,(int)(puzzRect.left+pw*j),puzzRect.bottom,paint);
        }

        isTouchDownOrUp = false;
    }

    private void makePuzzles() {
        fixedNum = 0;
        //将平涂分割成3*4的拼图 保存到puzzleCells
        Rect puzzleRect;
        PuzzleCell puzzleCell;
        Set<Integer> zOrders = new HashSet<>();
        for (int i=0;i<3;i++){
            for (int j=0;j<4;j++){
                puzzleCell = new PuzzleCell();
                puzzleCell.imgId = i*4+j;
                puzzleRect = new Rect((int) (j*pw),(int)(i*ph),(int) ((j+1)*pw),(int) ((i+1)*ph));
                puzzleCell.image = Bitmap.createBitmap(puzzleImage,puzzleRect.left,puzzleRect.top,puzzleRect.width(),puzzleRect.height());
                puzzleCell.width = (int)pw;
                puzzleCell.height = (int)ph;
                puzzleCell.x = cellRect.left+(int) ((Math.random()*cellRect.width()));
                puzzleCell.y = cellRect.top+(int) ((Math.random()*cellRect.height()));
//                Collections.shuffle();
                int order;
                do {
                    order = (int) (Math.random()*12);
                } while (zOrders.contains(order));
                zOrders.add(order);
                puzzleCell.zOrder = order;
                //确定拼图块的贵为区域 并且初始状态为未归位
                puzzleCell.homeX0 = (int) (j*pw)+DensityUtils.dp2px(mContext,10);
                puzzleCell.homeY0 = (int)(i*ph)+DensityUtils.dp2px(mContext,20);
                puzzleCell.isFixed = false;
                puzzleCells.add(puzzleCell);
            }

        }

        sortPuzzles();
    }

    /**
     * Canvas+Bitmap加载游戏背景图片 绘制拼图区域 缩略图区域 打乱拼图区域
     */
    public void initGames(){
        Bitmap bg = BitmapFactory.decodeResource(getResources(), R.mipmap.game_bg);
        background = Bitmap.createScaledBitmap(bg,scrrenW,scrennH,false);
        bg.recycle();
        pw = (scrrenW- DensityUtils.dp2px(mContext,10)*3)/5.5;
        ph = (scrennH-DensityUtils.dp2px(mContext,20)*2)/3.0;
        //计算拼图区域 缩略图区域 打乱拼图块区域
        puzzRect = new Rect(DensityUtils.dp2px(mContext,10),
                DensityUtils.dp2px(mContext,20),
                DensityUtils.dp2px(mContext,10)+(int)(4*pw),
                DensityUtils.dp2px(mContext,20)+(int)(3*ph));

        thumbRect = new Rect(DensityUtils.dp2px(mContext,10)*2+(int)(4*pw),
                DensityUtils.dp2px(mContext,20),
                scrrenW-DensityUtils.dp2px(mContext,10),
                (int) (DensityUtils.dp2px(mContext,20)+ph));

        cellRect = new Rect(DensityUtils.dp2px(mContext,10)*2+(int)(4*pw),
                (int) (DensityUtils.dp2px(mContext,20)+ph+DensityUtils.dp2px(mContext,5)),
                (int) (scrrenW-DensityUtils.dp2px(mContext,10)-pw),
                (int) (scrennH-DensityUtils.dp2px(mContext,20)-ph));
        //加载拼图
        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.mipmap.pic02);
        puzzleImage = Bitmap.createScaledBitmap(pic,puzzRect.width(),puzzRect.height(),false);
        pic.recycle();

        //创建后台界面图像
        backDrawing = Bitmap.createBitmap(scrrenW,scrennH, Bitmap.Config.ARGB_8888);
        backCanvas = new Canvas(backDrawing);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                for (int i=0;i<puzzleCells.size();i++){
                    PuzzleCell cell = puzzleCells.get(i);
                    if (cell.isFixed){
                        continue;
                    }
                    if (cell.isTouch(x,y)){
                        isTouchDownOrUp = true;
                        cell.zOrder = getCellMaxZorder()+1;

                        sortPuzzles();
                        touchCell=cell;
                        touchCell.setTouchedPoint(x,y);
                        //在后台画布上绘制一封干净的界面
                        drawPuzzle(backCanvas,cell);
                        //保存被点击的拼图块 记录触摸位置点

//                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchCell!=null){
                    //拼图原区域
//                    Rect rect1 = new Rect(touchCell.x,touchCell.y,touchCell.x+touchCell.width,touchCell.y+touchCell.height);
                    touchCell.moveTo(x,y);
                    //拼图移动后的区域
//                    Rect rect2 = new Rect(touchCell.x,touchCell.y,touchCell.x+touchCell.width,touchCell.y+touchCell.height);
                    //形成局部重绘区
//                    rect2.union(rect1);
//                    invalidate(rect2);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouchDownOrUp = true;
                if (touchCell!=null){
                    double ds = Math.sqrt((touchCell.x-touchCell.homeX0)*(touchCell.x-touchCell.homeX0)
                            +(touchCell.y-touchCell.homeY0)*(touchCell.y-touchCell.homeY0));
                    if (ds<DensityUtils.dp2px(mContext,10)){
                        touchCell.x = touchCell.homeX0;
                        touchCell.y = touchCell.homeY0;
                        touchCell.isFixed = true;
                        //解决拼图块将要归位时另一拼图块在归位的拼图块区域内 导致部分覆盖或者全部覆盖
                        touchCell.zOrder=-1;
                        sortPuzzles();
                        fixedNum++;
                        //停止背景音乐播放
                        //播放贵为音效
                        play(1,0);
                        if (fixedNum==puzzleCells.size()){
                            Toast.makeText(mContext,"拼图全部归位",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                touchCell=null;
                //解决MotionEvent.ACTION_UP比MotionEvent.ACTION_DOWN invalidate 先执行的问题
                drawPuzzle(backCanvas,null);
//                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void sortPuzzles() {
        //Zorder大的在上面
        Collections.sort(puzzleCells, new Comparator<PuzzleCell>() {
            @Override
            public int compare(PuzzleCell p1, PuzzleCell p2) {
                return p2.zOrder-p1.zOrder;
            }
        });
    }

    private int getCellMaxZorder() {
        int maxZorder=puzzleCells.get(0).zOrder;
        for (int i=1;i<puzzleCells.size();i++){
            PuzzleCell cell = puzzleCells.get(i);
            if (cell.zOrder>maxZorder){
                maxZorder = cell.zOrder;
            }
        }
        return maxZorder;
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //重后台缓存中绘制当前除移动拼图块的图像
//        canvas.drawBitmap(backDrawing,0,0,null);
//        if (touchCell!=null){
//            touchCell.onDraw(canvas);
//        }
//    }


}
