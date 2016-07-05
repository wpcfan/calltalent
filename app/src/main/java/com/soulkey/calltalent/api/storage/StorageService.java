package com.soulkey.calltalent.api.storage;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by peng on 2016/7/3.
 */
public class StorageService implements IStorageService {
    private final SharedPreferences prefs;

    public StorageService(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public Observable<Boolean> writeString(@NonNull String key, @NonNull String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        return Observable.just(editor.commit());
    }

    @Override
    public Observable<Boolean> writeBoolean(@NonNull String key, @NonNull Boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        return Observable.just(editor.commit());
    }

    @Override
    public Observable<String> readString(@NonNull String key) {
        String value = prefs.getString(key, "");
        return Observable.just(value);
    }

    @Override
    public Observable<Boolean> readBoolean(@NonNull String key) {
        Boolean value = prefs.getBoolean(key, true);
        return Observable.just(value);
    }

    @Override
    public Observable<Void> remove(@NonNull String key) {
        return Observable.fromCallable(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.apply();
            return null;
        });
    }
}
