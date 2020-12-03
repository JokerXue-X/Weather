package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ShowWeather extends AppCompatActivity {
    TextView infor_province, infor_city, infor_date, infor_temperture, infor_humidity, infor_weather;
    private MyDatabaseHelper dbHelper;//通过借助MyDatabaseHelper对象dbHelper来创建数据库和对数据库进行操
    String nameee, adcodeee;//关注城市的adcode和name

    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new MyDatabaseHelper(this, "Weather.db", null, 2);//创建一个对数据库进行读和写的对象dbHelper
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);
        sendRequestWithOkHttp();


        Button add_follow = (Button) findViewById(R.id.add_follow);
        add_follow.setOnClickListener(new View.OnClickListener() {//关注按钮的点击事件
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象
                //，这里通过这个对象db来对数据库进行增删改查。
                ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
                Cursor cursor = db.rawQuery("select * from FollowCity where followAdcode=?", new String[]{adcodeee});//判断FollowCity关注表中是否有该城市了
                if (cursor.moveToFirst()) {//如果已经关注了该城市则给出提示信息
                    Toast.makeText(ShowWeather.this, "已经关注了，请勿重复关注", Toast.LENGTH_LONG).show();
                } else {//如果未关注，则将关注的城市信息添加到关注表中，并给出信息
                    values.put("followAdcode", adcodeee);
                    values.put("followName", nameee);
                    db.insert("FollowCity", null, values);
                    Toast.makeText(ShowWeather.this, "关注成功", Toast.LENGTH_LONG).show();
                }
                //点击添加按钮之后就会跳转到AddActivi活动来填写作者和日记内容
            }
        });

        Button cancel_follow = (Button) findViewById(R.id.cancel_follow);
        cancel_follow.setOnClickListener(new View.OnClickListener() {//取消关注按钮的点击事件
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象
                //，这里通过这个对象db来对数据库进行增删改查。
                ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
                Cursor cursor = db.rawQuery("select * from FollowCity where followAdcode=?", new String[]{adcodeee});//判断FollowCity关注表中是否有该城市了
                if (cursor.moveToFirst()) {//如果关注了这个城市，则取消该城市的关注
                    db.delete("FollowCity", "followAdcode=?", new String[]{adcodeee});//调用SQLiteDatabase对象的delete()方法在关注表中删除该城市
                    Toast.makeText(ShowWeather.this, "取消关注成功", Toast.LENGTH_LONG).show();
                } else {//如果未关注该城市，则给出信息
                    Toast.makeText(ShowWeather.this, "该城市未关注，请先关注该城市", Toast.LENGTH_LONG).show();
                }
                //点击添加按钮之后就会跳转到AddActivi活动来填写作者和日记内容
            }
        });

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {//取消关注按钮的点击事件
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttp();
                Toast.makeText(ShowWeather.this, "刷新成功", Toast.LENGTH_LONG).show();
            }
        });

        Button show_return = (Button) findViewById(R.id.show_return);
        show_return.setOnClickListener(new View.OnClickListener() {//取消关注按钮的点击事件
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    //当点击查询按钮时会调用该方法
    public void sendRequestWithOkHttp() {//通过OkHttp用来从服务器端获取数据
        Intent intent = getIntent();
        String id = intent.getStringExtra("adcode");
        adcodeee = id;
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://restapi.amap.com/v3/weather/weatherInfo?city=" + id + "&key=50384b83ff5131999997982f2e5c2bff").build();
                //利用传递过来的城市id，让服务器定位到这个城市，得到这个城市的天气数据
                Response response = client.newCall(request).execute();//用response对象接受服务器的响应，服务器传来的数据是JSON格式
                String responseData = Objects.requireNonNull(response.body()).string();//将JSON格式的数据转化为字符串
                Log.d("abc", responseData);
                String changdu = String.valueOf(responseData.length());
                Log.d("abc1", changdu);

                if (responseData.length() == 70) {
                    Log.d("abc", "输入id有误");
                    Toast.makeText(ShowWeather.this, "id有误", Toast.LENGTH_LONG).show();
                    infor_province = (TextView) findViewById(R.id.infor_province);
                    infor_province.setText("输入id有误");

                } else {
                    showInformation(responseData);//然后调用showInformation方法来显示从服务器得来的信息
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showInformation(String responseData) {//通过数据库里面的信息将该城市的天气信息显示出来

        handleResponse(responseData);//先将服务器传来的数据进行解析，并放在数据库中
        Intent intent = getIntent();
        String id = intent.getStringExtra("adcode");
        SQLiteDatabase db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象
        //，这里通过这个对象db来对数据库进行增删改查。
        ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
        Cursor cursor = db.rawQuery("select * from Weather where adcode=?", new String[]{id});
        if (cursor.moveToFirst()) {
            String provinces = cursor.getString(cursor.getColumnIndex("provinces"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            nameee = city;
            String reporttime = cursor.getString(cursor.getColumnIndex("reporttime"));
            String temperature = cursor.getString(cursor.getColumnIndex("temperature"));
            String humidity = cursor.getString(cursor.getColumnIndex("humidity"));
            String weather = cursor.getString(cursor.getColumnIndex("weather"));
            //Log.d("aaa1",provinces + city + reporttime + temperature + humidity);
            infor_province = (TextView) findViewById(R.id.infor_province);
            infor_city = (TextView) findViewById(R.id.infor_city);
            infor_date = (TextView) findViewById(R.id.infor_date);
            infor_temperture = (TextView) findViewById(R.id.infor_temperature);
            infor_humidity = (TextView) findViewById(R.id.infor_humidity);
            infor_weather = (TextView) findViewById(R.id.infor_weather);
            infor_province.setText("省份:" + provinces);
            infor_city.setText("城市:" + city);
            infor_date.setText("更新时间:" + reporttime);
            infor_temperture.setText("温度:" + temperature);
            infor_humidity.setText("湿度:" + humidity);
            infor_weather.setText("天气:" + weather);
        }
    }


    public void handleResponse(String responseData) {//用来解析和处理服务器返回的数据,并将解析之后的数据存放在数据库中
        //Log.d("aaa", responseData);

        /*JSONArray information=new JSONArray(responseData);
        for(int i=0;i<information.length();i++){
            JSONObject infor=information.getJSONObject(i);
            String provinces=infor.getString("provinces");
            //之前是利用这种方法去获得服务器传过来的天气信息的 但是这种方法只适合于城市的JSON数据格式
        }*/


        Information information = JSON.parseObject(responseData, Information.class);//把服务器传来的JOSN数据存入到Information这个类里面
        List<Information.Lives> lives = information.getLives();//然后创建Information类里面一个属性（是一个列表，列表的类型是Lives）的一个对象，然后通过该对象去获取解析后的数据
        String provinces = lives.get(0).getProvince();//获得解析的数据

        //Log.d("aaa",provinces);

        String city = lives.get(0).getCity();
        String adcode = lives.get(0).getAdcode();
        String reporttime = lives.get(0).getReporttime();
        String temperature = lives.get(0).getTemperature();
        String humidity = lives.get(0).getHumidity();
        String weather = lives.get(0).getWeather();
        SQLiteDatabase db = dbHelper.getWritableDatabase();//通过getWritableDatabase()方法会创建SQLiteDatabase对象
        //，这里通过这个对象db来对数据库进行增删改查。
        ContentValues values = new ContentValues();//使用ContentValues对添加的数据进行组装
        //第一次点击查询某个城市的天气时，Weather表里面是没有该城市的天气信息的，因此在获得该城市天气信息后，要进行插入语句
        //在插入该城市的天气信息后，若以后在进行查询时，则不能在insert插入语句了，因为主键约束不能再插入该城市的天气信息，只能进行Update更新该城市的天气信息
        Cursor cursor = db.rawQuery("select * from Weather where adcode=?", new String[]{adcode});//判断Weather里面是否有该城市的天气信息，若没有则进行insert插入语句，若有则进行Update语句。
        if (cursor.moveToFirst()) {//若Weather表里面有该城市的信息则进行更新操作
            values.put("adcode", adcode);
            values.put("provinces", provinces);
            values.put("city", city);
            values.put("reporttime", reporttime);
            values.put("temperature", temperature);
            values.put("humidity", humidity);
            values.put("weather", weather);
            db.update("Weather", values, "adcode=?", new String[]{adcode});

        } else {//没有则进行插入操作
            values.put("adcode", adcode);
            values.put("provinces", provinces);
            Log.d("aaa", provinces);
            values.put("city", city);
            values.put("reporttime", reporttime);
            values.put("temperature", temperature);
            values.put("humidity", humidity);
            values.put("weather", weather);
            //Log.d("aaa2",adcode+provinces);
            db.insert("Weather", null, values);
        }
    }
}