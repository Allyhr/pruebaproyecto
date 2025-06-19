package com.example.proyectoprueba.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.VehiculoViewHolder> {

    private List<Vehiculo> vehiculos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Vehiculo vehiculo);
        void onDeleteClick(Vehiculo vehiculo);
    }

    public VehiculoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehiculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehiculo, parent, false);
        return new VehiculoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculoViewHolder holder, int position) {
        Vehiculo vehiculo = vehiculos.get(position);

        holder.tvNombre.setText(vehiculo.getAlias());
        holder.tvMarca.setText(vehiculo.getMarca() + " " + vehiculo.getModelo() + " - " + vehiculo.getAnio());

        holder.btnEditar.setOnClickListener(v -> listener.onEditClick(vehiculo));
        holder.btnEliminar.setOnClickListener(v -> listener.onDeleteClick(vehiculo));
    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }

    static class VehiculoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvMarca;
        Button btnEditar, btnEliminar;

        public VehiculoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}