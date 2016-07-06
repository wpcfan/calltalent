package com.soulkey.calltalent.di.module;

import com.soulkey.calltalent.api.network.INetworkService;
import com.soulkey.calltalent.domain.model.SplashModel;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by wangpeng on 16/7/6.
 */
@Module
public class SplashModule {
    @Provides
    public SplashModel providesSplashModule(SchedulerProvider schedulerProvider, INetworkService networkService) {
        return new SplashModel(schedulerProvider, networkService);
    }
}
