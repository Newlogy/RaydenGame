package com.example.raydengame.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;

import com.example.raydengame.R;
import com.example.raydengame.dao.PlayerInfoDao;
import com.example.raydengame.entity.Boss;
import com.example.raydengame.entity.Bullet;
import com.example.raydengame.entity.Enemy;
import com.example.raydengame.entity.Player;
import com.example.raydengame.entity.PlayerInfo;
import com.example.raydengame.interfaces.StageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 实现舞台功能, 舞台中的若干Actor共同构建游戏场景
 * 通过StageAdapter和RaydenGameView进行适配
 */
public class Stage implements StageAdapter {
    private Resources mResources;

    /**
     * 数据库操作对象
     */
    private PlayerInfoDao dao;
    /**
     * 用户名
     */
    private String name;
    /**
     * 杀敌数
     */
    private int mKill;

    /**
     * 当前舞台的宽和高
     */
    private int mWidth;
    private int mHeight;

    /**
     * 指示当前舞台是否结束
     */
    private boolean mExited;

    /**
     * 产生的敌机数量
     */
    private int enemyCount;

    /**
     * 当前舞台中用到的图像资源
     */
    private Bitmap mBgImage;           // 背景图像
    private Bitmap mPlayerImage;       // 玩家平飞图像
    private Bitmap mPlayerLeftImage;  // 玩家左移图像
    private Bitmap mPlayerRightImage; // 玩家右移图像
    private Bitmap mPlayerBulletImage;// 玩家子弹图像
    private Bitmap[] mPlayerEffectFrames;// 玩家“跟随”动画效果帧图像
    private Bitmap[] mPlayerTrailFrames;// 玩家“环绕轨迹”动画效果帧图像
    private Bitmap[] mEnemyImages;     // 敌机的图像
    private Bitmap mBossImage;  //Boss图像
    private Bitmap mEnemyBulletImage; // 普通敌机发射的子弹
    private Bitmap mBossBulletImage; //BOSS子弹图像
    private Bitmap mEnemyBigBulletImage;// 重型敌机发射的子弹
    private Bitmap[] mExplosionFrames;  // 普通敌机爆炸的帧动画图像
    private Bitmap[] mBigExplosionFrames; // 重型敌机爆炸的帧动画图像

    /**
     * 背景
     */
    private LoopBackground mBackground;

    /**
     * 玩家
     */
    private Player mPlayer;

    /**
     * 玩家飞机发射的子弹
     */
    private List<Bullet> mPlayerBullets = new ArrayList<>();

    /**
     * BOSS发射的子弹
     */
    private List<Bullet> mBossBullets = new ArrayList<>();

    /**
     * 子弹速度
     */
    private int playerBulletSpeed;
    private int enemyBulletSpeed;
    private int bossBulletSpeed;

    /**
     * 敌机
     */
    private List<Enemy> mEnemyList = Collections.synchronizedList(new ArrayList<Enemy>());

    /**
     * Boss
     */
    private Boss mBoss;

    /**
     * 敌机发射的子弹
     */
    private List<Bullet> mEnemyBullets = new ArrayList<>();

    /**
     * 敌机爆炸效果（多个敌机可以同时出现爆炸现象）
     */
    private List<Animation> mEnemyExplosions = new ArrayList<>();

    private Sounder sounder;

    /**
     * 当前舞台中显示玩家信息的画笔
     */
    private Paint mInfoPaint;
    private Context context;
    private boolean bossShow;


    /*
     * 在游戏顶部显示彩色半透明区域的画笔
     * private Paint mBarPaint;
     */


    public Stage(String name, Resources res, int width, int height, Context context) {
        // Resources.getSystem()是获取到系统资源，如果要加载应用程序自己的资源，则需要传递一个应用程序的res参数进来
        mResources = res;

        this.name = name;

        this.context = context;

        dao = new PlayerInfoDao(context);

        mWidth = width;
        mHeight = height;

        mInfoPaint = new Paint();
        mInfoPaint.setColor(Color.BLUE);
        mInfoPaint.setTextSize(mHeight / 25);

        /*// 自定义画笔，以绘制半透明的条带效果
        mBarPaint = new Paint();
        mBarPaint.setColor(Color.CYAN);
        mBarPaint.setAlpha(120);
        mBarPaint.setStyle(Paint.Style.FILL_AND_STROKE);*/
    }

