package com.orivon.mob.h3cmanager.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.orivon.mob.h3cmanager.internal.IDataCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Eenie on 2016/1/5.
 * 字符串回调
 */
public abstract class DataCallBack implements IDataCallBack {


    private final int ERROR_CODE_FAILURE = 0;
    private final int ERROR_CODE_SUCCESS = 1;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR_CODE_FAILURE:
                    onFailure(msg.arg1);
                    break;
                case ERROR_CODE_SUCCESS:
                    onSuccessful((String) msg.obj);
                    break;

            }

        }
    };

    @Override
    public abstract void onStart();

    public abstract void onFailure(int code);

    public abstract void onSuccessful(String response);

    @Override
    public abstract void onFinish();


    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {

        if (response.isSuccessful()) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE_SUCCESS;
            msg.obj = response.body().string();
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE_FAILURE;
            msg.arg1 = response.code();
            handler.sendMessage(msg);
        }
    }


}
