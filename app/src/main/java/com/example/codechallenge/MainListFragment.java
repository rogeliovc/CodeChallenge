package com.example.codechallenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainListFragment extends Fragment {
    private RecyclerView recyclerRetos;
    private LenguajesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_list, container, false);
        recyclerRetos = v.findViewById(R.id.recyclerRetos);
        recyclerRetos.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarLenguajes();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restaurar visibilidad de barras al volver a este fragmento
        if (getActivity() != null) {
            getActivity().findViewById(R.id.headerLayout).setVisibility(android.view.View.VISIBLE);
            getActivity().findViewById(R.id.bottomNav).setVisibility(android.view.View.VISIBLE);
        }
        cargarLenguajes();
    }

    private void cargarLenguajes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("challenges").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Challenge> retos = new ArrayList<>();
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                Challenge reto = doc.toObject(Challenge.class);
                if (reto != null) {
                    reto.setId(doc.getId());
                    retos.add(reto);
                }
            }
            // Agrupa retos por dificultad y por lenguaje
            HashSet<String> lenguajesSet = new HashSet<>();
            for (Challenge reto : retos) {
                if (reto.getLanguage() != null) {
                    lenguajesSet.add(reto.getLanguage());
                }
            }
            List<String> lenguajes = new ArrayList<>(lenguajesSet);
            java.util.Collections.sort(lenguajes);

            // Si solo hay un lenguaje, muestra retos agrupados por dificultad
            if (lenguajes.size() == 1) {
                List<Challenge> faciles = new ArrayList<>();
                List<Challenge> medios = new ArrayList<>();
                List<Challenge> dificiles = new ArrayList<>();
                for (Challenge r : retos) {
                    if (r.getDifficulty() == null) {
                        faciles.add(r);
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
                List<RetoSeccionAdapter.Seccion> secciones = new ArrayList<>();
                if (!faciles.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Fácil", faciles));
                if (!medios.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Medio", medios));
                if (!dificiles.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Difícil", dificiles));
                recyclerRetos.setAdapter(new RetoSeccionAdapter(secciones));
            } else {
                // Muestra lenguajes como cards
                adapter = new LenguajesAdapter(lenguajes, lenguaje -> {
                    // Al hacer clic en un lenguaje, muestra retos de ese lenguaje agrupados por dificultad
                    List<Challenge> retosDeLenguaje = new ArrayList<>();
                    for (Challenge r : retos) {
                        if (lenguaje.equals(r.getLanguage())) {
                            retosDeLenguaje.add(r);
                        }
                    }
                    List<Challenge> faciles = new ArrayList<>();
                    List<Challenge> medios = new ArrayList<>();
                    List<Challenge> dificiles = new ArrayList<>();
                    for (Challenge r : retosDeLenguaje) {
                        if (r.getDifficulty() == null) {
                            faciles.add(r);
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
                    List<RetoSeccionAdapter.Seccion> secciones = new ArrayList<>();
                    if (!faciles.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Fácil", faciles));
                    if (!medios.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Medio", medios));
                    if (!dificiles.isEmpty()) secciones.add(new RetoSeccionAdapter.Seccion("Difícil", dificiles));
                    recyclerRetos.setAdapter(new RetoSeccionAdapter(secciones));
                });
                recyclerRetos.setAdapter(adapter);
            }
        });
    }

    // Permite recargar el adapter cuando se vuelve al fragmento
    public void reload() {
        cargarLenguajes();
    }
}
