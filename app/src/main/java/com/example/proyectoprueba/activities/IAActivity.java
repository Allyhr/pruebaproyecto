package com.example.proyectoprueba.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.adapters.MensajeAdapter;
import com.example.proyectoprueba.ia.IAHelper;
import com.example.proyectoprueba.modelos.Diagnostico;
import com.example.proyectoprueba.modelos.Mensaje;
import com.example.proyectoprueba.modelos.RespuestaDiagnostico;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IAActivity extends AppCompatActivity {

    private static final String TAG = "IAActivity";

    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private MensajeAdapter adapter;
    private List<Mensaje> listaMensajes;
    private IAHelper iaHelper;
    private Handler handler = new Handler();
    private Diagnostico ultimoDiagnostico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        try {
            iaHelper = new IAHelper(this);
            rvMensajes = findViewById(R.id.rvMensajes);
            etMensaje = findViewById(R.id.etMensaje);
            listaMensajes = new ArrayList<>();

            adapter = new MensajeAdapter(listaMensajes);
            rvMensajes.setLayoutManager(new LinearLayoutManager(this));
            rvMensajes.setAdapter(adapter);

            mostrarMensajeBienvenida();

            findViewById(R.id.btnEnviar).setOnClickListener(v -> enviarMensaje());

        } catch (Exception e) {
            Log.e(TAG, "Error al iniciar el asistente", e);
            Toast.makeText(this, "Error al iniciar el asistente: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void mostrarMensajeBienvenida() {
        agregarMensajeBot("¡Hola! Soy tu asistente de diagnóstico vehicular. ¿Qué problema tiene tu vehículo?");
        agregarMensajeBot("Puedes describirlo con tus palabras o elegir una opción común:");

        List<String> opcionesIniciales = Arrays.asList(
                "Huele a quemado al acelerar o frenar",
                "El motor da tirones o pierde potencia",
                "El auto no enciende (batería descargada)",
                "El motor se sobrecalienta",
                "Sale humo azul por el escape",
                "Volante duro o dirección difícil de girar",
                "Frenos chillan al detenerse",
                "Ruidos en la suspensión al pasar baches",
                "Transmisión automática cambia mal o resbala",
                "El auto se va hacia un lado al manejar",
                "Luz de check engine encendida",
                "Hay manchas o fugas debajo del coche",
                "Ruidos extraños al arrancar el auto",
                "El volante vibra al conducir",
                "Huele a gasolina dentro o fuera del auto",
                "Pedal de freno se siente esponjoso o blando",
                "El aire acondicionado no enfría",
                "Chirrido de correa al encender o girar",
                "Motor inestable o revoluciones bajan en reposo",
                "Luces parpadean o dejan de funcionar",
                "Llantas desgastadas de forma irregular"
        );


        listaMensajes.add(new Mensaje(opcionesIniciales, true, Mensaje.TIPO_OPCIONES));
        adapter.notifyItemInserted(listaMensajes.size() - 1);
    }

    private void enviarMensaje() {
        String mensajeUsuario = etMensaje.getText().toString().trim();
        if (!mensajeUsuario.isEmpty()) {
            Log.d(TAG, "Enviando mensaje: " + mensajeUsuario);
            agregarMensajeUsuario(mensajeUsuario);
            etMensaje.setText("");
            procesarMensajeConIA(mensajeUsuario);
        }
    }

    public void procesarMensajeConIA(String mensaje) {
        mostrarCargando();

        new Thread(() -> {
            try {
                Log.d(TAG, "Procesando mensaje con IA: " + mensaje);
                RespuestaDiagnostico respuesta = iaHelper.diagnosticarProblema(mensaje);

                handler.post(() -> {
                    ocultarCargando();
                    if (respuesta != null) {
                        mostrarRespuestaIA(respuesta);
                    } else {
                        mostrarErrorIA();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error en el procesamiento de IA", e);
                handler.post(() -> {
                    ocultarCargando();
                    mostrarErrorIA();
                });
            }
        }).start();
    }

    private void mostrarRespuestaIA(RespuestaDiagnostico respuesta) {
        Log.d(TAG, "Mostrando respuesta IA: " + respuesta.getDiagnostico().getTitulo());
        agregarMensajeBot(respuesta.formatearRespuesta());

        if (respuesta.getConfianza() < 0.7) {
            Log.d(TAG, "Mostrando opciones de seguimiento por baja confianza");
            mostrarOpcionesSeguimiento();
        }
    }

    private void mostrarErrorIA() {
        agregarMensajeBot("Lo siento, hubo un problema al procesar tu solicitud. Por favor, inténtalo de nuevo.");
    }

    private void mostrarCargando() {
        Log.d(TAG, "Mostrando estado de carga");
        listaMensajes.add(new Mensaje("", true, Mensaje.TIPO_CARGANDO));
        adapter.notifyItemInserted(listaMensajes.size() - 1);
        rvMensajes.scrollToPosition(listaMensajes.size() - 1);
    }

    private void ocultarCargando() {
        Log.d(TAG, "Ocultando estado de carga");
        if (!listaMensajes.isEmpty() &&
                listaMensajes.get(listaMensajes.size() - 1).getTipo() == Mensaje.TIPO_CARGANDO) {
            listaMensajes.remove(listaMensajes.size() - 1);
            adapter.notifyItemRemoved(listaMensajes.size());
        }
    }

    private void mostrarOpcionesSeguimiento() {
        List<String> opciones = Arrays.asList(
                "Sí, exactamente eso",
                "Parcialmente, pero también...",
                "No, es algo diferente"
        );

        listaMensajes.add(new Mensaje(opciones, true, Mensaje.TIPO_OPCIONES));
        adapter.notifyItemInserted(listaMensajes.size() - 1);
        rvMensajes.scrollToPosition(listaMensajes.size() - 1);
    }

    public void agregarMensajeUsuario(String mensaje) {
        Log.d(TAG, "Agregando mensaje de usuario: " + mensaje);
        listaMensajes.add(new Mensaje(mensaje, false, Mensaje.TIPO_TEXTO));
        adapter.notifyItemInserted(listaMensajes.size() - 1);
        rvMensajes.scrollToPosition(listaMensajes.size() - 1);
    }

    private void agregarMensajeBot(String mensaje) {
        Log.d(TAG, "Agregando mensaje del bot: " + mensaje);
        listaMensajes.add(new Mensaje(mensaje, true, Mensaje.TIPO_TEXTO));
        adapter.notifyItemInserted(listaMensajes.size() - 1);
        rvMensajes.scrollToPosition(listaMensajes.size() - 1);
    }

    public void confirmarDiagnosticoAnterior() {
        if (ultimoDiagnostico != null) {
            // Aumentar confianza y mostrar mensaje de confirmación
            agregarMensajeBot("¡Gracias por confirmar! El diagnóstico es correcto:\n\n" +
                    "**" + ultimoDiagnostico.getTitulo() + "**\n\n" +
                    "Solución recomendada: " + ultimoDiagnostico.getSolucion());
        } else {
            agregarMensajeBot("No tengo un diagnóstico previo para confirmar. Por favor describe el problema.");
        }
    }

    public void solicitarMasInformacion() {
        agregarMensajeBot("Por favor, describe qué otros síntomas has notado:");
    }

    public void reiniciarDiagnostico() {
        ultimoDiagnostico = null;
        agregarMensajeBot("Entendido. Vamos a empezar de nuevo. Por favor, describe el problema:");
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destruyendo actividad");
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}