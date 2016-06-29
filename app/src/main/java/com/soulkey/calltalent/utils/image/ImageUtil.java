package com.soulkey.calltalent.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by wangpeng on 16/6/29.
 */
public class ImageUtil {
    public static Bitmap decodeFile(byte[] data) {
        // Decode image size
        BitmapFactory.Options prev = new BitmapFactory.Options();
        prev.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, prev);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 192;

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while (prev.outWidth / scale / 2 >= REQUIRED_SIZE &&
                prev.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options curr = new BitmapFactory.Options();
        curr.inSampleSize = scale;
        return BitmapFactory.decodeByteArray(data, 0, data.length, curr);
    }

    public static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}
