package com.example.proyectoprueba.modelos;

import java.util.Date;

public class Mantenimiento {
    private int id;
    private String noPlaca;
    private String tipoServicio;
    private String descripcion;
    private String fecha;
    private int kilometraje;
    private double costo;

    public Mantenimiento() {
    }

    public Mantenimiento(String noPlaca, String tipoServicio, String descripcion, String fecha, int kilometraje, double costo) {
        this.noPlaca = noPlaca;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.kilometraje = kilometraje;
        this.costo = costo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoPlaca() {
        return noPlaca;
    }

    public void setNoPlaca(String noPlaca) {
        this.noPlaca = noPlaca;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
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
}