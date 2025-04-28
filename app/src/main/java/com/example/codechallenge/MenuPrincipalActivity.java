package com.example.codechallenge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipalActivity extends AppCompatActivity {
    private RecyclerView recyclerRetos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        recyclerRetos = findViewById(R.id.recyclerRetos);
        recyclerRetos.setLayoutManager(new LinearLayoutManager(this));

        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        //if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
        //    logoCucei.setImageResource(R.drawable.logo_claro);
        //} else {
        //    logoCucei.setImageResource(R.drawable.logo_cucei);
        //}

        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // Declarar adapter fuera para poder usarlo en ambos lugares
        final LenguajesAdapter[] adapterHolder = new LenguajesAdapter[1];
        db.collection("challenges")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                java.util.List<Challenge> retos = new java.util.ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    Challenge reto = doc.toObject(Challenge.class);
                    if (reto != null) {
                        reto.setId(doc.getId());
                        retos.add(reto);
                    }
                }
                // Obtener lista de lenguajes únicos
                java.util.Set<String> lenguajesSet = new java.util.HashSet<>();
                for (Challenge reto : retos) {
                    if (reto.getLanguage() != null) {
                        lenguajesSet.add(reto.getLanguage());
                    }
                }
                java.util.List<String> lenguajes = new java.util.ArrayList<>(lenguajesSet);
                java.util.Collections.sort(lenguajes);
                // Mostrar lenguajes como cards
                adapterHolder[0] = new LenguajesAdapter(lenguajes, lenguaje -> {
                    // Al hacer clic en un lenguaje, mostrar retos agrupados por dificultad
                    java.util.List<Challenge> retosDeLenguaje = new java.util.ArrayList<>();
                    for (Challenge r : retos) {
                        if (lenguaje.equals(r.getLanguage())) {
                            retosDeLenguaje.add(r);
                        }
                    }
                    java.util.List<Challenge> faciles = new java.util.ArrayList<>();
                    java.util.List<Challenge> medios = new java.util.ArrayList<>();
                    java.util.List<Challenge> dificiles = new java.util.ArrayList<>();
                    for (Challenge r : retosDeLenguaje) {
                        if (r.getDifficulty() == null) {
                            faciles.add(r); // Default
                        } else if (r.getDifficulty().equalsIgnoreCase("Fácil")) {
                            faciles.add(r);
                        } else if (r.getDifficulty().equalsIgnoreCase("Medio")) {
                            medios.add(r);
                        } else if (r.getDifficulty().equalsIgnoreCase("Difícil")) {
                            dificiles.add(r);
                        } else {
                            faciles.add(r);
                        }
                    }
                    java.util.List<com.example.codechallenge.RetoSeccionAdapter.Seccion> secciones = new java.util.ArrayList<>();
                    if (!faciles.isEmpty()) secciones.add(new com.example.codechallenge.RetoSeccionAdapter.Seccion("Fácil", faciles));
                    if (!medios.isEmpty()) secciones.add(new com.example.codechallenge.RetoSeccionAdapter.Seccion("Medio", medios));
                    if (!dificiles.isEmpty()) secciones.add(new com.example.codechallenge.RetoSeccionAdapter.Seccion("Difícil", dificiles));
                    RetoSeccionAdapter retosAdapter = new RetoSeccionAdapter(secciones);
                    recyclerRetos.setAdapter(retosAdapter);
                    // Guardar referencia para retroceso
                    recyclerRetos.setTag(adapterHolder[0]);
                });
                recyclerRetos.setAdapter(adapterHolder[0]);
                // Guardar referencia para retroceso
                recyclerRetos.setTag(adapterHolder[0]);
            })
            .addOnFailureListener(e -> {
                android.widget.Toast.makeText(this, "Error al cargar retos", android.widget.Toast.LENGTH_SHORT).show();
            });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                RecyclerView.Adapter currentAdapter = recyclerRetos.getAdapter();
                Object tag = recyclerRetos.getTag();
                // Si estamos mostrando retos (no lenguajes), volver a la lista de lenguajes
                if (tag instanceof LenguajesAdapter && currentAdapter != tag) {
                    recyclerRetos.setAdapter((RecyclerView.Adapter) tag);
                } else {
                    // Ya estás en home, no hagas nada
                    return true;
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, AccountActivity.class));
                return true;
            }
            // Otros casos según tus ítems
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Si recyclerRetos es null, inicialízalo (previene crash al volver de otra actividad)
        if (recyclerRetos == null) {
            recyclerRetos = findViewById(R.id.recyclerRetos);
        }
    }

    @Override
    public void onBackPressed() {
        RecyclerView.Adapter currentAdapter = recyclerRetos.getAdapter();
        Object tag = recyclerRetos.getTag();
        // Si estamos mostrando retos (no lenguajes), volver a la lista de lenguajes
        if (tag instanceof LenguajesAdapter && currentAdapter != tag) {
            recyclerRetos.setAdapter((RecyclerView.Adapter) tag);
        } else {
            // Volver siempre al home (pantalla de lenguajes/principal)
            Intent intent = new Intent(this, MenuPrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    // Método para navegar al detalle del problema
    public void abrirDetalleProblema() {
        Fragment fragment = new ProblemDetailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}