package com.example.proyectoprueba.modelos;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("token")
    private String token;

    @SerializedName("usuario")
    private Usuario usuario;

    public String getMensaje() {
        return mensaje;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
