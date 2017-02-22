package es.correointeligente.cipostal.cimobile.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.R;


public class Util {

    // CONSTANTES NOMBRES BASE FICHEROS
    public final static String NOMBRE_FICHERO_ZIP = "notificaciones";
    public final static String NOMBRE_FICHERO_CSV = "notificaciones";
    public final static String NOMBRE_FICHERO_SEGUNDO_INTENTO = "segundo_intento";

    // CONSTANTES RUTAS DE FICHEROS
    public final static String DEFAULT_EXTERNAL_DIRECTORY_APP = "CIMobile";
    public final static String EXTERNAL_DIRECTORY_XML = "XML";
    public final static String EXTERNAL_DIRECTORY_SELLO_TIEMPO = "TS";
    public final static String EXTERNAL_DIRECTORY_FIRMAS_RECEPTOR = "FIRMAS_RECEPTOR";
    public final static String EXTERNAL_DIRECTORY_FIRMA_NOTIFICADOR = "FIRMAS_NOTIFICADOR";

    // CONSTANTES DE REQUESTS DE LOS ACTIVITY RESULTS
    public final static int REQUEST_CODE_BARCODE_SCAN = 0;
    public final static int REQUEST_CODE_NOTIFICATION_RESULT = 1;
    public final static int REQUEST_CODE_NOTIFICATION_DELETE_RESULT = 2;

    // ARRAY CON LOS TAMANYOS DE MEMORIA
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

    // CONSTANTES FICHEROS DE PREFERENCIAS
    public final static String FICHERO_PREFERENCIAS_SESION = "sesion";
    public final static String FICHERO_PREFERENCIAS_APP = "preferencias";

    // CONSTANTES CLAVES PREFERENCIAS
    public final static String CLAVE_SESION_NOTIFICADOR = "notificador";
    public final static String CLAVE_SESION_DELEGACION = "delegacion";
    public final static String CLAVE_SESION_COD_NOTIFICADOR = "codigoNotificador";

    public final static String CLAVE_PREFERENCIAS_FTP_IP = "ftpIp";
    public final static String CLAVE_PREFERENCIAS_FTP_PUERTO = "ftpPuerto";
    public final static String CLAVE_PREFERENCIAS_FTP_USER = "ftpUser";
    public final static String CLAVE_PREFERENCIAS_FTP_PASSWORD = "ftpPassword";
    public final static String CLAVE_PREFERENCIAS_FTP_TIMEOUT = "ftpTimeOut";
    public final static String CLAVE_PREFERENCIAS_FTP_CARPETA_SICERS = "ftpCarpetaSicers";
    public final static String CLAVE_PREFERENCIAS_TSA_URL = "tsaURL";
    public final static String CLAVE_PREFERENCIAS_TSA_USER = "tsaUser";
    public final static String CLAVE_PREFERENCIAS_TSA_PASSWORD = "tsaPass";

    public final static String CLAVE_PREFERENCIAS_WS_URL = "wsURL";
    public final static String CLAVE_PREFERENCIAS_WS_PUERTO = "wsPuerto";

    public final static String CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS = "sigVisitaDias";
    public final static String CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS = "sigVisitaHoras";



