package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.spongycastle.util.StringList;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;

import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.FTPHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;


/** Actividad inicial dada de alta en AndroidManifest con intent lo que significa que desde esta empezaremos
    siempre a debugar. Esta Actividad gestiona el layout llamado activity_start_session **/
public class StartSessionActivity extends AppCompatActivity implements View.OnClickListener {

    // Declaracion de variables a utilizar en el metodo
    EditText edt_usuario, edt_password;
    Button mButton_inciarSesion;
    SharedPreferences sp;
    FTPHelper ftpHelper;
    TextView txt_version_value;
    String fallo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instanciamos la actividad para poder darle logica
        super.onCreate(savedInstanceState);
        // Obtenemos el layout al que queremos darle logica
        setContentView(R.layout.activity_start_session);
        // Obtenemos el objeto que contiene las preferencias de la aplicacion
        // /data/user/0/es.correointeligente.cipostal.cimobile/shared_prefs/sesion.xml
        sp = this.getSharedPreferences(Util.FICHERO_PREFERENCIAS_SESION, MODE_PRIVATE);

        // Si SharedPreferences contiene el dato de la sesion se salta la pantalla de inicio de sesion
        // Es decir el usuario ya estaba logado no hace falta que se vuelva a logar
        if (sp.contains(Util.CLAVE_SESION_NOTIFICADOR)) {
            //Invocamos a la actividad siguiente del siguiente layout finalizando el inicio de sesion
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }

        // Al ser el primer inicio de sesion continuamos por aqui
        // Antes de continuar se comprueba que hay configuracion por defecto donde estan todos los datos para poder trabajar
        Util.cargarConfiguracionAplicacionPorDefecto(getBaseContext());

        // Invocamos al boton de inicioSesion para darle su logica
        mButton_inciarSesion = (Button) findViewById(R.id.button_iniciar_sesion);
        mButton_inciarSesion.setOnClickListener(this);

        // Creamos los objetos necesarios y almacenamos lo que el usuario introdujo
        // Usuario
        edt_usuario = (EditText) findViewById(R.id.edt_startSession_usuario);

        // Password
        edt_password = (EditText) findViewById(R.id.edt_startSession_password);
        edt_password.setTypeface(Typeface.DEFAULT);
        edt_password.setTransformationMethod(new PasswordTransformationMethod());

        // Version instalada
        txt_version_value= (TextView) findViewById(R.id.edt_startSession_version_value);
        String versionInstalada = null;
        try {
            // La version esta ubicada en el buid.gradle app
            versionInstalada = (getPackageManager().getPackageInfo(getPackageName(), 0).versionName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        txt_version_value.setText("Versión: " + versionInstalada);

        // Lanza una tarea en background para la conexión FTP y comprobar si hay actualizaciones
        // Solo se puede conectar al FTP de Ibermatica dentro de la red de SCI y CIPOSTAL
        // Cambiando las preferencias del FTP se podria conectar a un FTP Publico como el de 1and1
        FtpCheckUpdatesTask ftpCheckUpdatesTask = new FtpCheckUpdatesTask();
        ftpCheckUpdatesTask.execute();
    }

    /**
     * Clase privada ASINCRONA que se encarga de ejecutar en segundo plano la conexión via FTP,
     * y devuelve si la aplicacion instalada en el smartPhone tiene o no la ultima version
     */
    private class FtpCheckUpdatesTask extends AsyncTask<String, Void, String[]>  {
        // Parametros de entrada son (Tipo_empezarBackground, Tipo_duranteBackground, Tipo_terminarBackground)
        //                           <Params,                 Progress,               Result>

        //            Estructura de una AsyncTask
        // onPreExecute() + onProgressUpdate() + onCancelled()
        // <-----------------doInBackground()---------------->
        protected String[] doInBackground(String... variableNoUsada) {
            String[] args = new String[6];
            // Posicion [0] : versionMandada
            // Posicion [1] : Hay o no hay nueva version
            // Posicion [2] : Mensajes de KO
            // Posicion [3] : Mensajes de dispositivo diciendo que esta a la ultima version

            // Inicializamos la clase Singleton para la gestion FTP
            ftpHelper = FTPHelper.getInstancia();
            // Obtenemos class es.correointeligente.cipostal.cimobile.Util.FTPHelper
            if (ftpHelper != null && ftpHelper.connect(StartSessionActivity.this)) {
                // ftpData/ULTIMAVERSION/CIMOBILE
                String carpetaUpdates = Util.obtenerRutaFtpActualizaciones(getBaseContext());
                // Si existe la carpeta obtenemos el fichero version.txt
                if(ftpHelper.cargarCarpeta(carpetaUpdates)) {
                    // ftpData/ULTIMAVERSION/CIMOBILE/version.txt
                    String fichero = carpetaUpdates + Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_FTP_UPDATES_FICHERO, getBaseContext(), String.class.getSimpleName());;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(ftpHelper.leerFichero(fichero)))) {
                        // Se lee solo la primera linea del fichero txt
                        String linea = reader.readLine();
                        // Se separa el string "version:" del resto
                        String versionMandada = linea.replace("version:", "").trim();
                        args[0] = versionMandada;
                        Integer versionMandadaInteger = NumberFormat.getInstance().parse(versionMandada).intValue();
                        Integer versionInstaladaInteger = NumberFormat.getInstance().parse(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).intValue();
                        // Si la version que mandamos es mayor que la instalada se informa a onPostExecute
                        if (versionMandadaInteger > versionInstaladaInteger) {
                            args[1] =  "1";
                        }
                        else {
                            args[1] =  "0";
                            args[3] = "El dispositivo tiene la última version";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        args[2] = "No existe ningun fichero para actualizar";
                        args[1] = "0";
                        args[0] = "0";

                    }
                }
                ftpHelper.disconnect();
            }

            else {
                args[2] = "Problemas de conexión con el FTP";
                args[1] = "0";
                args[0] = "0";
            }



            return args;
        }

