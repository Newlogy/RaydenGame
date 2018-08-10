package com.example.raydengame.interfaces;

import android.graphics.Canvas;

/**
 * 所有stage都要实现这个接口以便实现其中的方法
 */
public interface StageAdapter {
    void onPrepare();

    void onFinish();

    boolean update();

    void render(Canvas canvas, float stateTime, int fps);

    void onTouchDown(float x, float y);

    void onTouchMove(float x, float y);

    void onTouchUp(float x, float y);

    interface Sounder {
        void play(String sound);
    }
}

