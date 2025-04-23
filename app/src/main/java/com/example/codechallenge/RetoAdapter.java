package com.example.codechallenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RetoAdapter extends RecyclerView.Adapter<RetoAdapter.RetoViewHolder> {
    private List<Reto> retos;

    public RetoAdapter(List<Reto> retos) {
        this.retos = retos;
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
        Reto reto = retos.get(position);
        holder.textTitulo.setText(reto.getTitulo());
        holder.textDescripcion.setText(reto.getDescripcion());
        ((CardView)holder.itemView).setCardBackgroundColor(reto.getColor());
    }

    @Override
    public int getItemCount() {
        return retos.size();
    }

    static class RetoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDescripcion;
        public RetoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTituloReto);
            textDescripcion = itemView.findViewById(R.id.textDescripcionReto);
        }
    }
}
