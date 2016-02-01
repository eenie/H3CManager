package com.orivon.mob.h3cmanager.internal;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Eenie on 2016/1/5.
 *
 */
public interface IDataCallBack extends Callback {

    void onStart();

    @Override
    void onFailure(Request request, IOException e);

    @Override
    void onResponse(Response response) throws IOException;


    void onFinish();




}
