package com.orivon.mob.h3cmanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eenie on 2016/2/2.
 */
public class SPTool {

    SharedPreferences sp;
    Context context;
    String spName = "h3cCache";

    public SPTool(Context context) {
        this.context = context;
        sp = context.getSharedPreferences(spName, 0);
    }


    public String getString(String key) {
        return sp.getString(key, "");
    }

    public void setString(String key , String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }



}
