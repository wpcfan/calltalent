package com.soulkey.calltalent.di.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.soulkey.calltalent.api.storage.IStorageManager;
import com.soulkey.calltalent.api.storage.StorageManager;
import com.soulkey.calltalent.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Storage Module
 * Created by peng on 2016/7/3.
 */
@SuppressWarnings("ALL")
@Module
public final class StorageModule {
    @Provides
    @ActivityScope
    public SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @ActivityScope
    IStorageManager providesStorageManager(SharedPreferences prefs) {
        return new StorageManager(prefs);
    }
}
