package com.example.codechallenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProblemDetailFragment extends Fragment {
    private ListenerRegistration challengeListener;
    private List<Challenge.TestCase> testCases = new ArrayList<>();
    private String challengeId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_problem_detail, container, false);

        // Obtener el ID del reto desde los argumentos
        Bundle args = getArguments();
        if (args != null) {
            challengeId = args.getString("challengeId", null);
            Log.d("DBG_PROBLEM", "challengeId: " + challengeId);
            Toast.makeText(getContext(), "challengeId: " + challengeId, Toast.LENGTH_SHORT).show();
        }

        TextView tituloView = v.findViewById(R.id.textTituloProblema);
        TextView descripcionView = v.findViewById(R.id.textDescripcionProblema);
        TextView ejemplosView = v.findViewById(R.id.textEjemplos);
        // Si challengeId no es nulo, obtener datos de Firestore
        if (challengeId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            challengeListener = db.collection("challenges").document(challengeId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) {
                        Log.e("DBG_PROBLEM", "Firestore error: " + (e != null ? e.getMessage() : "snapshot null"));
                        Toast.makeText(getContext(), "Firestore error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!snapshot.exists()) {
                        Log.w("DBG_PROBLEM", "Documento no existe para id: " + challengeId);
                        Toast.makeText(getContext(), "Documento no existe", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Obtener y mostrar título y descripción
                    String titulo = snapshot.getString("title");
                    String descripcion = snapshot.getString("description");
                    Log.d("DBG_PROBLEM", "title: " + titulo + ", desc: " + descripcion);
                    Toast.makeText(getContext(), "title: " + titulo, Toast.LENGTH_SHORT).show();
                    if (titulo != null) tituloView.setText(titulo);
                    if (descripcion != null) descripcionView.setText(descripcion);
                    // Obtener test cases (soportar dos formatos: List<Map<String, Object>> y List<String>)
                    Object tcObj = snapshot.get("testCases");
                    Log.d("DBG_PROBLEM", "testCases: " + (tcObj != null ? tcObj.toString() : "null"));
                    Toast.makeText(getContext(), "testCases: " + (tcObj != null ? tcObj.toString() : "null"), Toast.LENGTH_SHORT).show();
                    if (tcObj instanceof List) {
                        List<?> firestoreTestCases = (List<?>) tcObj;
                        testCases.clear();
                        StringBuilder ejemplosBuilder = new StringBuilder();
                        for (Object obj : firestoreTestCases) {
                            if (obj instanceof java.util.Map) {
                                java.util.Map map = (java.util.Map) obj;
                                String input = map.get("input") != null ? map.get("input").toString() : "";
                                String expected = map.get("expectedOutput") != null ? map.get("expectedOutput").toString() : "";
                                Challenge.TestCase testCase = new Challenge.TestCase();
                                testCase.setInput(input);
                                testCase.setExpectedOutput(expected);
                                testCases.add(testCase);
                                ejemplosBuilder.append("Entrada: ").append(input).append("\nSalida esperada: ").append(expected).append("\n\n");
                            } else if (obj instanceof String) {
                                // Soporta formato antiguo: "input => expectedOutput"
                                String[] parts = ((String) obj).split("=>");
                                String input = parts.length > 0 ? parts[0].trim() : "";
                                String expected = parts.length > 1 ? parts[1].trim() : "";
                                Challenge.TestCase testCase = new Challenge.TestCase();
                                testCase.setInput(input);
                                testCase.setExpectedOutput(expected);
                                testCases.add(testCase);
                                ejemplosBuilder.append("Entrada: ").append(input).append("\nSalida esperada: ").append(expected).append("\n\n");
                            }
                        }
                        ejemplosView.setText(ejemplosBuilder.toString());
                    }
                });
        }

        Button btnSend = v.findViewById(R.id.btnSendCode);
        EditText editCodigo = v.findViewById(R.id.editCodigo);
        editCodigo.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ajusta el alto del EditText automáticamente
                editCodigo.post(() -> {
                    int minLines = 4;
                    int maxLines = 20;
                    int lineCount = editCodigo.getLineCount();
                    if (lineCount < minLines) lineCount = minLines;
                    if (lineCount > maxLines) lineCount = maxLines;
                    editCodigo.setLines(lineCount);
                });
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
        btnSend.setOnClickListener(view -> {
            String codigo = editCodigo.getText().toString();
            // Aquí puedes usar testCases para validar la solución
        });

        Button btnSalir = v.findViewById(R.id.btnSalirDetalle);
        btnSalir.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (challengeListener != null) {
            challengeListener.remove();
        }
    }
}
