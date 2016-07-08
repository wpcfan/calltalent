package com.soulkey.calltalent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.soulkey.calltalent.db.model.SettingModel;
import com.soulkey.calltalent.db.populator.SettingPopulator;

public final class DbOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "calltalent.db";
    private static final int DB_VERSION = 1;
    private static DbOpenHelper instance;

    public static DbOpenHelper getInstance(Context context) {
        if (null == instance) {
            instance = new DbOpenHelper(context);
        }
        return instance;
    }

    private DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SettingModel.CREATE_TABLE);
        populateDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void populateDb(SQLiteDatabase db) {
        SettingPopulator.populate(db);
    }
}
