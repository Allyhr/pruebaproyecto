package com.example.proyectoprueba.vehiculos;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.*;

public class SqlVehiculos extends SQLiteOpenHelper {
    private static final String DB = "vehiculos.db";
    private static final int V = 1;

    // Nombres de tabla y columnas
    private static final String TABLE = "vehiculo";
    private static final String COL_ID = "id";
    private static final String COL_ALIAS = "alias";
    private static final String COL_MARCA = "marca_id";
    private static final String COL_MODELO = "modelo_id";
    private static final String COL_ANIO = "anio";
    private static final String COL_PLACA = "placa";
    private static final String COL_COLOR = "color_id";
    private static final String COL_KM = "kilometraje";
    private static final String COL_TRANSMISION = "transmision_id";
    private static final String COL_COMBUSTIBLE = "combustible_id";

    public SqlVehiculos(Context c) {
        super(c, DB, null, V);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ALIAS + " TEXT, " +
                COL_MARCA + " INTEGER, " +
                COL_MODELO + " INTEGER, " +
                COL_ANIO + " INTEGER, " +
                COL_PLACA + " TEXT, " +
                COL_COLOR + " INTEGER, " +
                COL_KM + " INTEGER, " +
                COL_TRANSMISION + " INTEGER, " +
                COL_COMBUSTIBLE + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertarVehiculo(Vehiculo v) {
        if (v.getAlias() == null || v.getAlias().isEmpty()) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_ALIAS, v.getAlias());
            cv.put(COL_MARCA, v.getMarcaId());
            cv.put(COL_MODELO, v.getModeloId());
            cv.put(COL_ANIO, v.getAnio());
            cv.put(COL_PLACA, v.getPlaca());
            cv.put(COL_COLOR, v.getColorId());
            cv.put(COL_KM, v.getKilometraje());
            cv.put(COL_TRANSMISION, v.getTransmisionId());
            cv.put(COL_COMBUSTIBLE, v.getCombustibleId());

            id = db.insert(TABLE, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return id;
    }

    public List<Vehiculo> obtenerVehiculos() {
        List<Vehiculo> l = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE, null);
            while (c.moveToNext()) {
                l.add(new Vehiculo(
                        c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_ALIAS)),
                        c.getInt(c.getColumnIndexOrThrow(COL_MARCA)),
                        c.getInt(c.getColumnIndexOrThrow(COL_MODELO)),
                        c.getInt(c.getColumnIndexOrThrow(COL_ANIO)),
                        c.getString(c.getColumnIndexOrThrow(COL_PLACA)),
                        c.getInt(c.getColumnIndexOrThrow(COL_COLOR)),
                        c.getInt(c.getColumnIndexOrThrow(COL_KM)),
                        c.getInt(c.getColumnIndexOrThrow(COL_TRANSMISION)),
                        c.getInt(c.getColumnIndexOrThrow(COL_COMBUSTIBLE))
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) c.close();
            db.close();
        }

        return l;
    }

    public void actualizarVehiculo(Vehiculo v) {
        if (v.getId() <= 0) return;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COL_ALIAS, v.getAlias());
            cv.put(COL_MARCA, v.getMarcaId());
            cv.put(COL_MODELO, v.getModeloId());
            cv.put(COL_ANIO, v.getAnio());
            cv.put(COL_PLACA, v.getPlaca());
            cv.put(COL_COLOR, v.getColorId());
            cv.put(COL_KM, v.getKilometraje());
            cv.put(COL_TRANSMISION, v.getTransmisionId());
            cv.put(COL_COMBUSTIBLE, v.getCombustibleId());

            db.update(TABLE, cv, COL_ID + "=?", new String[]{String.valueOf(v.getId())});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void eliminarVehiculo(int id) {
        if (id <= 0) return;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
