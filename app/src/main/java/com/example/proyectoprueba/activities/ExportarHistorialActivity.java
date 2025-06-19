package com.example.proyectoprueba.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.proyectoprueba.R;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Mantenimiento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ExportarHistorialActivity extends AppCompatActivity {

    private Spinner spinnerPlacas;
    private RadioButton radioTxt, radioCsv;
    private EditText editNombreArchivo;
    private Button btnExportar, btnCompartir, btnVerArchivo;
    private TextView txtArchivoGenerado;
    private DatabaseHelper dbHelper;
    private File archivoExportado;
    private Uri uriArchivoExportado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_historial);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupSpinnerPlacas();
        setupButtons();
    }

    private void initViews() {
        spinnerPlacas = findViewById(R.id.spinnerPlacas);
        radioTxt = findViewById(R.id.radioTxt);
        radioCsv = findViewById(R.id.radioCsv);
        editNombreArchivo = findViewById(R.id.editNombreArchivo);
        btnExportar = findViewById(R.id.btnExportar);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerArchivo = findViewById(R.id.btnVerArchivo);
        txtArchivoGenerado = findViewById(R.id.txtArchivoGenerado);

        // Inicialmente deshabilitar botones de compartir y ver
        btnCompartir.setEnabled(false);
        btnVerArchivo.setEnabled(false);
    }

    private void setupSpinnerPlacas() {
        List<String> placas = dbHelper.obtenerPlacasUnicas();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                placas
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlacas.setAdapter(adapter);
    }

    private void setupButtons() {
        btnExportar.setOnClickListener(v -> exportarHistorial());
        btnCompartir.setOnClickListener(v -> compartirArchivo());
        btnVerArchivo.setOnClickListener(v -> verArchivo());
    }

    private void exportarHistorial() {
        String placa = spinnerPlacas.getSelectedItem().toString();
        String nombreArchivo = editNombreArchivo.getText().toString().trim();
        boolean esTxt = radioTxt.isChecked();

        if (nombreArchivo.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre para el archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Mantenimiento> historial = dbHelper.obtenerMantenimientosPorPlaca(placa);

        if (historial.isEmpty()) {
            Toast.makeText(this, "No hay registros para esta placa", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder contenido = new StringBuilder();

        if (esTxt) {
            // Formato TXT
            contenido.append("Historial de Mantenimientos - Placa: ").append(placa).append("\n\n");

            for (Mantenimiento m : historial) {
                contenido.append("Fecha: ").append(m.getFecha()).append("\n");
                contenido.append("Servicio: ").append(m.getTipoServicio()).append("\n");
                contenido.append("Kilometraje: ").append(m.getKilometraje()).append(" km\n");
                contenido.append("Costo: $").append(String.format(Locale.getDefault(), "%.2f", m.getCosto())).append("\n");
                contenido.append("Descripción: ").append(m.getDescripcion()).append("\n\n");
            }
        } else {
            // Formato CSV
            contenido.append("Fecha,Servicio,Kilometraje,Costo,Descripción\n");

            for (Mantenimiento m : historial) {
                String descripcion = m.getDescripcion() != null ?
                        m.getDescripcion().replace("\"", "\"\"") : "";

                contenido.append("\"").append(m.getFecha()).append("\",");
                contenido.append("\"").append(m.getTipoServicio()).append("\",");
                contenido.append(m.getKilometraje()).append(",");
                contenido.append(String.format(Locale.getDefault(), "%.2f", m.getCosto())).append(",");
                contenido.append("\"").append(descripcion).append("\"\n");
            }
        }

        String extension = esTxt ? ".txt" : ".csv";

        try {
            // Usar el directorio interno de la aplicación
            File directorio = new File(getFilesDir(), "MantenimientosExportados");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            archivoExportado = new File(directorio, nombreArchivo + extension);
            try (FileOutputStream fos = new FileOutputStream(archivoExportado)) {
                fos.write(contenido.toString().getBytes());
            }

            // Generar URI para compartir/ver
            uriArchivoExportado = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    archivoExportado);

            // Actualizar UI
            runOnUiThread(() -> {
                txtArchivoGenerado.setText("Archivo generado: " + archivoExportado.getName());
                btnCompartir.setEnabled(true);
                btnVerArchivo.setEnabled(true);
                Toast.makeText(this, "Archivo exportado con éxito", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Error al exportar el archivo", Toast.LENGTH_SHORT).show());
        }
    }

    private void compartirArchivo() {
        if (uriArchivoExportado == null) {
            Toast.makeText(this, "Primero exporte un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(getContentType());
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriArchivoExportado);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(shareIntent, "Compartir archivo"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay aplicaciones para compartir", Toast.LENGTH_SHORT).show();
        }
    }

    private void verArchivo() {
        if (uriArchivoExportado == null) {
            Toast.makeText(this, "Primero exporte un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        viewIntent.setDataAndType(uriArchivoExportado, getContentType());
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No hay aplicaciones para visualizar este archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private String getContentType() {
        return radioTxt.isChecked() ? "text/plain" : "text/csv";
    }
}