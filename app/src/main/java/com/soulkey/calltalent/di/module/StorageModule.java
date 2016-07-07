package com.soulkey.calltalent.di.module;

import android.content.SharedPreferences;

import com.soulkey.calltalent.api.storage.IStorageManager;
import com.soulkey.calltalent.api.storage.StorageManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Storage Module
 * Created by peng on 2016/7/3.
 */
@Module
public class StorageModule {
    @Provides
    @Singleton
    IStorageManager providesStorageManager(SharedPreferences prefs) {
        return new StorageManager(prefs);
    }
}
