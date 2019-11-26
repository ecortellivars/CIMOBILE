package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.correointeligente.cipostal.cimobile.Adapters.FicheroAdapter;
import es.correointeligente.cipostal.cimobile.Holders.FicheroViewHolder;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.CiMobileException;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class CargarRepartoActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    Toolbar mToolbar;
    ListView mlistView_cargar_reparto_ficheros;
    FicheroAdapter itemsAdapter;
    DBHelper dbHelper;
    FTPHelper ftpHelper;
    Date fechaHoraRes1Date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_reparto);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        mlistView_cargar_reparto_ficheros = (ListView) findViewById(R.id.listView_cargar_reparto_ficheros);

        // Carga el layout comun de sesion actual
        this.loadLayoutCurrentSession();

        // Lanza una tarea en background para la conexión FTP
        FtpConnectionTask ftpConnectionTask = new FtpConnectionTask();
        ftpConnectionTask.execute();

    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_cargar_reparto;
    }

    // Gestión de los Iconos de la barra de herramientas
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ftpHelper.disconnect();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ftpHelper.disconnect();
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // Este metodo recoge el item seleccionado de la lista mostrada por pantalla
        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        List<Notificacion> notificaciones = dbHelper.obtenerNotificacionesGestionadas();
        if (notificaciones.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.cargar_fichero_sicer);
            builder.setMessage(R.string.no_cargar_el_fichero_sicer);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } else {
            final String nombreFicheroSeleccionado = ((FicheroViewHolder) adapterView.getItemAtPosition(position)).getNombreFichero();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.cargar_fichero_sicer);
            builder.setMessage(R.string.esta_seguro_de_cargar_el_fichero_sicer+" "+ nombreFicheroSeleccionado + "?");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Lanza la tarea en background de la carga del fichero SICER
                    CargarFicheroTask cargarFicheroTask = new CargarFicheroTask();
                    cargarFicheroTask.execute(nombreFicheroSeleccionado);
                }
            });
            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int wich) {
                    dialogInterface.cancel();
                }
            });

            builder.show();
        }
    }

    /**
     * Clase privada que se encarga de ejecutar en segundo plano la conexión via FTP
     */
    private class FtpConnectionTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CargarRepartoActivity.this, getString(R.string.conexion_ftp), getString(R.string.espere_conexion_servidor_ftp));
        }

        protected String doInBackground(Void... args) {
            String fallo = null;
            try {
                // Inicializamos la clase Singleton para la gestion FTP con el usuario por defecto que es delegacio/delegacion
                ftpHelper = FTPHelper.getInstancia();
                if(ftpHelper != null && ftpHelper.connect(CargarRepartoActivity.this)) {

                    if(ftpHelper.cargarCarpeta(Util.obtenerRutaFtpSICER(getBaseContext(), obtenerDelegacion()))) {

                        List<FicheroViewHolder> listaFicheros = ftpHelper.obtenerFicherosDirectorio();
                        itemsAdapter = new FicheroAdapter(getBaseContext(), R.layout.item_fichero, listaFicheros);

                    } else {
                        // Error cambio de carpeta
                        fallo = getString(R.string.error_acceso_carpeta_ftp);
                    }

                } else {
                    // Error de conexion
                    fallo = getString(R.string.error_conexion_ftp);
                }

            } catch (Exception e) {
                e.printStackTrace();
                fallo = getString(R.string.error_conexion_ftp);
            }

            return fallo;
        }

        @Override
        protected void onPostExecute(String fallo) {
            if (ftpHelper != null && ftpHelper.isConnected()) {
                // Se ha creado la conexión correctamente
                mlistView_cargar_reparto_ficheros.setAdapter(itemsAdapter);
                mlistView_cargar_reparto_ficheros.setOnItemClickListener(CargarRepartoActivity.this);
                mlistView_cargar_reparto_ficheros.setCacheColorHint(1);
            } else {
                fallo = fallo != null ? fallo : getString(R.string.fallo_conexion_servidor_ftp);
                // Ha fallado la conexión FTP
                AlertDialog.Builder builder = new AlertDialog.Builder(CargarRepartoActivity.this);
                builder.setTitle(R.string.conexion_ftp);
                builder.setMessage(fallo);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }

            progressDialog.dismiss();
        }
    }

    /**
     * Clase privada que ejecuta en segundo plano la carga de los ficheros desde el servidor FTP
     * a la Base de datos interna del dispositivo móvil
     */
    private class CargarFicheroTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CargarRepartoActivity.this, getString(R.string.cargando_notificacion), getString(R.string.cargando_fichero_sicer));
        }

        protected String doInBackground(String... args) {
            String nombreFicheroSeleccionado = args[0];
            String fallo = null;
            Boolean existeFicheroSICER = Boolean.FALSE;
            Boolean existeFicheroSEGUNDOLista = Boolean.FALSE;
            Boolean existeFicheroSEGUNDOReparto = Boolean.FALSE;
            Boolean cargadoSICER = Boolean.FALSE;
            Boolean cargadoSEGUNDOReparto = Boolean.FALSE;
            Boolean cargadoSEGUNDOLista = Boolean.FALSE;

            Boolean esAplicacionDeOficina = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, getBaseContext(), Boolean.class.getSimpleName());

            if (nombreFicheroSeleccionado != null && ftpHelper.isConnected()) {
                // Se comprueba si existe en la base de datos por lo que ya fue cargado anteriormente
                if (nombreFicheroSeleccionado.contains("sicer")) {
                    existeFicheroSICER = dbHelper.cargadoFicheroSICER();
                }
                // Se comprueba si existe en la base de datos por lo que ya fue cargado anteriormente
                if (nombreFicheroSeleccionado.contains("segundo_intento_repartidores")) {
                    existeFicheroSEGUNDOReparto = dbHelper.cargadoFicheroSEGUNDOSReparto();
                }
                // Se comprueba si existe en la base de datos por lo que ya fue cargado anteriormente
                if (nombreFicheroSeleccionado.contains("segundo_intento_lista")) {
                    existeFicheroSEGUNDOLista = dbHelper.cargadoFicheroSEGUNDOSLista();
                }
                // Si no fue cargado con anterioridad
                if ((!cargadoSICER && !existeFicheroSICER && nombreFicheroSeleccionado.contains("sicer"))
                 || (!cargadoSEGUNDOLista && !existeFicheroSEGUNDOLista && nombreFicheroSeleccionado.contains("segundo_intento_lista"))
                 || (!cargadoSEGUNDOReparto && !existeFicheroSEGUNDOReparto && nombreFicheroSeleccionado.contains("segundo_intento_repartidores"))) {

                    // No fue cargado pero es el fichero de segundos erroneo
                    if ((!cargadoSEGUNDOLista && esAplicacionDeOficina && nombreFicheroSeleccionado.contains("segundo_intento_lista"))
                      || (nombreFicheroSeleccionado.contains("sicer"))
                      || (nombreFicheroSeleccionado.contains("segundo_intento_repartidores"))) {

                        // No fue cargado pero es el fichero de segundos errorneo
                        if ((!cargadoSEGUNDOReparto && !esAplicacionDeOficina && nombreFicheroSeleccionado.contains("segundo_intento_repartidores"))
                          || (nombreFicheroSeleccionado.contains("sicer"))
                          || (nombreFicheroSeleccionado.contains("segundo_intento_lista"))) {

                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ftpHelper.leerFichero(nombreFicheroSeleccionado)))) {
                                Integer numLinea = 1;
                                List<Notificacion> listaNotificaciones = new ArrayList<>();
                                Map<String, String> mapaNotificacion = new HashMap<>();
                                // Es fichero SICER
                                Boolean esCargaPrimeraEntrega = Boolean.TRUE;
                                for (String linea = reader.readLine(); linea != null; linea = reader.readLine()) {
                                    // DETALLE (nosotros usaremos "P" para primera entrega)
                                    if (linea.startsWith("P")) {

                                        Notificacion notificacion = new Notificacion();
                                        notificacion.setNombreFicheroSicer(nombreFicheroSeleccionado);
                                        notificacion.setReferencia(linea.substring(1, 71).trim());
                                        notificacion.setNombre(linea.substring(71, 321).trim());
                                        notificacion.setDireccion(linea.substring(321, 456).trim());
                                        notificacion.setCodigoPostal(linea.substring(456, 461).trim());
                                        notificacion.setPoblacion(linea.substring(461, 561).trim());
                                        notificacion.setReferenciaSCB(linea.substring(561, 631).trim());
                                        notificacion.setSegundoIntento(false);
                                        notificacion.setObservacionesRes1("");
                                        notificacion.setObservacionesRes2("");
                                        notificacion.setLatitudRes1("");
                                        notificacion.setLongitudRes1("");
                                        notificacion.setLatitudRes2("");
                                        notificacion.setLongitudRes2("");
                                        notificacion.setEsLista(false);
                                        notificacion.setBackgroundColor(R.color.colorBackgroundSinGestionar);
                                        notificacion.setSegundoIntento(false);
                                        notificacion.setHayST(false);
                                        notificacion.setHayXML(false);

                                        if (linea.trim().endsWith("C")) {
                                            notificacion.setEsCertificado(true);
                                        }
                                        if (linea.trim().endsWith("N")) {
                                            notificacion.setEsCertificado(false);
                                        }

                                        if (!mapaNotificacion.containsKey(notificacion.getReferencia())) {
                                            listaNotificaciones.add(notificacion);
                                            mapaNotificacion.put(notificacion.getReferencia(), notificacion.getReferenciaSCB());
                                        } else {
                                            // Se valida si la referencia sin codigo de barras es la misma
                                            if (!mapaNotificacion.get(notificacion.getReferencia()).equalsIgnoreCase(notificacion.getReferenciaSCB())) {
                                                // si no es la misma, entonces, se incluye, en caso contrario no se incluye ya que es una duplicidad
                                                listaNotificaciones.add(notificacion);
                                                mapaNotificacion.put(notificacion.getReferencia(), notificacion.getReferenciaSCB());
                                            }
                                        }
                                        // Determina que es el formato del sicer de segundo intento
                                    } else if (linea.startsWith("S")) {

                                        if (esCargaPrimeraEntrega) {
                                            // Si es la primera vez que entra aquí, se tiene que comprobar si se ha cargado antes el primer fichero
                                            if (dbHelper.obtenerNotificacion(1) == null) {
                                                throw new CiMobileException(getString(R.string.error_cargar_primero_reparto_sicer));
                                            }
                                        }
                                        // Es SEGUNDOS_INETNTOS
                                        esCargaPrimeraEntrega = Boolean.FALSE;

                                        // Se recupera la referencia postal
                                        String referenciaPostal = linea.substring(1, 71).trim();
                                        //String referenciaSCB = linea.substring(188, 258).trim();

                                        // Lo primero se busca si existe en la base de datos interna la notificacion
                                        // cargada desde el SICER anterior
                                        Notificacion notificacion = dbHelper.obtenerNotificacion(referenciaPostal);
                                        if (notificacion != null) {
                                            Integer colorBackground = notificacion.getBackgroundColor();
                                            notificacion.setResultado1(linea.substring(71, 73).trim());
                                            notificacion.setDescResultado1(linea.substring(73, 98).trim());
                                            notificacion.setLongitudRes1(linea.substring(98, 118).trim());
                                            notificacion.setLatitudRes1(linea.substring(118, 138).trim());
                                            notificacion.setNotificadorRes1(linea.substring(138, 188).trim());
                                            notificacion.setFechaHoraRes1(linea.substring(258, 277).trim());
                                            notificacion.setObservacionesRes1("");
                                            notificacion.setSegundoIntento(true);
                                            // Se comprueba si existe en la base de datos por lo que ya fue cargado anteriormente
                                            if (nombreFicheroSeleccionado.contains("segundo_intento_repartidores")) {
                                                notificacion.setNombreFicheroSegundoRepartidor(nombreFicheroSeleccionado);
                                            }
                                            // Se comprueba si existe en la base de datos por lo que ya fue cargado anteriormente
                                            if (nombreFicheroSeleccionado.contains("segundo_intento_lista")) {
                                                notificacion.setNombreFicheroSegundoLista(nombreFicheroSeleccionado);
                                            }

                                            // Es LISTA
                                            if (linea.trim().endsWith("L")) {
                                                notificacion.setEsLista(true);
                                            }
                                            // NO es LISTA
                                            if (linea.trim().endsWith("R")) {
                                                notificacion.setEsLista(false);
                                            }

                                            // LISTA
                                            if (notificacion.getEsLista()) {
                                                if (notificacion.getEsCertificado()) {
                                                    colorBackground = R.color.colorPrimaryDark;
                                                    Integer numDiasCert = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_DIAS_CERTIFICADAS_LISTA, getBaseContext(), Integer.class.getSimpleName());
                                                    Calendar hoy = Calendar.getInstance();
                                                    Calendar calendarFechaLista = Calendar.getInstance();
                                                    Calendar calendarFechaListaSumada = Calendar.getInstance();
                                                    fechaHoraRes1Date = this.stringToDate(notificacion.getFechaHoraRes1(), "dd/MM/yyyy");
                                                    calendarFechaLista.setTime(fechaHoraRes1Date);
                                                    calendarFechaListaSumada.setTime(fechaHoraRes1Date);
                                                    calendarFechaListaSumada.add(Calendar.DATE, numDiasCert);
                                                    if (calendarFechaListaSumada.after(hoy) && calendarFechaLista.before(hoy)) {
                                                        notificacion.setFueraPlazoLista(Boolean.FALSE);
                                                    } else {
                                                        notificacion.setFueraPlazoLista(Boolean.TRUE);
                                                    }
                                                }
                                                if (!notificacion.getEsCertificado()) {
                                                    colorBackground = R.color.colorBoton;
                                                    Integer numDiasNA = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_DIAS_NA_LISTA, getBaseContext(), Integer.class.getSimpleName());
                                                    Calendar hoy = Calendar.getInstance();
                                                    Calendar calendarFechaLista = Calendar.getInstance();
                                                    Calendar calendarFechaListaSumada = Calendar.getInstance();
                                                    fechaHoraRes1Date = this.stringToDate(notificacion.getFechaHoraRes1(), "dd/MM/yyyy");
                                                    calendarFechaLista.setTime(fechaHoraRes1Date);
                                                    calendarFechaListaSumada.setTime(fechaHoraRes1Date);
                                                    calendarFechaListaSumada.add(Calendar.DATE, numDiasNA);
                                                    if (calendarFechaListaSumada.after(hoy) && calendarFechaLista.before(hoy)) {
                                                        notificacion.setFueraPlazoLista(Boolean.FALSE);
                                                    } else {
                                                        notificacion.setFueraPlazoLista(Boolean.TRUE);
                                                    }
                                                }
                                                //NO LISTA
                                                } else {
                                                    // Si hemos cargado el primer intento ponemos la notificacion en gris
                                                    if (notificacion.getSegundoIntento()) {
                                                        if (notificacion.getResultado1() != null && notificacion.getResultado1().trim().length() > 0) {
                                                            colorBackground = R.color.colorGrisSuave;
                                                        }
                                                    }
                                                }
                                            // Se mapea el backgroundcolor segun valores del resultado
                                            notificacion.setBackgroundColor(colorBackground);

                                            listaNotificaciones.add(notificacion);

                                        } else {
                                            // Si no se ha encontrado, se debe sacar un mensaje con el error al notificador
                                            fallo = getString(R.string.error_no_existe_notif_en_carga_previa) + "(" + referenciaPostal + ")";
                                        }
                                    }

                                    publishProgress(getString(R.string.cargando_fichero_sicer) + numLinea);
                                    numLinea++;
                                }

                                // Se limpia el mapa
                                mapaNotificacion = null;

                                publishProgress(getString(R.string.guardando_datos_en_bd_interna));
                                // SICER.txt
                                if (esCargaPrimeraEntrega) {
                                    // INSERT INTO
                                    dbHelper.guardarNotificacionesInicial(listaNotificaciones);

                                    // seguntoIntento.txt
                                } else {
                                    // UPDATE
                                    DBHelper.actualizarNotificacionesSegundoIntentoInicial(dbHelper, listaNotificaciones);
                                }

                            } catch (CiMobileException e) {
                                fallo = e.getError();
                            } catch (Exception e) {
                                e.printStackTrace();
                                fallo = getString(R.string.error_carga_fichero);
                            }
                        } else {
                            fallo = getString(R.string.segundos_lista_cargado_avisos);
                        }
                    } else {
                        fallo = getString(R.string.segundos_reparto_cargado_aviso);
                    }

            } else {
                if (nombreFicheroSeleccionado.contains("sicer")) {
                    fallo = getString(R.string.sicer_fichero_cargado_anteriormente);
                }
                if (nombreFicheroSeleccionado.contains("segundo_intento_repartidores")) {
                    fallo = getString(R.string.segundos_reparto_fichero_cargado_anteriormente);
                }
                if (nombreFicheroSeleccionado.contains("segundo_intento_lista")) {
                    fallo = getString(R.string.segundos_lista_fichero_cargado_anteriormente);
                }
            }

            } else {
                fallo = getString(R.string.fallo_conexion_servidor_ftp);
            }

            return fallo;
        }

        private Date stringToDate(String aDate,String aFormat) {
            if(aDate == null) return null;
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
            Date stringDate = simpledateformat.parse(aDate, pos);
            return stringDate;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(CargarRepartoActivity.this);
            builder.setTitle(R.string.informacion_carga);
            if (StringUtils.isBlank(result)) {
                builder.setMessage(R.string.datos_guardados_ok_bd);
            } else {
                // Se muestra el error
                builder.setMessage(result);
            }
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }
    }
}
