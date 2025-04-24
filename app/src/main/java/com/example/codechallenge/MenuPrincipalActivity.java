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

        List<Reto> retos = new ArrayList<>();
        retos.add(new Reto("Reto Rojo", "Resolver 5 problemas de lógica.", Color.parseColor("#E07A5F")));
        retos.add(new Reto("Reto Amarillo", "Completa el quiz semanal.", Color.parseColor("#F2CC8F")));
        retos.add(new Reto("Reto Verde", "Participa en el foro.", Color.parseColor("#81B29A")));
        retos.add(new Reto("Reto Azul", "Comparte un reto con un amigo.", Color.parseColor("#3D405B")));

        RetoAdapter adapter = new RetoAdapter(retos);
        recyclerRetos.setAdapter(adapter);

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
}