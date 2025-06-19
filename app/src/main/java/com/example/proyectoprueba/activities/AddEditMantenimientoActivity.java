package com.example.proyectoprueba.activities;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Mantenimiento;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditMantenimientoActivity extends AppCompatActivity {

    private TextView txtTitulo, tvFecha;
    private EditText etPlaca, etKilometraje, etDescripcion, etCosto;
    private Spinner spinnerTipoServicio;
    private Button btnSeleccionarFecha, btnGuardar, btnCancelar;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private int mantenimientoId = -1;

    private AutoCompleteTextView autoPlaca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_mantenimiento);

        // Inicializar vistas
        initViews();

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Configurar spinner de tipos de servicio
        setupSpinnerTipoServicio();

        // Configurar calendario para fecha
        calendar = Calendar.getInstance();

        // Configurar el autocompletado de placas
        setupPlacaAutocomplete();


        // Verificar si es edición o nuevo
        if (getIntent().hasExtra("MANTENIMIENTO_ID")) {
            mantenimientoId = getIntent().getIntExtra("MANTENIMIENTO_ID", -1);
            cargarDatosMantenimiento();
        }

        // Listeners
        btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        btnGuardar.setOnClickListener(v -> guardarMantenimiento());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void initViews() {
        txtTitulo = findViewById(R.id.txtTitulo);
        autoPlaca = findViewById(R.id.autoPlaca);
        spinnerTipoServicio = findViewById(R.id.spinnerTipoServicio);
        tvFecha = findViewById(R.id.tvFecha);
        etKilometraje = findViewById(R.id.etKilometraje);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCosto = findViewById(R.id.etCosto);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupSpinnerTipoServicio() {
        // Lista de tipos de servicio
        String[] tiposServicio = {
                "Cambio de aceite",
                "Rotación de llantas",
                "Alineación y balanceo",
                "Frenos",
                "Suspensión",
                "Transmisión",
                "Diagnóstico eléctrico",
                "Revisión general",
                "Otro"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tiposServicio
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoServicio.setAdapter(adapter);
    }

    private void setupPlacaAutocomplete() {
        List<String> placas = dbHelper.obtenerPlacasUnicas();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                placas
        );

        autoPlaca.setAdapter(adapter);
    }

    private void mostrarDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    actualizarFechaEnVista();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void actualizarFechaEnVista() {
        String formatoFecha = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha, Locale.getDefault());
        tvFecha.setText(sdf.format(calendar.getTime()));
    }

    private void cargarDatosMantenimiento() {
        if (mantenimientoId == -1) return;

        Mantenimiento mantenimiento = dbHelper.obtenerMantenimientoPorId(mantenimientoId);
        if (mantenimiento != null) {
            txtTitulo.setText("Editar Mantenimiento");
            autoPlaca.setText(mantenimiento.getNoPlaca());

            // Seleccionar el tipo de servicio en el spinner
            ArrayAdapter adapter = (ArrayAdapter) spinnerTipoServicio.getAdapter();
            int posicion = adapter.getPosition(mantenimiento.getTipoServicio());
            spinnerTipoServicio.setSelection(posicion >= 0 ? posicion : 0);

            // Configurar fecha
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.setTime(sdf.parse(mantenimiento.getFecha()));
                tvFecha.setText(mantenimiento.getFecha());
            } catch (Exception e) {
                e.printStackTrace();
            }

            etKilometraje.setText(String.valueOf(mantenimiento.getKilometraje()));
            etDescripcion.setText(mantenimiento.getDescripcion());
            etCosto.setText(String.valueOf(mantenimiento.getCosto()));
        }
    }

    private void guardarMantenimiento() {
        // Validar campos
        String placa = autoPlaca.getText().toString().trim();
        String tipoServicio = spinnerTipoServicio.getSelectedItem().toString();
        String fecha = tvFecha.getText().toString();
        String kilometrajeStr = etKilometraje.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String costoStr = etCosto.getText().toString().trim();

        if (placa.isEmpty() || fecha.equals("Seleccionar fecha") ||
                kilometrajeStr.isEmpty() || costoStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int kilometraje = Integer.parseInt(kilometrajeStr);
            double costo = Double.parseDouble(costoStr);

            Mantenimiento mantenimiento = new Mantenimiento();
            mantenimiento.setNoPlaca(placa);
            mantenimiento.setTipoServicio(tipoServicio);
            mantenimiento.setDescripcion(descripcion);
            mantenimiento.setFecha(fecha);
            mantenimiento.setKilometraje(kilometraje);
            mantenimiento.setCosto(costo);

            if (mantenimientoId == -1) {
                // Nuevo mantenimiento
                long id = dbHelper.agregarMantenimiento(mantenimiento);
                if (id != -1) {
                    Toast.makeText(this, "Mantenimiento guardado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Editar mantenimiento
                mantenimiento.setId(mantenimientoId);
                boolean actualizado = dbHelper.actualizarMantenimiento(mantenimiento);
                if (actualizado) {
                    Toast.makeText(this, "Mantenimiento actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato incorrecto en kilometraje o costo", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para crear un Intent para esta actividad
    public static android.content.Intent newIntent(android.content.Context context, int mantenimientoId) {
        android.content.Intent intent = new android.content.Intent(context, AddEditMantenimientoActivity.class);
        intent.putExtra("MANTENIMIENTO_ID", mantenimientoId);
        return intent;
    }
}