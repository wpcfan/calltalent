package com.soulkey.calltalent.ui.user;

import android.Manifest;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.request.Func;
import com.soulkey.calltalent.R;
import com.soulkey.calltalent.di.component.ApplicationComponent;
import com.soulkey.calltalent.ui.BaseActivity;
import com.soulkey.calltalent.utils.image.CameraUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;

public class AvatarActivity extends BaseActivity {
    private final String TAG = AvatarActivity.class.getSimpleName();
    private RxCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        Button captureBtn = (Button) findViewById(R.id.open_camera);
        Button saveBtn = (Button) findViewById(R.id.close_camera);
        TextureView textureView = (TextureView) findViewById(R.id.preview_surface);
        assert captureBtn != null;
        assert saveBtn != null;
        assert textureView != null;

        Subscription camSub = checkPermissionAndLaunchCamera(
                textureView, CameraUtil.getRxCameraConfig(true))
                .compose(bindToLifecycle())
                .subscribe(rxCamera -> {
                    captureBtn.setEnabled(true);
                });

        getSubsCollector().add(camSub);
//        getSubsCollector().add(captureStream(checkPermissionAndLaunchCamera(
//                textureView,
//                CameraUtil.getRxCameraConfig(true)),
//                textureViewOnTouchStream(textureView)));
//        getSubsCollector().add(closeCamera(saveBtn));
    }

    private Subscription closeCamera(Button closeCameraBtn) {
        return RxView.clicks(closeCameraBtn)
                .flatMap(__ -> camera == null || !camera.isOpenCamera() ?
                        Observable.just(true) : camera.closeCameraWithResult())
                .compose(bindToLifecycle())
                .subscribe(closed -> Toast.makeText(this, closed.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected Boolean requiredLoggedIn() {
        return true;
    }

    private Subscription captureStream(
            Observable<RxCamera> observableCamera, Observable<MotionEvent> observableTextureView) {
        return Observable.zip(
                observableCamera, observableTextureView, (rxCamera, motionEvent) -> rxCamera)
                .filter(rxCamera2 -> rxCamera2 != null)
                .flatMap(rxCamera1 ->
                        rxCamera1.request().takePictureRequest(
                                false,
                                (Func) () -> Log.d(TAG, "captured"),
                                CameraUtil.X_AXIS,
                                CameraUtil.Y_AXIS,
                                ImageFormat.JPEG,
                                true))
                .compose(bindToLifecycle())
                .subscribe(rxCameraData -> {

                    Log.d(TAG, "onCreate: " + Arrays.toString(rxCameraData.cameraData));
                });
    }

    @NonNull
    private Observable<MotionEvent> textureViewOnTouchStream(TextureView textureView) {
        return RxView.touches(textureView)
                .filter(motionEvent -> MotionEvent.ACTION_DOWN == motionEvent.getAction());
    }

    private Observable<RxCamera> checkPermissionAndLaunchCamera(
            View view, TextureView textureView, RxCameraConfig config) {
        return RxView.clicks(view)
                .compose(RxPermissions.getInstance(this).ensure(Manifest.permission.CAMERA))
                .doOnNext(granted -> {
                    if (!granted)
                        Toast.makeText(AvatarActivity.this, "", Toast.LENGTH_SHORT).show();
                })
                .filter(granted -> granted)
                .flatMap(__ -> getCameraPreviewObservable(textureView, config));
    }

    private Observable<RxCamera> checkPermissionAndLaunchCamera(
            TextureView textureView, RxCameraConfig config) {
        return RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA)
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
                .flatMap(rxCamera -> {
                    this.camera = rxCamera;
                    return rxCamera.bindTexture(textureView);
                })
                .flatMap(RxCamera::startPreview);
    }
}
