package com.example.codechallenge;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        // Mostrar la lista principal por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MainListFragment())
                .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (!(current instanceof MainListFragment)) {
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new MainListFragment())
                        .commit();
                }
                return true;
            } else if (itemId == R.id.nav_notifications) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ForumFragment())
                    .commit();
                return true;
            } else if (itemId == R.id.nav_stats) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new StatsFragment())
                    .commit();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            findViewById(R.id.headerLayout).setVisibility(android.view.View.VISIBLE);
            findViewById(R.id.bottomNav).setVisibility(android.view.View.VISIBLE);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // Método para navegar al detalle del problema
    public void abrirDetalleProblema(String challengeId) {
        // Verifica si el reto ya fue resuelto
        android.content.SharedPreferences prefs = getSharedPreferences("CodeChallengePrefs", 0);
        java.util.Set<String> solved = prefs.getStringSet("solved_challenges", new java.util.HashSet<>());
        if (solved.contains(challengeId)) {
            android.widget.Toast.makeText(this, "Este reto ya fue contestado.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        // Oculta barras solo al mostrar el detalle del problema
        findViewById(R.id.headerLayout).setVisibility(android.view.View.GONE);
        findViewById(R.id.bottomNav).setVisibility(android.view.View.GONE);
        Fragment fragment = new ProblemDetailFragment();
        Bundle args = new Bundle();
        args.putString("challengeId", challengeId);
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}