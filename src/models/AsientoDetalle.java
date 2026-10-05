package models;

public class AsientoDetalle {

    private int id;
    private int asientoId;
    private String cuentaCodigo;
    private String cuentaNombre; // solo para mostrar en pantalla, no se persiste
    private double debe;
    private double haber;
    private int orden;

    // Constructor para armar una línea NUEVA (antes de guardarla en la BD)
    public AsientoDetalle(String cuentaCodigo, double debe, double haber) {
        this.cuentaCodigo = cuentaCodigo;
        this.debe = debe;
        this.haber = haber;
    }

    // Constructor para reconstruir una línea ya existente (leída de la BD)
    public AsientoDetalle(int id, int asientoId, String cuentaCodigo, String cuentaNombre,
                          double debe, double haber, int orden) {
        this.id = id;
        this.asientoId = asientoId;
        this.cuentaCodigo = cuentaCodigo;
        this.cuentaNombre = cuentaNombre;
        this.debe = debe;
        this.haber = haber;
        this.orden = orden;
    }

    public int getId() {
        return id;
    }

    public int getAsientoId() {
        return asientoId;
    }

    public String getCuentaCodigo() {
        return cuentaCodigo;
    }

    public String getCuentaNombre() {
        return cuentaNombre;
    }

    public double getDebe() {
        return debe;
    }

    public double getHaber() {
        return haber;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
