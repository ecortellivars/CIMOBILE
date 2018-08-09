package es.correointeligente.cipostal.cimobile.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import es.correointeligente.cipostal.cimobile.R;


public class Util {

    // TIPOS DE DOCUMENTO
    public final static String TIPO_DOCUMENTO_NIF = "NIF";
    public final static String TIPO_DOCUMENTO_NIE = "NIE";
    public final static String TIPO_DOCUMENTO_CIF = "CIF";
    public final static String TIPO_DOCUMENTO_OTRO = "OTRO";

    // PATRON PARA LA VALIDACIÓN DEL CIF
    private static final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
    private static final String CONTROL_SOLO_NUMEROS = "ABEH"; // Sólo admiten números como caracter de control
    private static final String CONTROL_SOLO_LETRAS = "KPQS"; // Sólo admiten letras como caracter de control
    private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI"; // Conversión de dígito a letra de control.

    // CONSTANTES NOMBRES BASE FICHEROS
    public final static String NOMBRE_FICHERO_SEGUNDO_INTENTO = "segundo_intento";

    // CONSTANTES RUTAS DE FICHEROS
    public final static String DEFAULT_EXTERNAL_DIRECTORY_APP = "CIMobile";
    public final static String EXTERNAL_DIRECTORY_XML = "XML";
    public final static String EXTERNAL_DIRECTORY_SELLO_TIEMPO = "TS";
    public final static String EXTERNAL_DIRECTORY_FIRMAS_RECEPTOR = "FIRMAS_RECEPTOR";
    public final static String EXTERNAL_DIRECTORY_FIRMA_NOTIFICADOR = "FIRMAS_NOTIFICADOR";
    public final static String EXTERNAL_DIRECTORY_UPDATES_APP = "UPDATES_APP";
    public final static String EXTERNAL_DIRECTORY_FOTO_ACUSE = "FOTOS_ACUSE";

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
    // Para uso exclusivo de control del trabajo del notificador
    public final static String RESULTADO_ENTREGADO_SIN_FIRMA = "01";
    public final static String DESCRIPCION_ENTREGADO_SIN_FIRMA = "Notificado sin firma";
    public final static String DESCRIPCION_ENTREGADO_CON_FIRMA = "Notificado";

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
    public final static String CLAVE_PREFERENCIAS_FTP_CARPETA_BASE = "ftpCarpetaBase";
    public final static String CLAVE_PREFERENCIAS_FTP_UPDATES_CARPETA = "updatesCarpeta";
    public final static String CLAVE_PREFERENCIAS_FTP_UPDATES_FICHERO = "updatesFichero";

    public final static String CLAVE_PREFERENCIAS_TSA_ACTIVO = "tsaActivo";
    public final static String CLAVE_PREFERENCIAS_TSA_URL = "tsaURL";
    public final static String CLAVE_PREFERENCIAS_TSA_USER = "tsaUser";
    public final static String CLAVE_PREFERENCIAS_TSA_PASSWORD = "tsaPass";

    public final static String CLAVE_PREFERENCIAS_WS_NAMESPACE = "wsNamespace";
    public final static String CLAVE_PREFERENCIAS_WS_METHOD_NAME = "wsMetodo";
    public final static String CLAVE_PREFERENCIAS_WS_METHOD_URL = "wsURL";

    public final static String CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS = "sigVisitaDias";
    public final static String CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS = "sigVisitaHoras";

    public final static String CLAVE_PREFERENCIAS_APP_DE_OFICINA = "usarAPPEnOficina";



