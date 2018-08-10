package com.example.raydengame.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.raydengame.R;

/**
 * 开始界面
 */
public class RaydenGameActivity extends AppCompatActivity {

    //用户名
    private String name;

    private RaydenGameView raydenGameView;
    boolean exitFlag = false;   //标识是否可以退出
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                exitFlag = false;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rayden_game);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        raydenGameView = new RaydenGameView(this, name);
        //退出
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RaydenGameActivity.this, MainActivity.class);
                intent.putExtra("exit", true);
                startActivity(intent);
            }
        });

    }

    //点击后退键退出应用
    @Override
    public void onBackPressed() {
        if (!exitFlag) {
            exitFlag = true;
            Toast.makeText(this, "再点一次退出应用", Toast.LENGTH_SHORT).show();
            //发消息延迟2s将exitFlag=false
            handler.sendEmptyMessageDelayed(1, 2000);
        } else {
            Intent intent = new Intent(RaydenGameActivity.this, MainActivity.class);
            intent.putExtra("exit", true);
            startActivity(intent);
            super.onBackPressed();
        }
    }

    /**
     * 开始游戏
     *
     * @param view
     */
    public void startGame(View view) {
        setContentView(raydenGameView);
    }

    /**
     * 设置
     *
     * @param view
     */
    public void install(View view) {
        startActivity(new Intent(this, InstallActivity.class));
    }

    /**
     * 排行
     *
     * @param view
     */
    public void score(View view) {
        startActivity(new Intent(this, PlayerInfoActivity.class));
    }

}
