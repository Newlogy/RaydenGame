package com.example.raydengame.entity;

import android.graphics.Bitmap;

/**
 * 子弹实体类
 */
public class Bullet extends Actor {
    int playerBulletSpeed;
    int enemyBulletSpeed;

    /**
     * 子弹方向（向上和向下）
     */
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_UP = -1;

    /**
     * 子弹行进的方向
     */
    private int direction;

    public Bullet(Bitmap face, float x, float y, int direction) {
        super(face, x, y);
        this.direction = direction;
    }

    @Override
    public void update() {
        super.update();

        if (direction == MOVE_UP) {
            y = y - playerBulletSpeed;
        } else if (direction == MOVE_DOWN) {
            y = y + enemyBulletSpeed;
        }
    }

    public boolean isOutOfScreen(int width, int height) {
        if (direction == MOVE_UP && y < 0 || direction == MOVE_DOWN && y > height) {
            return true;
        }

        return false;
    }

    public boolean hit(Actor actor) {
        return bound().intersect(actor.bound());
    }

    public void setPlayerBulletSpeed(int playerBulletSpeed) {
        this.playerBulletSpeed = playerBulletSpeed;
    }

    public void setEnemyBulletSpeed(int enemyBulletSpeed) {
        this.enemyBulletSpeed = enemyBulletSpeed;
    }

}
