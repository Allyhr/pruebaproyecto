package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        // Mostrar información del usuario en el header
        View headerView = navigationView.getHeaderView(0);
        TextView nombreUser = headerView.findViewById(R.id.nombreuser);
        TextView correoUser = headerView.findViewById(R.id.correouser);

        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        nombreUser.setText(preferences.getString("nombre_completo", "Usuario"));
        correoUser.setText(preferences.getString("correo", "contact@example.com"));

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

    // ==================== [ MÉTODOS DE NAVEGACIÓN ] ====================
    private void abrirVehiculos() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        long userId = preferences.getLong("user_id", -1);
        if(userId != -1) {
            Intent intent = new Intent(this, MainVehiculoActivity.class);
            intent.putExtra("USUARIO_ID", userId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Error al identificar al usuario", Toast.LENGTH_SHORT).show();
            // Redirigir al login si no hay usuario
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inicio) {
            startActivity(new Intent(this, menu_users.class));
        } else if (id == R.id.gestion) {
            abrirVehiculos();
        } else if (id == R.id.gestion_man) {
            startActivity(new Intent(this, MantenimientosActivity.class));
        } else if (id == R.id.ia) {
            startActivity(new Intent(this, IAActivity.class));
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