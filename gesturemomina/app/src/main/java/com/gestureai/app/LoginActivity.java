package com.gestureai.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gestureai.app.database.DatabaseHelper;
import com.gestureai.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;

    private DatabaseHelper database;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = new DatabaseHelper(this);
        session = new SessionManager(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);

        Button loginButton = findViewById(R.id.loginButton);
        TextView signupLink = findViewById(R.id.signupLink);

        loginButton.setOnClickListener(v -> login());

        signupLink.setOnClickListener(v ->
                startActivity(
                        new Intent(this, SignUpActivity.class)
                )
        );
    }

    private void login() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Enter your password");
            return;
        }

        int userId = database.loginUser(email, password);

        if (userId != -1) {

            String name = database.getUserName(userId);

            session.saveLogin(userId, name);

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Incorrect email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}