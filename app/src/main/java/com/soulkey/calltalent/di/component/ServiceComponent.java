package com.soulkey.calltalent.di.component;

import com.soulkey.calltalent.di.module.ServiceModule;
import com.soulkey.calltalent.di.scope.ServiceScope;
import com.soulkey.calltalent.service.SplashService;

import dagger.Component;

@ServiceScope
@Component(modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(SplashService service);
}