package es.correointeligente.cipostal.cimobile.Model;

public class Notificador {
    // Codigo Escaner
    private String codigo;
    private String nombre;
    private String delegacion;

    public Notificador() {
    }

    public Notificador(String codigo, String nombre, String delegacion) {
        // Codigo Escaner
        this.codigo = codigo;
        this.nombre = nombre;
        this.delegacion = delegacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDelegacion() {
        return delegacion;
    }

    public void setDelegacion(String delegacion) {
        this.delegacion = delegacion;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
