package es.correointeligente.cipostal.cimobile.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_borrar_notificaciones:
                this.crearDialogoEliminarResultado();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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

            notificacion = notificacionAux;

            tv_refPostal.setText(notificacion.getReferencia().toString());
            tv_refSCB.setText(notificacion.getReferenciaSCB().toString());
            tv_nombre.setText(notificacion.getNombre().toString());
            tv_direccion.setText(notificacion.getDireccion().toString());

            TextView tv_resultado1, tv_fecha1, tv_notificador1, tv_longitud1, tv_latitud1, tv_observaciones1, tv_cabeceraResultado1, tv_receptor;
            TextView tv_resultado2, tv_fecha2, tv_notificador2, tv_longitud2, tv_latitud2, tv_observaciones2, tv_cabeceraResultado2;
            ImageView img_firma_receptor;
            ImageView img_foto_acuse_res1;
            ImageView img_foto_acuse_res2;

            Toast toast = null;

            // Hay dos resultados por lo que relleno 2 layouts
            if(notificacion.getResultado2() != null) {

                LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                // Instancio el linearLayout1 para cargar los datos del primer intento
                LinearLayout linearLayout1 =  (LinearLayout) inflater.inflate(R.layout.datos_resultado_no_entregado, null, false);
                LinearLayout linearLayout2 = null;

                resultadoEliminable = 2;

                tv_resultado1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_resultado);
                tv_fecha1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_fecha);
                tv_notificador1 = (TextView) linearLayout1.findViewById(R.id.tv_result_no_entregado_notificador);
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
                // Lo agrego al layout principal
                layoutResultado1.addView(linearLayout1);

                // Si el segundo intento es Entregado o Entregado en Oficina
                // pero no ENTREGA SIN FIRMA cargo datos, firma y foto
                if(notificacion.getResultado2().equals(Util.RESULTADO_ENTREGADO)
                || notificacion.getResultado2().equals(Util.RESULTADO_ENTREGADO_OFICINA)
                && !notificacion.getDescResultado2().equals(Util.RESULTADO_ENTREGADO_SIN_FIRMA)) {
                    // Instancio el otro layout para cargar los resultados del segundo intento ENTREGADO
                    linearLayout2 = (LinearLayout) inflater.inflate(R.layout.datos_resultado_entregado, null, false);

                    tv_resultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_resultado);
                    tv_fecha2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_fecha);
                    tv_notificador2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_notificador);
                    tv_longitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_longitud);
                    tv_latitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_latitud);
                    tv_observaciones2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_observaciones);
                    tv_cabeceraResultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_entregado_cabecera_resultado);
                    tv_receptor = (TextView)linearLayout2.findViewById(R.id.tv_result_entregado_receptor);
                    img_firma_receptor = (ImageView) linearLayout2.findViewById(R.id.imageView_result_entregado_firma);
                    img_foto_acuse_res2 = (ImageView) linearLayout2.findViewById(R.id.imageView_result_entregado_foto_acuse);

                    // Se cargan los datos del segundo resultado en el layout2
                    tv_resultado2.setText(notificacion.getResultado2() + " " + notificacion.getDescResultado2());
                    tv_fecha2.setText(notificacion.getFechaHoraRes2());
                    tv_notificador2.setText(notificacion.getNotificadorRes2());
                    tv_longitud2.setText(notificacion.getLongitudRes2());
                    tv_latitud2.setText(notificacion.getLatitudRes2());
                    tv_observaciones2.setText(notificacion.getObservacionesRes2());
                    tv_cabeceraResultado2.setText(R.string.resultado2);

                    // Como es ENTREGADO debe haber firma del receptor
                    tv_receptor.setText(notificacion.getNumDocReceptor() + " " + notificacion.getNombreReceptor());

                    // Buscamos la imagen de la firma
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
                    if(notificacion.getFotoAcuseRes2() != null && notificacion.getFotoAcuseRes2().trim().length() > 0) {
                        try {

                            InputStream is = new FileInputStream(notificacion.getFotoAcuseRes2());
                            if (is != null){
                                Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                                img_foto_acuse_res2.setImageDrawable(drw_imagenFoto);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        layoutResultado2.addView(linearLayout2);
                    }

                // Si NO es ENTREGADO lo mismo pero sin firma
                } else {
                    // Instancio el otro layout para cargar los resultados del segundo intento NO ENTREGADO
                    linearLayout2 = (LinearLayout) inflater.inflate(R.layout.datos_resultado_no_entregado, null, false);

                    tv_resultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_resultado);
                    tv_fecha2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_fecha);
                    tv_notificador2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_notificador);
                    tv_longitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_longitud);
                    tv_latitud2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_latitud);
                    tv_observaciones2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_observaciones);
                    tv_cabeceraResultado2 = (TextView) linearLayout2.findViewById(R.id.tv_result_no_entregado_cabecera_resultado);
                    img_foto_acuse_res2 = (ImageView) linearLayout2.findViewById(R.id.imageView_result_no_entregado_foto_acuse);

                    // Se cargan los datos del segundo resultado en el layout2
                    tv_resultado2.setText(notificacion.getResultado2() + " " + notificacion.getDescResultado2());
                    tv_fecha2.setText(notificacion.getFechaHoraRes2());
                    tv_notificador2.setText(notificacion.getNotificadorRes2());
                    tv_longitud2.setText(notificacion.getLongitudRes2());
                    tv_latitud2.setText(notificacion.getLatitudRes2());
                    tv_observaciones2.setText(notificacion.getObservacionesRes2());
                    tv_cabeceraResultado2.setText(R.string.resultado2);

                    // Obtenemos la foto del acuse
                    if (notificacion.getFotoAcuseRes2() != null && notificacion.getFotoAcuseRes2().trim().length() > 0) {
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

            // Solo hay 1 resultado
            // Si es la primera entrega y ya la hemos gestionado
            } else {

                LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                LinearLayout linearLayout = null;
                resultadoEliminable = 1;

                // Resultado "ENTREGADO" con Firma y en Oficina
                if((notificacion.getResultado1().equals(Util.RESULTADO_ENTREGADO)
                || notificacion.getResultado1().equals(Util.RESULTADO_ENTREGADO_OFICINA))
                && !notificacion.getDescResultado1().equals(Util.RESULTADO_ENTREGADO_SIN_FIRMA)) {
                    // Instancio el layout para cargar los resultados de ENTREGADO
                    linearLayout =  (LinearLayout) inflater.inflate(R.layout.datos_resultado_entregado, null, false);

                    // Se mapean las vistas del resultado 1
                    tv_resultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_resultado);
                    tv_fecha1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_fecha);
                    tv_notificador1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_notificador);
                    tv_longitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_longitud);
                    tv_latitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_latitud);
                    tv_observaciones1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_observaciones);
                    tv_cabeceraResultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_entregado_cabecera_resultado);
                    tv_receptor = (TextView)linearLayout.findViewById(R.id.tv_result_entregado_receptor);
                    tv_receptor.setText(notificacion.getNumDocReceptor() + " " + notificacion.getNombreReceptor());
                    img_firma_receptor = (ImageView) linearLayout.findViewById(R.id.imageView_result_entregado_firma);
                    img_foto_acuse_res1 = (ImageView) linearLayout.findViewById(R.id.imageView_result_entregado_foto_acuse);

                    // Se cargan los datos del UNICO resultado en el layout1
                    tv_resultado1.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                    tv_fecha1.setText(notificacion.getFechaHoraRes1());
                    tv_notificador1.setText(notificacion.getNotificadorRes1());
                    tv_longitud1.setText(notificacion.getLongitudRes1());
                    tv_latitud1.setText(notificacion.getLatitudRes1());
                    tv_observaciones1.setText(notificacion.getObservacionesRes1());
                    tv_cabeceraResultado1.setText(R.string.resultado1);

                    layoutResultado1.addView(linearLayout);


                    // Buscamos la imagen de la firma
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
                    if(notificacion.getFotoAcuseRes1() != null && notificacion.getFotoAcuseRes1().trim().length() > 0) {
                        try {
                            InputStream is = new FileInputStream(notificacion.getFotoAcuseRes1());
                            if (is != null) {
                                Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                                img_foto_acuse_res1.setImageDrawable(drw_imagenFoto);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                // El UNICO resultado NO es "ENTREGADO" con Firma
                } else {
                    // Instancio el layout para cargar los resultados del NO ENTREGADO o ENTREGADO sin firma
                    linearLayout = (LinearLayout) inflater.inflate(R.layout.datos_resultado_no_entregado, null, false);

                    // Se mapean las vistas del resultado 1
                    tv_resultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_resultado);
                    tv_fecha1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_fecha);
                    tv_notificador1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_notificador);
                    tv_longitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_longitud);
                    tv_latitud1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_latitud);
                    tv_observaciones1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_observaciones);
                    tv_cabeceraResultado1 = (TextView) linearLayout.findViewById(R.id.tv_result_no_entregado_cabecera_resultado);
                    img_foto_acuse_res1 = (ImageView) linearLayout.findViewById(R.id.imageView_result_no_entregado_foto_acuse);

                    // Se cargan los datos del UNICO resultado en el layout1
                    tv_resultado1.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
                    tv_fecha1.setText(notificacion.getFechaHoraRes1());
                    tv_notificador1.setText(notificacion.getNotificadorRes1());
                    tv_longitud1.setText(notificacion.getLongitudRes1());
                    tv_latitud1.setText(notificacion.getLatitudRes1());
                    tv_observaciones1.setText(notificacion.getObservacionesRes1());
                    tv_cabeceraResultado1.setText(R.string.resultado1);

                    layoutResultado1.addView(linearLayout);

                    // Obtenemos la foto del acuse
                    if (notificacion.getFotoAcuseRes1() != null && notificacion.getFotoAcuseRes1().trim().length() > 0) {
                        try {
                            InputStream is = new FileInputStream(notificacion.getFotoAcuseRes1());
                            if (is != null) {
                                Drawable drw_imagenFoto = Drawable.createFromStream(is, "imageView");
                                img_foto_acuse_res1.setImageDrawable(drw_imagenFoto);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            progressDialog.dismiss();
        }
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
            String referencia = notificacion.getReferencia();
            String referenciaSCB = notificacion.getReferenciaSCB();

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

            // Elimina el fichero xml
            try {
                String nombeFichero = referencia+"_"+StringUtils.defaultIfBlank(referenciaSCB,"")+".xml";
                file = new File(Util.obtenerRutaXML()+File.separator+nombeFichero);
                if (file.exists()) {
                    file.delete();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            // Elimina el fichero xml
            try {
                String nombeFichero = referencia+"_"+StringUtils.defaultIfBlank(referenciaSCB,"")+".ts";
                file = new File(Util.obtenerRutaSelloDeTiempo()+File.separator+nombeFichero);
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
}

