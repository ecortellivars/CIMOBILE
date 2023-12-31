package es.correointeligente.cipostal.cimobile.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.TSA.TimeStamp;
import es.correointeligente.cipostal.cimobile.TSA.TimeStampRequestParameters;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.CiMobileException;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class NuevaNotificacionActivity extends BaseActivity implements View.OnClickListener,
                            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Toolbar mToolbar;
    Integer idNotificacion;
    Integer posicionAdapter;
    DBHelper dbHelper;
    TextView tv_consejoLista, tv_refPostal, tv_nombre, tv_direccion, tv_resultadoDetallePrimerIntento, tv_consejoSegundoIntento,
             tv_latitud, tv_longitud, tv_fechaDetallePrimerIntento, tv_refSCB;
    EditText edt_observaciones;
    Button btn_noEntregado, btn_entregado;
    LinearLayout ll_detalleLista, ll_detallePrimerIntento, ll_botonera;
    String[] listaResultadosNoEntrega;
    List<Resultado> listaResultadosNoNotifica, listaResultados;
    int checkedItem;
    Notificacion notificacion;
    String codigoNotificador;
    Integer intentoGuardado = null;
    String consejoLista;

    // Variables para la localizacion GPS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    private LocationSettingsRequest.Builder builder;
    private Location mLastLocation;
    private String fallo = "";
    private static LocationRequest mLocRequest;
    private LocationListener listener;
    private Double latitud2 = 0.0;
    private Double longitud2 = 0.0;
    private ToggleButton btnActualizar;
    private Integer id = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_notificacion);

        mToolbar =  findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Se recupera el valor que se nos ha pasado desde la lista de notificaciones
        idNotificacion = getIntent().getIntExtra("idNotificacion", 0);
        posicionAdapter = getIntent().getIntExtra("posicionAdapter", 0);
        codigoNotificador = sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, "");
        btnActualizar =  findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLocationUpdates(btnActualizar.isChecked());
            }
        });

        // Mapeamos toda la vista del layout
        this.mapearVista();

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        // Lanza en background la consulta para obtener los resultados
        CargarResultadosTask cargarResultadosTask = new CargarResultadosTask();
        cargarResultadosTask.execute();

        // Lanza en background las consultas para rellenar la vista
        CargarDetalleNotificacionTask cargarDetalleNotificacionTask = new CargarDetalleNotificacionTask();
        cargarDetalleNotificacionTask.execute();
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_nueva_notificacion;
    }

    private void mapearVista() {
        ll_detallePrimerIntento =  findViewById(R.id.linearLayout_detalle_primer_intento);
        ll_botonera =  findViewById(R.id.linearLayout_nueva_notificacion_botonera);
        tv_refPostal =  findViewById(R.id.textView_nuevaNotificacion_refPostal);
        tv_refSCB =  findViewById(R.id.textView_nuevaNotificacion_refSCB);
        tv_nombre =  findViewById(R.id.textView_nuevaNotificacion_nombre);
        tv_direccion =  findViewById(R.id.textView_nuevaNotificacion_direccion);
        tv_latitud =  findViewById(R.id.textView_nuevaNotificacion_latitud);
        tv_longitud =  findViewById(R.id.textView_nuevaNotificacion_longitud);
        tv_resultadoDetallePrimerIntento =  findViewById(R.id.textView_nuevaNotificacion_resultadoDetalle_primerIntento);
        tv_fechaDetallePrimerIntento =  findViewById(R.id.textView_nuevaNotificacion_resultadoDetalle_primerIntento_fecha);
        tv_consejoSegundoIntento =  findViewById(R.id.textView_nuevaNotificacion_consejo_segundo_intento);
        edt_observaciones =  findViewById(R.id.editText_nuevaNotificacion_observaciones);
        btn_entregado =  findViewById(R.id.button_nueva_notificacion_entregado);
        btn_entregado.setOnClickListener(this);
        btn_noEntregado =  findViewById(R.id.button_nueva_notificacion_noEntregado);
        btn_noEntregado.setOnClickListener(this);
        btnActualizar =  findViewById(R.id.btnActualizar);
        tv_consejoLista =  findViewById(R.id.textView_nuevaNotificacion_consejo_lista);
        ll_detalleLista =  findViewById(R.id.linearLayout_detalle_lista);
    }

    @Override
    public void onClick(View view) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(20);

        switch (view.getId()) {
            // Todos los resultados incluyendo el ENTREGADO SIN FIRMA
            case R.id.button_nueva_notificacion_noEntregado:
                // Llamamos al Pop-Up de TODOS los resultados
                this.crearSelectorNoEntregado();
                break;

            // Solo ENTREGADO CON FIRMA
            case R.id.button_nueva_notificacion_entregado:
                Intent intent = new Intent(NuevaNotificacionActivity.this, NotificacionEntregadaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

                // Mandamos la informacion a la nueva pantallla
                intent.putExtra("referenciaPostal", notificacion.getReferencia());
                intent.putExtra("referenciaPostalSCB", notificacion.getReferenciaSCB());
                intent.putExtra("idNotificacion", idNotificacion);
                intent.putExtra("posicionAdapter", posicionAdapter);
                intent.putExtra("latitud", tv_latitud.getText().toString());
                intent.putExtra("longitud", tv_longitud.getText().toString());
                intent.putExtra("observaciones", edt_observaciones.getText().toString());
                intent.putExtra("notificador", codigoNotificador);
                intent.putExtra("resultado1", notificacion.getResultado1());
                intent.putExtra("destinatario", notificacion.getNombre());
                intent.putExtra("dirDestinatario", notificacion.getDireccion());

                String esCertificado = "";
                if (notificacion.getEsCertificado()) {
                    esCertificado = "1";
                } else {
                    esCertificado = "0";
                }
                intent.putExtra("esCertificado", esCertificado);
                String esLista = "";
                if (notificacion.getEsLista()) {
                    esLista = "1";
                } else {
                    esLista = "0";
                }
                intent.putExtra("esLista", esLista);

                startActivity(intent);

                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient = null;
        super.onStop();
    }

    /**
     * Clase privada que se ejecuta en segundo plano y sirve para cargar los resultados posibles de la notificacion
     */
    private class CargarResultadosTask extends AsyncTask<Void, Void, List<Resultado>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NuevaNotificacionActivity.this, getString(R.string.cargando_resultados), getString(R.string.espere_info_resultados));
        }

        @Override
        protected List<Resultado> doInBackground(Void... voids) {
            Boolean esAplicacionOficina = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, getBaseContext(), Boolean.class.getSimpleName());
            Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());

            Notificacion notificacion = dbHelper.obtenerNotificacion(idNotificacion);
            // Hay PRIMERA VISITA
            if (notificacion.getResultado1() != null) {
                if (esAplicacionOficina && notificacion.getEsLista()) {
                    listaResultados = dbHelper.obtenerResultadosEnOficina();
                }

                if (esAplicacionOficina && !notificacion.getEsLista()) {
                    listaResultados = null;
                }

                if (!esAplicacionOficina && !notificacion.getEsLista()) {
                    listaResultados = dbHelper.obtenerResultadosFinales();
                }

                // NO hay PRIMERA VISITA
            } else {
                if (esAplicacionOficina) {
                    listaResultados = null;
                } else if (esAplicacionPEE) {
                    listaResultados = dbHelper.obtenerResultadosNoFinalesPEE();
                } else {
                    listaResultados = dbHelper.obtenerResultadosNoFinales();
                }
            }
            return listaResultados;
        }

        @Override
        protected void onPostExecute(List<Resultado> listaResultados) {

            listaResultadosNoNotifica = listaResultados;
            if (listaResultados != null && !listaResultados.isEmpty()) {
                listaResultadosNoEntrega = new String[listaResultadosNoNotifica.size()];
                int index = 0;
                for (Resultado resultado : listaResultadosNoNotifica) {
                    listaResultadosNoEntrega[index] = (String) resultado.getCodigo() + " " + resultado.getDescripcion().toUpperCase();
                    index++;
                }
                progressDialog.dismiss();
            } else {
                btn_noEntregado.setVisibility(View.INVISIBLE);
                btn_entregado.setVisibility(View.INVISIBLE);
                fallo = getString(R.string.error_carga_resultados);
                progressDialog.dismiss();
            }
        }
    }

    /**
     * Clase privada que carga los datos de la notificacion a partir de su identificador
     */
    private class CargarDetalleNotificacionTask extends AsyncTask<Void, Void, Notificacion> {
        ProgressDialog progressDialog;

        @Override
        protected Notificacion doInBackground(Void... voids) {
            Notificacion notificacion = dbHelper.obtenerNotificacion(idNotificacion);

            return notificacion;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NuevaNotificacionActivity.this, getString(R.string.cargando_notificacion), getString(R.string.espere_info_notificacion));
        }

        @Override
        protected void onPostExecute(Notificacion notificacionAux) {

            notificacion = notificacionAux;

            tv_refPostal.setText(notificacion.getReferencia());
            tv_refSCB.setText(notificacion.getReferenciaSCB());
            tv_nombre.setText(notificacion.getNombre());
            tv_direccion.setText(notificacion.getDireccion());
            ll_detallePrimerIntento.setVisibility(View.INVISIBLE);
            ll_detalleLista.setVisibility(View.INVISIBLE);
            ll_botonera.setVisibility(View.VISIBLE);

            Boolean esAplicacionOficina = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_DE_OFICINA, getBaseContext(), Boolean.class.getSimpleName());
            // Si esta gestionando lista
            if (esAplicacionOficina) {
                // Se muestra el layout de información referente a la lista

                Integer numDiasNA = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_DIAS_NA_LISTA, getBaseContext(), Integer.class.getSimpleName());
                numDiasNA = numDiasNA == null ? 7 : numDiasNA; // en caso de no obtener el valor de las preferencias, por defecto son 7
                Integer numDiasCert = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_DIAS_CERTIFICADAS_LISTA, getBaseContext(), Integer.class.getSimpleName());
                numDiasCert = numDiasCert == null ? 10 : numDiasCert; // en caso de no obtener el valor de las preferencias, por defecto son 10

                if (notificacion.getFueraPlazoLista()) {
                    if (notificacion.getEsCertificado()) {
                        consejoLista = getString(R.string.dia_limite_cert) + " " + numDiasCert + "\n" +
                                getString(R.string.fecha_ultimo_resultado) + " " + notificacion.getFechaHoraRes1() + "\n" +
                                getString(R.string.informacion_lista);
                    } else {
                        consejoLista = getString(R.string.dia_limite_NA) + " " + numDiasNA + "\n" +
                                getString(R.string.fecha_ultimo_resultado) + " " + notificacion.getFechaHoraRes1() + "\n" +
                                getString(R.string.informacion_lista);
                    }
                    ll_detallePrimerIntento.setVisibility(View.INVISIBLE);
                    ll_detalleLista.setVisibility(View.VISIBLE);
                    tv_consejoLista.setText(consejoLista);
                    btn_noEntregado.setVisibility(View.INVISIBLE);
                }
            }else
                // Si requiere segundo intento lo muestro
                if (BooleanUtils.isTrue(notificacion.getSegundoIntento())) {
                    // Se muestra el layout de información referente al primer resultado
                    //String horaApartirDe = null;
                    //String horaAntesDe = null;
                    //String diaLimite = null;
                    Boolean invalidarBotonera = Boolean.FALSE;
                    Boolean mañana = Boolean.FALSE;
                    Boolean tarde = Boolean.FALSE;
                    //Calendar calendarDiaLimite = Calendar.getInstance();

                    //Integer numHoras = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS, getBaseContext(), Integer.class.getSimpleName());
                    //numHoras = numHoras == null ? 3 : numHoras; // en caso de no obtener el valor de las preferencias, por defecto son 3
                    //Integer numDias = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS, getBaseContext(), Integer.class.getSimpleName());
                    //numDias = numDias == null ? 3 : numDias; // en caso de no obtener el valor de las preferencias, por defecto son 3

                    String fechaString = notificacion.getFechaHoraRes1();
                    String horaStringSinBarra = fechaString.replace("/","");
                    String horaStringSinEspacio = horaStringSinBarra.replace(" ","");
                    String horaStringSinPuntos = horaStringSinEspacio.replace(":","");
                    String horaStringSub = horaStringSinPuntos.substring(8, 10);
                    switch (horaStringSub){
                        case "08":
                            mañana = Boolean.TRUE;
                            break;
                        case "09" :
                            mañana = Boolean.TRUE;
                            break;
                        case "10" :
                            mañana = Boolean.TRUE;
                            break;
                        case "11" :
                            mañana = Boolean.TRUE;
                            break;
                        case "12" :
                            mañana = Boolean.TRUE;
                            break;
                        case "13" :
                            mañana = Boolean.TRUE;
                            break;
                        case "14" :
                            mañana = Boolean.TRUE;
                            break;
                        case "15" :
                            tarde = Boolean.TRUE;
                            break;
                        case "16" :
                            tarde = Boolean.TRUE;
                            break;
                        case "17" :
                            tarde = Boolean.TRUE;
                            break;
                        case "18" :
                            tarde = Boolean.TRUE;
                            break;
                        case "19" :
                            tarde = Boolean.TRUE;
                            break;
                        case "20" :
                            tarde = Boolean.TRUE;
                            break;
                        case "21" :
                            tarde = Boolean.TRUE;
                            break;
                    }


                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    // Si estamos en la mañana y el primero fue por la mañana (NO PERMITIR)
                    if (currentHour >= 8 && currentHour <= 15 && mañana) {
                        invalidarBotonera = Boolean.TRUE;
                    }
                    // Si estamos en la mañana y el primero fue por la tarde  (PERMITIR)
                    else if (currentHour >= 8 && currentHour <= 15 && tarde) {
                        invalidarBotonera = Boolean.FALSE;
                    }
                    // Si estamos en la tarde y el primero fue por la tarde  (NO PERMITIR)
                    else if (currentHour >= 15 && currentHour <= 21 && tarde) {
                        invalidarBotonera = Boolean.TRUE;
                    }
                    // Si estamos en la tarde y el primero fue por la mañana (PERMITIR)
                    else if (currentHour >= 15 && currentHour <= 21 && mañana) {
                        invalidarBotonera = Boolean.FALSE;
                    }
                    // Resto
                    else {
                        invalidarBotonera = Boolean.TRUE;
                    }

                    if (!notificacion.getResultado1().equals(Util.RESULTADO_AUSENTE_SEGUNDO)
                     && !notificacion.getResultado1().equals(Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO)) {
                        String consejoSegundoIntento = "";
                        if (tarde){
                            consejoSegundoIntento = getString(R.string.informacion_segundo_intento_mañana) ;
                        }
                        else if (mañana){
                            consejoSegundoIntento = getString(R.string.informacion_segundo_intento_tarde);
                        } else {
                            consejoSegundoIntento = getString(R.string.informacion_intento_fuera_horario);
                        }


                        ll_detallePrimerIntento.setVisibility(View.VISIBLE);
                        ll_botonera.setVisibility(invalidarBotonera ? View.INVISIBLE : View.VISIBLE);
                        tv_resultadoDetallePrimerIntento.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                        tv_fechaDetallePrimerIntento.setText(notificacion.getFechaHoraRes1());
                        tv_consejoSegundoIntento.setText(consejoSegundoIntento);
                        btnActualizar.setVisibility(invalidarBotonera ? View.INVISIBLE : View.VISIBLE);
                    }
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
        if (fallo.equals("")) {
            mBuilder.setTitle(R.string.resultados_sin_firma);
            // Guardamos el resultado elegido por el usuario
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
                    // Fecha para base de datos
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String fechaHoraString = df.format(Calendar.getInstance().getTime());
                    String codResultado = listaResultadosNoNotifica.get(checkedItem).getCodigo();
                    String descResultado = listaResultadosNoNotifica.get(checkedItem).getDescripcion();

                    // Fecha para dar nombre a la imagen en base de datos y al fichero JPG
                    DateFormat df2 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                    // Preparamos la informacion si es Primer Intento
                    if (BooleanUtils.isFalse(notificacion.getSegundoIntento())
                     && BooleanUtils.isFalse(notificacion.getEsLista())) {
                        notificacion.setFechaHoraRes1(fechaHoraString);
                        notificacion.setResultado1(codResultado);
                        notificacion.setDescResultado1(descResultado);
                        notificacion.setLatitudRes1(tv_latitud.getText().toString().trim().length() == 0 ? "0" : tv_latitud.getText().toString());
                        notificacion.setLongitudRes1(tv_longitud.getText().toString().trim().length() == 0 ? "0" : tv_longitud.getText().toString());
                        notificacion.setObservacionesRes1(edt_observaciones.getText().toString().trim().length() == 0 ? "" : edt_observaciones.getText().toString());
                        notificacion.setNotificadorRes1(obtenerNombreNotificador());
                        notificacion.setFirmaNotificadorRes1(Util.obtenerRutaFirmaNotificador() + File.separator + obtenerCodigoNotificador() + ".png");
                        notificacion.setNombreReceptor("SIN RECEPTOR");
                        notificacion.setRelacionDestinatario("NO PROCEDE");
                    }
                    // Preparamos la informacion si es Segundo Intento
                    else {
                        notificacion.setFechaHoraRes2(fechaHoraString);
                        notificacion.setResultado2(codResultado);
                        notificacion.setDescResultado2(descResultado);
                        notificacion.setLatitudRes2(tv_latitud.getText().toString().trim().length() == 0 ? "0" : tv_latitud.getText().toString());
                        notificacion.setLongitudRes2(tv_longitud.getText().toString().trim().length() == 0 ? "0" : tv_longitud.getText().toString());
                        notificacion.setObservacionesRes2(edt_observaciones.getText().toString().trim().length() == 0 ? "" : edt_observaciones.getText().toString());
                        notificacion.setNotificadorRes2(obtenerNombreNotificador());
                        notificacion.setFirmaNotificadorRes2(Util.obtenerRutaFirmaNotificador() + File.separator + obtenerCodigoNotificador().trim() + ".png");
                        notificacion.setNombreReceptor("SIN RECEPTOR");
                        notificacion.setRelacionDestinatario("NO PROCEDE");
                        //notificacion.setSegundoIntento(false);
                    }

                    GuardarResultadoNegativoTask guardarResultadoNegativoTask = new GuardarResultadoNegativoTask();
                    guardarResultadoNegativoTask.execute();

                    // Se cierra el cuadro de dialogo de los resultados postales negativos
                    dialogInterface.dismiss();
                }
            });

            mBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            // Se construye el dialogo y se muestra por pantalla
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        } else {
            mBuilder.setTitle(R.string.sin_resultados_posibles);
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    // Se cierra el cuadro de dialogo de los resultados postales negativos
                    dialogInterface.dismiss();
                }
            });

            // Se construye el dialogo y se muestra por pantalla
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    }

    /**
     * Logica para el GPS
     */
    // Se inicializa el cliente Api de Google
    public void connectGPS() {
        if (mGoogleApiClient == null) {
            // Create a GoogleApiClient instance
            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext()).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(this /* FragmentActivity */,id,
                            this /* OnConnectionFailedListener */)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.disconnect();
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
                mLocationRequest = createLocationRequest();
                builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        //final LocationSettingsStates mState = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can initialize location requests here.
                                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                 && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                    if (mLastLocation != null){
                                        latitud2 = mLastLocation.getLatitude();
                                        longitud2 = mLastLocation.getLongitude();
                                    } else {
                                        latitud2 = 0.0;
                                        longitud2 = 0.0;
                                    }

                                    if (latitud2 != null) {
                                        tv_latitud.setText(Double.toString(latitud2));
                                        //getAddressFromLocation(mLastLocation, getApplicationContext(), new NuevaNotificacionActivity.GeoCoderHandler());
                                    } else {
                                        tv_latitud.setText("0");
                                        // La aplicacion no tiene los permisos concedidos, por lo que se le solicita al usuario si lo permite
                                        // ActivityCompat.requestPermissions(NuevaNotificacionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                    }
                                    if (longitud2 != null) {
                                        tv_longitud.setText(Double.toString(longitud2));
                                        //getAddressFromLocation(mLastLocation, getApplicationContext(), new NuevaNotificacionActivity.GeoCoderHandler());
                                    } else {
                                        // La aplicacion no tiene los permisos concedidos, por lo que se le solicita al usuario si lo permite
                                        // ActivityCompat.requestPermissions(NuevaNotificacionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                    }
                                    //if (mLastLocation != null) { onLocationChanged(mLastLocation); }

                                } else {
                                    // La aplicacion no tiene los permisos concedidos, por lo que se le solicita al usuario si lo permite
                                    // ActivityCompat.requestPermissions(NuevaNotificacionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                               try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(NuevaNotificacionActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way
                                // to fix the settings so we won't show the dialog.
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + status.getStatusCode());
                        }
                    }
                });
            }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setMaxWaitTime(1);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setExpirationDuration(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    private void toggleLocationUpdates(boolean enable) {
        if (enable) {
            enableLocationUpdates();
        } else {
            disableLocationUpdates();
        }
    }
    protected LocationRequest enableLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setMaxWaitTime(1);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setExpirationDuration(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        id = id + 1;
        connectGPS();
        return mLocationRequest;
    }

    private void disableLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        latitud2 = -0.0000000;
        longitud2 = 00.0000000;
        //mLastLocation.setLatitude(-0.0000000);
        //mLastLocation.setLongitude(00.0000000);
        mGoogleApiClient.disconnect();
        notificacion.setLatitudRes1("0");
        notificacion.setLongitudRes1("0");
        notificacion.setLatitudRes2("0");
        notificacion.setLongitudRes2("0");
        tv_latitud.setText("0");
        tv_longitud.setText("0");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
        id = id + 1;
        mGoogleApiClient = null;
    }

    /**
     * Clase privada que se ejecuta en background y se encarga de guardar la notificacion, generar el fichero XML y el sello de tiempo
     */
    private class GuardarResultadoNegativoTask extends AsyncTask<Void, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NuevaNotificacionActivity.this, getString(R.string.guardar), getString(R.string.guardando_datos_en_bd_interna));
        }

        /**
         * Logica para la certificacion de la ACCV
         * @param voids
         * @return
         */
        @Override
        protected String doInBackground(Void... voids) {
            String fallo = "";
            String ficheroST = "";

            if (notificacion.getLatitudRes1().equals("0") && notificacion.getLongitudRes1().equals("0")){
                    fallo = getString(R.string.error_guardar_en_bd_localizacion)   ;
                        } else {
                                File ficheroXML = null;
                                try {
                                    // Se genera el fichero XML
                                    publishProgress(getString(R.string.generado_xml));
                                    try {
                                        ficheroXML = Util.NotificacionToXML(notificacion, getBaseContext());
                                    }catch (CiMobileException e) {
                                        fallo = getString(R.string.problema_guardar_XML_realizar_notif_en_papel);
                                        // Elimina el fichero xml
                                        try {
                                            File file;
                                            String nombeFichero = notificacion.getReferencia() +  ".xml";
                                            file = new File(Util.obtenerRutaXML() + File.separator + nombeFichero);
                                            if (file.exists()) {
                                                file.delete();
                                                notificacion.setHayXML(Boolean.FALSE);
                                            }
                                        }catch (Exception e3) {
                                            e3.printStackTrace();
                                        }
                                    }

                                    if (ficheroXML != null) {
                                        notificacion.setHayXML(Boolean.TRUE);
                                        // Se realiza la llamada al servidor del sellado de tiempo y se genera el fichero de sello de tiempo
                                        Boolean tsaActivo = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_ACTIVO, getBaseContext(), Boolean.class.getSimpleName());
                                        if (BooleanUtils.isTrue(tsaActivo)) {
                                            publishProgress(getString(R.string.generado_sello_de_tiempo));
                                            String tsaUrl = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_URL, getBaseContext(), String.class.getSimpleName());
                                            String tsaUser = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_USER, getBaseContext(), String.class.getSimpleName());
                                            TimeStampRequestParameters timeStampRequestParameters = null;
                                            if (StringUtils.isNotBlank(tsaUser)) {
                                                String tsaPassword = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_PASSWORD, getBaseContext(), String.class.getSimpleName());
                                                timeStampRequestParameters = new TimeStampRequestParameters();
                                                timeStampRequestParameters.setUser(tsaUser);
                                                timeStampRequestParameters.setPassword(tsaPassword);
                                            }
                                            try{
                                                TimeStamp t = TimeStamp.stampDocument(FileUtils.readFileToByteArray(ficheroXML), new URL(tsaUrl), timeStampRequestParameters, null);
                                                ficheroST = Util.guardarFicheroSelloTiempo(notificacion, t.toDER());
                                            }catch (CiMobileException e2) {
                                                fallo = getString(R.string.problema_guardar_ST_realizar_notif_en_papel);
                                                // Elimina el fichero xml
                                                try {
                                                    File file;
                                                    String nombeFichero = notificacion.getReferencia() +  ".xml";
                                                    file = new File(Util.obtenerRutaXML() + File.separator + nombeFichero);
                                                    if (file.exists()) {
                                                        file.delete();
                                                        notificacion.setHayXML(Boolean.FALSE);
                                                    }
                                                }catch (Exception e3) {
                                                    e3.printStackTrace();
                                                }
                                            }

                                        }
                                    } else {
                                             notificacion.setHayXML(Boolean.FALSE);
                                            }

                                if (ficheroST != ""){
                                    notificacion.setHayST(Boolean.TRUE);
                                } else {
                                        notificacion.setHayST(Boolean.FALSE);
                                        // Elimina el fichero xml
                                        try {
                                            File file;
                                            String nombeFichero = notificacion.getReferencia() +  ".xml";
                                            file = new File(Util.obtenerRutaXML() + File.separator + nombeFichero);
                                            if (file.exists()) {
                                                file.delete();
                                                notificacion.setHayXML(Boolean.FALSE);
                                            }
                                        }catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }

                                if (!notificacion.getHayXML()){
                                    fallo = getString(R.string.problema_guardar_XML_realizar_notif_en_papel);
                                    } else if (!notificacion.getHayST()){
                                        fallo = getString(R.string.problema_guardar_ST_realizar_notif_en_papel);
                                        } else {
                                            intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);
                                            if (intentoGuardado == null) {
                                                fallo = getString(R.string.error_guardar_en_bd);
                                            }
                                        }

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
            AlertDialog.Builder builder = new AlertDialog.Builder(NuevaNotificacionActivity.this);
            // Si hubo fallo en el XML y en el SELLO de TIEMPO
            if(fallo != null && !fallo.isEmpty()) {
                if (notificacion.getHayXML().booleanValue() == false){
                    builder.setTitle(R.string.no_guardado);
                    // Añadir texto indicando que como no se ha generado ni el sello de tiempo ni el xml, esa notificacion
                    // debera realizarla en papel
                    fallo = getString(R.string.problema_guardar_XML_realizar_notif_en_papel) + " o " + fallo;
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            Intent intentResultado = new Intent();
                            intentResultado.putExtra("posicionAdapter", posicionAdapter);
                            intentResultado.putExtra("idNotificacion", idNotificacion);
                            setResult(CommonStatusCodes.ERROR, intentResultado);
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    } else if (notificacion.getHayST().booleanValue() == false){
                        builder.setTitle(R.string.no_guardado);
                        // Añadir texto indicando que como no se ha generado ni el sello de tiempo ni el xml, esa notificacion
                        // debera realizarla en papel
                        fallo += getString(R.string.problema_guardar_ST_realizar_notif_en_papel);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Intent intentResultado = new Intent();
                                intentResultado.putExtra("posicionAdapter", posicionAdapter);
                                intentResultado.putExtra("idNotificacion", idNotificacion);
                                setResult(CommonStatusCodes.ERROR, intentResultado);
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        } else
                        // Fallo al guardar
                        if(intentoGuardado == null) {
                            builder.setTitle(R.string.no_guardado);
                            // Añadir texto indicando que como no se ha generado ni el sello de tiempo ni el xml, esa notificacion
                            // debera realizarla en papel
                            fallo += getString(R.string.problema_guardar_realizar_notif_en_papel);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    Intent intentResultado = new Intent();
                                    intentResultado.putExtra("posicionAdapter", posicionAdapter);
                                    intentResultado.putExtra("idNotificacion", idNotificacion);
                                    setResult(CommonStatusCodes.ERROR, intentResultado);
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            });

                            } else  {
                                        builder.setTitle(R.string.guardado);
                                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                Intent intentResultado = new Intent();
                                                intentResultado.putExtra("posicionAdapter", posicionAdapter);
                                                intentResultado.putExtra("idNotificacion", idNotificacion);
                                                setResult(CommonStatusCodes.ERROR, intentResultado);
                                                dialogInterface.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                        builder.setMessage(fallo);
                        } else {
                            // Guardado y generado correctamente
                            builder.setTitle(R.string.guardado);
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
