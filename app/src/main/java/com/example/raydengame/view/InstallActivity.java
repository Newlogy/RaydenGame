package com.example.raydengame.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.raydengame.R;
import com.example.raydengame.service.MusicService;

/**
 * 设置界面
 */
public class InstallActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private SeekBar sb_install_music_volume;
    private SeekBar sb_install_sound_volume;

    private SharedPreferences sharedPreferences;

    private float musicVolume;
    private float soundVolume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        // 得到sharedPreferences对象
        sharedPreferences = getSharedPreferences("volume", MODE_PRIVATE);
        //初始化视图对象
        sb_install_music_volume = findViewById(R.id.sb_install_music_volume);
        sb_install_sound_volume = findViewById(R.id.sb_install_sound_volume);
        //设置进度条为当前音量
        sb_install_music_volume.setProgress((int) (sharedPreferences.getFloat("musicVolume", 1.0F) * 10));
        sb_install_sound_volume.setProgress((int) (sharedPreferences.getFloat("soundVolume", 1.0F) * 10));
        //设置进度条最大值
        sb_install_music_volume.setMax(10);
        sb_install_sound_volume.setMax(10);
        //设置当进度改变时的监听
        sb_install_music_volume.setOnSeekBarChangeListener(this);
        sb_install_sound_volume.setOnSeekBarChangeListener(this);

        Log.d("TAG", "musicVolume = " + (int) (sharedPreferences.getFloat("musicVolume", 1.0F) * 10));
        Log.d("TAG", "soundVolume = " + (int) (sharedPreferences.getFloat("soundVolume", 1.0F) * 10));

        findViewById(R.id.ib_install_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 得到editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (seekBar == sb_install_music_volume) {
            Intent intent = new Intent(InstallActivity.this, MusicService.class);
            //获得进度并转变为音量需要的Float类型
            musicVolume = sb_install_music_volume.getProgress() / 10.0F;
            //intent.putExtra("volume", musicVolume);
            editor.putFloat("musicVolume", musicVolume);
            startService(intent);
        }
        if (seekBar == sb_install_sound_volume) {
            soundVolume = sb_install_sound_volume.getProgress() / 10.0F;
            editor.putFloat("soundVolume", soundVolume);
        }
        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        /*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("musicVolume", musicVolume);
        editor.putFloat("soundVolume", soundVolume);
        editor.apply();*/
    }
}
