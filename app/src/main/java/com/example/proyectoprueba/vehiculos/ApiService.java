package com.example.proyectoprueba.vehiculos;
import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiService {
    private static final String BASE_URL = "http://192.168.1.9/vehiculos_api/";
    private static ApiService instance;
    private RequestQueue queue;

    private ApiService(Context ctx) {
        queue = Volley.newRequestQueue(ctx.getApplicationContext());
    }

    public static synchronized ApiService getInstance(Context ctx) {
        if (instance == null) instance = new ApiService(ctx);
        return instance;
    }

    public void getVehiculos(Response.Listener<JSONArray> onSuccess, Response.ErrorListener onError) {
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL + "get_vehicles.php",
                null, onSuccess, onError
        );
        queue.add(req);
    }

    public void insertVehiculo(JSONObject json, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "insert_vehicle.php",
                json, onSuccess, onError
        );
        queue.add(req);
    }

    public void updateVehiculo(JSONObject json, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "update_vehicle.php",
                json, onSuccess, onError
        );
        queue.add(req);
    }

    public void deleteVehiculo(JSONObject json, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + "delete_vehicle.php",
                json, onSuccess, onError
        );
        queue.add(req);
    }
}
