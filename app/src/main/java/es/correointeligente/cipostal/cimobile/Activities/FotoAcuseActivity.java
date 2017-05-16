package es.correointeligente.cipostal.cimobile.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.Util;



public class FotoAcuseActivity extends BaseActivity implements View.OnClickListener {


    Toolbar mToolbar;
    Button btn_guardarFoto, btn_hacerFoto, btn_cancelarFoto;
    ImageView imagenVista;
    String referencia;
    Integer idNotificacion;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;



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

        btn_cancelarFoto = (Button) findViewById(R.id.btn_foto_acuse_cancelar);
        btn_cancelarFoto.setOnClickListener(this);

        btn_guardarFoto = (Button) findViewById(R.id.btn_foto_acuse_guardar);
        btn_guardarFoto.setOnClickListener(this);

        imagenVista = (ImageView) findViewById(R.id.imagen_foto_acuse_ver_foto) ;
        
        // Recupera los datos de la notificacion
        referencia = getIntent().getStringExtra("referencia");
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
                // Revisamos que el dispositivo tiene camara
                if  (checkCameraHardware(this) == Boolean.TRUE) {
                    llamarIntent();
                }
                finish();
                break;

            // Logica boton guardar foto
            case R.id.btn_foto_acuse_guardar:

                imagenVista.setBackground(ContextCompat.getDrawable(FotoAcuseActivity.this, R.drawable.edit_text_shape));
                Bitmap bitmap = imagenVista.getDrawingCache();
                File file = new File(Util.obtenerRutaFotoAcuse(), referencia + "_" + idNotificacion + ".jpg");

                try (FileOutputStream ostream = new FileOutputStream(file)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, ostream);
                    ostream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Se redirige a la pantalla de nueva notificacion
                Intent i = new Intent(getBaseContext(), NuevaNotificacionActivity.class);
                startActivity(i);
                finish();
                break;

            // Logica boton cancelar foto
            case R.id.btn_foto_acuse_cancelar:
                // Se redirige a la pantalla de nueva notificacion
                Intent i2 = new Intent(getBaseContext(), NuevaNotificacionActivity.class);
                startActivity(i2);
                finish();
                break;

        }
    }



    private void llamarIntent() {
        // Abre la camara
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagenVista.setImageBitmap(imageBitmap);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}



