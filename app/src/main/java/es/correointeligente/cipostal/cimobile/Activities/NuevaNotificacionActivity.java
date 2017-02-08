package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.GPSHelper;

public class NuevaNotificacionActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    Integer idNotificacion;
    DBHelper dbHelper;
    TextView tv_refPostal, tv_nombre, tv_direccion, tv_resultadoDetallePrimerIntento, tv_consejoSegundoIntento;
    TextView tv_latitud, tv_longitud;
    Button btn_noEntregado, btn_entregado;
    LinearLayout ll_detallePrimerIntento;
    String[] listaResultadosNoEntrega;
    int checkedItem;
    Notificacion notificacion;
    GPSHelper gpsHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_notificacion);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Se recupera el valor que se nos ha pasado desde la lista de notificaciones
        idNotificacion = getIntent().getIntExtra("idNotificacion",0);

        // Mapeamos toda la vista del layout
        this.mapearVista();

        // Recupera la lista de los resultados de no entrega
        listaResultadosNoEntrega = getResources().getStringArray(R.array.resultados_no_entrega);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        // Lanza en background las consultas para rellenar la vista
        CargarDetalleNotificacionTask cargarDetalleNotificacionTask = new CargarDetalleNotificacionTask();
        cargarDetalleNotificacionTask.execute();
    }



    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_nueva_notificacion;
    }

    private void mapearVista() {
        ll_detallePrimerIntento = (LinearLayout) findViewById(R.id.linearLayout_detalle_primer_intento);
        tv_refPostal = (TextView) findViewById(R.id.textView_nuevaNotificacion_refPostal);
        tv_nombre = (TextView) findViewById(R.id.textView_nuevaNotificacion_nombre);
        tv_direccion = (TextView) findViewById(R.id.textView_nuevaNotificacion_direccion);
        tv_latitud = (TextView) findViewById(R.id.textView_nuevaNotificacion_latitud);
        tv_longitud = (TextView) findViewById(R.id.textView_nuevaNotificacion_longitud);
        tv_resultadoDetallePrimerIntento = (TextView) findViewById(R.id.textView_nuevaNotificacion_resultadoDetalle_primerIntento);
        tv_consejoSegundoIntento = (TextView) findViewById(R.id.textView_nuevaNotificacion_consejo_segundo_intento);
        btn_entregado = (Button) findViewById(R.id.button_nueva_notificacion_noEntregado);
        btn_entregado.setOnClickListener(this);
        btn_noEntregado = (Button) findViewById(R.id.button_nueva_notificacion_entregado);
        btn_noEntregado.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(20);

        switch (view.getId()) {
            case R.id.button_nueva_notificacion_noEntregado:
                this.crearSelectorNoEntregado();
                break;
            case R.id.button_nueva_notificacion_entregado:
                Intent intent = new Intent(getBaseContext(), NotificacionEntregadaActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class CargarDetalleNotificacionTask extends AsyncTask<Void, Void, Notificacion> {
        ProgressDialog progressDialog;

        @Override
        protected Notificacion doInBackground(Void... voids) {
            Notificacion notificacion = dbHelper.obtenerNotificacion(idNotificacion);

            return notificacion;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NuevaNotificacionActivity.this, getMessageResources(R.string.cargando_notificacion), getMessageResources(R.string.espere_info_notificacion));
        }

        @Override
        protected void onPostExecute(Notificacion notificacionAux) {

            notificacion = notificacionAux;

            tv_refPostal.setText(notificacion.getReferencia().toString());
            tv_nombre.setText(notificacion.getNombre().toString());
            tv_direccion.setText(notificacion.getDireccion().toString());
            tv_longitud.setText(GPSHelper.getInstancia().getLongitud());
            tv_latitud.setText(GPSHelper.getInstancia().getLatitud());
            if(notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento()) {
                ll_detallePrimerIntento.setVisibility(View.VISIBLE);
                String detallePrimerIntento = notificacion.getResultado1()+" "+notificacion.getFechaHoraRes1();
                String consejoSegundoIntento = "El segundo intento de notificación debería ser antes de las " +notificacion.getFechaHoraRes1()+
                                                " o después de las "+notificacion.getFechaHoraRes1();
                tv_resultadoDetallePrimerIntento.setText(detallePrimerIntento);
                tv_consejoSegundoIntento.setText(notificacion.getReferencia().toString());
            }

            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Crea el dialogo (popUp) para seleccionar el filtrado y así mostrar solo las notificaciones por el criterio seleccionado
     */
    private void crearSelectorNoEntregado() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(NuevaNotificacionActivity.this);
        mBuilder.setTitle(R.string.motivo_no_entrega);
        mBuilder.setSingleChoiceItems(listaResultadosNoEntrega, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int posicion) {
                        checkedItem = posicion;
                    }
                });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String fechaHoraString = df.format(Calendar.getInstance().getTime());
                String codResultado = null;
                String descResultado = null;
                switch (checkedItem) {
                    case 0: codResultado = "02";
                            descResultado = "DIR. INCORRECTA";
                        break;
                    case 1: codResultado = "03";
                            descResultado = "AUSENTE";
                        break;
                    case 2: codResultado = "04";
                            descResultado = "DESCONOCIDO";
                        break;
                    case 3: codResultado = "05";
                            descResultado = "FALLECIDO";
                        break;
                    case 4: codResultado = "06";
                            descResultado = "REHUSADO";
                        break;
                    case 5: codResultado = "07";
                        descResultado = "NADIE SE HACE CARGO";
                        break;
                }

                if(notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()) {
                    notificacion.setFechaHoraRes1(fechaHoraString);
                    notificacion.setResultado1(codResultado);
                    notificacion.setDescResultado1(descResultado);
                    notificacion.setLatitudRes1(GPSHelper.getInstancia().getLatitud());
                    notificacion.setLongitudRes1(GPSHelper.getInstancia().getLongitud());
                }else {
                    notificacion.setFechaHoraRes2(fechaHoraString);
                    notificacion.setResultado2(codResultado);
                    notificacion.setDescResultado2(descResultado);
                    notificacion.setLatitudRes2(GPSHelper.getInstancia().getLatitud());
                    notificacion.setLongitudRes2(GPSHelper.getInstancia().getLongitud());
                }

                dbHelper.guardaResultadoNotificacion(notificacion);

                // Se cierra el dialogo del filtrado
                dialogInterface.dismiss();

                NuevaNotificacionActivity.this.finish();
            }
        });

        mBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    protected void onStart() {
        // Inicializamos la clase Singleton para la gestion GPS
        gpsHelper = GPSHelper.getInstancia();
        gpsHelper.connect(getBaseContext());

        super.onStart();
    }

    @Override
    protected void onStop() {
        GPSHelper gpsHelper = GPSHelper.getInstancia();
        gpsHelper.disconnect();

        super.onStop();
    }
}
