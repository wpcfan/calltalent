package com.soulkey.calltalent.api.auth;

import com.soulkey.calltalent.domain.entity.User;

import rx.Observable;

/**
 * The authentication interface
 * Created by peng on 2016/5/27.
 */
public interface IAuthManager {
    Observable<Boolean> registerWithUsernameAndPassword(String username, String password);

    Observable<User> loginWithUsernameAndPassword(String username, String password);

    Observable<User> isUserLoggedIn();

    Observable<Boolean> signOut();
}
