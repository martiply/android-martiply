package com.martiply.android;

import android.app.Application;
import com.martiply.android.sqlite.DaoMaster;
import com.martiply.android.sqlite.DaoSession;
import com.martiply.android.sqlite.DbOpenHelper;
import jonathanfinerty.once.Once;

public class MyApplication extends Application {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Once.initialise(this);
        setupDatabase();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private void setupDatabase() {
        daoSession = new DaoMaster(new DbOpenHelper(this, "db").getWritableDb()).newSession();
    }
}
