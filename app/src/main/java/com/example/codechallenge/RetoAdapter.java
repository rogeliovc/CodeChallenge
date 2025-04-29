package com.example.codechallenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;

public class RetoAdapter extends RecyclerView.Adapter<RetoAdapter.RetoViewHolder> {
    private List<Challenge> challenges;
    private Context context;

    public RetoAdapter(List<Challenge> challenges, Context context) {
        this.challenges = challenges;
        this.context = context;
    }

    @NonNull
    @Override
    public RetoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reto, parent, false);
        return new RetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetoViewHolder holder, int position) {
        Challenge reto = challenges.get(position);
        holder.textTitulo.setText(reto.getTitle());
        holder.textDescripcion.setText(reto.getDescription());
        holder.textDificultad.setText(reto.getDifficulty());
        holder.textLenguaje.setText(reto.getLanguage());
        // Fondo del chip de lenguaje (color distinto por lenguaje)
        String lang = reto.getLanguage();
        int colorLang;
        if (lang == null) {
            colorLang = android.graphics.Color.parseColor("#3572A5");
        } else if (lang.equalsIgnoreCase("Python")) {
            colorLang = android.graphics.Color.parseColor("#3572A5");
        } else if (lang.equalsIgnoreCase("Java")) {
            colorLang = android.graphics.Color.parseColor("#b07219");
        } else if (lang.equalsIgnoreCase("C")) {
            colorLang = android.graphics.Color.parseColor("#555555");
        } else if (lang.equalsIgnoreCase("C++")) {
            colorLang = android.graphics.Color.parseColor("#f34b7d");
        } else {
            colorLang = android.graphics.Color.LTGRAY;
        }
        holder.textLenguaje.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorLang));
        // Color del chip de dificultad
        String dificultad = reto.getDifficulty();
        int color;
        if (dificultad == null) {
            color = android.graphics.Color.GRAY;
        } else if (dificultad.equalsIgnoreCase("Fácil")) {
            color = android.graphics.Color.parseColor("#81B29A"); // verde
        } else if (dificultad.equalsIgnoreCase("Medio")) {
            color = android.graphics.Color.parseColor("#F2CC8F"); // naranja
        } else if (dificultad.equalsIgnoreCase("Difícil")) {
            color = android.graphics.Color.parseColor("#E07A5F"); // rojo
        } else {
            color = android.graphics.Color.GRAY;
        }
        holder.textDificultad.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

        holder.itemView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                if (context instanceof MenuPrincipalActivity) {
                    ((MenuPrincipalActivity) context).abrirDetalleProblema(reto.getId());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class RetoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDescripcion, textDificultad, textLenguaje;

        public RetoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTituloReto);
            textDescripcion = itemView.findViewById(R.id.textDescripcionReto);
            textDificultad = itemView.findViewById(R.id.textDificultadReto);
            textLenguaje = itemView.findViewById(R.id.textLenguajeReto);

        }
    }
}
