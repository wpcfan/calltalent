package com.soulkey.calltalent.di.module;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.domain.model.SplashModel;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public final class SplashModule {
    @Provides
    public SplashModel providesSplashModel(
            ISchedulerProvider schedulerProvider,
            IHttpManager httpManager,
            ISettingDao settingDao) {
        return new SplashModel(schedulerProvider, httpManager, settingDao);
    }
}
