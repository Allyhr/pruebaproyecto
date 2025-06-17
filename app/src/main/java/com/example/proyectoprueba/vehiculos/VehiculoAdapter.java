package com.example.proyectoprueba.vehiculos;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;
import java.util.*;

public class VehiculoAdapter extends RecyclerView.Adapter<VehiculoAdapter.ViewHolder> {
    List<Vehiculo> vehiculos;
    Context ctx;
    SqlVehiculos db;

    public VehiculoAdapter(List<Vehiculo> v, Context c, SqlVehiculos db) {
        vehiculos = v; ctx = c; this.db = db;
    }

    public void actualizarLista(List<Vehiculo> n) {
        vehiculos = n; notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlias;
        Button btnEditar, btnEliminar;
        ViewHolder(View v) {
            super(v);
            tvAlias = v.findViewById(R.id.tvNombre);
            btnEditar = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_vehiculo, p, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Vehiculo v = vehiculos.get(pos);
        h.tvAlias.setText(v.getAlias());

        h.btnEliminar.setOnClickListener(vi -> {
            try {
                JSONObject o = new JSONObject();
                o.put("vehiculo_id", v.getId());

                ApiService.getInstance(ctx).deleteVehiculo(o,
                        r -> {
                            try {
                                Toast.makeText(ctx, r.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(ctx, "Eliminado", Toast.LENGTH_SHORT).show();
                            }
                        },
                        e -> Toast.makeText(ctx, "Error eliminar", Toast.LENGTH_SHORT).show()
                );

                db.eliminarVehiculo(v.getId());
                vehiculos.remove(pos);
                notifyItemRemoved(pos);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        h.btnEditar.setOnClickListener(vi -> {
            // implementa enviar 'v' a Formulario para editar
        });
    }


    @Override public int getItemCount() { return vehiculos.size(); }
}
