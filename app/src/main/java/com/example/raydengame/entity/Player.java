package com.example.raydengame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.raydengame.view.Animation;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家飞机类
 */
public class Player extends Actor {
    private static final int FIRE_TICKER_COUNT = 10; // 每10次循环发射一颗子弹

    /**
     * 玩家对应的飞机图像(平飞、左移、右移)
     */
    private Bitmap flyingFace;
    private Bitmap leftFace;
    private Bitmap rightFace;

    /**
     * 移动的速度
     */
    private int speed;

    /**
     * 玩家在屏幕上的移动目标（触摸位置）、是否允许移动
     */
    private float targetX;
    private float targetY;
    private boolean moveAllowed;

    /**
     * 玩家发射的子弹图像、是否正在发射子弹
     */
    private Bitmap bulletFace;
    private boolean firing;

    /**
     * 玩家发射子弹的延时计数
     */
    private int fireTicker;

    /**
     * 玩家的生命值和击中敌机的积分奖励
     */
    private int life;
    private int bonus;

    /**
     * 玩家“环绕”的动画效果
     */
    private Bitmap[] trailFrames;
    private List<Animation> animsTrail = new ArrayList<>();


    public Player(Bitmap flyingFace, float x, float y) {
        super(flyingFace, x, y);
        this.flyingFace = flyingFace;
        this.fireTicker = FIRE_TICKER_COUNT;
        this.life = 5;
    }

    public Player(Bitmap flyingFace, Bitmap leftFace, Bitmap rightFace, float x, float y) {
        this(flyingFace, x, y);
        this.leftFace = leftFace;
        this.rightFace = rightFace;
    }

    /**
     * 玩家执行子弹发射工作（从玩家飞机的顶部中间位置射出）
     *
     * @return 如果需要发射子弹，则返回一个子弹对象，否则返回null
     */
    public Bullet onFire() {
        if (!firing) {
            return null;
        }

        // 为避免在每一帧循环中发射子弹，这里进行延时计数，每秒60帧，每过10帧就发射一次子弹
        fireTicker = fireTicker - 1;
        if (fireTicker <= 0) {
            // 为下一次发射子弹的延时计数做准备
            fireTicker = FIRE_TICKER_COUNT;

            // 子弹从玩家飞机的上部中间位置发射出来
            float bx = x + width() / 2 - bulletFace.getWidth() / 2;
            float by = y - bulletFace.getHeight();
            return new Bullet(bulletFace, bx, by, Bullet.MOVE_UP);
        }

        return null;
    }

    @Override
    public void update() {
        super.update();

        // 根据玩家在目标位置的左边还是右边, 上边还是下边，分别对玩家横坐标值或纵坐标值增或减
        if (moveAllowed) {
            // 向下飞
            if (y > targetY) {
                y = y - speed;
                // 向上飞过头则直接到达目标位置
                if (y < targetY) {
                    y = targetY;
                }
                // 将玩家的图像替换为右移的图像
                if (x < targetX) {
                    face = (rightFace != null) ? rightFace : flyingFace;
                    // 恢复玩家的平飞图像
                    if (x > targetX) {
                        face = flyingFace;
                    }
                }
            } else if (y < targetY) {
                y = y + speed;
                if (y > targetY) {
                    y = targetY;
                }
                // 将玩家的图像替换为左移的图像
                if (x > targetX) {
                    face = (leftFace != null) ? leftFace : flyingFace;
                    if (x < targetX) {
                        // 恢复玩家的平飞图像
                        face = flyingFace;
                    }
                }
            }
            if (x < targetX) {
                // 将玩家的图像替换为右移的图像
                face = (rightFace != null) ? rightFace : flyingFace;
                x = x + speed;
                // 如果向右移动过头了，则直接到达目标位置
                if (x > targetX) {
                    x = targetX;
                    // 恢复玩家的平飞图像
                    face = flyingFace;
                }
            } else if (x > targetX) {
                // 将玩家的图像替换为左移的图像
                face = (leftFace != null) ? leftFace : flyingFace;
                x = x - speed;
                // 如果向左移动过头了，则直接到达目标位置
                if (x < targetX) {
                    x = targetX;
                    // 恢复玩家的平飞图像
                    face = flyingFace;
                }
            }
        } else {
            // 恢复玩家的平飞图像
            if (face != flyingFace) {
                face = flyingFace;
            }
        }

        if (!firing) {
            // 创建玩家的“环绕”动画效果
            double alpha = Math.PI * 2 * Math.random();
            float w = width();
            float h = height();
            // 玩家的中心点坐标
            float cx0 = x + w / 2;
            float cy0 = y + h / 2;
            // 设环绕直径为w+h，这样保证了飞机在环绕动画的轨迹圆弧之内
            float r = (w + h) / 2.0f;
            float cx = cx0 + r * (float) Math.cos(alpha);
            float cy = cy0 - r * (float) Math.sin(alpha);
            animsTrail.add(new Animation(0.4f, cx, cy, trailFrames));
        }

        // 将已经播放完的“环绕”动画对象移除掉
        List<Animation> goneAnims = new ArrayList<>();
        for (Animation anim : animsTrail) {
            if (anim.isStopped()) {
                goneAnims.add(anim);
            }
        }
        animsTrail.removeAll(goneAnims);
    }

    public void draw(Canvas canvas, float stateTime) {
        if (firing) {
            showAnimEffect(stateTime, canvas);
        }

        for (Animation anim : animsTrail) {
            anim.draw(stateTime, canvas);
        }

        //canvas.drawBitmap(face, x, y, null);
        super.draw(canvas, stateTime);
    }

    public void reduceLife(int val) {
        this.life = this.life - val;
    }

    public void incBonus(int val) {
        this.bonus = this.bonus + val;
    }

    public void applyAnimEffect(Bitmap[] effectFrames) {
        applyAnimEffect(0.4f, true, effectFrames);
        //animEffect = new Animation(0.4f, centerX(), centerY(), effectFrames);
        //animEffect.setLooping(true);
    }

    public void setTargetXY(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void setMoveAllowed(boolean moveAllowed) {
        this.moveAllowed = moveAllowed;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
        if (firing) {
            fireTicker = 0;
        }
    }

    public void setBulletFace(Bitmap bulletFace) {
        this.bulletFace = bulletFace;
    }

    public int getLife() {
        return life;
    }

    public int getBonus() {
        return bonus;
    }

    public void setTrailFrames(Bitmap[] trailFrames) {
        this.trailFrames = trailFrames;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
