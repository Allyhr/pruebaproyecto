package com.example.proyectoprueba.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.api.VehiculoService;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Vehiculo;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class FormularioVehiculoActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etAnio, etPlacas, etKilometraje;
    private Spinner spMarca, spModelo, spColor, spTransmision, spCombustible;
    private Button btnGuardar, btnCancelar;
    private DatabaseHelper dbHelper;
    private Vehiculo vehiculoEditar;
    private long usuarioId;
    private VehiculoService vehiculoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_vehiculo);


        dbHelper = new DatabaseHelper(this);
        usuarioId = getIntent().getLongExtra("USUARIO_ID", -1); // Usando getLongExtra

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: No se identificó al usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Inicializa el servicio
        vehiculoService = new VehiculoService(this);

        initViews();
        setupSpinners();
        checkEdicion();
        setupListeners();
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etAnio = findViewById(R.id.etAnio);
        etPlacas = findViewById(R.id.etPlacas);
        etKilometraje = findViewById(R.id.etKilometraje);

        spMarca = findViewById(R.id.spMarca);
        spModelo = findViewById(R.id.spModelo);
        spColor = findViewById(R.id.spColor);
        spTransmision = findViewById(R.id.spTransmision);
        spCombustible = findViewById(R.id.spCombustible);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupSpinners() {
        // Configurar spinner de marcas
        ArrayAdapter<CharSequence> marcaAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.marcas_vehiculos,
                android.R.layout.simple_spinner_item
        );
        marcaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMarca.setAdapter(marcaAdapter);

        // Configurar listener para actualizar modelos
        spMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ignorar el primer item (hint)
                    actualizarModelos(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar otros spinners
        configurarSpinnerSimple(spColor, R.array.colores_vehiculos);
        configurarSpinnerSimple(spTransmision, R.array.transmisiones);
        configurarSpinnerSimple(spCombustible, R.array.combustibles);
    }

    private void configurarSpinnerSimple(Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResource,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void actualizarModelos(String marca) {
        int arrayModelos = obtenerArrayModelos(marca);
        if (arrayModelos != 0) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    arrayModelos,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spModelo.setAdapter(adapter);

            // Si estamos editando, seleccionar el modelo guardado
            if (vehiculoEditar != null && marca.equals(vehiculoEditar.getMarca())) {
                seleccionarItemSpinner(spModelo, vehiculoEditar.getModelo());
            }
        }
    }

    private int obtenerArrayModelos(String marca) {
        switch (marca) {
            case "Toyota": return R.array.modelos_toyota;
            case "Honda": return R.array.modelos_honda;
            case "Ford": return R.array.modelos_ford;
            case "Chevrolet": return R.array.modelos_chevrolet;
            case "Nissan": return R.array.modelos_nissan;
            case "Volkswagen": return R.array.modelos_volkswagen;
            case "Hyundai": return R.array.modelos_hyundai;
            case "Kia": return R.array.modelos_kia;
            default: return R.array.modelos_default;
        }
    }

    private void checkEdicion() {
        if (getIntent().hasExtra("VEHICULO_ID")) {
            long id = getIntent().getLongExtra("VEHICULO_ID", -1);
            if (id != -1) {
                vehiculoEditar = dbHelper.obtenerVehiculoPorId(id);
                if (vehiculoEditar != null) {
                    llenarFormulario(vehiculoEditar);
                }
            }
        }
    }

    private void llenarFormulario(Vehiculo vehiculo) {
        etNombre.setText(vehiculo.getAlias());
        etAnio.setText(String.valueOf(vehiculo.getAnio()));
        etPlacas.setText(vehiculo.getPlaca());

        // Seleccionar valores en spinners
        seleccionarItemSpinner(spMarca, vehiculo.getMarca());
        seleccionarItemSpinner(spColor, vehiculo.getColor());
        seleccionarItemSpinner(spTransmision, vehiculo.getTransmision());
        seleccionarItemSpinner(spCombustible, vehiculo.getCombustible());

        if (vehiculo.getKilometraje() > 0) {
            etKilometraje.setText(String.valueOf(vehiculo.getKilometraje()));
        }
    }

    private void seleccionarItemSpinner(Spinner spinner, String valor) {
        if (valor != null && !valor.isEmpty()) {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(valor)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupListeners() {
        btnGuardar.setOnClickListener(v -> guardarVehiculo());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void guardarVehiculo() {
        if (!validarCampos()) {
            return;
        }

        Vehiculo vehiculo = new Vehiculo();
        if (vehiculoEditar != null) {
            vehiculo.setId(vehiculoEditar.getId());
        }
        vehiculo.setUsuarioId(usuarioId);
        vehiculo.setAlias(etNombre.getText().toString());
        vehiculo.setMarca(spMarca.getSelectedItem().toString());
        vehiculo.setModelo(spModelo.getSelectedItem().toString());
        vehiculo.setAnio(Integer.parseInt(etAnio.getText().toString()));
        vehiculo.setPlaca(etPlacas.getText().toString());
        vehiculo.setColor(spColor.getSelectedItem().toString());

        if (!etKilometraje.getText().toString().isEmpty()) {
            vehiculo.setKilometraje(Integer.parseInt(etKilometraje.getText().toString()));
        }

        vehiculo.setTransmision(spTransmision.getSelectedItem().toString());
        vehiculo.setCombustible(spCombustible.getSelectedItem().toString());

        guardarEnBaseDatos(vehiculo);
    }

    private boolean validarCampos() {
        boolean valido = true;

        // Validar nombre
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre es obligatorio");
            valido = false;
        } else {
            etNombre.setError(null);
        }

        // Validar año
        if (etAnio.getText().toString().trim().isEmpty()) {
            etAnio.setError("Año es obligatorio");
            valido = false;
        } else {
            etAnio.setError(null);
        }

        // Validar marca
        if (spMarca.getSelectedItemPosition() <= 0) {
            ((TextView)spMarca.getSelectedView()).setError("Seleccione una marca");
            valido = false;
        } else {
            ((TextView)spMarca.getSelectedView()).setError(null);
        }

        // Validar modelo
        if (spModelo.getSelectedItemPosition() <= 0) {
            ((TextView)spModelo.getSelectedView()).setError("Seleccione un modelo");
            valido = false;
        } else {
            ((TextView)spModelo.getSelectedView()).setError(null);
        }

        return valido;
    }

    // En FormularioVehiculoActivity.java
    private void guardarEnBaseDatos(Vehiculo vehiculo) {
        if (vehiculoEditar != null) {
            // Actualizar vehículo existente
            vehiculoService.actualizarVehiculo(vehiculo, new VehiculoService.VehiculoCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    runOnUiThread(() -> {
                        Toast.makeText(FormularioVehiculoActivity.this, "Vehículo actualizado", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(FormularioVehiculoActivity.this, "Error al actualizar: " + error, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else {
            // Crear nuevo vehículo
            vehiculoService.crearVehiculo(vehiculo, new VehiculoService.VehiculoCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    runOnUiThread(() -> {
                        try {
                            long id = response.getLong("id");
                            vehiculo.setId(id);
                            Toast.makeText(FormularioVehiculoActivity.this, "Vehículo creado", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(FormularioVehiculoActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(FormularioVehiculoActivity.this, "Error al crear: " + error, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}