package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.File;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;



public class FotoAcuseActivity extends BaseActivity  {

    Toolbar mToolbar;
    String referencia, resultado, fechaHoraRes , segundo;
    Notificacion notificacion;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String CARPETA_RAIZ = "CiMobile/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "FOTOS_ACUSE/";
    DBHelper dbHelper;
    Boolean esAplicacionPEE;
    Integer intentoGuardado = 1000;
    String imageFileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_acuse);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(0);

        // Recupera los datos de la notificacion
        referencia = getIntent().getStringExtra("referencia");
        resultado = getIntent().getStringExtra("resultado");
        fechaHoraRes = getIntent().getStringExtra("fechaHoraRes");
        segundo = getIntent().getStringExtra("segundo");
        dbHelper = new DBHelper(this);

        // Dependiendo de si es una aplicación PEE revisara las fotos o no
        esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());

        // Revisamos que el dispositivo tiene camara
        if  (checkCameraHardware(this) == Boolean.TRUE) {
            try {
                llamarIntentHacerFoto();
                finish();

            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = null;
                toast = Toast.makeText(this, "NO SE PUDO HACER LA FOTO. REVISE LOS PERMISOS DE LA APLICACION", Toast.LENGTH_LONG);
                toast.show();
                finish();
            }


        }
    }

    // Gestión de los Iconos de la barra de herramientas
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_foto_acuse;
    }



    // Intent para hacer foto
    private void llamarIntentHacerFoto() {

        File storageDir = null;
        File fileDestino = null;
        imageFileName = referencia + "_" + fechaHoraRes  + "_" + fechaHoraRes   + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR,"") + "_" + resultado + ".webp";
        storageDir = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        fileDestino = new File(storageDir, imageFileName);
        Uri cameraImageUri = Uri.fromFile(fileDestino);

        // Abre la camara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Enviamos la imagen
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraImageUri);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Lanzamos la actividad
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        else {
            Toast toast = null;
            toast = Toast.makeText(this, "NO SE PUDO HACER LA FOTO. REVISE LOS PERMISOS DE LA APLICACION", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Método que determina los resultados de las diferentes actividades que se han lanzado
        // y dependiendo de su requestCode sabemos que actividad ha sido.
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                    imageFileName = referencia + "_" + fechaHoraRes  + "_" + fechaHoraRes   + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR,"") + "_" + resultado + ".webp";

                    notificacion = dbHelper.obtenerNotificacion(referencia);
                    // Nombre archivo = NA460239960019170000307_20170510_20170512_A3_01.webp
                    // Create an image file name

                    if (!esAplicacionPEE && notificacion.getResultado2() != null && notificacion.getResultado2().equals(resultado)) {
                        notificacion.setFotoAcuseRes2(Util.obtenerRutaFotoAcuse() + File.separator + imageFileName);
                        notificacion.setFotoAcuseRes1(null);
                        intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);

                    } else  if (!esAplicacionPEE && notificacion.getResultado1() != null && notificacion.getResultado1().equals(resultado)) {
                        notificacion.setFotoAcuseRes1(Util.obtenerRutaFotoAcuse() + File.separator + imageFileName);
                        notificacion.setFotoAcuseRes2(null);
                        intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);
                    }

                    if (intentoGuardado != 1000) {
                        Toast toast = null;
                        toast = Toast.makeText(this, "Resultado guardado Correctamente", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        Toast toast = null;
                        toast = Toast.makeText(this, "Resultado NO guardado", Toast.LENGTH_LONG);
                        toast.show();
                    }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}