    /**
     * Obtiene una configuración de la aplicación por defecto
     */
    public static void cargarConfiguracionAplicacionPorDefecto(Context context) {
        // Preferencias por defecto
        SharedPreferences sp = context.getSharedPreferences(Util.FICHERO_PREFERENCIAS_APP, context.MODE_PRIVATE);
        if (!sp.contains(Util.CLAVE_PREFERENCIAS_FTP_IP)) {
            // Si no contiene la preferencia de la ip del FTP, se cargan todas

            SharedPreferences.Editor e = sp.edit();
            // Preferencias FTP
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_IP, "192.168.0.105");//46.17.141.94
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PUERTO, "23"); //1984
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_USER, "jorge");//valencia
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PASSWORD, "abc123.");//9ca174324c
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_TIMEOUT, "10000");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_SICERS, "/SICERS");

            // Preferncias TSA
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_URL, "http://tss.accv.es:8318/tsa"); // http://tss.accv.es:8318/tsaup
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_USER, ""); //cipostaluser
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_PASSWORD, ""); //8ttErr32

            // Preferencias WS
            e.putString(Util.CLAVE_PREFERENCIAS_WS_URL, "");//46.17.141.94
            e.putString(Util.CLAVE_PREFERENCIAS_WS_PUERTO, ""); //1984

            // Preferencias Siguiente visita
            e.putString(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS, "3");
            e.putString(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS, "3");

            e.commit();

        }
    }

    public static String obtenerValorPreferencia(String clave, Context context) {
        String valor = "";
        SharedPreferences sp = context.getSharedPreferences(Util.FICHERO_PREFERENCIAS_APP, context.MODE_PRIVATE);
        if (sp.contains(clave)) {
            valor = sp.getString(clave, "");
        }
        return valor;
    }

    /**
     * Obtiene la ruta del directorio de la APP
     * @return String
     */
    public static String obtenerRutaAPP() {
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+DEFAULT_EXTERNAL_DIRECTORY_APP);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se alojan las imagenes con las las firmas de los receptores
     * @return String
     */
    public static String obtenerRutaFirmasReceptor() {
        File file = new File(obtenerRutaAPP()+File.separator+EXTERNAL_DIRECTORY_FIRMAS_RECEPTOR);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se alojan los XML con la informacion de las notificaciones
     * @return String
     */
    public static String obtenerRutaXML() {
        File file = new File(obtenerRutaAPP()+File.separator+EXTERNAL_DIRECTORY_XML);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se alojan los sellos de tiempo
     * @return String
     */
    public static String obtenerRutaSelloDeTiempo() {
        File file = new File(obtenerRutaAPP()+File.separator+EXTERNAL_DIRECTORY_SELLO_TIEMPO);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se aloja la firma del notificador
     * @return String
     */
    public static String obtenerRutaFirmaNotificador() {
        File file = new File(obtenerRutaAPP()+File.separator+EXTERNAL_DIRECTORY_FIRMA_NOTIFICADOR);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }


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

    /**
     * Guarda en disco un array de bytes.
     * @param nombreFichero Fichero donde se guardará el contenido
     * @param contenido Contenido a guardar
     * @throws Exception No se puede escribir
     */
    public static void guardarFicheroSelloTiempo(String nombreFichero, byte[] contenido) throws IOException {
        // Los guardamos a disco.
        FileUtils.writeByteArrayToFile(new File(obtenerRutaSelloDeTiempo(), nombreFichero), contenido);
    }

    /**
     * Crear un fichero XML a partir de una notificacion dada
     * @param notificacion
     * @return File
     */
    public static File NotificacionToXML(Notificacion notificacion, Context context) throws CiMobileException {
        File xmlFile = null;
        try {
            // Se Determina si viene del primer o del segundo resultado
            Date date = null;
            String horaString = null;
            String fechaString = null;
            String resultadoString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            DateFormat dfHora = new SimpleDateFormat("HH:mm");
            DateFormat dfDia = new SimpleDateFormat("dd/MM/yyyy");
            String latitudString = null;
            String longitudString = null;
            String observacionesString = null;
            String notificadorString = null;

            if(notificacion.getResultado2() != null && !notificacion.getResultado2().trim().isEmpty()) {
                resultadoString = notificacion.getResultado2();
                date = formatter.parse(notificacion.getFechaHoraRes2());
                latitudString = notificacion.getLatitudRes2();
                longitudString = notificacion.getLongitudRes2();
                observacionesString = notificacion.getObservacionesRes2();
                notificadorString = notificacion.getNotificadorRes2();
            } else {
                resultadoString = notificacion.getResultado1();
                date = formatter.parse(notificacion.getFechaHoraRes1());
                latitudString = notificacion.getLatitudRes1();
                longitudString = notificacion.getLongitudRes1();
                observacionesString = notificacion.getObservacionesRes1();
                notificadorString = notificacion.getNotificadorRes1();
            }

            horaString = dfHora.format(date);
            fechaString = dfDia.format(date);

            // Se empieza a generar el arbol XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("notificacion");
            doc.appendChild(rootElement);

            Element referencia = doc.createElement("referencia");
            referencia.appendChild(doc.createTextNode(notificacion.getReferencia()));
            rootElement.appendChild(referencia);

            Element referenciaSCB = doc.createElement("referenciaSCB");
            referenciaSCB.appendChild(doc.createTextNode(notificacion.getReferenciaSCB()));
            rootElement.appendChild(referenciaSCB);

            Element destinatario = doc.createElement("destinatario");
            destinatario.appendChild(doc.createTextNode(notificacion.getNombre()));
            rootElement.appendChild(destinatario);

            Element dirDestinatario = doc.createElement("dirDestinatario");
            dirDestinatario.appendChild(doc.createTextNode(notificacion.getDireccion()));
            rootElement.appendChild(dirDestinatario);

            Element resultado = doc.createElement("resultado");
            resultado.appendChild(doc.createTextNode(resultadoString));
            rootElement.appendChild(resultado);

            Element fecha = doc.createElement("fecha");
            fecha.appendChild(doc.createTextNode(fechaString));
            rootElement.appendChild(fecha);

            Element hora = doc.createElement("hora");
            hora.appendChild(doc.createTextNode(horaString));
            rootElement.appendChild(hora);

            Element latitud = doc.createElement("latitud");
            latitud.appendChild(doc.createTextNode(latitudString));
            rootElement.appendChild(latitud);

            Element longitud = doc.createElement("longitud");
            longitud.appendChild(doc.createTextNode(longitudString));
            rootElement.appendChild(longitud);

            if(observacionesString != null && !observacionesString.trim().isEmpty()) {
                Element observaciones = doc.createElement("observaciones");
                observaciones.appendChild(doc.createTextNode(observacionesString));
                rootElement.appendChild(observaciones);
            }

            Element notificador = doc.createElement("notificador");
            notificador.appendChild(doc.createTextNode(notificadorString));
            rootElement.appendChild(notificador);

            if(notificacion.getNumDocReceptor() != null && !notificacion.getNumDocReceptor().trim().isEmpty()) {
                Element numDocReceptor = doc.createElement("numDocReceptor");
                numDocReceptor.appendChild(doc.createTextNode(notificacion.getNumDocReceptor()));
                rootElement.appendChild(numDocReceptor);

                Element nombreReceptor = doc.createElement("nombreReceptor");
                nombreReceptor.appendChild(doc.createTextNode(notificacion.getNombreReceptor()));
                rootElement.appendChild(nombreReceptor);

                FileInputStream fis = new FileInputStream(notificacion.getFirmaReceptor());
                byte[] filedata = IOUtils.toByteArray(fis);
                String encodedImage = Base64.encodeToString(filedata, Base64.NO_WRAP);
                Element firmaReceptor = doc.createElement("firmaReceptor");
                firmaReceptor.appendChild(doc.createTextNode(encodedImage));
                rootElement.appendChild(firmaReceptor);
            }

            xmlFile = new File(obtenerRutaXML(), notificacion.getReferencia()+".xml");
            if(!xmlFile.exists()) {
                xmlFile.createNewFile();
            }
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult target = new StreamResult(xmlFile);
            transformer.transform(source, target);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        if(xmlFile == null || xmlFile.length() == 0) {
            throw new CiMobileException(context.getString(R.string.error_generar_XML));
        }

        return xmlFile;
    }

    public static File comprimirZIP(String codigoNotificador) {
        DateFormat dfDia = new SimpleDateFormat("ddMMyyyy");
        String nombreFichero = NOMBRE_FICHERO_ZIP+"_"+codigoNotificador+"_"+dfDia.format(Calendar.getInstance().getTime())+".zip";

        File directorioAGenerarZIP = new File(obtenerRutaAPP());
        File ficheroZIP = new File(Environment.getExternalStorageDirectory(),nombreFichero);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(ficheroZIP))){
            anyadirFicherosRecursivamente(directorioAGenerarZIP, zos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ficheroZIP;
    }

    /**
     * Va recorriendo el directorio para ir incluyendo los ficheros al zip,
     * si encuentra un directorio, se llama de forma recursiva con la nueva ruta del directorio
     * @param file
     * @param zos
     * @throws IOException
     */
    private static void anyadirFicherosRecursivamente(File file, ZipOutputStream zos) throws IOException {

        if (file.isDirectory()) { // Si es un directorio, se llama de forma recursiva
            //Crea un array con todos los ficheros y directorios del path que se ha pasado
            String[] fileNames = file.list();
            if (fileNames != null) {
                for (int i=0; i<fileNames.length; i++){
                    anyadirFicherosRecursivamente(new File(file, fileNames[i]), zos);
                }
            }

        } else { // Si es un fichero, se añade al ZIP
            byte[] buf = new byte[1024];
            int len;
            ZipEntry zipEntry = new ZipEntry(file.toString());
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fin);
            zos.putNextEntry(zipEntry);
            while ((len = in.read(buf)) >= 0) {
                zos.write(buf, 0, len);
            }

            in.close();
            zos.closeEntry();
        }
    }

    public static Boolean borrarFicherosAplicacion() {
        Boolean eliminado = false;

        try {
            File directorio = new File(obtenerRutaAPP());
            FileUtils.deleteDirectory(directorio);
            eliminado = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  eliminado;
    }

}
