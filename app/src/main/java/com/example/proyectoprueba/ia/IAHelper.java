package com.example.proyectoprueba.ia;

import android.content.Context;
import android.util.Log;

import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Diagnostico;
import com.example.proyectoprueba.modelos.RespuestaDiagnostico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IAHelper mejorado
public class IAHelper {
    private Map<String, Diagnostico> diagnosticoMap;
    private Context context;
    private List<String> palabrasGenericas;

    public IAHelper(Context context) {
        this.context = context;
        cargarModeloIA();
        inicializarPalabrasGenericas();
    }

    private void inicializarPalabrasGenericas() {
        palabrasGenericas = Arrays.asList(
                "auto", "coche", "vehículo", "carro", "motor",
                "ruido", "sonido", "problema", "falla", "hace"
        );
    }

    private void cargarModeloIA() {
        diagnosticoMap = new HashMap<>();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.ia_model);
            String jsonString = leerArchivo(is);
            JSONObject json = new JSONObject(jsonString);

            JSONArray diagnosticos = json.getJSONArray("diagnosticos");
            for (int i = 0; i < diagnosticos.length(); i++) {
                JSONObject item = diagnosticos.getJSONObject(i);
                Diagnostico diagnostico = new Diagnostico(
                        item.getString("id"),
                        item.getString("titulo"),
                        item.getString("descripcion"),
                        item.getString("solucion"),
                        item.getJSONArray("palabras_clave")
                );
                diagnosticoMap.put(diagnostico.getId(), diagnostico);
            }
        } catch (Exception e) {
            Log.e("IAHelper", "Error cargando modelo IA", e);
        }
    }

    public RespuestaDiagnostico diagnosticarProblema(String descripcionUsuario) {
        descripcionUsuario = preprocesarTexto(descripcionUsuario);

        // 1. Buscar diagnóstico exacto
        for (Diagnostico diagnostico : diagnosticoMap.values()) {
            if (diagnostico.coincideExactamente(descripcionUsuario)) {
                return new RespuestaDiagnostico(diagnostico, 1.0);
            }
        }

        // 2. Buscar por similitud
        List<RespuestaDiagnostico> posibles = new ArrayList<>();
        for (Diagnostico diagnostico : diagnosticoMap.values()) {
            double similitud = diagnostico.calcularSimilitud(descripcionUsuario);
            if (similitud > 0.3) { // Umbral mínimo
                posibles.add(new RespuestaDiagnostico(diagnostico, similitud));
            }
        }

        // 3. Ordenar por mayor similitud
        if (!posibles.isEmpty()) {
            Collections.sort(posibles, (a, b) -> Double.compare(b.getConfianza(), a.getConfianza()));
            return posibles.get(0);
        }

        // 4. Respuesta por defecto
        return new RespuestaDiagnostico(
                new Diagnostico("default", "No estoy seguro",
                        "No pude identificar claramente el problema",
                        "Por favor, proporcione más detalles sobre los síntomas",
                        new JSONArray()),
                0.0
        );
    }

    private String preprocesarTexto(String texto) {
        // Convertir a minúsculas y eliminar caracteres especiales
        return texto.toLowerCase()
                .replaceAll("[^a-záéíóúñ ]", "")
                .trim();
    }

    private String leerArchivo(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}