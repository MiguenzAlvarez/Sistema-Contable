package models;

import java.util.ArrayList;
import java.util.List;

public class Asiento {

    private int id;
    private int numero;
    private String fecha; // formato yyyy-MM-dd
    private String concepto;
    private double totalDebe;
    private double totalHaber;
    private List<AsientoDetalle> detalles;

    public Asiento() {
        this.detalles = new ArrayList<>();
    }

    // Constructor para armar un asiento NUEVO (antes de guardarlo en la BD)
    public Asiento(String fecha, String concepto, List<AsientoDetalle> detalles) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.detalles = detalles != null ? detalles : new ArrayList<>();
        double sumaDebe = 0, sumaHaber = 0;
        for (AsientoDetalle d : this.detalles) {
            sumaDebe += d.getDebe();
            sumaHaber += d.getHaber();
        }
        this.totalDebe = sumaDebe;
        this.totalHaber = sumaHaber;
    }

    // Constructor para reconstruir un asiento ya existente (leído de la BD)
    public Asiento(int id, int numero, String fecha, String concepto, double totalDebe, double totalHaber) {
        this.id = id;
        this.numero = numero;
        this.fecha = fecha;
        this.concepto = concepto;
        this.totalDebe = totalDebe;
        this.totalHaber = totalHaber;
        this.detalles = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public double getTotalDebe() { return totalDebe; }
    public void setTotalDebe(double totalDebe) { this.totalDebe = totalDebe; }

    public double getTotalHaber() { return totalHaber; }
    public void setTotalHaber(double totalHaber) { this.totalHaber = totalHaber; }

    public List<AsientoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<AsientoDetalle> detalles) { this.detalles = detalles; }
}