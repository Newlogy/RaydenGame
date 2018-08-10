package com.example.raydengame.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raydengame.R;
import com.example.raydengame.dao.PlayerInfoDao;
import com.example.raydengame.entity.PlayerInfo;
import com.example.raydengame.service.MusicService;

public class MainActivity extends AppCompatActivity {
    private PlayerInfoDao dao;
    private EditText et_login_name;
    private EditText et_login_password;
    private TextView tv_login_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new PlayerInfoDao(this);

        et_login_name = findViewById(R.id.et_login_name);
        et_login_password = findViewById(R.id.et_login_password);
        tv_login_register = findViewById(R.id.tv_login_register);

        tv_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_login, null);
                final EditText et_reg_name = view.findViewById(R.id.et_reg_name);
                final EditText et_reg_password = view.findViewById(R.id.et_reg_password);
                final EditText et_reg_confirm = view.findViewById(R.id.et_reg_confirm);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("注册账号")
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = et_reg_name.getText().toString().trim();
                                Log.d("TAG", "onClick: name = " + name);
                                String password = et_reg_password.getText().toString().trim();
                                Log.d("TAG", "onClick: password = " + password);
                                String confirm = et_reg_confirm.getText().toString().trim();
                                if (name.equals("")) {
                                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
                                } else if (password.equals("")) {
                                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                                } else if (password.equals(confirm)) {
                                    if (!dao.isNameExist(name)) {
                                        dao.add(new PlayerInfo(-1, name, 0, password));
                                    } else {
                                        Toast.makeText(MainActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "两次输入密码不一样", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        //播放背景音乐
        startService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MusicService.class));
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean isExit = intent.getBooleanExtra("exit", false);
            if (isExit) {
                this.finish();
            }
        }
    }

    //登录游戏
    public void loginGame(View view) {
        String name = et_login_name.getText().toString().trim();
        String password = et_login_password.getText().toString().trim();
        if (dao.isNameExist(name)) {
            if (dao.isPassword(name, password)) {
                Intent intent = new Intent(this, RaydenGameActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
                et_login_name.setText("");
                et_login_password.setText("");
            }
        } else {
            Toast.makeText(this, "用户不存在，请注册", Toast.LENGTH_SHORT).show();
            et_login_name.setText("");
            et_login_password.setText("");
        }
    }

    //退出游戏
    public void exitGame(View view) {
        finish();
    }
}
