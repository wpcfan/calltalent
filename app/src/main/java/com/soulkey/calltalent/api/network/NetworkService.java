package com.soulkey.calltalent.api.network;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.ConnectivityStatus;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.soulkey.calltalent.api.network.processor.BingImageProcessor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

/**
 * Network API that encapsulates the network status and http client
 * Created by peng on 2016/7/3.
 */
public class NetworkService implements INetworkService {

    private final OkHttpClient client;
    private final ReactiveNetwork rxNetwork;
    private final Application application;

    public NetworkService(Application application) {
        rxNetwork = new ReactiveNetwork();
        this.application = application;
        this.client = new OkHttpClient.Builder().build();
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

    @Override
    public Observable<String> getSplashImageUrl(String url) {
        return getHttpResponse(url).map(BingImageProcessor::getImageUri);
    }

    private Observable<Response> getHttpResponse(String url) {
        return Observable.fromCallable(() -> client.newCall(new Request.Builder().url(url).build()).execute());
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
