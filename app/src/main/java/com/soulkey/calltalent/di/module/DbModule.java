package com.soulkey.calltalent.di.module;

import android.app.Application;

import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.db.SettingDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class DbModule {
    @Provides
    @Singleton
    ISettingDao provideSettingDao(Application application) {
        return new SettingDao(application);
    }
}
