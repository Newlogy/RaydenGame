package com.example.raydengame.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raydengame.R;

public class WinActivity extends AppCompatActivity {

    //用户名
    private String name;

    //分数
    private int score;
    //杀敌数
    private int kill;

    private TextView tv_win_score;
    private TextView tv_win_kill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        tv_win_kill = findViewById(R.id.tv_win_kill);
        tv_win_score = findViewById(R.id.tv_win_score);

        Intent intent = getIntent();
        kill = intent.getIntExtra("kill", 0);
        score = intent.getIntExtra("score", 0);

        tv_win_score.setText(score + "");
        tv_win_kill.setText(kill + "");
    }

    //返回
    public void backW(View view) {
        startActivity(new Intent(this, RaydenGameActivity.class));
    }

    //下一关
    public void nextLevel(View view) {
        Toast.makeText(this, "下一关待制作，感谢游玩", Toast.LENGTH_SHORT).show();
    }
}
