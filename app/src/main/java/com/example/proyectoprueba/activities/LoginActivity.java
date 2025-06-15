package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {
    EditText txtCorreo, txtContrasena;
    Button btnLogin, btnRegistro;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtCorreo = findViewById(R.id.edtCorreo);
        txtContrasena = findViewById(R.id.edtContrasena);
        btnLogin = findViewById(R.id.btnIniciarSesion);
        btnRegistro = findViewById(R.id.btnRegistrar);
        apiService = ApiClient.getRetrofit().create(ApiService.class);

        btnLogin.setOnClickListener(view -> login());
        btnRegistro.setOnClickListener(view -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void login() {
        String correo = txtCorreo.getText().toString().trim();
        String contrasena = txtContrasena.getText().toString().trim();

        Usuario user = new Usuario(correo, contrasena);

        apiService.login(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
                    prefs.edit().putBoolean("sesion_activa", true).apply();

                    // Cambia InicioActivity.class a menu_users.class
                    startActivity(new Intent(LoginActivity.this, menu_users.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
