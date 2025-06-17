package com.example.proyectoprueba.models;

public class Mantenimiento {
    private int id;
    private String vehiculo;
    private String tipoServicio;
    private String fecha;
    private String kilometraje;
    private String descripcion;
    private double costo;

    // Constructor vacío
    public Mantenimiento() {
    }

    // Constructor con parámetros
    public Mantenimiento(int id, String vehiculo, String tipoServicio, String fecha,
                         String kilometraje, String descripcion, double costo) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.tipoServicio = tipoServicio;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.descripcion = descripcion;
        this.costo = costo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return "Mantenimiento{" +
                "id=" + id +
                ", vehiculo='" + vehiculo + '\'' +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", fecha='" + fecha + '\'' +
                ", kilometraje='" + kilometraje + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", costo=" + costo +
                '}';
    }
}

