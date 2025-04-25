package com.example.codechallenge;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        RecyclerView recyclerRetos = findViewById(R.id.recyclerRetos);
        recyclerRetos.setLayoutManager(new LinearLayoutManager(this));

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
                // Ya estás en inicio
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
    public void onBackPressed() {
        RecyclerView recyclerRetos = findViewById(R.id.recyclerRetos);
        RecyclerView.Adapter currentAdapter = recyclerRetos.getAdapter();
        Object tag = recyclerRetos.getTag();
        // Si estamos mostrando retos (no lenguajes), volver a la lista de lenguajes
        if (tag instanceof LenguajesAdapter && currentAdapter != tag) {
            recyclerRetos.setAdapter((RecyclerView.Adapter) tag);
        } else {
            super.onBackPressed();
        }
    }
}