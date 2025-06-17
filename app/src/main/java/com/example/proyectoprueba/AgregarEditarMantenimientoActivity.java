package com.example.proyectoprueba;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.Calendar;
import com.example.proyectoprueba.models.Mantenimiento;
import com.example.proyectoprueba.database.DatabaseHelper;

public class AgregarEditarMantenimientoActivity extends AppCompatActivity {
    private Spinner spinnerVehiculo, spinnerTipoServicio;
    private TextView tvFecha;
    private Button btnSeleccionarFecha, btnGuardar, btnCancelar;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;

    private String modo; // "agregar" o "editar"
    private int mantenimientoId = -1;
    private int position = -1;
    private String fechaSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_mantenimiento);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupToolbar();
        obtenerDatosIntent();
        cargarSpinners();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        spinnerVehiculo = findViewById(R.id.spinnerVehiculo);
        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicio);
        tvFecha = findViewById(R.id.tvFecha);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void cargarSpinners() {
        // Cargar spinner de vehículos
        Cursor cursorVehiculos = dbHelper.obtenerTodosVehiculos();
        ArrayList<String> vehiculos = new ArrayList<>();
        vehiculos.add("Seleccione un vehículo");

        if (cursorVehiculos != null && cursorVehiculos.moveToFirst()) {
            do {
                vehiculos.add(cursorVehiculos.getString(1)); // Nombre del vehículo
            } while (cursorVehiculos.moveToNext());
            cursorVehiculos.close();
        }

        ArrayAdapter<String> vehiculosAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, vehiculos);
        vehiculosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehiculo.setAdapter(vehiculosAdapter);

        // Cargar spinner de tipos de servicio
        Cursor cursorServicios = dbHelper.obtenerTodosTiposServicio();
        ArrayList<String> servicios = new ArrayList<>();
        servicios.add("Seleccione un tipo de servicio");

        if (cursorServicios != null && cursorServicios.moveToFirst()) {
            do {
                servicios.add(cursorServicios.getString(1)); // Nombre del servicio
            } while (cursorServicios.moveToNext());
            cursorServicios.close();
        }

        ArrayAdapter<String> serviciosAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, servicios);
        serviciosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoServicio.setAdapter(serviciosAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void obtenerDatosIntent() {
        Intent intent = getIntent();
        modo = intent.getStringExtra("modo");

        if (modo == null) {
            modo = "agregar";
        }

        if (modo.equals("editar")) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Mantenimiento");
            }
            btnGuardar.setText("Actualizar");

            mantenimientoId = intent.getIntExtra("mantenimiento_id", -1);
            position = intent.getIntExtra("position", -1);

            // Seleccionar el vehículo en el spinner
            String vehiculo = intent.getStringExtra("vehiculo");
            ArrayAdapter<String> vehiculosAdapter = (ArrayAdapter<String>) spinnerVehiculo.getAdapter();
            int posicionVehiculo = vehiculosAdapter.getPosition(vehiculo);
            if (posicionVehiculo >= 0) {
                spinnerVehiculo.setSelection(posicionVehiculo);
            }

            // Seleccionar el tipo de servicio en el spinner
            String tipoServicio = intent.getStringExtra("tipo_servicio");
            ArrayAdapter<String> serviciosAdapter = (ArrayAdapter<String>) spinnerTipoServicio.getAdapter();
            int posicionServicio = serviciosAdapter.getPosition(tipoServicio);
            if (posicionServicio >= 0) {
                spinnerTipoServicio.setSelection(posicionServicio);
            }

            fechaSeleccionada = intent.getStringExtra("fecha");
            tvFecha.setText("Fecha seleccionada: " + fechaSeleccionada);

        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Agregar Mantenimiento");
            }
            btnGuardar.setText("Guardar");
            tvFecha.setText("Fecha seleccionada: No seleccionada");
        }
    }

    private void setupListeners() {
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());

        btnGuardar.setOnClickListener(v -> guardarMantenimiento());

        btnCancelar.setOnClickListener(v -> finish());

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    fechaSeleccionada = String.format("%02d/%02d/%d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    tvFecha.setText("Fecha seleccionada: " + fechaSeleccionada);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void guardarMantenimiento() {
        if (validarCampos()) {
            String vehiculo = spinnerVehiculo.getSelectedItem().toString();
            String tipoServicio = spinnerTipoServicio.getSelectedItem().toString();

            // Crear objeto Mantenimiento con los datos
            Mantenimiento mantenimiento = new Mantenimiento();

            if (modo.equals("editar")) {
                mantenimiento.setId(mantenimientoId);
            }

            mantenimiento.setVehiculo(vehiculo);
            mantenimiento.setTipoServicio(tipoServicio);
            mantenimiento.setFecha(fechaSeleccionada);
            // Aquí deberías añadir los demás campos (kilometraje, descripción, costo)

            // Obtener IDs para guardar en la base de datos
            int vehiculoId = dbHelper.obtenerIdVehiculo(vehiculo);
            int tipoServicioId = dbHelper.obtenerIdTipoServicio(tipoServicio);

            // En una app real, aquí guardarías en la base de datos usando los IDs
            // Por ahora, solo retornamos el resultado

            Intent resultIntent = new Intent();
            resultIntent.putExtra("mantenimiento_id", mantenimiento.getId());
            resultIntent.putExtra("vehiculo", mantenimiento.getVehiculo());
            resultIntent.putExtra("tipo_servicio", mantenimiento.getTipoServicio());
            resultIntent.putExtra("fecha", mantenimiento.getFecha());
            // Añadir los demás campos
            resultIntent.putExtra("position", position);
            resultIntent.putExtra("modo", modo);

            setResult(RESULT_OK, resultIntent);

            String mensaje = modo.equals("editar") ?
                    "Mantenimiento actualizado correctamente" :
                    "Mantenimiento agregado correctamente";
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    private boolean validarCampos() {
        if (spinnerVehiculo.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un vehículo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerTipoServicio.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de servicio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Aquí deberías añadir validaciones para los demás campos

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}