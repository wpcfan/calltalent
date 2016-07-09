package com.soulkey.calltalent.api.auth.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.soulkey.calltalent.api.auth.IAuthManager;
import com.soulkey.calltalent.api.wrapper.RxFirebase;
import com.soulkey.calltalent.domain.entity.User;

import rx.Observable;

/**
 * Created by peng on 2016/5/27.
 */
public class AuthServiceFirebaseImpl implements IAuthManager {

    private FirebaseAuth mAuth;

    public AuthServiceFirebaseImpl() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Observable<Boolean> registerWithUsernameAndPassword(String username, String password) {
        return RxFirebase.createWithPassword(mAuth, username, password)
                .map(authResult -> authResult != null);
    }

    @Override
    public Observable<User> loginWithUsernameAndPassword(String username, String password) {
        return RxFirebase.authWithPassword(mAuth, username, password)
                .map(authResult -> User.create(
                        authResult.getUser().getUid(),
                        authResult.getUser().isAnonymous()
                ));
    }

    @Override
    public Observable<User> isUserLoggedIn() {
        return RxFirebase.observeAuth(mAuth)
                .map(firebaseUser -> {
                    if (firebaseUser != null)
                        return User.create(
                                firebaseUser.getUid(),
                                firebaseUser.isAnonymous()
                        );
                    else
                        return null;
                });
    }

    @Override
    public Observable<Boolean> signOut() {
        return RxFirebase.signOut(mAuth);
    }
}
