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
 * Created by Eenie on 2016/1/6.
 * 图片回调
 */
public abstract class BitmapCallBack implements IDataCallBack {

    private final int ERROR_CODE_FAILURE = 0;
    private final int ERROR_CODE_SUCCESS = 1;

    private Bitmap bitmap;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR_CODE_FAILURE:

                    onFailure(msg.arg1);

                    break;
                case ERROR_CODE_SUCCESS:

                    onSuccessful((Bitmap) msg.obj);
                    break;

            }

        }
    };


    @Override
    public void onStart() {

    }

    @Override
    public void onFailure(Request request, IOException e) {

    }


    public abstract void onSuccessful(Bitmap bitmap);

    public abstract void onFailure(int code);


    @Override
    public void onResponse(Response response) throws IOException {

        if (response.isSuccessful()) {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE_SUCCESS;
            bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            msg.obj = bitmap;
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = ERROR_CODE_FAILURE;
            msg.arg1 = response.code();
            handler.sendMessage(msg);
        }


    }

    @Override
    public void onFinish() {

    }
}
