package com.example.proyectoprueba.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyectoprueba.modelos.Mantenimiento;
import com.example.proyectoprueba.modelos.Taller;
import com.example.proyectoprueba.modelos.Usuario;
import com.example.proyectoprueba.modelos.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mantenimiento_vehiculos.db";
    private static final int DATABASE_VERSION = 4;

    // Tabla usuarios
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre_completo";
    private static final String COLUMN_USUARIO = "usuario";
    private static final String COLUMN_CORREO = "correo";
    private static final String COLUMN_CONTRASENA = "contrasena";

    // Tabla talleres
    private static final String TABLE_TALLERES = "talleres";
    private static final String COL_TALLER_ID = "id";
    private static final String COL_TALLER_NOMBRE = "nombre";
    private static final String COL_TALLER_DIRECCION = "direccion";
    private static final String COL_TALLER_LATITUD = "latitud";
    private static final String COL_TALLER_LONGITUD = "longitud";

    // Tabla vehículos
    private static final String TABLE_VEHICULOS = "vehiculos";
    private static final String COL_VEHICULO_ID = "id";
    private static final String COL_VEHICULO_USUARIO_ID = "usuario_id";
    private static final String COL_VEHICULO_ALIAS = "alias";
    private static final String COL_VEHICULO_MARCA = "marca";
    private static final String COL_VEHICULO_MODELO = "modelo";
    private static final String COL_VEHICULO_ANIO = "anio";
    private static final String COL_VEHICULO_PLACA = "placa";
    private static final String COL_VEHICULO_COLOR = "color";
    private static final String COL_VEHICULO_KILOMETRAJE = "kilometraje";
    private static final String COL_VEHICULO_TRANSMISION = "transmision";
    private static final String COL_VEHICULO_COMBUSTIBLE = "combustible";

    // Tabla mantenimientos
    private static final String TABLE_MANTENIMIENTOS = "mantenimientos";
    private static final String COL_MANTENIMIENTO_ID = "id";
    private static final String COL_MANTENIMIENTO_PLACA = "noplaca";
    private static final String COL_MANTENIMIENTO_SERVICIO = "tipo_servicio";
    private static final String COL_MANTENIMIENTO_DESCRIPCION = "descripcion";
    private static final String COL_MANTENIMIENTO_FECHA = "fecha";
    private static final String COL_MANTENIMIENTO_KILOMETRAJE = "kilometraje";
    private static final String COL_MANTENIMIENTO_COSTO = "costo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOMBRE + " TEXT NOT NULL,"
                + COLUMN_USUARIO + " TEXT NOT NULL,"
                + COLUMN_CORREO + " TEXT NOT NULL UNIQUE,"
                + COLUMN_CONTRASENA + " TEXT NOT NULL)";
        db.execSQL(CREATE_USUARIOS_TABLE);

        // Crear tabla talleres
        String CREATE_TALLERES_TABLE = "CREATE TABLE " + TABLE_TALLERES + "("
                + COL_TALLER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TALLER_NOMBRE + " TEXT NOT NULL,"
                + COL_TALLER_DIRECCION + " TEXT NOT NULL,"
                + COL_TALLER_LATITUD + " REAL NOT NULL,"
                + COL_TALLER_LONGITUD + " REAL NOT NULL)";
        db.execSQL(CREATE_TALLERES_TABLE);

        // Crear tabla vehículos
        String CREATE_VEHICULOS_TABLE = "CREATE TABLE " + TABLE_VEHICULOS + "("
                + COL_VEHICULO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_VEHICULO_USUARIO_ID + " INTEGER NOT NULL,"
                + COL_VEHICULO_ALIAS + " TEXT NOT NULL,"
                + COL_VEHICULO_MARCA + " TEXT,"
                + COL_VEHICULO_MODELO + " TEXT,"
                + COL_VEHICULO_ANIO + " INTEGER NOT NULL,"
                + COL_VEHICULO_PLACA + " TEXT,"
                + COL_VEHICULO_COLOR + " TEXT,"
                + COL_VEHICULO_KILOMETRAJE + " INTEGER,"
                + COL_VEHICULO_TRANSMISION + " TEXT,"
                + COL_VEHICULO_COMBUSTIBLE + " TEXT,"
                + "FOREIGN KEY(" + COL_VEHICULO_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_VEHICULOS_TABLE);

        String CREATE_MANTENIMIENTOS_TABLE = "CREATE TABLE " + TABLE_MANTENIMIENTOS + "("
                + COL_MANTENIMIENTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MANTENIMIENTO_PLACA + " TEXT NOT NULL,"
                + COL_MANTENIMIENTO_SERVICIO + " TEXT NOT NULL,"
                + COL_MANTENIMIENTO_DESCRIPCION + " TEXT,"
                + COL_MANTENIMIENTO_FECHA + " TEXT NOT NULL,"
                + COL_MANTENIMIENTO_KILOMETRAJE + " INTEGER NOT NULL,"
                + COL_MANTENIMIENTO_COSTO + " REAL NOT NULL)";
        db.execSQL(CREATE_MANTENIMIENTOS_TABLE);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALLERES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICULOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANTENIMIENTOS);
        onCreate(db);
    }

    // Operaciones para Usuarios
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

        String[] columns = {
                COLUMN_ID,
                COLUMN_NOMBRE,
                COLUMN_USUARIO,
                COLUMN_CORREO,
                COLUMN_CONTRASENA
        };

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

    // Operaciones para Talleres
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
        return db.delete(TABLE_TALLERES, COL_TALLER_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Cursor obtenerTodosLosTalleres() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_TALLERES,
                new String[]{COL_TALLER_ID, COL_TALLER_NOMBRE, COL_TALLER_DIRECCION, COL_TALLER_LATITUD, COL_TALLER_LONGITUD},
                null,
                null,
                null,
                null,
                COL_TALLER_NOMBRE + " ASC"
        );
    }

    public List<Vehiculo> buscarVehiculos(int usuarioId, String query) {
        List<Vehiculo> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COL_VEHICULO_ID,
                COL_VEHICULO_ALIAS,
                COL_VEHICULO_MARCA,
                COL_VEHICULO_MODELO,
                COL_VEHICULO_ANIO,
                COL_VEHICULO_PLACA,
                COL_VEHICULO_COLOR,
                COL_VEHICULO_KILOMETRAJE,
                COL_VEHICULO_TRANSMISION,
                COL_VEHICULO_COMBUSTIBLE
        };

        String selection = COL_VEHICULO_USUARIO_ID + " = ? AND (" +
                COL_VEHICULO_ALIAS + " LIKE ? OR " +
                COL_VEHICULO_MARCA + " LIKE ? OR " +
                COL_VEHICULO_MODELO + " LIKE ? OR " +
                COL_VEHICULO_PLACA + " LIKE ?)";

        String[] selectionArgs = {
                String.valueOf(usuarioId),
                "%" + query + "%",
                "%" + query + "%",
                "%" + query + "%",
                "%" + query + "%"
        };

        Cursor cursor = db.query(
                TABLE_VEHICULOS,
                columns,
                selection,
                selectionArgs,
                null, null,
                COL_VEHICULO_ALIAS + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_VEHICULO_ID)));
                vehiculo.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_ALIAS)));
                vehiculo.setMarca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MARCA)));
                vehiculo.setModelo(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MODELO)));
                vehiculo.setAnio(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_ANIO)));
                vehiculo.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_PLACA)));
                vehiculo.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COLOR)));
                vehiculo.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_KILOMETRAJE)));
                vehiculo.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_TRANSMISION)));
                vehiculo.setCombustible(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COMBUSTIBLE)));

                resultados.add(vehiculo);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return resultados;
    }

    // Operaciones para Vehículos
    public long guardarVehiculo(Vehiculo vehiculo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_VEHICULO_USUARIO_ID, vehiculo.getUsuarioId());
        values.put(COL_VEHICULO_ALIAS, vehiculo.getAlias());
        values.put(COL_VEHICULO_MARCA, vehiculo.getMarca());
        values.put(COL_VEHICULO_MODELO, vehiculo.getModelo());
        values.put(COL_VEHICULO_ANIO, vehiculo.getAnio());
        values.put(COL_VEHICULO_PLACA, vehiculo.getPlaca());
        values.put(COL_VEHICULO_COLOR, vehiculo.getColor());
        values.put(COL_VEHICULO_KILOMETRAJE, vehiculo.getKilometraje());
        values.put(COL_VEHICULO_TRANSMISION, vehiculo.getTransmision());
        values.put(COL_VEHICULO_COMBUSTIBLE, vehiculo.getCombustible());

        long id = db.insert(TABLE_VEHICULOS, null, values);
        db.close();
        return id;
    }

    public boolean actualizarVehiculo(Vehiculo vehiculo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_VEHICULO_ALIAS, vehiculo.getAlias());
        values.put(COL_VEHICULO_MARCA, vehiculo.getMarca());
        values.put(COL_VEHICULO_MODELO, vehiculo.getModelo());
        values.put(COL_VEHICULO_ANIO, vehiculo.getAnio());
        values.put(COL_VEHICULO_PLACA, vehiculo.getPlaca());
        values.put(COL_VEHICULO_COLOR, vehiculo.getColor());
        values.put(COL_VEHICULO_KILOMETRAJE, vehiculo.getKilometraje());
        values.put(COL_VEHICULO_TRANSMISION, vehiculo.getTransmision());
        values.put(COL_VEHICULO_COMBUSTIBLE, vehiculo.getCombustible());

        int rowsAffected = db.update(TABLE_VEHICULOS, values,
                COL_VEHICULO_ID + " = ?",
                new String[]{String.valueOf(vehiculo.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public boolean eliminarVehiculo(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_VEHICULOS, COL_VEHICULO_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public Vehiculo obtenerVehiculoPorId(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Vehiculo vehiculo = null;

        String[] columns = {
                COL_VEHICULO_ID,
                COL_VEHICULO_USUARIO_ID,
                COL_VEHICULO_ALIAS,
                COL_VEHICULO_MARCA,
                COL_VEHICULO_MODELO,
                COL_VEHICULO_ANIO,
                COL_VEHICULO_PLACA,
                COL_VEHICULO_COLOR,
                COL_VEHICULO_KILOMETRAJE,
                COL_VEHICULO_TRANSMISION,
                COL_VEHICULO_COMBUSTIBLE
        };

        Cursor cursor = db.query(
                TABLE_VEHICULOS,
                columns,
                COL_VEHICULO_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            vehiculo = new Vehiculo();
            vehiculo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_VEHICULO_ID)));
            vehiculo.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_USUARIO_ID)));
            vehiculo.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_ALIAS)));
            vehiculo.setMarca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MARCA)));
            vehiculo.setModelo(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MODELO)));
            vehiculo.setAnio(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_ANIO)));
            vehiculo.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_PLACA)));
            vehiculo.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COLOR)));
            vehiculo.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_KILOMETRAJE)));
            vehiculo.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_TRANSMISION)));
            vehiculo.setCombustible(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COMBUSTIBLE)));

            cursor.close();
        }
        db.close();
        return vehiculo;
    }

    public List<Vehiculo> obtenerTodosLosVehiculos(long usuarioId) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COL_VEHICULO_ID,
                COL_VEHICULO_ALIAS,
                COL_VEHICULO_MARCA,
                COL_VEHICULO_MODELO,
                COL_VEHICULO_ANIO,
                COL_VEHICULO_PLACA,
                COL_VEHICULO_COLOR,
                COL_VEHICULO_KILOMETRAJE,
                COL_VEHICULO_TRANSMISION,
                COL_VEHICULO_COMBUSTIBLE
        };

        Cursor cursor = db.query(
                TABLE_VEHICULOS,
                columns,
                COL_VEHICULO_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)},
                null, null,
                COL_VEHICULO_ALIAS + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_VEHICULO_ID)));
                vehiculo.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_ALIAS)));
                vehiculo.setMarca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MARCA)));
                vehiculo.setModelo(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MODELO)));
                vehiculo.setAnio(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_ANIO)));
                vehiculo.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_PLACA)));
                vehiculo.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COLOR)));
                vehiculo.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_KILOMETRAJE)));
                vehiculo.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_TRANSMISION)));
                vehiculo.setCombustible(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COMBUSTIBLE)));

                vehiculos.add(vehiculo);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return vehiculos;
    }

    private Vehiculo cursorToVehiculo(Cursor cursor) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_VEHICULO_ID)));
        vehiculo.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_USUARIO_ID)));
        vehiculo.setAlias(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_ALIAS)));
        vehiculo.setMarca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MARCA)));
        vehiculo.setModelo(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_MODELO)));
        vehiculo.setAnio(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_ANIO)));
        vehiculo.setPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_PLACA)));
        vehiculo.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COLOR)));
        vehiculo.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_VEHICULO_KILOMETRAJE)));
        vehiculo.setTransmision(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_TRANSMISION)));
        vehiculo.setCombustible(cursor.getString(cursor.getColumnIndexOrThrow(COL_VEHICULO_COMBUSTIBLE)));
        return vehiculo;
    }

    // Métodos para operaciones CRUD de mantenimientos
    public long agregarMantenimiento(Mantenimiento mantenimiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MANTENIMIENTO_PLACA, mantenimiento.getNoPlaca());
        values.put(COL_MANTENIMIENTO_SERVICIO, mantenimiento.getTipoServicio());
        values.put(COL_MANTENIMIENTO_DESCRIPCION, mantenimiento.getDescripcion());
        values.put(COL_MANTENIMIENTO_FECHA, mantenimiento.getFecha());
        values.put(COL_MANTENIMIENTO_KILOMETRAJE, mantenimiento.getKilometraje());
        values.put(COL_MANTENIMIENTO_COSTO, mantenimiento.getCosto());

        long id = db.insert(TABLE_MANTENIMIENTOS, null, values);
        db.close();
        return id;
    }

    public boolean actualizarMantenimiento(Mantenimiento mantenimiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MANTENIMIENTO_PLACA, mantenimiento.getNoPlaca());
        values.put(COL_MANTENIMIENTO_SERVICIO, mantenimiento.getTipoServicio());
        values.put(COL_MANTENIMIENTO_DESCRIPCION, mantenimiento.getDescripcion());
        values.put(COL_MANTENIMIENTO_FECHA, mantenimiento.getFecha());
        values.put(COL_MANTENIMIENTO_KILOMETRAJE, mantenimiento.getKilometraje());
        values.put(COL_MANTENIMIENTO_COSTO, mantenimiento.getCosto());

        int rowsAffected = db.update(TABLE_MANTENIMIENTOS, values,
                COL_MANTENIMIENTO_ID + " = ?",
                new String[]{String.valueOf(mantenimiento.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public boolean eliminarMantenimiento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_MANTENIMIENTOS,
                COL_MANTENIMIENTO_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public List<Mantenimiento> obtenerTodosMantenimientos() {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MANTENIMIENTOS,
                null, null, null, null, null,
                COL_MANTENIMIENTO_FECHA + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Mantenimiento mantenimiento = new Mantenimiento();
                mantenimiento.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_ID)));
                mantenimiento.setNoPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_PLACA)));
                mantenimiento.setTipoServicio(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_SERVICIO)));
                mantenimiento.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_DESCRIPCION)));
                mantenimiento.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_FECHA)));
                mantenimiento.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_KILOMETRAJE)));
                mantenimiento.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_COSTO)));

                mantenimientos.add(mantenimiento);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return mantenimientos;
    }

    // Nuevo método para búsqueda combinada
    public List<Mantenimiento> buscarMantenimientosCombinada(String query) {
        List<Mantenimiento> resultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Buscar tanto en placa como en servicio
        String selection = COL_MANTENIMIENTO_PLACA + " LIKE ? OR " +
                COL_MANTENIMIENTO_SERVICIO + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.query(TABLE_MANTENIMIENTOS,
                null, selection, selectionArgs, null, null,
                COL_MANTENIMIENTO_FECHA + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Mantenimiento mantenimiento = new Mantenimiento();
                mantenimiento.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_ID)));
                mantenimiento.setNoPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_PLACA)));
                mantenimiento.setTipoServicio(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_SERVICIO)));
                mantenimiento.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_DESCRIPCION)));
                mantenimiento.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_FECHA)));
                mantenimiento.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_KILOMETRAJE)));
                mantenimiento.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_COSTO)));

                resultados.add(mantenimiento);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return resultados;
    }

    public Mantenimiento obtenerMantenimientoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Mantenimiento mantenimiento = null;

        Cursor cursor = db.query(
                TABLE_MANTENIMIENTOS,
                new String[]{
                        COL_MANTENIMIENTO_ID,
                        COL_MANTENIMIENTO_PLACA,
                        COL_MANTENIMIENTO_SERVICIO,
                        COL_MANTENIMIENTO_DESCRIPCION,
                        COL_MANTENIMIENTO_FECHA,
                        COL_MANTENIMIENTO_KILOMETRAJE,
                        COL_MANTENIMIENTO_COSTO
                },
                COL_MANTENIMIENTO_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            mantenimiento = new Mantenimiento();
            mantenimiento.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_ID)));
            mantenimiento.setNoPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_PLACA)));
            mantenimiento.setTipoServicio(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_SERVICIO)));
            mantenimiento.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_DESCRIPCION)));
            mantenimiento.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_FECHA)));
            mantenimiento.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_KILOMETRAJE)));
            mantenimiento.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_COSTO)));

            cursor.close();
        }
        db.close();
        return mantenimiento;
    }

    public List<Mantenimiento> obtenerMantenimientosPorPlaca(String placa) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MANTENIMIENTOS,
                null,
                COL_MANTENIMIENTO_PLACA + " = ?",
                new String[]{placa},
                null, null,
                COL_MANTENIMIENTO_FECHA + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Mantenimiento mantenimiento = new Mantenimiento();
                mantenimiento.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_ID)));
                mantenimiento.setNoPlaca(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_PLACA)));
                mantenimiento.setTipoServicio(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_SERVICIO)));
                mantenimiento.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_DESCRIPCION)));
                mantenimiento.setFecha(cursor.getString(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_FECHA)));
                mantenimiento.setKilometraje(cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_KILOMETRAJE)));
                mantenimiento.setCosto(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_MANTENIMIENTO_COSTO)));

                mantenimientos.add(mantenimiento);
            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return mantenimientos;
    }

    public List<String> obtenerPlacasUnicas() {
        List<String> placas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Obtener placas de mantenimientos
        Cursor cursorMantenimientos = db.query(true, TABLE_MANTENIMIENTOS,
                new String[]{COL_MANTENIMIENTO_PLACA},
                null, null, null, null, null, null);

        if (cursorMantenimientos != null) {
            while (cursorMantenimientos.moveToNext()) {
                placas.add(cursorMantenimientos.getString(0));
            }
            cursorMantenimientos.close();
        }

        // Obtener placas de vehículos
        Cursor cursorVehiculos = db.query(true, TABLE_VEHICULOS,
                new String[]{COL_VEHICULO_PLACA},
                COL_VEHICULO_PLACA + " IS NOT NULL", null, null, null, null, null);

        if (cursorVehiculos != null) {
            while (cursorVehiculos.moveToNext()) {
                String placa = cursorVehiculos.getString(0);
                if (!placas.contains(placa)) {
                    placas.add(placa);
                }
            }
            cursorVehiculos.close();
        }

        db.close();
        return placas;
    }




}