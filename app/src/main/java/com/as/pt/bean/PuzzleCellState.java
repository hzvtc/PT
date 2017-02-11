package com.as.pt.bean;

/**
 * Created by FJQ on 2017/2/8.
 */
public class PuzzleCellState {
    public int ImgId;//拼图块你对应的小图编号
    public int posX;//平图块在频幕上显示位置x坐标
    public int posY;//平图块在频幕上显示位置y坐标
    public int zOrder;//拼图块堆叠的上下次序
    public boolean isFixed;//平图块是否被固定
}
