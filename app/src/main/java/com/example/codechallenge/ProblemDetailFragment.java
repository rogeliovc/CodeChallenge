package com.example.codechallenge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProblemDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_problem_detail, container, false);

        // Ejemplo de cómo puedes actualizar los textos si los recibes por argumentos
        // Bundle args = getArguments();
        // if (args != null) {
        //     ((TextView) v.findViewById(R.id.textTituloProblema)).setText(args.getString("titulo", "Maximum Distance"));
        //     ((TextView) v.findViewById(R.id.textDescripcionProblema)).setText(args.getString("descripcion", "..."));
        //     ((TextView) v.findViewById(R.id.textEjemplos)).setText(args.getString("ejemplos", "..."));
        // }

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
            // Por ahora solo muestra el código ingresado (puedes cambiar esto luego)
            String codigo = editCodigo.getText().toString();
            // Aquí puedes mostrar un Toast, enviar a un servidor, etc.
        });

        Button btnSalir = v.findViewById(R.id.btnSalirDetalle);
        btnSalir.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return v;
    }
}
