package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoprueba.MainActivity;
import com.example.proyectoprueba.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
            boolean sesionActiva = prefs.getBoolean("sesion_activa", false);

            if (sesionActiva) {
                // Ir a Home si la sesión está activa
                startActivity(new Intent(SplashActivity.this, InicioActivity.class));
            } else {
                // Ir a Login si no hay sesión
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
}
