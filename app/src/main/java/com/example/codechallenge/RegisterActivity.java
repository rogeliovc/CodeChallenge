package com.example.codechallenge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private android.widget.EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private android.widget.Button btnSignUp;
    private android.widget.ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);

        btnSignUp.setOnClickListener(v -> attemptRegister());
        ((TextView)findViewById(R.id.linkToLogin)).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Permitir registro con Enter desde el teclado
        editTextConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN)) {
                attemptRegister();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void attemptRegister() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);

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
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Confirma tu contraseña");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Las contraseñas no coinciden");
            editTextConfirmPassword.requestFocus();
            return;
        }
        btnSignUp.setEnabled(false);
        progressBar.setVisibility(android.view.View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                btnSignUp.setEnabled(true);
                progressBar.setVisibility(android.view.View.GONE);
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MenuPrincipalActivity.class));
                    finish();
                } else {
                    String msg = "Error desconocido. Intenta de nuevo.";
                    Exception ex = task.getException();
                    if (ex != null && ex.getMessage() != null) {
                        if (ex.getMessage().contains("email address is already in use")) {
                            msg = "El correo ya está registrado.";
                        } else if (ex.getMessage().contains("badly formatted")) {
                            msg = "Correo inválido.";
                        } else if (ex.getMessage().contains("least 6 characters")) {
                            msg = "La contraseña debe tener al menos 6 caracteres.";
                        } else {
                            msg = ex.getMessage();
                        }
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
            });
    }
}

