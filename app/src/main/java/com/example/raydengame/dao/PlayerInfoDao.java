package com.example.raydengame.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.raydengame.entity.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作player_info表的Dao
 */

public class PlayerInfoDao {
    private static final String TAG = "TAG";
    private DBHelper dbHelper;
    private static String DATABASE_NAME = "info.db";
    private static String TABLE_NAME = "player_info";
    private static int DATABASE_VERSION = 1;

    public PlayerInfoDao(Context context) {
        dbHelper = new DBHelper(context, DATABASE_NAME, DATABASE_VERSION);
    }

    /**
     * 添加一条记录
     *
     * @param playerInfo
     */
    public void add(PlayerInfo playerInfo) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行insert insert into player_info (name, score) values(xxx, xx)
        ContentValues values = new ContentValues();
        values.put("name", playerInfo.getName());
        values.put("score", playerInfo.getScore());
        values.put("password", playerInfo.getPassword());
        long id = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "Id = " + id);
        //3. 设置id
        playerInfo.setId((int) id);
        //4. 关闭
        database.close();
    }

    /**
     * 根据id删除一条数据
     *
     * @param id
     */
    public void delete(int id) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行delete delete from player_info where _id = id
        int deleteCount = database.delete(TABLE_NAME, "_id = ?", new String[]{id + ""});
        Log.d(TAG, "删除了" + deleteCount + "条记录");
        //3. 关闭
        database.close();
    }

    /**
     * 更新一条记录
     *
     * @param playerInfo
     */
    public void update(PlayerInfo playerInfo) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行update update player_info set score = xxx where name = name
        ContentValues values = new ContentValues();
        values.put("score", playerInfo.getScore());
        int updateCount = database.update(TABLE_NAME, values, "name = " + "'" + playerInfo.getName() + "'", null);
        Log.d(TAG, "更新了" + updateCount + "条数据");
        //3. 关闭连接
        database.close();
    }

    /**
     * 查询前十条记录封装成List<PlayerInfo>
     *
     * @return
     */
    public List<PlayerInfo> getAll() {
        List<PlayerInfo> list = new ArrayList<>();
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行query SELECT * FROM [player_info] ORDER BY [score] DESC LIMIT 10 orderBy: "score desc", Integer.toString(10)
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, "score desc", Integer.toString(10));
        //3. 从cursor中取出所有数据并封装到List中
        while (cursor.moveToNext()) {
            //id
            int id = cursor.getInt(0);
            //name
            String name = cursor.getString(1);
            //score
            int score = cursor.getInt(2);
            //password
            String password = cursor.getString(3);
            list.add(new PlayerInfo(id, name, score, password));
        }
        //4. 关闭连接
        cursor.close();
        database.close();

        return list;
    }

    /**
     * 得到前十名成绩封装成List<PlayerInfo>
     *
     * @return
     */
    public List<PlayerInfo> getAllScore() {
        List<PlayerInfo> list = new ArrayList<>();
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行query SELECT name, score FROM [player_info] ORDER BY [score] DESC LIMIT 10 orderBy: "score desc", Integer.toString(10)
        Cursor cursor = database.query(TABLE_NAME, new String[]{"name", "score"}, null, null, null, null, "score desc", Integer.toString(10));
        //3. 从cursor中取出所有数据并封装到List中
        while (cursor.moveToNext()) {
            //name
            String name = cursor.getString(0);
            //score
            int score = cursor.getInt(1);
            list.add(new PlayerInfo(name, score));
        }
        //4. 关闭连接
        cursor.close();
        database.close();

        return list;
    }

    /**
     * 根据incomingName查询记录是否存在
     *
     * @return
     */
    public boolean isNameExist(String incomingName) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行query select name from player_info where name = 'incomingName'
        Cursor cursor = database.query(TABLE_NAME, new String[]{"name"}, "name = ?", new String[]{incomingName}, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            if (name.equals(incomingName)) {
                cursor.close();
                database.close();
                return true;
            } else {
                cursor.close();
                database.close();
                return false;
            }
        }
        return false;
    }

    /**
     * 通过名字得到成绩
     *
     * @param name
     * @return
     */
    public int getScoreByName(String name) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行query select score from player_info where name = 'name'
        Cursor cursor = database.query(TABLE_NAME, new String[]{"score"}, "name = ?", new String[]{name}, null, null, null);
        while (cursor.moveToNext()) {
            int score = cursor.getInt(0);
            //4. 关闭
            cursor.close();
            database.close();
            return score;
        }
        cursor.close();
        database.close();
        return 0;
    }

    /**
     * 判断密码是否正确
     *
     * @return
     */
    public boolean isPassword(String name, String incomingPassword) {
        //1. 得到连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //2. 执行query select name, password from player_info where name = 'name'
        Cursor cursor = database.query(TABLE_NAME, new String[]{"name", "password"}, "name = ?", new String[]{name}, null, null, null);
        while (cursor.moveToNext()) {
            String password = cursor.getString(1);
            if (password.equals(incomingPassword)) {
                cursor.close();
                database.close();
                return true;
            } else {
                cursor.close();
                database.close();
                return false;
            }
        }
        return false;
    }
}
