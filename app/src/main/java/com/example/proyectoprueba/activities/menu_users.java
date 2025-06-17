package com.example.proyectoprueba.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Taller;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class menu_users extends AppCompatActivity implements
        OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMapLongClickListener {

    private DrawerLayout drawerLayout;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private DatabaseHelper dbHelper;
    private LatLng currentLocation;

    // Variables para ubicación
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_users);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Inicializar Mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_containeradmin);
        mapFragment.getMapAsync(this);

        // Inicializar Geocoder y SQLite Helper
        geocoder = new Geocoder(this, Locale.getDefault());
        dbHelper = new DatabaseHelper(this);

        // Configurar botón para guardar taller
        MaterialButton btnGuardarTaller = findViewById(R.id.guardar_taller_button);
        btnGuardarTaller.setOnClickListener(v -> guardarTaller());

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        // Configuración básica del mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);  // Esto activa el punto azul
            obtenerUbicacionActual();
        } else {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // ==================== [ MÉTODOS DE UBICACIÓN ] ====================
    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                Toast.makeText(this, "Se necesitan permisos de ubicación para mostrar tu posición", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Configurar LocationRequest
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 segundos
        locationRequest.setFastestInterval(5000); // 5 segundos

        // Configurar LocationCallback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                        // Detener actualizaciones después de obtener la ubicación
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                }
            }
        };

        // Solicitar actualizaciones de ubicación
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        // Obtener última ubicación conocida (más rápida)
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                });
    }

    private void mostrarUbicacionActual(LatLng ubicacion) {
        // Limpiar marcadores anteriores
        mMap.clear();

        // Agregar marcador personalizado
        mMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .title("Mi ubicación")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        // Mover cámara
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
    }

    // ==================== [ GUARDAR TALLER ] ====================
    private void guardarTaller() {
        if (currentLocation == null) {
            Toast.makeText(this, "Selecciona una ubicación en el mapa primero", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            List<Address> addresses = geocoder.getFromLocation(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Construir dirección completa
                StringBuilder direccionBuilder = new StringBuilder();
                if (address.getThoroughfare() != null) {
                    direccionBuilder.append(address.getThoroughfare());
                    if (address.getSubThoroughfare() != null) {
                        direccionBuilder.append(" ").append(address.getSubThoroughfare());
                    }
                    direccionBuilder.append(", ");
                }
                if (address.getSubLocality() != null) {
                    direccionBuilder.append(address.getSubLocality()).append(", ");
                }
                if (address.getLocality() != null) {
                    direccionBuilder.append(address.getLocality()).append(", ");
                }
                if (address.getAdminArea() != null) {
                    direccionBuilder.append(address.getAdminArea());
                }

                String direccionCompleta = direccionBuilder.toString();
                // Eliminar coma final si existe
                if (direccionCompleta.endsWith(", ")) {
                    direccionCompleta = direccionCompleta.substring(0, direccionCompleta.length() - 2);
                }

                String nombreTaller = "Taller en " + address.getLocality();

                Taller taller = new Taller();
                taller.setNombre(nombreTaller);
                taller.setDireccion(direccionCompleta); // Guardar dirección formateada
                taller.setLatitud(currentLocation.latitude);
                taller.setLongitud(currentLocation.longitude);

                long id = dbHelper.guardarTaller(taller);

                if (id > 0) {
                    Toast.makeText(this, "Taller guardado: " + nombreTaller, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error al guardar el taller", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Fallback: usar coordenadas si no se obtiene dirección
                String direccionFallback = "Ubicación: " + currentLocation.latitude + ", " + currentLocation.longitude;
                Taller taller = new Taller();
                taller.setNombre("Taller guardado");
                taller.setDireccion(direccionFallback);
                taller.setLatitud(currentLocation.latitude);
                taller.setLongitud(currentLocation.longitude);
                dbHelper.guardarTaller(taller);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== [ NAVEGACIÓN DEL MENÚ LATERAL ] ====================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inicio) {
            // Ya estamos en inicio
        } else if (id == R.id.gestion) {
            // Ir a gestión de vehículos
        } else if (id == R.id.gestion_man) {
            // Ir a gestión de mantenimiento
        } else if (id == R.id.ia) {
            Intent intent = new Intent(menu_users.this, IAActivity.class);
            startActivity(intent);
        } else if (id == R.id.talleres_favoritos) {
            Intent intent = new Intent(menu_users.this, TalleresFavoritosActivity.class);
            startActivity(intent);
        } else if (id == R.id.configuracion) {
            Intent intent = new Intent(menu_users.this, ConfiguracionActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // ==================== [ CICLO DE VIDA ] ====================
    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
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
    public void onMapLongClick(LatLng latLng) {
        // Guardar la ubicación seleccionada
        currentLocation = latLng;

        // Limpiar el mapa y agregar nuevo marcador
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Ubicación seleccionada")
                .snippet("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude));

        // Mover la cámara a la ubicación seleccionada con zoom
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        // Mostrar mensaje al usuario
        Toast.makeText(this,
                "Ubicación guardada:\n" +
                        "Latitud: " + latLng.latitude + "\n" +
                        "Longitud: " + latLng.longitude,
                Toast.LENGTH_LONG).show();

        // Opcional: Obtener dirección usando Geocoder
        obtenerDireccionDesdeCoordenadas(latLng);
    }

    private void obtenerDireccionDesdeCoordenadas(LatLng latLng) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1); // Obtener solo 1 resultado

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String direccion = address.getAddressLine(0);

                    // Actualizar el marcador con la dirección
                    runOnUiThread(() -> {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Ubicación guardada")
                                .snippet(direccion));
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error al obtener dirección",
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}