package com.soulkey.calltalent.api.auth.wilddog;

import android.app.Application;
import android.support.annotation.NonNull;

import com.soulkey.calltalent.api.auth.IAuthManager;
import com.soulkey.calltalent.api.auth.IAuthResult;
import com.soulkey.calltalent.api.wrapper.RxWilddog;
import com.soulkey.calltalent.domain.entity.User;
import com.wilddog.client.Wilddog;

import rx.Observable;

/**
 * Wilddog version of the implementation of IAuthManager
 * Created by peng on 2016/5/27.
 */
public class AuthManagerWilddogImpl implements IAuthManager {

    private Wilddog wilddog;

    public AuthManagerWilddogImpl(Application application) {
        Wilddog.setAndroidContext(application);
        String AUTH_URL = "https://calltalent.wilddogio.com/";
        this.wilddog = new Wilddog(AUTH_URL);
    }

    @Override
    public Observable<Boolean> registerWithUsernameAndPassword(
            @NonNull String username, @NonNull String password) {
        return RxWilddog.createWithPassword(wilddog, username, password)
                .map(IAuthResult::isSuccessful);
    }

    @Override
    public Observable<User> loginWithUsernameAndPassword(
            @NonNull String username, @NonNull String password) {
        return RxWilddog.authWithPassword(wilddog, username, password)
                .map(iAuthResult -> iAuthResult.isSuccessful() ?
                        User.create(iAuthResult.getData().getUid(), false) : null);
    }

    @Override
    public Observable<User> isUserLoggedIn() {
        return RxWilddog.observeAuth(wilddog)
                .map(iAuthResult -> iAuthResult.isSuccessful() ?
                        User.create(iAuthResult.getData().getUid(), false) : null);
    }

    @Override
    public Observable<Boolean> signOut() {
        return RxWilddog.signOut(wilddog);
    }
}
