package com.soulkey.calltalent.utils.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by peng on 2016/6/22.
 */
public class AnimationUtil {

    public static Animation applySlideLeftAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        animation.setDuration(200);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }

    public static Animation applySlideDownAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );

        animation.setDuration(100);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }
}
