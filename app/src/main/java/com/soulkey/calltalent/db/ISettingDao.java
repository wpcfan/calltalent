package com.soulkey.calltalent.db;

import android.support.annotation.NonNull;

import rx.Observable;

public interface ISettingDao {
    Observable<String> getSettingValueByName(@NonNull String settingName);

    boolean updateSetting(@NonNull String name, String value);

    boolean createSetting(@NonNull String name, String value);
}
