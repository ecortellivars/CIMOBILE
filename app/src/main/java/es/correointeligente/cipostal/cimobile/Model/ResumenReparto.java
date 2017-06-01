package es.correointeligente.cipostal.cimobile.Model;

public class ResumenReparto {
    private Integer totFicheros;
    private Integer totNotificaciones;
    private Integer totNotifGestionadas;



    private Integer totNotifPendientesSegundoHoy;
    private Integer totNotifPendientesSegundoOtroDia;
    private Integer totNotifMarcadas;
    private Integer numEntregados;
    private Integer numDirIncorrectas;
    private Integer numAusentes;
    private Integer numAusentesPendientes;
    private Integer numDesconocidos;
    private Integer numFallecidos;
    private Integer numRehusados;
    private Integer numNadieSeHaceCargo;
    private Integer numNadieSeHaceCargoPendientes;


    public ResumenReparto() {
    }

    public ResumenReparto(int totFicheros, int totNotificaciones,
                          int totNotifGestionadas, int totNotifMarcadas, int totNotifPendientesSegundoHoy,
                          int numEntregados, int totNotifPendientesSegundoOtroDia,
                          int numDirIncorrectas, int numAusentes, int numAusentesPendientes, int numDesconocidos,
                          int numFallecidos, int numRehusados, int numNadieSeHaceCargo, int numNadieSeHaceCargoPendientes) {
        this.totFicheros = totFicheros;
        this.totNotificaciones = totNotificaciones;
        this.totNotifGestionadas = totNotifGestionadas;
        this.totNotifPendientesSegundoHoy = totNotifPendientesSegundoHoy;
        this.totNotifPendientesSegundoOtroDia = totNotifPendientesSegundoOtroDia;
        this.totNotifMarcadas = totNotifMarcadas;
        this.numEntregados = numEntregados;
        this.numDirIncorrectas = numDirIncorrectas;
        this.numAusentes = numAusentes;
        this.numAusentesPendientes = numAusentesPendientes;
        this.numDesconocidos = numDesconocidos;
        this.numFallecidos = numFallecidos;
        this.numRehusados = numRehusados;
        this.numNadieSeHaceCargo = numNadieSeHaceCargo;
        this.numNadieSeHaceCargoPendientes = numNadieSeHaceCargoPendientes;
    }

    public Integer getTotFicheros() {
        return totFicheros;
    }

    public void setTotFicheros(Integer totFicheros) {
        this.totFicheros = totFicheros;
    }

    public Integer getTotNotificaciones() {
        return totNotificaciones;
    }

    public void setTotNotificaciones(Integer totNotificaciones) { this.totNotificaciones = totNotificaciones; }

    public Integer getTotNotifGestionadas() {
        return totNotifGestionadas;
    }

    public void setTotNotifGestionadas(Integer totNotifGestionadas) { this.totNotifGestionadas = totNotifGestionadas; }

    public Integer getTotNotifMarcadas() {
        return totNotifMarcadas;
    }

    public void setTotNotifMarcadas(Integer totNotifMarcadas) { this.totNotifMarcadas = totNotifMarcadas; }

    public Integer getNumEntregados() { return numEntregados; }

    public void setNumEntregados(Integer numEntregados) { this.numEntregados = numEntregados; }

    public Integer getNumDirIncorrectas() {
        return numDirIncorrectas;
    }

    public void setNumDirIncorrectas(Integer numDirIncorrectas) { this.numDirIncorrectas = numDirIncorrectas; }

    public Integer getNumAusentes() {
        return numAusentes;
    }

    public void setNumAusentes(Integer numAusentes) {
        this.numAusentes = numAusentes;
    }

    public Integer getNumDesconocidos() {
        return numDesconocidos;
    }

    public void setNumDesconocidos(Integer numDesconocidos) { this.numDesconocidos = numDesconocidos; }

    public Integer getNumFallecidos() {
        return numFallecidos;
    }

    public void setNumFallecidos(Integer numFallecidos) {
        this.numFallecidos = numFallecidos;
    }

    public Integer getNumRehusados() {
        return numRehusados;
    }

    public void setNumRehusados(Integer numRehusados) {
        this.numRehusados = numRehusados;
    }

    public Integer getNumNadieSeHaceCargo() {
        return numNadieSeHaceCargo;
    }

    public void setNumNadieSeHaceCargo(Integer numNadieSeHaceCargo) { this.numNadieSeHaceCargo = numNadieSeHaceCargo; }

    public Integer getNumAusentesPendientes() { return numAusentesPendientes; }

    public void setNumAusentesPendientes(Integer numAusentesPendientes) { this.numAusentesPendientes = numAusentesPendientes; }

    public Integer getNumNadieSeHaceCargoPendientes() { return numNadieSeHaceCargoPendientes; }

    public void setNumNadieSeHaceCargoPendientes(Integer numNadieSeHaceCargoPendientes) { this.numNadieSeHaceCargoPendientes = numNadieSeHaceCargoPendientes; }

    public Integer getTotNotifPendientesSegundoHoy() { return totNotifPendientesSegundoHoy; }

    public void setTotNotifPendientesSegundoHoy(Integer totNotifPendientesSegundoHoy) { this.totNotifPendientesSegundoHoy = totNotifPendientesSegundoHoy; }

    public Integer getTotNotifPendientesSegundoOtroDia() { return totNotifPendientesSegundoOtroDia; }

    public void setTotNotifPendientesSegundoOtroDia(Integer totNotifPendientesSegundoOtroDia) { this.totNotifPendientesSegundoOtroDia = totNotifPendientesSegundoOtroDia; }

}
