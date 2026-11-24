package com.gestureai.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gestureai.app.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            goToLogin();
            return;
        }

        TextView welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText(
                "Welcome, " + session.getUserName() + "!"
        );

        Button cameraButton = findViewById(R.id.cameraButton);
        Button galleryButton = findViewById(R.id.galleryButton);
        Button historyButton = findViewById(R.id.historyButton);
        Button aboutButton = findViewById(R.id.aboutButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        cameraButton.setOnClickListener(v ->
                startActivity(
                        new Intent(this, GestureActivity.class)
                )
        );

        galleryButton.setOnClickListener(v ->
                startActivity(
                        new Intent(this, GalleryActivity.class)
                )
        );

        historyButton.setOnClickListener(v ->
                startActivity(
                        new Intent(this, HistoryActivity.class)
                )
        );

        aboutButton.setOnClickListener(v ->
                startActivity(
                        new Intent(this, AboutActivity.class)
                )
        );

        logoutButton.setOnClickListener(v -> {

            session.logout();

            goToLogin();
        });
    }

    private void goToLogin() {

        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}