package com.soulkey.calltalent.db;

import android.app.Application;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.soulkey.calltalent.db.model.Setting;
import com.squareup.sqlbrite.BriteDatabase;

import rx.Observable;

public class SettingDao implements ISettingDao {

    private BriteDatabase db;

    public SettingDao(Application application) {
        db = SqlBrightWrapper.getDatabase(application);
    }

    @Override
    public Observable<String> getSettingValueByName(@NonNull String settingName) {
        return db.createQuery(
                Setting.TABLE_NAME,
                Setting.SELECT_BY_SETTING_NAME,
                settingName)
                .map(query -> {
                    Cursor cursor = query.run();
                    try {
                        if (cursor != null && cursor.moveToNext()) {
                            return cursor.getString(1);
                        }
                    } finally {
                        cursor.close();
                    }
                    return null;
                }).firstOrDefault("");
    }

    @Override
    public boolean updateSetting(@NonNull String name, String value) {
        return db.update(
                Setting.TABLE_NAME,
                Setting.FACTORY.marshal().setting_value(value).asContentValues(),
                Setting.SETTING_NAME + " = ? ",
                name) == 1;
    }

    @Override
    public boolean createSetting(@NonNull String name, String value) {
        return db.insert(
                Setting.TABLE_NAME,
                Setting.FACTORY.marshal().setting_name(name).setting_value(value).asContentValues()) == 1;
    }
}
