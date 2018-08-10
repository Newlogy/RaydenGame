package com.example.raydengame.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.raydengame.R;

public class LoseActivity extends AppCompatActivity {

    //用户名
    private String name;

    //分数
    private int score;
    //杀敌数
    private int kill;

    private TextView tv_lose_score;
    private TextView tv_lose_kill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        tv_lose_score = findViewById(R.id.tv_lose_score);
        tv_lose_kill = findViewById(R.id.tv_lose_kill);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        score = intent.getIntExtra("score", 0);
        kill = intent.getIntExtra("kill", 0);

        tv_lose_score.setText(score + "");
        tv_lose_kill.setText(kill + "");
    }

    //重新挑战
    public void reStart(View view) {
        finish();
    }

    //返回
    public void backF(View view) {
        Intent intent = new Intent(this, RaydenGameActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}
