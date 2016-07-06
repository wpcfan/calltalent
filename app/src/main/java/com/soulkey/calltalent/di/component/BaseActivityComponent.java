package com.soulkey.calltalent.di.component;

import android.app.Application;

import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.di.module.UserModule;
import com.soulkey.calltalent.di.scope.ActivityScope;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.SplashActivity;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import dagger.Component;

/**
 * Created by wangpeng on 16/7/6.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {
        UserModule.class,
        NetworkModule.class,
        SplashModule.class
})
public interface BaseActivityComponent {
    Application getApplication();

    SchedulerProvider getProvider();

    void inject(BaseActivity activity);

    void inject(SplashActivity activity);
}
