package com.soulkey.calltalent.api.wrapper;

import android.support.annotation.IntDef;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Firebase's RX Wrapper
 * Created by peng on 2016/6/8.
 */
public class RxFirebase {

    public static Observable<FirebaseChildEvent<DataSnapshot>> observeChildren(final Query query) {
        return Observable.create(subscriber -> {
            final ChildEventListener childEventListener =
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new FirebaseChildEvent<>(
                                    dataSnapshot, FirebaseChildEvent.TYPE_ADD, prevName));
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new FirebaseChildEvent<>(
                                    dataSnapshot, FirebaseChildEvent.TYPE_CHANGE, prevName));
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            subscriber.onNext(new FirebaseChildEvent<>(
                                    dataSnapshot, FirebaseChildEvent.TYPE_REMOVE, null));
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevName) {
                            subscriber.onNext(new FirebaseChildEvent<>(
                                    dataSnapshot, FirebaseChildEvent.TYPE_MOVE, prevName));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(new DatabaseException(databaseError));
                        }
                    });
            subscriber.add(Subscriptions.create(() ->
                    query.removeEventListener(childEventListener)));
        });
    }

    public static Func1<FirebaseChildEvent, Boolean> makeEventFilter(
            final @FirebaseChildEvent.EventType int eventType) {
        return firebaseChildEvent -> firebaseChildEvent.eventType == eventType;
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
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(new DatabaseException(databaseError));
                        }
                    });

            subscriber.add(Subscriptions.create(() -> query.removeEventListener(listener)));
        });
    }

    public static Observable<Void> setValue(final DatabaseReference reference, final Object value) {
        return toObservable(() -> reference.setValue(value));
    }

    public static Observable<Void> updateChildren(
            final DatabaseReference reference, final Map<String, ?> children) {
        return toObservable(() -> {
            //noinspection unchecked
            return reference.updateChildren((Map<String, Object>) children);
        });
    }

    public static Observable<FirebaseAuth> observeAuthChange(final FirebaseAuth auth) {
        return Observable
                .create(subscriber -> {
                    final FirebaseAuth.AuthStateListener listener =
                            firebaseAuth -> subscriber.onNext(firebaseAuth);
                    subscriber.add(
                            Subscriptions.create(() -> auth.removeAuthStateListener(listener)));

                    auth.addAuthStateListener(listener);
                });
    }

    public static Observable<FirebaseUser> observeAuth(FirebaseAuth auth) {
        return observeAuthChange(auth)
                .map(FirebaseAuth::getCurrentUser)
                .distinctUntilChanged();
    }

    public static Observable<AuthResult> authAnonymously(final FirebaseAuth firebaseAuth) {
        return toObservable(firebaseAuth::signInAnonymously);
    }

    public static Observable<AuthResult> authWithCredential(
            final FirebaseAuth firebaseAuth, final AuthCredential credential) {
        return toObservable(() -> firebaseAuth.signInWithCredential(credential));
    }

    public static Observable<AuthResult> authWithPassword(
            final FirebaseAuth firebaseAuth, final String username, final String password) {
        return toObservable(() -> firebaseAuth.signInWithEmailAndPassword(username, password));
    }

    public static Observable<AuthResult> createWithPassword(
            final FirebaseAuth firebaseAuth, final String username, final String password) {
        return toObservable(() -> firebaseAuth.createUserWithEmailAndPassword(username, password));
    }

    /**
     * Returns an Observable that signs out on subscription, emits null and completes.
     */
    public static Observable<Boolean> signOut(final FirebaseAuth auth) {
        return Observable.create(subscriber -> {
            auth.signOut();
            observeAuth(auth)
                    .map(firebaseUser -> firebaseUser == null)
                    .filter(result -> result)
                    .take(1)
                    .subscribe(subscriber);
        });
    }

    private static <T> Observable<T> toObservable(Func0<? extends Task<T>> factory) {
        return Observable.create(new TaskOnSubscribe<>(factory));
    }

    public static class FirebaseChildEvent<T> {
        public static final int TYPE_ADD = 1;
        public static final int TYPE_CHANGE = 2;
        public static final int TYPE_REMOVE = 3;
        public static final int TYPE_MOVE = 4;
        public final T value;
        public final
        @EventType
        int eventType;
        public final String prevName;

        public FirebaseChildEvent(T value, @EventType int eventType, String prevName) {
            this.value = value;
            this.eventType = eventType;
            this.prevName = prevName;
        }

        public static <V> FirebaseChildEvent<V> cast(
                FirebaseChildEvent<DataSnapshot> event,
                Class<V> cls) {
            return event.withValue(event.value.getValue(cls));
        }

        public <V> FirebaseChildEvent<V> withValue(V value) {
            return new FirebaseChildEvent<>(value, eventType, prevName);
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TYPE_ADD, TYPE_CHANGE, TYPE_MOVE, TYPE_REMOVE})
        public @interface EventType {
        }
    }

    public static class DatabaseException extends IOException {
        private final DatabaseError error;

        public DatabaseException(DatabaseError error) {
            super(error.getMessage() + "\n" + error.getDetails(), error.toException());
            this.error = error;
        }

        public DatabaseError getError() {
            return error;
        }
    }

    private static class TaskOnSubscribe<T> implements Observable.OnSubscribe<T> {
        private final Func0<? extends Task<T>> taskFactory;

        private TaskOnSubscribe(Func0<? extends Task<T>> taskFactory) {
            this.taskFactory = taskFactory;
        }

        @Override
        public void call(final Subscriber<? super T> subscriber) {
            taskFactory.call().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    subscriber.onNext(task.getResult());
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(task.getException());
                }
            });
        }
    }

}
