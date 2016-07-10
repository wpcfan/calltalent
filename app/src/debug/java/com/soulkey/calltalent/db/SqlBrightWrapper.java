package com.soulkey.calltalent.db;

import android.content.Context;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SqlBrightWrapper {
    public static BriteDatabase getDatabase(Context context) {
        BriteDatabase db = SqlBrite.create(message -> Timber.tag("Database").v(message))
                .wrapDatabaseHelper(DbOpenHelper.getInstance(context), Schedulers.io());
        db.setLoggingEnabled(true);
        return db;
    }
}
