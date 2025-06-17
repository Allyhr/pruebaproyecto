package com.example.proyectoprueba.vehiculos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivityVehiculos extends AppCompatActivity {
    RecyclerView recyclerVehiculos;
    FloatingActionButton fabAgregar;
    VehiculoAdapter adapter;
    SqlVehiculos db;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);

        recyclerVehiculos = findViewById(R.id.recyclerVehiculos);
        fabAgregar = findViewById(R.id.fabAgregar);
        db = new SqlVehiculos(this);

        recyclerVehiculos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VehiculoAdapter(new ArrayList<>(), this, db);
        recyclerVehiculos.setAdapter(adapter);

        fabAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, FormularioVehiculoActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLocales();
        cargarServidores();
    }

    private void cargarLocales() {
        List<Vehiculo> lista = db.obtenerVehiculos();
        adapter.actualizarLista(lista);
    }

    private void cargarServidores() {
        ApiService.getInstance(this).getVehiculos(
                response -> {
                    List<Vehiculo> lista = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject o = response.getJSONObject(i);
                            lista.add(Vehiculo.fromJson(o));
                        }
                        adapter.actualizarLista(lista);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error procesando datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error servidor", Toast.LENGTH_SHORT).show()
        );
    }

}


