package es.correointeligente.cipostal.cimobile.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.TSA.TimeStamp;
import es.correointeligente.cipostal.cimobile.TSA.TimeStampRequestParameters;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.CiMobileException;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class DetalleNotificacionActivity extends BaseActivity {

    Toolbar mToolbar;
    DBHelper dbHelper;
    Integer idNotificacion, posicionAdapter;
    Notificacion notificacion;
    TextView tv_refPostal, tv_refSCB, tv_nombre, tv_direccion;
    private ViewGroup layoutResultado1, layoutResultado2;
    int resultadoEliminable = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Integer intentoGuardado = 1000;
    Boolean esAplicacionPEE = Boolean.FALSE;
    String fechaHoraString3;
    String imageFileName = null;
    private final String CARPETA_RAIZ = "CiMobile/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "FOTOS_ACUSE/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_notificacion);
        // Aqui meto los datos del primer intento
        layoutResultado1 = (ViewGroup) findViewById(R.id.linearLayout_detalleNotificacion_resultado1);
        // Aqui meto los datos del segundo intento si lo hay
        layoutResultado2 = (ViewGroup) findViewById(R.id.linearLayout_detalleNotificacion_resultado2);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Se recupera el valor que se nos ha pasado desde la lista de notificaciones
        idNotificacion = getIntent().getIntExtra("idNotificacion",0);
        posicionAdapter = getIntent().getIntExtra("posicionAdapter",0);

        // Mapeamos toda la vista del layout
        this.mapearVista();

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        // Lanza en background las consultas para rellenar la vista
        CargarDetalleNotificacionTask cargarDetalleNotificacionTask = new CargarDetalleNotificacionTask();
        cargarDetalleNotificacionTask.execute();
    }


    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_detalle_notificacion;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_borrar_notificacion, menu);
        return true;
    }

    // Gestión de los Iconos de la barra de herramientas
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Dependiendo de si es una aplicación PEE revisara las fotos o no
        esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
        Intent i = null;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_borrar_notificaciones:
                this.crearDialogoEliminarResultado();
                break;

            case R.id.menu_sello_tiempo:
                this.crearSelloTiempo();
                break;

            case R.id.imageButton_listaNotificaciones_foto:
                if (!esAplicacionPEE) {
                    // Revisamos que el dispositivo tiene camara
                    // Assume thisActivity is the current activity
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if (permissionCheck != -1) {
                        if (checkCameraHardware(this) == Boolean.TRUE) {
                            try {
                                // Si es segundo intento obtengo los datos para invocar al componente FotoAcuseActivity
                                if (notificacion.getSegundoIntento()) {
                                    // Dependiendo de si es una aplicación PEE revisara las fotos o no
                                    esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
                                    DateFormat df3 = new SimpleDateFormat("yyyyMMdd");
                                    fechaHoraString3 = df3.format(Calendar.getInstance().getTime());
                                    File storageDir = null;
                                    File fileDestino = null;
                                    imageFileName = notificacion.getReferencia() + "_" + fechaHoraString3  + "_" + fechaHoraString3   + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR,"") + "_" + notificacion.getResultado2() + ".webp";
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
                                }
                                // Si es primer intento obtengo sus datos para invocar al componente FotoAcuseActivity
                                else {
                                    // Dependiendo de si es una aplicación PEE revisara las fotos o no
                                    esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
                                    DateFormat df3 = new SimpleDateFormat("yyyyMMdd");
                                    fechaHoraString3 = df3.format(Calendar.getInstance().getTime());
                                    File storageDir = null;
                                    File fileDestino = null;
                                    imageFileName = notificacion.getReferencia() + "_" + fechaHoraString3  + "_" + fechaHoraString3   + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR,"") + "_" + notificacion.getResultado1() + ".webp";
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
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast toast = null;
                                toast = Toast.makeText(this, "NO SE PUDO HACER LA FOTO! REVISE LOS PERMISOS DE CAMARA DE LA APLICACION", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            }
                            break;
                        }
                    }else {
                        Toast toast = null;
                        toast = Toast.makeText(this, "NO SE PUDO HACER LA FOTO! REVISE LOS PERMISOS DE CAMARA DE LA APLICACION", Toast.LENGTH_LONG);
                        toast.show();
                    }

                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Método que determina los resultados de las diferentes actividades que se han lanzado
        // y dependiendo de su requestCode sabemos que actividad ha sido.
        if (resultCode == CommonStatusCodes.CANCELED) {
            Toast toast = null;
            toast = Toast.makeText(this, "Foto cancelado por el usuario", Toast.LENGTH_LONG);
            requestCode = 0;
            toast.show();
            } else  {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                        // Nombre archivo = NA460239960019170000307_20170510_20170512_A3_01.webp
                        if (!esAplicacionPEE && notificacion.getResultado2() != null) {
                            notificacion.setFotoAcuseRes2(Util.obtenerRutaFotoAcuse() + File.separator + notificacion.getReferencia() + "_" + fechaHoraString3 + "_" + fechaHoraString3 + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, "") + "_" + notificacion.getResultado2() + ".webp");
                            notificacion.setFotoAcuseRes1(null);
                            intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);

                        } else if (!esAplicacionPEE && notificacion.getResultado1() != null) {
                            notificacion.setFotoAcuseRes1(Util.obtenerRutaFotoAcuse() + File.separator + notificacion.getReferencia() + "_" + fechaHoraString3 + "_" + fechaHoraString3 + "_" + sp.getString(Util.CLAVE_SESION_COD_NOTIFICADOR, "") + "_" + notificacion.getResultado1() + ".webp");
                            notificacion.setFotoAcuseRes2(null);
                            intentoGuardado = dbHelper.guardaResultadoNotificacion(notificacion);
                        }

                        if (intentoGuardado != 1000) {
                            Toast toast = null;
                            toast = Toast.makeText(this, "Resultado guardado Correctamente", Toast.LENGTH_LONG);
                            toast.show();
                            break;
                        } else {
                            Toast toast = null;
                            toast = Toast.makeText(this, "Resultado NO guardado", Toast.LENGTH_LONG);
                            toast.show();
                            break;
                        }
                        default:
                            Toast toast = null;
                            toast = Toast.makeText(this, "NO SE PUDO HACER LA FOTO! REVISE LOS PERMISOS DE CAMARA DE LA APLICACION", Toast.LENGTH_LONG);
                            toast.show();
                            break;
                }
        }
    }

    private void mapearVista() {
        tv_refPostal = (TextView) findViewById(R.id.textView_detalleNotificacion_refPostal);
        tv_refSCB = (TextView) findViewById(R.id.textView_detalleNotificacion_refSCB);
        tv_nombre = (TextView) findViewById(R.id.textView_detalleNotificacion_nombre);
        tv_direccion = (TextView) findViewById(R.id.textView_detalleNotificacion_direccion);
    }

    /**
     * Clase privada que se encarga de cargar el detalle de la notificación en segundo plano
     */
    private class CargarDetalleNotificacionTask extends AsyncTask<Void, Void, Notificacion> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DetalleNotificacionActivity.this, getString(R.string.cargando_notificacion), getString(R.string.espere_info_notificacion));
        }

        @Override
        protected Notificacion doInBackground(Void... voids) {
            Notificacion notificacion = dbHelper.obtenerNotificacion(idNotificacion);

            return notificacion;
        }

        @Override
        protected void onPostExecute(Notificacion notificacionAux) {
            // Dependiendo de si es una aplicación PEE revisara las fotos o no
            Boolean esAplicacionPEE = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_APP_PEE, getBaseContext(), Boolean.class.getSimpleName());
            notificacion = notificacionAux;

            tv_refPostal.setText(notificacion.getReferencia().toString());
            tv_refSCB.setText(notificacion.getReferenciaSCB().toString());
            tv_nombre.setText(notificacion.getNombre().toString());
            tv_direccion.setText(notificacion.getDireccion().toString());

            TextView tv_relacion_destinatario1 = null, tv_XML1, tv_ST1, tv_receptor1 = null, tv_resultado1, tv_fecha1, tv_notificador1, tv_longitud1, tv_latitud1, tv_observaciones1, tv_cabeceraResultado1;
            TextView tv_relacion_destinatario2 = null, tv_XML2, tv_ST2, tv_receptor2 = null, tv_resultado2, tv_fecha2, tv_notificador2, tv_longitud2, tv_latitud2, tv_observaciones2, tv_cabeceraResultado2;
            ImageView img_firma_receptor = null;
            ImageView img_foto_acuse_res1;
            ImageView img_foto_acuse_res2 = null;
            Toast toast = null;

            // Hay dos resultados por lo que relleno 2 layouts
            if(notificacion.getResultado2() != null) {

                LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                // Instancio el linearLayout1 para cargar los datos del primer intento
                LinearLayout linearLayout1 = (LinearLayout) inflater.inflate(R.layout.datos_resultado_no_entregado, null, false);
                LinearLayout linearLayout2 = null;

                resultadoEliminable = 2;
                // Primer Intento NO ENTREGADO
                tv_resultado1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_resultado);
                tv_fecha1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_fecha);
                tv_notificador1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_notificador);
                tv_XML1 = (TextView) linearLayout1.findViewById(R.id.tv_result_xml);
                tv_ST1 = (TextView) linearLayout1.findViewById(R.id.tv_result_st);
                tv_longitud1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_longitud);
                tv_latitud1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_latitud);
                tv_observaciones1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_observaciones);
                tv_cabeceraResultado1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_cabecera_resultado);

                // Se cargan los datos de la notificacion en la vista
                tv_resultado1.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                tv_fecha1.setText(notificacion.getFechaHoraRes1());
                tv_notificador1.setText(notificacion.getNotificadorRes1());
                tv_longitud1.setText(notificacion.getLongitudRes1());
                tv_latitud1.setText(notificacion.getLatitudRes1());
                tv_observaciones1.setText(notificacion.getObservacionesRes1());
                tv_cabeceraResultado1.setText(R.string.resultado1);
                tv_XML1.setText("NO");
                tv_ST1.setText("NO");

                // Lo agrego al layout principal
                layoutResultado1.addView(linearLayout1);

                // Instancio el otro layout para cargar los resultados del segundo intento ENTREGADO
                linearLayout2 = (LinearLayout) inflater.inflate(R.layout.datos_resultado_entregado, null, false);

                tv_resultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_resultado);
                tv_fecha2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_fecha);
                tv_notificador2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_notificador);
                tv_longitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_longitud);
                tv_latitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_latitud);
                tv_observaciones2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_observaciones);
                tv_cabeceraResultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_cabecera_resultado);
                tv_receptor2 = (TextView)linearLayout2.findViewById(R.id.tv_result_entregado_receptor);
                tv_relacion_destinatario2 = (TextView)linearLayout2.findViewById(R.id.tv_result_entregado_relacion_destinatario);
                tv_XML2 = (TextView) linearLayout2.findViewById(R.id.tv_result_xml);
                tv_ST2 = (TextView) linearLayout2.findViewById(R.id.tv_result_st);

                if (notificacion.getFirmaNotificadorRes2() != null && !notificacion.getFirmaNotificadorRes2().isEmpty()) {
                    img_firma_receptor = (ImageView) linearLayout2.findViewById(R.id.imageView_result_entregado_firma);
                }
                if (!esAplicacionPEE) {
                    img_foto_acuse_res2 = (ImageView) linearLayout2.findViewById(R.id.imageView_result_entregado_foto_acuse);
                    img_foto_acuse_res2.setVisibility(View.VISIBLE);
                }


                // Se cargan los datos del segundo resultado en el layout2
                tv_resultado2.setText(notificacion.getResultado2() + " " + notificacion.getDescResultado2());
                tv_fecha2.setText(notificacion.getFechaHoraRes2());
                tv_notificador2.setText(notificacion.getNotificadorRes2());
                tv_longitud2.setText(notificacion.getLongitudRes2());
                tv_latitud2.setText(notificacion.getLatitudRes2());
                tv_observaciones2.setText(notificacion.getObservacionesRes2());
                tv_cabeceraResultado2.setText(R.string.resultado2);
                if (notificacion.getNombreReceptor() != null){
                    tv_receptor2.setText(notificacion.getNombreReceptor());
                } else {
                    tv_receptor2.setText("SIN RECEPTOR");
                }
                if (notificacion.getHayXML()){
                    tv_XML2.setText("EXISTE");
                } else {
                    tv_XML2.setText("NO EXISTE");
                }
                if (notificacion.getHayST()){
                    tv_ST2.setText("EXISTE");
                } else {
                    tv_ST2.setText("NO EXISTE");
                }
                tv_relacion_destinatario2.setText(notificacion.getRelacionDestinatario());


                // Buscamos la imagen de la firma si la hay
                if (notificacion.getFirmaReceptor() != null && notificacion.getFirmaReceptor().trim().length() > 0) {
                    try {
                        InputStream is = new FileInputStream(notificacion.getFirmaReceptor());
                        Drawable drw_imagenFirma = Drawable.createFromStream(is, "imageView");
                        img_firma_receptor.setImageDrawable(drw_imagenFirma);

                    } catch (Exception e) {
                        e.printStackTrace();
                        toast = Toast.makeText(DetalleNotificacionActivity.this, "No existe la Firma del Receptor", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                // Obtenemos la foto del acuse
                if (!esAplicacionPEE && notificacion.getFotoAcuseRes2() != null && notificacion.getFotoAcuseRes2().trim().length() > 0) {
                    try {
                        InputStream is = new FileInputStream(notificacion.getFotoAcuseRes2());
                        if (is != null) {
                            Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                            img_foto_acuse_res2.setImageDrawable(drw_imagenFoto);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                layoutResultado2.addView(linearLayout2);

            }

            // Solo hay 1 resultado y ENTREGADO
            // Si es la primera entrega y ya la hemos gestionado
            if(notificacion.getResultado2() == null) {
                LayoutInflater inflater2 = LayoutInflater.from(getBaseContext());
                LinearLayout linearLayout = null;
                resultadoEliminable = 1;

                // Resultado "ENTREGADO" con Firma y en Oficina o CERTIFICADA
                if((notificacion.getResultado1().equals(Util.RESULTADO_ENTREGADO)
                || (notificacion.getSegundoIntento() == false && notificacion.getResultado1().equals(Util.RESULTADO_AUSENTE))
                || (notificacion.getSegundoIntento() == false && notificacion.getResultado1().equals(Util.RESULTADO_NADIE_SE_HACE_CARGO))
                 || notificacion.getResultado1().equals(Util.RESULTADO_ENTREGADO_OFICINA))
                && !notificacion.getDescResultado1().equals(Util.RESULTADO_ENTREGADO_SIN_FIRMA)) {
                    // Instancio el layout para cargar los resultados de ENTREGADO
                    linearLayout =  (LinearLayout) inflater2.inflate(R.layout.datos_resultado_entregado, null, false);

                    // Se mapean las vistas del resultado 1
                    tv_resultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_resultado);
                    tv_fecha1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_fecha);
                    tv_notificador1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_notificador);
                    tv_longitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_longitud);
                    tv_latitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_latitud);
                    tv_observaciones1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_observaciones);
                    tv_receptor1 = (TextView)linearLayout.findViewById(R.id.tv_result_entregado_receptor);
                    tv_relacion_destinatario1 = (TextView)linearLayout.findViewById(R.id.tv_result_entregado_relacion_destinatario);
                    img_firma_receptor = (ImageView) linearLayout.findViewById(R.id.imageView_result_entregado_firma);
                    tv_XML1 = (TextView) linearLayout.findViewById(R.id.tv_result_xml);
                    tv_ST1 = (TextView) linearLayout.findViewById(R.id.tv_result_st);


                    img_foto_acuse_res1 = (ImageView) linearLayout.findViewById(R.id.imageView_result_entregado_foto_acuse);
                    if(!esAplicacionPEE) {
                        img_foto_acuse_res1.setVisibility(View.VISIBLE);
                    }   else{
                        img_foto_acuse_res1.setVisibility(View.INVISIBLE);
                    }
                    // Se cargan los datos del UNICO resultado en el layout1
                    tv_resultado1.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                    tv_fecha1.setText(notificacion.getFechaHoraRes1());
                    tv_notificador1.setText(notificacion.getNotificadorRes1());
                    tv_longitud1.setText(notificacion.getLongitudRes1());
                    tv_latitud1.setText(notificacion.getLatitudRes1());
                    tv_observaciones1.setText(notificacion.getObservacionesRes1());
                    if (notificacion.getNombreReceptor() != null){
                        tv_receptor1.setText(notificacion.getNombreReceptor());
                    } else {
                        tv_receptor1.setText("SIN RECEPTOR");
                    }
                    if (notificacion.getHayXML()){
                        tv_XML1.setText("EXISTE");
                    } else {
                        tv_XML1.setText("NO EXISTE");
                    }
                    if (notificacion.getHayST()){
                        tv_ST1.setText("EXISTE");
                    } else {
                        tv_ST1.setText("NO EXISTE");
                    }
                    tv_relacion_destinatario1.setText(notificacion.getRelacionDestinatario());

                    layoutResultado1.addView(linearLayout);

                    // Buscamos la imagen de la firma si la hay
                    if (notificacion.getFirmaReceptor() != null && notificacion.getFirmaReceptor().trim().length() > 0) {
                        try {

                            InputStream is = new FileInputStream(notificacion.getFirmaReceptor());
                            Drawable drw_imagenFirma = Drawable.createFromStream(is, "imageView");
                            img_firma_receptor.setImageDrawable(drw_imagenFirma);

                        } catch (Exception e) {
                            e.printStackTrace();
                            toast = Toast.makeText(DetalleNotificacionActivity.this, "No existe la Firma del Receptor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }

                    // Obtenemos la foto del acuse
                    if(!esAplicacionPEE && notificacion.getFotoAcuseRes1() != null && notificacion.getFotoAcuseRes1().trim().length() > 0) {
                        try {
                            InputStream is = new FileInputStream(notificacion.getFotoAcuseRes1());
                            if (is != null) {
                                Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                                img_foto_acuse_res1.setImageDrawable(drw_imagenFoto);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        img_foto_acuse_res1.setVisibility(View.INVISIBLE);
                    }

                    // El UNICO resultado NO "ENTREGADO"
                } else {
                    // Instancio el layout para cargar los resultados del NO ENTREGADO o ENTREGADO sin firma
                    linearLayout = (LinearLayout) inflater2.inflate(R.layout.datos_resultado_no_entregado, null, false);

                    // Se mapean las vistas del resultado 1
                    tv_resultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_resultado);
                    tv_fecha1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_fecha);
                    tv_notificador1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_notificador);
                    tv_longitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_longitud);
                    tv_latitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_latitud);
                    tv_observaciones1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_observaciones);
                    tv_cabeceraResultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_cabecera_resultado);
                    tv_cabeceraResultado1.setText(R.string.resultado1);
                    tv_XML1 = (TextView) linearLayout.findViewById(R.id.tv_result_xml);
                    tv_ST1 = (TextView) linearLayout.findViewById(R.id.tv_result_st);


                    img_foto_acuse_res1 = (ImageView) linearLayout.findViewById(R.id.imageView_result_no_entregado_foto_acuse);
                    if(!esAplicacionPEE) {
                        img_foto_acuse_res1.setVisibility(View.VISIBLE);
                    }   else{
                        img_foto_acuse_res1.setVisibility(View.INVISIBLE);
                    }
                    // Se cargan los datos del UNICO resultado en el layout1
                    tv_resultado1.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                    tv_fecha1.setText(notificacion.getFechaHoraRes1());
                    tv_notificador1.setText(notificacion.getNotificadorRes1());
                    tv_longitud1.setText(notificacion.getLongitudRes1());
                    tv_latitud1.setText(notificacion.getLatitudRes1());
                    tv_observaciones1.setText(notificacion.getObservacionesRes1());
                    tv_cabeceraResultado1.setText(R.string.resultado1);
                    if (notificacion.getHayXML()){
                        tv_XML1.setText("EXISTE");
                    } else {
                        tv_XML1.setText("NO EXISTE");
                    }
                    if (notificacion.getHayST()){
                        tv_ST1.setText("EXISTE");
                    } else {
                        tv_ST1.setText("NO EXISTE");
                    }

                    layoutResultado1.addView(linearLayout);

                    // Obtenemos la foto del acuse
                    if (!esAplicacionPEE && notificacion.getFotoAcuseRes1() != null && notificacion.getFotoAcuseRes1().trim().length() > 0) {
                        try {
                            InputStream is = new FileInputStream(notificacion.getFotoAcuseRes1());
                            if (is != null) {
                                Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                                img_foto_acuse_res1.setImageDrawable(drw_imagenFoto);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        img_foto_acuse_res1.setVisibility(View.INVISIBLE);
                    }
                }
            }
            progressDialog.dismiss();
        }
    }

    /**
     * Método privado que pide confiramación para eliminar el resultado
     */
    private void crearSelloTiempo() {
        String fallo = "";
        notificacion = dbHelper.obtenerNotificacion(idNotificacion);
        File ficheroXML = null;
        try {
            // Se genera el fichero XML
            //publishProgress(getString(R.string.generado_xml));
            ficheroXML = Util.NotificacionToXML(notificacion, getBaseContext());

            // Se realiza la llamada al servidor del sellado de tiempo y se genera el fichero de sello de tiempo
            Boolean tsaActivo = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_ACTIVO, getBaseContext(), Boolean.class.getSimpleName());
            if(BooleanUtils.isTrue(tsaActivo)) {
                //publishProgress(getString(R.string.generado_sello_de_tiempo));
                String tsaUrl = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_URL, getBaseContext(), String.class.getSimpleName());
                String tsaUser = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_USER, getBaseContext(), String.class.getSimpleName());
                TimeStampRequestParameters timeStampRequestParameters = null;
                if (StringUtils.isNotBlank(tsaUser)) {
                    String tsaPassword = Util.obtenerValorPreferencia(Util.CLAVE_PREFERENCIAS_TSA_PASSWORD, getBaseContext(), String.class.getSimpleName());
                    timeStampRequestParameters = new TimeStampRequestParameters();
                    timeStampRequestParameters.setUser(tsaUser);
                    timeStampRequestParameters.setPassword(tsaPassword);
                }
                TimeStamp t = TimeStamp.stampDocument(FileUtils.readFileToByteArray(ficheroXML), new URL(tsaUrl), timeStampRequestParameters, null);
                Util.guardarFicheroSelloTiempo(notificacion, t.toDER());
            }

        } catch (CiMobileException e) {
            fallo = e.getError();
        } catch (IOException e) {
            fallo = getString(R.string.error_lectura_fichero_xml);
        }
        //return fallo;
    }

    /**
     * Método privado que pide confiramación para eliminar el resultado
     */
    private void crearDialogoEliminarResultado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_resultado);
        builder.setMessage(R.string.seguro_eliminar_resultado);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Lanza la tarea en background para la eliminación del resultado
                EliminarResultadoNotificacionTask eliminarResultadoNotificacionTask = new EliminarResultadoNotificacionTask();
                eliminarResultadoNotificacionTask.execute();
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

    /**
     * Clase privada que se encarga de ejecutar en segundo planta la eliminacion del resultado de una notificacion
     * Además elimina el fichero físico donde se encuentra la imagen de la firma del receptor(si es que tuviera)
     * Tambíen debe eliminar el fichero XML y el sello de tiempo asociado
     */
    private class EliminarResultadoNotificacionTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DetalleNotificacionActivity.this, getString(R.string.eliminar_resultado), getString(R.string.espere_info_eliminar_resultado));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File file = null;
            String firmaPath = notificacion.getFirmaReceptor();
            String fotoAcuse1Path = notificacion.getFotoAcuseRes1();
            String fotoAcuse2Path = notificacion.getFotoAcuseRes2();
            String referencia = notificacion.getReferencia();

            Boolean eliminado = dbHelper.eliminarResultadoNotificacion(idNotificacion, resultadoEliminable);

            // Se elimina la firma del receptor si la tuviera
            if(BooleanUtils.isTrue(eliminado) && StringUtils.isNotBlank(firmaPath)) {
                try {
                    // Si se ha eliminado de la base de datos correctamente, se intenta eliminar si tuviera imagen asociada
                    file = new File(firmaPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Se elimina la FOTO1 si la tuviera
            if(BooleanUtils.isTrue(eliminado) && StringUtils.isNotBlank(fotoAcuse1Path)) {
                try {
                    // Si se ha eliminado de la base de datos correctamente, se intenta eliminar si tuviera imagen asociada
                    file = new File(fotoAcuse1Path);
                    if (file.exists()) {
                        file.delete();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Se elimina la FOTO2 si la tuviera
            if(BooleanUtils.isTrue(eliminado) && StringUtils.isNotBlank(fotoAcuse2Path)) {
                try {
                    // Si se ha eliminado de la base de datos correctamente, se intenta eliminar si tuviera imagen asociada
                    file = new File(fotoAcuse2Path);
                    if (file.exists()) {
                        file.delete();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Elimina el fichero xml
            try {
                String nombeFichero = referencia +  ".xml";
                file = new File(Util.obtenerRutaXML() + File.separator + nombeFichero);
                if (file.exists()) {
                    file.delete();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            // Elimina el fichero ts
            try {
                String nombeFichero = referencia + ".ts";
                file = new File(Util.obtenerRutaSelloDeTiempo() + File.separator + nombeFichero);
                if (file.exists()) {
                    file.delete();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            return eliminado;
        }

        @Override
        protected void onPostExecute(Boolean eliminado) {

            if(eliminado) {
                Intent intentResultado = new Intent();
                intentResultado.putExtra("eliminado", eliminado);
                intentResultado.putExtra("posicionAdapter", posicionAdapter);
                intentResultado.putExtra("idNotificacion", idNotificacion);
                setResult(CommonStatusCodes.SUCCESS, intentResultado);
                finish();
            } else {

            }
            progressDialog.dismiss();
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

