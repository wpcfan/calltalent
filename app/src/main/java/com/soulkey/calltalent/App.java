package com.soulkey.calltalent;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.di.component.DaggerApplicationComponent;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.DbModule;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.SupportModule;

/**
 * Custom application definition.
 * Created by peng on 2016/5/20.
 */
public class App extends Application {

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
        appComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .supportModule(new SupportModule())
                .dbModule(new DbModule())
                .networkModule(new NetworkModule())
                .build();
        appComponent.inject(this);
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
