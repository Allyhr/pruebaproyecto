package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyectoprueba.R;
import com.google.android.material.navigation.NavigationView;

public class ConfiguracionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Button btnExportarHistorial, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_con_menu);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configurar Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Inicializar botones
        btnExportarHistorial = findViewById(R.id.btnExportarHistorial);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Configurar listeners
        btnExportarHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracionActivity.this, ExportarHistorialActivity.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inicio) {
            startActivity(new Intent(this, menu_users.class));
        } else if (id == R.id.gestion) {
            // Ir a gestión de vehículos
        } else if (id == R.id.gestion_man) {
            // Ir a gestión de mantenimiento
        } else if (id == R.id.ia) {
            // Ir a IA
        } else if (id == R.id.talleres_favoritos) {
            startActivity(new Intent(this, TalleresFavoritosActivity.class));
        } else if (id == R.id.configuracion) {
            // Ya estamos en configuración
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}