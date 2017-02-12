package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Adapters.FicheroAdapter;
import es.correointeligente.cipostal.cimobile.Holders.FicheroViewHolder;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;

public class CargarRepartoActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    Toolbar mToolbar;
    ListView mlistView_cargar_reparto_ficheros;
    FicheroAdapter itemsAdapter;
    DBHelper dbHelper;
    FTPHelper ftpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_reparto);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        mlistView_cargar_reparto_ficheros = (ListView) findViewById(R.id.listView_cargar_reparto_ficheros);

        // Carga el layout comun de sesion actual
        this.loadLayoutCurrentSession();

        // Lanza una tarea en background para la conexión FTP
        FtpConnectionTask ftpConnectionTask = new FtpConnectionTask();
        ftpConnectionTask.execute();

    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_cargar_reparto;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ftpHelper.disconnect();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ftpHelper.disconnect();
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        final String nombreFicheroSeleccionado = ((FicheroViewHolder) adapterView.getItemAtPosition(position)).getNombreFichero();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cargar_fichero_sicer);
        builder.setMessage(R.string.esta_seguro_de_cargar_el_fichero_sicer+" "+ nombreFicheroSeleccionado + "?");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Lanza la tarea en background de la carga del fichero SICER
                CargarFicheroTask cargarFicheroTask = new CargarFicheroTask();
                cargarFicheroTask.execute(nombreFicheroSeleccionado);
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int wich) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private class FtpConnectionTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        protected Void doInBackground(Void... args) {
            try {

               // Inicializamos la clase Singleton para la gestion FTP
               ftpHelper = FTPHelper.getInstancia();
               if(ftpHelper != null && ftpHelper.connect()) {

                   if(ftpHelper.cargarCarpetaSICER()) {

                       List<FicheroViewHolder> listaFicheros = ftpHelper.obtenerFicherosDirectorio();
                       itemsAdapter = new FicheroAdapter(getBaseContext(), R.layout.item_fichero, listaFicheros);

                   } else {
                       // error cambio de carpeta
                   }

               } else {
                 // error de conexion
               }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CargarRepartoActivity.this, getString(R.string.conexion_ftp), getString(R.string.espere_conexion_servidor_ftp));
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ftpHelper != null && ftpHelper.isConnected()) {
                // Se ha creado la conexión correctamente
                mlistView_cargar_reparto_ficheros.setAdapter(itemsAdapter);
                mlistView_cargar_reparto_ficheros.setOnItemClickListener(CargarRepartoActivity.this);
            } else {
                // Ha fallado la conexión FTP
                AlertDialog.Builder builder = new AlertDialog.Builder(CargarRepartoActivity.this);
                builder.setTitle(R.string.conexion_ftp);
                builder.setMessage(getString(R.string.fallo_conexion_servidor_ftp));
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }

            progressDialog.dismiss();
        }
    }

    private class CargarFicheroTask extends AsyncTask<String, String, Boolean> {
        ProgressDialog progressDialog;

        protected Boolean doInBackground(String... args) {
            Boolean resultado = true;
            String nombreFicheroSeleccionado = args[0];

            if (nombreFicheroSeleccionado != null && ftpHelper.isConnected()) {
                // Se comprueba si ya se ha cargado con anterioridad ese fichero, en ese caso no se puede cargar nuevamente
                Boolean existeFichero = dbHelper.existeFichero(nombreFicheroSeleccionado);

                if (!existeFichero) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(ftpHelper.leerFichero(nombreFicheroSeleccionado)))) {

                        Integer numLinea = 1;
                        List<Notificacion> listaNotificaciones = new ArrayList<>();

                        Boolean esCargaPrimeraEntrega = Boolean.TRUE;
                        for (String linea = reader.readLine(); linea != null; linea = reader.readLine()) {

                            if (linea.startsWith("P")) { // DETALLE (nosotros usaremos "P" para primera entrega
                                Notificacion notificacion = new Notificacion();
                                notificacion.setNombreFichero(nombreFicheroSeleccionado);
                                notificacion.setReferencia(linea.substring(1, 71).trim());
                                notificacion.setNombre(linea.substring(71, 321).trim());
                                notificacion.setDireccion(linea.substring(321, 456).trim());
                                notificacion.setCodigoPostal(linea.substring(456, 461).trim());
                                notificacion.setPoblacion(linea.substring(461, 561).trim());
                                notificacion.setReferenciaSCB(linea.substring(561, 631).trim());

                                notificacion.setSegundoIntento(false);
                                listaNotificaciones.add(notificacion);

                            } else if(linea.startsWith("S")) { // Determina que es el formato del sicer de segundo intento

                                esCargaPrimeraEntrega = Boolean.FALSE;

                                // Se recupera la referencia postal
                                String referenciaPostal = linea.substring(1, 71).trim();

                                // Lo primero se busca si existe en la base de datos interna, es decir, si se ha cargado
                                Notificacion notificacion = dbHelper.obtenerNotificacion(referenciaPostal);
                                if(notificacion != null) {

                                    notificacion.setResultado1(linea.substring(71, 73).trim());
                                    notificacion.setDescResultado1(linea.substring(73, 103).trim());
                                    notificacion.setFechaHoraRes1(linea.substring(103, 128).trim());
                                    notificacion.setLongitudRes1(linea.substring(128, 148).trim());
                                    notificacion.setLatitudRes1(linea.substring(148, 168).trim());
                                    notificacion.setNotificadorRes1(linea.substring(168, 218).trim());
                                    notificacion.setSegundoIntento(true);

                                    listaNotificaciones.add(notificacion);

                                } else {
                                    // Si no se ha encontrado, se debe sacar un mensaje con el error al notificador
                                }
                            }

                            publishProgress(getString(R.string.cargando_fichero_sicer) + numLinea);
                            numLinea++;
                        }

                        publishProgress(getString(R.string.guardando_datos_en_bd_interna));
                        if(esCargaPrimeraEntrega) {
                            dbHelper.guardarNotificacionesInicial(listaNotificaciones);
                        } else {
                            dbHelper.actualizarNotificacionesSegundoIntentoInicial(listaNotificaciones);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getStackTrace());
                    }
                } else {
                    resultado = false;
                }

            }
            return resultado;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CargarRepartoActivity.this, "Cargando", "Cargando fichero SICER...");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(CargarRepartoActivity.this);
            builder.setTitle(R.string.informacion_carga);
            if (result) {
                builder.setMessage(R.string.datos_guardados_ok_bd);
            } else {
                // El fichero ya estaba cargado
                builder.setMessage(R.string.fichero_cargado_anteriormente);
            }
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
        }
    }
}
