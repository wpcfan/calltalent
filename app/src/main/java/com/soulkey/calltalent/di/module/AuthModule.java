package com.soulkey.calltalent.di.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.soulkey.calltalent.api.auth.IAuthService;
import com.soulkey.calltalent.api.auth.wilddog.AuthServiceWilddogImpl;
import com.soulkey.calltalent.api.storage.UserProfileDiskCache;
import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.api.user.wilddog.UserManagerWilddogImpl;
import com.soulkey.calltalent.domain.Clock;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
//import com.soulkey.calltalent.api.auth.firebase.AuthServiceFirebaseImpl;
//import com.soulkey.calltalent.api.auth.wilddog.AuthServiceWilddogImpl;
//import com.soulkey.calltalent.api.user.firebase.UserManagerFirebaseImpl;

/**
 * Wilddog Auth Module
 *
 * @see <a href="http://wilddogio.com">WildDog</a> for details
 * Created by peng on 2016/5/25.
 */
@Module
public class AuthModule {

    @Provides
    @Singleton
    IAuthService providesAuthService(Application application) {
        return new AuthServiceWilddogImpl(application);
    }

//    @Provides
//    @Singleton
//    IAuthService providesAuthService() {
//        return new AuthServiceFirebaseImpl();
//    }

    @Provides
    @Singleton
    public IUserManager providesUserManager(Application application) {
        return new UserManagerWilddogImpl(application);
    }

//    @Provides
//    @Singleton
//    public IUserManager providesUserManager(){
//        return new UserManagerFirebaseImpl();
//    }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    public UserProfileDiskCache providesUserDiskCache(SharedPreferences prefs) {
        return new UserProfileDiskCache(prefs);
    }

    @Provides
    public SchedulerProvider providesSchedulerProvider() {
        return SchedulerProvider.DEFAULT;
    }

    @Provides
    public Clock providesClock() {
        return Clock.REAL;
    }

    @Provides
    @Singleton
    public UserModel providesUserModel(
            SchedulerProvider provider,
            UserProfileDiskCache userDiskCache,
            IAuthService service,
            IUserManager userManager,
            Clock clock) {
        return new UserModel(provider, userDiskCache, service, userManager, clock);
    }

}
