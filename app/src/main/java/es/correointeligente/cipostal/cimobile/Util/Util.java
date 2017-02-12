package es.correointeligente.cipostal.cimobile.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Notificador;


public class Util {
    public final static int REQUEST_CODE_BARCODE_SCAN = 0;
    public final static int REQUEST_CODE_NOTIFICATION_RESULT = 1;
    public final static int REQUEST_CODE_NOTIFICATION_DELETE_RESULT = 2;
    final static List<String> tamanyosMemoria = Arrays.asList("Bytes", "KB", "MB", "GB", "TB");

    // CONSTANTES CODIGOS RESULTADOS
    public final static String RESULTADO_ENTREGADO           = "01";
    public final static String RESULTADO_DIR_INCORRECTA      = "02";
    public final static String RESULTADO_AUSENTE             = "31";
    public final static String RESULTADO_DESCONOCIDO         = "04";
    public final static String RESULTADO_FALLECIDO           = "05";
    public final static String RESULTADO_REHUSADO            = "06";
    public final static String RESULTADO_NADIE_SE_HACE_CARGO = "07";
    public final static String RESULTADO_ENTREGADO_OFICINA   = "08";

    public static List<Notificador> obtenerNotificadores() {
        List<Notificador> listaNotificadores = new ArrayList<>();
        Notificador notificador1 = new Notificador("A1", "Jorge Zaldivar Donato", "Valencia");
        Notificador notificador2 = new Notificador("A2", "Juan Vicente Martinez", "Paterna");

        listaNotificadores.add(notificador1);
        listaNotificadores.add(notificador2);

        return listaNotificadores;
    }

    public static String obtenerTamanyoFicheroString(long bytes) {
        String result;
        if (bytes == 0) {
            result = "0 Bytes";
        }
        Double valor = (Math.floor(Math.log(bytes) / Math.log(1024)));
        Long nuevoTamanyo = Math.round((bytes / Math.pow(1024, valor.intValue())));
        result = nuevoTamanyo.toString() + " " + tamanyosMemoria.get(valor.intValue());

        return result;
    }

}
