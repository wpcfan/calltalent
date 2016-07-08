package com.soulkey.calltalent.db.populator;

import android.database.sqlite.SQLiteDatabase;

import com.soulkey.calltalent.db.model.Setting;
import com.soulkey.calltalent.db.model.SettingModel;

public class SettingPopulator {
    private static final String SPLASH_IMAGE_URI = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";

    public static void populate(SQLiteDatabase db) {
        db.insert(
                SettingModel.TABLE_NAME,
                null,
                Setting.FACTORY.marshal()
                        .setting_name("downloaded_splash_image_local_uri")
                        .setting_value("").asContentValues());
        db.insert(
                SettingModel.TABLE_NAME,
                null,
                Setting.FACTORY.marshal()
                        .setting_name("splash_image_remote_uri")
                        .setting_value(SPLASH_IMAGE_URI).asContentValues());
    }
}
