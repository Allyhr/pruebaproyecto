package com.example.proyectoprueba.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.activities.IAActivity;
import com.example.proyectoprueba.modelos.Mensaje;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_USUARIO = 0;
    private static final int TIPO_BOT = 1;
    private static final int TIPO_CARGANDO = 2;
    private static final int TIPO_OPCIONES = 3;

    private List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = mensajes.get(position);
        if (mensaje.getTipo() == Mensaje.TIPO_CARGANDO) return TIPO_CARGANDO;
        if (mensaje.getTipo() == Mensaje.TIPO_OPCIONES) return TIPO_OPCIONES;
        return mensaje.isEsBot() ? TIPO_BOT : TIPO_USUARIO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TIPO_BOT:
                return new BotViewHolder(inflater.inflate(R.layout.item_mensaje_bot, parent, false));
            case TIPO_USUARIO:
                return new UsuarioViewHolder(inflater.inflate(R.layout.item_mensaje_usuario, parent, false));
            case TIPO_CARGANDO:
                return new CargandoViewHolder(inflater.inflate(R.layout.item_mensaje_cargando, parent, false));
            case TIPO_OPCIONES:
                return new OpcionesViewHolder(inflater.inflate(R.layout.item_mensaje_opciones, parent, false));
            default:
                throw new IllegalArgumentException("Tipo de mensaje no válido");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        switch (holder.getItemViewType()) {
            case TIPO_BOT:
                ((BotViewHolder) holder).tvMensaje.setText(mensaje.getContenido());
                break;
            case TIPO_USUARIO:
                ((UsuarioViewHolder) holder).tvMensaje.setText(mensaje.getContenido());
                break;
            case TIPO_OPCIONES:
                OpcionesViewHolder opcionesHolder = (OpcionesViewHolder) holder;
                opcionesHolder.layoutOpciones.removeAllViews();

                for (String opcion : mensaje.getOpciones()) {
                    Button boton = new Button(opcionesHolder.itemView.getContext());
                    boton.setText(opcion);
                    boton.setOnClickListener(v -> {
                        if (holder.itemView.getContext() instanceof IAActivity) {
                            ((IAActivity) holder.itemView.getContext()).agregarMensajeUsuario(opcion);
                        }
                    });
                    opcionesHolder.layoutOpciones.addView(boton);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        BotViewHolder(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensajeBot);
        }
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        UsuarioViewHolder(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensajeUsuario);
        }
    }

    static class CargandoViewHolder extends RecyclerView.ViewHolder {
        CargandoViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class OpcionesViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutOpciones;
        OpcionesViewHolder(View itemView) {
            super(itemView);
            layoutOpciones = itemView.findViewById(R.id.layoutOpciones);
        }
    }
}
