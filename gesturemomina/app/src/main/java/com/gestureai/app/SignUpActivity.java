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

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText nameInput, emailInput;
    private TextInputEditText passwordInput, confirmInput;

    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        database = new DatabaseHelper(this);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmInput = findViewById(R.id.confirmInput);

        Button signupButton = findViewById(R.id.signupButton);
        TextView loginLink = findViewById(R.id.loginLink);

        signupButton.setOnClickListener(v -> register());

        loginLink.setOnClickListener(v -> finish());
    }

    private void register() {

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirm = confirmInput.getText().toString();

        if (name.isEmpty()) {
            nameInput.setError("Enter your full name");
            return;
        }

        if (email.isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError(
                    "Password must contain at least 6 characters"
            );
            return;
        }

        if (!password.equals(confirm)) {
            confirmInput.setError("Passwords do not match");
            return;
        }

        if (database.registerUser(name, email, password)) {

            Toast.makeText(
                    this,
                    "Account created successfully. Please log in.",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

        } else {
            emailInput.setError("This email is already registered");
        }
    }
}