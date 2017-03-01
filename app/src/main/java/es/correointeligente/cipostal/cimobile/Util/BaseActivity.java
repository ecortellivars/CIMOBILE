package es.correointeligente.cipostal.cimobile.Util;

import android.content.SharedPreferences;
import android.os.Bundle;
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

    public String obtenerNombreNotificador() { return sp.getString(Util.CLAVE_SESION_NOTIFICADOR, ""); }
    public String obtenerDelegacion() {
        return sp.getString(Util.CLAVE_SESION_DELEGACION, "");
    }
    public String obtenerCodigoNotificador() { return sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, ""); }
}
