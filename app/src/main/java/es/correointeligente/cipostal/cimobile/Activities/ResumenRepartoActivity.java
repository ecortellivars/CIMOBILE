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
import android.widget.Toast;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
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
    Integer totNotisGestionadas = 0;
    Integer totResultados = 0;
    Integer totFotosHechas = 0;
    Integer totNumLista = 0;
    Boolean borradoManual = Boolean.FALSE;

    // Variables para instanciar los objetos del layaout y darles valor
    TextView tv_totFicheros, tv_totNotificaciones, tv_totNotifGestionadas, tv_totNotifPendientes_2_hoy,
             tv_totNotifPendientes_2_otro_dia, tv_totNotifMarcadas, tv_totFotos, tv_totFotos_txt,  tv_totResultados, tv_totNotiLista;
    TextView tv_entregado, tv_dirIncorrecta, tv_ausente, tv_ausente_pendiente, tv_desconocido, tv_fallecido, tv_rehusado,
            tv_noSeHaceCargo, tv_noSeHaceCargoPendiente, tv_entregado_en_oficina, tv_no_entregado_en_oficina,
            tv_entregado_en_oficina_txt, tv_no_entregado_en_oficina_txt, tv_totNotiLista_txt;

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
        tv_totFotos_txt = (TextView) findViewById(R.id.textView_resumen_total_fotos);
        tv_totFotos = (TextView) findViewById(R.id.textView_resumen_total_fotos_value);
        tv_totResultados = (TextView) findViewById(R.id.textView_resumen_total_resultados_value);
        tv_totNotifPendientes_2_hoy = (TextView) findViewById(R.id.textView_resumen_total_notif_pendientes_2_hoy_value);
        tv_totNotifPendientes_2_otro_dia = (TextView) findViewById(R.id.textView_resumen_total_notif_pendientes_2_otro_dia_value);
        tv_totNotifMarcadas = (TextView) findViewById(R.id.textView_resumen_total_notif_marcadas_value);
        tv_totNotiLista = (TextView) findViewById(R.id.textView_resumen_total_notif_lista_value);
        tv_totNotiLista_txt = (TextView) findViewById(R.id.textView_resumen_total_lista_txt);


        tv_entregado = (TextView) findViewById(R.id.textView_resumen_entregado_value);
        tv_entregado_en_oficina = (TextView) findViewById(R.id.textView_resumen_entregado_en_oficina_value);
        tv_no_entregado_en_oficina = (TextView) findViewById(R.id.textView_resumen_no_entregado_en_oficina_value);
        tv_entregado_en_oficina_txt = (TextView) findViewById(R.id.textView_resumen_entregado_en_oficina_text);
        tv_no_entregado_en_oficina_txt = (TextView) findViewById(R.id.textView_resumen_no_entregado_en_oficina_text);
        tv_dirIncorrecta = (TextView) findViewById(R.id.textView_resumen_dir_incorrecta_value);
        tv_ausente = (TextView) findViewById(R.id.textView_resumen_ausente_value);
        tv_ausente_pendiente = (TextView) findViewById(R.id.textView_resumen_ausente_pendiente_value);
        tv_desconocido = (TextView) findViewById(R.id.textView_resumen_desconocido_value);
        tv_fallecido = (TextView) findViewById(R.id.textView_resumen_fallecido_value);
        tv_rehusado = (TextView) findViewById(R.id.textView_resumen_rehusado_value);
        tv_noSeHaceCargo = (TextView) findViewById(R.id.textView_resumen_nadie_cargo_value);
        tv_noSeHaceCargoPendiente = (TextView) findViewById(R.id.textView_resumen_nadie_cargo_pendiente_value);

        mCerrarReparto = (Button) findViewById(R.id.button_resumen_cerrar_reparto);
        mCerrarReparto.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_borrar_notificacion, menu);
        return true;
    }

    // Logica de los iconos de la toolBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case  R.id.menu_borrar_notificaciones:
                this.crearDialogoEliminarNotificaciones();
                borradoManual = Boolean.TRUE;
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
            totNotisGestionadas = resumen.getTotNotifGestionadas();
            totResultados = resumen.getTotResultados();
            totNumLista = resumen.getTotNumLista();
            return resumen;
        }

        /**
         * Cargamos los valores a mostrar en el layout
         * @param resumenReparto
         */
        @Override
        protected void onPostExecute(ResumenReparto resumenReparto) {

            progressDialog.dismiss();

            tv_totFicheros.setText(resumenReparto.getTotFicheros().toString());
            tv_totNotificaciones.setText(resumenReparto.getTotNotificaciones().toString());
            tv_totNotifPendientes_2_hoy.setText(resumenReparto.getTotNotifPendientesSegundoHoy().toString());
            tv_totNotifPendientes_2_otro_dia.setText(resumenReparto.getTotNotifPendientesSegundoOtroDia().toString());
            tv_totNotifMarcadas.setText(resumenReparto.getTotNotifMarcadas().toString());
            tv_totNotiLista.setText(resumenReparto.getTotNumLista().toString());

            // Se recuperan las notificaciones que se han gestionado durante el reparto para contar la fotos
            List<Notificacion> listaNotificacionesGestionadas = dbHelper.obtenerNotificacionesGestionadas();
            Integer contadorFotos = 0;
            if (!listaNotificacionesGestionadas.isEmpty()){
                for (Notificacion noti : listaNotificacionesGestionadas){
                    if (noti.getFotoAcuseRes2() != null && !noti.getFotoAcuseRes2().isEmpty()) {
                        contadorFotos = contadorFotos + 1;
                    }
                    if (noti.getFotoAcuseRes1() != null && !noti.getFotoAcuseRes1().isEmpty()) {
                        contadorFotos = contadorFotos + 1;
                    }
                }
            }
            totFotosHechas = contadorFotos;
            tv_totFotos.setText(contadorFotos.toString());
            tv_totResultados.setText(resumenReparto.getTotResultados().toString());
            tv_totNotifGestionadas.setText(resumenReparto.getTotNotifGestionadas().toString());
            tv_entregado.setText(resumenReparto.getNumEntregados().toString());
            tv_entregado_en_oficina.setText(resumenReparto.getNumEntregadosEnOficina().toString());
            tv_no_entregado_en_oficina.setText(resumenReparto.getNumNoEntregadosEnOficna().toString());

            Boolean esAplicacionDeOficina = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, getBaseContext(), Boolean.class.getSimpleName());
            Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
            // No hay LISTA oculto
            if (!esAplicacionDeOficina){
                tv_entregado_en_oficina.setVisibility(View.INVISIBLE);
                tv_no_entregado_en_oficina.setVisibility(View.INVISIBLE);
                tv_entregado_en_oficina_txt.setVisibility(View.INVISIBLE);
                tv_no_entregado_en_oficina_txt.setVisibility(View.INVISIBLE);
                tv_totNotiLista.setVisibility(View.INVISIBLE);
                tv_totNotiLista_txt.setVisibility(View.INVISIBLE);

            }
            // Hay PEE NO hay fotos
            if (esAplicacionPEE){
                tv_totFotos.setVisibility(View.INVISIBLE);
                tv_totFotos_txt.setVisibility(View.INVISIBLE);
            }
            tv_dirIncorrecta.setText(resumenReparto.getNumDirIncorrectas().toString());
            tv_ausente.setText(resumenReparto.getNumAusentes().toString());
            tv_ausente_pendiente.setText(resumenReparto.getNumAusentesPendientes().toString());
            tv_desconocido.setText(resumenReparto.getNumDesconocidos().toString());
            tv_fallecido.setText(resumenReparto.getNumFallecidos().toString());
            tv_rehusado.setText(resumenReparto.getNumRehusados().toString());
            tv_noSeHaceCargo.setText(resumenReparto.getNumNadieSeHaceCargo().toString());
            tv_noSeHaceCargoPendiente.setText(resumenReparto.getNumNadieSeHaceCargoPendientes().toString());
        }
    }

    /**
     * Método privado que pide confirmación para el cierre del reparto indicando todas las acciones a realizar
     */
    private void crearDialogoAvisoCierreReparto() {
        // Dependiendo de si es una aplicación PEE revisara las fotos o no
        Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());

        if (totNotisGestionadas != totFotosHechas && !esAplicacionPEE) {
            Toast toast = null;
            toast = Toast.makeText(ResumenRepartoActivity.this, "Existen notificaciones sin foto por lo que no se puede cerrar el reparto", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            // Modificación 
            // El CSV para los resultados: valencia_A22_25052017.csv
            String nombreFicheroCSV = obtenerDelegacion() + "_" + obtenerCodigoNotificador() + "_" + dfDia.format(Calendar.getInstance().getTime()) + ".csv";
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
                            // Quitamos el string "sin firma" o "(2ª VISITA)"
                            for (Notificacion notificacion : listaNotificacionesGestionadas) {
                                String resultadoSinFirma1 =  notificacion.getDescResultado1();
                                if (resultadoSinFirma1 != null){
                                    notificacion.setDescResultado1(resultadoSinFirma1.replace("sin firma",""));
                                    notificacion.setDescResultado1(resultadoSinFirma1.replace("(1ª VISITA)",""));
                                    notificacion.setDescResultado1(resultadoSinFirma1.replace("(1ª Visita)",""));
                                }
                                String resultadoSinFirma2 =  notificacion.getDescResultado1();
                                if (resultadoSinFirma2 != null){
                                    notificacion.setDescResultado2(resultadoSinFirma2.replace("sin firma",""));
                                    notificacion.setDescResultado2(resultadoSinFirma2.replace("(2ª VISITA)",""));
                                    notificacion.setDescResultado2(resultadoSinFirma2.replace("(2ª Visita)",""));
                                }

                                // Se recupera el codigo de resultado y la fecha segun es primer intento
                                String codResultado1 = notificacion.getResultado1();
                                String fechaResultadoString1 = notificacion.getFechaHoraRes1();
                                // Se recupera el codigo de resultado y la fecha segun es segundo intento
                                String codResultado2 = notificacion.getResultado2();
                                String fechaResultadoString2 = notificacion.getFechaHoraRes2();
                                // Si es segundo intento para lista o cierra lista
                                if (codResultado2 != null && !codResultado2.isEmpty()) {
                                    // En caso de ser un resultado de segundo intento hay que codificar correctamente
                                    // su codigo dependiendo del resultado
                                    Resultado resultado = dbHelper.obtenerResultado(notificacion.getResultado2());
                                    if ((resultado.getEsFinal() != null && !resultado.getEsFinal())) {
                                        codResultado2 = resultado.getCodigoSegundoIntento();
                                        if (resultado.getEsResultadoOficina()) {
                                            codResultado2 = notificacion.getResultado2();
                                        }
                                    }else {
                                        notificacion.setResultado2(codResultado2);
                                        notificacion.setDescResultado2(resultado.getDescripcion());
                                    }

                                    // Se formatea la fecha resultado y se inserta en el csv
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date dateAux = formatter.parse(fechaResultadoString1);
                                    DateFormat df = new SimpleDateFormat("yyyyMMdd");
                                    calendarAux.setTime(dateAux);
                                    fechaResultadoString2 = df.format(calendarAux.getTime());
                                    writerCSV.append(obtenerDelegacion() + ";" + obtenerCodigoNotificador() + ";" + codResultado2 + ";" + notificacion.getReferencia() + ";" + fechaResultadoString2 + ";" + fechaResultadoString2 + "\n");

                                    if (codResultado2.equals(Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO)
                                     || codResultado2.equals(Util.RESULTADO_AUSENTE_SEGUNDO)){
                                        if (notificacion.getLongitudRes1().isEmpty()
                                                || notificacion.getLongitudRes1().toString().length() == 0
                                                || notificacion.getLongitudRes1() == null) {
                                            notificacion.setLongitudRes1("-0.0000000");
                                        }

                                        if (notificacion.getLatitudRes1().isEmpty()
                                                || notificacion.getLatitudRes1().toString().length() == 0
                                                || notificacion.getLatitudRes1() == null) {
                                            notificacion.setLatitudRes1("00.0000000");
                                        }
                                        String linea = "S" + StringUtils.rightPad(notificacion.getReferencia(), 70);
                                        linea += StringUtils.rightPad(resultado.getCodigo(), 2);
                                        linea += StringUtils.rightPad(resultado.getDescripcion().toUpperCase().replace("(2ª VISITA)",""), 25);
                                        linea += StringUtils.rightPad(notificacion.getLongitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getLatitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getNotificadorRes1(), 50);
                                        linea += StringUtils.rightPad(notificacion.getReferenciaSCB(), 70);
                                        linea += StringUtils.rightPad(notificacion.getFechaHoraRes1(), 19);
                                        linea += "\n";
                                        writerTXT.append(linea);
                                    }
                                } else {
                                    // Si es resultado de primer intento, dependiendo de si el resultado es final o no,
                                    // hay que ir añadiendolo al fichero de segundos intentos para el dia siguiente
                                    Resultado resultado = dbHelper.obtenerResultado(codResultado1);
                                    // Si es primera visita y NO ES FINAL lo guardamos en un TXT para la carga del dia siguiente
                                    // O es segunda visita pero gestiona lista y no es 08 ni 09
                                    if (BooleanUtils.isFalse(resultado.getEsFinal()) && BooleanUtils.isFalse(resultado.getEsResultadoOficina()))
                                    {
                                        if (notificacion.getLongitudRes1().isEmpty()
                                         || notificacion.getLongitudRes1().toString().length() == 0
                                         || notificacion.getLongitudRes1() == null) {
                                                 notificacion.setLongitudRes1("-0.0000000");
                                        }

                                        if (notificacion.getLatitudRes1().isEmpty()
                                         || notificacion.getLatitudRes1().toString().length() == 0
                                         || notificacion.getLatitudRes1() == null) {
                                                 notificacion.setLatitudRes1("00.0000000");
                                        }
                                        String linea = "S" + StringUtils.rightPad(notificacion.getReferencia(), 70);
                                        linea += StringUtils.rightPad(resultado.getCodigo(), 2);
                                        linea += StringUtils.rightPad(resultado.getDescripcion().toUpperCase().replace("(1ª VISITA)",""), 25);
                                        linea += StringUtils.rightPad(notificacion.getLongitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getLatitudRes1(), 20);
                                        linea += StringUtils.rightPad(notificacion.getNotificadorRes1(), 50);
                                        linea += StringUtils.rightPad(notificacion.getReferenciaSCB(), 70);
                                        linea += StringUtils.rightPad(notificacion.getFechaHoraRes1(), 19);
                                        linea += "\n";
                                        writerTXT.append(linea);
                                    }
                                    // Se formatea la fecha resultado
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date dateAux = formatter.parse(fechaResultadoString1);
                                    DateFormat df = new SimpleDateFormat("yyyyMMdd");
                                    calendarAux.setTime(dateAux);
                                    fechaResultadoString1 = df.format(calendarAux.getTime());
                                    writerCSV.append(obtenerDelegacion() + ";" + obtenerCodigoNotificador() + ";" + codResultado1 + ";" + notificacion.getReferencia() + ";" + fechaResultadoString1 + ";" + fechaResultadoString1 + "\n");

                                }

                               }

                            writerCSV.flush();
                            writerTXT.flush();

                            // Una vez generado el fichero, se sube al servidor FTP
                            publishProgress(getString(R.string.subiendo_fichero_CSV));
                            if (!ftpHelper.subirFichero(ficheroCSV, pathVolcado)) {
                                // Si fallo borro CSV y acabo
                                fallo = getString(R.string.error_subir_fichero_CSV);
                                // Borro movil
                                ficheroCSV.delete();
                                // Borro ftp
                                ftpHelper.borrarFichero(ficheroCSV, pathVolcado);
                                // Si no fallo y hay TXT continuo con el TXT
                                } else if (fallo == "" && ficheroTXT != null && !ftpHelper.subirFichero(ficheroTXT, pathVolcadoSegundoIntento)) {
                                   publishProgress(getString(R.string.subiendo_fichero_segundo_intento));
                                    // Si fallo borro ambos
                                    fallo = getString(R.string.error_subiendo_fichero_segundo_intento);
                                    // Borro movil
                                    ficheroTXT.delete();
                                    ficheroCSV.delete();
                                    // Borro ftp
                                    ftpHelper.borrarFichero(ficheroTXT, pathVolcado);
                                    ftpHelper.borrarFichero(ficheroCSV, pathVolcado);
                                } else {
                                    // Generar ZIP con los xml, las firmas, los sellos de tiempo, las fotos de los acuses y el csv
                                    publishProgress(getString(R.string.generando_fichero_zip));
                                    ficheroZIP = Util.comprimirZIP(obtenerCodigoNotificador(), obtenerDelegacion());
                                    publishProgress(getString(R.string.subiendo_fichero_zip));
                                    if (!ftpHelper.subirFichero(ficheroZIP, pathVolcado)) {
                                        // Si fallo borro los 3
                                        fallo = getString(R.string.error_subir_fichero_zip);
                                        // Borro movil
                                        ficheroZIP.delete();
                                        ficheroTXT.delete();
                                        ficheroCSV.delete();
                                        // Borro ftp
                                        ftpHelper.borrarFichero(ficheroTXT, pathVolcado);
                                        ftpHelper.borrarFichero(ficheroCSV, pathVolcado);
                                        ftpHelper.borrarFichero(ficheroZIP, pathVolcado);
                                    }
                                }

                        } catch (IOException e) {
                            fallo = getString(R.string.error_apertura_ficheros_escritura);
                        }
                    } else {
                        // Error cambio de carpeta o crear carpeta
                        fallo = getString(R.string.error_acceso_carpeta_ftp) + " '" + pathVolcado + "'";
                    }

                } else {
                    // error de conexion
                    fallo = getString(R.string.error_conexion_ftp);
                }

                // Dependiendo de si es una aplicación PEE revisara si borra o no
                Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());

                if (esAplicacionPEE && !borradoManual){
                    fallo = getString(R.string.error_borrado_notificaciones_PEE);
                } else if (StringUtils.isBlank(fallo)) {
                        // Si no ha habido ningún fallo y no es PEE se limpia la base de datos
                        publishProgress(getString(R.string.limpiando_base_datos));
                        if (!dbHelper.borrarNotificaciones()) {
                            fallo = getString(R.string.error_borrado_notificaciones);
                        } else {
                            // Borra las carpetas
                            publishProgress(getString(R.string.limpiando_directorio));
                            if (!Util.borrarFicherosAplicacion()) {
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
            // Dependiendo de si es una aplicación PEE revisara si borra o no
            Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
            String fallo = null;

            if (esAplicacionPEE && !borradoManual){
                fallo = getString(R.string.error_borrado_notificaciones_PEE);
                } else  if(!dbHelper.borrarNotificaciones()) {
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
