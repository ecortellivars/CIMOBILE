package es.correointeligente.cipostal.cimobile.Model;

public class Resultado {
    private String codigo;
    private String descripcion;
    private Integer esFinal;

    public Resultado() {
    }

    public Resultado(String codigo, String descripcion, Integer esFinal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.esFinal = esFinal;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEsFinal() {
        return esFinal;
    }

    public void setEsFinal(Integer esFinal) {
        this.esFinal = esFinal;
    }

}
