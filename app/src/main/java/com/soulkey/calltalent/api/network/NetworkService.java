package com.soulkey.calltalent.api.network;

import android.app.Application;

import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;

import rx.Observable;

/**
 * Created by peng on 2016/7/3.
 */
public class NetworkService implements INetworkService {

    private final ReactiveNetwork reactiveNetwork;
    private final Application application;

    public NetworkService(Application application) {
        reactiveNetwork = new ReactiveNetwork();
        this.application = application;
    }

    @Override
    public Observable<NetworkStatus> getNetworkStatus() {
        return reactiveNetwork
                .observeNetworkConnectivity(application)
                .map(connectivityStatus -> {
                    switch (connectivityStatus) {
                        case MOBILE_CONNECTED:
                            return NetworkStatus.MOBILE_CONNECTED;
                        case WIFI_CONNECTED:
                            return NetworkStatus.WIFI_CONNECTED;
                        case OFFLINE:
                            return NetworkStatus.OFFLINE;
                        default:
                            return NetworkStatus.UNKNOWN;
                    }
                });
    }
}
