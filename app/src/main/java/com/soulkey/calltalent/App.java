package com.soulkey.calltalent;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.di.component.DaggerApplicationComponent;
import com.soulkey.calltalent.di.module.AppModule;
import com.soulkey.calltalent.di.module.NetworkModule;
import com.soulkey.calltalent.di.module.SupportModule;

/**
 * Custom application definition.
 * Created by peng on 2016/5/20.
 */
@SuppressWarnings("ALL")
public final class App extends Application {

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
        appComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .supportModule(new SupportModule())
                .networkModule(new NetworkModule())
                .build();
        appComponent.inject(this);

//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
