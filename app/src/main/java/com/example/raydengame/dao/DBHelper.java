package com.example.raydengame.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 操作数据库的辅助类
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "TAG";

    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DBHelper onCreate: ");
        //创建表
        db.execSQL("create table player_info(_id integer primary key autoincrement, name varchar not null, score integer not null, password varchar not null)");
        //插入一些初始数据
        db.execSQL("insert into player_info (name, score, password) values ('管理者', 1000, 'admin')");
        db.execSQL("insert into player_info (name, score, password) values ('仲裁者', 800, 'Tom')");
        db.execSQL("insert into player_info (name, score, password) values ('探索者', 500, 'Jack')");
        db.execSQL("insert into player_info (name, score, password) values ('圣灵武士', 600, 'Bob')");
        db.execSQL("insert into player_info (name, score, password) values ('探险家', 400, 'Amy')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
