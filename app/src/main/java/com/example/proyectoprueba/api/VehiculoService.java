package com.example.proyectoprueba.api;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.proyectoprueba.modelos.Vehiculo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class VehiculoService {
    private static final String BASE_URL = "http://192.168.0.8:5000/"; // Usando tu IP local y puerto 5000
    private final Context context;

    public VehiculoService(Context context) {
        this.context = context;
    }

    public interface VehiculoCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public interface VehiculosListCallback {
        void onSuccess(JSONArray response);
        void onError(String error);
    }

    public void obtenerVehiculos(long usuarioId, VehiculosListCallback callback) {
        String url = BASE_URL + "vehiculos/usuario/" + usuarioId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    public void crearVehiculo(Vehiculo vehiculo, VehiculoCallback callback) {
        String url = BASE_URL + "vehiculos";

        try {
            JSONObject body = new JSONObject();
            body.put("usuario_id", vehiculo.getUsuarioId());
            body.put("alias", vehiculo.getAlias());
            body.put("marca", vehiculo.getMarca());
            body.put("modelo", vehiculo.getModelo());
            body.put("anio", vehiculo.getAnio());
            body.put("placa", vehiculo.getPlaca());
            body.put("color", vehiculo.getColor());
            body.put("kilometraje", vehiculo.getKilometraje());
            body.put("transmision", vehiculo.getTransmision());
            body.put("combustible", vehiculo.getCombustible());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, body,
                    callback::onSuccess,
                    error -> callback.onError(error.toString())
            );

            ApiClient.getInstance(context).addToRequestQueue(request);
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void actualizarVehiculo(Vehiculo vehiculo, VehiculoCallback callback) {
        String url = BASE_URL + "vehiculos/" + vehiculo.getId();

        try {
            JSONObject body = new JSONObject();
            body.put("alias", vehiculo.getAlias());
            body.put("marca", vehiculo.getMarca());
            body.put("modelo", vehiculo.getModelo());
            body.put("anio", vehiculo.getAnio());
            body.put("placa", vehiculo.getPlaca());
            body.put("color", vehiculo.getColor());
            body.put("kilometraje", vehiculo.getKilometraje());
            body.put("transmision", vehiculo.getTransmision());
            body.put("combustible", vehiculo.getCombustible());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT, url, body,
                    callback::onSuccess,
                    error -> callback.onError(error.toString())
            );

            ApiClient.getInstance(context).addToRequestQueue(request);
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void eliminarVehiculo(long vehiculoId, VehiculoCallback callback) {
        String url = BASE_URL + "vehiculos/" + vehiculoId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }

    public void buscarVehiculos(long usuarioId, String query, VehiculosListCallback callback) {
        String url = BASE_URL + "vehiculos/buscar?usuario_id=" + usuarioId + "&query=" + query;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                callback::onSuccess,
                error -> callback.onError(error.toString())
        );

        ApiClient.getInstance(context).addToRequestQueue(request);
    }
}