package com.example.raydengame.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Animation {
    private Bitmap[] keyFrames;  // 动画图像帧
    private float cx;           // 动画图像帧的中心点位置
    private float cy;
    private float duration;     // 播放动画的持续时长
    private float frameTime;    // 分配到每帧图像的时长
    private float t0;            // 动画播放开始的时间点
    private boolean stopped;    // 动画播放是否已停止
    private boolean looping;    // 是否循环播放动画

    /**
     * @param duration  动画播放的持续时间，单位为秒
     * @param cx        动画帧的中心点横坐标
     * @param cy        动画帧的中心点纵坐标
     * @param keyFrames 帧图像序列
     */
    public Animation(float duration, float cx, float cy, Bitmap... keyFrames) {
        this.duration = duration;
        this.cx = cx;
        this.cy = cy;
        this.keyFrames = keyFrames;

        frameTime = duration / keyFrames.length;
        t0 = -1F;
    }

    /**
     * 根据游戏运行的时间获取动画关键帧
     *
     * @param stateTime 游戏流淌的时间，可以理解为时间轴，单位为秒
     * @param looping   是否需要重复播放动画
     * @return
     */
    private Bitmap getKeyFrame(float stateTime, boolean looping) {
        // 记录动画播放的起始时间戳
        if (t0 == -1F) {
            t0 = stateTime;
        }

        // 根据经历的时间，获取某个图像帧
        int frameNumber = (int) ((stateTime - t0) / frameTime);

        if (!looping) {
            // 非循环播放动画时，最多到最后一帧
            frameNumber = Math.min(keyFrames.length - 1, frameNumber);
            // 在非循环模式下，如果已经超出动画播放的时间，则设置停止标志
            if (stateTime - t0 >= duration) {
                stopped = true;
            }
        } else {
            // 循环播放动画时，反复取得第0帧到最后一帧中的图像
            frameNumber = frameNumber % keyFrames.length;
        }

        return keyFrames[frameNumber];
    }

    /**
     * 绘制动画帧
     *
     * @param stateTime 流淌过的时间
     * @param canvas    画布
     */
    public void draw(float stateTime, Canvas canvas) {
        Bitmap frame = getKeyFrame(stateTime, looping);
        canvas.drawBitmap(frame, cx - frame.getWidth() / 2, cy - frame.getHeight() / 2, null);
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void changeAnimPos(float cx, float cy) {
        this.cx = cx;
        this.cy = cy;
    }

}

