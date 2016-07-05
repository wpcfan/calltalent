package com.soulkey.calltalent.di.module;

import android.app.Application;

import com.soulkey.calltalent.api.network.INetworkService;
import com.soulkey.calltalent.api.network.NetworkService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Network Module
 * Created by peng on 2016/7/3.
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    INetworkService providesNetworkService(Application application) {
        return new NetworkService(application);
    }
}
