package com.example.codechallenge;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder> {
    private final List<TestResult> results;
    private final Context context;

    public TestResultAdapter(Context context, List<TestResult> results) {
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_test_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestResult result = results.get(position);
        holder.txtTestTitle.setText(result.title);
        holder.txtInput.setText("Entrada: " + result.input);
        holder.txtExpected.setText("Esperado: " + result.expected);
        holder.txtOutput.setText("Salida: " + result.output);
        if (result.error != null && !result.error.isEmpty()) {
            holder.txtError.setVisibility(View.VISIBLE);
            holder.txtError.setText("Error: " + result.error);
        } else {
            holder.txtError.setVisibility(View.GONE);
        }
        if (result.passed) {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
            holder.imgStatus.setColorFilter(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.imgStatus.setImageResource(android.R.drawable.ic_delete);
            holder.imgStatus.setColorFilter(context.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStatus;
        TextView txtTestTitle, txtInput, txtExpected, txtOutput, txtError;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtTestTitle = itemView.findViewById(R.id.txtTestTitle);
            txtInput = itemView.findViewById(R.id.txtInput);
            txtExpected = itemView.findViewById(R.id.txtExpected);
            txtOutput = itemView.findViewById(R.id.txtOutput);
            txtError = itemView.findViewById(R.id.txtError);
        }
    }
}
