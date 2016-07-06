package com.soulkey.calltalent.di.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.soulkey.calltalent.domain.Clock;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Common Support Libraries
 * Created by wangpeng on 16/7/6.
 */
@Module
public class SupportModule {
    @Provides
    @Singleton
    public SchedulerProvider providesSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    @Singleton
    public Clock providesClock() {
        return Clock.REAL;
    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
