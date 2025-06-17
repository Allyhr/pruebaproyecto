package com.example.proyectoprueba.vehiculos;
import org.json.JSONException;
import org.json.JSONObject;

public class Vehiculo {
    private int id, marcaId, modeloId, anio, colorId, kilometraje, transmisionId, combustibleId;
    private String alias, placa;

    public Vehiculo(int id, String alias, int marcaId, int modeloId, int anio, String placa,
                    int colorId, int kilometraje, int transmisionId, int combustibleId) {
        this.id = id;
        this.alias = alias;
        this.marcaId = marcaId;
        this.modeloId = modeloId;
        this.anio = anio;
        this.placa = placa;
        this.colorId = colorId;
        this.kilometraje = kilometraje;
        this.transmisionId = transmisionId;
        this.combustibleId = combustibleId;
    }

    // Getter y Setter para ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters para los demás atributos
    public String getAlias() {
        return alias;
    }

    public int getMarcaId() {
        return marcaId;
    }

    public int getModeloId() {
        return modeloId;
    }

    public int getAnio() {
        return anio;
    }

    public String getPlaca() {
        return placa;
    }

    public int getColorId() {
        return colorId;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public int getTransmisionId() {
        return transmisionId;
    }

    public int getCombustibleId() {
        return combustibleId;
    }

    public static Vehiculo fromJson(JSONObject o) throws JSONException {
        return new Vehiculo(
                o.getInt("id"), o.getString("alias"),
                o.getInt("marca_id"), o.getInt("modelo_id"),
                o.getInt("anio"), o.getString("placa"),
                o.getInt("color_id"), o.getInt("kilometraje"),
                o.getInt("transmision_id"), o.getInt("combustible_id")
        );
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("vehiculo_id", id);
        o.put("nombre", alias);
        o.put("marca_id", marcaId);
        o.put("modelo_id", modeloId);
        o.put("anio", anio);
        o.put("placas", placa);
        o.put("color_id", colorId);
        o.put("kilometraje", kilometraje);
        o.put("transmision_id", transmisionId);
        o.put("combustible_id", combustibleId);
        return o;
    }
}
