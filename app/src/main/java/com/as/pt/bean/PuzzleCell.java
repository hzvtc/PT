package com.as.pt.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

/**
 * Created by FJQ on 2017/2/7.
 */
public class PuzzleCell {
    public Bitmap image;
    public int imgId;
    public int x;//拼图块在屏幕左上角的位置
    public int y;
    public int width;
    public int height;
    public int zOrder;//决定拼图块的上下堆叠次序
    public Point touchedPoint;

    public int homeX0;
    public int homeY0;
    public boolean isFixed;

    public void setTouchedPoint(int x,int y) {
        if (touchedPoint==null){
            touchedPoint=  new Point(x,y);
        }
        else {
            touchedPoint.set(x,y);
        }

    }

    public void moveTo(int x,int y){
        int dx = x-touchedPoint.x;
        int dy = y-touchedPoint.y;
        this.x = this.x+dx;
        this.y=this.y+dy;
        setTouchedPoint(x,y);
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(image,x,y,null);
    }

    public boolean isTouch(int x1,int y1){
        if (x1>x&&x1<x+width&&y1>y&&y1<y+height){
            return true;
        }
        return false;
    }
}
