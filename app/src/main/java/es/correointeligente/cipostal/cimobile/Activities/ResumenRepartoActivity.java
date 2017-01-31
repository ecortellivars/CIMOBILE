package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.correointeligente.cipostal.cimobile.Model.ResumenReparto;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;

public class ResumenRepartoActivity extends BaseActivity implements View.OnClickListener {
    Toolbar mToolbar;
    DBHelper dbHelper;
    Button mCerrarReparto;

    TextView tv_totFicheros, tv_totRemesas, tv_totNotificaciones, tv_totNotifGestionadas, tv_totNotifMarcadas;
    TextView tv_entregado, tv_dirIncorrecta, tv_ausente, tv_desconocido, tv_fallecido, tv_rehusado, tv_noSeHaceCargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_reparto);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        this.mapearVistaTextViews();

        // Lanza en background las consultas para rellenar la vista
        CargaResumenTask cargaResumenTask = new CargaResumenTask();
        cargaResumenTask.execute();
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_resumen_reparto;
    }

    @Override
    public void onClick(View view) {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(20);

        switch (view.getId()) {
            case R.id.button_resumen_cerrar_reparto:
                break;
            default:
                break;
        }
    }

    private void mapearVistaTextViews() {
        tv_totFicheros = (TextView) findViewById(R.id.textView_resumen_total_ficheros_value);
        tv_totRemesas = (TextView) findViewById(R.id.textView_resumen_total_remesas_value);
        tv_totNotificaciones = (TextView) findViewById(R.id.textView_resumen_total_notificaciones_value);
        tv_totNotifGestionadas = (TextView) findViewById(R.id.textView_resumen_total_notif_gestionadas_value);
        tv_totNotifMarcadas = (TextView) findViewById(R.id.textView_resumen_total_notif_marcadas_value);

        tv_entregado = (TextView) findViewById(R.id.textView_resumen_entregado_value);
        tv_dirIncorrecta = (TextView) findViewById(R.id.textView_resumen_dir_incorrecta_value);
        tv_ausente = (TextView) findViewById(R.id.textView_resumen_ausente_value);
        tv_desconocido = (TextView) findViewById(R.id.textView_resumen_desconocido_value);
        tv_fallecido = (TextView) findViewById(R.id.textView_resumen_fallecido_value);
        tv_rehusado = (TextView) findViewById(R.id.textView_resumen_rehusado_value);
        tv_noSeHaceCargo = (TextView) findViewById(R.id.textView_resumen_nadie_cargo_value);

        mCerrarReparto = (Button) findViewById(R.id.button_resumen_cerrar_reparto);
        mCerrarReparto.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class CargaResumenTask extends AsyncTask<Void, Void, ResumenReparto> {
        ProgressDialog progressDialog;

        @Override
        protected ResumenReparto doInBackground(Void... voids) {
            ResumenReparto resumen = dbHelper.obtenerResumenReparto();

            return resumen;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ResumenRepartoActivity.this, getMessageResources(R.string.resumen_reparto), getMessageResources(R.string.espere_info_reparto));
        }

        @Override
        protected void onPostExecute(ResumenReparto resumenReparto) {

            progressDialog.dismiss();

            tv_totFicheros.setText(resumenReparto.getTotFicheros().toString());
            tv_totRemesas.setText(resumenReparto.getTotRemesas().toString());
            tv_totNotificaciones.setText(resumenReparto.getTotNotificaciones().toString());
            tv_totNotifMarcadas.setText(resumenReparto.getTotNotifMarcadas().toString());
            tv_totNotifGestionadas.setText(resumenReparto.getTotNotifGestionadas().toString());

            tv_entregado.setText(resumenReparto.getNumEntregados().toString());
            tv_dirIncorrecta.setText(resumenReparto.getNumDirIncorrectas().toString());
            tv_ausente.setText(resumenReparto.getNumAusentes().toString());
            tv_desconocido.setText(resumenReparto.getNumDesconocidos().toString());
            tv_fallecido.setText(resumenReparto.getNumFallecidos().toString());
            tv_rehusado.setText(resumenReparto.getNumRehusados().toString());
            tv_noSeHaceCargo.setText(resumenReparto.getNumNadieSeHaceCargo().toString());
        }
    }
}
