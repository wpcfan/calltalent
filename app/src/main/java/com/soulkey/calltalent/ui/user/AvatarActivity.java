package com.soulkey.calltalent.ui.user;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.Func;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.ui.UIHelper;
import com.soulkey.calltalent.ui.auth.LoginParams;
import com.soulkey.calltalent.utils.image.CameraUtil;
import com.soulkey.calltalent.utils.image.ImageUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.HashMap;
import java.util.Map;

import icepick.State;
import rx.Observable;
import rx.Subscription;

import static solid.collectors.ToArray.toArray;
import static solid.collectors.ToArrays.toBytes;
import static solid.stream.Primitives.box;
import static solid.stream.Stream.of;

public class AvatarActivity extends BaseActivity {
    private final String TAG = AvatarActivity.class.getSimpleName();
    @State
    Byte[] cameraData;
    @State
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        final Button captureBtn = (Button) findViewById(R.id.open_camera);
        final Button saveBtn = (Button) findViewById(R.id.close_camera);
        final Button recaptureBtn = (Button) findViewById(R.id.recapture);
        final TextureView textureView = (TextureView) findViewById(R.id.preview_surface);
        final ImageView imagePreview = (ImageView) findViewById(R.id.image_preview);
        assert captureBtn != null;
        assert saveBtn != null;
        assert textureView != null;
        assert recaptureBtn != null;
        assert imagePreview != null;

        this.uid = receiveParams(LoginParams.PARAM_KEY_UID.getValue());
        Map<String, String> params = new HashMap<>();
        params.put(LoginParams.PARAM_KEY_UID.getValue(), uid);
        Observable<RxCamera> observable = checkPermissionAndLaunchCamera(textureView).publish().refCount();

        Subscription subPreview = observable.compose(bindToLifecycle())
                .subscribe(__ -> captureBtn.setEnabled(true));
        getSubsCollector().add(subPreview);

        Subscription subCapture = RxView.clicks(captureBtn)
                .doOnNext(__ -> captureBtn.setEnabled(false))
                .flatMap(__ -> observable)
                .flatMap(rxCamera ->
                        rxCamera.request().takePictureRequest(
                                true,
                                (Func) () -> Log.d(TAG, "captured"),
                                CameraUtil.X_AXIS,
                                CameraUtil.Y_AXIS,
                                ImageFormat.JPEG,
                                true))
                .compose(bindToLifecycle())
                .subscribe(rxCameraData -> {
                    this.cameraData = box(rxCameraData.cameraData).collect(toArray(Byte.class));
                    captureBtn.setVisibility(View.GONE);
                    textureView.setVisibility(View.GONE);
                    imagePreview.setVisibility(View.VISIBLE);
                    recaptureBtn.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    imagePreview.setImageBitmap(ImageUtil.decodeFile(rxCameraData.cameraData));
                });
        getSubsCollector().add(subCapture);

        Subscription subRecapture = RxView.clicks(recaptureBtn)
                .compose(bindToLifecycle())
                .subscribe(__ -> {
                    captureBtn.setEnabled(true);
                    captureBtn.setVisibility(View.VISIBLE);
                    textureView.setVisibility(View.VISIBLE);
                    imagePreview.setVisibility(View.GONE);
                    recaptureBtn.setVisibility(View.GONE);
                    saveBtn.setVisibility(View.GONE);
                });
        getSubsCollector().add(subRecapture);

        Subscription subSave = RxView.clicks(saveBtn)
                .doOnNext(__ -> {
                    saveBtn.setEnabled(false);
                    recaptureBtn.setEnabled(false);
                })
                .flatMap(__ -> saveImageToGallery(of(this.cameraData).collect(toBytes())))
                .doOnNext(mediaUri -> userModel.saveAvatarToDiskCache(this.uid, mediaUri))
                .flatMap(__ -> userModel.uploadAvatar(of(this.cameraData).collect(toBytes()), this.uid))
                .compose(bindToLifecycle())
                .subscribe(remoteUri -> {
                    params.put(LoginParams.PARAM_KEY_AVATAR_URI.getValue(), remoteUri);
                    UIHelper.launchActivity(AvatarActivity.this, CreateUserProfileActivity.class, params);
                    finish();
                });
        getSubsCollector().add(subSave);
    }

    private Observable<String> saveImageToGallery(final byte[] data) {
        return Observable.fromCallable(() -> MediaStore.Images.Media.insertImage(
                getContentResolver(),
                BitmapFactory.decodeByteArray(data, 0, data.length),
                R.string.app_name + String.valueOf(System.currentTimeMillis()),
                R.string.app_name + "avatar"));
    }

    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }

    private Observable<RxCamera> checkPermissionAndLaunchCamera(
            TextureView textureView) {
        return RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(granted -> {
                    if (!granted)
                        Toast.makeText(AvatarActivity.this, "", Toast.LENGTH_SHORT).show();
                })
                .filter(granted -> granted)
                .flatMap(__ -> getCameraPreviewObservable(textureView, CameraUtil.getRxCameraConfig(true)));
    }

    private Observable<RxCamera> getCameraPreviewObservable(
            TextureView textureView, RxCameraConfig config) {
        return RxCamera.open(this, config)
                .flatMap(rxCamera -> rxCamera.bindTexture(textureView))
                .flatMap(RxCamera::startPreview);
    }
}