    public void setSounder(Sounder sounder) {
        this.sounder = sounder;
    }

    @Override
    public void onPrepare() {
        // 加载图像资源
        loadGameImages();
        // 根据屏幕宽高限制，初始化连续移动的背景图
        initBackground();
        // 初始化玩家，必须在加载游戏图像资源后调用
        initPlayer();
        // 当前舞台准备上演
        mExited = false;

        //设置子弹速度
        playerBulletSpeed = mHeight / 90;
        bossBulletSpeed = mHeight / 100;
        enemyBulletSpeed = mHeight / 120;

        // 启动产生敌机的线程
        new Thread(new EnemyGenerator()).start();
    }

    @Override
    public void onFinish() {
        // 当前舞台表演结束
        mExited = true;
    }

    @Override
    public void onTouchDown(float x, float y) {
        mPlayer.setTargetXY(x, y);
        mPlayer.setMoveAllowed(true);
        mPlayer.setFiring(true);
    }

    @Override
    public void onTouchMove(float x, float y) {
        mPlayer.setTargetXY(x, y);
    }

    @Override
    public void onTouchUp(float x, float y) {
        mPlayer.setMoveAllowed(false);
        mPlayer.setFiring(false);
    }

    /**
     * 初始化无限连续移动的背景
     */
    private void initBackground() {
        /*
         背景图缩放：背景图宽度等于界面宽度，高度不小于界面高度
          */
        Bitmap bgBmp = mBgImage;
        // 比例
        float ratio = ((float) mWidth) / bgBmp.getWidth();
        // 高度按宽度相同的比例缩放
        int dstHeight = (int) (bgBmp.getHeight() * ratio);
        // 如果按比例缩放后的高度仍比屏幕高度小，则缩放后的高度设为屏幕高度了
        if (dstHeight < mHeight) {
            dstHeight = mHeight;
        }
        mBgImage = Bitmap.createScaledBitmap(bgBmp, mWidth, dstHeight, false);

        // 计算背景图在屏幕上显示的初始位置Y坐标
        float offsetY = -(mBgImage.getHeight() - mHeight);
        // 初始化背景对象
        mBackground = new LoopBackground(mBgImage, 0, offsetY, mHeight, mHeight / 1000);
    }

    /**
     * 初始化玩家
     */
    private void initPlayer() {
        // 设置玩家初始位置在屏幕的底部中央，为避免飞机被触摸时的手指盖住，将飞机向上偏移40
        int x0 = mWidth / 2 - mPlayerImage.getWidth() / 2;
        int y0 = mHeight - 40 - mPlayerImage.getHeight();
        mPlayer = new Player(mPlayerImage, mPlayerLeftImage, mPlayerRightImage, x0, y0);
        mPlayer.setBulletFace(mPlayerBulletImage);
        mPlayer.setTrailFrames(mPlayerTrailFrames);
        mPlayer.applyAnimEffect(mPlayerEffectFrames);
        mPlayer.setSpeed(mHeight / 140);
    }

    /**
     * 初始化BOSS
     */
    private void initBoss() {
        Bitmap face = mBossImage;
        int x0 = mWidth / 2;
        int y0 = -face.getHeight();
        int maxY = -face.getHeight() + mHeight / 10;
        mBoss = new Boss(face, x0, y0, maxY, mHeight / 160, 50);
        // 设置Boss子弹样式
        mBoss.setBulletFace(mBossBulletImage);
        mBoss.setTrackStep(1);
        mBoss.setBonusVal(10);
        bossShow = true;
    }

    /**
     * 更新画面
     */
    @Override
    public boolean update() {
        mBackground.update();
        if (!mExited) {
            updatePlayer();
            updateEnemies();
            updateBulletsOfAll();
            updateEnemyExplosions();
            if (bossShow) {
                updateBoss();
            }
            return false;
        }
        return true;
    }


