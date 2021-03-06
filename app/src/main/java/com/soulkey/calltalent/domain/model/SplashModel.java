package com.soulkey.calltalent.domain.model;

import android.graphics.Bitmap;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.api.network.processor.BingImageProcessor;
import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.db.model.Setting;
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
        return httpManager.getHttpResponse(url).map(BingImageProcessor::getImageUri);
    }

    public Observable<Bitmap> fetchImageByUrl(String url) {
        return httpManager.fetchImageByUrl(url);
    }

    public Observable<String> getLastSavedSplashRemoteUri() {
        return settingDao
                .getSettingValueByName(Setting.PARAM.SPLASH_REMOTE_URI.getValue())
                .compose(schedulerProvider.applySchedulers());
    }

    public boolean saveSplashRemoteUri(String uri) {
        return settingDao.updateSetting(Setting.PARAM.SPLASH_REMOTE_URI.getValue(), uri);
    }
}
