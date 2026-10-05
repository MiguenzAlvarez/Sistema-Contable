/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

public class DetalleAsiento {

    private int id;
    private int asientoId;

    private String codigoCuenta;
    private String nombreCuenta;

    private double debe;
    private double haber;

    public DetalleAsiento() {
    }

    public DetalleAsiento(String codigoCuenta,
                          String nombreCuenta,
                          double debe,
                          double haber) {

        this.codigoCuenta = codigoCuenta;
        this.nombreCuenta = nombreCuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsientoId() {
        return asientoId;
    }

    public void setAsientoId(int asientoId) {
        this.asientoId = asientoId;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public double getDebe() {
        return debe;
    }

    public void setDebe(double debe) {
        this.debe = debe;
    }

    public double getHaber() {
        return haber;
    }

    public void setHaber(double haber) {
        this.haber = haber;
    }

}