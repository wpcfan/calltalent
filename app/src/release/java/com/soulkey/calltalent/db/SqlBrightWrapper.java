package com.soulkey.calltalent.db;

import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;

public class SqlBrightWrapper {
    public static BriteDatabase getDatabase(Context context) {
        BriteDatabase db = SqlBrite.create()
                .wrapDatabaseHelper(DbOpenHelper.getInstance(context), Schedulers.io());
        db.setLoggingEnabled(false);
        return db;
    }
}
