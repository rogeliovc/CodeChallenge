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
import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import okhttp3.MediaType;
import android.app.AlertDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProblemDetailFragment extends Fragment {
    private ListenerRegistration challengeListener;
    private List<Challenge.TestCase> testCases = new ArrayList<>();
    private String challengeId;
    private final OkHttpClient client = new OkHttpClient();
    private static final String RAPIDAPI_KEY = "3fb615a497msh37ec6351aad9227p1137a1jsnbbc131296c1e";
    private static final String RAPIDAPI_HOST = "judge0-ce.p.rapidapi.com";
    private static final String JUDGE0_URL = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=false&wait=false";

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
            if (testCases == null || testCases.isEmpty()) {
                return;
            }
            // Evaluar todos los test cases
            List<String> resultados = new ArrayList<>();
            final int[] completados = {0};
            for (int i = 0; i < testCases.size(); i++) {
                Challenge.TestCase tc = testCases.get(i);
                String inputTest = tc.getInput();
                String expected = tc.getExpectedOutput();
                String languageId = "50"; // Por ahora C (puedes mejorar esto luego)
                int idx = i;
                evaluarCodigoConJudge0(codigo, languageId, inputTest, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        requireActivity().runOnUiThread(() -> {
                            resultados.add("Test " + (idx+1) + ": Error en Judge0: " + e.getMessage());
                            completados[0]++;
                            if (completados[0] == testCases.size()) mostrarResultados(resultados);
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject resp = new JSONObject(response.body().string());
                            String output = resp.optString("stdout", "");
                            String error = resp.optString("stderr", "");
                            // Normaliza output y expected
                            String normOutput = output.trim().replaceAll("\\r\\n", "\\n").replaceAll("\\r", "\\n");
                            String normExpected = expected.trim().replaceAll("\\r\\n", "\\n").replaceAll("\\r", "\\n");
                            boolean ok = normOutput.equals(normExpected);
                            String res = "Test " + (idx+1) + ": " + (ok ? "✔️ PASA" : "❌ FALLA") +
                                    "\nEntrada: " + inputTest +
                                    "\nEsperado: " + normExpected +
                                    "\nSalida: " + normOutput +
                                    (error.isEmpty() ? "" : ("\nError: " + error));
                            requireActivity().runOnUiThread(() -> {
                                resultados.add(res);
                                completados[0]++;
                                if (completados[0] == testCases.size()) mostrarResultados(resultados);
                            });
                        } catch (JSONException e) {
                            requireActivity().runOnUiThread(() -> {
                                resultados.add("Test " + (idx+1) + ": Error JSON Judge0: " + e.getMessage());
                                completados[0]++;
                                if (completados[0] == testCases.size()) mostrarResultados(resultados);
                            });
                        }
                    }
                });
            }
        });

        Button btnSalir = v.findViewById(R.id.btnSalirDetalle);
        btnSalir.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return v;
    }

    private void evaluarCodigoConJudge0(String sourceCode, String languageId, String stdin, okhttp3.Callback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("source_code", sourceCode);
            json.put("language_id", Integer.parseInt(languageId)); // 50 para C
            json.put("stdin", stdin);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Request request = new Request.Builder()
                .url(JUDGE0_URL)
                .post(body)
                .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", RAPIDAPI_HOST)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(call, new IOException("Unexpected code " + response));
                    return;
                }
                try {
                    JSONObject resp = new JSONObject(response.body().string());
                    String token = resp.getString("token");
                    obtenerResultadoJudge0(token, callback);
                } catch (JSONException e) {
                    callback.onFailure(call, new IOException("JSON error: " + e.getMessage()));
                }
            }
        });
    }

    private void obtenerResultadoJudge0(String token, okhttp3.Callback callback) {
        String url = "https://judge0-ce.p.rapidapi.com/submissions/" + token + "?base64_encoded=false";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", RAPIDAPI_HOST)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(call, new IOException("Unexpected code " + response));
                    return;
                }
                String respStr = response.body().string();
                try {
                    JSONObject resp = new JSONObject(respStr);
                    int statusId = resp.getJSONObject("status").getInt("id");
                    if (statusId < 3) {
                        // Si hay error, detén el polling
                        if (statusId == 13 || statusId == 11) { // Compilation error o runtime error
                            // Crea un nuevo response con el mismo body para pasar al callback
                            Response newResponse = response.newBuilder()
                                .body(ResponseBody.create(response.body().contentType(), respStr))
                                .build();
                            callback.onResponse(call, newResponse);
                            return;
                        }
                        // Todavía procesando, espera y vuelve a consultar
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                        obtenerResultadoJudge0(token, callback);
                    } else {
                        // Listo, crea un nuevo response con el mismo body para pasar al callback
                        Response newResponse = response.newBuilder()
                            .body(ResponseBody.create(response.body().contentType(), respStr))
                            .build();
                        callback.onResponse(call, newResponse);
                    }
                } catch (JSONException e) {
                    callback.onFailure(call, new IOException("JSON error: " + e.getMessage()));
                }
            }
        });
    }

    private void mostrarResultados(List<String> resultados) {
        // Adaptar a TestResult list
        List<TestResult> testResultList = new ArrayList<>();
        for (String r : resultados) {
            // Parsear el string (mejorable si se pasa objeto directamente)
            // Espera: "Test 1: ✔️ PASA\nEntrada: ...\nEsperado: ...\nSalida: ...\n[Error: ...]"
            String[] lines = r.split("\n");
            String title = lines[0];
            String input = lines.length > 1 ? lines[1].replace("Entrada: ", "") : "";
            String expected = lines.length > 2 ? lines[2].replace("Esperado: ", "") : "";
            String output = lines.length > 3 ? lines[3].replace("Salida: ", "") : "";
            String error = (lines.length > 4 && lines[4].startsWith("Error:")) ? lines[4].replace("Error: ", "") : "";
            boolean passed = title.contains("✔️");
            testResultList.add(new TestResult(title, input, expected, output, error, passed));
        }
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_test_results, null);
        RecyclerView recycler = view.findViewById(R.id.recyclerResults);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new TestResultAdapter(getContext(), testResultList));
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (challengeListener != null) {
            challengeListener.remove();
        }
    }
}
