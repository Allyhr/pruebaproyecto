package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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
import com.example.proyectoprueba.adapters.VehiculoAdapter;
import com.example.proyectoprueba.api.VehiculoService;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Vehiculo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainVehiculoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private RecyclerView recyclerVehiculos;
    private EditText etBuscar;
    private FloatingActionButton fabAgregar;

    private DatabaseHelper dbHelper;
    private VehiculoAdapter adapter;
    private long usuarioId;
    private List<Vehiculo> listaVehiculosCompleta;
    private VehiculoService vehiculoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vehiculo);

        // Obtener el ID del usuario
        usuarioId = getIntent().getLongExtra("USUARIO_ID", -1);
        if(usuarioId == -1) {
            Toast.makeText(this, "Error: No se identificó al usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializa el servicio
        vehiculoService = new VehiculoService(this);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupToolbar();
        setupDrawerToggle(); // Configurar el toggle del drawer
        setupNavigation();
        setupRecyclerView();
        setupListeners();
        setupBusqueda();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarVehiculos();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        recyclerVehiculos = findViewById(R.id.recyclerVehiculos);
        etBuscar = findViewById(R.id.etBuscar);
        fabAgregar = findViewById(R.id.fabAgregar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gestión de Vehículos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupDrawerToggle() {
        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        // Agregar esto para mostrar la información del usuario en el header
        View headerView = navigationView.getHeaderView(0);
        TextView nombreUser = headerView.findViewById(R.id.nombreuser);
        TextView correoUser = headerView.findViewById(R.id.correouser);

        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        nombreUser.setText(preferences.getString("nombre_completo", "Usuario"));
        correoUser.setText(preferences.getString("correo", "contact@example.com"));
    }

    private void setupRecyclerView() {
        adapter = new VehiculoAdapter(new VehiculoAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Vehiculo vehiculo) {
                editarVehiculo(vehiculo);
            }

            @Override
            public void onDeleteClick(Vehiculo vehiculo) {
                eliminarVehiculo(vehiculo);
            }
        });

        recyclerVehiculos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVehiculos.setAdapter(adapter);
    }

    private void setupListeners() {
        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainVehiculoActivity.this, FormularioVehiculoActivity.class);
            intent.putExtra("USUARIO_ID", usuarioId);
            startActivity(intent);
        });
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

    private void setupBusqueda() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarVehiculos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filtrarVehiculos(String textoBusqueda) {
        vehiculoService.buscarVehiculos(usuarioId, textoBusqueda, new VehiculoService.VehiculosListCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                runOnUiThread(() -> {
                    List<Vehiculo> vehiculos = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonVehiculo = response.getJSONObject(i);
                            Vehiculo vehiculo = new Vehiculo();
                            vehiculo.setId(jsonVehiculo.getLong("id"));
                            vehiculo.setUsuarioId(usuarioId);
                            vehiculo.setAlias(jsonVehiculo.getString("alias"));
                            vehiculo.setMarca(jsonVehiculo.getString("marca"));
                            vehiculo.setModelo(jsonVehiculo.getString("modelo"));
                            vehiculo.setAnio(jsonVehiculo.getInt("anio"));
                            vehiculo.setPlaca(jsonVehiculo.getString("placa"));
                            vehiculo.setColor(jsonVehiculo.getString("color"));
                            vehiculo.setKilometraje(jsonVehiculo.getInt("kilometraje"));
                            vehiculo.setTransmision(jsonVehiculo.getString("transmision"));
                            vehiculo.setCombustible(jsonVehiculo.getString("combustible"));

                            vehiculos.add(vehiculo);
                        }

                        adapter.setVehiculos(vehiculos);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(MainVehiculoActivity.this, "Error al procesar la búsqueda", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(MainVehiculoActivity.this, "Error en búsqueda: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void cargarVehiculos() {
        vehiculoService.obtenerVehiculos(usuarioId, new VehiculoService.VehiculosListCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                runOnUiThread(() -> {
                    List<Vehiculo> vehiculos = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonVehiculo = response.getJSONObject(i);
                            Vehiculo vehiculo = new Vehiculo();
                            vehiculo.setId(jsonVehiculo.getLong("id"));
                            vehiculo.setUsuarioId(usuarioId);
                            vehiculo.setAlias(jsonVehiculo.getString("alias"));
                            vehiculo.setMarca(jsonVehiculo.getString("marca"));
                            vehiculo.setModelo(jsonVehiculo.getString("modelo"));
                            vehiculo.setAnio(jsonVehiculo.getInt("anio"));
                            vehiculo.setPlaca(jsonVehiculo.getString("placa"));
                            vehiculo.setColor(jsonVehiculo.getString("color"));
                            vehiculo.setKilometraje(jsonVehiculo.getInt("kilometraje"));
                            vehiculo.setTransmision(jsonVehiculo.getString("transmision"));
                            vehiculo.setCombustible(jsonVehiculo.getString("combustible"));

                            vehiculos.add(vehiculo);
                        }

                        listaVehiculosCompleta = vehiculos;
                        adapter.setVehiculos(vehiculos);
                        adapter.notifyDataSetChanged();

                        if (!etBuscar.getText().toString().isEmpty()) {
                            filtrarVehiculos(etBuscar.getText().toString());
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainVehiculoActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(MainVehiculoActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void editarVehiculo(Vehiculo vehiculo) {
        Intent intent = new Intent(this, FormularioVehiculoActivity.class);
        intent.putExtra("VEHICULO_ID", vehiculo.getId());
        intent.putExtra("USUARIO_ID", usuarioId);
        startActivity(intent);
    }

    private void eliminarVehiculo(Vehiculo vehiculo) {
        vehiculoService.eliminarVehiculo(vehiculo.getId(), new VehiculoService.VehiculoCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    cargarVehiculos();
                    Toast.makeText(MainVehiculoActivity.this, "Vehículo eliminado", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(MainVehiculoActivity.this, "Error al eliminar: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inicio) {
            startActivity(new Intent(this, menu_users.class));
        } else if (id == R.id.gestion) {
            // Ya estamos aquí
        } else if (id == R.id.gestion_man) {
            startActivity(new Intent(this, MantenimientosActivity.class));
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
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}