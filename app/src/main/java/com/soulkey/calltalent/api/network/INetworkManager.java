package com.soulkey.calltalent.api.network;

import rx.Observable;

/**
 *
 * Created by peng on 2016/7/3.
 */
public interface INetworkManager {
    enum NetworkStatus {
        UNKNOWN("unknown"),
        CONNECTED("connected"),
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
}
