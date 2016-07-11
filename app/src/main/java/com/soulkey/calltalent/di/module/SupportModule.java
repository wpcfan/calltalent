package com.soulkey.calltalent.di.module;

import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.domain.IClock;
import com.soulkey.calltalent.domain.model.SettingModel;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Common Support Libraries
 * Created by wangpeng on 16/7/6.
 */
@Module
public final class SupportModule {
    @Provides
    @Singleton
    public ISchedulerProvider providesSchedulerProvider() {
        return ISchedulerProvider.DEFAULT;
    }

    @Provides
    @Singleton
    public IClock providesClock() {
        return IClock.REAL;
    }

    @Provides
    @Singleton
    public SettingModel provideSettingModel(ISettingDao settingDao, ISchedulerProvider schedulerProvider) {
        return new SettingModel(settingDao, schedulerProvider);
    }
}
