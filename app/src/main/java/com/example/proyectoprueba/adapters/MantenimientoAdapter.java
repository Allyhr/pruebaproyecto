package com.example.proyectoprueba.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Mantenimiento;
import com.example.proyectoprueba.models.*;
public class MantenimientoAdapter extends RecyclerView.Adapter<MantenimientoAdapter.MantenimientoViewHolder> {

    private List<Mantenimiento> listaMantenimientos;
    private Context context;
    private OnItemClickListener listener;

    // Interface para manejar clicks
    public interface OnItemClickListener {
        void onEditClick(Mantenimiento mantenimiento, int position);
        void onDeleteClick(Mantenimiento mantenimiento, int position);
    }

    public MantenimientoAdapter(List<Mantenimiento> listaMantenimientos, Context context) {
        this.listaMantenimientos = listaMantenimientos;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MantenimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mantenimiento, parent, false);
        return new MantenimientoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MantenimientoViewHolder holder, int position) {
        Mantenimiento mantenimiento = listaMantenimientos.get(position);

        holder.tvVehiculo.setText("Vehículo: " + mantenimiento.getVehiculo());
        holder.tvTipoServicio.setText("Servicio: " + mantenimiento.getTipoServicio());
        holder.tvFecha.setText("Fecha: " + mantenimiento.getFecha());
        holder.tvKilometraje.setText("Kilometraje: " + mantenimiento.getKilometraje());

        // Click listeners para los botones
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(mantenimiento, position);
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(mantenimiento, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMantenimientos.size();
    }

    // Método para actualizar la lista (útil para búsquedas)
    public void updateList(List<Mantenimiento> nuevaLista) {
        this.listaMantenimientos = nuevaLista;
        notifyDataSetChanged();
    }

    // Método para eliminar un item
    public void removeItem(int position) {
        listaMantenimientos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listaMantenimientos.size());
    }

    // ViewHolder class
    public static class MantenimientoViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehiculo, tvTipoServicio, tvFecha, tvKilometraje;
        Button btnEditar, btnEliminar;

        public MantenimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehiculo = itemView.findViewById(R.id.tvVehiculo);
            tvTipoServicio = itemView.findViewById(R.id.tvTipoServicio);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvKilometraje = itemView.findViewById(R.id.tvKilometraje);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}