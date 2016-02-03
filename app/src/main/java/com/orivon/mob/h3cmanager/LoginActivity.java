package com.orivon.mob.h3cmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.orivon.mob.h3cmanager.callback.BitmapCallBack;
import com.orivon.mob.h3cmanager.callback.DataCallBack;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    final String kaptURL = "http://newcms.h3c.com/hpcms/kaptcha";
    final String loginURL = "http://newcms.h3c.com/hpcms/login.jsp";

    final String beforeURL = "http://newcms.h3c.com/hpcms/beforeMain.jsp";


    H3CApplication application;
    HttpConnect http;


    ImageView imgKapt;

    Button btnLogin;


    EditText editName, editPwd, editKapt;

    SPTool spTool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        application = (H3CApplication) getApplication();
        http = application.getHttp();
        initView();


        getKapt();

        spTool = new SPTool(this);


    }

    private void initView() {
        imgKapt = (ImageView) findViewById(R.id.imgKapt);
        imgKapt.setOnClickListener(this);


        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);


        editName = (EditText) findViewById(R.id.editName);
        editPwd = (EditText) findViewById(R.id.editPwd);
        editKapt = (EditText) findViewById(R.id.editKapt);

    }


    /**
     * 获取验证码
     */
    public void getKapt() {
        http.doGet(kaptURL, new BitmapCallBack() {
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
                        application.setH3cCookie(response.header("Set-Cookie").split(";")[0]);
                    }
                }


        );


    }


    public void h3cLogin() {
        Headers.Builder build = new Headers.Builder();
        build.add("Referer", "http://newcms.h3c.com/hpcms/login.jsp");
        build.add("Origin", "http://newcms.h3c.com");
        build.add("Upgrade-Insecure-Requests", "1");
        build.add("Cookie", application.getH3cCookie());
        Map<String, String> parse = new HashMap<>();
        parse.put("kaptcha", editKapt.getText().toString().trim());
        parse.put("password", editPwd.getText().toString().trim());
        parse.put("userName", editName.getText().toString().trim());

        http.doPost(
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
                            spTool.setString("h3cCookie", application.getH3cCookie());
                            application.writeCookie(application.getH3cCookie());
                            Intent intent = new Intent(LoginActivity.this, CaseInfoListActivity.class);
                            startActivity(intent);
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




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgKapt:
                getKapt();
                break;

            case R.id.btnLogin:


                h3cLogin();
                break;
        }


    }
}
