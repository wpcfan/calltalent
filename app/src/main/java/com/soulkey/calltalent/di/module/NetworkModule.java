package com.soulkey.calltalent.di.module;

import android.app.Application;

import com.soulkey.calltalent.api.network.INetworkManager;
import com.soulkey.calltalent.api.network.NetworkManager;

import dagger.Module;
import dagger.Provides;

/**
 * Network Module
 * Created by peng on 2016/7/3.
 */
@Module
public class NetworkModule {
    @Provides
    INetworkManager providesNetworkManager(Application application) {
        return new NetworkManager(application);
    }
}
