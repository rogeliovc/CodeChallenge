package com.example.codechallenge;

import android.widget.LinearLayout;

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
        LinearLayout rankingContainer = view.findViewById(R.id.rankingContainer);
        loadRanking(rankingContainer);
        TextView txtExercisesSolved = view.findViewById(R.id.txtExercisesSolved);

        // Inicializar SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, 0);

        // Cargar puntos guardados
        totalPoints = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
        updatePoints();
        uploadPointsToFirestore(totalPoints);

        // Obtener retos resueltos
        java.util.Set<String> solved = sharedPreferences.getStringSet("solved_challenges", new java.util.HashSet<>());
        int solvedCount = solved.size();
        txtExercisesSolved.setText(String.valueOf(solvedCount));

        return view;
    }

    public void addPoints(int points) {
        android.util.Log.d("DBG_STATS", "addPoints llamado con: " + points);

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

    @Override
    public void onResume() {
        android.util.Log.d("DBG_STATS", "onResume llamado");
        super.onResume();
        // Cargar puntos guardados al volver al fragmento
        if (sharedPreferences != null) {
            totalPoints = sharedPreferences.getInt(KEY_TOTAL_POINTS, 0);
            android.util.Log.d("DBG_STATS", "onResume: totalPoints=" + totalPoints);
            updatePoints();
        }
    }

    // Carga el ranking de los 3 usuarios con más puntos y los muestra en el rankingContainer
    private void loadRanking(final LinearLayout rankingContainer) {
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("users")
            .orderBy("total_points", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                rankingContainer.removeAllViews();
                android.content.Context ctx = getContext();
                int pos = 1;
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    String name = doc.contains("displayName") ? doc.getString("displayName") : "Usuario";
                    long points = doc.contains("total_points") ? doc.getLong("total_points") : 0;
                    android.widget.TextView tv = new android.widget.TextView(ctx);
                    tv.setText(pos + ". " + name + " - " + points + " pts");
                    tv.setTextSize(16);
                    tv.setPadding(8, 8, 8, 8);
                    if (pos == 1) tv.setTextColor(android.graphics.Color.parseColor("#FFD700")); // Oro
                    else if (pos == 2) tv.setTextColor(android.graphics.Color.parseColor("#C0C0C0")); // Plata
                    else if (pos == 3) tv.setTextColor(android.graphics.Color.parseColor("#cd7f32")); // Bronce
                    rankingContainer.addView(tv);
                    pos++;
                }
            })
            .addOnFailureListener(e -> {
                rankingContainer.removeAllViews();
                android.widget.TextView tv = new android.widget.TextView(getContext());
                tv.setText("Error cargando ranking");
                rankingContainer.addView(tv);
            });
    }

    // Sube los puntos del usuario actual a Firestore
    private void uploadPointsToFirestore(int points) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();
        String displayName = user.getDisplayName() != null ? user.getDisplayName() : "Usuario";
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("displayName", displayName);
        data.put("total_points", points);
        db.collection("users").document(uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener(aVoid -> android.util.Log.d("DBG_STATS", "Puntos subidos a Firestore"))
            .addOnFailureListener(e -> android.util.Log.e("DBG_STATS", "Error subiendo puntos", e));
    }
}
