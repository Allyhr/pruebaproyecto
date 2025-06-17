package com.example.proyectoprueba.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoprueba.R;
import com.example.proyectoprueba.database.DatabaseHelper;
import com.example.proyectoprueba.modelos.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private EditText edtNombre, edtUsuario, edtCorreo, edtContrasena, edtConfirmarContrasena;
    private Button btnRegistrarse;
    private ImageView btnToggleContrasena, btnToggleConfirmar;
    private boolean isPasswordVisible = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new DatabaseHelper(this);

        // Inicializar vistas
        edtNombre = findViewById(R.id.edtNombre);
        edtUsuario = findViewById(R.id.edtUsuario);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtContrasena = findViewById(R.id.edtContrasena);
        edtConfirmarContrasena = findViewById(R.id.edtConfirmarContrasena);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnToggleContrasena = findViewById(R.id.btnToggleContrasena);
        btnToggleConfirmar = findViewById(R.id.btnToggleConfirmar);

        // Configurar listeners para los botones de mostrar/ocultar contraseña
        btnToggleContrasena.setOnClickListener(v -> togglePasswordVisibility(edtContrasena));
        btnToggleConfirmar.setOnClickListener(v -> togglePasswordVisibility(edtConfirmarContrasena));

        // Listener para el botón de registro
        btnRegistrarse.setOnClickListener(v -> registrarUsuario());
    }

    private void togglePasswordVisibility(EditText editText) {
        if (isPasswordVisible) {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnToggleContrasena.setImageResource(R.drawable.ic_eye_off);
            btnToggleConfirmar.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnToggleContrasena.setImageResource(R.drawable.ic_eye);
            btnToggleConfirmar.setImageResource(R.drawable.ic_eye);
        }
        isPasswordVisible = !isPasswordVisible;
        editText.setSelection(editText.getText().length());
    }

    private void registrarUsuario() {
        String nombre = edtNombre.getText().toString().trim();
        String usuario = edtUsuario.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();
        String contrasena = edtContrasena.getText().toString().trim();
        String confirmarContrasena = edtConfirmarContrasena.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el usuario ya existe
        if (dbHelper.checkUser(usuario, correo)) {
            Toast.makeText(this, "El usuario o correo ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCompleto(nombre);
        nuevoUsuario.setUsuario(usuario);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena); // En una app real, esto debería estar encriptado

        // Guardar usuario en la base de datos
        long id = dbHelper.addUser(nuevoUsuario);

        if (id > 0) {
            // Guardar sesión con SharedPreferences
            SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("user_id", id);
            editor.putString("user_name", usuario);
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

            // Redirigir a la actividad principal
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}