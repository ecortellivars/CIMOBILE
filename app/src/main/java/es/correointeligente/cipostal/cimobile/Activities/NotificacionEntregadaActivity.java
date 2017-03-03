package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.TSA.TimeStamp;
import es.correointeligente.cipostal.cimobile.TSA.TimeStampRequestParameters;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.CiMobileException;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Lienzo;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class NotificacionEntregadaActivity extends BaseActivity implements View.OnClickListener{

    Lienzo mLienzo;
    Toolbar mToolbar;
    Button btn_guardar;
    DBHelper dbHelper;
    String referenciaPostal, referenciaPostalSCB, longitud, latitud, observaciones;
    Integer idNotificacion, posicionAdapter;
    Boolean esPrimerResultado;
    EditText edt_numeroDocumentoReceptor, edt_nombreReceptor;
    Spinner spinner_tipoDocumentoReceptor;
    Boolean numeroDocumentoValido;
    final  String[] listaTiposDocumento = new String[]{Util.TIPO_DOCUMENTO_NIF, Util.TIPO_DOCUMENTO_CIF, Util.TIPO_DOCUMENTO_NIE, Util.TIPO_DOCUMENTO_OTRO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_entregada);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Recupera laos datos de la notificacion
        referenciaPostal = getIntent().getStringExtra("referenciaPostal");
        referenciaPostalSCB = getIntent().getStringExtra("referenciaPostalSCB");
        idNotificacion = getIntent().getIntExtra("idNotificacion",0);
        posicionAdapter = getIntent().getIntExtra("posicionAdapter",0);
        longitud = getIntent().getStringExtra("longitud");
        latitud = getIntent().getStringExtra("latitud");
        observaciones = getIntent().getStringExtra("observaciones");
        esPrimerResultado = getIntent().getBooleanExtra("esPrimerResultado", Boolean.TRUE);

        numeroDocumentoValido = true;
        edt_numeroDocumentoReceptor = (EditText) findViewById(R.id.editText_notificacionEntregada_numeroDocumento);
        edt_numeroDocumentoReceptor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    numeroDocumentoValido = Util.validarNumeroDocumento(((EditText)view).getText().toString(), spinner_tipoDocumentoReceptor.getSelectedItem().toString());
                    if(!numeroDocumentoValido) {
                        ((EditText)view).setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape_error));
                    } else {
                        ((EditText)view).setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape));
                    }
                }
            }
        });
        edt_nombreReceptor = (EditText) findViewById(R.id.editText_notificacionEntregada_nombreApellidos);
        spinner_tipoDocumentoReceptor = (Spinner) findViewById(R.id.spinner_notificacionEntregada_tipoDocumento);
        spinner_tipoDocumentoReceptor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listaTiposDocumento));
        spinner_tipoDocumentoReceptor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Escuchador del item seleccionado del spinner para validar numero de documento
                if(edt_numeroDocumentoReceptor.getText().toString().length() > 0) {
                    numeroDocumentoValido = Util.validarNumeroDocumento(edt_numeroDocumentoReceptor.getText().toString(), ((Spinner)adapterView).getSelectedItem().toString());
                    if(!numeroDocumentoValido) {
                        edt_numeroDocumentoReceptor.setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape_error));
                    } else {
                        edt_numeroDocumentoReceptor.setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Se fuerza que el inputText se haga entero en mayusculas
        edt_numeroDocumentoReceptor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        edt_nombreReceptor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        mLienzo = (Lienzo) findViewById(R.id.lienzo_firma);
        mLienzo.setDrawingCacheEnabled(true);

        btn_guardar = (Button) findViewById(R.id.button_notif_entregada_guardar);
        btn_guardar.setOnClickListener(this);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_notificacion_entregada;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_notif_entregada_guardar) {

            if(numeroDocumentoValido) {

                // Primero se pone el fondo en su color original para validar posteriormente
                edt_nombreReceptor.setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape));
                if(StringUtils.isNotBlank(edt_nombreReceptor.getText().toString())) {

                    // Guarda la imagen firmada en el sistema de archivos
                    try {
                        mLienzo.setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape));
                        Bitmap bitmap = mLienzo.getDrawingCache();
                        File file = new File(Util.obtenerRutaFirmasReceptor(), referenciaPostal+"_"+StringUtils.defaultIfBlank(referenciaPostalSCB,"") + ".png");

                        try (FileOutputStream ostream = new FileOutputStream(file);) {

                            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                            ostream.close();

                            // Lanza en background el guardado de la notificacion entregada
                            GuardarNotificacionEntregadaTask guardarNotificacionEntregadaTask = new GuardarNotificacionEntregadaTask();
                            guardarNotificacionEntregadaTask.execute(file.getPath(), edt_nombreReceptor.getText().toString(), edt_numeroDocumentoReceptor.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // si no se ha introducido texto en el nombre receptor se pinta en rojo
                    edt_nombreReceptor.setBackground(ContextCompat.getDrawable(NotificacionEntregadaActivity.this, R.drawable.edit_text_shape_error));
                }
            }
        }
    }


    /**
     * Clase privada que se encarga de guardar el resultado en la base de datos, generar el xml
     * y generar el fichero de sello de tiempo
     */
    private class GuardarNotificacionEntregadaTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        Boolean guardadoNotificacionEnBD;

        @Override
        protected void onPreExecute() {
            guardadoNotificacionEnBD = false;
            progressDialog = ProgressDialog.show(NotificacionEntregadaActivity.this, getString(R.string.guardar), getString(R.string.guardando_datos_en_bd_interna));
        }

        @Override
        protected String doInBackground(String... args) {
            String fallo = "";

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String fechaHoraString = df.format(Calendar.getInstance().getTime());

            Notificacion notificacionAux = new Notificacion();
            notificacionAux.setId(idNotificacion);
            notificacionAux.setFirmaReceptor(args[0]);
            notificacionAux.setNombreReceptor(args[1]);
            notificacionAux.setNumDocReceptor(args[2]);

            // Dependiendo de si es una aplicación de oficina o no, el resultado entregado tiene un código u otro
            Boolean esAplicacionDeOficina = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, getBaseContext(), Boolean.class.getSimpleName());
            Resultado resultado = null;
            if(esAplicacionDeOficina) {
                resultado = dbHelper.obtenerResultado(Util.RESULTADO_ENTREGADO_OFICINA);
            } else {
                resultado = dbHelper.obtenerResultado(Util.RESULTADO_ENTREGADO);
            }

            if(esPrimerResultado) {

                notificacionAux.setDescResultado1(resultado.getDescripcion().toUpperCase());
                notificacionAux.setResultado1(resultado.getCodigo());
                notificacionAux.setFechaHoraRes1(fechaHoraString);
                notificacionAux.setLatitudRes1(latitud);
                notificacionAux.setLongitudRes1(longitud);
                notificacionAux.setObservacionesRes1(observaciones);
                notificacionAux.setNotificadorRes1(obtenerNombreNotificador());
                notificacionAux.setFirmaNotificadorRes1(Util.obtenerRutaFirmaNotificador()+File.separator+obtenerCodigoNotificador()+".png");
                notificacionAux.setSegundoIntento(!esPrimerResultado);
            } else {

                notificacionAux.setDescResultado2(resultado.getDescripcion().toUpperCase());
                notificacionAux.setResultado2(resultado.getCodigo());
                notificacionAux.setFechaHoraRes2(fechaHoraString);
                notificacionAux.setLatitudRes2(latitud);
                notificacionAux.setLongitudRes2(longitud);
                notificacionAux.setObservacionesRes2(observaciones);
                notificacionAux.setNotificadorRes2(obtenerNombreNotificador());
                notificacionAux.setFirmaNotificadorRes2(Util.obtenerRutaFirmaNotificador()+File.separator+obtenerCodigoNotificador()+".png");
                notificacionAux.setSegundoIntento(esPrimerResultado);
            }

            guardadoNotificacionEnBD = dbHelper.guardaResultadoNotificacion(notificacionAux);

            if(!guardadoNotificacionEnBD) {
                fallo = getString(R.string.error_guardar_en_bd);
            } else {
                notificacionAux = dbHelper.obtenerNotificacion(idNotificacion);
                File ficheroXML = null;
                try {
                    // Se genera el fichero XML
                    publishProgress(getString(R.string.generado_xml));
                    ficheroXML = Util.NotificacionToXML(notificacionAux, getBaseContext());

                    // Se realiza la llamada al servidor del sellado de tiempo y se genera el fichero de sello de tiempo
                    Boolean tsaActivo = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_ACTIVO, getBaseContext(), Boolean.class.getSimpleName());
                    if(BooleanUtils.isTrue(tsaActivo)) {
                        String tsaUrl = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_URL, getBaseContext(), String.class.getSimpleName());
                        String tsaUser = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_USER, getBaseContext(), String.class.getSimpleName());
                        TimeStampRequestParameters timeStampRequestParameters = null;
                        if (StringUtils.isNotBlank(tsaUser)) {
                            String tsaPassword = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_PASSWORD, getBaseContext(), String.class.getSimpleName());
                            timeStampRequestParameters = new TimeStampRequestParameters();
                            timeStampRequestParameters.setUser(tsaUser);
                            timeStampRequestParameters.setPassword(tsaPassword);
                        }
                        publishProgress(getString(R.string.generado_sello_de_tiempo));
                        TimeStamp t = TimeStamp.stampDocument(FileUtils.readFileToByteArray(ficheroXML), new URL(tsaUrl), timeStampRequestParameters, null);
                        Util.guardarFicheroSelloTiempo(notificacionAux, t.toDER());
                    }
                } catch (CiMobileException e) {
                    fallo = e.getError();
                } catch (IOException e) {
                    fallo = getString(R.string.error_lectura_fichero_xml);
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

            // Se cierra el dialogo de espera
            progressDialog.dismiss();
            // Se crea el dialogo de respuesta del guardado
            AlertDialog.Builder builder = new AlertDialog.Builder(NotificacionEntregadaActivity.this);
            builder.setTitle(R.string.guardado);

            if(fallo != null && !fallo.isEmpty()) {
                // Fallo al guardar
                if(guardadoNotificacionEnBD) {
                    // Añadir texto indicando que como no se ha generado ni el sello de tiempo ni el xml, esa notificacion
                    // debera realizarla en papel
                    fallo += getString(R.string.realizar_notif_en_papel);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            Intent intentResultado = new Intent();
                            intentResultado.putExtra("posicionAdapter", posicionAdapter);
                            intentResultado.putExtra("idNotificacion", idNotificacion);
                            setResult(CommonStatusCodes.SUCCESS, intentResultado);
                            dialogInterface.dismiss();
                            finish();
                        }
                    });

                } else {
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    });
                }

                builder.setMessage(fallo);
            } else {
                // Guardado y generado correctamente
                builder.setMessage(R.string.notificacion_grabada_correctamente);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent intentResultado = new Intent();
                        intentResultado.putExtra("posicionAdapter", posicionAdapter);
                        intentResultado.putExtra("idNotificacion", idNotificacion);
                        setResult(CommonStatusCodes.SUCCESS, intentResultado);
                        dialogInterface.dismiss();
                        finish();
                    }
                });
            }

            // Crear el dialogo con los parametros que se han definido y se muestra por pantalla
            builder.show();
        }
    }
}
