package com.soulkey.calltalent;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.di.component.DaggerApplicationComponent;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.AuthModule;
import com.squareup.leakcanary.LeakCanary;

/**
 * Custom application definition.
 * Created by peng on 2016/5/20.
 */
public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    private ApplicationComponent appComponent;

    /**
     * Extracts application from support context types.
     *
     * @param context Source context.
     * @return Application instance or {@code null}.
     */
    public static App from(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        appComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .authModule(new AuthModule())
                .build();
        appComponent.inject(this);
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
