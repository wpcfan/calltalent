package com.soulkey.calltalent.di.component;

import android.app.Application;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.api.network.INetworkManager;
import com.soulkey.calltalent.db.ISettingDao;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.DbModule;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.SupportModule;
import com.soulkey.calltalent.domain.IClock;
import com.soulkey.calltalent.domain.model.SettingModel;
import com.soulkey.calltalent.service.SplashService;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

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
        NetworkModule.class,
        DbModule.class
})
public interface ApplicationComponent {
    Application getApplication();

    INetworkManager getNetworkManager();

    IHttpManager getHttpManager();

    ISettingDao getSettingDao();
    ISchedulerProvider getSchedulerProvider();
    IClock getClock();

    SettingModel getSettingModel();
    void inject(App app);

    void inject(SplashService service);
}
