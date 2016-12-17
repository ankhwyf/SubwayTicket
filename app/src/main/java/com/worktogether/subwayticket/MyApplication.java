package com.worktogether.subwayticket;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created by Ankhwyf on 16/12/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //默认初始化
        Bmob.initialize(this,"9ea43370e3823c7bc07ba1f9e851088a");
    }
}
