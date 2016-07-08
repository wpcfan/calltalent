package com.soulkey.calltalent.di.module;

import android.app.Application;

import com.fuck_boilerplate.rx_paparazzo.RxPaparazzo;
import com.soulkey.calltalent.BuildConfig;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Dagger Module for customized application object -- App
 * Created by peng on 2016/5/24.
 */
@Module
public final class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
        LeakCanary.install(mApplication);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        RxPaparazzo.register(mApplication);
        CrashReport.initCrashReport(mApplication, "900037648", true);
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }
}
