package com.example.codechallenge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatsFragment extends Fragment {
    private static final String PREFS_NAME = "CodeChallengePrefs";
    private static final String KEY_TOTAL_POINTS = "total_points";
    
    private TextView txtTotalPoints;
    private int totalPoints = 0;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        
        // Inicializar vistas
        txtTotalPoints = view.findViewById(R.id.txtTotalPoints);
        
        // Inicializar SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);
        
        // Cargar puntos guardados
        totalPoints = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
        updatePoints();
        
        return view;
    }



    public void addPoints(int points) {
        totalPoints += points;
        
        // Guardar puntos en SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_TOTAL_POINTS, totalPoints);
        editor.apply();
        
        updatePoints();
    }

    private void updatePoints() {
        txtTotalPoints.setText("Puntos Totales: " + totalPoints);
    }
}
