package com.soulkey.calltalent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String TABLE_SETTING_TABLE_NAME = "setting";
    private static final String TABLE_SETTING_COLUMN_ID = "name";
    private static final String TABLE_SETTING_COLUMN_VALUE = "value";

    private static final String CREATE_SETTING = ""
            + "CREATE TABLE " + TABLE_SETTING_TABLE_NAME + "("
            + TABLE_SETTING_COLUMN_ID + " TEXT NOT NULL PRIMARY KEY,"
            + TABLE_SETTING_COLUMN_VALUE + " TEXT NOT NULL"
            + ")";

    public DbOpenHelper(Context context) {
        super(context, "calltalent.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SETTING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