    /**
     * 绘制游戏中的内容
     *
     * @param canvas
     * @param stateTime
     * @param fps
     */
    @Override
    public void render(Canvas canvas, float stateTime, int fps) {
        // 显示移动的背景
        mBackground.draw(canvas, stateTime);

        // 显示所有的敌机
        synchronized (mEnemyList) {
            for (Enemy enemy : mEnemyList) {
                enemy.draw(canvas, stateTime);
            }
        }

        // 显示所有敌机发射的子弹
        for (Bullet bullet : mEnemyBullets) {
            bullet.draw(canvas, stateTime);
        }

        //显示所有BOSS发射的子弹
        for (Bullet bullet : mBossBullets) {
            bullet.draw(canvas, stateTime);
        }

        // 显示玩家
        mPlayer.draw(canvas, stateTime);

        // 显示BOSS
        if (bossShow) {
            mBoss.draw(canvas, stateTime);
        }

        // 显示玩家发射出的子弹
        for (Bullet bullet : mPlayerBullets) {
            bullet.draw(canvas, stateTime);
        }

        // 显示敌机被击中的爆炸效果
        for (Animation anim : mEnemyExplosions) {
            anim.draw(stateTime, canvas);
        }

        /*// 绘制一条半透明的彩色条带（位于顶部）
        canvas.drawRect(0, 0, mWidth / 3 + 10, 55, mBarPaint);*/
        // 显示玩家的信息和帧率
        canvas.drawText("当前得分：" + mPlayer.getBonus(), 0, mHeight / 20, mInfoPaint);
        canvas.drawText("FPS: " + fps, mWidth / 2 - mHeight / 25 * 2, mHeight, mInfoPaint);
        canvas.drawText("生命: " + mPlayer.getLife(), mWidth - mHeight / 25 * 3, mHeight / 20, mInfoPaint);
    }

