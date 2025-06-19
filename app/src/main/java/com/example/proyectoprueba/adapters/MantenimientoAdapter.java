package com.example.proyectoprueba.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Mantenimiento;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class MantenimientoAdapter extends RecyclerView.Adapter<MantenimientoAdapter.MantenimientoViewHolder> {

    private List<Mantenimiento> mantenimientos;
    private Context context;
    private OnMantenimientoListener onMantenimientoListener;

    // Agrega este nuevo método para obtener la lista de mantenimientos
    public List<Mantenimiento> getMantenimientos() {
        return mantenimientos;
    }

    public interface OnMantenimientoListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public MantenimientoAdapter(Context context, List<Mantenimiento> mantenimientos, OnMantenimientoListener listener) {
        this.context = context;
        this.mantenimientos = mantenimientos;
        this.onMantenimientoListener = listener;
    }

    @NonNull
    @Override
    public MantenimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mantenimiento, parent, false);
        return new MantenimientoViewHolder(view, onMantenimientoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MantenimientoViewHolder holder, int position) {
        Mantenimiento mantenimiento = mantenimientos.get(position);

        holder.tvPlaca.setText(mantenimiento.getNoPlaca());
        holder.tvFecha.setText(mantenimiento.getFecha());
        holder.tvTipoServicio.setText(mantenimiento.getTipoServicio());
        holder.tvKilometraje.setText(String.format(Locale.getDefault(), "Kilometraje: %,d km", mantenimiento.getKilometraje()));

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        holder.tvCosto.setText(format.format(mantenimiento.getCosto()));
    }

    @Override
    public int getItemCount() {
        return mantenimientos.size();
    }

    public static class MantenimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaca, tvFecha, tvTipoServicio, tvKilometraje, tvCosto;
        com.google.android.material.button.MaterialButton btnEditar, btnEliminar;

        public MantenimientoViewHolder(@NonNull View itemView, OnMantenimientoListener listener) {
            super(itemView);

            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTipoServicio = itemView.findViewById(R.id.tvTipoServicio);
            tvKilometraje = itemView.findViewById(R.id.tvKilometraje);
            tvCosto = itemView.findViewById(R.id.tvCosto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

            btnEditar.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    public void updateList(List<Mantenimiento> newList) {
        mantenimientos = newList;
        notifyDataSetChanged();
    }
}