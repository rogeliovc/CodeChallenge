package com.example.codechallenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private android.widget.EditText editTextEmail, editTextPassword;
    private android.widget.Button btnLogin;
    private android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            // Ya hay sesión, ve directo al menú principal
            startActivity(new android.content.Intent(this, MenuPrincipalActivity.class));
            finish();
        }
        ((TextView)findViewById(R.id.linkToRegister)).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });

        // Permitir login con Enter desde el teclado
        editTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        if (email.isEmpty()) {
            editTextEmail.setError("El correo es obligatorio");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("La contraseña es obligatoria");
            editTextPassword.requestFocus();
            return;
        }
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                btnLogin.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MenuPrincipalActivity.class));
                    finish();
                } else {
                    String msg = "Error desconocido. Intenta de nuevo.";
                    Exception ex = task.getException();
                    if (ex != null && ex.getMessage() != null) {
                        if (ex.getMessage().contains("password")) {
                            msg = "Contraseña incorrecta.";
                        } else if (ex.getMessage().contains("no user record")) {
                            msg = "Usuario no encontrado.";
                        } else {
                            msg = ex.getMessage();
                        }
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
            });
    }
}

