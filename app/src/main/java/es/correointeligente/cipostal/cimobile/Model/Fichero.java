package es.correointeligente.cipostal.cimobile.Model;

public class Fichero {
    private String nombreFichero;
    private String codigoCliente;
    private String fechaFichero;
    private String numRemesas;
    private String numNotificaciones;

    public Fichero() {
    }

    public Fichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    public Fichero(String nombreFichero, String codigoCliente, String fechaFichero, String numRemesas, String numNotificaciones) {
        this.nombreFichero = nombreFichero;
        this.codigoCliente = codigoCliente;
        this.fechaFichero = fechaFichero;
        this.numRemesas = numRemesas;
        this.numNotificaciones = numNotificaciones;
    }

    public String getNombreFichero() {
        return nombreFichero;
    }

    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getFechaFichero() {
        return fechaFichero;
    }

    public void setFechaFichero(String fechaFichero) {
        this.fechaFichero = fechaFichero;
    }

    public String getNumRemesas() {
        return numRemesas;
    }

    public void setNumRemesas(String numRemesas) {
        this.numRemesas = numRemesas;
    }

    public String getNumNotificaciones() {
        return numNotificaciones;
    }

    public void setNumNotificaciones(String numNotificaciones) {
        this.numNotificaciones = numNotificaciones;
    }
}
