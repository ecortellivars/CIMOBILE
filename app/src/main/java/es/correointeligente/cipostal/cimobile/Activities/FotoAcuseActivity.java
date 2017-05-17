package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
    Button btn_guardarFoto, btn_hacerFoto;
    ImageView imagenVista;
    String referencia, notificadorRes1, notificadorRes2, resultado1, resultado2, fechaHoraRes1, fechaHoraRes2;
    Integer idNotificacion;
    static final int EXTRA_OUTPUT = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Boolean esPrimerResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResocurce() );

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_hacerFoto = (Button) findViewById(R.id.button_foto_acuse_hacer_foto);
        btn_hacerFoto.setOnClickListener(this);

        btn_guardarFoto = (Button) findViewById(R.id.btn_foto_acuse_guardar);
        btn_guardarFoto.setOnClickListener(this);
        
        // Recupera los datos de la notificacion
        referencia = getIntent().getStringExtra("referencia");
        notificadorRes1 = getIntent().getStringExtra("notificadorRes1");
        notificadorRes2 = getIntent().getStringExtra("notificadorRes2");
        resultado1 = getIntent().getStringExtra("resultado1");
        resultado2 = getIntent().getStringExtra("resultado2");
        fechaHoraRes1 = getIntent().getStringExtra("fechaHoraRes1");
        fechaHoraRes2 = getIntent().getStringExtra("fechaHoraRes2");
        esPrimerResultado = getIntent().getBooleanExtra("esPrimerResultado", Boolean.TRUE);

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
                    llamarIntentHacerFoto();
                }
                break;

            // Logica boton guardar foto
            case R.id.btn_foto_acuse_guardar:

                llamarIntentGuardarFoto();
                // Se redirige a la pantalla de lista de notificaciones
                Intent i = new Intent(getBaseContext(), ListaNotificacionesActivity.class);
                startActivity(i);
                finish();

        }
    }

    // Intent para hacer foto
    private void llamarIntentHacerFoto() {
        // Abre la camara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }

    // Intent para guardar foto
    private void llamarIntentGuardarFoto() {
        // Abre la camara
        Intent savePictureIntent = new Intent(MediaStore.EXTRA_OUTPUT);
        // Ensure that there's a camera activity to handle the intent
        startActivityForResult(savePictureIntent, EXTRA_OUTPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imagenVista = (ImageView) findViewById(R.id.imagen_foto_acuse_ver_foto) ;
            imagenVista.setImageBitmap(imageBitmap);
        }
        if (requestCode == EXTRA_OUTPUT && resultCode == RESULT_OK) {
            if (esPrimerResultado){
                convertBitmapToFile(imageBitmap,referencia + "_" + notificadorRes1 + "_" + resultado1 + "_" + fechaHoraRes1  + ".jpg");
            } else {
                convertBitmapToFile(imageBitmap,referencia + "_" + notificadorRes2 + "_" + resultado2 + "_" + fechaHoraRes2  + ".jpg");
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

    private static void convertBitmapToFile(Bitmap bitmap, String name) {
        File filesDir = new File("CIMobile/FOTOS_ACUSE");

        File imageFile = new File(filesDir, name);
        File file = new File(Util.obtenerRutaFotoAcuse(), name);

        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

        } catch (Exception e) {
            //TODO
        }

    }

}



