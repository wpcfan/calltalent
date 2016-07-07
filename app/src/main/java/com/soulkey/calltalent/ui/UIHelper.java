package com.soulkey.calltalent.ui;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Map;

/**
 *
 * Created by peng on 2016/6/7.
 */
public final class UIHelper {

    // region Utility Methods
    public static void hideKeyboard(Context context, View view) {
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                if (view != null) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }
    // endregion

    /**
     * Helper method to launch an Activity with parameters
     *
     * @param clazz  the Activity class to be navigated to
     * @param params the parameters to be received
     */
    public static void launchActivity(Activity activity, final Class<?> clazz, final Map<String, String> params) {
        Intent intent = new Intent(activity, clazz);
        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        ActivityCompat.startActivity(activity, intent, null);
    }

    /**
     * Helper method to launch an Activity
     *
     * @param clazz the Activity class to be navigated to.
     */
    public static void launchActivity(Activity activity, Class<?> clazz) {
        Intent intent = new Intent(activity, clazz);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void launchActivityWithTransition(
            Activity activity, Class<?> clazz, View view, String transitionName, final Map<String, String> params) {
        Intent intent = new Intent(activity, clazz);
        for (Map.Entry<String, String> entry :
                params.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, view, transitionName);
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        } else {
            ActivityCompat.startActivity(activity, intent, null);
        }
    }
}
