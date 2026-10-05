package models;

import java.util.List;

public class Asiento {

    private int id;
    private int numero;       // número correlativo visible para el usuario
    private String fecha;     // formato "yyyy-MM-dd" (el que usa SQL)
    private String concepto;
    private List<AsientoDetalle> detalles;
    private double totalDebe;
    private double totalHaber;

    // Constructor para armar un asiento NUEVO (antes de guardarlo en la BD)
    public Asiento(String fecha, String concepto, List<AsientoDetalle> detalles) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.detalles = detalles;
    }

    // Constructor para reconstruir la CABECERA de un asiento ya existente,
    // con los totales ya calculados desde la BD (sin cargar el detalle todavía)
    public Asiento(int id, int numero, String fecha, String concepto,
                   double totalDebe, double totalHaber) {
        this.id = id;
        this.numero = numero;
        this.fecha = fecha;
        this.concepto = concepto;
        this.totalDebe = totalDebe;
        this.totalHaber = totalHaber;
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public List<AsientoDetalle> getDetalles() {
        return detalles;
    }

    public double getTotalDebe() {
        return totalDebe;
    }

    public double getTotalHaber() {
        return totalHaber;
    }

    // Un asiento está balanceado cuando el Debe y el Haber coinciden
    // (partida doble). Se compara con una tolerancia mínima por
    // redondeos de punto flotante.
    public boolean estaBalanceado() {
        return Math.abs(totalDebe - totalHaber) < 0.005;
    }
}
