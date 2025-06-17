package com.example.proyectoprueba.modelos;

import java.util.List;

public class Mensaje {
    public static final int TIPO_TEXTO = 0;
    public static final int TIPO_OPCIONES = 1;
    public static final int TIPO_CARGANDO = 2;

    private String contenido;
    private boolean esBot;
    private int tipo;
    private List<String> opciones;

    // Constructor para mensajes de texto normales
    public Mensaje(String contenido, boolean esBot, int tipo) {
        this.contenido = contenido;
        this.esBot = esBot;
        this.tipo = tipo;
    }

    // Constructor para mensajes con opciones
    public Mensaje(List<String> opciones, boolean esBot, int tipo) {
        this.opciones = opciones;
        this.esBot = esBot;
        this.tipo = tipo;
    }

    // Getters y setters
    public String getContenido() {
        return contenido;
    }

    public boolean isEsBot() {
        return esBot;
    }

    public int getTipo() {
        return tipo;
    }

    public List<String> getOpciones() {
        return opciones;
    }
}