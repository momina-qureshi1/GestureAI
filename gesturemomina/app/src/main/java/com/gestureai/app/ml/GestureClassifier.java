
package com.gestureai.app.ml;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class GestureClassifier {

    private static final int IMAGE_SIZE = 224;
    private static final int NUM_CLASSES = 15;
    private static final float CONFIDENCE_THRESHOLD = 0.60f;

    private final Interpreter interpreter;

    private final String[] gestures = {
            "call", "dislike", "fist", "four", "like",
            "mute", "no_gesture", "ok", "one", "palm",
            "peace", "rock", "stop", "three", "two_up"
    };

    public GestureClassifier(Context context) throws IOException {
        interpreter = new Interpreter(
                loadModel(context),
                new Interpreter.Options().setNumThreads(2)
        );
    }

    private MappedByteBuffer loadModel(Context context)
            throws IOException {

        try (AssetFileDescriptor descriptor =
                     context.getAssets().openFd("gesture_model.tflite");
             FileInputStream inputStream =
                     new FileInputStream(descriptor.getFileDescriptor())) {

            FileChannel channel = inputStream.getChannel();

            return channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    descriptor.getStartOffset(),
                    descriptor.getDeclaredLength()
            );
        }
    }

    public Prediction classify(Bitmap bitmap) {

        Bitmap resized = Bitmap.createScaledBitmap(
                bitmap, IMAGE_SIZE, IMAGE_SIZE, true
        );

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(
                4 * IMAGE_SIZE * IMAGE_SIZE * 3
        );

        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[IMAGE_SIZE * IMAGE_SIZE];

        resized.getPixels(
                pixels, 0, IMAGE_SIZE, 0, 0,
                IMAGE_SIZE, IMAGE_SIZE
        );

        for (int pixel : pixels) {

            float red = (pixel >> 16) & 0xFF;
            float green = (pixel >> 8) & 0xFF;
            float blue = pixel & 0xFF;

            // Model performs its own normalization
            inputBuffer.putFloat(red);
            inputBuffer.putFloat(green);
            inputBuffer.putFloat(blue);
        }

        inputBuffer.rewind();

        float[][] output = new float[1][NUM_CLASSES];

        interpreter.run(inputBuffer, output);

        int bestIndex = 0;
        float bestConfidence = output[0][0];

        for (int i = 1; i < NUM_CLASSES; i++) {
            if (output[0][i] > bestConfidence) {
                bestConfidence = output[0][i];
                bestIndex = i;
            }
        }

        String gestureName = bestConfidence >= CONFIDENCE_THRESHOLD
                ? gestures[bestIndex]
                : "No confident gesture";

        return new Prediction(gestureName, bestConfidence);
    }

    public void close() {
        interpreter.close();
    }

    public static class Prediction {

        public final String gestureName;
        public final float confidence;

        public Prediction(String gestureName, float confidence) {
            this.gestureName = gestureName;
            this.confidence = confidence;
        }

        public float getConfidencePercentage() {
            return confidence * 100;
        }
    }
}
