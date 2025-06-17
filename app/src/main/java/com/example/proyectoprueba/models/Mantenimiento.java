package com.example.proyectoprueba.modelos;

public class Mantenimiento {
    private int id;
    private int vehiculoId;
    private int tipoServicioId;
    private String descripcion;
    private String fecha;
    private int kilometraje;
    private double costo;
    private int tallerId;

    // Campos adicionales para mostrar información relacionada
    private String vehiculoNombre;
    private String tipoServicioNombre;
    private String tallerNombre;

    // Constructor vacío
    public Mantenimiento() {
    }

    // Constructor con parámetros principales
    public Mantenimiento(int vehiculoId, int tipoServicioId, String descripcion,
                         String fecha, int kilometraje, double costo, int tallerId) {
        this.vehiculoId = vehiculoId;
        this.tipoServicioId = tipoServicioId;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.costo = costo;
        this.tallerId = tallerId;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public int getTipoServicioId() {
        return tipoServicioId;
    }

    public void setTipoServicioId(int tipoServicioId) {
        this.tipoServicioId = tipoServicioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getTallerId() {
        return tallerId;
    }

    public void setTallerId(int tallerId) {
        this.tallerId = tallerId;
    }

    // Getters y Setters para campos adicionales de información
    public String getVehiculoNombre() {
        return vehiculoNombre;
    }

    public void setVehiculoNombre(String vehiculoNombre) {
        this.vehiculoNombre = vehiculoNombre;
    }

    public String getTipoServicioNombre() {
        return tipoServicioNombre;
    }

    public void setTipoServicioNombre(String tipoServicioNombre) {
        this.tipoServicioNombre = tipoServicioNombre;
    }

    public String getTallerNombre() {
        return tallerNombre;
    }

    public void setTallerNombre(String tallerNombre) {
        this.tallerNombre = tallerNombre;
    }

    // Método toString para facilitar la depuración
    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id=" + id +
                ", vehiculoId=" + vehiculoId +
                ", tipoServicioId=" + tipoServicioId +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", kilometraje=" + kilometraje +
                ", costo=" + costo +
                ", tallerId=" + tallerId +
                ", vehiculoNombre='" + vehiculoNombre + '\'' +
                ", tipoServicioNombre='" + tipoServicioNombre + '\'' +
                ", tallerNombre='" + tallerNombre + '\'' +
                '}';
    }
}