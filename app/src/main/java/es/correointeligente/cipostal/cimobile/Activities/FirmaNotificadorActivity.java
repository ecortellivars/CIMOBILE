package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Lienzo;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class FirmaNotificadorActivity extends BaseActivity implements View.OnClickListener{

    Toolbar mToolbar;
    Button btn_guardar;
    Lienzo mLienzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma_notificador);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_guardar = (Button) findViewById(R.id.btn_firma_notificador_guardar);
        btn_guardar.setOnClickListener(this);

        mLienzo = (Lienzo) findViewById(R.id.lienzo_firma_notificador);
        mLienzo.setDrawingCacheEnabled(true);
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_firma_notificador;
    }

    @Override
    public void onClick(View view) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(20);

        if(view.getId() == R.id.btn_firma_notificador_guardar) {

            // Se guarda la imagen firmada por el notificador
            mLienzo.setBackground(ContextCompat.getDrawable(FirmaNotificadorActivity.this, R.drawable.edit_text_shape));
            Bitmap bitmap = mLienzo.getDrawingCache();
            File file = new File(Util.obtenerRutaFirmaNotificador(), obtenerCodigoNotificador().trim() + ".png");

            try (FileOutputStream ostream = new FileOutputStream(file);) {

                bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                ostream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Se redirige a la pantalla de lista de notificaciones
            Intent i = new Intent(getBaseContext(), ListaNotificacionesActivity.class);
            startActivity(i);
            finish();
        }
    }

    // Gestión de los Iconos de la barra de herramientas
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
