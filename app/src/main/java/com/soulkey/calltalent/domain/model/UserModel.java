package com.soulkey.calltalent.domain.model;

import com.soulkey.calltalent.api.auth.IAuthManager;
import com.soulkey.calltalent.api.storage.AvatarDiskCache;
import com.soulkey.calltalent.api.storage.UserProfileDiskCache;
import com.soulkey.calltalent.api.user.IUserManager;
import com.soulkey.calltalent.domain.IClock;
import com.soulkey.calltalent.domain.entity.Avatar;
import com.soulkey.calltalent.domain.entity.User;
import com.soulkey.calltalent.domain.entity.UserProfile;
import com.soulkey.calltalent.utils.rx.ISchedulerProvider;

import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;

/**
 * The User Model providing the UI with data needed
 * Created by peng on 2016/6/4.
 */
public final class UserModel {
    private static final String TAG = UserModel.class.getSimpleName();
    private static final long STALE_MS = 50 * 1000;
    private final ISchedulerProvider schedulerProvider;
    private final UserProfileDiskCache diskCache;
    private final AvatarDiskCache avatarDiskCache;
    private final IAuthManager service;
    private final IUserManager userManager;
    private final IClock clock;
    private UserProfile memoryCache;
    private ReplaySubject<UserProfile> userProfileReplaySubject;
    private Subscription userProfileSubscription;

    public UserModel(
            ISchedulerProvider schedulerProvider,
            UserProfileDiskCache diskCache,
            AvatarDiskCache avatarDiskCache,
            IAuthManager service,
            IUserManager userManager,
            IClock clock) {
        this.schedulerProvider = schedulerProvider;
        this.diskCache = diskCache;
        this.avatarDiskCache = avatarDiskCache;
        this.service = service;
        this.userManager = userManager;
        this.clock = clock;
    }

    public Observable<Boolean> saveAvatarToDiskCache(String uid, String mediaUri) {
        return avatarDiskCache.saveEntity(Avatar.create(uid, mediaUri));
    }

    public Observable<String> uploadAvatar(byte[] data, String uid) {
        return userManager.uploadAvatar(data, uid);
    }

    public Observable<Boolean> register(String username, String password) {
        return service.registerWithUsernameAndPassword(username, password)
                .compose(schedulerProvider.applySchedulers());
    }

    public Observable<User> login(String username, String password) {
        return service.loginWithUsernameAndPassword(username, password)
                .compose(schedulerProvider.applySchedulers());
    }

    public Observable<User> loginStatus() {
        return service.isUserLoggedIn().compose(schedulerProvider.applySchedulers());
    }

    public Observable<Boolean> signOut() {
        return service.signOut();
    }

    public Observable<UserProfile> getUserProfile(String uid) {
        if (userProfileSubscription == null || userProfileSubscription.isUnsubscribed()) {
            userProfileReplaySubject = ReplaySubject.create();
            userProfileSubscription = Observable.concat(memory(), disk(), network(uid))
                    .first(entity -> entity != null && isUpToDate(entity))
                    .subscribe(userProfileReplaySubject);
        }
        return userProfileReplaySubject.asObservable();
    }

    public Observable<Boolean> saveUserProfile(UserProfile profile, String uid) {
        return userManager.saveUserProfile(profile, uid)
                .doOnNext(__ -> memoryCache = profile)
                .flatMap(__ -> diskCache.saveEntity(profile))
                .compose(schedulerProvider.applySchedulers());
    }

    public void clearMemoryCache() {
        memoryCache = null;
    }

    public void clearMemoryAndDiskCache() {
        diskCache.clear();
        clearMemoryCache();
    }

    private Observable<UserProfile> network(String uid) {
        return userManager.getUserProfile(uid)
                .doOnNext(entity -> memoryCache = entity)
                .flatMap(entity -> diskCache.saveEntity(entity).map(result -> entity))
                .compose(schedulerProvider.applySchedulers());
    }

    private Observable<UserProfile> disk() {
        return diskCache.getEntity()
                .doOnNext(entity -> memoryCache = entity)
                .compose(schedulerProvider.applySchedulers());
    }

    private Observable<UserProfile> memory() {
        if (memoryCache == null)
            return Observable.just(null);
        return Observable.just(memoryCache);
    }

    private boolean isUpToDate(UserProfile profile) {
        return clock.millis() - profile.timestamp() < STALE_MS;
    }
}
