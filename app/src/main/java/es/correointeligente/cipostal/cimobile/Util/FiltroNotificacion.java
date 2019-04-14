package es.correointeligente.cipostal.cimobile.Util;

public class FiltroNotificacion {

    private Boolean entregado;
    private Boolean dirIncorrecta;
    private Boolean ausente;
    private Boolean desconocido;
    private Boolean fallecido;
    private Boolean rehusado;
    private Boolean nadieSeHaceCargo;
    private Boolean marcadas;
    private Boolean entregadoEnOficina;
    private Boolean noEntregadoEnOficina;
    private String referencia;
    private Integer pagina;

    public FiltroNotificacion() {
        this.entregado = false;
        this.dirIncorrecta = false;
        this.ausente = false;
        this.desconocido = false;
        this.fallecido = false;
        this.rehusado = false;
        this.nadieSeHaceCargo = false;
        this.entregadoEnOficina = false;
        this.noEntregadoEnOficina = false;
        this.marcadas = false;
        this.pagina = 0;
    }



    public FiltroNotificacion(Boolean entregado, Boolean dirIncorrecta, Boolean ausente, Boolean desconocido, Boolean fallecido,
                              Boolean rehusado, Boolean nadieSeHaceCargo, Boolean marcadas, Boolean entregadoEnOficina, Boolean noEntregadoEnOficina, String referencia,
                              Integer pagina) {
        this.entregado = entregado;
        this.dirIncorrecta = dirIncorrecta;
        this.ausente = ausente;
        this.desconocido = desconocido;
        this.fallecido = fallecido;
        this.rehusado = rehusado;
        this.nadieSeHaceCargo = nadieSeHaceCargo;
        this.marcadas = marcadas;
        this.entregadoEnOficina = entregadoEnOficina;
        this.noEntregadoEnOficina = noEntregadoEnOficina;
        this.referencia = referencia;
        this.pagina = pagina;
    }

    public Boolean getEntregado() {
        return entregado;
    }

    public void setEntregado(Boolean entregado) {
        this.entregado = entregado;
    }

    public Boolean getDirIncorrecta() { return dirIncorrecta; }

    public void setDirIncorrecta(Boolean dirIncorrecta) {this.dirIncorrecta = dirIncorrecta;}

    public Boolean getAusente() {return ausente;}

    public void setAusente(Boolean ausente) {
        this.ausente = ausente;
    }

    public Boolean getDesconocido() {
        return desconocido;
    }

    public void setDesconocido(Boolean desconocido) {
        this.desconocido = desconocido;
    }

    public Boolean getFallecido() {
        return fallecido;
    }

    public void setFallecido(Boolean fallecido) {
        this.fallecido = fallecido;
    }

    public Boolean getRehusado() {
        return rehusado;
    }

    public void setRehusado(Boolean rehusado) {
        this.rehusado = rehusado;
    }

    public Boolean getNadieSeHaceCargo() {
        return nadieSeHaceCargo;
    }

    public void setNadieSeHaceCargo(Boolean nadieSeHaceCargo) { this.nadieSeHaceCargo = nadieSeHaceCargo; }

    public Boolean getMarcadas() {
        return marcadas;
    }

    public void setMarcadas(Boolean marcadas) {
        this.marcadas = marcadas;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public Boolean getEntregadoEnOficina() {return entregadoEnOficina;}

    public void setEntregadoEnOficna(Boolean entregadoEnOficina) {this.entregadoEnOficina = entregadoEnOficina;}

    public Boolean getNoEntregadoEnOficina() {return noEntregadoEnOficina;}

    public void setNoEntregadoEnOficina(Boolean noEntregadoEnOficina) {this.noEntregadoEnOficina = noEntregadoEnOficina;}

}
