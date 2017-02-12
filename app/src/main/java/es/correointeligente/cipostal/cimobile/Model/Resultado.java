package es.correointeligente.cipostal.cimobile.Model;

public class Resultado {
    private String codigo;
    private String descripcion;
    private Boolean esFinal;
    private String codigoSegundoIntento;
    private Boolean esResultadoOficina;
    private Boolean notifica;

    public Resultado() {
    }

    public Resultado(String codigo, String descripcion, Boolean esFinal,
                     String codigoSegundoIntento, Boolean esResultadoOficina,
                     Boolean notifica) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.esFinal = esFinal;
        this.codigoSegundoIntento = codigoSegundoIntento;
        this.esResultadoOficina = esResultadoOficina;
        this.notifica = notifica;
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

    public Boolean getEsFinal() {
        return esFinal;
    }

    public void setEsFinal(Boolean esFinal) {
        this.esFinal = esFinal;
    }

    public String getCodigoSegundoIntento() {
        return codigoSegundoIntento;
    }

    public void setCodigoSegundoIntento(String codigoSegundoIntento) {
        this.codigoSegundoIntento = codigoSegundoIntento;
    }

    public Boolean getEsResultadoOficina() {
        return esResultadoOficina;
    }

    public void setEsResultadoOficina(Boolean esResultadoOficina) {
        this.esResultadoOficina = esResultadoOficina;
    }

    public Boolean getNotifica() {
        return notifica;
    }

    public void setNotifica(Boolean notifica) {
        this.notifica = notifica;
    }
}
