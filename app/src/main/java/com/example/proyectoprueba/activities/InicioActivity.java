package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoprueba.R;

public class InicioActivity extends AppCompatActivity {

    Button btnCerrarSesion;
    TextView txtBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        txtBienvenida = findViewById(R.id.txtBienvenida);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        txtBienvenida.setText("¡Bienvenido/a a la aplicación!");

        btnCerrarSesion.setOnClickListener(view -> {
            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
            prefs.edit().clear().apply();

            startActivity(new Intent(InicioActivity.this, LoginActivity.class));
            finish();
        });
    }
}
