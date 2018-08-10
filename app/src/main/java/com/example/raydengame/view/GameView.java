package com.example.raydengame.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.raydengame.interfaces.StageAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 完成通用游戏界面功能
 */
public abstract class GameView extends SurfaceView {
    //用户名
    private String name;
    //屏幕高度和宽度
    int mWidth;
    int mHeight;
    // 1秒60帧的,每帧耗时16.7毫秒
    private static final float FRAME_TIME = 1000.0F / 60;

    //用于操纵Surface进行绘图，绘图结束后提交给系统以便立即显示
    private SurfaceHolder mHolder;

    //指示当前游戏是否结束
    private boolean mFinished;

    //音效池和音效资源
    private SoundPool mSoundPool;
    private Map<String, Integer> mSoundMap = new HashMap<>();

    // 音效播放器，用来传递给Stage使用
    private StageAdapter.Sounder mSounder = new StageAdapter.Sounder() {
        @Override
        public void play(String sound) {
            playSound(sound);
        }
    };

    // 当前上演的“舞台”
    private StageAdapter mStage;

    //SharedPreferences对象, 用来获取音效值
    SharedPreferences sharedPreferences;

    public GameView(Context context, String name) {
        super(context);
        this.name = name;
        // 获得当前的SurfaceHolder对象，并设置回调接口参数，以介入SurfaceView的创建、启动和退出过程
        mHolder = getHolder();
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Rect surfaceFrame = mHolder.getSurfaceFrame();
                mWidth = surfaceFrame.width();
                mHeight = surfaceFrame.height();
                mFinished = false;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                initialize(mWidth, mHeight);
                // 启动一个界面刷新线程
                new Thread(new GameRender()).start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mFinished = true;
                // 释放子类的其他游戏资源
                dispose();
            }
        });

        // 得到sharedPreferences对象
        sharedPreferences = context.getSharedPreferences("volume", Context.MODE_PRIVATE);

        // 设置触屏模式下自动获取焦点，否则会导致在手机上无法触摸操作
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        // 设置在游戏运行期间，屏幕为常亮，不熄屏
        setKeepScreenOn(true);
    }

    // 子类必须实现initialize方法，以便创建初始的舞台
    public abstract void initialize(int width, int height);

    public void dispose() {
        mStage.onFinish();
    }

    public void update() {
        mFinished = mStage.update();
    }

    public void render(Canvas canvas, float stateTime, int fps) {
        mStage.render(canvas, stateTime, fps);
    }

    public void touchDown(float x, float y) {
        mStage.onTouchDown(x, y);
    }

    public void touchMove(float x, float y) {
        mStage.onTouchMove(x, y);
    }

    public void touchUp(float x, float y) {
        mStage.onTouchUp(x, y);
    }

    /**
     * 设置游戏中用到的音效
     */
    public void setSound(String soundName, int resId) {
        // 如果音效还未初始化，则先初始化音效池对象
        if (mSoundPool == null) {
            // SoundPool的三个参数分别是可同时播放的音效数、音效类型和播放质量
            mSoundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 100);
        }
        int soundId = mSoundPool.load(getContext(), resId, 1);
        mSoundMap.put(soundName, soundId);
    }

    public void playSound(String sound) {
        // 获取系统的声音服务
        AudioManager mgr = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (mgr != null) {
            //float currVol = 0;
            // 得到系统当前的音量和最大音量
            //currVol = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            //float maxVol = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //float volume = currVol / maxVol;
            float volume = sharedPreferences.getFloat("soundVolume", 1.0F);
            int soundId = mSoundMap.get(sound);
            // 播放音效的四个参数分别是音效的id、左声道音量、右声道音量、优先级、循环方式、回放
            mSoundPool.play(soundId, volume, volume, 1, 0, 1.0f);
        }
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                break;
        }
        // 当前View声明处理触屏事件，否则会导致后续的触屏事件的传递中断
        return true;
    }

    //绘制界面
    private class GameRender implements Runnable {
        @Override
        public void run() {
            float stateTime = 0.0f;
            int fps = 0;

            while (!mFinished) {
                // 取得绘图开始的时间点
                long tick0 = System.currentTimeMillis();

                Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {

                    // -------更新游戏元素的状态----------
                    update();

                    // -------渲染游戏元素----------
                    render(canvas, stateTime, fps);

                    // 计算FPS并适当延时，确保每帧为16.7ms(帧率为60帧)
                    long tick1 = System.currentTimeMillis();
                    float deltaTime = tick1 - tick0;
                    if (deltaTime < FRAME_TIME) {
                        SystemClock.sleep((int) (FRAME_TIME - deltaTime));
                        deltaTime = FRAME_TIME;
                    }

                    mHolder.unlockCanvasAndPost(canvas);

                    stateTime = stateTime + deltaTime / 1000.0f;
                    fps = (int) (1000.0F / deltaTime);
                }
            }
        }
    }

    public void changeGameStage(StageAdapter stage) {
        // 设置当前场景(要实现场景切换，如通关，直接改变舞台对象就行了)
        mStage = stage;
        mStage.onPrepare();
    }

    public StageAdapter.Sounder getGameSounder() {
        return mSounder;
    }

    /**
     * 将dip转换为当前设备的px值
     */
    public static int dip2px(float dip) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * 将sp转换为px绝对像素值
     */
    public static int sp2px(float sp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }
}
