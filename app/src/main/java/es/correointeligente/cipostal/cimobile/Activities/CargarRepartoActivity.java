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
import es.correointeligente.cipostal.cimobile.Model.Fichero;
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

        // Inicializamos la clase Singleton para la gestion FTP
        ftpHelper = FTPHelper.getInstancia();

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
        builder.setTitle("Cargar fichero SICER");
        builder.setMessage("¿Está seguro de cargar el fichero SICER " + nombreFicheroSeleccionado + "?");
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

               if(ftpHelper.connect()) {

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
            progressDialog = ProgressDialog.show(CargarRepartoActivity.this, "Conecanto al ftp", "Espere mientras se conecta al servidor FTP");
        }

        @Override
        protected void onPostExecute(Void result) {
            if (ftpHelper.isConnected()) {
                // Se ha creado la conexión correctamente
                mlistView_cargar_reparto_ficheros.setAdapter(itemsAdapter);
                mlistView_cargar_reparto_ficheros.setOnItemClickListener(CargarRepartoActivity.this);
            } else {
                // Ha fallado la conexión FTP
                AlertDialog.Builder builder = new AlertDialog.Builder(CargarRepartoActivity.this);
                builder.setTitle("Conexión FTP");
                builder.setMessage("No se ha podido conectar al servidor FTP en la ip 192.168.0.105");
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
                Fichero fichero = dbHelper.obtenerFichero(nombreFicheroSeleccionado);

                if (fichero == null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(ftpHelper.leerFichero(nombreFicheroSeleccionado)))) {

                        fichero = new Fichero(nombreFicheroSeleccionado);
                        Integer numLinea = 1;
                        List<Notificacion> listaNotificaciones = new ArrayList<>();

                        for (String linea = reader.readLine(); linea != null; linea = reader.readLine()) {

                            if (linea.startsWith("F")) { // CABECERA DE FICHERO

                                fichero.setCodigoCliente(linea.substring(4, 12));
                                fichero.setFechaFichero(linea.substring(19, 27) + " " + linea.substring(27, 32));

                            } else if (linea.startsWith("D")) { // DETALLE
                                Notificacion notificacion = new Notificacion();
                                notificacion.setNombreFichero(fichero.getNombreFichero());
                                notificacion.setReferencia(linea.substring(1, 24));
                                notificacion.setNombre(linea.substring(24, 124).trim());
                                notificacion.setDireccion(linea.substring(124, 174).trim());
                                notificacion.setPoblacion(linea.substring(174, 214).trim());
                                notificacion.setCodigoPostal(linea.substring(214, 219).trim());
                                notificacion.setSegundoIntento(false);
                                listaNotificaciones.add(notificacion);

                            } else if (linea.startsWith("f")) { // FIN DE FICHERO
                                fichero.setNumRemesas(linea.substring(12, 14));
                                fichero.setNumNotificaciones(linea.substring(15, 23));
                            }

                            publishProgress("Cargando fichero SICER...\nLinea: " + numLinea);
                            numLinea++;
                        }

                        publishProgress("Guardando los datos en la base de datos interna");
                        dbHelper.guardarFicheroInicial(fichero, listaNotificaciones);

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
            builder.setTitle("Información carga");
            if (result) {
                builder.setMessage("Se ha guardado correctamente en la base de datos");
            } else {
                // El fichero ya estaba cargado
                builder.setMessage("El fichero ya se habia cargado anteriormente");
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
