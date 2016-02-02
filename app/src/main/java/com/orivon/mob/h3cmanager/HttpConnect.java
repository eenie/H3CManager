package com.orivon.mob.h3cmanager;

import com.orivon.mob.h3cmanager.internal.IDataCallBack;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eenie on 2015/12/30.
 * 网络请求库基库
 */
public class HttpConnect {


    public static final String TAG = "HttpConnect";
    private boolean debug = true;


    //---------协议信息-------------
    public static final MediaType jsonReq = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType formReq = MediaType.parse("application/x-www-form-urlencoded");
    public static final MediaType imgReq = MediaType.parse("image/webp,image/*,*/*;q=0.8");



    //------------Head 信息------------
    public static final String HEAD_USER_AGENT = "User-Agent";
    public static final String HEAD_COOKIE = "Cookie";

    //-------------常量信息-------------
    private static final int SOCKET_TIMEOUT = 5; //连接超时
    private static final int READ_TIMEOUT = 5; //读取超时
    private static final int WRITE_TIMEOUT = 5; // 写入超时
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36"; //设备标识

    private OkHttpClient client;
    public HttpConnect() {
        client = new OkHttpClient();
        client.setConnectTimeout(SOCKET_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
    }


    public void doGet(String url, Headers headers, IDataCallBack callback) {
        Request request;
        if (headers != null) {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .headers(headers)
                    .header(HEAD_USER_AGENT, USER_AGENT)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .header(HEAD_USER_AGENT, USER_AGENT)
                    .build();
        }
        client.newCall(request).enqueue(callback);
    }
    public void doGet(String url, IDataCallBack callback) {
        Request request;
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .header(HEAD_USER_AGENT, USER_AGENT)
                    .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     * POST请求
     *
     * @param url       网址
     * @param paramMap  参数表
     * @param mediaType 内容类型协议
     * @param callback  回调接口
     */
    public void doPost(String url, Map<String, String> paramMap, MediaType mediaType, Headers headers, IDataCallBack callback) {
        callback.onStart();
        RequestBody body = null;

        try {
            body = RequestBody.create(mediaType, mapToString(paramMap));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .headers(headers)
                    .header(HEAD_USER_AGENT, USER_AGENT)
                    .build();
            client.newCall(request).enqueue(callback);
    }









    public void download(String url, IDataCallBack callBack) {
        callBack.onStart();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callBack);
    }



    public void getImg() {





//        Accept-Encoding: gzip


//        Accept: image/webp,image/*,*/*;q=0.8

//        Accept-Encoding: gzip, deflate, sdch


//        Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.6


    }









    /**
     * 将参数Map转成String
     * @param paramMap 参数表
     * @return 转换、编码后的结果
     */
    private String mapToString(Map<String, String> paramMap) throws UnsupportedEncodingException {
        String res = "";
        if (paramMap != null && !paramMap.isEmpty()) {
            for (String key : paramMap.keySet()) {
                res = res + key + "=" + URLEncoder.encode(paramMap.get(key), "utf-8") + "&";
            }
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }





}
