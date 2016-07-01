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
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.auth.LoginParams;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;

/**
 * CreateUserProfileActivity is the UI for users to create his/her profile
 */
public class CreateUserProfileActivity extends BaseActivity {

    @BindView(R.id.nameInput)
    EditText nameInput;
    @BindView(R.id.titleInput)
    EditText titleInput;
    @BindView(R.id.descInput)
    EditText descInput;
    @BindView(R.id.completeUserProfileBtn)
    Button completeUserProfile;
    @BindView(R.id.showUserProfileBtn)
    Button showUserProfileBtn;
    @BindView(R.id.take_photo_btn)
    ImageButton takePhotoBtn;
    @BindView(R.id.gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.gender_male_selected)
    RadioButton maleChecked;
    @BindView(R.id.gender_female_selected)
    RadioButton femaleChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        ButterKnife.bind(this);

        String uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());
        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);
        getSubsCollector().add(maleCheckedSubscription());
        getSubsCollector().add(femaleCheckedSubscription());

        getSubsCollector().add(dealWithSubmit(uid));
        getSubsCollector().add(RxView.clicks(showUserProfileBtn)
                .flatMap(aVoid -> userModel.getUserProfile(uid))
                .compose(bindToLifecycle())
                .subscribe(profile -> Toast.makeText(this, profile.name(), Toast.LENGTH_SHORT).show()));
        getSubsCollector().add(dealWithAvatar(params));
    }

    private Subscription dealWithAvatar(Map<String, String> params) {
        return RxView.clicks(takePhotoBtn)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    UIHelper.launchActivity(this, AvatarActivity.class, params);
                    finish();
                });
    }

    private Subscription dealWithSubmit(String uid) {
        return getSubmitProfileStream(uid)
                .subscribe(aVoid1 -> Log.d("Haha", "onCreate: "));
    }

    private Observable<Boolean> getSubmitProfileStream(String uid) {
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
                .compose(bindToLifecycle());
    }

    private Subscription femaleCheckedSubscription() {
        return RxView.clicks(femaleChecked)
                .subscribe(__ -> {
                    femaleChecked.setTextColor(
                            ContextCompat.getColor(this, R.color.gender_female));
                    maleChecked.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                });
    }

    private Subscription maleCheckedSubscription() {
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
