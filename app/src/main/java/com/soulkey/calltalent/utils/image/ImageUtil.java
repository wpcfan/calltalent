package com.soulkey.calltalent.utils.image;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.soulkey.calltalent.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.exceptions.Exceptions;

/**
 * Image Helper to ease image manipulation
 * Created by wangpeng on 16/6/29.
 */
public class ImageUtil {
    private static final int MAX_WIDTH = 1080;
    private static final int MAX_HEIGHT = 1920;
    private static final String DATE_FORMAT = "ddMMyyyy_HHmmss";
    private static final String LOCALE_EN = "en";

    /**
     * decode the byte array to Bitmap
     *
     * @param data the byte array
     * @return the observable Bitmap
     */
    public static Observable<Bitmap> decodeByteArray(final byte[] data) {
        return Observable.fromCallable(() -> {
            // Decode image size
            BitmapFactory.Options prev = new BitmapFactory.Options();
            prev.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, prev);

            // Find the correct scale value. It should be the power of 2.
            int scale = calculateInSampleSize(prev, MAX_WIDTH, MAX_HEIGHT);

            // Decode with inSampleSize
            BitmapFactory.Options curr = new BitmapFactory.Options();
            curr.inSampleSize = scale;
            return BitmapFactory.decodeByteArray(data, 0, data.length, curr);
        });
    }

    public static Observable<Bitmap> decodeFile(final File f) {
        return Observable.fromCallable(()->{
            // Decode image size
            BitmapFactory.Options prev = new BitmapFactory.Options();
            prev.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, prev);

            // Find the correct scale value. It should be the power of 2.
            int scale = calculateInSampleSize(prev, MAX_WIDTH, MAX_HEIGHT);

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        });
    }

    public static Observable<Uri> saveImageToGallery(
            final ContentResolver resolver, final byte[] data, final Matrix matrix) {
        return Observable.fromCallable(() -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return Uri.parse(
                    MediaStore.Images.Media.insertImage(
                            resolver,
                            Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    0,
                                    bitmap.getWidth(),
                                    bitmap.getHeight(),
                                    matrix,
                                    false),
                            R.string.app_name + String.valueOf(System.currentTimeMillis()),
                            R.string.app_name + "avatar"));
        });
    }

    public static Observable<Uri> saveImageToCache(
            final Context context,
            final byte[] data, Matrix matrix,
            Bitmap.CompressFormat compressFormat) {
        return createBitmap(data, matrix)
                .map(bitmap -> {
                    File file = getOutputFile(context);
                    bitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG);
                    return Uri.fromFile(file);
                });
    }

    public static Observable<Uri> saveImageToCache(
            final Context context,
            final Bitmap bitmap) {
        return Observable.fromCallable(() -> {
            File file = getOutputFile(context);
            bitmap2file(bitmap, file, Bitmap.CompressFormat.JPEG);
            return Uri.fromFile(file);
        });
    }

    public static boolean bitmap2file(Bitmap bitmap, File file, Bitmap.CompressFormat compressFormat) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(compressFormat, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw Exceptions.propagate(e);
        }
    }

    public static Observable<Bitmap> createBitmap(final byte[] cameraData, final Matrix rotateMatrix) {
        return decodeByteArray(cameraData)
                .map(bitmap -> Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false));
    }

    private static int calculateInSampleSize(
            final BitmapFactory.Options options, final int maxWidth, final int maxHeight) {
        int inSampleSize = 1;
        int[] dimensPortrait = getDimensionsPortrait(options.outWidth, options.outHeight);
        int[] maxDimensPortrait = getDimensionsPortrait(maxWidth, maxHeight);
        float width = dimensPortrait[0];
        float height = dimensPortrait[1];
        float newMaxWidth = maxDimensPortrait[0];
        float newMaxHeight = maxDimensPortrait[1];

        if (height > newMaxHeight || width > newMaxWidth) {
            int heightRatio = Math.round(height / newMaxHeight);
            int widthRatio = Math.round(width / newMaxWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            float totalPixels = width * height;
            float totalReqPixels = newMaxWidth * newMaxHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixels) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static int[] getDimensionsPortrait(final int width, final int height) {
        if (width < height) {
            return new int[]{width, height};
        } else {
            return new int[]{height, width};
        }
    }

    public static int[] getScreenDimens(final Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    public static int[] getFileDimens(final String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static Observable<Uri> getOutputUri(Context context) {
        return Observable.just(Uri.fromFile(getOutputFile(context)));
    }

    public static File getOutputFile(Context context) {
        String filename = new SimpleDateFormat(DATE_FORMAT, new Locale(LOCALE_EN)).format(new Date());
        filename = "IMG-" + filename + ".jpg";
        String dirname = getApplicationName(context);
        File dir = getPublicDir(null, dirname, context);
        return new File(dir.getAbsolutePath(), filename);
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static File getPublicDir(String dirRoot, String dirname, Context context) {
        File storageDir = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = (dirRoot != null) ? Environment.getExternalStoragePublicDirectory(dirRoot) : Environment.getExternalStorageDirectory();
            storageDir = new File(dir, dirname);

            if (!storageDir.exists() && !storageDir.mkdirs()) {
                storageDir = null;
            }
        }

        if (storageDir == null) {
            storageDir = getPrivateDir(dirname, context);
        }

        return storageDir;
    }

    public static File getPrivateDir(String dirname, Context context) {
        File dir = context.getFilesDir();
        File storageDir = TextUtils.isEmpty(dirname) ? dir : new File(dir, dirname);

        // Create the storage directory if it does not exist
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            storageDir = null;
        }
        return storageDir;
    }

    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        assert inputStream != null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
