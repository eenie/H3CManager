package com.orivon.mob.h3cmanager;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.orhanobut.logger.Logger;
import com.orivon.mob.h3cmanager.bean.MyQueuesBean;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;


/**
 * Created by Eenie on 2016/1/29.
 */
public class H3CApplication extends Application {


    private String h3cCookie = "";
    private HttpConnect http;

    private SPTool spTool;

    private DbManager dbManager;
    NotificationManager nm;


    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("Exam");
        http = new HttpConnect();
        spTool = new SPTool(this);
        setH3cCookie(readCookie("h3cCookie"));
        x.Ext.init(this);
        initDB();

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
                .setDbVersion(2)
                ;
        dbManager = x.getDb(daoConfig);
    }


    public DbManager getDbManager() {
        return dbManager;
    }


    public void save(Object object,AddListener listener) {
        try {
            dbManager.save(object);
            listener.onSuccess();
        } catch (DbException e) {
            listener.onFailure();
        }
    }
    public void save(Object object) {
        try {
            dbManager.save(object);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public MyQueuesBean isExist(MyQueuesBean queuesBean) {
        try {
            MyQueuesBean queues= dbManager.selector(MyQueuesBean.class).where("caseID", "=", queuesBean.getID()).findFirst();
            if (queues!=null) {
                return queues;
            } else {
                return null;
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean isNew(MyQueuesBean queuesBean) {
        try {
            DbModel dbModel = dbManager.selector(MyQueuesBean.class).select("isNew").where("caseID", "=", queuesBean.getID()).findFirst();

            if (dbModel != null) {
                if (dbModel.getBoolean("isNew")) {
                    return true;
                } else {
                    return false;
                }
            }


        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }


public void setOld(MyQueuesBean queuesBean) {
    MyQueuesBean Bean=isExist(queuesBean);
    Bean.setIsNew(false);
    if (Bean != null) {
        try {
            dbManager.update(Bean, "isNew");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}


    public interface AddListener {
        void onSuccess();

        void onFailure();

    }


    public void showNotice(int noticeID,String title,String content) {
        //此Builder为android.support.v4.app.NotificationCompat.Builder中的，下同。
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        //系统收到通知时，状态栏栏上面显示的文字。
        mBuilder.setTicker("你有新的消息！！！");
        //显示在通知栏上的小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        mBuilder.setContentTitle(title);
        //通知内容
        mBuilder.setContentText(content);
        //设置大图标，即通知条上左侧的图片（如果只设置了小图标，则此处会显示小图标）
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //显示在小图标左侧的数字
        //设置为不可清除模式
        mBuilder.setOngoing(true);

        mBuilder.setAutoCancel(true);

        long[] pattern = {800, 1500, 800, 1500};
        mBuilder.setVibrate(pattern);

        //点击通知之后需要跳转的页面
        Intent resultIntent = new Intent(this, CaseInfoListActivity.class);

        //使用TaskStackBuilder为“通知页面”设置返回关系
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //为点击通知后打开的页面设定 返回 页面。（在manifest中指定）
        stackBuilder.addParentStack(CaseInfoListActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);

        //显示通知，id必须不重复，否则新的通知会覆盖旧的通知（利用这一特性，可以对通知进行更新）
        nm.notify(noticeID, mBuilder.build());
    }
}
