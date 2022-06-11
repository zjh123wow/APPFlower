package com.example.flower;


import android.content.Context;
import android.content.SharedPreferences;

import java.sql.Timestamp;

//本类主要作为工具类实现我的页面登录与显示信息页面的转换
public class AnalysisUtils {
    public static User u;



    //读取登录状态
    public static boolean readLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
        return isLogin;
    }

    //清除登录状态
    public static void cleanLoginStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin", false);
        editor.putString("loginUserName", "");
        editor.putString("UserInfo", null);
        editor.commit();
    }
    //读取User信息
    public static String getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String user=sharedPreferences.getString("UserInfo",null);
        return user;
    }
    //改变登录信息
    public  static void putUser(Context context,String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //改变登录状态
        editor.putBoolean("isLogin",true);
        editor.putString("UserInfo",user);
        editor.apply();
        //
       // return "a";
    }
    //刚注册的id信息
    public  static void putLastinsert(Context context,int lastinsert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("registerInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("lastinsert",lastinsert);
        editor.apply();
        //
    }
    //读取上次注册id信息
    public static int getinsertid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("registerInfo", Context.MODE_PRIVATE);
        int lastinsert=sharedPreferences.getInt("lastinsert",0);
        return lastinsert;
    }
    //刚注册的id信息
    public  static void putLastmod(Context context,long lastmod) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fileInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastmod",lastmod);
        editor.apply();
        //
    }
    //读取上次注册id信息
    public static long getlastmod(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fileInfo", Context.MODE_PRIVATE);
       long lastinsert=sharedPreferences.getLong("lastmod",0);
        return lastinsert;
    }
    //上传flower上次修改时间
    public  static void putFlowerTimestamp(Context context, Timestamp timestamp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fileInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //改变登录状态
        editor.putString("lastflowerupdate",timestamp.toString());
        editor.apply();
        //
    }
    //读取上次注册id信息
    public static String getFlowerTimestamp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fileInfo", Context.MODE_PRIVATE);
        String lastflowerupdate=sharedPreferences.getString("lastflowerupdate","");
        return lastflowerupdate;
    }
}
