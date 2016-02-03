package com.orivon.mob.h3cmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import com.orivon.mob.h3cmanager.bean.MyQueuesBean;
import com.orivon.mob.h3cmanager.callback.DataCallBack;
import com.squareup.okhttp.Headers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Eenie on 2016/1/31.
 */
public class CaseService extends Service {

    Context mContext;
    static Vibrator vibrator;

    final String assCaseURL = "http://newcms.h3c.com/hpcms/case/caseList?type=Assigned%20Cases";

    String caseURL = "http://newcms.h3c.com/hpcms/case/queueCaseList?column=&sort=&selectAllCheckBox=on&queueIds=a552af6e5c214996970713b448dd3cb4&queueIds=c368478d38ac4e5aafb709bcc3ea87b1&queueIds=3286d93e2e1a43b68e135c7a1aff3ceb&queueIds=911e08605d564655bf2b69e7af3db71b&queueIds=d0645ed9b1424a3cbb73286b6d2b4296&queueIds=94571150ce1041d09ebd76923c9f8ee2&queueIds=61a51ccd069348d3b653bb319c103960";

    H3CApplication application;
    HttpConnect http;
    Handler handler;
    Headers.Builder builder;

    Intent intent;

    long time = 60000;

    int caseCount = 0;
    int lastCount = 0;

    CaseRunnable caseRunnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onCreate() {

        System.out.println("service");

        application = (H3CApplication) getApplication();
        http = application.getHttp();
        handler = new Handler();
        caseRunnable = new CaseRunnable();
        builder = new Headers.Builder();
        mContext = getApplicationContext();
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        builder.add("Cookie", application.getH3cCookie());
        handler.post(caseRunnable);

        intent = new Intent();
        intent.setAction("com.asscase");


    }


    private class CaseRunnable implements Runnable {

        @Override
        public void run() {

            http.doGet(caseURL, builder.build(),
                    new DataCallBack() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure(int code) {

                        }


                        @Override
                        public void onSuccessful(String response) {
                            parseHtml(response);
                            handler.postDelayed(caseRunnable, time);
                        }

                        @Override
                        public void onFinish() {

                        }

                    });


        }
    }


    public static void startNotice() {
        try {
            long[] pattern = {800, 1500, 800, 1500};
            vibrator.vibrate(pattern, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopNotice() {
        try {
            vibrator.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MyQueuesBean> parseHtml(String content) {
        Document document = Jsoup.parse(content);
        Elements bodyElements = document.getElementsByTag("tbody");
        Element element = bodyElements.get(2);
        Elements comElements = element.getElementsByTag("tr");
        List<MyQueuesBean> myQueuesBeans = new ArrayList<>();
        MyQueuesBean.Builder builder = new MyQueuesBean.Builder();
        Elements countElements = document.getElementsByClass("blue");
        builder.setRecordCount(countElements.get(0).text().trim());
        builder.setPagerCount(countElements.get(2).text().trim());
        for (int i = 0; i < comElements.size(); i++) {
            Elements infos = comElements.get(i).getElementsByTag("td");
            builder.setID(Integer.parseInt(infos.get(0).text()))
                    .setCompany(infos.get(1).text())
                    .setTitle(infos.get(2).text())
                    .setProductDes(infos.get(3).text())
                    .setSLA(infos.get(4).text())
                    .setQueue(infos.get(5).text())
                    .setSeverity(infos.get(6).text())
                    .setProvince(infos.get(7).text())
                    .setAge(infos.get(8).text())
                    .setUrl("");
            final MyQueuesBean myQueuesBean = builder.create();
            application.save(myQueuesBean, new H3CApplication.AddListener() {
                @Override
                public void onSuccess() {

                    System.out.println("save success");
                    startNotice();
                    application.showNotice(1000, "new message!!!", "Total of " + myQueuesBean.getRecordCount() + " records,total  " + myQueuesBean.getPagerCount() + " pages");
                    Intent caseIntent = new Intent();
                    caseIntent.setAction("new case");
                    sendBroadcast(caseIntent);

                }

                @Override
                public void onFailure() {
                    System.out.println("save not success");

                }
            });
            myQueuesBeans.add(myQueuesBean);
        }
        return myQueuesBeans;
    }


}
