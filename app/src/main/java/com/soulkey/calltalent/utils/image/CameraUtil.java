package com.soulkey.calltalent.utils.image;

import android.graphics.Point;

import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.config.RxCameraConfigChooser;

/**
 * Camera helper functions
 * Created by peng on 2016/6/13.
 */
public class CameraUtil {
    public final static int Y_AXIS = 640;
    public final static int X_AXIS = 480;

    public static RxCameraConfig getRxCameraConfig(boolean useFront) {
        if (useFront) return RxCameraConfigChooser.obtain()
                .useFrontCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(X_AXIS, Y_AXIS), false)
                .setHandleSurfaceEvent(true)
                .get();
        return RxCameraConfigChooser.obtain()
                .useBackCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(X_AXIS, Y_AXIS), false)
                .setHandleSurfaceEvent(true)
                .get();
    }
}
