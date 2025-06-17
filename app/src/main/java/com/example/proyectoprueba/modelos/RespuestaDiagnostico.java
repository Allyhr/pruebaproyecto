package com.example.proyectoprueba.modelos;

public class RespuestaDiagnostico {
    private Diagnostico diagnostico;
    private double confianza;

    public RespuestaDiagnostico(Diagnostico diagnostico, double confianza) {
        this.diagnostico = diagnostico;
        this.confianza = confianza;
    }

    public String formatearRespuesta() {
        if (confianza > 0.7) {
            return "Estoy bastante seguro del problema:\n\n" +
                    "**" + diagnostico.getTitulo() + "**\n\n" +
                    "Descripción: " + diagnostico.getDescripcion() + "\n\n" +
                    "Solución recomendada: " + diagnostico.getSolucion();
        } else if (confianza > 0.4) {
            return "Podría ser:\n\n" +
                    "**" + diagnostico.getTitulo() + "**\n\n" +
                    "Descripción: " + diagnostico.getDescripcion() + "\n\n" +
                    "Posible solución: " + diagnostico.getSolucion() +
                    "\n\n(Confianza: " + (int)(confianza * 100) + "%)";
        } else {
            return "No estoy muy seguro, pero podría ser:\n\n" +
                    "**" + diagnostico.getTitulo() + "**\n\n" +
                    "¿Podrías confirmar si tienes estos síntomas?\n" +
                    diagnostico.getDescripcion();
        }
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }

    public double getConfianza() {
        return confianza;
    }

    public void setConfianza(double confianza) {
        this.confianza = confianza;
    }
}