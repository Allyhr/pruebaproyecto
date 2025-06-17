package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportarHistorialActivity extends AppCompatActivity {

    private Spinner spinnerVehiculos;
    private RadioButton radioTxt, radioCsv;
    private EditText editNombreArchivo;
    private Button btnExportar, btnCompartir, btnVerArchivo;
    private TextView txtArchivoGenerado;

    private String archivoGeneradoRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_historial);

        spinnerVehiculos = findViewById(R.id.spinnerVehiculos);
        radioTxt = findViewById(R.id.radioTxt);
        radioCsv = findViewById(R.id.radioCsv);
        editNombreArchivo = findViewById(R.id.editNombreArchivo);
        btnExportar = findViewById(R.id.btnExportar);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerArchivo = findViewById(R.id.btnVerArchivo);
        txtArchivoGenerado = findViewById(R.id.txtArchivoGenerado);

        // Simulación de carga de vehículos (esto se reemplazará con datos reales más adelante)
        String[] vehiculos = {"Toyota Corolla", "Nissan Versa", "Honda Civic"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vehiculos);
        spinnerVehiculos.setAdapter(adapter);

        btnExportar.setOnClickListener(v -> exportarArchivo());
        btnCompartir.setOnClickListener(v -> compartirArchivo());
        btnVerArchivo.setOnClickListener(v -> verArchivo());
    }

    private void exportarArchivo() {
        String nombreArchivo = editNombreArchivo.getText().toString();
        if (nombreArchivo.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        String extension = radioTxt.isChecked() ? ".txt" : ".csv";
        String nombreCompleto = nombreArchivo + extension;

        File directorio = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File archivo = new File(directorio, nombreCompleto);

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            // Datos simulados (aquí irán los mantenimientos del vehículo seleccionado)
            String contenido = "Vehículo: " + spinnerVehiculos.getSelectedItem().toString() + "\n" +
                    "Mantenimiento 1: Cambio de aceite\n" +
                    "Mantenimiento 2: Revisión general\n";

            fos.write(contenido.getBytes());
            Toast.makeText(this, "Archivo exportado correctamente", Toast.LENGTH_SHORT).show();

            archivoGeneradoRuta = archivo.getAbsolutePath();
            txtArchivoGenerado.setText(nombreCompleto);

        } catch (IOException e) {
            Toast.makeText(this, "Error al exportar archivo", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void compartirArchivo() {
        if (archivoGeneradoRuta == null) {
            Toast.makeText(this, "Primero exporta un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        File archivo = new File(archivoGeneradoRuta);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivo);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Compartir archivo"));
    }

    private void verArchivo() {
        if (archivoGeneradoRuta == null) {
            Toast.makeText(this, "Primero exporta un archivo", Toast.LENGTH_SHORT).show();
            return;
        }

        File archivo = new File(archivoGeneradoRuta);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", archivo);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
