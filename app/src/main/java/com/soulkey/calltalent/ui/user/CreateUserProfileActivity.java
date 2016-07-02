package com.soulkey.calltalent.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.soulkey.calltalent.utils.image.ImageUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;
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
    ImageView takePhotoBtn;
    @BindView(R.id.gender_radio_group)
    RadioGroup genderRadioGroup;
    @BindView(R.id.gender_male_selected)
    RadioButton maleChecked;
    @BindView(R.id.gender_female_selected)
    RadioButton femaleChecked;
    @State
    Uri localUri;
    @State
    String uid;
    @State
    String remoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        ButterKnife.bind(this);

        uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());
        Subscription subReceiveParam = getParams()
                .subscribe(uri -> {
                    localUri = Uri.parse(uri);
                    takePhotoBtn.setImageURI(localUri);
                });
        getSubsCollector().add(subReceiveParam);

        //parameters to be passed to the login screen if the linktoSignin is clicked
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);
        getSubsCollector().add(maleCheckedSubscription());
        getSubsCollector().add(femaleCheckedSubscription());

        getSubsCollector().add(dealWithSubmit());
        getSubsCollector().add(RxView.clicks(showUserProfileBtn)
                .flatMap(aVoid -> userModel.getUserProfile(uid))
                .compose(bindToLifecycle())
                .subscribe(profile -> Toast.makeText(this, profile.name(), Toast.LENGTH_SHORT).show()));
        getSubsCollector().add(dealWithAvatar(params));
    }

    private Observable<String> getParams() {
        return Observable.just(receiveParams(LoginParams.PARAM_KEY_AVATAR_URI.getValue()))
                .filter(code -> code != null)
                .compose(bindToLifecycle());
    }

    private Subscription dealWithAvatar(Map<String, String> params) {
        return RxView.clicks(takePhotoBtn)
                .compose(bindToLifecycle())
                .subscribe(aVoid -> {
                    UIHelper.launchActivity(this, AvatarActivity.class, params);
                    finish();
                });
    }

    private Subscription dealWithSubmit() {
        return getSubmitProfileStream()
                .subscribe(aVoid1 -> Log.d("Haha", "onCreate: "));
    }

    private Observable<Boolean> getSubmitProfileStream() {
        return RxView.clicks(completeUserProfile)
                .flatMap(__ -> {
                    byte[] data = new byte[0];
                    try {
                        data = ImageUtil.getBytes(this, localUri);
                    } catch (IOException e) {
                        return Observable.error(e).materialize();
                    }
                    return userModel.uploadAvatar(data, uid).materialize();
                })
                .flatMap(notification -> {
                    if (notification.hasValue()) {
                        remoteUri = (String) notification.getValue();
                        UserProfile userProfile = UserProfile.create(
                                uid,
                                nameInput.getText().toString(),
                                titleInput.getText().toString(),
                                remoteUri,
                                genderRadioGroup.getCheckedRadioButtonId() == R.id.gender_male_selected,
                                descInput.getText().toString());
                        return userModel.saveUserProfile(userProfile, uid);
                    } else {
                        //TODO:upload error to server
                        return Observable.just(false);
                    }
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