package com.example.smartchili;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<HistoryModel> list;

    public HistoryAdapter(ArrayList<HistoryModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryModel data = list.get(position);

        holder.txtJudul.setText("Pengeringan #" + (position + 1));
        holder.txtMulai.setText("Mulai : " + formatWaktu(data.mulai));

        if (data.selesai > 0) {
            holder.txtSelesai.setText("Selesai : " + formatWaktu(data.selesai));
        } else {
            holder.txtSelesai.setText("Selesai : -");
        }

        holder.txtTargetItem.setText("Target Suhu : " + data.targetSuhu + " °C");
        holder.txtStatusItem.setText("Status : " + data.status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String formatWaktu(long waktu) {
        if (waktu <= 0) return "-";

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"));
        return sdf.format(new Date(waktu));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtJudul, txtMulai, txtSelesai, txtTargetItem, txtStatusItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtMulai = itemView.findViewById(R.id.txtMulai);
            txtSelesai = itemView.findViewById(R.id.txtSelesai);
            txtTargetItem = itemView.findViewById(R.id.txtTargetItem);
            txtStatusItem = itemView.findViewById(R.id.txtStatusItem);
        }
    }
}