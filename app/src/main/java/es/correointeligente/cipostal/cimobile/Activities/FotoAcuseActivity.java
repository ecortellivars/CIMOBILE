package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Util;



public class FotoAcuseActivity extends BaseActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Toolbar mToolbar;
    Button btn_guardar, btn_hacerFoto, btn_cancelar;
    ImageView imagenVista;
    String referenciaPostal;
    Integer idNotificacion;
    private Camera mCamera;
    private CameraPreview mPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_acuse);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_hacerFoto = (Button) findViewById(R.id.button_foto_acuse_hacer_foto);
        btn_hacerFoto.setOnClickListener(this);

        btn_cancelar = (Button) findViewById(R.id.btn_foto_acuse_cancelar);
        btn_cancelar.setOnClickListener(this);

        btn_guardar = (Button) findViewById(R.id.btn_foto_acuse_guardar);
        btn_guardar.setOnClickListener(this);
        
        // Recupera los datos de la notificacion
        referenciaPostal = getIntent().getStringExtra("referenciaPostal");
        idNotificacion = getIntent().getIntExtra("idNotificacion", 0);

    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_foto_acuse;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            // Lógica boton hacer foto
            case R.id.button_foto_acuse_hacer_foto:
                if  (checkCameraHardware(this) == Boolean.TRUE) {
                    llamarIntent();
            }

            // Logica boton guardar foto
            case R.id.btn_foto_acuse_guardar:

                imagenVista.setBackground(ContextCompat.getDrawable(FotoAcuseActivity.this, R.drawable.edit_text_shape));
                Bitmap bitmap = imagenVista.getDrawingCache();
                File file = new File(Util.obtenerRutaFirmaNotificador(), referenciaPostal + "_" + idNotificacion + ".png");

                try (FileOutputStream ostream = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                    ostream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Se redirige a la pantalla de nueva notificacion
                Intent i = new Intent(getBaseContext(), NuevaNotificacionActivity.class);
                startActivity(i);
                finish();


            // Logica boton cancelar foto
            case R.id.btn_foto_acuse_cancelar:

        }
    }

    private void llamarIntent() {
        // Conectamos con la camara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Lanzamos la tarea de hacer foto a la camara y esperamos resultado
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Si todo ha ido bien recibimos un Bundle de la camara
            Bundle extras = data.getExtras();
            // Lo convertimos en un BitMap
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // La mostramos en pantalla
            imagenVista.setImageBitmap(imageBitmap);
        }
    }

}



