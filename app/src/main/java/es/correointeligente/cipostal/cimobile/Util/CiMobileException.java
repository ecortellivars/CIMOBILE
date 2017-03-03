package es.correointeligente.cipostal.cimobile.Util;

public class CiMobileException extends Exception {

    private String error;

    public CiMobileException() {
    }

    public CiMobileException(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
