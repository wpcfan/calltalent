package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.db.model.Setting;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import rx.Observable;

public class SettingModel {
    private ISettingDao settingDao;
    private ISchedulerProvider schedulerProvider;

    public SettingModel(ISettingDao settingDao, ISchedulerProvider schedulerProvider) {
        this.settingDao = settingDao;
        this.schedulerProvider = schedulerProvider;
    }

    public Observable<String> getLastSavedSplashLocalUri() {
        return settingDao
                .getSettingValueByName(Setting.PARAM.DOWNLOADED_SPLASH_URI.getValue())
                .compose(schedulerProvider.applySchedulers());
    }

    public Observable<String> getLastSavedSplashRemoteUri() {
        return settingDao
                .getSettingValueByName(Setting.PARAM.SPLASH_REMOTE_URI.getValue())
                .compose(schedulerProvider.applySchedulers());
    }

    public boolean saveSplashLocalUri(String uri) {
        return settingDao.updateSetting(Setting.PARAM.DOWNLOADED_SPLASH_URI.getValue(), uri);
    }

    public boolean saveSplashRemoteUri(String uri) {
        return settingDao.updateSetting(Setting.PARAM.SPLASH_REMOTE_URI.getValue(), uri);
    }
}
