
package com.gestureai.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.gestureai.app.database.DatabaseHelper;
import com.gestureai.app.ml.GestureClassifier;
import com.gestureai.app.utils.SessionManager;

import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GalleryActivity extends AppCompatActivity {

    private ImageView selectedImage;
    private TextView resultText, confidenceText;

    private GestureClassifier classifier;
    private DatabaseHelper database;
    private SessionManager session;

    private ExecutorService executor;

    private GestureClassifier.Prediction latestPrediction;

    // Image picker compatible with older AndroidX versions
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            processImage(uri);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        selectedImage = findViewById(R.id.selectedImage);
        resultText = findViewById(R.id.resultText);
        confidenceText = findViewById(R.id.confidenceText);

        Button pickButton = findViewById(R.id.pickButton);
        Button saveButton = findViewById(R.id.saveButton);

        database = new DatabaseHelper(this);
        session = new SessionManager(this);

        executor = Executors.newSingleThreadExecutor();

        try {
            classifier = new GestureClassifier(this);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Model loading failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            pickButton.setEnabled(false);
            saveButton.setEnabled(false);
            return;
        }

        pickButton.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );

        saveButton.setOnClickListener(v -> saveResult());
    }

    private void processImage(Uri uri) {

        executor.execute(() -> {

            try {
                Bitmap bitmap;

                try (InputStream stream =
                             getContentResolver().openInputStream(uri)) {

                    if (stream == null) {
                        throw new Exception("Unable to open image");
                    }

                    bitmap = BitmapFactory.decodeStream(stream);
                }

                if (bitmap == null) {
                    throw new Exception("Invalid image");
                }

                GestureClassifier.Prediction prediction =
                        classifier.classify(bitmap);

                bitmap.recycle();

                runOnUiThread(() -> {

                    selectedImage.setImageURI(uri);

                    resultText.setText(prediction.gestureName);

                    confidenceText.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "Confidence: %.2f%%",
                                    prediction.getConfidencePercentage()
                            )
                    );

                    latestPrediction = prediction;
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Image processing failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }

    private void saveResult() {

        if (latestPrediction == null ||
                latestPrediction.gestureName.equals(
                        "No confident gesture")) {

            Toast.makeText(
                    this,
                    "No confident prediction to save",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean saved = database.addHistory(
                session.getUserId(),
                latestPrediction.gestureName,
                latestPrediction.confidence
        );

        Toast.makeText(
                this,
                saved ? "Saved to history" : "Save failed",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (executor != null) {
            executor.shutdown();
        }

        if (classifier != null) {
            classifier.close();
        }
    }
}
