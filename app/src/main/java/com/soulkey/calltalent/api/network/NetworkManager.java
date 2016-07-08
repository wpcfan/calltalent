package com.soulkey.calltalent.api.network;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;

import rx.Observable;

/**
 * Network API that encapsulates the network status and http client
 * Created by peng on 2016/7/3.
 */
public final class NetworkManager implements INetworkManager {

    private final ReactiveNetwork rxNetwork;
    private final Application application;

    public NetworkManager(Application application) {
        rxNetwork = new ReactiveNetwork();
        this.application = application;
    }

    @Override
    public Observable<NetworkStatus> observeNetworkChange() {
        return rxNetwork
                .observeNetworkConnectivity(application)
                .map(this::mapNetworkStatus);
    }

    @Override
    public Observable<NetworkStatus> getNetworkStatus() {
        return Observable.fromCallable(() -> mapNetworkStatus(rxNetwork.getConnectivityStatus(application)));
    }

    @NonNull
    private NetworkStatus mapNetworkStatus(ConnectivityStatus connectivityStatus) {
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
    }
}
