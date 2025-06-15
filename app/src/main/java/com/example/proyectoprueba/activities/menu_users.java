package com.example.proyectoprueba.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.example.proyectoprueba.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

public class menu_users extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button guardarTallerButton;

    // Variables añadidas para la selección de talleres
    private Marker selectedMarker;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_users);

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Configurar Toggle con Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Configurar listener para el menú
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar datos del header
        if (navigationView.getHeaderCount() > 0) {
            TextView nombre = navigationView.getHeaderView(0).findViewById(R.id.nombreuser);
            TextView correo = navigationView.getHeaderView(0).findViewById(R.id.correouser);

            if (nombre != null) nombre.setText("Kate Pérez");
            if (correo != null) correo.setText("kate@ejemplo.com");
        }

        // Inicializar cliente de ubicación y lugares
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), "YOUR_API_KEY");
        placesClient = Places.createClient(this);

        // Configurar Mapa
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map_containeradmin, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        // Configurar botón Guardar Taller
        guardarTallerButton = findViewById(R.id.guardar_taller_button);
        guardarTallerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTallerFavorito();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this); // Habilitar la selección con long press
        getLocationPermission();
    }

    // Método nuevo para manejar la selección de ubicación
    @Override
    public void onMapLongClick(LatLng latLng) {
        // Eliminar marcador anterior si existe
        if (selectedMarker != null) {
            selectedMarker.remove();
        }

        // Crear nuevo marcador rojo
        selectedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Taller seleccionado")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        selectedLocation = latLng;
        Toast.makeText(this, "Taller seleccionado", Toast.LENGTH_SHORT).show();
    }

    // Método actualizado para guardar talleres
    private void guardarTallerFavorito() {
        if (selectedLocation == null) {
            Toast.makeText(this, "Primero selecciona un taller en el mapa (mantén presionado)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí iría tu lógica para guardar el taller en base de datos
        Toast.makeText(this,
                "Taller guardado en: " + selectedLocation.latitude + ", " + selectedLocation.longitude,
                Toast.LENGTH_LONG).show();

        // Cambiar el marcador a verde como confirmación
        if (selectedMarker != null) {
            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

    // El resto de tus métodos permanecen EXACTAMENTE IGUAL
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();
            findNearbyWorkshops();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void findNearbyWorkshops() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();
                for (com.google.android.libraries.places.api.model.PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Place place = placeLikelihood.getPlace();
                    if (place.getTypes() != null && place.getTypes().contains(Place.Type.CAR_REPAIR)) {
                        LatLng placeLatLng = place.getLatLng();
                        if (placeLatLng != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(placeLatLng)
                                    .title(place.getName())
                                    .snippet("Taller Mecánico"));
                        }
                    }
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("Place API", "Place not found: " + exception.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.inicio) {
            // Acción para Inicio
        } else if (id == R.id.gestion) {
            // Acción para Gestión de Vehículos
        } else if (id == R.id.gestion_man) {
            // Acción para Gestión de Mantenimiento
        } else if (id == R.id.ia) {
            // Acción para IA
        } else if (id == R.id.talleres_favoritos) {
            // Acción para Talleres favoritos
        } else if (id == R.id.configuracion) {
            // Acción para Configuración
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
                findNearbyWorkshops();
            }
        }
    }
}