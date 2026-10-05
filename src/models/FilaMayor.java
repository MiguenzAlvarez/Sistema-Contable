package models;

import java.sql.Date;

// Representa una fila ya procesada del Libro Mayor de una cuenta,
// con el saldo corrido y su naturaleza (Deudor/Acreedor) calculados.
public class FilaMayor {

    private Date fecha;
    private int numeroAsiento;
    private String detalle;
    private double debe;
    private double haber;
    private double saldo;       // siempre positivo, el signo lo da naturalezaSaldo
    private String naturalezaSaldo; // "D" o "A"

    public FilaMayor(Date fecha, int numeroAsiento, String detalle, double debe, double haber,
                      double saldo, String naturalezaSaldo) {
        this.fecha = fecha;
        this.numeroAsiento = numeroAsiento;
        this.detalle = detalle;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
        this.naturalezaSaldo = naturalezaSaldo;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public String getDetalle() {
        return detalle;
    }

    public double getDebe() {
        return debe;
    }

    public double getHaber() {
        return haber;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNaturalezaSaldo() {
        return naturalezaSaldo;
    }
}
