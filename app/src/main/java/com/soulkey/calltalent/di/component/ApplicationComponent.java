package com.soulkey.calltalent.di.component;

import android.app.Application;

import com.soulkey.calltalent.App;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.AuthModule;
import com.soulkey.calltalent.ui.BaseActivity;
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
        AuthModule.class
})
public interface ApplicationComponent {
    void inject(App app);

    void inject(BaseActivity activity);

    Application getApplication();

    SchedulerProvider getProvider();

}
