package es.correointeligente.cipostal.cimobile.Holders;

public class FicheroViewHolder {
    private String nombreFichero;
    private String tamanyo;
    private String fecha;
    private String path;


    public FicheroViewHolder(String nombreFichero, String tamanyo, String fecha, String path) {
        this.nombreFichero = nombreFichero;
        this.tamanyo = tamanyo;
        this.fecha = fecha;
        this.path = path;
    }

    public String getNombreFichero() {
        return nombreFichero;
    }

    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    public String getTamanyo() {
        return tamanyo;
    }

    public void setTamanyo(String tamanyo) {
        this.tamanyo = tamanyo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int compareTo(FicheroViewHolder o) {
        if (this.nombreFichero != null) {
            return this.nombreFichero.toLowerCase().compareTo(o.getNombreFichero().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
