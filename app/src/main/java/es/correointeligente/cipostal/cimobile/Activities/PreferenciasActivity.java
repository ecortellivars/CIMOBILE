package es.correointeligente.cipostal.cimobile.Activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class PreferenciasActivity extends BaseActivity {
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instanciamos la pantalla de preferencias
        setContentView(R.layout.activity_preferencias);

        // Instanciamos la barra de herramientas
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Damos funcionalidad a la la barra de herramientas
        setSupportActionBar(mToolbar);
        // No mostrar el título de la Activity (obligatorio si está junto con el icono)
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Muestra la flecha para ir a la actividad anterior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Inicia la transacción al fragment que contiene el xml de preferencias
        getFragmentManager().beginTransaction().replace(R.id.fragment_preferencias_container, new PrefFragment()).commit();
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_preferencias;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Clase publica que carga el fichero de preferencias con la codificación inicial que se hace en UTIL
     */
    public static class PrefFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            PreferenceManager prefMgr = getPreferenceManager();
            // Obtenemos las preferencias con los datos incluidos
            prefMgr.setSharedPreferencesName(Util.FICHERO_PREFERENCIAS_APP);
            // El modo de operación del archivo puede ser:
                // MODE_PRIVATE solo la aplicación puede acceder al archivo de preferencias.
                // MODE_WORLD_READABLE otras aplicaciones pueden consultar el archivo de preferencias
                // MODE_WORLD_WRITEABLE otras aplicaciones pueden consultar y modificar el archivo.
                // MODE_MULTI_PROCESS varios procesos pueden acceder (Requiere Android 2.3)
            prefMgr.setSharedPreferencesMode(MODE_PRIVATE);

            addPreferencesFromResource(R.xml.preferencias);

        }
    }
}
