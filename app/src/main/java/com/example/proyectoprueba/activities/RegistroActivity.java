package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoprueba.R;
import com.example.proyectoprueba.modelos.Usuario;
import com.example.proyectoprueba.network.ApiClient;
import com.example.proyectoprueba.network.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroActivity extends AppCompatActivity {
    EditText txtNombre, txtUsuario, txtCorreo, txtContrasena, txtConfirmar;
    Button btnRegistrar;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = findViewById(R.id.edtNombre);
        txtUsuario = findViewById(R.id.edtUsuario);
        txtCorreo = findViewById(R.id.edtCorreo);
        txtContrasena = findViewById(R.id.edtContrasena);
        txtConfirmar = findViewById(R.id.edtConfirmarContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrarse);

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        btnRegistrar.setOnClickListener(view -> registrar());
    }

    private void registrar() {
        String nombre = txtNombre.getText().toString().trim();
        String usuario = txtUsuario.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();
        String confirmar = txtConfirmar.getText().toString().trim();

        if (!contrasena.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario user = new Usuario(nombre, usuario, correo, contrasena);

        apiService.registrar(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "Registrado con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                    finish();
                } else {
                    // NUEVO: Mostrar código de error y mensaje del servidor
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(RegistroActivity.this, "Error en el registro: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegistroActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
