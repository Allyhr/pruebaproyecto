package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText edtCorreo, edtContrasena;
    private Button btnIniciarSesion, btnRegistrar;
    private ImageView btnTogglePassword;
    private boolean isPasswordVisible = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Inicializar vistas
        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasena = findViewById(R.id.edtContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);

        // Configurar listener para mostrar/ocultar contraseña
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Listener para el botón de login
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        // Listener para el botón de registro
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Verificar si ya hay una sesión activa
        verificarSesionActiva();
    }

    private void verificarSesionActiva() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        if(isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, menu_users.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edtContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.ic_eye_closed);
        } else {
            edtContrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(R.drawable.ic_eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        edtContrasena.setSelection(edtContrasena.getText().length());
    }

    private void iniciarSesion() {
        String correo = edtCorreo.getText().toString().trim();
        String contrasena = edtContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Usuario usuario = dbHelper.checkUserCredentials(correo, contrasena);

            if (usuario != null) {
                guardarSesionUsuario(usuario);

                // Redirigir al menú principal
                Intent intent = new Intent(LoginActivity.this, menu_users.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void guardarSesionUsuario(Usuario usuario) {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("user_id", usuario.getId());
        editor.putString("nombre_completo", usuario.getNombreCompleto());
        editor.putString("usuario", usuario.getUsuario());
        editor.putString("correo", usuario.getCorreo());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}