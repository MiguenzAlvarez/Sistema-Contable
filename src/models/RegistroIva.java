package models;

import java.sql.Date;

// Representa una fila del libro auxiliar de IVA Compras o IVA Ventas.
public class RegistroIva {

    private Date fecha;
    private String nroComprobante;
    private String tipoComprobante;
    private String cuit;
    private String razonSocial;      // proveedor (compras) o cliente (ventas)
    private String condicionIva;     // Resp. Inscripto / Monotributo / Exento / Consumidor Final
    private double netoGravado;
    private double netoNoGravado;
    private double exento;
    private double alicuota;         // porcentaje: 21, 10.5, 27, etc.
    private double iva;
    private double total;
    private String dni = "";
    private int puntoVenta;
    private double otrosPercepciones;
    private double horas;

    public RegistroIva(Date fecha, String nroComprobante, String tipoComprobante, String cuit,
                        String razonSocial, String condicionIva, double netoGravado,
                        double netoNoGravado, double exento, double alicuota,
                        double iva, double total) {
        this.fecha = fecha;
        this.nroComprobante = nroComprobante;
        this.tipoComprobante = tipoComprobante;
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.condicionIva = condicionIva;
        this.netoGravado = netoGravado;
        this.netoNoGravado = netoNoGravado;
        this.exento = exento;
        this.alicuota = alicuota;
        this.iva = iva;
        this.total = total;
    }

    public Date getFecha() { return fecha; }
    public String getNroComprobante() { return nroComprobante; }
    public String getTipoComprobante() { return tipoComprobante; }
    public String getCuit() { return cuit; }
    public String getRazonSocial() { return razonSocial; }
    public String getCondicionIva() { return condicionIva; }
    public double getNetoGravado() { return netoGravado; }
    public double getNetoNoGravado() { return netoNoGravado; }
    public double getExento() { return exento; }
    public double getAlicuota() { return alicuota; }
    public double getIva() { return iva; }
    public double getTotal() { return total; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public int getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(int puntoVenta) { this.puntoVenta = puntoVenta; }
    public double getOtrosPercepciones() { return otrosPercepciones; }
    public void setOtrosPercepciones(double valor) { otrosPercepciones = valor; }
    public double getHoras() { return horas; }
    public void setHoras(double valor) { horas = valor; }
    public void setNroComprobante(String numero) { nroComprobante = numero; }
}
