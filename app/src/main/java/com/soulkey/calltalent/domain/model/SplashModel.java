package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.api.network.INetworkService;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import rx.Observable;

/**
 * Created by wangpeng on 16/7/6.
 */
public class SplashModel {
    private final SchedulerProvider schedulerProvider;
    private final INetworkService networkService;

    public SplashModel(SchedulerProvider schedulerProvider, INetworkService networkService) {
        this.schedulerProvider = schedulerProvider;
        this.networkService = networkService;
    }

    public Observable<String> getSplashImage(String url) {
        return networkService.getSplashImageUrl(url).compose(schedulerProvider.applySchedulers());
    }
}
