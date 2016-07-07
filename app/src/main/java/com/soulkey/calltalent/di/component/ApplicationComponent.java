package com.soulkey.calltalent.di.component;

import android.app.Application;
import android.content.SharedPreferences;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.INetworkManager;
import com.soulkey.calltalent.api.storage.IStorageManager;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.StorageModule;
import com.soulkey.calltalent.di.module.SupportModule;
import com.soulkey.calltalent.domain.Clock;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The bridge between the Modules and Inject targets
 * Created by peng on 2016/5/25.
 */
@Singleton
@Component(modules = {
        AppModule.class,
        SupportModule.class,
        StorageModule.class,
        NetworkModule.class
})
public interface ApplicationComponent {
    void inject(App app);
    Application getApplication();
    Clock getClock();
    SchedulerProvider getProvider();

    IStorageManager getStorageManager();
    SharedPreferences getSharedPreferences();

    INetworkManager getNetworkManager();
}
