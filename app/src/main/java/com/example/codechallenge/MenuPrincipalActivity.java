package com.example.codechallenge;

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

        List<Reto> retos = new ArrayList<>();
        retos.add(new Reto("Reto Rojo", "Resolver 5 problemas de lógica.", Color.parseColor("#F44336")));
        retos.add(new Reto("Reto Amarillo", "Completa el quiz semanal.", Color.parseColor("#FFEB3B")));
        retos.add(new Reto("Reto Verde", "Participa en el foro.", Color.parseColor("#4CAF50")));
        retos.add(new Reto("Reto Blanco", "Revisa tu progreso.", Color.parseColor("#FFFFFF")));
        retos.add(new Reto("Reto Morado", "Desbloquea el siguiente nivel.", Color.parseColor("#7C4DFF")));
        retos.add(new Reto("Reto Azul", "Comparte un reto con un amigo.", Color.parseColor("#40C9FF")));

        RetoAdapter adapter = new RetoAdapter(retos);
        recyclerRetos.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            // Aquí puedes manejar la navegación
            return true;
        });
    }
}