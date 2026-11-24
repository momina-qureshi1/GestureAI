package com.gestureai.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView description = findViewById(R.id.aboutDescription);

        description.setText(
                "GestureAI is an Android-based hand gesture recognition " +
                        "application. It uses a MobileNetV2 image classification " +
                        "model converted to TensorFlow Lite for on-device inference.\n\n" +

                        "Supported gestures:\n" +
                        "call, dislike, fist, four, like, mute, no_gesture, ok, " +
                        "one, palm, peace, rock, stop, three, two_up\n\n" +

                        "Model information:\n" +
                        "Architecture: MobileNetV2\n" +
                        "Input: 224 x 224 RGB\n" +
                        "Input type: Float32\n" +
                        "Output: 15 softmax probabilities\n" +
                        "Inference: On-device TensorFlow Lite\n\n" +

                        "Reference repository:\n" +
                        "https://github.com/Uzair390-Del/NumberGuessingAppTflite.git"
        );
    }
}