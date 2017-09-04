package es.correointeligente.cipostal.cimobile.Util;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.correointeligente.cipostal.cimobile.R;

public abstract class BaseActivity extends AppCompatActivity {

    public SharedPreferences sp;
    Toolbar toolbar;
    TextView mTextViewNotificador;
    TextView mTextViewFecha;
    TextView mTextViewDelegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResocurce());

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = this.getSharedPreferences(Util.FICHERO_PREFERENCIAS_SESION, MODE_PRIVATE);

        /** Dar permisos de escritura en la memoria */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
        /** Fin de permisos escritura en la memoria **/

        /** Dar permisos de camara */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.CAMERA},
                        2);
            }
        }
        /** Fin de permisos camara **/

        /** Dar permisos de GPS */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
        }
        /** Fin de permisos GPS **/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: { //Memoria
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case 2: { //Camara
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
            case 3: { //GPS
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }

    protected abstract int getLayoutResocurce();

    protected void setActionBarIcon(int iconRes) {
        toolbar.setNavigationIcon(iconRes);
    }

    protected void loadLayoutCurrentSession() {
        mTextViewNotificador = (TextView) findViewById(R.id.textView_notificador_value);
        mTextViewNotificador.setText(sp.getString(Util.CLAVE_SESION_NOTIFICADOR, ""));

        mTextViewDelegacion = (TextView) findViewById(R.id.textView_delegacion_value);
        mTextViewDelegacion.setText(sp.getString(Util.CLAVE_SESION_DELEGACION, ""));

        mTextViewFecha = (TextView) findViewById(R.id.textView_fecha_value);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        mTextViewFecha.setText(format.format(new Date()));
    }
    // DNI
    public String obtenerNombreNotificador() { return sp.getString(Util.CLAVE_SESION_NOTIFICADOR, ""); }
    public String obtenerDelegacion() {
        return sp.getString(Util.CLAVE_SESION_DELEGACION, "");
    }
    // DNI
    public String obtenerCodigoNotificador() { return sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, ""); }
}