    //更新玩家
    private void updatePlayer() {
        mPlayer.update();
        // 如果玩家发射了子弹，则新增一颗子弹
        Bullet bullet = mPlayer.onFire();
        if (bullet != null) {
            bullet.setPlayerBulletSpeed(playerBulletSpeed);
            mPlayerBullets.add(bullet);
            // 播放玩家子弹射击的声音
            sounder.play(RaydenGameView.PLAYER_BULLETS_SOUND);
        }

        //如果玩家死了就转到失败界面
        if (mPlayer.getLife() <= 0) {
            onFinish();
            float duration = (float) (0.2 + Math.random() * 0.5);
            Bitmap[] explFrames = mExplosionFrames;
            Animation explostion = new Animation(duration,
                    mPlayer.getX() + mPlayer.bound().width() / 2,
                    mPlayer.getY() + mPlayer.bound().height() / 2, explFrames);
            mEnemyExplosions.add(explostion);
            if (dao.getScoreByName(name) < mPlayer.getBonus()) {
                dao.update(new PlayerInfo(-1, name, mPlayer.getBonus(), null));
            }
            Intent intent = new Intent(context, LoseActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("score", mPlayer.getBonus());
            intent.putExtra("kill", mKill);
            context.startActivity(intent);
        }
    }

    //更新Boss
    private void updateBoss() {
        // 如果BOSS发射了子弹，则新增一颗子弹
        Bullet bullet = mBoss.onFire();
        // 及时修改跟踪目标（因为玩家飞机随时可能都在变化位置）
        mBoss.setTargetX(mPlayer.getX());
        mBoss.update();
        if (bullet != null) {
            bullet.setEnemyBulletSpeed(bossBulletSpeed);
            mBossBullets.add(bullet);
            // 播放BOSS子弹射击的声音
            sounder.play(RaydenGameView.PLAYER_BULLETS_SOUND);
        }
        //如果Boss死了就转到获胜界面
        if (mBoss.getLife() <= 0) {
            float duration = (float) (0.2 + Math.random() * 0.5);
            Bitmap[] explFrames = mExplosionFrames;
            Animation explostion = new Animation(duration,
                    mBoss.getX() + mBoss.bound().width() / 2,
                    mBoss.getY() + mBoss.bound().height() / 2, explFrames);
            mEnemyExplosions.add(explostion);
            onFinish();
            if (dao.getScoreByName(name) < mPlayer.getBonus()) {
                dao.update(new PlayerInfo(-1, name, mPlayer.getBonus(), null));
            }
            Intent intent = new Intent(context, WinActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("score", mPlayer.getBonus());
            intent.putExtra("kill", mKill);
            context.startActivity(intent);
        }
    }

    //更新敌机
    private void updateEnemies() {
        List<Enemy> goneEnemies = new ArrayList<>();
        synchronized (mEnemyList) {
            for (Enemy enemy : mEnemyList) {
                // 更新敌机的状态
                enemy.update();

                // 检查敌机是否超出屏幕范围，如果是则加入“删除队列”
                if (enemy.isOutOfScreen(mWidth, mHeight)) {
                    goneEnemies.add(enemy);
                }

                // 及时修改跟踪目标（因为玩家飞机随时可能都在变化位置）
                enemy.setTargetX(mPlayer.getX());

                // 根据需要，处理敌机子弹的发射工作
                Bullet bullet = enemy.onFire();
                if (bullet != null) {
                    bullet.setEnemyBulletSpeed(enemyBulletSpeed);
                    mEnemyBullets.add(bullet);
                }
            }

            // 将超出屏幕范围的敌机移除
            mEnemyList.removeAll(goneEnemies);
        }
    }

    //更新所有子弹状态
    private void updateBulletsOfAll() {
        List<Bullet> goneBullets = new ArrayList<>();
        List<Enemy> goneEnemies = new ArrayList<>();

        // 更新玩家子弹状态，使其不断向上移动，一旦超出屏幕范围就加入“移除队列”
        goneBullets.clear();
        for (Bullet bullet : mPlayerBullets) {
            bullet.update();
            if (bullet.isOutOfScreen(mWidth, mHeight)) {
                goneBullets.add(bullet);
            }
        }
        // 移除超出屏幕范围的玩家子弹
        mPlayerBullets.removeAll(goneBullets);

        // 更新BOSS子弹状态，使其不断向下移动，一旦超出屏幕范围就加入“移除队列”
        goneBullets.clear();
        for (Bullet bullet : mBossBullets) {
            bullet.update();
            if (bullet.isOutOfScreen(mWidth, mHeight)) {
                goneBullets.add(bullet);
            }
        }
        // 移除超出屏幕范围的玩家子弹
        mBossBullets.removeAll(goneBullets);

        // 更新敌机子弹状态，使其不断向下移动，一旦超出屏幕范围就加入“移除队列”
        goneBullets.clear();
        for (Bullet bullet : mEnemyBullets) {
            bullet.update();
            if (bullet.isOutOfScreen(mWidth, mHeight)) {
                goneBullets.add(bullet);
            }
        }
        mEnemyBullets.removeAll(goneBullets);

        // 判断玩家子弹是否撞到敌机，如果击中则消灭敌机，然后将当前子弹和敌机一起移除
        goneBullets.clear();
        goneEnemies.clear();
        synchronized (mEnemyList) {
            for (Enemy enemy : mEnemyList) {
                for (Bullet bullet : mPlayerBullets) {
                    if (bullet.hit(enemy)) {
                        // 分别将当前敌机和子弹加入“移除队列”
                        goneEnemies.add(enemy);
                        goneBullets.add(bullet);
                        //杀敌数+1
                        mKill++;
                        // 根据所击中的敌机，给予玩家积分奖励
                        mPlayer.incBonus(enemy.getBonusVal());

                        // 初始化爆炸动画的持续时间（时长为0.2-0.7之间，重型敌机再延长0.3秒）和帧图像
                        float duration = (float) (0.2 + Math.random() * 0.5);
                        Bitmap[] explFrames = mExplosionFrames;
                        if (enemy.getBonusVal() > 3) {
                            duration = duration + 0.3f;
                            explFrames = mBigExplosionFrames;
                        }
                        // 添加敌机爆炸效果动画
                        Animation explostion = new Animation(duration,
                                enemy.getX() + enemy.bound().width() / 2,
                                enemy.getY() + enemy.bound().height() / 2, explFrames);
                        mEnemyExplosions.add(explostion);

                        // 播放敌机爆炸音效
                        sounder.play(RaydenGameView.ENEMY_EXPLOSION_SOUND);

                        // 检查下一个敌机是否被玩家子弹击中
                        break;
                    }
                }
            }
            // 判断玩家子弹是否撞到BOSS，如果击中则BOSS生命值减少，然后将当前子弹和敌机一起移除
            if (bossShow) {
                for (Bullet bullet : mPlayerBullets) {
                    if (bullet.hit(mBoss)) {
                        goneBullets.add(bullet);
                        //Boss被玩家飞机子弹击中，则BOSS生命值减2
                        mBoss.reduceLife(2);
                        // 根据所击中的敌机，给予玩家积分奖励
                        mPlayer.incBonus(mBoss.getBonusVal());
                    }
                }
            }
            // 将碰撞的玩家子弹和敌机一起从屏幕上移除
            mPlayerBullets.removeAll(goneBullets);
            mEnemyList.removeAll(goneEnemies);
        }

        // 判断敌机子弹是否击中了玩家
        goneBullets.clear();
        for (Bullet bullet : mEnemyBullets) {
            if (bullet.hit(mPlayer)) {
                goneBullets.add(bullet);
                // 玩家被敌机子弹击中，则玩家生命值减少1
                mPlayer.reduceLife(1);
            }
        }
        mEnemyBullets.removeAll(goneBullets);

        // 判断BOSS子弹是否击中了玩家
        goneBullets.clear();
        for (Bullet bullet : mBossBullets) {
            if (bullet.hit(mPlayer)) {
                goneBullets.add(bullet);
                // 玩家被BOSS子弹击中，则玩家生命值减少2
                mPlayer.reduceLife(2);
            }
        }
        mBossBullets.removeAll(goneBullets);
    }

    public void updateEnemyExplosions() {
        List<Animation> stoppedAnims = new ArrayList<>();

        for (Animation anim : mEnemyExplosions) {
            // 如果某个爆炸动画一架播放结束，则将其放入删除队列
            if (anim.isStopped()) {
                stoppedAnims.add(anim);
            }
        }
        // 删除播放完的动画对象
        mEnemyExplosions.removeAll(stoppedAnims);
    }

    class EnemyGenerator implements Runnable {
        @Override
        public void run() {
            while (!mExited) {
                //产生的敌机数量增加
                enemyCount++;
                // 从敌机图像数组中随机选取一个，下标为0-5，其中0为重型敌机
                int n = (int) (6 * Math.random());
                Bitmap face = mEnemyImages[n];

                // 确定敌机的初始随机位置
                int x0 = (int) (mWidth * Math.random());
                int y0 = (int) (-face.getHeight() + (Math.random() * 5));

                // 创建敌机对象
                Enemy enemy = new Enemy(face, x0, y0);

                enemy.setSpeed(mHeight / 240);

                // 设置敌机发射的子弹图片（重型敌机发射的子弹和普通敌机不一样）
                if (n == 0) {
                    enemy.setBulletFace(mEnemyBigBulletImage);
                    // 设定重型敌机被击中的积分奖励为5分
                    enemy.setBonusVal(5);
                } else {
                    enemy.setBulletFace(mEnemyBulletImage);
                    // 设定普通敌机被击中的积分奖励为3分
                    enemy.setBonusVal(3);
                }

                // 设置敌机跟踪玩家的速度，即每一帧水平移动的步长(0-0.5)
                enemy.setTrackStep((float) (Math.random()));

                synchronized (mEnemyList) {
                    // 将新产生的敌机加入到集合数组
                    mEnemyList.add(enemy);
                }
                if (enemyCount >= 120 && !bossShow) {
                    initBoss();
                }
                // 随机休眠一小段时间(相隔200-1200毫秒)，避免产生敌机的速度太快
                SystemClock.sleep(200 + (int) (1000 * Math.random()));
            }
        }
    }

    /**
     * 载入游戏运行用到的图像资源
     */
    private void loadGameImages() {
        mBgImage = BitmapFactory.decodeResource(mResources, R.mipmap.img_bg_level_1);

        //玩家图像
        mPlayerImage = BitmapFactory.decodeResource(mResources, R.mipmap.player);
        mPlayerLeftImage = BitmapFactory.decodeResource(mResources, R.mipmap.player_left);
        mPlayerRightImage = BitmapFactory.decodeResource(mResources, R.mipmap.player_right);
        mPlayerBulletImage = BitmapFactory.decodeResource(mResources, R.mipmap.bullet1);

        // 敌机图像，其中mEnemyImages[0]是一架重型敌机，其他几个是普通的小型敌机
        mEnemyImages = new Bitmap[6];
        mEnemyImages[0] = BitmapFactory.decodeResource(mResources, R.mipmap.e1);
        mEnemyImages[1] = BitmapFactory.decodeResource(mResources, R.mipmap.e2);
        mEnemyImages[2] = BitmapFactory.decodeResource(mResources, R.mipmap.e3);
        mEnemyImages[3] = BitmapFactory.decodeResource(mResources, R.mipmap.e4);
        mEnemyImages[4] = BitmapFactory.decodeResource(mResources, R.mipmap.e5);
        mEnemyImages[5] = BitmapFactory.decodeResource(mResources, R.mipmap.e6);
        // BOSS图像
        mBossImage = BitmapFactory.decodeResource(mResources, R.mipmap.boss2);
        // 敌机子弹图像和重型敌机的子弹图像
        mEnemyBulletImage = BitmapFactory.decodeResource(mResources, R.mipmap.bullet2);
        mEnemyBigBulletImage = BitmapFactory.decodeResource(mResources, R.mipmap.bullet3);

        mBossBulletImage = BitmapFactory.decodeResource(mResources, R.mipmap.boss_bullet1);

        // 爆炸效果图像
        mExplosionFrames = new Bitmap[6];
        mExplosionFrames[0] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_1);
        mExplosionFrames[1] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_2);
        mExplosionFrames[2] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_3);
        mExplosionFrames[3] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_4);
        mExplosionFrames[4] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_5);
        mExplosionFrames[5] = BitmapFactory.decodeResource(mResources, R.mipmap.bomb_enemy_6);
        mBigExplosionFrames = new Bitmap[8];
        mBigExplosionFrames[0] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion01);
        mBigExplosionFrames[1] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion02);
        mBigExplosionFrames[2] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion03);
        mBigExplosionFrames[3] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion04);
        mBigExplosionFrames[4] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion05);
        mBigExplosionFrames[5] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion06);
        mBigExplosionFrames[6] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion07);
        mBigExplosionFrames[7] = BitmapFactory.decodeResource(mResources, R.mipmap.explosion08);

        // 玩家的“跟随”动画和“环绕”动画效果图像
        mPlayerEffectFrames = new Bitmap[15];
        mPlayerTrailFrames = new Bitmap[16];
        int[] effectFrames = {
                R.mipmap.player_eff01, R.mipmap.player_eff02, R.mipmap.player_eff03,
                R.mipmap.player_eff04, R.mipmap.player_eff05, R.mipmap.player_eff06,
                R.mipmap.player_eff07, R.mipmap.player_eff08, R.mipmap.player_eff09,
                R.mipmap.player_eff10, R.mipmap.player_eff11, R.mipmap.player_eff12,
                R.mipmap.player_eff13, R.mipmap.player_eff14, R.mipmap.player_eff15,
        };
        int[] trailFrames = {
                R.mipmap.s0, R.mipmap.s1, R.mipmap.s2, R.mipmap.s3, R.mipmap.s4, R.mipmap.s5,
                R.mipmap.s6, R.mipmap.s7, R.mipmap.s8, R.mipmap.s9, R.mipmap.s10, R.mipmap.s11,
                R.mipmap.s12, R.mipmap.s13, R.mipmap.s14, R.mipmap.s15,
        };
        for (int i = 0; i < effectFrames.length; i++) {
            mPlayerEffectFrames[i] = BitmapFactory.decodeResource(mResources, effectFrames[i]);
        }
        for (int i = 0; i < trailFrames.length; i++) {
            mPlayerTrailFrames[i] = BitmapFactory.decodeResource(mResources, trailFrames[i]);
        }

    }


}
