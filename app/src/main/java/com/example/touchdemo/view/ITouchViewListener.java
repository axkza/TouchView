package com.example.touchdemo.view;

public interface ITouchViewListener {
    void onTouch(boolean touch); // 是否触摸屏幕
    void onMove(int offsetY); // 手指在屏幕的位置
}