    /**
     * Obtiene una configuración de la aplicación por defecto
     */
    public static void cargarConfiguracionAplicacionPorDefecto(Context context, String delegacion) {
        // Preferencias por defecto
        SharedPreferences sp = context.getSharedPreferences(Util.FICHERO_PREFERENCIAS_APP, context.MODE_PRIVATE);
        // Si no contiene la preferencia de la ip del FTP, se cargan todas
        if (!sp.contains(Util.CLAVE_PREFERENCIAS_FTP_IP)) {

            SharedPreferences.Editor e = sp.edit();

            // Preferencias FTP
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_IP, "77.224.48.164");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PUERTO, "1984");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_USER, "andalucia");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PASSWORD, "andalucia");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_TIMEOUT, "10000");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_SICERS, "/SICER");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_BASE, "/home");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_CARPETA, "/ultimaversion/ultimaversion/ULTIMAVERSION/CIMOBILE");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_FICHERO, "/version.txt");

            // Preferencias FTP CIPOSTAL (1and1) para pruebas
            /**e.putString(Util.CLAVE_PREFERENCIAS_FTP_IP, "home557660407.1and1-data.host");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PUERTO, "22");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_USER, "u79475687");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_PASSWORD, "abc123.");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_TIMEOUT, "10000");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_SICERS, "/SICER");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_BASE, "/ENRIQUE");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_CARPETA, "/ULTIMAVERSION/CIMOBILE");
            e.putString(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_FICHERO, "/version.txt"); */

            // Preferncias TSA
            e.putBoolean(Util.CLAVE_PREFERENCIAS_TSA_ACTIVO, true);
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_URL, "http://tss.accv.es:8318/tsaup"); // http://tss.accv.es:8318/tsaup
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_USER, "cipostaluser"); //cipostaluser
            e.putString(Util.CLAVE_PREFERENCIAS_TSA_PASSWORD, "8ttErr32"); //8ttErr32

            // Preferencias WS
            e.putString(Util.CLAVE_PREFERENCIAS_WS_NAMESPACE, "http://impl.v01.srvPostal.business.postal.sdci.es/");
            e.putString(Util.CLAVE_PREFERENCIAS_WS_METHOD_NAME, "validarLoginWS");
            e.putString(Util.CLAVE_PREFERENCIAS_WS_METHOD_URL, "http://correointeligente.es:9995/PostalService");

            // Preferencias Siguiente visita
            e.putString(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS, "3");
            e.putString(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS, "3");

            // Preferencias para el uso de la aplicación en oficina
            e.putBoolean(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, false);

            e.commit();
        }
    }

    /**
     * Metodo genérico que devuelve el valor de la preferencia independientemente de la clase
     * @param clave
     * @param context
     * @param clase
     * @param <T>
     * @return valor
     */
    public static <T> T obtenerValorPreferencia(String clave, Context context, String clase) {
        T valor = null;
        Object result = null;
        // Llamamos al fichero preferencias.xml en modo privado
        SharedPreferences sp = context.getSharedPreferences(Util.FICHERO_PREFERENCIAS_APP, context.MODE_PRIVATE);
        // Si el dato que buscamos esta en el XML
        if (sp.contains(clave)) {
            if(clase.equalsIgnoreCase(String.class.getSimpleName())) {
                result = sp.getString(clave, "");
            } else if(clase.equalsIgnoreCase(Integer.class.getSimpleName())) {
                try {
                    result = sp.getInt(clave, 0);
                }catch (ClassCastException e) {
                    // En caso de fallar por la clase (Integer cuando es string), se prueba recuperar como string y castear a int
                    result = Integer.parseInt(sp.getString(clave, ""));
                }
            } else if(clase.equalsIgnoreCase(Boolean.class.getSimpleName())) {
                result = sp.getBoolean(clave, false);
            } else if(clase.equalsIgnoreCase(Long.class.getSimpleName())) {
                result = sp.getLong(clave, 0);
            } else if(clase.equalsIgnoreCase(Float.class.getSimpleName())) {
                result = sp.getFloat(clave, 0);
            }
        }

        return valor = ((T) result);
    }

    /**
     * Obtiene la ruta del directorio de la APP
     * @return String
     */
    public static String obtenerRutaAPP() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DEFAULT_EXTERNAL_DIRECTORY_APP);
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
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_FIRMAS_RECEPTOR);
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
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_XML);
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
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_SELLO_TIEMPO);
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
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_FIRMA_NOTIFICADOR);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se aloja la foto del acuse
     * @return String
     */
    public static String obtenerRutaFotoAcuse() {
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_FOTO_ACUSE);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del directorio donde se aloja los apk con las actualizaciones
     * @return String
     */
    public static String obtenerRutaActualizaciones() {
        File file = new File(obtenerRutaAPP() + File.separator + EXTERNAL_DIRECTORY_UPDATES_APP);
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * Obtiene la ruta del FTP del SICER de la delegacion a la que pertenece el notificador
     * @param context
     * @param delegacion
     * @return
     */
    public static String obtenerRutaFtpSICER(Context context, String delegacion) {
        String ruta = "";

        //ruta = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_BASE, context, String.class.getSimpleName());
        // /valencia/VALENCIA/SICER
        ruta = File.separator + delegacion.toLowerCase() +  File.separator + delegacion.toUpperCase() + Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_SICERS, context, String.class.getSimpleName());

        return ruta;
    }

    /**
     * Obtiene la ruta del FTP de la carpeta donde se enceuntran los apks de las nuevas versiones de la APP
     * @param context
     * @return
     */
    public static String obtenerRutaFtpActualizaciones(Context context) {
        String ruta = "";

        ruta = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_CARPETA_BASE, context, String.class.getSimpleName());
        ruta = ruta + Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_CARPETA, context, String.class.getSimpleName());

        return ruta;
    }

    public static String obtenerTamanyoFicheroString(long bytes) {
        String result;
        if (bytes == 0) {
            result = "0 Bytes";
        } else {
            Double valor = (Math.floor(Math.log(bytes) / Math.log(1024)));
            Long nuevoTamanyo = Math.round((bytes / Math.pow(1024, valor.intValue())));
            result = nuevoTamanyo.toString() + " " + tamanyosMemoria.get(valor.intValue());
        }

        return result;
    }

    /**
     * Guarda en disco un array de bytes.
     * @param notificacion Fichero donde se guardará el contenido
     * @param contenido Contenido a guardar
     * @throws Exception No se puede escribir
     */
    public static void guardarFicheroSelloTiempo(Notificacion notificacion, byte[] contenido) throws IOException {
        // Los guardamos a disco.
        String nombreFichero = notificacion.getReferencia() + "_" + StringUtils.defaultIfBlank(notificacion.getReferenciaSCB(),"") + ".ts";
        FileUtils.writeByteArrayToFile(new File(obtenerRutaSelloDeTiempo(), nombreFichero), contenido);
    }

    /**
     * Crear un fichero XML a partir de una notificacion dada
     * @param notificacion
     * @param context
     * @return File
     */
    public static File NotificacionToXML(Notificacion notificacion, Context context) throws CiMobileException {
        File xmlFile = null;
        try {

            String nombeFichero = notificacion.getReferencia() + "_" + StringUtils.defaultIfBlank(notificacion.getReferenciaSCB(),"") + ".xml";

            // Se Determina si viene del primer o del segundo resultado
            Date date = null;
            String horaString = null;
            String fechaString = null;
            String resultadoString = null;
            String resultadoDescString = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            DateFormat dfHora = new SimpleDateFormat("HH:mm");
            DateFormat dfDia = new SimpleDateFormat("dd/MM/yyyy");
            String latitudString = null;
            String longitudString = null;
            String observacionesString = null;
            String notificadorString = null;
            String firmaNotificadorString = null;
            String fotoAcuseString = null;

            if(StringUtils.isNotBlank(notificacion.getResultado2())) {
                resultadoString = notificacion.getResultado2();
                resultadoDescString = notificacion.getDescResultado2();
                date = formatter.parse(notificacion.getFechaHoraRes2());
                latitudString = notificacion.getLatitudRes2();
                longitudString = notificacion.getLongitudRes2();
                observacionesString = notificacion.getObservacionesRes2();
                notificadorString = notificacion.getNotificadorRes2();
                firmaNotificadorString = notificacion.getFirmaNotificadorRes2();
                fotoAcuseString = notificacion.getFotoAcuseRes2();
            } else {
                resultadoString = notificacion.getResultado1();
                resultadoDescString = notificacion.getDescResultado1();
                date = formatter.parse(notificacion.getFechaHoraRes1());
                latitudString = notificacion.getLatitudRes1();
                longitudString = notificacion.getLongitudRes1();
                observacionesString = notificacion.getObservacionesRes1();
                notificadorString = notificacion.getNotificadorRes1();
                firmaNotificadorString = notificacion.getFirmaNotificadorRes1();
                fotoAcuseString = notificacion.getFotoAcuseRes1();
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
            resultado.appendChild(doc.createTextNode(resultadoString+" "+resultadoDescString));
            rootElement.appendChild(resultado);

            Element fecha = doc.createElement("fecha");
            fecha.appendChild(doc.createTextNode(fechaString));
            rootElement.appendChild(fecha);

            Element hora = doc.createElement("hora");
            hora.appendChild(doc.createTextNode(horaString));
            rootElement.appendChild(hora);

            if(StringUtils.isNotBlank(latitudString)) {
                Element latitud = doc.createElement("latitud");
                latitud.appendChild(doc.createTextNode(latitudString));
                rootElement.appendChild(latitud);
            }

            if(StringUtils.isNotBlank(longitudString)) {
                Element longitud = doc.createElement("longitud");
                longitud.appendChild(doc.createTextNode(longitudString));
                rootElement.appendChild(longitud);
            }

            if(StringUtils.isNotBlank(observacionesString)) {
                Element observaciones = doc.createElement("observaciones");
                observaciones.appendChild(doc.createTextNode(observacionesString));
                rootElement.appendChild(observaciones);
            }

            Element notificador = doc.createElement("notificador");
            notificador.appendChild(doc.createTextNode(notificadorString));
            rootElement.appendChild(notificador);

            FileInputStream fis = null;
            byte[] filedata = null;
            String encodedImage = null;

            // Si el resultado es ENTREGADO CON FIRMA capturamos los datos del RECEPTOR de la carta
            if(notificacion.getNumDocReceptor() != null && !notificacion.getNumDocReceptor().trim().isEmpty()) {
                Element numDocReceptor = doc.createElement("numDocReceptor");
                numDocReceptor.appendChild(doc.createTextNode(notificacion.getNumDocReceptor()));
                rootElement.appendChild(numDocReceptor);

                Element nombreReceptor = doc.createElement("nombreReceptor");
                nombreReceptor.appendChild(doc.createTextNode(notificacion.getNombreReceptor()));
                rootElement.appendChild(nombreReceptor);

                fis = new FileInputStream(notificacion.getFirmaReceptor());
                filedata = IOUtils.toByteArray(fis);
                encodedImage = Base64.encodeToString(filedata, Base64.NO_WRAP);
                Element firmaReceptor = doc.createElement("firmaReceptor");
                firmaReceptor.appendChild(doc.createTextNode(encodedImage));
                rootElement.appendChild(firmaReceptor);
                fis.close();
            }

            fis = new FileInputStream(firmaNotificadorString);
            filedata = IOUtils.toByteArray(fis);
            encodedImage = Base64.encodeToString(filedata, Base64.NO_WRAP);
            Element firmaNotificador = doc.createElement("firmaNotificador");
            firmaNotificador.appendChild(doc.createTextNode(encodedImage));
            rootElement.appendChild(firmaNotificador);
            fis.close();

            /**fis = new FileInputStream(fotoAcuseString);
            filedata = IOUtils.toByteArray(fis);
            encodedImage = Base64.encodeToString(filedata, Base64.NO_WRAP);
            Element fotoAcuse = doc.createElement("fotoAcuse");
            fotoAcuse.appendChild(doc.createTextNode(encodedImage));
            rootElement.appendChild(fotoAcuse);
            fis.close();*/

            xmlFile = new File(obtenerRutaXML(), nombeFichero);
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

    /**
     * Comprime en zip el reparto del dia
     * @param codigoNotificador
     * @param delegacion
     * @return
     */
    public static File comprimirZIP(String codigoNotificador, String delegacion) {
        DateFormat dfDia = new SimpleDateFormat("ddMMyyyy");
        String nombreFichero = delegacion + "_" + codigoNotificador + "_" + dfDia.format(Calendar.getInstance().getTime()) + ".zip";

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
     * Realiza una validación de si existe la firma del notificador fisicamente en un archivo
     * @param codigoNotificador
     * @return
     */
    public static  Boolean existeFirmaNotificador(String codigoNotificador) {
        Boolean existe = false;
        try {
            File f = new File(obtenerRutaFirmaNotificador() + File.separator + codigoNotificador + ".png");
            existe = f.exists();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return existe;
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
                for (int i = 0; i < fileNames.length; i++){
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

    /**
     * Método estático que se encarga de borrar los archivos físicos que se encuentran en el dispositivo
     * @return Boolean
     */
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

    /**
     * Metodo que guarda el fichero APK en downloads
     * @param is
     * @param nombreFichero
     * @return String
     */
    public static String guardarFicheroAPK(InputStream is, String nombreFichero) {
        String carpeta = Environment.getExternalStorageDirectory() + File.separator + EXTERNAL_DIRECTORY_UPDATES_APP;

        File file = new File(carpeta);
        if(!file.exists()) {
            file.mkdirs();
        }
        File outputFile = new File(file, nombreFichero);
        if(outputFile.exists()){
            outputFile.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile);) {
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile.getPath();
    }

    /**
     * Método estático que se encarga de validar el número de documento del receptor
     * @param numeroDocumento
     * @param tipoDocumento
     * @return Boolean
     */
    public static Boolean validarNumeroDocumento(String numeroDocumento, String tipoDocumento) {
        Boolean valido = Boolean.TRUE;
        if(tipoDocumento.equalsIgnoreCase(TIPO_DOCUMENTO_NIF) || tipoDocumento.equalsIgnoreCase(TIPO_DOCUMENTO_NIE)) {
            valido = validarNifNie(numeroDocumento, tipoDocumento);
        } else if(tipoDocumento.equalsIgnoreCase(TIPO_DOCUMENTO_CIF)) {
            valido = validarCif(numeroDocumento);
        }

        return valido;
    }

    /**
     * Método privado para la validacion del NIF o del NIE
     * @param numeroDocumento
     * @param tipoDocumento
     * @return
     */
    private static Boolean validarNifNie(String numeroDocumento, String tipoDocumento) {
        Boolean valido = Boolean.TRUE;

        //si es NIE, eliminar la x,y,z inicial para tratarlo como nif
        Boolean esNumDocNIE = false;
        if (numeroDocumento.toUpperCase().startsWith("X")) {
            numeroDocumento = numeroDocumento.substring(1);
            esNumDocNIE = true;
        } else if (numeroDocumento.toUpperCase().startsWith("Y")) {
            numeroDocumento = "1" + numeroDocumento.substring(1);
            esNumDocNIE = true;
        } else if (numeroDocumento.toUpperCase().startsWith("Z")) {
            numeroDocumento = "2" + numeroDocumento.substring(1);
            esNumDocNIE = true;
        }

        if((tipoDocumento.equalsIgnoreCase(TIPO_DOCUMENTO_NIF) && !esNumDocNIE) ||
           (tipoDocumento.equalsIgnoreCase(TIPO_DOCUMENTO_NIE) && esNumDocNIE)) {

            Pattern nifPattern = Pattern.compile("(\\d{1,8})([TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke])");
            Matcher m = nifPattern.matcher(numeroDocumento);
            if (m.matches()) {
                String letra = m.group(2);
                //Extraer letra del NIF
                String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
                int dni = Integer.parseInt(m.group(1));
                dni = dni % 23;
                String reference = letras.substring(dni, dni + 1);
                if (!reference.equalsIgnoreCase(letra)) {
                    valido = false;
                }
            } else {
                valido = false;
            }
        } else {
            valido = false;
        }

        return valido;
    }

    /**
     * Metodo para validar un CIF
     * @param numeroDocumento
     * @return Boolean
     */
    private static Boolean validarCif(String numeroDocumento) {
        Boolean valido = Boolean.TRUE;
        try {
            if (cifPattern.matcher(numeroDocumento).matches()) {
                int parA = 0;
                for (int i = 2; i < 8; i += 2) {
                    final int digito = Character.digit(numeroDocumento.charAt(i), 10);
                    if (digito < 0) {
                        valido = false;
                        break;
                    }
                    parA += digito;
                }

                if(valido) {

                    int nonB = 0;
                    for (int i = 1; i < 9; i += 2) {
                        final int digito = Character.digit(numeroDocumento.charAt(i), 10);
                        if (digito < 0) {
                            valido = false;
                            break;
                        }
                        int nn = 2 * digito;
                        if (nn > 9) {
                            nn = 1 + (nn - 10);
                        }
                        nonB += nn;
                    }

                    if(valido) {
                        final int parcialC = parA + nonB;
                        final int digitoE = parcialC % 10;
                        final int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
                        final char letraIni = numeroDocumento.charAt(0);
                        final char caracterFin = numeroDocumento.charAt(8);

                        valido = (CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && // ¿el caracter de control es válido como letra?
                                                   CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) || // ¿el carácter de control es válido como dígito?
                                                  (CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 &&
                                                   digitoD == Character.digit(caracterFin, 10));
                    }
                }

            } else {
                // No cumple el patrón de CIF
                valido = false;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }

        return valido;
    }
}
