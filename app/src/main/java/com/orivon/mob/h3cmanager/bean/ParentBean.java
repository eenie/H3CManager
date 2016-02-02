package com.orivon.mob.h3cmanager.bean;

import android.database.DatabaseUtils;

/**
 * Created by Eenie on 2016/2/3.
 */
public class ParentBean {

    int id;

    String name;

    public ParentBean() {


        DatabaseUtils databaseUtils = new DatabaseUtils();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
