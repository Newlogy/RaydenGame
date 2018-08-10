package com.example.raydengame.entity;

import android.graphics.Bitmap;

/**
 * 敌机实体类
 */
public class Enemy extends Actor {
    /**
     * 敌机最多每隔180帧发射一颗子弹
     */
    private static final int FIRE_TICKER_MAX_COUNT = 60 * 3;

    /**
     * 敌机跟踪玩家的目标位置和步长
     */
    private float targetX;
    private float trackStep;

    /**
     * 发射子弹的延时帧数(随机产生)
     */
    private int fireTicker;

    /**
     * 敌机发射的子弹外观
     */
    private Bitmap bulletFace;

    /**
     * 敌机被消灭时，奖励给玩家的积分值
     */
    private int bonusVal;
    private float speed;


    public Enemy(Bitmap face, float x, float y) {
        super(face, x, y);
        fireTicker = (int) (FIRE_TICKER_MAX_COUNT * Math.random());
    }

    public Bullet onFire() {
        // 为避免在每一帧循环中发射子弹，这里进行延时计数
        fireTicker = fireTicker - 1;
        if (fireTicker <= 0) {
            // 为下一次发射子弹作准备，产生一个0-180之间的随机数，即下一次是隔多少帧后发射子弹
            fireTicker = (int) (FIRE_TICKER_MAX_COUNT * Math.random());

            // 子弹在敌机下部的中间位置发射
            float bx = x + width() / 2 - bulletFace.getWidth() / 2;
            float by = y + height();
            return new Bullet(bulletFace, bx, by, Bullet.MOVE_DOWN);
        }

        return null;
    }

    @Override
    public void update() {
        super.update();

        // 敌机向下移动
        y = y + speed;

        // 处理敌机水平方向的跟踪(如果跟踪速度小于0.2，则不进行跟踪了)
        if (trackStep > 0.2f && x != targetX) {
            if (x < targetX) {
                x = x + trackStep;
            } else {
                x = x - trackStep;
            }
        }
    }

    public boolean isOutOfScreen(int width, int height) {
        if (x > width) {
            return true;
        }

        if (y > height) {
            return true;
        }

        return false;
    }

    public void setBulletFace(Bitmap bulletFace) {
        this.bulletFace = bulletFace;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public void setTrackStep(float trackStep) {
        this.trackStep = trackStep;
    }

    public int getBonusVal() {
        return bonusVal;
    }

    public void setBonusVal(int bonusVal) {
        this.bonusVal = bonusVal;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
