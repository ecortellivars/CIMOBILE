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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    TextView tv_refPostal, tv_nombre, tv_direccion, tv_resultadoDetallePrimerIntento, tv_consejoSegundoIntento,
            tv_latitud, tv_longitud, tv_fechaDetallePrimerIntento, tv_refSCB;
    EditText edt_observaciones;
    Button btn_noEntregado, btn_entregado;
    LinearLayout ll_detallePrimerIntento, ll_botonera;
    String[] listaResultadosNoEntrega;
    List<Resultado> listaResultadosNoNotifica, listaResultados, listaResultadosNoNotificaEliminar;
    int checkedItem;
    Notificacion notificacion;
    String codigoNotificador;
    Integer intentoGuardado = null;

    // Variables para la localizacion GPS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    private LocationSettingsRequest.Builder builder;
    private Location mLastLocation;
    //    private final int REQUEST_LOCATION = 200;
//    private final int REQUEST_CHECK_SETTINGS = 300; Variables para la localizacion de la calle via google Maps
    private static int TAKE_PICTURE = 1;
    private static int SELECT_PICTURE = 2;
    private String name = "";
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

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Se recupera el valor que se nos ha pasado desde la lista de notificaciones
        idNotificacion = getIntent().getIntExtra("idNotificacion", 0);
        posicionAdapter = getIntent().getIntExtra("posicionAdapter", 0);
        codigoNotificador = sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, "");
        btnActualizar = (ToggleButton) findViewById(R.id.btnActualizar);
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
        ll_detallePrimerIntento = (LinearLayout) findViewById(R.id.linearLayout_detalle_primer_intento);
        ll_botonera = (LinearLayout) findViewById(R.id.linearLayout_nueva_notificacion_botonera);
        tv_refPostal = (TextView) findViewById(R.id.textView_nuevaNotificacion_refPostal);
        tv_refSCB = (TextView) findViewById(R.id.textView_nuevaNotificacion_refSCB);
        tv_nombre = (TextView) findViewById(R.id.textView_nuevaNotificacion_nombre);
        tv_direccion = (TextView) findViewById(R.id.textView_nuevaNotificacion_direccion);
        tv_latitud = (TextView) findViewById(R.id.textView_nuevaNotificacion_latitud);
        tv_longitud = (TextView) findViewById(R.id.textView_nuevaNotificacion_longitud);
        tv_resultadoDetallePrimerIntento = (TextView) findViewById(R.id.textView_nuevaNotificacion_resultadoDetalle_primerIntento);
        tv_fechaDetallePrimerIntento = (TextView) findViewById(R.id.textView_nuevaNotificacion_resultadoDetalle_primerIntento_fecha);
        tv_consejoSegundoIntento = (TextView) findViewById(R.id.textView_nuevaNotificacion_consejo_segundo_intento);
        edt_observaciones = (EditText) findViewById(R.id.editText_nuevaNotificacion_observaciones);
        btn_entregado = (Button) findViewById(R.id.button_nueva_notificacion_noEntregado);
        btn_entregado.setOnClickListener(this);
        btn_noEntregado = (Button) findViewById(R.id.button_nueva_notificacion_entregado);
        btn_noEntregado.setOnClickListener(this);
        btnActualizar = (ToggleButton) findViewById(R.id.btnActualizar);
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

            tv_refPostal.setText(notificacion.getReferencia().toString());
            tv_refSCB.setText(notificacion.getReferenciaSCB().toString());
            tv_nombre.setText(notificacion.getNombre().toString());
            tv_direccion.setText(notificacion.getDireccion().toString());
            ll_detallePrimerIntento.setVisibility(View.INVISIBLE);
            ll_botonera.setVisibility(View.VISIBLE);

            // Si requiere segundo intento lo muestro
            if (BooleanUtils.isTrue(notificacion.getSegundoIntento())) {
                // Se muestra el layout de información referente al primer resultado
                String horaApartirDe = null;
                String horaAntesDe = null;
                String diaLimite = null;
                Boolean invalidarBotonera = Boolean.FALSE;
                try {

                    Integer numHoras = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_HORAS, getBaseContext(), Integer.class.getSimpleName());
                    numHoras = numHoras == null ? 3 : numHoras; // en caso de no obtener el valor de las preferencias, por defecto son 3
                    Integer numDias = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_SIGUIENTE_VISITA_DIAS, getBaseContext(), Integer.class.getSimpleName());
                    numDias = numDias == null ? 3 : numDias; // en caso de no obtener el valor de las preferencias, por defecto son 3

                    String fechaString = notificacion.getFechaHoraRes1();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = formatter.parse(fechaString);
                    Calendar calendarApartirDe = Calendar.getInstance();
                    Calendar calendarAntesDe = Calendar.getInstance();
                    Calendar calendarDiaLimite = Calendar.getInstance();
                    calendarApartirDe.setTime(date);
                    calendarApartirDe.add(Calendar.HOUR, numHoras);
                    calendarAntesDe.setTime(date);
                    calendarAntesDe.add(Calendar.HOUR, -numHoras);
                    calendarDiaLimite.setTime(date);
                    calendarDiaLimite.add(Calendar.DATE, numDias);
                    calendarDiaLimite.add(Calendar.HOUR, numHoras);

                    DateFormat df = new SimpleDateFormat("HH:mm");
                    horaApartirDe = df.format(calendarApartirDe.getTime());
                    horaAntesDe = df.format(calendarAntesDe.getTime());

                    Calendar calendarParaComparaHoras = Calendar.getInstance();
                    calendarParaComparaHoras.setTime(date);

                    df = new SimpleDateFormat("dd/MM/yyyy");
                    diaLimite = df.format(calendarDiaLimite.getTime());

                    //invalidarBotonera = !(calendarParaComparaHoras.after(calendarAntesDe) &&
                    //calendarParaComparaHoras.before(calendarApartirDe) &&
                    //calendarDiaLimite.after(Calendar.getInstance()));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!notificacion.getResultado1().equals(Util.RESULTADO_AUSENTE_SEGUNDO) && !notificacion.getResultado1().equals(Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO)) {
                    String consejoSegundoIntento = getString(R.string.dia_limite) + " " + diaLimite + "\n" + getString(R.string.informacion_segundo_intento_1) + " " + horaAntesDe + " " + getString(R.string.informacion_segundo_intento_2) + " " + horaApartirDe;
                    ll_detallePrimerIntento.setVisibility(View.VISIBLE);
                    ll_botonera.setVisibility(invalidarBotonera ? View.INVISIBLE : View.VISIBLE);
                    tv_resultadoDetallePrimerIntento.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                    tv_fechaDetallePrimerIntento.setText(notificacion.getFechaHoraRes1());
                    tv_consejoSegundoIntento.setText(consejoSegundoIntento);
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
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String fechaHoraString = df.format(Calendar.getInstance().getTime());
                    String codResultado = listaResultadosNoNotifica.get(checkedItem).getCodigo();
                    String descResultado = listaResultadosNoNotifica.get(checkedItem).getDescripcion();

                    // Fecha para dar nombre a la imagen en base de datos y al fichero JPG
                    DateFormat df2 = new SimpleDateFormat("yyyyMMdd");

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
            // Primero guarda el resultado de notificacion y recupera todos los datos para generar el fichero xml
            intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);
            if(intentoGuardado == null) {
                fallo = getString(R.string.error_guardar_en_bd)   ;
                } else if (intentoGuardado == 0) {
                    fallo = getString(R.string.error_guardar_en_bd_localizacion)   ;
                        } else {
                                notificacion = dbHelper.obtenerNotificacion(idNotificacion);
                                File ficheroXML = null;
                                try {
                                    // Se genera el fichero XML
                                    publishProgress(getString(R.string.generado_xml));
                                    ficheroXML = Util.NotificacionToXML(notificacion, getBaseContext());

                                    // Se realiza la llamada al servidor del sellado de tiempo y se genera el fichero de sello de tiempo
                                    Boolean tsaActivo = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_ACTIVO, getBaseContext(), Boolean.class.getSimpleName());
                                    if(BooleanUtils.isTrue(tsaActivo)) {
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
                                        TimeStamp t = TimeStamp.stampDocument(FileUtils.readFileToByteArray(ficheroXML), new URL(tsaUrl), timeStampRequestParameters, null);
                                        Util.guardarFicheroSelloTiempo(notificacion, t.toDER());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(NuevaNotificacionActivity.this);
            // Si hubo fallo en el XML y en el SELLO de TIEMPO
            if(fallo != null && !fallo.isEmpty()) {
                // Fallo al guardar
                if(intentoGuardado == null) {
                    builder.setTitle(R.string.no_guardado);
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
                    builder.setTitle(R.string.no_guardado);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
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
                        mGoogleApiClient = null;
                        finish();
                    }
                });
            }
            // Crear el dialogo con los parametros que se han definido y se muestra por pantalla
            builder.show();
        }
    }
}
