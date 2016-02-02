package com.orivon.mob.h3cmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import com.orivon.mob.h3cmanager.callback.DataCallBack;
import com.squareup.okhttp.Headers;


/**
 * Created by Eenie on 2016/1/31.
 *
 */
public class CaseService extends Service {

    Context mContext;
    Vibrator vibrator;

    final String assCaseURL = "http://newcms.h3c.com/hpcms/case/caseList?type=Assigned%20Cases";
    H3CApplication application;
    HttpConnect http;
    Handler handler;
    Headers.Builder builder;

    Intent intent;

    long time = 10000;

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

            http.doGet(assCaseURL, builder.build(),
                    new DataCallBack() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure(int code) {

                        }


                        @Override
                        public void onSuccessful(String response) {



                            caseCount = getRecordeCount(response);
                            intent.putExtra("caseCount", caseCount);
                            sendBroadcast(intent);


                            if (caseCount > lastCount) {
                                startNoitice();
                            } else {

                            }


                            lastCount = caseCount;

                            handler.postDelayed(caseRunnable, time);

                        }

                        @Override
                        public void onFinish() {

                        }

                    });


        }
    }


    private int getRecordeCount(String content) {
        String str = "Total of <i class=\"blue\">";
        int startCount = content.indexOf(str) + str.length();
        int endCount = content.indexOf("</i> records");
        return Integer.parseInt(content.substring(startCount, endCount));
    }


    public void startNoitice() {
        try {

            long[] pattern = {800, 1500, 800, 1500};
            vibrator.vibrate(pattern, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
