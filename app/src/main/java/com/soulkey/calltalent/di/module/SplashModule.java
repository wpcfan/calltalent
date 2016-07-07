package com.soulkey.calltalent.di.module;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.domain.model.SplashModel;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class SplashModule {
    @Provides
    public SplashModel providesSplashModule(SchedulerProvider schedulerProvider, IHttpManager networkService) {
        return new SplashModel(schedulerProvider, networkService);
    }
}
