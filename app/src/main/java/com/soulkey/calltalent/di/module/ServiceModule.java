package com.soulkey.calltalent.di.module;

import com.soulkey.calltalent.api.network.HttpManager;
import com.soulkey.calltalent.api.network.IHttpManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {

    @Provides
    IHttpManager providesHttpManager() {
        return new HttpManager();
    }

}
