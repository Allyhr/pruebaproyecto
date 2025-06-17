package com.example.proyectoprueba.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyectoprueba.modelos.Taller;
import com.example.proyectoprueba.modelos.Usuario;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mantenimiento_vehiculos.db";
    private static final int DATABASE_VERSION = 2;

    // Tabla usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre_completo";
    private static final String COLUMN_USUARIO = "usuario";
    private static final String COLUMN_CORREO = "correo";
    private static final String COLUMN_CONTRASENA = "contrasena";

    private static final String TABLE_TALLERES = "talleres";
    private static final String COL_TALLER_ID = "id";
    private static final String COL_TALLER_NOMBRE = "nombre";
    private static final String COL_TALLER_DIRECCION = "direccion";
    private static final String COL_TALLER_LATITUD = "latitud";
    private static final String COL_TALLER_LONGITUD = "longitud";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOMBRE + " TEXT NOT NULL,"
                + COLUMN_USUARIO + " TEXT NOT NULL,"
                + COLUMN_CORREO + " TEXT NOT NULL UNIQUE,"
                + COLUMN_CONTRASENA + " TEXT NOT NULL)";
        db.execSQL(CREATE_USUARIOS_TABLE);

        String CREATE_TALLERES_TABLE = "CREATE TABLE " + TABLE_TALLERES + "("
                + COL_TALLER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TALLER_NOMBRE + " TEXT NOT NULL,"
                + COL_TALLER_DIRECCION + " TEXT NOT NULL,"
                + COL_TALLER_LATITUD + " REAL NOT NULL,"
                + COL_TALLER_LONGITUD + " REAL NOT NULL)";
        db.execSQL(CREATE_TALLERES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALLERES);
        onCreate(db);
    }

    public long addUser(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, usuario.getNombreCompleto());
        values.put(COLUMN_USUARIO, usuario.getUsuario());
        values.put(COLUMN_CORREO, usuario.getCorreo());
        values.put(COLUMN_CONTRASENA, usuario.getContrasena());

        long id = db.insert(TABLE_USUARIOS, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String usuario, String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COLUMN_ID},
                COLUMN_USUARIO + " = ? OR " + COLUMN_CORREO + " = ?",
                new String[]{usuario, correo},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public Usuario checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Usuario usuario = null;

        // Definir las columnas que vamos a consultar
        String[] columns = {
                COLUMN_ID,
                COLUMN_NOMBRE,
                COLUMN_USUARIO,
                COLUMN_CORREO,
                COLUMN_CONTRASENA
        };

        // Consulta con parámetros
        String selection = COLUMN_CORREO + " = ? AND " + COLUMN_CONTRASENA + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(
                TABLE_USUARIOS,
                columns,
                selection,
                selectionArgs,
                null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    usuario = new Usuario();
                    // Usar getColumnIndexOrThrow para mayor seguridad
                    usuario.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    usuario.setNombreCompleto(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)));
                    usuario.setUsuario(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO)));
                    usuario.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CORREO)));
                    usuario.setContrasena(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTRASENA)));
                }
            } finally {
                cursor.close();
            }
        }
        db.close();
        return usuario;
    }

    public long guardarTaller(Taller taller) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TALLER_NOMBRE, taller.getNombre());
        values.put(COL_TALLER_DIRECCION, taller.getDireccion());
        values.put(COL_TALLER_LATITUD, taller.getLatitud());
        values.put(COL_TALLER_LONGITUD, taller.getLongitud());

        long id = db.insert(TABLE_TALLERES, null, values);
        db.close();
        return id;
    }

    public boolean eliminarTaller(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("talleres", "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor obtenerTodosLosTalleres() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                "talleres",
                new String[]{"id", "nombre", "direccion", "latitud", "longitud"},
                null,
                null,
                null,
                null,
                "nombre ASC"
        );
    }

    // Puedes agregar más métodos según necesites (login, actualizar, etc.)
}