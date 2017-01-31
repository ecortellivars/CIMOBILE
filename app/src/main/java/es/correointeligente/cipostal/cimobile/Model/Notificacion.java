package es.correointeligente.cipostal.cimobile.Model;

public class Notificacion {

    private String referencia;
    private String nombre;
    private String direccion;
    private String poblacion;
    private String codigoPostal;
    private String resultado1;
    private String fechaHoraRes1;
    private String notificadorRes1;
    private String firmaNotificadorRes1;
    private String resultado2;
    private String fechaHoraRes2;
    private String notificadorRes2;
    private String firmaNotificadorRes2;
    private String tipoDocReceptor;
    private String numDocReceptor;
    private String nombreReceptor;
    private String firmaReceptor;
    private String longitud;
    private String latitud;
    private String nombreFichero;
    private Boolean marcada;
    private Boolean segundoIntento;

    public Notificacion() {
    }

    public Notificacion(String referencia, String nombre, String direccion, String poblacion,
                        String codigoPostal, String nombreFichero, Boolean marcada, Boolean segundoIntento) {
        this.referencia = referencia;
        this.nombre = nombre;
        this.direccion = direccion;
        this.poblacion = poblacion;
        this.codigoPostal = codigoPostal;
        this.nombreFichero = nombreFichero;
        this.marcada = marcada;
        this.segundoIntento = segundoIntento;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getResultado1() {
        return resultado1;
    }

    public void setResultado1(String resultado1) {
        this.resultado1 = resultado1;
    }

    public String getFechaHoraRes1() {
        return fechaHoraRes1;
    }

    public void setFechaHoraRes1(String fechaHoraRes1) {
        this.fechaHoraRes1 = fechaHoraRes1;
    }

    public String getNotificadorRes1() {
        return notificadorRes1;
    }

    public void setNotificadorRes1(String notificadorRes1) {
        this.notificadorRes1 = notificadorRes1;
    }

    public String getFirmaNotificadorRes1() {
        return firmaNotificadorRes1;
    }

    public void setFirmaNotificadorRes1(String firmaNotificadorRes1) {
        this.firmaNotificadorRes1 = firmaNotificadorRes1;
    }

    public String getResultado2() {
        return resultado2;
    }

    public void setResultado2(String resultado2) {
        this.resultado2 = resultado2;
    }

    public String getFechaHoraRes2() {
        return fechaHoraRes2;
    }

    public void setFechaHoraRes2(String fechaHoraRes2) {
        this.fechaHoraRes2 = fechaHoraRes2;
    }

    public String getNotificadorRes2() {
        return notificadorRes2;
    }

    public void setNotificadorRes2(String notificadorRes2) {
        this.notificadorRes2 = notificadorRes2;
    }

    public String getFirmaNotificadorRes2() {
        return firmaNotificadorRes2;
    }

    public void setFirmaNotificadorRes2(String firmaNotificadorRes2) {
        this.firmaNotificadorRes2 = firmaNotificadorRes2;
    }

    public String getTipoDocReceptor() {
        return tipoDocReceptor;
    }

    public void setTipoDocReceptor(String tipoDocReceptor) {
        this.tipoDocReceptor = tipoDocReceptor;
    }

    public String getNumDocReceptor() {
        return numDocReceptor;
    }

    public void setNumDocReceptor(String numDocReceptor) {
        this.numDocReceptor = numDocReceptor;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public String getFirmaReceptor() {
        return firmaReceptor;
    }

    public void setFirmaReceptor(String firmaReceptor) {
        this.firmaReceptor = firmaReceptor;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getNombreFichero() {
        return nombreFichero;
    }

    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    public Boolean getMarcada() {
        return marcada;
    }

    public void setMarcada(Boolean marcada) {
        this.marcada = marcada;
    }

    public Boolean getSegundoIntento() {
        return segundoIntento;
    }

    public void setSegundoIntento(Boolean segundoIntento) {
        this.segundoIntento = segundoIntento;
    }
}
