package es.correointeligente.cipostal.cimobile.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Adapters.NotificacionesAdapter;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.NotificacionProgressbar;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.BaseActivity;
import es.correointeligente.cipostal.cimobile.Util.DBHelper;
import es.correointeligente.cipostal.cimobile.Util.FiltroNotificacion;
import es.correointeligente.cipostal.cimobile.Util.Util;

public class ListaNotificacionesActivity extends BaseActivity {
    Toolbar mToolbar;
    DBHelper dbHelper;
    RecyclerView mRecyclerNotificaciones;
    FiltroNotificacion mFiltroNotificacion;
    List<Notificacion> listaNotificaciones;
    String[] listaFiltroBusqueda;
    EditText mRefPostal;
    ImageButton mButtonScanBar;
    boolean[] checkedFiltros;
    boolean isLoading = true;
    // Valor fijo del numero de notificaciones que mostrará por pagina en el endlessScroll
    static final int THRESHOLD_PAGINA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notificaciones);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Se inicializa la lista de notificaciones
        listaNotificaciones = new ArrayList<>();

        // Recupera la lista para el Pop-Up del filtro de busqueda
        listaFiltroBusqueda = getResources().getStringArray(R.array.filtro_busqueda_array);
        checkedFiltros = new boolean[listaFiltroBusqueda.length];

        // Obtenemos la instancia del helper de la base de datos
        dbHelper = new DBHelper(this);

        // Se mapea el editText donde se escribirá la ref postal
        this.mapearEditTextRefPostal();

        // Se mapea el botón del scanBar
        this.mapearImageButtonScanBar();

        // Se mapea el recycler
        mRecyclerNotificaciones = findViewById(R.id.recyclerView_listaNotificaciones);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerNotificaciones.setLayoutManager(layoutManager);

        mRecyclerNotificaciones.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && (recyclerView.getLayoutManager().getItemCount()) == ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() + 1) {
                    loadMoreData();
                }
            }
        });

        // Se lanza una tarea en background para recuperar las notificaciones
        ObtenerNotificacionesTask obtenerNotificacionesTask = new ObtenerNotificacionesTask();
        mFiltroNotificacion = new FiltroNotificacion();
        obtenerNotificacionesTask.execute(mFiltroNotificacion);
    }

    @Override
    protected int getLayoutResocurce() {
        return R.layout.activity_lista_notificaciones;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_notificaciones, menu);
        return true;
    }

    /**
     * Método que se encarga de hacer la petición de la siguiente pagina para el scroll una vez ha
     * llegado a los 10 items que se cargan (THRESHOLD_PAGINA)
     */
    public void loadMoreData() {
        mFiltroNotificacion.setPagina(mFiltroNotificacion.getPagina() + 1);

        // Lanza en background la petición de obtener mas notificaciones
        ObtenerNotificacionesTask obtenerNotificacionesTask = new ObtenerNotificacionesTask();
        obtenerNotificacionesTask.execute(mFiltroNotificacion);
    }

    // Este metodo es llamado desde el viewholder de las notificaciones para guardar en la base de datos el marcado
    public void actualizarNotificacionMarcada(Notificacion notificacion) {
        dbHelper.actualizarNotificacionMarcada(notificacion);
    }

    // Gestión de los Iconos de la barra de herramientas
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_filter_notificaciones:
                this.crearFiltroDeNotificacionesDialog();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clase privada que se ejecuta en segundo plano y se encarga de obtener las notificaiciones
     * en una lista infinita desde la base de datos interna
     */
    private class ObtenerNotificacionesTask extends AsyncTask<FiltroNotificacion, Void, List<Notificacion>> {

        @Override
        protected List<Notificacion> doInBackground(FiltroNotificacion... args) {
            FiltroNotificacion filtroNotificacion = args[0];

            List<Notificacion> listaNotificaciones;
            listaNotificaciones = dbHelper.obtenerNotificacionesPorFiltro(filtroNotificacion);

            return listaNotificaciones;
        }

        @Override
        protected void onPreExecute() {
            isLoading = true;
        }

        @Override
        protected void onPostExecute(List<Notificacion> listaNotificacionesConsulta) {

            if(listaNotificacionesConsulta.size() > 0) {
                int size = listaNotificaciones.size();

                if (mRecyclerNotificaciones.getAdapter() == null) {
                    listaNotificaciones.addAll(listaNotificacionesConsulta);

                    if (listaNotificacionesConsulta.size() > THRESHOLD_PAGINA) {
                        // Solo se añade el progressbar en caso de que haya mas resultado que el por defecto (THRESHOLD_PAGINA)
                        listaNotificaciones.add(new NotificacionProgressbar());
                    }
                    NotificacionesAdapter adapter = new NotificacionesAdapter(ListaNotificacionesActivity.this, listaNotificaciones);
                    mRecyclerNotificaciones.setAdapter(adapter);

                 } else {
                    int tamanyoCalculado = size;
                    if(listaNotificaciones.get(size - 1) instanceof NotificacionProgressbar) {
                        tamanyoCalculado = tamanyoCalculado - 1;
                        listaNotificaciones.remove(tamanyoCalculado);
                    }
                    listaNotificaciones.addAll(listaNotificacionesConsulta);
                    if (listaNotificacionesConsulta.size() == THRESHOLD_PAGINA) {
                        listaNotificaciones.add(new NotificacionProgressbar());
                    }

                    mRecyclerNotificaciones.getAdapter().notifyItemRangeChanged(tamanyoCalculado, listaNotificaciones.size() - size);

                }
            }

            isLoading = false;
        }
    }

    /**
     * Crea el dialogo (popUp) para seleccionar el filtrado y así mostrar solo las notificaciones por el criterio seleccionado
     */
    private void crearFiltroDeNotificacionesDialog() {

        // Se recuperan los filtros que ya estuvieran marcados anteriormente
        checkedFiltros[0] = mFiltroNotificacion.getEntregado();
        checkedFiltros[1] = mFiltroNotificacion.getDirIncorrecta();
        checkedFiltros[2] = mFiltroNotificacion.getAusente();
        checkedFiltros[3] = mFiltroNotificacion.getDesconocido();
        checkedFiltros[4] = mFiltroNotificacion.getFallecido();
        checkedFiltros[5] = mFiltroNotificacion.getRehusado();
        checkedFiltros[6] = mFiltroNotificacion.getNadieSeHaceCargo();
        checkedFiltros[7] = mFiltroNotificacion.getMarcadas();
        checkedFiltros[8] = mFiltroNotificacion.getNoEntregadoEnOficina();
        checkedFiltros[9] = mFiltroNotificacion.getEntregadoEnOficina();
        checkedFiltros[10] = mFiltroNotificacion.getSinFoto();


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ListaNotificacionesActivity.this);
        mBuilder.setTitle(R.string.filtro_busqueda);
        mBuilder.setMultiChoiceItems(listaFiltroBusqueda, checkedFiltros, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                checkedFiltros[position] = isChecked;
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(int position = 0; position < checkedFiltros.length; position++) {
                    switch (position) {
                        case 0:
                            mFiltroNotificacion.setEntregado(checkedFiltros[position]);
                            break;
                        case 1:
                            mFiltroNotificacion.setDirIncorrecta(checkedFiltros[position]);
                            break;
                        case 2:
                            mFiltroNotificacion.setAusente(checkedFiltros[position]);
                            break;
                        case 3:
                            mFiltroNotificacion.setDesconocido(checkedFiltros[position]);
                            break;
                        case 4:
                            mFiltroNotificacion.setFallecido(checkedFiltros[position]);
                            break;
                        case 5:
                            mFiltroNotificacion.setRehusado(checkedFiltros[position]);
                            break;
                        case 6:
                            mFiltroNotificacion.setNadieSeHaceCargo(checkedFiltros[position]);
                            break;
                        case 7:
                            mFiltroNotificacion.setMarcadas(checkedFiltros[position]);
                            break;
                        case 8:
                            mFiltroNotificacion.setEntregadoEnOficna(checkedFiltros[position]);
                            break;
                        case 9:
                            mFiltroNotificacion.setNoEntregadoEnOficina(checkedFiltros[position]);
                            break;
                        case 10:
                            mFiltroNotificacion.setSinFoto(checkedFiltros[position]);
                            break;

                    }
                }

                // Se reinicializa la búsqueda limpiando el adaptador y llamando al hilo de obtener notificaciones
                mRecyclerNotificaciones.setAdapter(null);
                listaNotificaciones = new ArrayList<>();
                mFiltroNotificacion.setPagina(0);
                ObtenerNotificacionesTask obtenerNotificacionesTask = new ObtenerNotificacionesTask();
                obtenerNotificacionesTask.execute(mFiltroNotificacion);

                // Se cierra el dialogo del filtrado
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Método privado que se encarga de mapear el editText donde se escribe la referencia,
     * se le añaden escuchadores para detectar que va escribiendo el usuario para ir haciendo
     * el filtro de búsqueda de forma dinámica
     */
    private void mapearEditTextRefPostal() {
        mRefPostal =  findViewById(R.id.editText_listaNotificaciones_refPostal);
        // Se fuerza que el inputText se haga entero en mayusculas
        mRefPostal.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mRefPostal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
               if(charSequence.length() > 2) {
                   // Se reinicializa la búsqueda limpiando el adaptador y llamando al hilo de obtener notificaciones
                   mRecyclerNotificaciones.setAdapter(null);
                   listaNotificaciones = new ArrayList<>();
                   mFiltroNotificacion.setPagina(0);
                   mFiltroNotificacion.setReferencia("'%" + charSequence + "%'");

                   ObtenerNotificacionesTask obtenerNotificacionesTask = new ObtenerNotificacionesTask();
                   obtenerNotificacionesTask.execute(mFiltroNotificacion);

               } else if(before > 2 && charSequence.length() <= 2) {
                   mRecyclerNotificaciones.setAdapter(null);
                   listaNotificaciones = new ArrayList<>();
                   mFiltroNotificacion.setPagina(0);
                   mFiltroNotificacion.setReferencia(null);

                   ObtenerNotificacionesTask obtenerNotificacionesTask = new ObtenerNotificacionesTask();
                   obtenerNotificacionesTask.execute(mFiltroNotificacion);
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Mapeo el boton de escaner de codigos de barra, y se le añade un escuchador para lanzar
     * su actividad
     */
    public void mapearImageButtonScanBar() {
        mButtonScanBar = findViewById(R.id.imageButton_listaNotificaciones_barScan);
        mButtonScanBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), ScanBarcodeActivity.class);
                startActivityForResult(i, Util.REQUEST_CODE_BARCODE_SCAN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Método que determina los resultados de las diferentes actividades que se han lanzado
        // y dependiendo de su requestCode sabemos que actividad ha sido.
        if (requestCode == Util.REQUEST_CODE_BARCODE_SCAN) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String barcode = data.getStringExtra("barcode");
                    mRefPostal.setText(barcode);
                } else {
                    mRefPostal.setText(null);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else if(requestCode == Util.REQUEST_CODE_NOTIFICATION_RESULT) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Integer posicionAdapter = data.getIntExtra("posicionAdapter",0);
                    Integer idNotificacion = data.getIntExtra("idNotificacion",0);
                    Notificacion notificacionAux = dbHelper.obtenerNotificacion(idNotificacion);
                    if(notificacionAux != null) {
                        listaNotificaciones.set(posicionAdapter, notificacionAux);
                        mRecyclerNotificaciones.getAdapter().notifyItemChanged(posicionAdapter);
                    }
                }
            }
        } else if(requestCode == Util.REQUEST_CODE_NOTIFICATION_DELETE_RESULT) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Boolean eliminado = data.getBooleanExtra("eliminado", false);
                    Integer posicionAdapter = data.getIntExtra("posicionAdapter",0);
                    Integer idNotificacion = data.getIntExtra("idNotificacion",0);
                    if(eliminado) {
                       Notificacion notificacionAux = dbHelper.obtenerNotificacion(idNotificacion);
                       if(notificacionAux != null) {
                            listaNotificaciones.set(posicionAdapter, notificacionAux);
                            mRecyclerNotificaciones.getAdapter().notifyItemChanged(posicionAdapter);
                       }
                    }
                }
            }
        }
    }
}