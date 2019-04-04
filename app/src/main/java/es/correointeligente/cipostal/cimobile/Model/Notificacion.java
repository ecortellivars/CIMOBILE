package es.correointeligente.cipostal.cimobile.Model;

public class Notificacion {

    private Integer id;
    private String referencia;
    private String referenciaSCB;
    private String nombre;
    private String direccion;
    private String poblacion;
    private String codigoPostal;
    private String resultado1;
    private String descResultado1;
    private String fechaHoraRes1;
    private String notificadorRes1;
    private String firmaNotificadorRes1;
    private String longitudRes1;
    private String latitudRes1;
    private String observacionesRes1;
    private String resultado2;
    private String descResultado2;
    private String fechaHoraRes2;
    private String notificadorRes2;
    private String firmaNotificadorRes2;
    private String longitudRes2;
    private String latitudRes2;
    private String observacionesRes2;
    private String tipoDocReceptor;
    private String numDocReceptor;
    private String nombreReceptor;
    private String firmaReceptor;
    private String nombreFichero;
    private Boolean marcada;
    private String timestampMarcada;
    // Nos indica que existe un primer intento pero requiere un segundo para CERRAR la notifacion
    private Boolean segundoIntento;
    private Boolean esLista;
    private Boolean esCertificado;
    private String fotoAcuseRes1;
    private String fotoAcuseRes2;
    private Boolean hayPrimerResultado;
    private Boolean haySegundoResultado;

    // Variables de visualizacion
    private Integer backgroundColor;

    public Notificacion() {
    }

    public Notificacion(Integer id, String referencia, String nombre, String direccion, String poblacion,
                        String codigoPostal, String nombreFichero, Boolean marcada, Boolean segundoIntento,
                        Boolean esCertificado, Integer backgroundColor, String referenciaSCB, String fotoAcuse, Boolean esLista) {
        this.id = id;
        this.referencia = referencia;
        this.referenciaSCB = referenciaSCB;
        this.nombre = nombre;
        this.direccion = direccion;
        this.poblacion = poblacion;
        this.codigoPostal = codigoPostal;
        this.nombreFichero = nombreFichero;
        this.marcada = marcada;
        this.segundoIntento = segundoIntento;
        this.esLista = esLista;
        this.backgroundColor = backgroundColor;
        this.fotoAcuseRes1 = fotoAcuseRes1;
        this.fotoAcuseRes2 = fotoAcuseRes2;
        this.esCertificado = esCertificado;
    }

    public Boolean getHayPrimerResultado() {
        return hayPrimerResultado;
    }

    public void setHayPrimerResultado(Boolean hayPrimerResultado) {this.hayPrimerResultado = hayPrimerResultado;}

    public Boolean getHaySegundoResultado() {
        return haySegundoResultado;
    }

    public void setHaySegundoResultado(Boolean haySegundoResultado) {this.haySegundoResultado = haySegundoResultado;}

    public Boolean getEsLista() {return esLista;}

    public void setEsLista(Boolean esLista) {this.esLista = esLista;}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescResultado1() {
        return descResultado1;
    }

    public void setDescResultado1(String descResultado1) {
        this.descResultado1 = descResultado1;
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

    public void setNotificadorRes1(String notificadorRes1) { this.notificadorRes1 = notificadorRes1; }

    public String getFirmaNotificadorRes1() {
        return firmaNotificadorRes1;
    }

    public void setFirmaNotificadorRes1(String firmaNotificadorRes1) { this.firmaNotificadorRes1 = firmaNotificadorRes1; }

    public String getLongitudRes1() {
        return longitudRes1;
    }

    public void setLongitudRes1(String longitudRes1) {
        this.longitudRes1 = longitudRes1;
    }

    public String getLatitudRes1() {
        return latitudRes1;
    }

    public void setLatitudRes1(String latitudRes1) {
        this.latitudRes1 = latitudRes1;
    }

    public String getResultado2() {
        return resultado2;
    }

    public void setResultado2(String resultado2) {
        this.resultado2 = resultado2;
    }

    public String getDescResultado2() {
        return descResultado2;
    }

    public void setDescResultado2(String descResultado2) {
        this.descResultado2 = descResultado2;
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

    public void setNotificadorRes2(String notificadorRes2) { this.notificadorRes2 = notificadorRes2; }

    public String getFirmaNotificadorRes2() {
        return firmaNotificadorRes2;
    }

    public void setFirmaNotificadorRes2(String firmaNotificadorRes2) { this.firmaNotificadorRes2 = firmaNotificadorRes2; }

    public String getLongitudRes2() {
        return longitudRes2;
    }

    public void setLongitudRes2(String longitudRes2) {
        this.longitudRes2 = longitudRes2;
    }

    public String getLatitudRes2() {
        return latitudRes2;
    }

    public void setLatitudRes2(String latitudRes2) {
        this.latitudRes2 = latitudRes2;
    }

    public String getTipoDocReceptor() {
        return tipoDocReceptor;
    }

    public void setTipoDocReceptor(String tipoDocReceptor) { this.tipoDocReceptor = tipoDocReceptor; }

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

    public String getTimestampMarcada() {
        return timestampMarcada;
    }

    public void setTimestampMarcada(String timestampMarcada) { this.timestampMarcada = timestampMarcada; }

    public Boolean getSegundoIntento() {
        return segundoIntento;
    }

    public void setSegundoIntento(Boolean segundoIntento) {
        this.segundoIntento = segundoIntento;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) { this.backgroundColor = backgroundColor; }

    public String getObservacionesRes1() {
        return observacionesRes1;
    }

    public void setObservacionesRes1(String observacionesRes1) { this.observacionesRes1 = observacionesRes1; }

    public String getObservacionesRes2() {
        return observacionesRes2;
    }

    public void setObservacionesRes2(String observacionesRes2) { this.observacionesRes2 = observacionesRes2; }

    public String getReferenciaSCB() {
        return referenciaSCB;
    }

    public void setReferenciaSCB(String referenciaSCB) {
        this.referenciaSCB = referenciaSCB;
    }

    public String getFotoAcuseRes1() {
        return fotoAcuseRes1;
    }

    public void setFotoAcuseRes1(String fotoAcuse) {
        this.fotoAcuseRes1 = fotoAcuse;
    }

    public String getFotoAcuseRes2() {
        return fotoAcuseRes2;
    }

    public void setFotoAcuseRes2(String fotoAcuse) {
        this.fotoAcuseRes2 = fotoAcuse;
    }

    public Boolean getEsCertificado() {return esCertificado;}

    public void setEsCertificado(Boolean esCertificado) {this.esCertificado = esCertificado;}


}
