package com.soulkey.calltalent.api.network;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;

import rx.Observable;

/**
 * Network API that encapsulates the network status and http client
 * Created by peng on 2016/7/3.
 */
public final class NetworkManager implements INetworkManager {

    private final Application application;

    public NetworkManager(Application application) {
        this.application = application;
    }

    @Override
    public Observable<NetworkStatus> observeNetworkChange() {
        return ReactiveNetwork
                .observeNetworkConnectivity(application)
                .map(this::mapNetworkStatus);
    }

    @NonNull
    private NetworkStatus mapNetworkStatus(Connectivity connectivity) {
        switch (connectivity.getState()) {
            case CONNECTED:
                return NetworkStatus.CONNECTED;
            case DISCONNECTED:
                return NetworkStatus.OFFLINE;
            default:
                return NetworkStatus.UNKNOWN;
        }
    }
}
