package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText txtCorreo, txtContrasena;
    private Button btnLogin, btnRegistro;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de vistas
        txtCorreo = findViewById(R.id.edtCorreo);
        txtContrasena = findViewById(R.id.edtContrasena);
        btnLogin = findViewById(R.id.btnIniciarSesion);
        btnRegistro = findViewById(R.id.btnRegistrar);

        apiService = ApiClient.getRetrofit().create(ApiService.class);

        btnLogin.setOnClickListener(view -> login());
        btnRegistro.setOnClickListener(view -> startActivity(new Intent(this, RegistroActivity.class)));

        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);

        btnTogglePassword.setOnClickListener(v -> {
            if (txtContrasena.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Mostrar contraseña
                txtContrasena.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_open); // cambia a ícono de ojo abierto
            } else {
                // Ocultar contraseña
                txtContrasena.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_closed); // cambia a ícono de ojo cerrado
            }
            // Mover el cursor al final del texto
            txtContrasena.setSelection(txtContrasena.getText().length());
        });

    }

    private void login() {
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        // Validaciones
        if (correo.isEmpty() && contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el correo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lógica original si todo está correcto

        if(correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        Usuario user = new Usuario(correo, contrasena);

        apiService.login(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    guardarSesion(correo);
                    redirigirAMenuUsuarios();
                } else {

                    Toast.makeText(LoginActivity.this, "Datos inválidos", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LOGIN_ERROR", "Error: ", t);
            }
        });
    }


    private void guardarSesion(String correo) {
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sesion_activa", true);
        editor.putString("correo_usuario", correo);
        editor.apply();
    }

    private void redirigirAMenuUsuarios() {
        try {
            Intent intent = new Intent(this, menu_users.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("NAVEGACION", "Error al redirigir: " + e.getMessage());
            Toast.makeText(this, "Error al iniciar la aplicación", Toast.LENGTH_SHORT).show();
        }
    }
}