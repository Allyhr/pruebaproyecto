package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoprueba.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Verificar si hay sesión activa
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

            Intent intent;
            if (isLoggedIn) {
                // Si hay sesión
                intent = new Intent(SplashActivity.this, menu_users.class);
            } else {
                // Si no hay sesión, ir al LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}