package pt.up.fe.mobilecardriving.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class AssetLoader {
    public static String getAssetPath(Context context, String filepath) throws IOException {
        final File file = new File(context.getFilesDir(), filepath);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(filepath)) {
            try (OutputStream os = new FileOutputStream(file)) {
                final byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    public static Bitmap imgToBitmap(Image image) {
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer yBuffer = planes[0].getBuffer();
        final ByteBuffer uBuffer = planes[1].getBuffer();
        final ByteBuffer vBuffer = planes[2].getBuffer();

        final int ySize = yBuffer.remaining();
        final int uSize = uBuffer.remaining();
        final int vSize = vBuffer.remaining();

        final byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        final YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        final byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
