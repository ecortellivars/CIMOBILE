package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Util;



public class FotoAcuseActivity extends BaseActivity implements View.OnClickListener {

    Toolbar mToolbar;
    ImageButton btn_hacerFoto;
    String referencia, resultado, fechaHoraRes;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String CARPETA_RAIZ = "CiMobile/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "FOTOS_ACUSE/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_acuse);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_hacerFoto = (ImageButton) findViewById(R.id.button_foto_acuse_hacer_foto);
        btn_hacerFoto.setOnClickListener(this);


        // Recupera los datos de la notificacion
        referencia = getIntent().getStringExtra("referencia");
        resultado = getIntent().getStringExtra("resultado");
        fechaHoraRes = getIntent().getStringExtra("fechaHoraRes");


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // Lógica boton hacer foto
            case R.id.button_foto_acuse_hacer_foto:

                // Revisamos que el dispositivo tiene camara
                if  (checkCameraHardware(this) == Boolean.TRUE) {
                    try {
                        llamarIntentHacerFoto();
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast toast = null;
                        toast = Toast.makeText(this, "Revisa los permisos de la camara del movil", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    }
                    break;
                }
        }
    }

    // Intent para hacer foto
    private void llamarIntentHacerFoto() {
        String imageFileName = null;
        File storageDir = null;
        File fileDestino = null;
        // Create an image file name
        imageFileName = referencia + "_" + fechaHoraRes  + "_" + fechaHoraRes   + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR,"") + "_" + resultado + ".jpg";
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
            toast = Toast.makeText(this, "Revisa los permisos de la camara del movil", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        if (requestCode == 1) {

        }

        else {
            Toast toast = null;
            toast = Toast.makeText(this, "Revisa los permisos de la camara del movil", Toast.LENGTH_LONG);
            toast.show();
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

