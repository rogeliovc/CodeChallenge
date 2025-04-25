package com.example.codechallenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RetoAdapter extends RecyclerView.Adapter<RetoAdapter.RetoViewHolder> {
    private List<Challenge> challenges;

    public RetoAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
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
        // Icono según lenguaje
        String lang = reto.getLanguage();
        if (lang == null) {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_python);
        } else if (lang.equalsIgnoreCase("Python")) {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_python);
        } else if (lang.equalsIgnoreCase("Java")) {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_java);
        } else if (lang.equalsIgnoreCase("C")) {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_c);
        } else if (lang.equalsIgnoreCase("C++")) {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_cpp);
        } else {
            holder.iconLenguaje.setImageResource(R.drawable.ic_lang_python);
        }
        // Fondo del chip de lenguaje (color distinto por lenguaje)
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
    }


    @Override
    public int getItemCount() {
        return challenges.size();
    }

    static class RetoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDescripcion, textDificultad, textLenguaje;
        ImageView iconLenguaje;
        public RetoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTituloReto);
            textDescripcion = itemView.findViewById(R.id.textDescripcionReto);
            textDificultad = itemView.findViewById(R.id.textDificultadReto);
            textLenguaje = itemView.findViewById(R.id.textLenguajeReto);
            iconLenguaje = itemView.findViewById(R.id.iconLenguajeReto);
        }
    }
}
