package com.gestureai.app.ml;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    public static Bitmap imageProxyToBitmap(ImageProxy image) {

        int width = image.getWidth();
        int height = image.getHeight();

        ImageProxy.PlaneProxy[] planes = image.getPlanes();

        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[width * height * 3 / 2];

        yBuffer.get(nv21, 0, ySize);

        int chromaWidth = width / 2;
        int chromaHeight = height / 2;

        int position = width * height;

        // Interleave V and U chroma samples into NV21 format.
        for (int row = 0; row < chromaHeight; row++) {
            for (int col = 0; col < chromaWidth; col++) {

                int uIndex = row * planes[1].getRowStride()
                        + col * planes[1].getPixelStride();

                int vIndex = row * planes[2].getRowStride()
                        + col * planes[2].getPixelStride();

                nv21[position++] = vBuffer.get(vIndex);
                nv21[position++] = uBuffer.get(uIndex);
            }
        }

        YuvImage yuvImage = new YuvImage(
                nv21,
                android.graphics.ImageFormat.NV21,
                width,
                height,
                null
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(
                new Rect(0, 0, width, height),
                90,
                output
        );

        byte[] jpeg = output.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                jpeg, 0, jpeg.length
        );

        if (bitmap == null) {
            return null;
        }

        // Rotate according to CameraX image metadata.
        Matrix matrix = new Matrix();
        matrix.postRotate(image.getImageInfo().getRotationDegrees());

        Bitmap rotated = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        bitmap.recycle();

        return rotated;
    }
}