        /**
         * Clase que informa al usuario que existe una nueva version de la app mas actual que la instalada en su smartPhone
         * @param args
         */
        protected void onPostExecute(String[] args) {

            String hayNuevaVersion = args[1].toString();
            final String versionMandada = args[0].toString();
            Toast toast = null;

            // Si no ha habido error y el dispositivo no esta a la ultima version
            if ( args[2] == null && args[3] == null) {
                if (hayNuevaVersion == "1") {

                    // Usamos la clase AlertDialog para mandar mensajes al usuario
                    // Primero le damos el contexto de la aplicacion
                    AlertDialog.Builder builder = new AlertDialog.Builder(StartSessionActivity.this);
                    // Le incluimos al objeto los mensajes a mostrar
                    builder.setTitle(R.string.actualizacion);
                    builder.setMessage(getString(R.string.detalle_actualizacion) + " " + versionMandada);

                    // Si el usuario no quiere actualizar la app
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.cancel();
                        }
                    });

                    // Si el usuario si quiere actualizarse
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            // Creamos el objeto que descargara la apk
                            DescargarEInstalarAPKTask descargarEInstalarAPKTask = new DescargarEInstalarAPKTask(versionMandada);
                            // Tarea en background
                            // Ejecutamos la logica pasandole como parametro un NULL
                            descargarEInstalarAPKTask.execute(versionMandada);
                        }
                    });
                    builder.show();
                }
            }
            else {
                // Si ha habido algun error
                if (args[2] != null) {
                    toast = Toast.makeText(StartSessionActivity.this, "Problemas de conexión con el FTP", Toast.LENGTH_LONG);
                    toast.setGravity(1,0,500);
                    toast.show();
                }
                // Si el dispositivo ya esta a la ultima version
                if (args[3] != null) {
                    toast = Toast.makeText(StartSessionActivity.this, "El dispositivo tiene la última version", Toast.LENGTH_LONG);
                    toast.setGravity(1,0,500);
                    toast.show();
                }

            }
        }
    }

    /**
     * Clase privada ASINCRONA que se encarga descargar el apk con la nueva version e iniciar el
     * instalador
     */
    private class DescargarEInstalarAPKTask extends AsyncTask<String, Void, String[]> {
        // Creamos una tarea de progreso para mostrar al usuario que estamos bajandonos la apk del FTP
        ProgressDialog progressDialog;
        String versionMandada;

        // Constructor
        public DescargarEInstalarAPKTask (String versionMandada) {
            this.versionMandada = versionMandada;
        }

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(StartSessionActivity.this, getString(R.string.actualizacion), getString(R.string.descargando_version));
        }

        protected String[] doInBackground(String... versionMandada) {
            String[] args2 = new String[6];
            // Posicion [1] : OK
            // Posicion [2] : versionMandada
            // Posicion [3] : KO


            try {
                // Inicializamos la clase Singleton para la gestion FTP
                ftpHelper = FTPHelper.getInstancia();

                if (ftpHelper != null && ftpHelper.connect(StartSessionActivity.this)) {
                    String carpetaUpdates = Util.obtenerRutaFtpActualizaciones(getBaseContext());
                    if (ftpHelper.cargarCarpeta(carpetaUpdates)) {
                        String fichero = "CIMobile-release-" + versionMandada[0] + ".apk";
                        // Descargamos el fichero apk del ftp
                        String falloDescarga = ftpHelper.descargarFichero(fichero, Util.obtenerRutaActualizaciones());
                        // Si no hubo problemas con la descarga instalo la apk bajada
                        if (falloDescarga == null) {
                            // /storage/emulated/0/CIMobile/UPDATES_APP/CIMobile-release-3.0.apk
                            String rutaFinalFicheroUpdate = Util.obtenerRutaActualizaciones() + File.separator + fichero;
                            ftpHelper.disconnect();
                            progressDialog.dismiss();
                            // Se lanza la actividad de actualizacion(Lanza el gestor de instalaciones)
                            Intent install = new Intent(Intent.ACTION_VIEW);
                            // install.setDataAndType(Uri.fromFile(new File(rutaFinalFicheroUpdate)), "application/vnd.android.package-archive");
                            install.setDataAndType(FileProvider.getUriForFile(getBaseContext(), getBaseContext().getApplicationContext().getPackageName() + ".provider", new File(rutaFinalFicheroUpdate)), "application/vnd.android.package-archive");
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(install);
                            args2[1] = getString(R.string.no_error_durante_actualizacion);
                            args2[2] = versionMandada[0];
                            args2[3] = null;
                        }
                        else {
                            args2[1] = null;
                            args2[3] = "No hay nueva versión";
                            args2[2] = versionMandada[0];
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                fallo = getString(R.string.error_durante_actualizacion);
                args2[1] = null;
                args2[3] = fallo;
                args2[2] = versionMandada[0];
            }

            if(ftpHelper != null && BooleanUtils.isTrue(ftpHelper.isConnected())) {
                ftpHelper.disconnect();
            }
            return args2;
        }

        // Tarea que finaliza el hilo en backGround
        protected void onPostExecute(String[] args2) {

            progressDialog.dismiss();

            String versionMandada = args2[2].toString();
            Toast toast = null;

            // Si no hubo error
            if( args2[3] == null) {
                toast = Toast.makeText(StartSessionActivity.this, "Se actualizó correctamente", Toast.LENGTH_LONG);
                toast.setGravity(1,0,500);
                toast.show();
                // Si no ha habido error mando al layout el nuevo TextView con la nueva version instalada
                txt_version_value = (TextView) findViewById(R.id.edt_startSession_version_value);
                txt_version_value.setText("Versión: " + versionMandada);
                }
                // Si hubo error
                else  if( args2[3] != null)  {
                    toast = Toast.makeText(StartSessionActivity.this, "Se produjo un error durante la actualización. Revisa los permisos del movil", Toast.LENGTH_LONG);
                    toast.setGravity(1,0,500);
                    toast.show();
                }
            }
    }



    /**
     * Clase publica que tiene la logica necesaria cuando el usuario hace click en el boton
     * llamado button_iniciar_sesion y lanza en backGroung la llamada al WS para validarlos
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_iniciar_sesion:
                // Se llama en background al webservice implementado en CIPOSTAL
                // que valida el usuario y password introducido por el usuario
                LoginUsuarioTask loginUsuarioTask = new LoginUsuarioTask();
                loginUsuarioTask.execute(edt_usuario.getText().toString(), edt_password.getText().toString());

                break;
        }
    }

    /**
     * Clase privada ASINCRONA que se ejecuta en segundo plano y se encarga de iniciar la sesion contra el
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
                // http://impl.v01.srvPostal.business.postal.sdci.es/
                String NAMESPACE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_NAMESPACE, getBaseContext(), String.class.getSimpleName());
                // validarLoginWS
                String METHOD_NAME = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_METHOD_NAME, getBaseContext(), String.class.getSimpleName());
                // http://correointeligente.es:9995/PostalService
                String URL = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_WS_METHOD_URL, getBaseContext(), String.class.getSimpleName());

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("nombreUsuario", params[0]);
                request.addProperty("contrasenya", params[1]);

                SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                sobre.dotNet = false; // para los webservice asmx true, sino false
                sobre.setOutputSoapObject(request);

                // Modelo el transporte: Llamada al WS
                HttpTransportSE transporte = new HttpTransportSE(URL);
                transporte.debug = true;
                transporte.call(URL + "/" + METHOD_NAME, sobre);
                // bodyIn = RespuestaValidarUsuarioWS{codigoEscaner=A22; corporacion=valencia; nombreUsuario=usuValencia; valido=true; }
                // bodyOut = validarLoginWS{nombreUsuario=usuValencia; contrasenya=sdci; }

                if(sobre.bodyIn instanceof SoapObject) {
                    SoapObject s = (SoapObject) sobre.bodyIn;
                    Boolean valido = BooleanUtils.toBoolean(s.getProperty("valido").toString());
                    if(BooleanUtils.isTrue(valido)) {
                        String codigoEscaner = s.hasProperty("codigoEscaner")? s.getProperty("codigoEscaner").toString() : s.getProperty("nombreUsuario").toString();
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
