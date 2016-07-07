package com.soulkey.calltalent.api.storage;

import android.support.annotation.NonNull;

/**
 *
 * Created by peng on 2016/7/3.
 */
public interface IStorageManager {
    Boolean writeString(@NonNull String key, @NonNull String value);

    Boolean writeBoolean(@NonNull String key, @NonNull Boolean value);

    String readString(@NonNull String key);

    Boolean readBoolean(@NonNull String key);

    void remove(@NonNull String key);
}
