package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //编写建表语句，并把它变成一个字符串常量
    public static final String CREATE_Weather= "create table Weather("
            + "adcode text primary key ,"//建表
            + "provinces text,"
            + "weather text,"
            + "city text,"
            + "reporttime text,"
            + "temperature text,"
            + "humidity text )";


    public static final String CREATE_City = "create table FollowCity("
            + "followAdcode String primary key ,"//自动将id项加一，并且不用输入
            + "followName String )";



    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //重写SQLiteOpenHelper的构造方法，四个参数
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//重写SQLiteOpenHelper两个抽象方法onCreat()和onUpgrade()
        db.execSQL(CREATE_Weather);
        db.execSQL(CREATE_City);
        //当通过创建MyDatabaseHelper对象，并调用getWritableDatabase()方法时，
        // 首先检测到数据库里面有没有Weather.db数据库，如果没有就会调用onCreate()方法创建一个Diary.db数据库和Diary表，若有则不执行onCreate()方法
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
