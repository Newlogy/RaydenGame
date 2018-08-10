package com.example.raydengame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.raydengame.view.Animation;

/**
 * 代表演员, 即参与的游戏对象(背景, 玩家, 敌机, 子弹, 爆炸等)
 */
public class Actor {
    /**
     * 游戏元素对应的图像和在屏幕上的位置坐标
     */
    protected Bitmap face;
    protected float x;
    protected float y;

    /**
     * 游戏元素的动画效果
     */
    private Animation animEffect;

    public Actor(Bitmap face, float x, float y) {
        this.face = face;
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (animEffect != null) {
            float cx = x + width() / 2;
            float cy = y + height() / 2;
            animEffect.changeAnimPos(cx, cy);
        }
    }

    public void draw(Canvas canvas, float stateTime) {
        canvas.drawBitmap(face, x, y, null);
    }

    public RectF bound() {
        return new RectF(x, y, x + face.getWidth(), y + face.getHeight());
    }

    public float width() {
        return face.getWidth();
    }

    public float height() {
        return face.getHeight();
    }

    public float centerX() {
        return x + face.getWidth() / 2;
    }

    public float centerY() {
        return x + face.getHeight() / 2;
    }

    public void applyAnimEffect(float duration, boolean looping, Bitmap[] effectFrames) {
        animEffect = new Animation(duration, centerX(), centerY(), effectFrames);
        animEffect.setLooping(looping);
    }

    public void showAnimEffect(float stateTime, Canvas canvas) {
        if (animEffect != null) {
            animEffect.draw(stateTime, canvas);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }


}
