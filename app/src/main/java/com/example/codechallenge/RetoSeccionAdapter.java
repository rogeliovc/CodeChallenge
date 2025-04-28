package com.example.codechallenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RetoSeccionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static class Seccion {
        public String titulo;
        public List<Challenge> retos;
        public Seccion(String titulo, List<Challenge> retos) {
            this.titulo = titulo;
            this.retos = retos;
        }
    }

    private List<Object> items = new ArrayList<>();

    public RetoSeccionAdapter(List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            items.add(seccion.titulo);
            items.addAll(seccion.retos);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_header_dificultad, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reto, parent, false);
            return new RetoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((HeaderViewHolder) holder).textHeader.setText((String) items.get(position));
        } else {
            Challenge reto = (Challenge) items.get(position);
            RetoViewHolder vh = (RetoViewHolder) holder;
            vh.textTitulo.setText(reto.getTitle());
            vh.textDescripcion.setText(reto.getDescription());
            vh.textDificultad.setText(reto.getDifficulty());
            vh.textLenguaje.setText(reto.getLanguage());
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
            vh.textDificultad.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

            // Hacer clic en el reto abre el detalle
            vh.itemView.setOnClickListener(v -> {
                if (v.getContext() instanceof MenuPrincipalActivity) {
                    ((MenuPrincipalActivity) v.getContext()).abrirDetalleProblema();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.textHeaderDificultad);
        }
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
