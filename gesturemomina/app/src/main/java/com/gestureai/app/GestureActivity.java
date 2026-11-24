
package com.gestureai.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gestureai.app.ml.GestureClassifier;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GestureActivity extends AppCompatActivity {

    private PreviewView cameraPreview;
    private TextView txtGesture, txtConfidence;

    private GestureClassifier classifier;
    private ExecutorService cameraExecutor;

    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);

        cameraPreview = findViewById(R.id.cameraPreview);
        txtGesture = findViewById(R.id.txtGesture);
        txtConfidence = findViewById(R.id.txtConfidence);

        cameraExecutor = Executors.newSingleThreadExecutor();

        try {
            classifier = new GestureClassifier(this);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Model loading failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(
                        cameraPreview.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis
                );

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Camera error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy image) {
        try {
            Bitmap bitmap = image.toBitmap();

            GestureClassifier.Prediction prediction =
                    classifier.classify(bitmap);

            runOnUiThread(() -> {
                txtGesture.setText(prediction.gestureName);
                txtConfidence.setText(String.format(
                        java.util.Locale.getDefault(),
                        "Confidence: %.1f%%",
                        prediction.getConfidencePercentage()));
            });

        } catch (Exception e) {
            android.util.Log.e("GestureAI",
                    "Prediction failed", e);
        } finally {
            image.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Camera permission is required",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }

        if (classifier != null) {
            classifier.close();
        }
    }
}
