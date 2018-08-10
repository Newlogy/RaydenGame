package com.example.raydengame.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.raydengame.R;

import java.io.IOException;

/**
 * 播放背景音乐的服务
 */

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;

    SharedPreferences sharedPreferences;

    //初始化音乐资源
    @Override
    public void onCreate() {
        try {
            if (mediaPlayer == null) {
                // 创建MediaPlayer对象
                mediaPlayer = new MediaPlayer();
            }
            sharedPreferences = getSharedPreferences("volume", MODE_PRIVATE);
            mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic);
            // 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TAG", "onCreate: 没有找见音乐文件");
            e.printStackTrace();
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //开始播放音乐
        //float musicVolume = intent.getFloatExtra("volume", 1.0F);
        float musicVolume = sharedPreferences.getFloat("musicVolume", 1.0F);
        mediaPlayer.setVolume(musicVolume, musicVolume);
        mediaPlayer.start();
        //音乐播放完毕的事件处理
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                //释放资源
                mp.release();
                return false;
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //服务停止时停止播放音乐并释放资源
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
