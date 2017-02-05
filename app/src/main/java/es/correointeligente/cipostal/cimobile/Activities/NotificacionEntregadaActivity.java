package es.correointeligente.cipostal.cimobile.Activities;

import android.os.Bundle;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Lienzo;

public class NotificacionEntregadaActivity extends BaseActivity {

    Lienzo mLienzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_entregada);

        mLienzo = (Lienzo) findViewById(R.id.lienzo_firma);
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_notificacion_entregada;
    }
}
