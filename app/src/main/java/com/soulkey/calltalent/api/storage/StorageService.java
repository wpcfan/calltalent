package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 *
 * Created by peng on 2016/7/3.
 */
public class StorageService implements IStorageService {
    private final SharedPreferences prefs;

    public StorageService(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public Boolean writeString(@NonNull String key, @NonNull String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    @Override
    public Boolean writeBoolean(@NonNull String key, @NonNull Boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    @Override
    public String readString(@NonNull String key) {
        String value = prefs.getString(key, "");
        return value;
    }

    @Override
    public Boolean readBoolean(@NonNull String key) {
        Boolean value = prefs.getBoolean(key, true);
        return value;
    }

    @Override
    public void remove(@NonNull String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
