package com.example.codechallenge;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Forzar modo oscuro por default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);
        SwitchCompat switchNotifications = findViewById(R.id.switchNotifications);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Oculta el switch de modo oscuro, ya que el modo oscuro es forzado
        //switchDarkMode.setChecked(true);
        //switchDarkMode.setEnabled(false);

        // Switch de modo oscuro funcional
        switchDarkMode.setEnabled(true);
        switchDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Aquí puedes agregar listeners para guardar preferencias si lo deseas

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Aquí va la lógica para cerrar sesión
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });
    }
}
