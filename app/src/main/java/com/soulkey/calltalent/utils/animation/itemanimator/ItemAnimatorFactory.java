package com.soulkey.calltalent.utils.animation.itemanimator;

import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;

public class ItemAnimatorFactory {

    static public RecyclerView.ItemAnimator slidein() {
        SlideInUpDelayedAnimator animator = new SlideInUpDelayedAnimator(new DecelerateInterpolator(1.2f));
        animator.setAddDuration(600);
        return animator;
    }
}
