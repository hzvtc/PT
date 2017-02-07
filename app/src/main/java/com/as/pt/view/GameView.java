package com.as.pt.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.as.pt.R;
import com.as.pt.activity.MainActivity;
import com.as.pt.bean.PuzzleCell;
import com.as.pt.util.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by FJQ on 2017/2/7.
 */
public class GameView extends View {

    private Bitmap background;
    private Bitmap puzzleImage; //背景图片 拼图
    private Paint paint;
    private double pw;
    private double ph;
    private Rect puzzRect;
    private Rect thumbRect;
    private Rect cellRect;//拼图区域 缩略图区域 打乱拼图区
    private Context mContext;
    private List<PuzzleCell> puzzleCells = new ArrayList<>();
    //触摸的拼图
    private PuzzleCell touchCell;
    private Bitmap backDrawing;//界面后台背景
    private Canvas backCanvas;//界面后台画布
    private int scrrenW;
    private int scrennH;
    public GameView(Context context) {
        super(context);
        this.mContext=context;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //计算屏幕宽度
        scrrenW = (w > h) ? w : h;
        scrennH = (w > h) ? h : w;
        initGames();
        makePuzzles();
        drawPuzzle(backCanvas);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void drawPuzzle(Canvas canvas) {
        canvas.drawBitmap(background,0,0,null);
        canvas.drawBitmap(puzzleImage,null,puzzRect,null);
        canvas.drawBitmap(puzzleImage,null,thumbRect,null);
//        canvas.drawRect(cellRect,paint);
//        for (PuzzleCell puzzleCell:puzzleCells){
//            canvas.drawBitmap(puzzleCell.image,cellRect.left,cellRect.top,null);
//        }

        for (int j=puzzleCells.size()-1;j>=0;j--){
            PuzzleCell cell = puzzleCells.get(j);
            cell.onDraw(canvas);
        }
        canvas.drawRect(puzzRect,paint);
        for (int i=1;i<=2;i++){
            canvas.drawLine(puzzRect.left,(int)(puzzRect.top+ph*i),puzzRect.right,(int)(puzzRect.top+ph*i),paint);
        }

        for (int j=1;j<=3;j++){
            canvas.drawLine((int)(puzzRect.left+pw*j),puzzRect.top,(int)(puzzRect.left+pw*j),puzzRect.bottom,paint);
        }
    }

    private void makePuzzles() {
        //将平涂分割成3*4的拼图 保存到puzzleCells
        Rect puzzleRect;
        PuzzleCell puzzleCell;
        Set<Integer> zOrders = new HashSet<>();
        for (int i=0;i<3;i++){
            for (int j=0;j<4;j++){
                puzzleCell = new PuzzleCell();
                puzzleRect = new Rect((int) (j*pw),(int)(i*ph),(int) ((j+1)*pw),(int) ((i+1)*ph));
                puzzleCell.image = Bitmap.createBitmap(puzzleImage,puzzleRect.left,puzzleRect.top,puzzleRect.width(),puzzleRect.height());
                puzzleCell.width = (int)pw;
                puzzleCell.height = (int)ph;
                puzzleCell.x = cellRect.left+(int) ((Math.random()*cellRect.width()));
                puzzleCell.y = cellRect.top+(int) ((Math.random()*cellRect.height()));

                int order;
                do {
                    order = (int) (Math.random()*12);
                } while (zOrders.contains(order));
                zOrders.add(order);
                puzzleCell.zOrder = order;
                puzzleCells.add(puzzleCell);
            }

        }

        sortPuzzles();
    }

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
                    if (cell.isTouch(x,y)){
                        cell.zOrder = getCellMaxZorder()+1;
                        touchCell=cell;
                        touchCell.setTouchedPoint(x,y);
                        sortPuzzles();
                        invalidate();
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchCell!=null){
                    //拼图原区域
                    Rect rect1 = new Rect(touchCell.x,touchCell.y,touchCell.x+touchCell.width,touchCell.y+touchCell.height);
                    touchCell.moveTo(x,y);
                    //拼图移动后的区域
                    Rect rect2 = new Rect(touchCell.x,touchCell.y,touchCell.x+touchCell.width,touchCell.y+touchCell.height);
                    //形成局部重绘区
                    rect2.union(rect1);
                    invalidate(rect2);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchCell=null;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //重后台缓存中绘制当前除移动拼图块的图像
        canvas.drawBitmap(backDrawing,0,0,null);
        if (touchCell!=null){
            touchCell.onDraw(canvas);
        }
    }


}
