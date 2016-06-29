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
    public final static int MIN_FRAME = 15;
    public final static int MAX_FRAME = 30;
    public final static boolean ACCEPT_SQUARE_PREVIEW = false;

    /**
     * The configuration builder to set the Camera parameters
     *
     * @param useFront if front camera is to be used
     * @return the Camera configuration object
     */
    public static RxCameraConfig getRxCameraConfig(boolean useFront) {
        RxCameraConfigChooser chooser = RxCameraConfigChooser.obtain();
        chooser = useFront ? chooser.useFrontCamera() : chooser.useBackCamera();
        return chooser
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(MIN_FRAME, MAX_FRAME)
                .setPreferPreviewSize(new Point(X_AXIS, Y_AXIS), ACCEPT_SQUARE_PREVIEW)
                .setHandleSurfaceEvent(true)
                .get();
    }
}
