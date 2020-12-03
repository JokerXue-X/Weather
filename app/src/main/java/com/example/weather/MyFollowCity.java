package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyFollowCity extends AppCompatActivity {

    private List<FollowCity> followList = new ArrayList<>();
    private MyDatabaseHelper dbHelper;//通过借助MyDatabaseHelper对象dbHelper来创建数据库和对数据库进行操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new MyDatabaseHelper(this, "Weather.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_follow_city);


        Button follow_return = (Button) findViewById(R.id.follow_return);
        follow_return.setOnClickListener(new View.OnClickListener() {//取消关注按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    protected void onResume() {
        init();//为followList获得数据
        FollowCityAdapter adapter = new FollowCityAdapter(MyFollowCity.this, R.layout.city_view,followList);//创建自定义的适配器，其中ListView的子布局为city_view，其中的数据为关注城市的数据
        ListView listView = (ListView) findViewById(R.id.list_view_follow);
        listView.setAdapter(adapter);//配置适配器

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设计关注城市列表的点击事件，通过将关注列表中城市的adcode传过去然后在ShowWeather显示
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(MyFollowCity.this,ShowWeather.class);
                String follow_adcode =followList.get(position).getFollowAdcode();
                intent.putExtra("adcode",follow_adcode);//将关注城市的adcode传过去，利用它进行显示关注城市的信息
                startActivity(intent);
            }
        });

        super.onResume();//更新数据库即刷新
    }


    private void init() {//为关注列表添加关注城市的数据
        followList.clear();
        //dbHelper = new MyDatabaseHelper(this, "Weather.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        SQLiteDatabase db = dbHelper.getWritableDatabase();//这里通过这个对象db来对数据库进行增删改查。

        Cursor cursor = db.query("FollowCity", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String followName = cursor.getString(cursor.getColumnIndex("followName"));//获得followname这一列的值
                String followAdcode = cursor.getString(cursor.getColumnIndex("followAdcode"));
                FollowCity followCity = new FollowCity(followName, followAdcode);//通过获得的属性创建diary对象然后放到diary_list列表然后在ListView显示
                followList.add(followCity);//获得数据库里面每一行的作者和创建时间然后创造diary对象并它放到diary_list列表里面然后为ListView添加数据
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


}


class FollowCity {//自定义一个类City
    private String followName;
    private String followAdcode;

    public FollowCity(String followName, String followAdcode) {
        this.followName = followName;
        this.followAdcode = followAdcode;
    }

    public String getFollowName() {
        return followName;
    }

    public void setFollowName(String followName) {
        this.followName = followName;
    }

    public String getFollowAdcode() {
        return followAdcode;
    }

    public void setFollowAdcode(String followAdcode) {
        this.followAdcode = followAdcode;
    }
}


class FollowCityAdapter extends ArrayAdapter<FollowCity> {//创建一个自定义适配器，其类型为FollowCity，此适配器用来显示关注的省份城市
    private int resourceId;

    public FollowCityAdapter(Context context, int textViewResourceId, List<FollowCity> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//重新getView方法用来返回布局
        FollowCity followcity = getItem(position);//获得当前滚到屏幕类的实例，然后将这个实例加载到界面上
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView TextView_city = (TextView) view.findViewById(R.id.TextView_city);
        TextView_city.setText(followcity.getFollowName());
        return view;
    }


}