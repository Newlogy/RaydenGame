package com.example.raydengame.view;

import android.content.Context;

import com.example.raydengame.R;

/**
 * 实现游戏资源准备, 舞台搭建, 舞台切换
 */
public class RaydenGameView extends GameView {

    public static final String PLAYER_BULLETS_SOUND = "玩家子弹";
    public static final String ENEMY_EXPLOSION_SOUND = "敌机爆炸";

    /*
     * 游戏界面的宽和高尺寸
     */
    private int mScreenWidth;
    private int mScreenHeight;
    private String name;

    private Context context;

    public RaydenGameView(Context context, String name) {
        super(context, name);
        this.name = name;
        this.context = context;
        // 加载音效资源
        setSound("玩家子弹", R.raw.shot);
        setSound("敌机爆炸", R.raw.explosion);
    }

    @Override
    public void initialize(int width, int height) {
        // 保存屏幕大小参数
        mScreenWidth = width;
        mScreenHeight = height;

        // 创建一个舞台，设置其音效播放对象
        Stage stage = new Stage(name, getResources(), width, height, context);
        stage.setSounder(getGameSounder());
        // 将stage作为当前舞台场景
        changeGameStage(stage);
    }
}
