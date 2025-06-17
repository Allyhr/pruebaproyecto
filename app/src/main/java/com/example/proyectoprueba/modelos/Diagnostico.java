package com.example.proyectoprueba.modelos;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Diagnostico {
    private String id;
    private String titulo;
    private String descripcion;
    private String solucion;
    private List<String> palabrasClave;
    private List<String> sintomasRelacionados;

    public Diagnostico(String id, String titulo, String descripcion, String solucion, JSONArray palabrasClave) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.solucion = solucion;
        this.palabrasClave = new ArrayList<>();

        try {
            for (int i = 0; i < palabrasClave.length(); i++) {
                this.palabrasClave.add(palabrasClave.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean coincideExactamente(String texto) {
        for (String palabra : palabrasClave) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    public double calcularSimilitud(String texto) {
        int coincidencias = 0;
        for (String palabra : palabrasClave) {
            if (texto.contains(palabra)) {
                coincidencias++;
            }
        }
        return (double) coincidencias / palabrasClave.size();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public List<String> getPalabrasClave() {
        return palabrasClave;
    }

    public void setPalabrasClave(List<String> palabrasClave) {
        this.palabrasClave = palabrasClave;
    }

    public List<String> getSintomasRelacionados() {
        return sintomasRelacionados;
    }

    public void setSintomasRelacionados(List<String> sintomasRelacionados) {
        this.sintomasRelacionados = sintomasRelacionados;
    }
}

