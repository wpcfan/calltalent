package com.soulkey.calltalent.di.component;

import android.app.Application;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.INetworkManager;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.DbModule;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.SupportModule;
import com.soulkey.calltalent.domain.IClock;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * The bridge between the Modules and Inject targets
 * Created by peng on 2016/5/25.
 */
@SuppressWarnings("ALL")
@Singleton
@Component(modules = {
        AppModule.class,
        SupportModule.class,
        NetworkModule.class,
        DbModule.class
})
public interface ApplicationComponent {
    Application getApplication();

    ISchedulerProvider getSchedulerProvider();

    IClock getClock();
    INetworkManager getNetworkManager();
    void inject(App app);
}
