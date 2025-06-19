package com.example.proyectoprueba.modelos;

public class Vehiculo {
    private long id;
    private long usuarioId;
    private String alias;
    private String marca;
    private String modelo;
    private int anio;
    private String placa;
    private String color;
    private int kilometraje;
    private String transmision;
    private String combustible;

    // Constructores
    public Vehiculo() {
    }

    public Vehiculo(long id, String alias, String marca, String modelo, int anio, String placa,
                    String color, int kilometraje, String transmision, String combustible) {
        this.id = id;
        this.alias = alias;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.placa = placa;
        this.color = color;
        this.kilometraje = kilometraje;
        this.transmision = transmision;
        this.combustible = combustible;
    }

    // Getters y Setters


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getTransmision() {
        return transmision;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public String getCombustible() {
        return combustible;
    }

    public void setCombustible(String combustible) {
        this.combustible = combustible;
    }
}