package com.orivon.mob.h3cmanager;

import android.app.Application;

import com.orhanobut.logger.Logger;

import org.xutils.DbManager;
import org.xutils.x;


/**
 * Created by Eenie on 2016/1/29.
 */
public class H3CApplication extends Application {


    private String h3cCookie = "";
    private HttpConnect http;

    private SPTool spTool;

    private DbManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("Exam");
        http = new HttpConnect();
        spTool = new SPTool(this);
        setH3cCookie(readCookie("h3cCookie"));
        x.Ext.init(this);
        initDB();
    }

    public String getH3cCookie() {
        return h3cCookie;
    }

    public void setH3cCookie(String h3cCookie) {
        this.h3cCookie = h3cCookie;
    }


    public HttpConnect getHttp() {
        return http;
    }


    public void writeCookie(String h3cCookie) {
        spTool.setString("h3cCookie", h3cCookie);
    }

    public String readCookie(String key) {
        return spTool.getString(key);
    }

    public void removeCppkie() {
        spTool.removeKey("h3cCookie");
    }


    public void initDB() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("h3c.db")
                .setDbVersion(2);
        dbManager = x.getDb(daoConfig);
    }


    public DbManager getDbManager() {
        return dbManager;
    }
}
