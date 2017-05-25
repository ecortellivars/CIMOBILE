package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.Model.ResumenReparto;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class ResumenRepartoActivity extends BaseActivity implements View.OnClickListener {
    Toolbar mToolbar;
    DBHelper dbHelper;
    Button mCerrarReparto;
    FTPHelper ftpHelper;

    TextView tv_totFicheros, tv_totNotificaciones, tv_totNotifGestionadas, tv_totNotifMarcadas;
    TextView tv_entregado, tv_dirIncorrecta, tv_ausente, tv_desconocido, tv_fallecido, tv_rehusado, tv_noSeHaceCargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_reparto);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        this.mapearVistaTextViews();

        // Lanza en background las consultas para rellenar la vista
        CargaResumenTask cargaResumenTask = new CargaResumenTask();
        cargaResumenTask.execute();
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_resumen_reparto;
    }

    @Override
    public void onClick(View view) {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(20);

        switch (view.getId()) {
            case R.id.button_resumen_cerrar_reparto:
                crearDialogoAvisoCierreReparto();
                break;
            default:
                break;
        }
    }

    /**
     * Clase privada para mapear todas las vistas del layout
     */
    private void mapearVistaTextViews() {
        tv_totFicheros = (TextView) findViewById(R.id.textView_resumen_total_ficheros_value);
        tv_totNotificaciones = (TextView) findViewById(R.id.textView_resumen_total_notificaciones_value);
        tv_totNotifGestionadas = (TextView) findViewById(R.id.textView_resumen_total_notif_gestionadas_value);
        tv_totNotifMarcadas = (TextView) findViewById(R.id.textView_resumen_total_notif_marcadas_value);

        tv_entregado = (TextView) findViewById(R.id.textView_resumen_entregado_value);
        tv_dirIncorrecta = (TextView) findViewById(R.id.textView_resumen_dir_incorrecta_value);
        tv_ausente = (TextView) findViewById(R.id.textView_resumen_ausente_value);
        tv_desconocido = (TextView) findViewById(R.id.textView_resumen_desconocido_value);
        tv_fallecido = (TextView) findViewById(R.id.textView_resumen_fallecido_value);
        tv_rehusado = (TextView) findViewById(R.id.textView_resumen_rehusado_value);
        tv_noSeHaceCargo = (TextView) findViewById(R.id.textView_resumen_nadie_cargo_value);

        mCerrarReparto = (Button) findViewById(R.id.button_resumen_cerrar_reparto);
        mCerrarReparto.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_borrar_notificacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case  R.id.menu_borrar_notificaciones:
                this.crearDialogoEliminarNotificaciones();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Clase privada que se lanza en background para realizar las consultas a la BD SQLite y
     * cargar el resumen de las notificaciones
     */
    private class CargaResumenTask extends AsyncTask<Void, Void, ResumenReparto> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getString(R.string.resumen_reparto), getString(R.string.espere_info_reparto));
        }

        @Override
        protected ResumenReparto doInBackground(Void... voids) {
            ResumenReparto resumen = dbHelper.obtenerResumenReparto();

            return resumen;
        }

        @Override
        protected void onPostExecute(ResumenReparto resumenReparto) {

            progressDialog.dismiss();

            tv_totFicheros.setText(resumenReparto.getTotFicheros().toString());
            tv_totNotificaciones.setText(resumenReparto.getTotNotificaciones().toString());
            tv_totNotifMarcadas.setText(resumenReparto.getTotNotifMarcadas().toString());
            tv_totNotifGestionadas.setText(resumenReparto.getTotNotifGestionadas().toString());

            tv_entregado.setText(resumenReparto.getNumEntregados().toString());
            tv_dirIncorrecta.setText(resumenReparto.getNumDirIncorrectas().toString());
            tv_ausente.setText(resumenReparto.getNumAusentes().toString());
            tv_desconocido.setText(resumenReparto.getNumDesconocidos().toString());
            tv_fallecido.setText(resumenReparto.getNumFallecidos().toString());
            tv_rehusado.setText(resumenReparto.getNumRehusados().toString());
            tv_noSeHaceCargo.setText(resumenReparto.getNumNadieSeHaceCargo().toString());
        }
    }

    /**
     * Método privado que pide confirmación para el cierre del reparto indicando todas las acciones a realizar
     */
    private void crearDialogoAvisoCierreReparto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cerrar_reparto);
        builder.setMessage(R.string.cerrar_reparto_info);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Lanza la tarea en background de la carga del fichero SICER
                CerrarRepartoTASK cerrarRepartoTASK = new CerrarRepartoTASK();
                cerrarRepartoTASK.execute();
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

    /**
     * Clase privada que se encarga del cierre del reparto, entre las acciones a realizar:
     *  1.- Conexión al servidor FTP
     *  2.- Recorrer las notificaciones gestionadas e ir volcando la informacion a un fichero CSV
     *  3.- Generar un fichero ZIP con todos los sellados de tiempo, los xml, las firmas y el CSV
     *  4.- Volcar ambos ficheros al servidor FTP
     */
    private class CerrarRepartoTASK extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getString(R.string.cerrar_reparto), getString(R.string.espere_conexion_servidor_ftp));
        }

        protected String doInBackground(String... args) {
            String fallo = "";
            File ficheroZIP = null;
            File ficheroCSV = null;
            File ficheroTXT = null;

            DateFormat dfDia = new SimpleDateFormat("ddMMyyyy");
            // El CSV para los resultados: valencia_25052017.csv
            String nombreFicheroCSV = obtenerDelegacion() + "_" + dfDia.format(Calendar.getInstance().getTime()) + ".csv";
            // El TXT para segundos intentos: segundo_intento_A22_25052017.txt
            String nombreFicheroTXT = Util.NOMBRE_FICHERO_SEGUNDO_INTENTO + "_" + obtenerCodigoNotificador() + "_" +dfDia.format(Calendar.getInstance().getTime())+".txt";
            try {
                // Se establece la conexion con el servidor FTP
                ftpHelper = FTPHelper.getInstancia();

                if(ftpHelper != null && ftpHelper.connect(ResumenRepartoActivity.this)) {

                    // Se comprueba si existe la carpeta del notificador, sino se crea /ftpData/VALENCIA/SICER
                    String rutaCarpetaSICER = Util.obtenerRutaFtpSICER(getBaseContext(), obtenerDelegacion());
                    // /ftpData/VALENCIA/SICER/A22
                    String pathVolcado = rutaCarpetaSICER + File.separator + obtenerCodigoNotificador();
                    // /ftpData/VALENCIA/SICER
                    String pathVolcadoSegundoIntento = rutaCarpetaSICER;
                    if(ftpHelper.cargarCarpetaNotificador(pathVolcado)) {

                        // Se recuperan las notificaciones que se han gestionado durante el reparto
                        List<Notificacion> listaNotificacionesGestionadas = dbHelper.obtenerNotificacionesGestionadas();
                        Calendar calendarAux = Calendar.getInstance();

                        // valencia_25052017.csv
                        ficheroCSV = new File(Util.obtenerRutaAPP(), nombreFicheroCSV);
                        // segundo_intento_A22_25052017.txt
                        ficheroTXT = new File(Util.obtenerRutaAPP(), nombreFicheroTXT);

                        try (FileWriter writerCSV = new FileWriter(ficheroCSV);
                             FileWriter writerTXT = new FileWriter(ficheroTXT);) {


                            publishProgress(getString(R.string.generando_fichero_CSV));
                            for (Notificacion notificacion : listaNotificacionesGestionadas) {

                                // Se recupera el codigo de resultado y la fecha segun es primer o segundo intento
                                String codResultado = notificacion.getResultado1();
                                String fechaResultadoString = notificacion.getFechaHoraRes1();
                                if (notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento()) {
                                    // En caso de ser un resultado de segundo intento hay que codificar correctamente
                                    // su codigo dependiendo del resultado
                                    codResultado = notificacion.getResultado2();
                                    fechaResultadoString = notificacion.getFechaHoraRes2();
                                    Resultado resultado = dbHelper.obtenerResultado(notificacion.getResultado2());
                                    if (resultado.getEsFinal() != null && !resultado.getEsFinal()) {
                                        codResultado = resultado.getCodigoSegundoIntento();
                                    }

                                } else {
                                    // Si es resultado de primer intento, dependiendo de si el resultado es final o no,
                                    // hay que ir añadiendolo al fichero de segundos intentos para el dia siguiente
                                    Resultado resultado = dbHelper.obtenerResultado(codResultado);
                                    // Si es primera visita y NO ES FINAL lo guardamos en un TXT para la carga del dia siguiente
                                    if (BooleanUtils.isFalse(resultado.getEsFinal())) {
                                        String linea = "S" + StringUtils.rightPad(notificacion.getReferencia(), 70);
                                        linea += StringUtils.rightPad(resultado.getCodigo(), 2);
                                        linea += StringUtils.rightPad(resultado.getDescripcion().toUpperCase(), 25);
                                        linea += StringUtils.rightPad(notificacion.getLongitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getLatitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getNotificadorRes1(), 50);
                                        linea += StringUtils.rightPad(notificacion.getReferenciaSCB(), 70);
                                        linea += StringUtils.rightPad(notificacion.getFechaHoraRes1(), 19);
                                        linea += "\n";
                                        writerTXT.append(linea);
                                    }
                                }

                                // Se formatea la fecha resultado
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date dateAux = formatter.parse(fechaResultadoString);
                                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                                calendarAux.setTime(dateAux);
                                fechaResultadoString = df.format(calendarAux.getTime());

                                writerCSV.append(obtenerDelegacion() + ";" + obtenerCodigoNotificador() + ";" + codResultado + ";" + notificacion.getReferencia() + ";" + fechaResultadoString + ";" + fechaResultadoString + "\n");
                            }

                            writerCSV.flush();
                            writerTXT.flush();

                            // Una vez generado el fichero, se sube al servidor FTP
                            publishProgress(getString(R.string.subiendo_fichero_CSV));
                            if (!ftpHelper.subirFichero(ficheroCSV, pathVolcado)) {
                                fallo = getString(R.string.error_subir_fichero_CSV);
                                ficheroCSV.delete();
                            } else {

                                if (ficheroTXT != null && ficheroTXT.length() > 0) {
                                    publishProgress(getString(R.string.subiendo_fichero_segundo_intento));
                                    if (!ftpHelper.subirFichero(ficheroTXT, pathVolcadoSegundoIntento)) {
                                        fallo = getString(R.string.error_subiendo_fichero_segundo_intento);
                                        ficheroTXT.delete();
                                    }
                                } else {
                                    ficheroTXT.delete();
                                }

                                // Generar ZIP con los xml, las firmas, los sellos de tiempo, las fotos de los acuses y el csv
                                publishProgress(getString(R.string.generando_fichero_zip));
                                ficheroZIP = Util.comprimirZIP(obtenerCodigoNotificador(), obtenerDelegacion());
                                publishProgress(getString(R.string.subiendo_fichero_zip));
                                if (!ftpHelper.subirFichero(ficheroZIP, pathVolcado)) {
                                    fallo = getString(R.string.error_subir_fichero_zip);
                                    ficheroZIP.delete();
                                }
                            }
                        } catch (IOException e) {
                            fallo = getString(R.string.error_apertura_ficheros_escritura);
                        }
                    } else {
                        // error cambio de carpeta o crear carpeta
                        fallo = getString(R.string.error_acceso_carpeta_ftp)+" '"+pathVolcado+"'";
                    }

                } else {
                    // error de conexion
                    fallo = getString(R.string.error_conexion_ftp);
                }

                if(StringUtils.isBlank(fallo)) {
                    // Si no ha habido ningún fallo se limpia la base de datos
                    publishProgress(getString(R.string.limpiando_base_datos));
                    if(!dbHelper.borrarNotificaciones()) {
                        fallo = getString(R.string.error_borrado_notificaciones);
                    } else {
                        // Borra las carpetas
                        publishProgress(getString(R.string.limpiando_directorio));
                        if(!Util.borrarFicherosAplicacion()) {
                            fallo = getString(R.string.error_fallo_borrar_ficheros_sensibles);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                fallo = getString(R.string.error_proceso_cierre_reparto);
            }

            return fallo;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String fallo) {
            // Se cierra el dialogo de espera
            progressDialog.dismiss();

            // Se crea el dialogo de respuesta
            AlertDialog.Builder builder = new AlertDialog.Builder(ResumenRepartoActivity.this);
            builder.setTitle(R.string.cerrar_reparto);

            if(fallo != null && !fallo.isEmpty()) {
                // En caso de haber habiado algún fallo
                builder.setMessage(fallo);
            } else {
                builder.setMessage(R.string.cierre_reparto_correcto);
            }

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    finish();
                }
            });

            // Genera el dialogo y lo muestra por pantalla
            builder.show();
        }
    }

    /**
     * Método privado que se encarga de crear un dialogo para informar de las acciones a
     * realizar si se acepta eliminar las notificaciones
     */
    private void crearDialogoEliminarNotificaciones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_notificaciones);
        builder.setMessage(R.string.seguro_eliminar_notificaciones);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Lanza la tarea en background de la eliminación de todas las notificaciones
                EliminarNotificacionTask eliminarNotificacionTask = new EliminarNotificacionTask();
                eliminarNotificacionTask.execute();
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


    /**
     * Clase privada que se encarga de eliminar todas las notificaciones
     * 1.- Limpia la BD interna SQLite
     * 2.- Elimina los XML
     * 3.- Elimina los sellos de tiempo
     * 4.- Elimina las imagenes firmadas
     */
    private class EliminarNotificacionTask extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getString(R.string.eliminar_notificaciones), getString(R.string.limpiando_base_datos));
        }

        @Override
        protected String doInBackground(Void... voids) {
            String fallo = null;

            if(!dbHelper.borrarNotificaciones()) {
                fallo = getString(R.string.error_borrado_notificaciones);
            } else {
                // Borra las carpetas
                publishProgress(getString(R.string.limpiando_directorio));
                if(!Util.borrarFicherosAplicacion()) {
                    fallo = getString(R.string.error_fallo_borrar_ficheros_sensibles);
                }
            }

            return fallo;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String fallo) {

            progressDialog.dismiss();
            // Se crea el dialogo de respuesta
            AlertDialog.Builder builder = new AlertDialog.Builder(ResumenRepartoActivity.this);
            builder.setTitle(R.string.eliminar_notificaciones);

            if(fallo != null && !fallo.isEmpty()) {
                // En caso de haber habiado algún fallo
                builder.setMessage(fallo);
            } else {
                builder.setMessage(R.string.eliminado_todo_correctamente);
            }

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    finish();
                }
            });

            // Genera el dialogo y lo muestra por pantalla
            builder.show();
        }
    }

}
