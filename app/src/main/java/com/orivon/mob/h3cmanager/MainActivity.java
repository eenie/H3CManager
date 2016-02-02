package com.orivon.mob.h3cmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.orivon.mob.h3cmanager.bean.MyQueuesBean;
import com.orivon.mob.h3cmanager.callback.BitmapCallBack;
import com.orivon.mob.h3cmanager.callback.DataCallBack;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editName, editPwd, editKaptcha;
    Button btnLogin;
    HttpConnect httpConnect;
    ImageView imgKapt;
    final String kaptURL = "http://newcms.h3c.com/hpcms/kaptcha";
    final String loginURL = "http://newcms.h3c.com/hpcms/login.jsp";
    final String caseURL = "http://newcms.h3c.com/hpcms/case/caseList?type=Assigned%20Cases";

    String h3cCookie;
    final String Referer = "http://newcms.h3c.com/hpcms/login.jsp";

    SharedPreferences sp;
    NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("cache", 0);

        initView();
        httpConnect = new HttpConnect();

        loadKapt();

         nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private void initView() {
        editName = (EditText) findViewById(R.id.editName);
        editPwd = (EditText) findViewById(R.id.editPwd);
        editKaptcha = (EditText) findViewById(R.id.editKaptcha);
        imgKapt = (ImageView) findViewById(R.id.imgKapt);
        imgKapt.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.btnLogin:


                login();

                break;

            case R.id.imgKapt:


                loadKapt();

                break;

        }







    }

    public String getCaseHtml() {
        AssetManager manager = getAssets();
        StringBuffer sb = null;
        try {
            InputStream is = manager.open("case.txt");
            InputStreamReader reader = new InputStreamReader(is);
            sb = new StringBuffer();
            char[] c = new char[1024];
            int len = reader.read(c);
            while (len > 0) {
                len = reader.read(c);
                sb.append(new String(c));
            }
            is.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public List<MyQueuesBean> parseHtml(String url) {
        Document document = Jsoup.parse(url);
        Elements bodyElements = document.getElementsByTag("tbody");
        Element element = bodyElements.get(2);
        Elements comElements = element.getElementsByTag("tr");
        List<MyQueuesBean> myQueuesBeans = new ArrayList<>();
        for (int i = 0; i < comElements.size(); i++) {
            Elements infos = comElements.get(i).getElementsByTag("td");
            MyQueuesBean.Builder builder = new MyQueuesBean.Builder();
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
            MyQueuesBean myQueuesBean = builder.create();
            myQueuesBeans.add(myQueuesBean);
        }
        return myQueuesBeans;
    }


    public void loadKapt() {

        httpConnect.doGet(kaptURL, new BitmapCallBack() {
            @Override
            public void onSuccessful(Bitmap bitmap) {
                imgKapt.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(int code) {
                System.out.println(code);
            }


            @Override
            public void onResponse(Response response) throws IOException {
                super.onResponse(response);
                h3cCookie = response.header("Set-Cookie").split(";")[0];
                System.out.println("h3cCookie->" + h3cCookie);
            }
        });


    }


    public void login() {

        Headers.Builder build = new Headers.Builder();
        build.add("Referer", "http://newcms.h3c.com/hpcms/login.jsp");
        build.add("Origin", "http://newcms.h3c.com");
        build.add("Cookie", h3cCookie);

        Map<String, String> parse = new HashMap<>();

        parse.put("kaptcha", editKaptcha.getText().toString().trim());

        parse.put("password", editPwd.getText().toString().trim());
        parse.put("userName", editName.getText().toString().trim());
        httpConnect.doPost(
                "http://newcms.h3c.com/hpcms/login",
                parse,
                HttpConnect.formReq,
                build.build(),
                new DataCallBack() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure(int code) {

                    }

                    @Override
                    public void onSuccessful(String response) {

                    }


                    @Override
                    public void onFinish() {

                    }


                    @Override
                    public void onResponse(Response response) throws IOException {

                        if (response.header("Content-Length").equals("172")) {
                            Intent caseIntent = new Intent(MainActivity.this, CaseInfoListActivity.class);
                            caseIntent.putExtra("h3cCookie", h3cCookie);




                            startActivity(caseIntent);
                            finish();
                        } else {
                            System.out.println("登录失败");
                        }


                    }


                    @Override
                    public void onFailure(Request request, IOException e) {
                        System.out.println("no");
                    }
                }
        );


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
