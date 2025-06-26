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

public class IAHelper {
    private static final String TAG = "IAHelper";
    private Map<String, Diagnostico> diagnosticoMap;
    private Context context;
    private List<String> palabrasGenericas;
    private Map<String, List<String>> sinonimos;
    private Map<String, String> palabraBase;
    private Diagnostico ultimoDiagnosticoMostrado;
    private boolean esperandoConfirmacion = false;


    public IAHelper(Context context) {
        this.context = context;
        this.diagnosticoMap = new HashMap<>();
        this.sinonimos = new HashMap<>();
        this.palabraBase = new HashMap<>();
        cargarModeloIA();
        inicializarPalabrasGenericas();
        inicializarSinonimos();
    }

    public Diagnostico getUltimoDiagnosticoMostrado() {
        return ultimoDiagnosticoMostrado;
    }

    public boolean isEsperandoConfirmacion() {
        return esperandoConfirmacion;
    }

    private void inicializarPalabrasGenericas() {
        palabrasGenericas = Arrays.asList(
                "auto", "coche", "vehículo", "carro", "motor",
                "ruido", "sonido", "problema", "falla", "hace",
                "tengo", "siento", "escucho", "veo", "noto"
        );
    }

    private void inicializarSinonimos() {
        agregarSinonimos("auto", "coche", "vehículo", "carro", "automóvil");
        agregarSinonimos("huele", "olor", "hedor", "aroma");
        agregarSinonimos("quemado", "quemazón", "chamuscado", "tostado");
        agregarSinonimos("frenar", "detener", "parar", "ralentizar");
        agregarSinonimos("acelerar", "avanzar", "acelerón", "pisar el acelerador");
        agregarSinonimos("motor", "propulsor", "máquina", "bloque motor");
        agregarSinonimos("ruido", "sonido", "estruendo", "estridor");
        agregarSinonimos("problema", "falla", "avería", "defecto");
        agregarSinonimos("volante", "dirección", "timón", "manubrio");
        agregarSinonimos("frenos", "sistema de frenado", "disco de freno", "pastillas");
        agregarSinonimos("chirrido", "chillido", "silbido", "rechinamiento");
        agregarSinonimos("vibración", "temblor", "sacudida", "oscilación");
        agregarSinonimos("humo", "humareda", "vaho", "neblina");
        agregarSinonimos("temperatura", "calor", "sobrecalentamiento", "fiebre del motor");
        agregarSinonimos("líquido", "fluido", "aceite", "refrigerante");
    }

    private void agregarSinonimos(String palabraBase, String... sinonimos) {
        this.sinonimos.put(palabraBase, Arrays.asList(sinonimos));
        for (String sinonimo : sinonimos) {
            this.palabraBase.put(sinonimo, palabraBase);
        }
    }

