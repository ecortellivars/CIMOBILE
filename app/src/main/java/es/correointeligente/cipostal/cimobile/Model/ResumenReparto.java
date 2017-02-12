package es.correointeligente.cipostal.cimobile.Model;

public class ResumenReparto {
    private Integer totFicheros, totNotificaciones, totNotifGestionadas, totNotifMarcadas, numEntregados,
                    numDirIncorrectas, numAusentes, numDesconocidos, numFallecidos, numRehusados, numNadieSeHaceCargo;

    public ResumenReparto() {
    }

    public ResumenReparto(int totFicheros, int totNotificaciones,
                          int totNotifGestionadas, int totNotifMarcadas, int numEntregados,
                          int numDirIncorrectas, int numAusentes, int numDesconocidos,
                          int numFallecidos, int numRehusados, int numNadieSeHaceCargo) {
        this.totFicheros = totFicheros;
        this.totNotificaciones = totNotificaciones;
        this.totNotifGestionadas = totNotifGestionadas;
        this.totNotifMarcadas = totNotifMarcadas;
        this.numEntregados = numEntregados;
        this.numDirIncorrectas = numDirIncorrectas;
        this.numAusentes = numAusentes;
        this.numDesconocidos = numDesconocidos;
        this.numFallecidos = numFallecidos;
        this.numRehusados = numRehusados;
        this.numNadieSeHaceCargo = numNadieSeHaceCargo;
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

    public void setTotNotificaciones(Integer totNotificaciones) {
        this.totNotificaciones = totNotificaciones;
    }

    public Integer getTotNotifGestionadas() {
        return totNotifGestionadas;
    }

    public void setTotNotifGestionadas(Integer totNotifGestionadas) {
        this.totNotifGestionadas = totNotifGestionadas;
    }

    public Integer getTotNotifMarcadas() {
        return totNotifMarcadas;
    }

    public void setTotNotifMarcadas(Integer totNotifMarcadas) {
        this.totNotifMarcadas = totNotifMarcadas;
    }

    public Integer getNumEntregados() {
        return numEntregados;
    }

    public void setNumEntregados(Integer numEntregados) {
        this.numEntregados = numEntregados;
    }

    public Integer getNumDirIncorrectas() {
        return numDirIncorrectas;
    }

    public void setNumDirIncorrectas(Integer numDirIncorrectas) {
        this.numDirIncorrectas = numDirIncorrectas;
    }

    public Integer getNumAusentes() {
        return numAusentes;
    }

    public void setNumAusentes(Integer numAusentes) {
        this.numAusentes = numAusentes;
    }

    public Integer getNumDesconocidos() {
        return numDesconocidos;
    }

    public void setNumDesconocidos(Integer numDesconocidos) {
        this.numDesconocidos = numDesconocidos;
    }

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

    public void setNumNadieSeHaceCargo(Integer numNadieSeHaceCargo) {
        this.numNadieSeHaceCargo = numNadieSeHaceCargo;
    }
}
