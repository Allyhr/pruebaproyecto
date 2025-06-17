package com.example.proyectoprueba;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
import com.example.proyectoprueba.models.Mantenimiento;
import com.example.proyectoprueba.AgregarEditarMantenimientoActivity;
public class AgregarEditarMantenimientoActivity extends AppCompatActivity {
    private EditText etVehiculo, etTipoServicio, etKilometraje, etDescripcion, etCosto;
    private TextView tvFecha;
    private Button btnSeleccionarFecha, btnGuardar, btnCancelar;
    private Toolbar toolbar;

    private String modo; // "agregar" o "editar"
    private int mantenimientoId = -1;
    private int position = -1;
    private String fechaSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_editar_mantenimiento);

        initViews();
        setupToolbar();
        obtenerDatosIntent();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etVehiculo = findViewById(R.id.etVehiculo);
        etTipoServicio = findViewById(R.id.etTipoServicio);
        etKilometraje = findViewById(R.id.etKilometraje);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCosto = findViewById(R.id.etCosto);
        tvFecha = findViewById(R.id.tvFecha);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
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
            // Configurar para modo editar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Mantenimiento");
            }
            btnGuardar.setText("Actualizar");

            // Cargar datos existentes
            mantenimientoId = intent.getIntExtra("mantenimiento_id", -1);
            position = intent.getIntExtra("position", -1);

            etVehiculo.setText(intent.getStringExtra("vehiculo"));
            etTipoServicio.setText(intent.getStringExtra("tipo_servicio"));
            etKilometraje.setText(intent.getStringExtra("kilometraje"));
            etDescripcion.setText(intent.getStringExtra("descripcion"));
            etCosto.setText(String.valueOf(intent.getDoubleExtra("costo", 0.0)));

            fechaSeleccionada = intent.getStringExtra("fecha");
            tvFecha.setText("Fecha seleccionada: " + fechaSeleccionada);

        } else {
            // Configurar para modo agregar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Agregar Mantenimiento");
            }
            btnGuardar.setText("Guardar");
            tvFecha.setText("Fecha seleccionada: No seleccionada");
        }
    }

    private void setupListeners() {
        btnSeleccionarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarMantenimiento();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener para el botón de regreso en el toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formato DD/MM/YYYY
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
            // Crear objeto Mantenimiento con los datos
            Mantenimiento mantenimiento = new Mantenimiento();

            if (modo.equals("editar")) {
                mantenimiento.setId(mantenimientoId);
            }

            mantenimiento.setVehiculo(etVehiculo.getText().toString().trim());
            mantenimiento.setTipoServicio(etTipoServicio.getText().toString().trim());
            mantenimiento.setFecha(fechaSeleccionada);
            mantenimiento.setKilometraje(etKilometraje.getText().toString().trim());
            mantenimiento.setDescripcion(etDescripcion.getText().toString().trim());

            try {
                double costo = Double.parseDouble(etCosto.getText().toString().trim());
                mantenimiento.setCosto(costo);
            } catch (NumberFormatException e) {
                mantenimiento.setCosto(0.0);
            }

            // En una app real, aquí guardarías en la base de datos
            // Por ahora, solo retornamos el resultado

            Intent resultIntent = new Intent();
            resultIntent.putExtra("mantenimiento_id", mantenimiento.getId());
            resultIntent.putExtra("vehiculo", mantenimiento.getVehiculo());
            resultIntent.putExtra("tipo_servicio", mantenimiento.getTipoServicio());
            resultIntent.putExtra("fecha", mantenimiento.getFecha());
            resultIntent.putExtra("kilometraje", mantenimiento.getKilometraje());
            resultIntent.putExtra("descripcion", mantenimiento.getDescripcion());
            resultIntent.putExtra("costo", mantenimiento.getCosto());
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
        if (etVehiculo.getText().toString().trim().isEmpty()) {
            etVehiculo.setError("El vehículo es requerido");
            etVehiculo.requestFocus();
            return false;
        }

        if (etTipoServicio.getText().toString().trim().isEmpty()) {
            etTipoServicio.setError("El tipo de servicio es requerido");
            etTipoServicio.requestFocus();
            return false;
        }

        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona una fecha", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etKilometraje.getText().toString().trim().isEmpty()) {
            etKilometraje.setError("El kilometraje es requerido");
            etKilometraje.requestFocus();
            return false;
        }

        if (etCosto.getText().toString().trim().isEmpty()) {
            etCosto.setError("El costo es requerido");
            etCosto.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(etCosto.getText().toString().trim());
        } catch (NumberFormatException e) {
            etCosto.setError("Ingresa un costo válido");
            etCosto.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
