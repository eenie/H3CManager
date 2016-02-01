package com.orivon.mob.h3cmanager;

import android.app.Application;

import com.orhanobut.logger.Logger;

import org.xutils.x;

/**
 * Created by Eenie on 2016/1/29.
 */
public class H3CApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("Exam");

        x.Ext.init(this);


    }
}
