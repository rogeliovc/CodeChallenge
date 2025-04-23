package com.example.codechallenge;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- INICIO INTEGRACIÓN FIREBASE REGISTRO ---
        com.google.firebase.FirebaseApp.initializeApp(this);
        android.widget.EditText emailEditText = findViewById(R.id.editTextEmail);
        android.widget.EditText passwordEditText = findViewById(R.id.editTextPassword);
        android.widget.Button btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Por favor ingresa correo y contraseña.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            registrarUsuario(email, password);
        });

        android.widget.Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Por favor ingresa correo y contraseña.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            iniciarSesion(email, password);
        });
        // --- FIN INTEGRACIÓN FIREBASE REGISTRO ---
    }

    private void registrarUsuario(String email, String password) {
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                    android.widget.Toast.makeText(this, "Registro exitoso: " + user.getEmail(), android.widget.Toast.LENGTH_SHORT).show();
                } else {
                    android.widget.Toast.makeText(this, "Error: " + task.getException().getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void iniciarSesion(String email, String password) {
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                    android.widget.Toast.makeText(this, "Bienvenido: " + user.getEmail(), android.widget.Toast.LENGTH_SHORT).show();
                    // Navegar a MenuPrincipalActivity
                    android.content.Intent intent = new android.content.Intent(this, MenuPrincipalActivity.class);
                    startActivity(intent);
                    finish(); // Opcional: cerrar MainActivity para que no vuelva con back
                } else {
                    android.widget.Toast.makeText(this, "Error: " + task.getException().getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
    }
}