package es.correointeligente.cipostal.cimobile.Util;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.Model.Resultado;


public class Util {

    final static List<String> tamanyosMemoria = Arrays.asList("Bytes", "KB", "MB", "GB", "TB");

    public static List<Resultado> crearResultadosPorDefecto(Context context) {
        List<Resultado> listaResultados = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);

        dbHelper.addResultado(new Resultado("01", "Notificado", 1));
        dbHelper.addResultado(new Resultado("02", "Dirección Incorrecta", 1));
        dbHelper.addResultado(new Resultado("03", "Ausente", 0));
        dbHelper.addResultado(new Resultado("04", "Desconocido", 1));
        dbHelper.addResultado(new Resultado("05", "Fallecido", 1));
        dbHelper.addResultado(new Resultado("06", "Rehusado", 1));
        dbHelper.addResultado(new Resultado("07", "Nadie se hace cargo", 1));

        return dbHelper.getAllResultados();
    }

    public static List<Notificador> obtenerNotificadores() {
        List<Notificador> listaNotificadores = new ArrayList<>();
        Notificador notificador1 = new Notificador("jorzaldo", "Jorge Zaldivar Donato", "Valencia");
        Notificador notificador2 = new Notificador("juanvi", "Juan Vicente Martinez", "Paterna");

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
