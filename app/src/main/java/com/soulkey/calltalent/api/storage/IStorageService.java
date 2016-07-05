package com.soulkey.calltalent.api.storage;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by peng on 2016/7/3.
 */
public interface IStorageService {
    Observable<Boolean> writeString(@NonNull String key, @NonNull String value);

    Observable<Boolean> writeBoolean(@NonNull String key, @NonNull Boolean value);

    Observable<String> readString(@NonNull String key);

    Observable<Boolean> readBoolean(@NonNull String key);

    Observable<Void> remove(@NonNull String key);
}
