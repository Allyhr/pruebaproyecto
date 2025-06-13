package com.example.proyectoprueba.modelos;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("nombre_completo")
    private String nombre;

    private String usuario;
    private String correo;
    private String contrasena;

    public Usuario(String nombre, String usuario, String correo, String contrasena) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public Usuario(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
