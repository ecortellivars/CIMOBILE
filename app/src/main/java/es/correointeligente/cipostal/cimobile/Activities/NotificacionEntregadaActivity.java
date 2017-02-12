package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Lienzo;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class NotificacionEntregadaActivity extends BaseActivity implements View.OnClickListener{

    Lienzo mLienzo;
    Toolbar mToolbar;
    Button btn_guardar;
    DBHelper dbHelper;
    String referenciaPostal, longitud, latitud, observaciones;
    Integer idNotificacion, posicionAdapter;
    Boolean esPrimerResultado;
    EditText edt_numeroDocumentoReceptor, edt_nombreReceptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_entregada);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Recupera la referencia postal y el id
        referenciaPostal = getIntent().getStringExtra("referenciaPostal");
        idNotificacion = getIntent().getIntExtra("idNotificacion",0);
        posicionAdapter = getIntent().getIntExtra("posicionAdapter",0);
        longitud = getIntent().getStringExtra("longitud");
        latitud = getIntent().getStringExtra("latitud");
        observaciones = getIntent().getStringExtra("observaciones");
        esPrimerResultado = getIntent().getBooleanExtra("esPrimerResultado", Boolean.TRUE);

        edt_numeroDocumentoReceptor = (EditText) findViewById(R.id.editText_notificacionEntregada_numeroDocumento);
        edt_nombreReceptor = (EditText) findViewById(R.id.editText_notificacionEntregada_nombreApellidos);
        // Se fuerza que el inputText se haga entero en mayusculas
        edt_nombreReceptor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
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

            // Guarda la imagen firmada en el sistema de archivos
            try {

                Bitmap bitmap = mLienzo.getDrawingCache();
                File file = new File(getFilesDir(),referenciaPostal+".png");
                if(!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                ostream.close();

                // Lanza en background el guardado de la notificacion entregada
                GuardarNotificacionEntregadaTask guardarNotificacionEntregadaTask = new GuardarNotificacionEntregadaTask();
                guardarNotificacionEntregadaTask.execute(file.getPath(), edt_nombreReceptor.getText().toString(), edt_numeroDocumentoReceptor.getText().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GuardarNotificacionEntregadaTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected Boolean doInBackground(String... args) {

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String fechaHoraString = df.format(Calendar.getInstance().getTime());

            Notificacion notificacionAux = new Notificacion();
            notificacionAux.setId(idNotificacion);
            notificacionAux.setFirmaReceptor(args[0]);
            notificacionAux.setNombreReceptor(args[1]);
            notificacionAux.setNumDocReceptor(args[2]);

            // TODO: Cuando se implemente las preferencias de en oficia hay que distinguir
            // TODO: aqui entre "ENTREGADO" y "ENTREGADO OFICINA"
            Resultado resultado = dbHelper.obtenerResultado(Util.RESULTADO_ENTREGADO);

            if(esPrimerResultado) {

                notificacionAux.setDescResultado1(resultado.getDescripcion().toUpperCase());
                notificacionAux.setResultado1(resultado.getCodigo());
                notificacionAux.setFechaHoraRes1(fechaHoraString);
                notificacionAux.setLatitudRes1(latitud);
                notificacionAux.setLongitudRes1(longitud);
                notificacionAux.setObservacionesRes1(observaciones);
                notificacionAux.setNotificadorRes1(obtenerNombreNotificador());
            } else {
                notificacionAux.setDescResultado2(resultado.getDescripcion().toUpperCase());
                notificacionAux.setResultado2(resultado.getCodigo());
                notificacionAux.setFechaHoraRes2(fechaHoraString);
                notificacionAux.setLatitudRes2(latitud);
                notificacionAux.setLongitudRes2(longitud);
                notificacionAux.setObservacionesRes2(observaciones);
                notificacionAux.setNotificadorRes2(obtenerNombreNotificador());
            }

            return dbHelper.guardaResultadoNotificacion(notificacionAux);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NotificacionEntregadaActivity.this, getMessageResources(R.string.cargando_notificacion), getMessageResources(R.string.espere_info_notificacion));
        }

        @Override
        protected void onPostExecute(Boolean guardado) {

            progressDialog.dismiss();

            if(guardado) {

                Intent intentResultado = new Intent();
                intentResultado.putExtra("posicionAdapter", posicionAdapter);
                intentResultado.putExtra("idNotificacion", idNotificacion);
                setResult(CommonStatusCodes.SUCCESS, intentResultado);
                finish();
            }


        }
    }
}
