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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Eenie on 2016/1/31.
 */
public class CaseService extends Service {

    Context mContext;


    final String assCaseURL = "http://newcms.h3c.com/hpcms/case/caseList?type=Assigned%20Cases";

    String caseURL = "http://newcms.h3c.com/hpcms/case/queueCaseList?column=&sort=&selectAllCheckBox=on";
    H3CApplication application;
    HttpConnect http;
    Handler handler;
    Headers.Builder builder;

    Intent intent;

    long time = 60000;

    int caseCount = 0;
    int lastCount = 0;

    Map<String, String> parse = new HashMap<>();

    CaseRunnable caseRunnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onCreate() {

        System.out.println("service start");

        application = (H3CApplication) getApplication();
        http = application.getHttp();
        handler = new Handler();
        caseRunnable = new CaseRunnable();
        builder = new Headers.Builder();
        mContext = getApplicationContext();


        builder.add("Cookie", application.readCookie());
        handler.postDelayed(caseRunnable, 10000);
        intent = new Intent();
        intent.setAction("com.asscase");


    }


    private class CaseRunnable implements Runnable {

        @Override
        public void run() {


            try {
                http.doGet(caseURL + HttpConnect.mapTooString(parse), builder.build(),
                        new DataCallBack() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFailure(int code) {

                            }


                            @Override
                            public void onSuccessful(String response) {


                                try {
                                    parseHtml(response);
                                    handler.postDelayed(caseRunnable, time);

                                } catch (IndexOutOfBoundsException e) {
                                    application.removeCppkie();
                                    application.showNotice(1001, "登录信息过期", "单击重新登录", LoginActivity.class);
                                    application.startNotice();
                                    CaseService.this.stopSelf();
                                }


                            }

                            @Override
                            public void onFinish() {

                            }

                        });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    }




    public List<MyQueuesBean> parseHtml(String content) throws IndexOutOfBoundsException {
        Document document = Jsoup.parse(content);
        Elements bodyElements = document.getElementsByTag("tbody");
        if (bodyElements.size() < 2) {
            throw new IndexOutOfBoundsException("cookie invide");
        }

        Element element = bodyElements.get(2);
        Elements comElements = element.getElementsByTag("tr");
        List<MyQueuesBean> myQueuesBeans = new ArrayList<>();
        MyQueuesBean.Builder builder = new MyQueuesBean.Builder();
        Elements countElements = document.getElementsByClass("blue");
        builder.setRecordCount(countElements.get(0).text().trim());
        builder.setPagerCount(countElements.get(2).text().trim());
        //解析case list
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
                    application.startNotice();
                    application.showNotice(1000, "new message!!!", "Total of " + myQueuesBean.getRecordCount() + " records,total  " + myQueuesBean.getPagerCount() + " pages", CaseInfoListActivity.class);
                    Intent caseIntent = new Intent();
                    caseIntent.setAction("new case");
                    sendBroadcast(caseIntent);

                }

                @Override
                public void onFailure() {


                }
            });
            myQueuesBeans.add(myQueuesBean);
        }


        Elements e = document.getElementsByClass("queues");
        Elements es = e.get(0).getElementsByAttribute("value");
        Elements esTitle = e.get(0).getElementsByTag("li");
        for (int i = 0; i < es.size(); i++) {
            parse.put(es.get(i).attributes().get("value"), esTitle.get(i + 1).text());
        }


        return myQueuesBeans;
    }


}
