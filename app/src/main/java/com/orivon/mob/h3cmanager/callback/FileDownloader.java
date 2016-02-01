package com.orivon.mob.h3cmanager.callback;

import android.os.Handler;
import android.os.Message;

import com.orivon.mob.h3cmanager.internal.IDataCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Eenie on 2016/1/6.
 * 文件回调
 */
public abstract class FileDownloader implements IDataCallBack {


    private long fileSize;   //文件总大小
    private int cacheSize = 1024;   //缓冲区的大小


    private final int DOWNLOAD_FAILURE = 0;
    private final int DOWNLOAD_PROGRESS = 1;
    private final int DOWNLOAD_SUCCESSFUL = 2;

    private File file;
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DOWNLOAD_FAILURE:

                    onFailure((Request) msg.obj);
                    onFinish();
                    break;
                case DOWNLOAD_SUCCESSFUL:
                    onSuccessful(file);
                    onFinish();
                    break;
                case DOWNLOAD_PROGRESS:
                    onDownload(fileSize, msg.arg1);
                    break;

            }


        }
    };


    public FileDownloader(String path, String fileName) {
        this.file = new File(path + File.separator + fileName);
    }



    @Override
    public abstract void onStart();

    public abstract void onSuccessful(File file);

    public abstract void onDownload(long totalSize, long currentSize);

    public abstract void onFailure(Request request);

    @Override
    public abstract void onFinish();



    @Override
    public void onFailure(Request request, IOException e) {


        Message msg = handler.obtainMessage();
        msg.what = DOWNLOAD_FAILURE;
        msg.obj = request;
        handler.sendMessage(msg);
    }

    @Override
    public void onResponse(Response response) throws IOException {




        if (response.isSuccessful()) {
            fileSize = response.body().contentLength();
            InputStream is = response.body().byteStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[cacheSize];
            int len = bis.read(bytes);
            int currentSize = 0;
            while (len > 0) {
                fos.write(bytes, 0, len);
                len = bis.read(bytes);
                currentSize = currentSize + len;
                Message msg = handler.obtainMessage();
                msg.what = DOWNLOAD_PROGRESS;
                msg.arg1 = currentSize;
                handler.sendMessage(msg);
            }
            fos.flush();
            bis.close();
            fos.close();
            is.close();
            fos.close();
            Message msg = handler.obtainMessage();
            msg.what = DOWNLOAD_SUCCESSFUL;
            handler.sendMessage(msg);
        } else {
            Message msg = handler.obtainMessage();
            msg.what = DOWNLOAD_FAILURE;
            handler.sendMessage(msg);
        }


    }

}
