package com.example.proyectoprueba.modelos;

public class Usuario {
    private long id;
    private String nombreCompleto;
    private String usuario;
    private String correo;
    private String contrasena;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros básicos
    public Usuario(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Constructor completo
    public Usuario(long id, String nombreCompleto, String usuario, String correo, String contrasena) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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