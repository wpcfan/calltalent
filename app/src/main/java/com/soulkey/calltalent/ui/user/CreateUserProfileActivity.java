package com.soulkey.calltalent.ui.user;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        final EditText nameInput = (EditText) findViewById(R.id.nameInput);
        final EditText titleInput = (EditText) findViewById(R.id.titleInput);
        final EditText descInput = (EditText) findViewById(R.id.descInput);
        final Button completeUserProfile = (Button) findViewById(R.id.completeUserProfileBtn);
        final Button showUserProfileBtn = (Button) findViewById(R.id.showUserProfileBtn);
        final ImageButton takePhotoBtn = (ImageButton) findViewById(R.id.take_photo_btn);
        final RadioGroup genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        final RadioButton maleChecked = (RadioButton) findViewById(R.id.gender_male_selected);
        final RadioButton femaleChecked = (RadioButton) findViewById(R.id.gender_female_selected);

        assert nameInput != null;
        assert descInput != null;
        assert titleInput != null;
        assert completeUserProfile != null;
        assert takePhotoBtn != null;
        assert showUserProfileBtn != null;
        assert genderRadioGroup != null;
        assert maleChecked != null;
        assert femaleChecked != null;

        String uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());

        getSubsCollector().add(maleCheckedSubscription(maleChecked, femaleChecked));
        getSubsCollector().add(femaleCheckedSubscription(femaleChecked, maleChecked));

        getSubsCollector().add(dealWithSubmit(
                nameInput, titleInput, descInput, completeUserProfile, genderRadioGroup, uid));
        getSubsCollector().add(RxView.clicks(showUserProfileBtn)
                .flatMap(aVoid -> userModel.getUserProfile(uid))
                .compose(bindToLifecycle())
                .subscribe(profile -> Toast.makeText(this, profile.name(), Toast.LENGTH_SHORT).show()));
        getSubsCollector().add(dealWithAvatar(takePhotoBtn));
    }

    private Subscription dealWithAvatar(ImageButton takePhotoBtn) {
        return RxView.clicks(takePhotoBtn)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> launchActivity(AvatarActivity.class));
    }

    private Subscription dealWithSubmit(EditText nameInput, EditText titleInput, EditText descInput, Button completeUserProfile, RadioGroup genderRadioGroup, String uid) {
        return RxView.clicks(completeUserProfile)
                .flatMap(aVoid -> {
                    UserProfile userProfile = UserProfile.create(
                            uid,
                            nameInput.getText().toString(),
                            titleInput.getText().toString(),
                            "",
                            genderRadioGroup.getCheckedRadioButtonId() == R.id.gender_male_selected,
                            descInput.getText().toString());
                    return userModel.saveUserProfile(userProfile, uid);
                })
                .compose(bindToLifecycle())
                .subscribe(aVoid1 -> Log.d("Haha", "onCreate: "));
    }

    private Subscription femaleCheckedSubscription(RadioButton femaleChecked, RadioButton maleChecked) {
        return RxView.clicks(femaleChecked)
                .subscribe(__ -> {
                    femaleChecked.setTextColor(
                            ContextCompat.getColor(this, R.color.gender_female));
                    maleChecked.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                });
    }

    private Subscription maleCheckedSubscription(RadioButton maleChecked, RadioButton femaleChecked) {
        return RxView.clicks(maleChecked)
                .subscribe(__ -> {
                    maleChecked.setTextColor(
                            ContextCompat.getColor(this, R.color.gender_male));
                    femaleChecked.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                });
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
