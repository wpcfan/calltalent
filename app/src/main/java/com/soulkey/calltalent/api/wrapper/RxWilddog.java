package com.soulkey.calltalent.api.wrapper;

import android.support.annotation.IntDef;

import com.soulkey.calltalent.api.auth.AuthResult;
import com.soulkey.calltalent.api.auth.IAuthResult;
import com.wilddog.client.AuthData;
import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Wilddog's RX Wrapper
 * Created by peng on 2016/6/12.
 */
public final class RxWilddog {
    public static Func1<WilddogChildEvent, Boolean> makeEventFilter(
            final @WilddogChildEvent.EventType int eventType) {
        return wilddogChildEvent -> wilddogChildEvent.eventType == eventType;
    }

    public static Observable<DataSnapshot> observe(final Query query) {
        return Observable.create(subscriber -> {
            final ValueEventListener listener = query.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            subscriber.onNext(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(WilddogError databaseError) {
                            subscriber.onError(new DatabaseException(databaseError));
                        }
                    });
            subscriber.add(Subscriptions.create(() -> query.removeEventListener(listener)));
        });
    }

    public static Observable<DataSnapshot> fetchByPath(final Wilddog reference, String path) {
        return Observable.create(subscriber -> {
            Wilddog ref = reference.child(path);
            final ValueEventListener listener = ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    subscriber.onNext(dataSnapshot);
                    subscriber.onCompleted();
                }

                @Override
                public void onCancelled(WilddogError wilddogError) {
                    subscriber.onError(wilddogError.toException());
                }
            });
            subscriber.add(Subscriptions.create(() -> ref.removeEventListener(listener)));
        });
    }

    public static Observable<Void> setValue(final Wilddog reference, final Object value) {
        return Observable.create(subscriber -> {
            WeakReference<Subscriber> subscriberRef = new WeakReference<>(subscriber);
            Wilddog.CompletionListener listener = (wilddogError, wilddog) -> {
                if (wilddogError == null) {
                    subscriberRef.get().onNext(null);
                    subscriberRef.get().onCompleted();
                } else
                    subscriberRef.get().onError(wilddogError.toException());
            };
            reference.setValue(value, listener);
        });
    }

    public static Observable<Void> updateChildren(
            final Wilddog reference, final Map<String, Object> children) {
        return Observable.create(subscriber -> {
            WeakReference<Subscriber> subscriberRef = new WeakReference<>(subscriber);
            final Wilddog.CompletionListener listener = (wilddogError, wilddog) -> {
                if (wilddogError == null) {
                    subscriberRef.get().onNext(null);
                    subscriberRef.get().onCompleted();
                } else
                    subscriberRef.get().onError(wilddogError.toException());
            };
            reference.updateChildren(children, listener);
        });
    }

    public static Observable<IAuthResult<AuthData>> observeAuthChange(final Wilddog auth) {
        return Observable.create(subscriber -> {
            final Wilddog.AuthStateListener listener = authData -> {
                if (subscriber.isUnsubscribed()) return;
                if (authData != null) {
                    subscriber.onNext(AuthResult.success(authData));
                } else {
                    subscriber.onNext(AuthResult.failure("User signed out", authData));
                }
                subscriber.onCompleted();
            };
            subscriber.add(Subscriptions.create(() -> auth.removeAuthStateListener(listener)));
            auth.addAuthStateListener(listener);
        });
    }

    public static Observable<IAuthResult<AuthData>> observeAuth(Wilddog auth) {
        return observeAuthChange(auth)
                .distinctUntilChanged();
    }

    public static Observable<IAuthResult<AuthData>> authAnonymously(final Wilddog wilddogAuth) {
        return Observable.create(subscriber -> {
            WeakReference<Subscriber> subscriberRef = new WeakReference<>(subscriber);
            final Wilddog.AuthResultHandler handler = new Wilddog.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    if (subscriberRef.get().isUnsubscribed()) return;
                    subscriberRef.get().onNext(AuthResult.success(authData));
                    subscriberRef.get().onCompleted();
                }

                @Override
                public void onAuthenticationError(WilddogError wilddogError) {
                    subscriberRef.get().onError(wilddogError.toException());
                }
            };
            wilddogAuth.authAnonymously(handler);
        });
    }

    public static Observable<IAuthResult<AuthData>> authWithPassword(
            final Wilddog wilddogAuth, final String username, final String password) {
        return Observable.create(subscriber -> {
            WeakReference<Subscriber> subscriberRef = new WeakReference<>(subscriber);
            final Wilddog.AuthResultHandler authResultHandler = new Wilddog.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    if (subscriberRef.get().isUnsubscribed()) return;
                    subscriberRef.get().onNext(AuthResult.success(authData));
                    subscriberRef.get().onCompleted();
                }

                @Override
                public void onAuthenticationError(WilddogError wilddogError) {
                    subscriberRef.get().onError(wilddogError.toException());
                }
            };

            wilddogAuth.authWithPassword(username, password, authResultHandler);
        });
    }

    public static Observable<IAuthResult<AuthData>> createWithPassword(
            final Wilddog wilddogAuth, final String username, final String password) {
        return Observable.create(subscriber -> {
            WeakReference<Subscriber> subscriberRef = new WeakReference<>(subscriber);
            Wilddog.ResultHandler resultHandler = new Wilddog.ResultHandler() {
                @Override
                public void onSuccess() {
                    if (subscriberRef.get().isUnsubscribed()) {
                        return;
                    }
                    subscriberRef.get().onNext(AuthResult.success(wilddogAuth.getAuth()));
                    subscriberRef.get().onCompleted();
                }

                @Override
                public void onError(WilddogError wilddogError) {
                    subscriberRef.get().onError(wilddogError.toException());
                }
            };

            wilddogAuth.createUser(username, password, resultHandler);
        });
    }

    /**
     * Returns an Observable that signs out on subscription, emits null and completes.
     */
    public static Observable<Boolean> signOut(final Wilddog auth) {
        return Observable.create(subscriber -> {
            auth.unauth();
            observeAuth(auth)
                    .map(IAuthResult::isSuccessful)
                    .filter(result -> !result)
                    .take(1)
                    .subscribe(subscriber);
        });
    }

    Observable<WilddogChildEvent<DataSnapshot>> observeChildren(final Query query) {
        return Observable.create(subscriber -> {
            final ChildEventListener childEventListener =
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new WilddogChildEvent<>(
                                    dataSnapshot, WilddogChildEvent.TYPE_ADD, prevName));
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new WilddogChildEvent<>(
                                    dataSnapshot, WilddogChildEvent.TYPE_CHANGE, prevName));
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            subscriber.onNext(new WilddogChildEvent<>(
                                    dataSnapshot, WilddogChildEvent.TYPE_REMOVE, null));
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new WilddogChildEvent<>(
                                    dataSnapshot, WilddogChildEvent.TYPE_MOVE, prevName));
                        }

                        @Override
                        public void onCancelled(WilddogError databaseError) {
                            subscriber.onError(new DatabaseException(databaseError));
                        }
                    });
            subscriber.add(Subscriptions.create(() ->
                    query.removeEventListener(childEventListener)));
        });
    }

    public static class WilddogChildEvent<T> {
        public static final int TYPE_ADD = 1;
        public static final int TYPE_CHANGE = 2;
        public static final int TYPE_REMOVE = 3;
        public static final int TYPE_MOVE = 4;
        public final T value;
        public final
        @EventType
        int eventType;
        public final String prevName;

        public WilddogChildEvent(T value, @EventType int eventType, String prevName) {
            this.value = value;
            this.eventType = eventType;
            this.prevName = prevName;
        }

        public static <V> WilddogChildEvent<V> cast(
                WilddogChildEvent<DataSnapshot> event,
                Class<V> cls) {
            return event.withValue((V) event.value.getValue(cls));
        }

        public <V> WilddogChildEvent<V> withValue(V value) {
            return new WilddogChildEvent<>(value, eventType, prevName);
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TYPE_ADD, TYPE_CHANGE, TYPE_MOVE, TYPE_REMOVE})
        public @interface EventType {
        }
    }

    public static class DatabaseException extends IOException {
        private final WilddogError error;

        public DatabaseException(WilddogError error) {
            super(error.getMessage() + "\n" + error.getDetails(), error.toException());
            this.error = error;
        }

        public WilddogError getError() {
            return error;
        }
    }
}
