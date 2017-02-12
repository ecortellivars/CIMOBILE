package es.correointeligente.cipostal.cimobile.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;

public class NuevaNotificacionActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Toolbar mToolbar;
    Integer idNotificacion;
    Integer posicionAdapter;
    DBHelper dbHelper;
    TextView tv_refPostal, tv_nombre, tv_direccion, tv_resultadoDetallePrimerIntento, tv_consejoSegundoIntento,
             tv_latitud, tv_longitud, tv_fechaDetallePrimerIntento;
    EditText edt_observaciones;
    Button btn_noEntregado, btn_entregado;
    LinearLayout ll_detallePrimerIntento, ll_botonera;
    String[] listaResultadosNoEntrega;
    List<Resultado> listaResultadosNoNotifica;
    int checkedItem;
    Notificacion notificacion;

    // Variables para la localizacion GPS
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingResult<LocationSettingsResult> result;
    private LocationSettingsRequest.Builder builder;
    private Location mLastLocation;
//    private final int REQUEST_LOCATION = 200;
//    private final int REQUEST_CHECK_SETTINGS = 300; Variables para la localizacion de la calle via google Maps


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
        posicionAdapter = getIntent().getIntExtra("posicionAdapter",0);

        // Mapeamos toda la vista del layout
        this.mapearVista();

        connectGPS();

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
                Intent intent = new Intent(NuevaNotificacionActivity.this, NotificacionEntregadaActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

                intent.putExtra("referenciaPostal", notificacion.getReferencia());
                intent.putExtra("idNotificacion", idNotificacion);
                intent.putExtra("posicionAdapter", posicionAdapter);
                intent.putExtra("latitud", tv_latitud.getText().toString());
                intent.putExtra("longitud", tv_longitud.getText().toString());
                intent.putExtra("observaciones", edt_observaciones.getText().toString());
                intent.putExtra("esPrimerResultado", (notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()));

                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        disconnectGPS();
        super.onStop();
    }

    private class CargarResultadosTask extends AsyncTask<Void, Void, List<Resultado>> {
        ProgressDialog progressDialog;

        @Override
        protected List<Resultado> doInBackground(Void... voids) {
            List<Resultado> listaResultados = dbHelper.obtenerResultadosNoNotifican();

            return listaResultados;
        }
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(NuevaNotificacionActivity.this, getMessageResources(R.string.cargando_notificacion), getMessageResources(R.string.espere_info_notificacion));
        }

        @Override
        protected void onPostExecute(List<Resultado> listaResultados) {
            listaResultadosNoNotifica = listaResultados;
            listaResultadosNoEntrega = new String[listaResultadosNoNotifica.size()];
            int index = 0;
            for (Resultado resultado : listaResultadosNoNotifica) {
                listaResultadosNoEntrega[index] = (String) resultado.getCodigo()+" "+resultado.getDescripcion().toUpperCase();
                index++;
            }

            progressDialog.dismiss();
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
            ll_detallePrimerIntento.setVisibility(View.INVISIBLE);
            ll_botonera.setVisibility(View.VISIBLE);

            if(notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento()) {
                // Se muestra el layout de información referente al primer resultado
                String horaApartirDe = null;
                String horaAntesDe = null;
                Boolean invalidarBotonera = Boolean.FALSE;
                try {
                    String fechaString = notificacion.getFechaHoraRes1();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = formatter.parse(fechaString);
                    Calendar calendarApartirDe = Calendar.getInstance();
                    Calendar calendarAntesDe = Calendar.getInstance();
                    calendarApartirDe.setTime(date);
                    calendarApartirDe.add(Calendar.HOUR, 3);
                    calendarAntesDe.setTime(date);
                    calendarAntesDe.add(Calendar.HOUR, -3);

                    DateFormat df = new SimpleDateFormat("HH:mm");
                    horaApartirDe = df.format(calendarApartirDe.getTime());
                    horaAntesDe = df.format(calendarAntesDe.getTime());

                    Calendar calendarParaComparaHoras = Calendar.getInstance();
                    calendarParaComparaHoras.setTime(date);

                    invalidarBotonera = !(calendarParaComparaHoras.after(calendarAntesDe) && calendarParaComparaHoras.before(calendarApartirDe));

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String consejoSegundoIntento = "El segundo intento de notificación debería ser antes de las " +horaAntesDe+
                                                " o después de las "+horaApartirDe;


                ll_detallePrimerIntento.setVisibility(View.VISIBLE);
                ll_botonera.setVisibility(invalidarBotonera ? View.INVISIBLE : View.VISIBLE);
                tv_resultadoDetallePrimerIntento.setText(notificacion.getResultado1()+" "+notificacion.getDescResultado1());
                tv_fechaDetallePrimerIntento.setText(notificacion.getFechaHoraRes1());
                tv_consejoSegundoIntento.setText(consejoSegundoIntento);
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
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String fechaHoraString = df.format(Calendar.getInstance().getTime());
                String codResultado = listaResultadosNoNotifica.get(checkedItem).getCodigo();
                String descResultado = listaResultadosNoNotifica.get(checkedItem).getDescripcion();

                if(notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()) {
                    notificacion.setFechaHoraRes1(fechaHoraString);
                    notificacion.setResultado1(codResultado);
                    notificacion.setDescResultado1(descResultado);
                    notificacion.setLatitudRes1(tv_latitud.getText().toString().trim().length() == 0 ? null : tv_latitud.getText().toString());
                    notificacion.setLongitudRes1(tv_longitud.getText().toString().trim().length() == 0 ? null : tv_longitud.getText().toString());
                    notificacion.setObservacionesRes1(edt_observaciones.getText().toString().trim().length() == 0 ? null : edt_observaciones.getText().toString());
                    notificacion.setNotificadorRes1(obtenerNombreNotificador());
                }else {
                    notificacion.setFechaHoraRes2(fechaHoraString);
                    notificacion.setResultado2(codResultado);
                    notificacion.setDescResultado2(descResultado);
                    notificacion.setLatitudRes2(tv_latitud.getText().toString().trim().length() == 0 ? null : tv_latitud.getText().toString());
                    notificacion.setLongitudRes2(tv_longitud.getText().toString().trim().length() == 0 ? null : tv_longitud.getText().toString());
                    notificacion.setObservacionesRes2(edt_observaciones.getText().toString().trim().length() == 0 ? null : edt_observaciones.getText().toString());
                    notificacion.setNotificadorRes2(obtenerNombreNotificador());
                }

                dbHelper.guardaResultadoNotificacion(notificacion);

                // Se cierra el dialogo del filtrado
                dialogInterface.dismiss();

                Intent intentResultado = new Intent();
                intentResultado.putExtra("posicionAdapter", posicionAdapter);
                intentResultado.putExtra("idNotificacion", idNotificacion);
                setResult(CommonStatusCodes.SUCCESS, intentResultado);
                finish();
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

    public void connectGPS() {
        // Se inicializa el cliente Api de Google
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext()).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    public void disconnectGPS() {
            mGoogleApiClient.disconnect();
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
                final LocationSettingsStates mState = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                tv_latitud.setText(Double.toString(mLastLocation.getLatitude()));
                                tv_longitud.setText(Double.toString(mLastLocation.getLongitude()));
//                             getAddressFromLocation(mLastLocation, getApplicationContext(), new NuevaNotificacionActivity.GeoCoderHandler());*
                            }
                        } else {
                            // La aplicacion no tiene los permisos concedidos, por lo que se le solicita al usuario si lo permite
                            // TODO: hay que devolver el resultado denegado y desde la pantalla del activity hacer el request de los permisos
                            // ActivityCompat.requestPermissions(NuevaNotificacionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                       /* try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(NuevaNotificacionActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }*/
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mLastLocation != null) {
            tv_latitud.setText(Double.toString(mLastLocation.getLatitude()));
            tv_longitud.setText(Double.toString(mLastLocation.getLongitude()));
            //getAddressFromLocation(mLastLocation, getApplicationContext(), new NuevaNotificacionActivity.GeoCoderHandler());
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

}
