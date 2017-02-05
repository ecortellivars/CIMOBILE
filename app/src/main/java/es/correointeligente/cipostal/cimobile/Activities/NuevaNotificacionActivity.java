package es.correointeligente.cipostal.cimobile.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;
import es.correointeligente.cipostal.cimobile.Util.GPSHelper;

public class NuevaNotificacionActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    String refPostal;
    DBHelper dbHelper;
    TextView tv_refPostal, tv_nombre, tv_direccion, tv_resultadoDetallePrimerIntento, tv_consejoSegundoIntento;
    Button btn_noEntregado, btn_entregado;
    LinearLayout ll_detallePrimerIntento;
    String[] listaResultadosNoEntrega;
    int checkedItem;
    Notificacion notificacion;


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
        refPostal = getIntent().getStringExtra("refPostal");

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
            Notificacion notificacion = dbHelper.obtenerNotificacion(refPostal);

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
                switch (checkedItem) {
                    case 0: codResultado = "02";
                        break;
                    case 1: codResultado = "03";
                        break;
                    case 2: codResultado = "04";
                        break;
                    case 3: codResultado = "05";
                        break;
                    case 4: codResultado = "06";
                        break;
                    case 5: codResultado = "07";
                        break;
                }

                if(notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()) {
                    notificacion.setFechaHoraRes1(fechaHoraString);
                    notificacion.setResultado1(codResultado);
                }else {
                    notificacion.setFechaHoraRes2(fechaHoraString);
                    notificacion.setResultado2(codResultado);
                }

                notificacion.setLatitud("");
                notificacion.setLongitud("");

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
        // Inicializamos la clase Singleton para la gestion FTP
        GPSHelper gpsHelper = GPSHelper.getInstancia();
        gpsHelper.connect(getBaseContext());

        super.onStart();
    }

    @Override
    protected void onStop() {
        GPSHelper gpsHelper = GPSHelper.getInstancia();
        gpsHelper.disconnect();

        super.onStop();
    }

    /** METODOS PARA OBTENER LA LOCALIZACION GPS
   @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
                        if (ActivityCompat.checkSelfPermission(NuevaNotificacionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NuevaNotificacionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(NuevaNotificacionActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        } else {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            if (mLastLocation != null) {
                                latitud.setText(String.valueOf(mLastLocation.getLatitude()));
                                longitud.setText(String.valueOf(mLastLocation.getLongitude()));
                                getAddressFromLocation(mLastLocation, getApplicationContext(), new GeoCoderHandler());
                            }
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
            latitud.setText(String.valueOf(mLastLocation.getLatitude()));
            longitud.setText(String.valueOf(mLastLocation.getLongitude()));
            getAddressFromLocation(mLastLocation, getApplicationContext(), new GeoCoderHandler());
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getCountryName();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }
    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }
            ciudad.setText(result);
        }
    }**/

}
