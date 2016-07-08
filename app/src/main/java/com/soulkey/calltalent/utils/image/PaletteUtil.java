package com.soulkey.calltalent.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.soulkey.calltalent.R;

@SuppressWarnings("ALL")
public final class PaletteUtil {
    public enum ColorFlavor {
        LightVibrant,
        DarkMuted,
        LightMuted,
        DarkVibrant,
        Muted,
        Vibrant
    }

    int extractColorFromBitmap(Bitmap bitmap, ColorFlavor target, Context context) {
        Palette palette = Palette.from(bitmap).generate();
        switch (target) {
            case Muted:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.accent));
            case DarkMuted:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.accent_translucent));
            case LightVibrant:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.primary_light));
            case DarkVibrant:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.primary_dark));
            case LightMuted:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.primary_text));
            case Vibrant:
                return palette.getMutedColor(ContextCompat.getColor(context, R.color.secondary_text));
            default:
                return ContextCompat.getColor(context, R.color.icons);
        }
    }

}
