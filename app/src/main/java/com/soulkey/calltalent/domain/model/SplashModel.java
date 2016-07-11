package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import rx.Observable;

public final class SplashModel {
    private final ISchedulerProvider schedulerProvider;
    private final IHttpManager httpManager;
    private final ISettingDao settingDao;

    public SplashModel(
            ISchedulerProvider schedulerProvider, IHttpManager httpManager, ISettingDao settingDao) {
        this.schedulerProvider = schedulerProvider;
        this.httpManager = httpManager;
        this.settingDao = settingDao;
    }

    public Observable<String> getSplashImageUrl(String url) {
        return httpManager
                .getSplashImageUrl(url)
                .compose(schedulerProvider.applySchedulers());
    }
}
