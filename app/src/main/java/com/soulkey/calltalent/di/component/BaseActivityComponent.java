package com.soulkey.calltalent.di.component;

import android.app.Application;

import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.di.module.HttpModule;
import com.soulkey.calltalent.di.module.SplashModule;
import com.soulkey.calltalent.di.module.UserModule;
import com.soulkey.calltalent.di.scope.ActivityScope;
import com.soulkey.calltalent.domain.model.SplashModel;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {
        UserModule.class,
        HttpModule.class,
        SplashModule.class
})
public interface BaseActivityComponent {
    Application getApplication();
    SchedulerProvider getProvider();

    IHttpManager getHttpManager();

    UserModel getUserModel();

    SplashModel getSplashModel();
    void inject(BaseActivity activity);
}
