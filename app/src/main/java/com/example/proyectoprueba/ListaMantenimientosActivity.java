package com.example.proyectoprueba;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import com.example.proyectoprueba.models.Mantenimiento;
import com.example.proyectoprueba.adapters.MantenimientoAdapter;
import com.example.proyectoprueba.AgregarEditarMantenimientoActivity;
public class ListaMantenimientosActivity extends AppCompatActivity implements  MantenimientoAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private MantenimientoAdapter adapter;
    private List<Mantenimiento> listaMantenimientos;
    private List<Mantenimiento> listaFiltrada;
    private SearchView searchView;
    private FloatingActionButton fabAgregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mantenimientos);

        initViews();
        initData();
        setupRecyclerView();
        setupSearchView();
        setupFab();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewMantenimientos);
        searchView = findViewById(R.id.searchView);
        fabAgregar = findViewById(R.id.fabAgregar);
    }

    private void initData() {
        // Datos de ejemplo - En una app real, estos vendrían de una base de datos
        listaMantenimientos = new ArrayList<>();
        listaMantenimientos.add(new Mantenimiento(1, "Toyota Corolla 2020", "Cambio de aceite",
                "15/06/2025", "45,000 km", "Cambio de aceite y filtro", 800.0));
        listaMantenimientos.add(new Mantenimiento(2, "Honda Civic 2019", "Revisión general",
                "10/06/2025", "38,500 km", "Revisión completa del motor", 1200.0));
        listaMantenimientos.add(new Mantenimiento(3, "Nissan Sentra 2021", "Cambio de llantas",
                "08/06/2025", "25,000 km", "Cambio de 4 llantas nuevas", 3200.0));
        listaMantenimientos.add(new Mantenimiento(4, "Ford Focus 2018", "Alineación y balanceo",
                "05/06/2025", "62,000 km", "Alineación y balanceo completo", 450.0));

        // Crear copia para filtrado
        listaFiltrada = new ArrayList<>(listaMantenimientos);
    }

    private void setupRecyclerView() {
        adapter = new MantenimientoAdapter(listaFiltrada, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarLista(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarLista(newText);
                return false;
            }
        });
    }

    private void setupFab() {
        fabAgregar.setOnClickListener(v -> {
            // Abrir actividad para agregar nuevo mantenimiento
            Intent intent = new Intent(this, AgregarEditarMantenimientoActivity.class);
            intent.putExtra("modo", "agregar");
            startActivityForResult(intent, 100);
        });
    }

    private void filtrarLista(String texto) {
        listaFiltrada.clear();

        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaMantenimientos);
        } else {
            String filtro = texto.toLowerCase().trim();
            for (Mantenimiento mantenimiento : listaMantenimientos) {
                if (mantenimiento.getVehiculo().toLowerCase().contains(filtro) ||
                        mantenimiento.getTipoServicio().toLowerCase().contains(filtro)) {
                    listaFiltrada.add(mantenimiento);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(Mantenimiento mantenimiento, int position) {
        // Abrir actividad para editar mantenimiento
        Intent intent = new Intent(this, AgregarEditarMantenimientoActivity.class);
        intent.putExtra("modo", "editar");
        intent.putExtra("mantenimiento_id", mantenimiento.getId());
        intent.putExtra("vehiculo", mantenimiento.getVehiculo());
        intent.putExtra("tipo_servicio", mantenimiento.getTipoServicio());
        intent.putExtra("fecha", mantenimiento.getFecha());
        intent.putExtra("kilometraje", mantenimiento.getKilometraje());
        intent.putExtra("descripcion", mantenimiento.getDescripcion());
        intent.putExtra("costo", mantenimiento.getCosto());
        intent.putExtra("position", position);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onDeleteClick(Mantenimiento mantenimiento, int position) {
        mostrarDialogoEliminar(mantenimiento, position);
    }

    private void mostrarDialogoEliminar(Mantenimiento mantenimiento, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar eliminación");
        builder.setMessage("¿Estás seguro de que deseas eliminar este mantenimiento?\n\n" +
                "Vehículo: " + mantenimiento.getVehiculo() + "\n" +
                "Servicio: " + mantenimiento.getTipoServicio());

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarMantenimiento(position);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void eliminarMantenimiento(int position) {
        // Encontrar el elemento en la lista original
        Mantenimiento mantenimientoAEliminar = listaFiltrada.get(position);
        listaMantenimientos.remove(mantenimientoAEliminar);

        // Actualizar la lista filtrada
        listaFiltrada.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, listaFiltrada.size());

        Toast.makeText(this, "Mantenimiento eliminado correctamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                // Nuevo mantenimiento agregado
                // Aquí puedes recargar los datos o agregar el nuevo elemento
                Toast.makeText(this, "Mantenimiento agregado correctamente", Toast.LENGTH_SHORT).show();
                // En una app real, aquí recargarías desde la base de datos
            } else if (requestCode == 101) {
                // Mantenimiento editado
                Toast.makeText(this, "Mantenimiento actualizado correctamente", Toast.LENGTH_SHORT).show();
                // En una app real, aquí recargarías desde la base de datos
            }
        }
    }
}
