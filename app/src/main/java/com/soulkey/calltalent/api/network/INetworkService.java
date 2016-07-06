package com.soulkey.calltalent.api.network;

import rx.Observable;

/**
 *
 * Created by peng on 2016/7/3.
 */
public interface INetworkService {
    enum NetworkStatus {
        UNKNOWN("unknown"),
        WIFI_CONNECTED("connected to WiFi"),
        MOBILE_CONNECTED("connected to mobile network"),
        OFFLINE("offline");

        private final String value;

        NetworkStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    Observable<NetworkStatus> observeNetworkChange();
    Observable<NetworkStatus> getNetworkStatus();

    Observable<String> getSplashImageUrl(String url);
}
