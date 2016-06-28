package com.soulkey.calltalent.utils.animation;

import android.os.Build;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Animation Helper
 * Created by peng on 2016/6/22.
 */
public class AnimationUtil {

    private static final long LongDuration = 1000L;
    private static final long ShortDuration = 200L;

    public static Animation applySlideLeftAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        animation.setDuration(ShortDuration);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }

    public static Animation applySlideDownAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );

        animation.setDuration(ShortDuration);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }

    /**
     * Add fancy activity enter/exit animation for Lollipop and above version of Android
     */
    public static void setupWindowAnimations(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(LongDuration);
            window.setEnterTransition(fade);
            Slide slide = new Slide(Gravity.LEFT);
            slide.setDuration(LongDuration);
            window.setReturnTransition(slide);
        }
    }
}
