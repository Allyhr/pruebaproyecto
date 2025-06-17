package com.example.proyectoprueba.vehiculos;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import com.example.proyectoprueba.R;


public class FormularioVehiculoActivity extends AppCompatActivity {

    EditText etNombre, etPlacas, etAnio, etKilometraje;
    Spinner spMarca, spModelo, spColor, spTransmision, spCombustible;
    Button btnGuardar, btnCancelar, btnSincronizar;
    SqlVehiculos db;
    boolean editar = false;
    Vehiculo veh;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_formulario_vehiculo);

        // Enlazar EditTexts
        etNombre = findViewById(R.id.etNombre);
        etPlacas = findViewById(R.id.etPlacas);
        etAnio = findViewById(R.id.etAnio);
        etKilometraje = findViewById(R.id.etKilometraje);

        // Enlazar Spinners
        spMarca = findViewById(R.id.spMarca);
        spModelo = findViewById(R.id.spModelo);
        spColor = findViewById(R.id.spColor);
        spTransmision = findViewById(R.id.spTransmision);
        spCombustible = findViewById(R.id.spCombustible);

        // Enlazar Botones
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnSincronizar = findViewById(R.id.btnSincronizar);

        db = new SqlVehiculos(this);

        // Evento Guardar
        btnGuardar.setOnClickListener(v -> {
            try {
                int idVehiculo = (editar && veh != null) ? veh.getId() : 0;

                veh = new Vehiculo(
                        idVehiculo,
                        etNombre.getText().toString(),
                        spMarca.getSelectedItemPosition() + 1,
                        spModelo.getSelectedItemPosition() + 1,
                        Integer.parseInt(etAnio.getText().toString()),
                        etPlacas.getText().toString(),
                        spColor.getSelectedItemPosition() + 1,
                        Integer.parseInt(etKilometraje.getText().toString()),
                        spTransmision.getSelectedItemPosition() + 1,
                        spCombustible.getSelectedItemPosition() + 1
                );

                if (!editar) {
                    db.insertarVehiculo(veh);
                    ApiService.getInstance(this).insertVehiculo(veh.toJson(),
                            res -> Toast.makeText(this, "Insertado", Toast.LENGTH_SHORT).show(),
                            err -> Toast.makeText(this, "Error al sincronizar", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    db.actualizarVehiculo(veh);
                    ApiService.getInstance(this).updateVehiculo(veh.toJson(),
                            res -> Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show(),
                            err -> Toast.makeText(this, "Error al sincronizar", Toast.LENGTH_SHORT).show()
                    );
                }

                finish();

            } catch (JSONException | NumberFormatException e) {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
            }
        });

        // Evento Cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Evento Sincronizar
        btnSincronizar.setOnClickListener(v -> {
            // Aquí puedes agregar la lógica de sincronización manual si la necesitas
            Toast.makeText(this, "Sincronización manual aún no implementada", Toast.LENGTH_SHORT).show();
        });

        // (Opcional) Cargar datos de ejemplo en los Spinners
        cargarDatosSpinner();
    }

    private void cargarDatosSpinner() {
        ArrayAdapter<String> adapterSimple = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Seleccione", "Opción 1", "Opción 2", "Opción 3"});
        adapterSimple.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spMarca.setAdapter(adapterSimple);
        spModelo.setAdapter(adapterSimple);
        spColor.setAdapter(adapterSimple);
        spTransmision.setAdapter(adapterSimple);
        spCombustible.setAdapter(adapterSimple);
    }
}
