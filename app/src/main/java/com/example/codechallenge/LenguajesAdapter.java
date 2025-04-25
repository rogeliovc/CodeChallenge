package com.example.codechallenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LenguajesAdapter extends RecyclerView.Adapter<LenguajesAdapter.LenguajeViewHolder> {
    public interface OnLenguajeClickListener {
        void onLenguajeClick(String lenguaje);
    }
    private List<String> lenguajes;
    private OnLenguajeClickListener listener;

    public LenguajesAdapter(List<String> lenguajes, OnLenguajeClickListener listener) {
        this.lenguajes = lenguajes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LenguajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lenguaje, parent, false);
        return new LenguajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LenguajeViewHolder holder, int position) {
        String lenguaje = lenguajes.get(position);
        holder.textLenguajeNombre.setText(lenguaje);
        // Icono, fondo, tagline y número de retos según lenguaje
        int iconRes;
        int bgRes;
        String tagline;
        String numRetos;
        switch (lenguaje.toLowerCase()) {
            case "python":
                iconRes = R.drawable.ic_lang_python;
                bgRes = R.drawable.bg_card_python;
                tagline = "Versatilidad y simplicidad";
                numRetos = "12 retos";
                break;
            case "java":
                iconRes = R.drawable.ic_lang_java;
                bgRes = R.drawable.bg_card_java;
                tagline = "Robustez multiplataforma";
                numRetos = "10 retos";
                break;
            case "c":
                iconRes = R.drawable.ic_lang_c;
                bgRes = R.drawable.bg_card_c;
                tagline = "Alto rendimiento";
                numRetos = "8 retos";
                break;
            case "c++":
                iconRes = R.drawable.ic_lang_cpp;
                bgRes = R.drawable.bg_card_cpp;
                tagline = "Potencia y control";
                numRetos = "6 retos";
                break;
            default:
                iconRes = R.drawable.ic_code;
                bgRes = R.drawable.bg_card_python;
                tagline = "Lenguaje de programación";
                numRetos = "? retos";
        }
        holder.imgLenguajeIcono.setImageResource(iconRes);
        holder.cardView.setBackgroundResource(bgRes);
        holder.textLenguajeTagline.setText(tagline);
        holder.textLenguajeNumRetos.setText(numRetos);
        // Animación de elevación al presionar
        holder.cardView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    holder.cardView.setCardElevation(18f);
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    holder.cardView.setCardElevation(8f);
                    break;
            }
            return false;
        });
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onLenguajeClick(lenguaje);
        });
    }

    @Override
    public int getItemCount() {
        return lenguajes.size();
    }

    static class LenguajeViewHolder extends RecyclerView.ViewHolder {
        TextView textLenguajeNombre, textLenguajeTagline, textLenguajeNumRetos;
        ImageView imgLenguajeIcono;
        CardView cardView;
        public LenguajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textLenguajeNombre = itemView.findViewById(R.id.textLenguajeNombre);
            imgLenguajeIcono = itemView.findViewById(R.id.imgLenguajeIcono);
            textLenguajeTagline = itemView.findViewById(R.id.textLenguajeTagline);
            textLenguajeNumRetos = itemView.findViewById(R.id.textLenguajeNumRetos);
            cardView = (CardView) itemView;
        }
    }
}
