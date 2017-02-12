package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class CargaResumenTask extends AsyncTask<Void, Void, ResumenReparto> {
        ProgressDialog progressDialog;

        @Override
        protected ResumenReparto doInBackground(Void... voids) {
            ResumenReparto resumen = dbHelper.obtenerResumenReparto();

            return resumen;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getMessageResources(R.string.resumen_reparto), getMessageResources(R.string.espere_info_reparto));
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

    private void crearDialogoAvisoCierreReparto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cerrar_reparto);
        builder.setMessage("Va a cerrar el reparto, lo que conlleva la generacion del fichero con los datos de las notificaciones," +
                " el volcado del mismo en la carpeta FTP correspondiente y el borrado de los datos internos de la PDA. ¿Desea proseguir?");
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

    private class CerrarRepartoTASK extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        protected Void doInBackground(Void... args) {
            try {

                // Se establece la conexion con el servidor FTP
                ftpHelper = FTPHelper.getInstancia();

                if(ftpHelper != null && ftpHelper.connect()) {

                    // Se comprueba si existe la carpeta del notificador, sino se crea
                    String pathVolcado = "/SICERS/" + obtenerCodigoNotificador();
                    if(ftpHelper.cargarCarpetaNotificador(pathVolcado)) {

                        // Se recuperan las notificaciones que se han gestionado durante el reparto
                        List<Notificacion> listaNotificacionesGestionadas = dbHelper.obtenerNotificacionesGestionadas();
                        Calendar calendarAux = Calendar.getInstance();
                        File file = File.createTempFile("prueba", ".csv");
                        FileWriter writer = new FileWriter(file);

                        for (Notificacion notificacion : listaNotificacionesGestionadas) {

                            // Se recupera el codigo de resultado y la fecha segun es primer o segundo intento
                            String codResultado = notificacion.getResultado1();
                            String fechaResultadoString = notificacion.getFechaHoraRes1();
                            if (notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento()) {
                                codResultado = notificacion.getResultado2();
                                fechaResultadoString = notificacion.getFechaHoraRes2();
                                Resultado resultado = dbHelper.obtenerResultado(notificacion.getResultado2());
                                if (resultado.getEsFinal() != null && !resultado.getEsFinal()) {
                                    codResultado = resultado.getCodigoSegundoIntento();
                                }
                            }

                            // Se formatea la fecha resultado
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date dateAux = formatter.parse(fechaResultadoString);
                            DateFormat df = new SimpleDateFormat("yyyyMMdd");
                            calendarAux.setTime(dateAux);
                            fechaResultadoString = df.format(calendarAux.getTime());

                            writer.append(obtenerDelegacion() + ";" + obtenerCodigoNotificador() + ";" + codResultado + ";" + notificacion.getReferencia() + ";" + fechaResultadoString + ";" + fechaResultadoString);

                        }

                        writer.flush();

                        // Una vez generado el fichero, se sube al servidor FTP
                        Boolean ok = ftpHelper.subirFichero(file, pathVolcado);


                    } else {
                        // error cambio de carpeta o crear carpeta
                    }

                } else {
                    // error de conexion
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getString(R.string.conexion_ftp), getString(R.string.espere_conexion_servidor_ftp));
        }

        @Override
        protected void onPostExecute(Void result) {

            progressDialog.dismiss();
        }
    }

}
