package com.example.proyectoprueba.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Taller;
import java.util.List;
import java.util.Locale;

public class TallerAdapter extends RecyclerView.Adapter<TallerAdapter.TallerViewHolder> {

    private final List<Taller> talleres;
    private final OnTallerClickListener listener;

    public interface OnTallerClickListener {
        void onTallerClick(Taller taller);
        void onEliminarClick(Taller taller);
    }

    public TallerAdapter(Context context, List<Taller> talleres, OnTallerClickListener listener) {
        this.talleres = talleres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TallerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_taller, parent, false);
        return new TallerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TallerViewHolder holder, int position) {
        Taller taller = talleres.get(position);
        holder.bind(taller, listener);
    }

    @Override
    public int getItemCount() {
        return talleres.size();
    }

    static class TallerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDireccion;
        private final TextView tvCoordenadas;
        private final ImageButton btnEliminar;

        public TallerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadas);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(final Taller taller, final OnTallerClickListener listener) {
            // Mostrar la dirección completa
            tvDireccion.setText(taller.getDireccion());

            // Formatear las coordenadas
            String coordenadas = String.format(Locale.getDefault(),
                    "Coordenadas: %.6f, %.6f",
                    taller.getLatitud(),
                    taller.getLongitud());
            tvCoordenadas.setText(coordenadas);

            // Configurar clics
            itemView.setOnClickListener(v -> listener.onTallerClick(taller));
            btnEliminar.setOnClickListener(v -> listener.onEliminarClick(taller));
        }
    }
}