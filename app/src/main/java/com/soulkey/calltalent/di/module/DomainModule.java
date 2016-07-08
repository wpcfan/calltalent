package com.soulkey.calltalent.di.module;

import android.app.Application;
import android.content.SharedPreferences;

import com.soulkey.calltalent.api.auth.IAuthManager;
import com.soulkey.calltalent.api.auth.wilddog.AuthManagerWilddogImpl;
import com.soulkey.calltalent.api.network.HttpManager;
import com.soulkey.calltalent.api.network.IHttpManager;
import com.soulkey.calltalent.api.storage.AvatarDiskCache;
import com.soulkey.calltalent.api.storage.UserProfileDiskCache;
import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.api.user.wilddog.UserManagerWilddogImpl;
import com.soulkey.calltalent.di.scope.ActivityScope;
import com.soulkey.calltalent.domain.Clock;
import com.soulkey.calltalent.domain.model.UserModel;
import com.soulkey.calltalent.utils.rx.SchedulerProvider;

import dagger.Module;
import dagger.Provides;
/**
 * Wilddog Auth Module
 *
 * @see <a href="http://wilddogio.com">WildDog</a> for details
 * Created by peng on 2016/5/25.
 */
@Module
public class DomainModule {

    @ActivityScope
    @Provides
    IAuthManager providesAuthManager(Application application) {
        return new AuthManagerWilddogImpl(application);
    }

    @ActivityScope
    @Provides
    public IUserManager providesUserManager(Application application) {
        return new UserManagerWilddogImpl(application);
    }

    @ActivityScope
    @Provides
    public UserProfileDiskCache providesUserDiskCache(SharedPreferences prefs) {
        return new UserProfileDiskCache(prefs);
    }

    @ActivityScope
    @Provides
    public AvatarDiskCache providesAvatarDiskCache(SharedPreferences prefs) {
        return new AvatarDiskCache(prefs);
    }

    @Provides
    IHttpManager providesHttpManager() {
        return new HttpManager();
    }

    @ActivityScope
    @Provides
    public UserModel providesUserModel(
            SchedulerProvider provider,
            UserProfileDiskCache userDiskCache,
            AvatarDiskCache avatarDiskCache,
            IAuthManager service,
            IUserManager userManager,
            Clock clock) {
        return new UserModel(provider, userDiskCache, avatarDiskCache, service, userManager, clock);
    }
}
