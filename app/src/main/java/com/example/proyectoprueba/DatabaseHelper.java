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
    // Versión de la base de datos (incrementar cuando hagas cambios en el esquema)
    private static final String DATABASE_NAME = "mantenimiento_vehiculos.db";
    private static final int DATABASE_VERSION = 3;

    // Nombres de tablas
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String TABLE_TALLERES = "talleres";
    private static final String TABLE_VEHICULOS = "vehiculos";
    private static final String TABLE_TIPOS_SERVICIO = "tipos_servicio";
    private static final String TABLE_MANTENIMIENTOS = "mantenimientos";

    // Columnas comunes
    private static final String COL_ID = "id";

    // Columnas para usuarios
    private static final String COL_USUARIO_NOMBRE = "nombre_completo";
    private static final String COL_USUARIO_USERNAME = "usuario";
    private static final String COL_USUARIO_EMAIL = "correo";
    private static final String COL_USUARIO_PASSWORD = "contrasena";

    // Columnas para talleres
    private static final String COL_TALLER_NOMBRE = "nombre";
    private static final String COL_TALLER_DIRECCION = "direccion";
    private static final String COL_TALLER_LATITUD = "latitud";
    private static final String COL_TALLER_LONGITUD = "longitud";

    // Columnas para vehículos
    private static final String COL_VEHICULO_MARCA = "marca";
    private static final String COL_VEHICULO_MODELO = "modelo";
    private static final String COL_VEHICULO_ANIO = "anio";
    private static final String COL_VEHICULO_PLACA = "placa";
    private static final String COL_VEHICULO_USUARIO_ID = "usuario_id";

    // Columnas para tipos de servicio
    private static final String COL_TIPO_SERVICIO_NOMBRE = "nombre";

    // Columnas para mantenimientos
    private static final String COL_MANTENIMIENTO_VEHICULO_ID = "vehiculo_id";
    private static final String COL_MANTENIMIENTO_TIPO_SERVICIO_ID = "tipo_servicio_id";
    private static final String COL_MANTENIMIENTO_DESCRIPCION = "descripcion";
    private static final String COL_MANTENIMIENTO_FECHA = "fecha";
    private static final String COL_MANTENIMIENTO_KILOMETRAJE = "kilometraje";
    private static final String COL_MANTENIMIENTO_COSTO = "costo";
    private static final String COL_MANTENIMIENTO_TALLER_ID = "taller_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear todas las tablas
        crearTablaUsuarios(db);
        crearTablaTalleres(db);
        crearTablaVehiculos(db);
        crearTablaTiposServicio(db);
        crearTablaMantenimientos(db);

        // Insertar datos iniciales
        insertarTiposServicioIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar todas las tablas existentes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TALLERES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICULOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPOS_SERVICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANTENIMIENTOS);

        // Volver a crear las tablas
        onCreate(db);
    }

    // =============== MÉTODOS PARA CREAR TABLAS ===============
    private void crearTablaUsuarios(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USUARIO_NOMBRE + " TEXT NOT NULL,"
                + COL_USUARIO_USERNAME + " TEXT NOT NULL,"
                + COL_USUARIO_EMAIL + " TEXT NOT NULL UNIQUE,"
                + COL_USUARIO_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(sql);
    }

    private void crearTablaTalleres(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_TALLERES + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TALLER_NOMBRE + " TEXT NOT NULL,"
                + COL_TALLER_DIRECCION + " TEXT NOT NULL,"
                + COL_TALLER_LATITUD + " REAL NOT NULL,"
                + COL_TALLER_LONGITUD + " REAL NOT NULL)";
        db.execSQL(sql);
    }

    private void crearTablaVehiculos(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_VEHICULOS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_VEHICULO_MARCA + " TEXT NOT NULL,"
                + COL_VEHICULO_MODELO + " TEXT NOT NULL,"
                + COL_VEHICULO_ANIO + " INTEGER NOT NULL,"
                + COL_VEHICULO_PLACA + " TEXT NOT NULL UNIQUE,"
                + COL_VEHICULO_USUARIO_ID + " INTEGER NOT NULL)";
        db.execSQL(sql);
    }

    private void crearTablaTiposServicio(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_TIPOS_SERVICIO + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TIPO_SERVICIO_NOMBRE + " TEXT NOT NULL UNIQUE)";
        db.execSQL(sql);
    }

    private void crearTablaMantenimientos(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_MANTENIMIENTOS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MANTENIMIENTO_VEHICULO_ID + " INTEGER NOT NULL,"
                + COL_MANTENIMIENTO_TIPO_SERVICIO_ID + " INTEGER NOT NULL,"
                + COL_MANTENIMIENTO_DESCRIPCION + " TEXT,"
                + COL_MANTENIMIENTO_FECHA + " TEXT NOT NULL,"
                + COL_MANTENIMIENTO_KILOMETRAJE + " INTEGER NOT NULL,"
                + COL_MANTENIMIENTO_COSTO + " REAL,"
                + COL_MANTENIMIENTO_TALLER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COL_MANTENIMIENTO_VEHICULO_ID + ") REFERENCES " + TABLE_VEHICULOS + "(" + COL_ID + "),"
                + "FOREIGN KEY(" + COL_MANTENIMIENTO_TIPO_SERVICIO_ID + ") REFERENCES " + TABLE_TIPOS_SERVICIO + "(" + COL_ID + "),"
                + "FOREIGN KEY(" + COL_MANTENIMIENTO_TALLER_ID + ") REFERENCES " + TABLE_TALLERES + "(" + COL_ID + "))";
        db.execSQL(sql);
    }

    // =============== MÉTODOS PARA DATOS INICIALES ===============
    private void insertarTiposServicioIniciales(SQLiteDatabase db) {
        String[] tiposServicio = {
                "Cambio de aceite", "Revisión general", "Cambio de frenos",
                "Alineación y balanceo", "Cambio de llantas", "Lavado y detallado",
                "Revisión de suspensión", "Revisión de batería", "Revisión de luces",
                "Afinación completa"
        };

        for (String servicio : tiposServicio) {
            ContentValues values = new ContentValues();
            values.put(COL_TIPO_SERVICIO_NOMBRE, servicio);
            db.insert(TABLE_TIPOS_SERVICIO, null, values);
        }
    }

    // =============== MÉTODOS PARA USUARIOS ===============
    public long agregarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_USUARIO_NOMBRE, usuario.getNombreCompleto());
        values.put(COL_USUARIO_USERNAME, usuario.getUsuario());
        values.put(COL_USUARIO_EMAIL, usuario.getCorreo());
        values.put(COL_USUARIO_PASSWORD, usuario.getContrasena());

        long id = db.insert(TABLE_USUARIOS, null, values);
        db.close();
        return id;
    }

    public Usuario obtenerUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Usuario usuario = null;

        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COL_ID, COL_USUARIO_NOMBRE, COL_USUARIO_USERNAME, COL_USUARIO_EMAIL, COL_USUARIO_PASSWORD},
                COL_USUARIO_EMAIL + " = ? AND " + COL_USUARIO_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getLong(0));
            usuario.setNombreCompleto(cursor.getString(1));
            usuario.setUsuario(cursor.getString(2));
            usuario.setCorreo(cursor.getString(3));
            usuario.setContrasena(cursor.getString(4));
            cursor.close();
        }
        db.close();
        return usuario;
    }

    public boolean existeUsuario(String usuario, String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USUARIOS,
                new String[]{COL_ID},
                COL_USUARIO_USERNAME + " = ? OR " + COL_USUARIO_EMAIL + " = ?",
                new String[]{usuario, correo},
                null, null, null);

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    // =============== MÉTODOS PARA VEHÍCULOS ===============
    public long agregarVehiculo(Vehiculo vehiculo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_VEHICULO_MARCA, vehiculo.getMarca());
        values.put(COL_VEHICULO_MODELO, vehiculo.getModelo());
        values.put(COL_VEHICULO_ANIO, vehiculo.getAnio());
        values.put(COL_VEHICULO_PLACA, vehiculo.getPlaca());
        values.put(COL_VEHICULO_USUARIO_ID, vehiculo.getUsuarioId());

        long id = db.insert(TABLE_VEHICULOS, null, values);
        db.close();
        return id;
    }

    public List<Vehiculo> obtenerVehiculosPorUsuario(long usuarioId) {
        List<Vehiculo> vehiculos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VEHICULOS,
                new String[]{COL_ID, COL_VEHICULO_MARCA, COL_VEHICULO_MODELO, COL_VEHICULO_ANIO, COL_VEHICULO_PLACA},
                COL_VEHICULO_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)},
                null, null, COL_VEHICULO_MARCA + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(cursor.getInt(0));
                vehiculo.setMarca(cursor.getString(1));
                vehiculo.setModelo(cursor.getString(2));
                vehiculo.setAnio(cursor.getInt(3));
                vehiculo.setPlaca(cursor.getString(4));
                vehiculo.setUsuarioId(usuarioId);
                vehiculos.add(vehiculo);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return vehiculos;
    }

    public Cursor obtenerVehiculosParaSpinner(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_VEHICULOS,
                new String[]{COL_ID, COL_VEHICULO_MARCA + " || ' ' || " + COL_VEHICULO_MODELO + " AS vehiculo"},
                COL_VEHICULO_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)},
                null, null, COL_VEHICULO_MARCA + " ASC");
    }

    public Vehiculo obtenerVehiculoPorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Vehiculo vehiculo = null;

        Cursor cursor = db.query(TABLE_VEHICULOS,
                new String[]{COL_ID, COL_VEHICULO_MARCA, COL_VEHICULO_MODELO, COL_VEHICULO_ANIO, COL_VEHICULO_PLACA, COL_VEHICULO_USUARIO_ID},
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            vehiculo = new Vehiculo();
            vehiculo.setId(cursor.getInt(0));
            vehiculo.setMarca(cursor.getString(1));
            vehiculo.setModelo(cursor.getString(2));
            vehiculo.setAnio(cursor.getInt(3));
            vehiculo.setPlaca(cursor.getString(4));
            vehiculo.setUsuarioId(cursor.getLong(5));
            cursor.close();
        }
        db.close();
        return vehiculo;
    }

    // =============== MÉTODOS PARA TIPOS DE SERVICIO ===============
    public Cursor obtenerTiposServicioParaSpinner() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TIPOS_SERVICIO,
                new String[]{COL_ID, COL_TIPO_SERVICIO_NOMBRE},
                null, null, null, null, COL_TIPO_SERVICIO_NOMBRE + " ASC");
    }

    // =============== MÉTODOS PARA TALLERES ===============
    public long agregarTaller(Taller taller) {
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

    public List<Taller> obtenerTodosTalleres() {
        List<Taller> talleres = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TALLERES,
                new String[]{COL_ID, COL_TALLER_NOMBRE, COL_TALLER_DIRECCION, COL_TALLER_LATITUD, COL_TALLER_LONGITUD},
                null, null, null, null, COL_TALLER_NOMBRE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Taller taller = new Taller();
                taller.setId(cursor.getInt(0));
                taller.setNombre(cursor.getString(1));
                taller.setDireccion(cursor.getString(2));
                taller.setLatitud(cursor.getDouble(3));
                taller.setLongitud(cursor.getDouble(4));
                talleres.add(taller);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return talleres;
    }

    // =============== MÉTODOS PARA MANTENIMIENTOS ===============
    public long agregarMantenimiento(Mantenimiento mantenimiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MANTENIMIENTO_VEHICULO_ID, mantenimiento.getVehiculoId());
        values.put(COL_MANTENIMIENTO_TIPO_SERVICIO_ID, mantenimiento.getTipoServicioId());
        values.put(COL_MANTENIMIENTO_DESCRIPCION, mantenimiento.getDescripcion());
        values.put(COL_MANTENIMIENTO_FECHA, mantenimiento.getFecha());
        values.put(COL_MANTENIMIENTO_KILOMETRAJE, mantenimiento.getKilometraje());
        values.put(COL_MANTENIMIENTO_COSTO, mantenimiento.getCosto());
        values.put(COL_MANTENIMIENTO_TALLER_ID, mantenimiento.getTallerId());

        long id = db.insert(TABLE_MANTENIMIENTOS, null, values);
        db.close();
        return id;
    }

    public List<Mantenimiento> obtenerMantenimientosPorUsuario(long usuarioId) {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, v." + COL_VEHICULO_MARCA + ", v." + COL_VEHICULO_MODELO + ", " +
                "ts." + COL_TIPO_SERVICIO_NOMBRE + ", t." + COL_TALLER_NOMBRE + " " +
                "FROM " + TABLE_MANTENIMIENTOS + " m " +
                "INNER JOIN " + TABLE_VEHICULOS + " v ON m." + COL_MANTENIMIENTO_VEHICULO_ID + " = v." + COL_ID + " " +
                "INNER JOIN " + TABLE_TIPOS_SERVICIO + " ts ON m." + COL_MANTENIMIENTO_TIPO_SERVICIO_ID + " = ts." + COL_ID + " " +
                "LEFT JOIN " + TABLE_TALLERES + " t ON m." + COL_MANTENIMIENTO_TALLER_ID + " = t." + COL_ID + " " +
                "WHERE v." + COL_VEHICULO_USUARIO_ID + " = ? " +
                "ORDER BY m." + COL_MANTENIMIENTO_FECHA + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Mantenimiento mantenimiento = new Mantenimiento();
                mantenimiento.setId(cursor.getInt(0));
                mantenimiento.setVehiculoId(cursor.getInt(1));
                mantenimiento.setTipoServicioId(cursor.getInt(2));
                mantenimiento.setDescripcion(cursor.getString(3));
                mantenimiento.setFecha(cursor.getString(4));
                mantenimiento.setKilometraje(cursor.getInt(5));
                mantenimiento.setCosto(cursor.getDouble(6));
                mantenimiento.setTallerId(cursor.isNull(7) ? 0 : cursor.getInt(7));
                mantenimiento.setVehiculoNombre(cursor.getString(8) + " " + cursor.getString(9));
                mantenimiento.setTipoServicioNombre(cursor.getString(10));
                mantenimiento.setTallerNombre(cursor.isNull(11) ? "No especificado" : cursor.getString(11));
                mantenimientos.add(mantenimiento);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return mantenimientos;
    }

    public boolean actualizarMantenimiento(Mantenimiento mantenimiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_MANTENIMIENTO_VEHICULO_ID, mantenimiento.getVehiculoId());
        values.put(COL_MANTENIMIENTO_TIPO_SERVICIO_ID, mantenimiento.getTipoServicioId());
        values.put(COL_MANTENIMIENTO_DESCRIPCION, mantenimiento.getDescripcion());
        values.put(COL_MANTENIMIENTO_FECHA, mantenimiento.getFecha());
        values.put(COL_MANTENIMIENTO_KILOMETRAJE, mantenimiento.getKilometraje());
        values.put(COL_MANTENIMIENTO_COSTO, mantenimiento.getCosto());
        values.put(COL_MANTENIMIENTO_TALLER_ID, mantenimiento.getTallerId());

        int rowsAffected = db.update(TABLE_MANTENIMIENTOS, values,
                COL_ID + " = ?",
                new String[]{String.valueOf(mantenimiento.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public boolean eliminarMantenimiento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_MANTENIMIENTOS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // =============== MÉTODOS UTILITARIOS ===============
    public int obtenerIdVehiculoPorNombre(String marcaModelo, long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] partes = marcaModelo.split(" ");
        String marca = partes[0];
        String modelo = marcaModelo.substring(marca.length()).trim();

        Cursor cursor = db.query(TABLE_VEHICULOS,
                new String[]{COL_ID},
                COL_VEHICULO_MARCA + " = ? AND " + COL_VEHICULO_MODELO + " = ? AND " + COL_VEHICULO_USUARIO_ID + " = ?",
                new String[]{marca, modelo, String.valueOf(usuarioId)},
                null, null, null);

        int id = -1;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return id;
    }

    public int obtenerIdTipoServicioPorNombre(String nombreServicio) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TIPOS_SERVICIO,
                new String[]{COL_ID},
                COL_TIPO_SERVICIO_NOMBRE + " = ?",
                new String[]{nombreServicio},
                null, null, null);

        int id = -1;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return id;
    }

    public Taller obtenerTallerPorId(int id) {
       SQLiteDatabase db = this.getReadableDatabase();
        Taller taller = null;

       Cursor cursor = db.query(TABLE_TALLERES,
              new String[]{COL_ID, COL_TALLER_NOMBRE, COL_TALLER_DIRECCION, COL_TALLER_LATITUD, COL_TALLER_LONGITUD},
                COL_ID + " = ?",
               new String[]{String.valueOf(id)},
                null, null, null);

       if (cursor != null && cursor.moveToFirst()) {
          taller = new Taller();
           taller.setId(cursor.getInt(0));
           taller.setNombre(cursor.getString(1));
           taller.setDireccion(cursor.getString(2));
          taller.setLatitud(cursor.getDouble(3));
            taller.setLongitud(cursor.getDouble(4));
            cursor.close();
        }
        db.close();
        return taller;
    }
}