package com.example.raydengame.view;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.raydengame.R;
import com.example.raydengame.dao.PlayerInfoDao;
import com.example.raydengame.entity.PlayerInfo;

import java.util.List;

public class PlayerInfoActivity extends ListActivity {

    private ListView list;
    private PlayerInfoDao dao;
    private PlayerInfoAdapter adapter;
    private List<PlayerInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        adapter = new PlayerInfoAdapter();

        list = getListView();

        dao = new PlayerInfoDao(this);
        data = dao.getAllScore();

        list.setAdapter(adapter);

        //设置返回
        findViewById(R.id.btn_info_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //给ListView设置创建ContextMenu的监听
        /*list.setOnCreateContextMenuListener(this);*/
    }

   /* //创建ContextMenu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //添加2个item
        menu.add(0, 1, 0, "更新");
        menu.add(0, 2, 0, "删除");

        //得到长按的position
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = info.position;

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //得到对应的BlackList对象
        final PlayerInfo playerInfo = data.get(position);
        switch (item.getItemId()) {
            //响应对item的更新操作:
            case 1:
                final EditText editText = new EditText(this);
                editText.setHint(playerInfo.getName() + ": " + playerInfo.getScore());
                new AlertDialog.Builder(this)
                        .setTitle("更新黑名单")
                        .setView(editText)
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //1. 更新List中对应的数据
                                playerInfo.setScore(Integer.parseInt(editText.getText().toString().trim()));
                                //2. 更新数据表中对应的数据
                                dao.update(playerInfo);
                                //3. 通知更新列表
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            //响应对item的删除操作:
            case 2:
                TextView textView = new TextView(this);
                textView.setPadding(10, 10, 10, 10);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setText("要删除的数据为: " + playerInfo.getName());
                textView.setTextSize(18);
                new AlertDialog.Builder(this)
                        .setTitle("确认删除吗？")
                        .setView(textView)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            //2. 在删除的回调方法实现:
                            public void onClick(DialogInterface dialog, int which) {
                                //1). 删除数据表中对应的数据
                                dao.delete(playerInfo.getId());
                                //2). 删除List对应的数据
                                data.remove(position);
                                //3). 通知更新列表
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
        return super.onContextItemSelected(item);
    }*/

    //ListView适配器
    private class PlayerInfoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(PlayerInfoActivity.this, R.layout.player_info_item, null);
            }
            PlayerInfo playerInfo = data.get(position);
            TextView tv_item_name = convertView.findViewById(R.id.tv_item_name);
            TextView tv_item_score = convertView.findViewById(R.id.tv_item_score);

            tv_item_name.setText(playerInfo.getName());
            tv_item_score.setText(playerInfo.getScore() + "");
            return convertView;
        }
    }
}
