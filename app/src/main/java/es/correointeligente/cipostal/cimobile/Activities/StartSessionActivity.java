package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class StartSessionActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_usuario, edt_password;
    Button mButton_inciarSesion;
    SharedPreferences sp;
    FTPHelper ftpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        sp = this.getSharedPreferences(Util.FICHERO_PREFERENCIAS_SESION, MODE_PRIVATE);

        //Si SharedPreferences contiene el dato de la sesion se salta la pantalla de inicio de sesion
        if (sp.contains(Util.CLAVE_SESION_NOTIFICADOR)) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        // Antes de continuar se comprueba que hay configuracion por defecto
        Util.cargarConfiguracionAplicacionPorDefecto(getBaseContext());

        mButton_inciarSesion = (Button) findViewById(R.id.button_iniciar_sesion);
        mButton_inciarSesion.setOnClickListener(this);

        edt_usuario = (EditText) findViewById(R.id.edt_startSession_usuario);
        edt_password = (EditText) findViewById(R.id.edt_startSession_password);
        edt_password.setTypeface(Typeface.DEFAULT);
        edt_password.setTransformationMethod(new PasswordTransformationMethod());

        // Lanza una tarea en background para la conexión FTP y comprobar si hay actualizaciones
        FtpCheckUpdatesTask ftpCheckUpdatesTask = new FtpCheckUpdatesTask();
        ftpCheckUpdatesTask.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_iniciar_sesion:
                // Se llama en background al webservice que valida el usuario y password
                LoginUsuarioTask loginUsuarioTask = new LoginUsuarioTask();
                loginUsuarioTask.execute(edt_usuario.getText().toString(), edt_password.getText().toString());

                break;
        }

    }

    /**
     * Clase privada que se encarga de ejecutar en segundo plano la conexión via FTP,
     * y comprobar las actualizaciones de la aplicacion
     */
    private class FtpCheckUpdatesTask extends AsyncTask<Void, Void, Boolean> {
        String version = null;
        protected Boolean doInBackground(Void... args) {
            Boolean hayNuevaVersion = false;
            try {

                // Inicializamos la clase Singleton para la gestion FTP
                ftpHelper = FTPHelper.getInstancia();
                if (ftpHelper != null && ftpHelper.connect(StartSessionActivity.this)) {
                    if(ftpHelper.cargarCarpeta((String)Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_UPDATES_CARPETA, getBaseContext(), String.class.getSimpleName()))) {

                        String fichero = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_UPDATES_FICHERO, getBaseContext(), String.class.getSimpleName());
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ftpHelper.leerFichero(fichero)))) {
                            // Se lee solo la primera linea
                            String linea = reader.readLine();
                            //Se separa el string "version:" del resto
                            version = linea.replace("version:", "").trim();
                            if (!version.equalsIgnoreCase(getPackageManager().getPackageInfo(getPackageName(), 0).versionName)) {
                                // si no es la misma versión se saca
                                hayNuevaVersion = true;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ftpHelper.disconnect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return hayNuevaVersion;
        }

        @Override
        protected void onPostExecute(Boolean hayNuevaVersion) {

            if(hayNuevaVersion) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartSessionActivity.this);
                builder.setTitle(R.string.actualizacion);
                builder.setMessage(getString(R.string.detalle_actualizacion)+" "+version);
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        DescargarEInstalarAPKTask descargarEInstalarAPKTask = new DescargarEInstalarAPKTask();
                        descargarEInstalarAPKTask.execute(version);
                    }
                });
                builder.show();
            }

        }
    }

    /**
     * Clase privada que se encarga descargar el apk con la nueva version e iniciar el
     * instalador
     */
    private class DescargarEInstalarAPKTask extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(StartSessionActivity.this, getString(R.string.actualizacion), getString(R.string.descargando_version));
        }

        @Override
        protected Void doInBackground(String... args) {
            String version = args[0];

            try {
                // Inicializamos la clase Singleton para la gestion FTP
                ftpHelper = FTPHelper.getInstancia();

                if (ftpHelper != null && ftpHelper.connect(StartSessionActivity.this)) {
                    if (ftpHelper.cargarCarpeta((String)Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_UPDATES_CARPETA, getBaseContext(), String.class.getSimpleName()))) {
                        String fichero = "CIMobile-release-" + version + ".apk";
                        ftpHelper.descargarFichero(fichero, Util.obtenerRutaActualizaciones());
                        String rutaFinalFicheroUpdate = Util.obtenerRutaActualizaciones()+File.separator+fichero;
                        ftpHelper.disconnect();
                        progressDialog.dismiss();

                        // Se lanza la actividad de actualizacion(Lanza el gestor de instalaciones)
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setDataAndType(Uri.fromFile(new File(rutaFinalFicheroUpdate)), "application/vnd.android.package-archive");
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(install);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(StartSessionActivity.this, R.string.error_durante_actualizacion,Toast.LENGTH_SHORT).show();
            }

            if(ftpHelper != null && BooleanUtils.isTrue(ftpHelper.isConnected())) {
                ftpHelper.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            progressDialog.dismiss();
        }
    }

    /**
     * Clase privada que se ejecuta en segundo plano y se encarga de iniciar la sesion contra el
     * webservice publicado por CI POSTAL
     */
    private class LoginUsuarioTask extends AsyncTask<String, Void, Notificador> {
        ProgressDialog progressDialog;
        String fallo;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(StartSessionActivity.this, getString(R.string.iniciando_sesion), getString(R.string.espere_info_inicio_sesion));
        }

        @Override
        protected Notificador doInBackground(String... params) {
            Notificador notificador = null;

            try {
                String NAMESPACE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_NAMESPACE, getBaseContext(), String.class.getSimpleName());
                String METHOD_NAME = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_METHOD_NAME, getBaseContext(), String.class.getSimpleName());
                String URL = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_METHOD_URL, getBaseContext(), String.class.getSimpleName());

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("nombreUsuario", params[0]);
                request.addProperty("contrasenya", params[1]);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = false; // para los webservice asmx true, sino false
                sobre.setOutputSoapObject(request);

                //Modelo el transporte
                HttpTransportSE transporte = new HttpTransportSE(URL);
                transporte.debug = true;
                transporte.call(URL+"/"+METHOD_NAME, sobre);

                if(sobre.bodyIn instanceof SoapObject) {
                    SoapObject s = (SoapObject) sobre.bodyIn;
                    Boolean valido = BooleanUtils.toBoolean(s.getProperty("valido").toString());
                    if(BooleanUtils.isTrue(valido)) {
                        String codigoEscaner = s.hasProperty("codigo")? s.getProperty("codigo").toString() : s.getProperty("nombreUsuario").toString();
                        notificador = new Notificador(codigoEscaner, s.getProperty("nombreUsuario").toString(),  s.getProperty("corporacion").toString());
                    } else {
                        switch (s.getProperty("error").toString()) {
                            case "UNV": fallo = getString(R.string.usuario_no_valido);
                                break;
                            case "CNV": fallo = getString(R.string.password_no_valido);
                                break;
                            default: fallo = getString(R.string.error_inicio_sesion);
                                break;
                        }
                    }
                } else if (sobre.bodyIn instanceof SoapFault) {
                    SoapFault soapFault = (SoapFault) sobre.bodyIn;
                    fallo = soapFault.getMessage();
                }

            } catch (Exception e) {
                fallo = getString(R.string.error_inicio_sesion);
                e.printStackTrace();
            }

            return notificador;
        }

        @Override
        protected void onPostExecute(Notificador notificador) {
            progressDialog.dismiss();

            if(notificador != null) {
                SharedPreferences.Editor e = sp.edit();
                e.putString(Util.CLAVE_SESION_NOTIFICADOR, notificador.getNombre());
                e.putString(Util.CLAVE_SESION_DELEGACION, notificador.getDelegacion());
                e.putString(Util.CLAVE_SESION_COD_NOTIFICADOR, notificador.getCodigo());
                e.commit();

                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            } else {
                if(StringUtils.isNotBlank(fallo)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartSessionActivity.this);
                    builder.setTitle(R.string.iniciar_sesion);
                    builder.setMessage(fallo);
                    builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int wich) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            }
        }
    }
}
