package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.adapters.TallerAdapter;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Taller;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class TalleresFavoritosActivity extends AppCompatActivity
        implements TallerAdapter.OnTallerClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private TallerAdapter adapter;
    private List<Taller> talleresList;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talleres_favoritos);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // Icono de hamburguesa
        }

        // Configurar Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(
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

        // Inicializar vistas del contenido principal
        recyclerView = findViewById(R.id.recyclerTalleres);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        talleresList = new ArrayList<>();
        adapter = new TallerAdapter(this, talleresList, this);
        recyclerView.setAdapter(adapter);

        // Inicializar DB Helper
        dbHelper = new DatabaseHelper(this);

        // Configurar SwipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this::cargarTalleres);

        // Cargar datos iniciales
        cargarTalleres();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincronizar el estado del toggle después de onRestoreInstanceState
        toggle.syncState();
    }


    private void cargarTalleres() {
        swipeRefreshLayout.setRefreshing(true);

        Cursor cursor = dbHelper.obtenerTodosLosTalleres();
        talleresList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Taller taller = new Taller();
                int idIndex = cursor.getColumnIndexOrThrow("id");
                int nombreIndex = cursor.getColumnIndexOrThrow("nombre");
                int direccionIndex = cursor.getColumnIndexOrThrow("direccion");
                int latitudIndex = cursor.getColumnIndexOrThrow("latitud");
                int longitudIndex = cursor.getColumnIndexOrThrow("longitud");

                taller.setId(cursor.getInt(idIndex));
                taller.setNombre(cursor.getString(nombreIndex));
                taller.setDireccion(cursor.getString(direccionIndex));
                taller.setLatitud(cursor.getDouble(latitudIndex));
                taller.setLongitud(cursor.getDouble(longitudIndex));

                talleresList.add(taller);
            } while (cursor.moveToNext());

            cursor.close();
        }

        adapter.notifyDataSetChanged();
        tvEmptyState.setVisibility(talleresList.isEmpty() ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onTallerClick(Taller taller) {
        Toast.makeText(this, "Taller seleccionado: " + taller.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminarClick(Taller taller) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar taller")
                .setMessage("¿Estás seguro de eliminar " + taller.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (dbHelper.eliminarTaller(taller.getId())) {
                        cargarTalleres();
                        Toast.makeText(this, "Taller eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
            // Ya estamos aquí, solo cerramos el drawer
        } else if (id == R.id.configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar clic en el icono de la toolbar
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}