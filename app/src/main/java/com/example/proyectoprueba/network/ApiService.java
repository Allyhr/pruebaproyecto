package com.example.proyectoprueba.network;

import com.example.proyectoprueba.modelos.Usuario;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/autenticacion/login")
    Call<ResponseBody> login(@Body Usuario usuario);

    @POST("api/autenticacion/registro")
    Call<ResponseBody> registrar(@Body Usuario usuario);

}