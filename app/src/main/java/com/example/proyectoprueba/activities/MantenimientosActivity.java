package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoprueba.R;
import com.example.proyectoprueba.adapters.MantenimientoAdapter;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Mantenimiento;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MantenimientosActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MantenimientoAdapter.OnMantenimientoListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MantenimientoAdapter adapter;
    private DatabaseHelper dbHelper;
    private Chip chipVehiculo, chipServicio;
    private boolean buscarPorPlaca = true;
    private FloatingActionButton fabAgregar;
    private TextInputEditText etBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimientos);

        try {
            // Inicializar vistas
            initViews();

            // Configurar toolbar
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                getSupportActionBar().setTitle("Mantenimientos");
            }

            // Configurar NavigationView
            navigationView.setNavigationItemSelectedListener(this);

            // Inicializar base de datos
            dbHelper = new DatabaseHelper(this);

            // Configurar RecyclerView
            setupRecyclerView();

            setupNavigation();

            // Configurar búsqueda simplificada
            setupBusquedaSimplificada();

            // Cargar datos iniciales
            cargarMantenimientos();

            // Configurar FAB
            fabAgregar.setOnClickListener(v -> {
                startActivity(new Intent(this, AddEditMantenimientoActivity.class)
                        .putExtra("MANTENIMIENTO_ID", -1));
            });

        } catch (Exception e) {
            Log.e("MantenimientosActivity", "Error en onCreate: " + e.getMessage());
            Toast.makeText(this, "Error al iniciar la actividad: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewMantenimientos);
        etBuscar = findViewById(R.id.etBuscar); // Cambiado a TextInputEditText
        fabAgregar = findViewById(R.id.fabAgregar);
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);

        // Mostrar información del usuario en el header
        View headerView = navigationView.getHeaderView(0);
        TextView nombreUser = headerView.findViewById(R.id.nombreuser);
        TextView correoUser = headerView.findViewById(R.id.correouser);

        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        nombreUser.setText(preferences.getString("nombre_completo", "Usuario"));
        correoUser.setText(preferences.getString("correo", "contact@example.com"));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MantenimientoAdapter(this, null, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupBusquedaSimplificada() {
        // Búsqueda al presionar enter/action search
        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                realizarBusquedaCombinada();
                return true;
            }
            return false;
        });

        // Búsqueda mientras se escribe (opcional, puedes quitarlo si prefieres solo al presionar buscar)
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                realizarBusquedaCombinada();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void realizarBusquedaCombinada() {
        String query = etBuscar.getText().toString().trim();
        List<Mantenimiento> resultados;

        if (!query.isEmpty()) {
            // Buscar tanto por placa como por servicio
            resultados = dbHelper.buscarMantenimientosCombinada(query);
        } else {
            resultados = dbHelper.obtenerTodosMantenimientos();
        }

        adapter.updateList(resultados);
    }

    private void cargarMantenimientos() {
        List<Mantenimiento> mantenimientos = dbHelper.obtenerTodosMantenimientos();
        adapter.updateList(mantenimientos);
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
            finish();
        } else if (id == R.id.gestion) {
            abrirVehiculos();
        } else if (id == R.id.gestion_man) {
            // Ya estamos en mantenimientos
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.ia) {
            startActivity(new Intent(this, IAActivity.class));
        } else if (id == R.id.talleres_favoritos) {
            startActivity(new Intent(this, TalleresFavoritosActivity.class));
        } else if (id == R.id.configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(int position) {
        Mantenimiento mantenimiento = adapter.getMantenimientos().get(position);
        startActivity(new Intent(this, AddEditMantenimientoActivity.class)
                .putExtra("MANTENIMIENTO_ID", mantenimiento.getId()));
    }

    @Override
    public void onDeleteClick(int position) {
        Mantenimiento mantenimiento = adapter.getMantenimientos().get(position);
        mostrarDialogoConfirmacionEliminar(mantenimiento.getId(), position);
    }

    private void mostrarDialogoConfirmacionEliminar(int idMantenimiento, int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este mantenimiento?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean eliminado = dbHelper.eliminarMantenimiento(idMantenimiento);
                    if (eliminado) {
                        adapter.notifyItemRemoved(position);
                        cargarMantenimientos();
                        Toast.makeText(this, "Mantenimiento eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMantenimientos();
    }
}