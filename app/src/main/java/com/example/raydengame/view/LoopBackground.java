package com.example.raydengame.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.raydengame.entity.Actor;

/**
 * 游戏背景循环移动
 */
public class LoopBackground extends Actor {
    private float screenHeight;
    private float screenSpeed;

    public LoopBackground(Bitmap face, float x, float y, float screenHeight, float screenSpeed) {
        super(face, x, y);
        this.screenSpeed = screenSpeed;
        this.screenHeight = screenHeight;
    }

    @Override
    public void update() {
        // 向下移动背景图(Y坐标增大)
        y = y + screenSpeed;

        // 如果y坐标大于或等于屏幕高度，则恢复至初始位置
        if (y >= screenHeight) {
            y = -(height() - screenHeight);
        }
    }

    @Override
    public void draw(Canvas canvas, float stateTime) {
        // y从初始位置到0，只需绘制一次即可
        super.draw(canvas, stateTime);

        // 当y>0且位于屏幕垂直方向范围内时，需再绘制一次，以填补偏移后的顶部空白
        if (y > 0 && y < screenHeight) {
            canvas.drawBitmap(face, 0, -(height() - y), null);
        }
    }
}