    private void cargarModeloIA() {
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
            Log.e(TAG, "Error cargando modelo IA", e);
        }
    }

    public RespuestaDiagnostico diagnosticarProblema(String descripcionUsuario) {
        // Manejar respuestas de confirmación
        if (esperandoConfirmacion) {
            return manejarRespuestaConfirmacion(descripcionUsuario);
        }

        String textoNormalizado = normalizarTexto(descripcionUsuario);
        Log.d(TAG, "Texto normalizado: " + textoNormalizado);

        // 1. Buscar diagnóstico exacto
        for (Diagnostico diagnostico : diagnosticoMap.values()) {
            if (coincidenciaExactaMejorada(diagnostico, textoNormalizado)) {
                Log.d(TAG, "Coincidencia exacta encontrada: " + diagnostico.getTitulo());
                ultimoDiagnosticoMostrado = diagnostico;
                esperandoConfirmacion = true;
                return new RespuestaDiagnostico(diagnostico, 1.0);
            }
        }

        // 2. Buscar por similitud mejorada
        List<RespuestaDiagnostico> posibles = new ArrayList<>();
        for (Diagnostico diagnostico : diagnosticoMap.values()) {
            double similitud = calcularSimilitudMejorada(diagnostico, textoNormalizado);
            if (similitud > 0.3) {
                posibles.add(new RespuestaDiagnostico(diagnostico, similitud));
                Log.d(TAG, "Posible diagnóstico: " + diagnostico.getTitulo() + " - Similitud: " + similitud);
            }
        }

        // 3. Ordenar por mayor similitud
        if (!posibles.isEmpty()) {
            Collections.sort(posibles, (a, b) -> Double.compare(b.getConfianza(), a.getConfianza()));
            RespuestaDiagnostico mejor = posibles.get(0);
            ultimoDiagnosticoMostrado = mejor.getDiagnostico();
            esperandoConfirmacion = mejor.getConfianza() < 0.7;

            if (mejor.getConfianza() < 0.7 && posibles.size() > 1) {
                double diferencia = mejor.getConfianza() - posibles.get(1).getConfianza();
                if (diferencia < 0.2) {
                    mejor.setConfianza(mejor.getConfianza() * 0.9);
                }
            }

            return mejor;
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

    private RespuestaDiagnostico manejarRespuestaConfirmacion(String respuestaUsuario) {
        esperandoConfirmacion = false;

        if (respuestaUsuario.equalsIgnoreCase("Sí, exactamente eso") && ultimoDiagnosticoMostrado != null) {
            return new RespuestaDiagnostico(
                    new Diagnostico("confirmado", "Diagnóstico confirmado",
                            "Has confirmado que el problema es: " + ultimoDiagnosticoMostrado.getTitulo(),
                            ultimoDiagnosticoMostrado.getSolucion(),
                            new JSONArray()),
                    1.0
            );
        } else if (respuestaUsuario.equalsIgnoreCase("Parcialmente, pero también...")) {
            return new RespuestaDiagnostico(
                    new Diagnostico("mas_info", "Más información necesaria",
                            "Por favor, describe qué otros síntomas has notado",
                            "Esperando más detalles para refinar el diagnóstico",
                            new JSONArray()),
                    0.0
            );
        } else if (respuestaUsuario.equalsIgnoreCase("No, es algo diferente")) {
            ultimoDiagnosticoMostrado = null;
            return new RespuestaDiagnostico(
                    new Diagnostico("reiniciar", "Nuevo diagnóstico",
                            "Por favor, describe nuevamente los síntomas",
                            "Vamos a comenzar un nuevo diagnóstico",
                            new JSONArray()),
                    0.0
            );
        } else {
            // Si la respuesta no es ninguna de las opciones esperadas, tratar como nuevo síntoma
            return diagnosticarProblema(respuestaUsuario);
        }
    }

    private boolean coincidenciaExactaMejorada(Diagnostico diagnostico, String texto) {
        for (String palabraClave : diagnostico.getPalabrasClave()) {
            if (texto.contains(palabraClave)) {
                return true;
            }

            String[] palabras = palabraClave.split(" ");
            boolean todasCoinciden = true;
            for (String palabra : palabras) {
                if (!texto.contains(palabra)) {
                    boolean sinonimoEncontrado = false;
                    if (sinonimos.containsKey(palabra)) {
                        for (String sinonimo : sinonimos.get(palabra)) {
                            if (texto.contains(sinonimo)) {
                                sinonimoEncontrado = true;
                                break;
                            }
                        }
                    }
                    if (!sinonimoEncontrado) {
                        todasCoinciden = false;
                        break;
                    }
                }
            }
            if (todasCoinciden) {
                return true;
            }
        }
        return false;
    }

    private double calcularSimilitudMejorada(Diagnostico diagnostico, String texto) {
        double maxSimilitud = 0;
        int totalPalabrasClave = diagnostico.getPalabrasClave().size();
        int coincidencias = 0;

        for (String palabraClave : diagnostico.getPalabrasClave()) {
            double similitudPalabraClave = calcularSimilitudPalabraClave(palabraClave, texto);
            if (similitudPalabraClave > 0) {
                coincidencias++;
            }
            if (similitudPalabraClave > maxSimilitud) {
                maxSimilitud = similitudPalabraClave;
            }
        }

        return (maxSimilitud * 0.6) + ((double) coincidencias / totalPalabrasClave * 0.4);
    }

    private double calcularSimilitudPalabraClave(String palabraClave, String texto) {
        String[] palabrasClave = palabraClave.split(" ");
        String[] palabrasTexto = texto.split(" ");
        double mejorSimilitud = 0;

        for (int i = 0; i <= palabrasTexto.length - palabrasClave.length; i++) {
            double similitudActual = 0;
            boolean secuenciaValida = true;

            for (int j = 0; j < palabrasClave.length; j++) {
                String palabraTextoActual = palabrasTexto[i + j];
                String palabraClaveActual = palabrasClave[j];
                double similitudPalabra = calcularSimilitudPalabra(palabraClaveActual, palabraTextoActual);

                if (similitudPalabra < 0.5) {
                    secuenciaValida = false;
                    break;
                }
                similitudActual += similitudPalabra;
            }

            if (secuenciaValida) {
                similitudActual /= palabrasClave.length;
                if (similitudActual > mejorSimilitud) {
                    mejorSimilitud = similitudActual;
                }
            }
        }
        return mejorSimilitud;
    }

    private double calcularSimilitudPalabra(String palabra1, String palabra2) {
        if (palabra1.equals(palabra2)) return 1.0;
        if (sonSinonimos(palabra1, palabra2)) return 0.9;
        if (palabra1.contains(palabra2) || palabra2.contains(palabra1)) return 0.7;

        int distancia = distanciaLevenshtein(palabra1, palabra2);
        int longitudMax = Math.max(palabra1.length(), palabra2.length());

        if (distancia <= 1 && longitudMax >= 3) return 0.8;
        if (distancia <= 2 && longitudMax >= 4) return 0.6;
        return 0.0;
    }

    private boolean sonSinonimos(String palabra1, String palabra2) {
        if (sinonimos.containsKey(palabra1) && sinonimos.get(palabra1).contains(palabra2)) return true;
        if (sinonimos.containsKey(palabra2) && sinonimos.get(palabra2).contains(palabra1)) return true;

        String base1 = palabraBase.getOrDefault(palabra1, palabra1);
        String base2 = palabraBase.getOrDefault(palabra2, palabra2);
        return base1.equals(base2);
    }

    private String normalizarTexto(String texto) {
        String normalizado = texto.toLowerCase()
                .replaceAll("[^a-záéíóúñ ]", "")
                .trim();

        String[] palabras = normalizado.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (palabraBase.containsKey(palabra)) {
                resultado.append(palabraBase.get(palabra)).append(" ");
            } else {
                resultado.append(palabra).append(" ");
            }
        }

        return resultado.toString().trim();
    }

    private int distanciaLevenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int costo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + costo);
            }
        }
        return dp[a.length()][b.length()];
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