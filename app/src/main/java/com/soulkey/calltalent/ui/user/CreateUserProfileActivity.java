package com.soulkey.calltalent.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.domain.entity.UserProfile;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.auth.LoginParams;

import rx.Subscription;

/**
 * CreateUserProfileActivity is the UI for users to create his/her profile
 */
public class CreateUserProfileActivity extends BaseActivity {
    private final String TAG = CreateUserProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        EditText nameInput = (EditText) findViewById(R.id.nameInput);
        EditText avatarInput = (EditText) findViewById(R.id.avatarInput);
        EditText titleInput = (EditText) findViewById(R.id.titleInput);
        Button completeUserProfile = (Button) findViewById(R.id.completeUserProfileBtn);
        Button showUserProfileBtn = (Button) findViewById(R.id.showUserProfileBtn);
        Button addAvatar = (Button) findViewById(R.id.addAvatarBtn);

        assert nameInput != null;
        assert avatarInput != null;
        assert titleInput != null;
        assert completeUserProfile != null;
        assert addAvatar != null;
        assert showUserProfileBtn != null;


        String uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());

        Subscription subscriptionSubmit = RxView.clicks(completeUserProfile)
                .flatMap(aVoid -> {
                    UserProfile userProfile = UserProfile.create(
                            uid,
                            nameInput.getText().toString(),
                            nameInput.getText().toString(),
                            titleInput.getText().toString(),
                            avatarInput.getText().toString(),
                            true,
                            "something blablabla");
                    return userModel.saveUserProfile(userProfile, uid);
                })
                .compose(bindToLifecycle())
                .subscribe(aVoid1 -> Log.d("Haha", "onCreate: "));

        getSubsCollector().add(subscriptionSubmit);
        getSubsCollector().add(RxView.clicks(showUserProfileBtn)
                .flatMap(aVoid -> userModel.getUserProfile(uid))
                .compose(bindToLifecycle())
                .subscribe(profile -> Toast.makeText(this, profile.name(), Toast.LENGTH_SHORT).show()));

        Subscription subscriptionAddAvatar = RxView.clicks(addAvatar)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> launchActivity(AvatarActivity.class));
        getSubsCollector().add(subscriptionAddAvatar);
    }

    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }
}
