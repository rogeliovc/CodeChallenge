package com.example.codechallenge;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.io.IOException;

public class AccountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageAvatar;
    private ImageButton btnEditAvatar, btnBack;
    private EditText editTextEmail;
    private Button btnLogout;
    private SwitchCompat switchDarkMode, switchNotifications;

    // Simulación de datos de usuario
    private String userName = "Juan Pérez";
    private String userEmail = "juan.perez@email.com";
    private Uri avatarUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Enlazar vistas
        imageAvatar = findViewById(R.id.imageAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        editTextEmail = findViewById(R.id.editTextEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        // Mostrar datos reales del usuario autenticado (Firebase Auth)
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        android.widget.TextView textViewName = findViewById(R.id.textViewName);
        if (user != null) {
            // Mostrar nombre
            if (user.getDisplayName() != null) {
                textViewName.setText(user.getDisplayName());
            }
            // Mostrar correo (no editable)
            if (user.getEmail() != null) {
                editTextEmail.setText(user.getEmail());
                editTextEmail.setEnabled(false); // El correo de Google no debe ser editable
            }
            // Mostrar foto/avatar
            if (user.getPhotoUrl() != null) {
                try {
                    // Si tienes Picasso
                    com.squareup.picasso.Picasso.get()
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(imageAvatar);
                } catch (Exception e) {
                    imageAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                }
            } else {
                imageAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        }

        btnEditAvatar.setOnClickListener(v -> requestStoragePermission());

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Cerrar sesión de Firebase
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                    // Cerrar sesión de Google
                    com.google.android.gms.auth.api.signin.GoogleSignInOptions gso = new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                    com.google.android.gms.auth.api.signin.GoogleSignInClient googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);
                    googleSignInClient.signOut();
                    // Ir al login y limpiar el back stack
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });

        // Por default, modo oscuro
        int mode = AppCompatDelegate.getDefaultNightMode();
        if (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM || mode == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            mode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        switchDarkMode.setChecked(mode == AppCompatDelegate.MODE_NIGHT_YES || (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatarUri);
                imageAvatar.setImageBitmap(bitmap);
                // Aquí puedes guardar el avatar en preferencias o backend
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Permisos de almacenamiento para galería (Android 13+ y anteriores)
    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1001);
            } else {
                openImagePicker();
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
            } else {
                openImagePicker();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a imágenes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // SimpleTextWatcher para detectar cambios en EditText sin implementar todos los métodos
    private static class SimpleTextWatcher implements android.text.TextWatcher {
        private Runnable onChanged;
        SimpleTextWatcher(Runnable onChanged) { this.onChanged = onChanged; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { onChanged.run(); }
        @Override public void afterTextChanged(android.text.Editable s) {}
    }
}
