package com.example.proyectoprueba.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {
    public static void guardarSesion(Context context, boolean activa) {
        SharedPreferences prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("sesion_activa", activa).apply();
    }

    public static boolean obtenerSesion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        return prefs.getBoolean("sesion_activa", false);
    }
}