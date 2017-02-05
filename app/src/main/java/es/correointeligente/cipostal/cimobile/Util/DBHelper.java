package es.correointeligente.cipostal.cimobile.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Fichero;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Notificador;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.Model.ResumenReparto;

public class DBHelper extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "notificacionesManager";

    /*********************************************************/
    /************* TABLA NOTIFICADOR *************************/
    /*********************************************************/
    private static final String TABLE_NOTIFICADOR = "notificador";
    // Columnas tabla Notificador
    private static final String KEY_NOTIFICADOR_CODIGO = "codigo";
    private static final String KEY_NOTIFICADOR_NOMBRE = "nombre";
    private static final String KEY_NOTIFICADOR_DELEGACION = "delegacion";

    /*********************************************************/
    /************* TABLA FICHERO *****************************/
    /*********************************************************/
    private static final String TABLE_FICHERO = "fichero";

    // Columnas tabla fichero
    private static final String KEY_FICHERO_NOMBRE_FICHERO = "nombreFichero";
    private static final String KEY_FICHERO_CODIGO_CLIENTE = "codigoCliente";
    private static final String KEY_FICHERO_FECHA_FICHERO = "fechaFichero";
    private static final String KEY_FICHERO_NUMERO_REMESAS = "numRemesas";
    private static final String KEY_FICHERO_NUMERO_NOTIFICACIONES = "numNotificaciones";

    /*********************************************************/
    /************* TABLA NOTIFICACIION ***********************/
    /*********************************************************/
    private static final String TABLE_NOTIFICACION = "notificacion";
    // Columnas tabla Notificacion
    private static final String KEY_NOTIFICACION_REFERENCIA = "referencia";
    private static final String KEY_NOTIFICACION_NOMBRE = "nombre";
    private static final String KEY_NOTIFICACION_DIRECCION = "direccion";
    private static final String KEY_NOTIFICACION_POBLACION = "poblacion";
    private static final String KEY_NOTIFICACION_CODIGO_POSTAL = "codigoPostal";
    private static final String KEY_NOTIFICACION_RESULTADO_1 = "resultado1";
    private static final String KEY_NOTIFICACION_FECHA_HORA_RES_1 = "fechaHoraRes1";
    private static final String KEY_NOTIFICACION_NOTIFICADOR_RES_1 = "notificadorRes1";
    private static final String KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1 = "firmaNotificadorRes1";
    private static final String KEY_NOTIFICACION_RESULTADO_2 = "resultado2";
    private static final String KEY_NOTIFICACION_FECHA_HORA_RES_2 = "fechaHoraRes2";
    private static final String KEY_NOTIFICACION_NOTIFICADOR_RES_2 = "notificadorRes2";
    private static final String KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2 = "firmaNotificadorRes2";
    private static final String KEY_NOTIFICACION_TIPO_DOC_RECEPTOR = "tipoDocReceptor";
    private static final String KEY_NOTIFICACION_NUM_DOC_RECEPTOR = "numDocReceptor";
    private static final String KEY_NOTIFICACION_NOMBRE_RECEPTOR = "nombreReceptor";
    private static final String KEY_NOTIFICACION_FIRMA_RECEPTOR = "firmaReceptor";
    private static final String KEY_NOTIFICACION_LONGITUD = "longitud";
    private static final String KEY_NOTIFICACION_LATITUD = "latitud";
    private static final String KEY_NOTIFICACION_NOMBRE_FICHERO = "nombreFichero";
    private static final String KEY_NOTIFICACION_MARCADA = "marcada";
    private static final String KEY_NOTIFICACION_SEGUNDO_INTENTO = "segundoIntento";


    /*********************************************************/
    /************* TABLA RESULTADO ***************************/
    /*********************************************************/
    private static final String TABLE_RESULTADO = "resultado";
    // Columnas tabla Resultado
    private static final String KEY_RESULTADO_CODIGO = "codigo";
    private static final String KEY_RESULTADO_DESCRIPCION = "descripcion";
    private static final String KEY_RESULTADO_FINAL = "esFinal";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.crearTablaNotificador(sqLiteDatabase);
        this.crearTablaFichero(sqLiteDatabase);
        this.crearTablaNotificaciones(sqLiteDatabase);
        this.crearTablaResultados(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionAnterior, int versionNueva) {
        // Borrar las tabla antiguas si existe
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICADOR);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FICHERO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICACION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTADO);

        // Crea las tablas de nuevo
        onCreate(sqLiteDatabase);
    }

    /************************
     * QUERIES RESULTADOS
     *******************************************/
    public void addResultado(Resultado resultado) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RESULTADO_CODIGO, resultado.getCodigo());
        values.put(KEY_RESULTADO_DESCRIPCION, resultado.getDescripcion());
        values.put(KEY_RESULTADO_FINAL, resultado.getEsFinal());

        db.insert(TABLE_RESULTADO, null, values);
        db.close();
    }

    public Resultado getResultado(String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_RESULTADO,
                new String[]{
                        KEY_RESULTADO_CODIGO,
                        KEY_RESULTADO_DESCRIPCION,
                        KEY_RESULTADO_FINAL
                },
                KEY_RESULTADO_CODIGO + " = ?",
                new String[]{codigo},
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Resultado resultado = new Resultado();
        resultado.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO)));
        resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_DESCRIPCION)));
        resultado.setEsFinal(cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_FINAL)));

        return resultado;
    }

    public List<Resultado> getAllResultados() {
        List<Resultado> listaResultados = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RESULTADO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // loop thru rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    Resultado resultado = new Resultado();
                    resultado.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO)));
                    resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_DESCRIPCION)));
                    resultado.setEsFinal(cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_FINAL)));

                    listaResultados.add(resultado);

                } while (cursor.moveToNext());
            }
        }

        return listaResultados;
    }

    public int deleteResultado(Resultado resultado) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnDelete = db.delete(
                TABLE_RESULTADO,
                KEY_RESULTADO_CODIGO + " = ?",
                new String[]{resultado.getCodigo()}
        );

        db.close();

        return returnDelete;
    }

    /********************************************/
    /************ QUERIES PERSONALIZADAS ********/
    /********************************************/
    public ResumenReparto obtenerResumenReparto() {
        SQLiteDatabase db = this.getReadableDatabase();
        ResumenReparto resumenReparto = new ResumenReparto();

        // Totales del fichero
        Cursor cursor = db.query(true, TABLE_FICHERO, new String[]{KEY_FICHERO_NOMBRE_FICHERO}, null, null, null, null, null, null);
        resumenReparto.setTotFicheros(cursor.getCount());

        cursor = db.query(TABLE_FICHERO, new String[]{"sum(" + KEY_FICHERO_NUMERO_REMESAS + ")"}, null, null, null, null, null, null);
        resumenReparto.setTotRemesas(cursor.moveToFirst() ? cursor.getInt(0) : 0);

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA}, null, null, null, null, null, null);
        resumenReparto.setTotNotificaciones(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA}, KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL", null, null, null, null, null);
        resumenReparto.setTotNotifGestionadas(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA}, KEY_NOTIFICACION_MARCADA + " = ?", new String[]{"1"}, null, null, null, null);
        resumenReparto.setTotNotifMarcadas(cursor.getCount());

        // Detalle de las notificaciones

        // Entregado
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"01", "0", "01", "1"}, null, null, null, null);
        resumenReparto.setNumEntregados(cursor.getCount());

        // Dir. Incorrecta
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"02", "0", "02", "1"}, null, null, null, null);
        resumenReparto.setNumDirIncorrectas(cursor.getCount());

        // Ausente
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"03", "0", "03", "1"}, null, null, null, null);
        resumenReparto.setNumAusentes(cursor.getCount());

        // Desconocido
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"04", "0", "04", "1"}, null, null, null, null);
        resumenReparto.setNumDesconocidos(cursor.getCount());

        // Fallecido
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"05", "0", "05", "1"}, null, null, null, null);
        resumenReparto.setNumFallecidos(cursor.getCount());

        // Rehusado
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"06", "0", "06", "1"}, null, null, null, null);
        resumenReparto.setNumRehusados(cursor.getCount());

        // Nadie se hace cargo
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_REFERENCIA},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{"07", "0", "07", "1"}, null, null, null, null);
        resumenReparto.setNumNadieSeHaceCargo(cursor.getCount());


        return resumenReparto;
    }


    /********************************************/
    /************ QUERIES FICHEROS **************/
    /********************************************/
    public Fichero obtenerFichero(String nombreFichero) {
        Fichero fichero = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_FICHERO,
                new String[]{
                        KEY_FICHERO_NOMBRE_FICHERO,
                        KEY_FICHERO_CODIGO_CLIENTE,
                        KEY_FICHERO_FECHA_FICHERO,
                        KEY_FICHERO_NUMERO_REMESAS,
                        KEY_FICHERO_NUMERO_NOTIFICACIONES
                },
                KEY_FICHERO_NOMBRE_FICHERO + " = ?", new String[]{nombreFichero},
                null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                fichero = this.mapearCursorAFichero(cursor);
            }
        }

        return fichero;
    }

    public void guardarFicheroInicial(Fichero fichero, List<Notificacion> listaNotificaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put(KEY_FICHERO_NOMBRE_FICHERO, fichero.getNombreFichero());
            values.put(KEY_FICHERO_CODIGO_CLIENTE, fichero.getCodigoCliente());
            values.put(KEY_FICHERO_FECHA_FICHERO, fichero.getFechaFichero());
            values.put(KEY_FICHERO_NUMERO_REMESAS, fichero.getNumRemesas());
            values.put(KEY_FICHERO_NUMERO_NOTIFICACIONES, fichero.getNumNotificaciones());
            db.insert(TABLE_FICHERO, null, values);

            this.guardarNotificacionesInicial(listaNotificaciones, db);

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    private Fichero mapearCursorAFichero(Cursor cursor) {
        Fichero fichero = new Fichero();

        int columna = cursor.getColumnIndex(KEY_FICHERO_NOMBRE_FICHERO);
        if (columna != -1) {
            fichero.setNombreFichero(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_FICHERO_CODIGO_CLIENTE);
        if (columna != -1) {
            fichero.setCodigoCliente(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_FICHERO_FECHA_FICHERO);
        if (columna != -1) {
            fichero.setFechaFichero(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_FICHERO_NUMERO_REMESAS);
        if (columna != -1) {
            fichero.setNumRemesas(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_FICHERO_NUMERO_NOTIFICACIONES);
        if (columna != -1) {
            fichero.setNumNotificaciones(cursor.getString(columna));
        }

        return fichero;
    }
    /********************************************/
    /****** QUERIES NOTIFICACIONES **************/
    /********************************************/
    public void guardarNotificacionesInicial(List<Notificacion> listaNotificaciones, SQLiteDatabase db) {

        for (Notificacion notificacion : listaNotificaciones) {

            ContentValues values = new ContentValues();
            values.put(KEY_NOTIFICACION_REFERENCIA, notificacion.getReferencia());
            values.put(KEY_NOTIFICACION_NOMBRE, notificacion.getNombre());
            values.put(KEY_NOTIFICACION_DIRECCION, notificacion.getDireccion());
            values.put(KEY_NOTIFICACION_CODIGO_POSTAL, notificacion.getCodigoPostal());
            values.put(KEY_NOTIFICACION_MARCADA, notificacion.getMarcada());
            values.put(KEY_NOTIFICACION_SEGUNDO_INTENTO, notificacion.getSegundoIntento());
            values.put(KEY_NOTIFICACION_NOMBRE_FICHERO, notificacion.getNombreFichero());

            db.insert(TABLE_NOTIFICACION, null, values);
        }
    }

    public Notificacion obtenerNotificacion(String referencia) {
        Notificacion notificacion = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD,
                        KEY_NOTIFICACION_LATITUD,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA
                },
                KEY_NOTIFICACION_REFERENCIA + " = ?", new String[]{referencia},
                null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                notificacion = this.mapearCursorANotificacion(cursor);
            }
        }

        return notificacion;
    }

    public List<Notificacion> obtenerNotificacionesPorFiltro(FiltroNotificacion filtroNotificacion) {
        List<Notificacion> listaNotificaciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT * FROM " + TABLE_NOTIFICACION;
        query += " WHERE 1 = 1 ";

        if (filtroNotificacion.getReferencia() != null && filtroNotificacion.getReferencia().trim().length() > 0) {
            query += "AND (" + KEY_NOTIFICACION_REFERENCIA + " LIKE "+filtroNotificacion.getReferencia()+ ") ";
            query += "OR (" + KEY_NOTIFICACION_NOMBRE + " LIKE "+filtroNotificacion.getReferencia()+ ") ";
        }
        if (filtroNotificacion.getEntregado()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 01 ";
        }
        if (filtroNotificacion.getDirIncorrecta()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 02 ";
        }
        if (filtroNotificacion.getAusente()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 03 ";
        }
        if (filtroNotificacion.getDesconocido()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 04 ";
        }
        if (filtroNotificacion.getFallecido()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 05 ";
        }
        if (filtroNotificacion.getRehusado()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 06 ";
        }
        if (filtroNotificacion.getNadieSeHaceCargo()) {
            query += "AND " + KEY_NOTIFICACION_RESULTADO_1 + " = 07 ";
        }
        if (filtroNotificacion.getMarcadas()) {
            query += "AND " + KEY_NOTIFICACION_MARCADA + " = "+1+" ";
//            query += "ORDER BY "+KEY_NOTIFICACION_TIMESTAMP_MARCADA+" ASC ";
        } else {
            query += "ORDER BY "+KEY_NOTIFICACION_REFERENCIA+" ASC ";
        }

        if(filtroNotificacion.getPagina() > 0) {
            query += "LIMIT 10 OFFSET " + filtroNotificacion.getPagina() * 10 + " ";
        } else {
            query += "LIMIT 10 ";
        }

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Notificacion notificacion = mapearCursorANotificacion(cursor);
                    listaNotificaciones.add(notificacion);
                } while (cursor.moveToNext());
            }
        }

        return listaNotificaciones;
    }

    public List<Notificacion> obtenerTodasLasNotificaciones() {
        List<Notificacion> listaNotificaciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true,
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD,
                        KEY_NOTIFICACION_LATITUD,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA
                }, null, null, null, null, KEY_NOTIFICACION_REFERENCIA + " ASC", null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Notificacion notificacion = mapearCursorANotificacion(cursor);
                    listaNotificaciones.add(notificacion);
                } while (cursor.moveToNext());
            }
        }

        return listaNotificaciones;
    }

    public int deleteNotificacion(Notificacion notificacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnDelete = db.delete(
                TABLE_NOTIFICACION,
                KEY_NOTIFICACION_REFERENCIA + " = ?",
                new String[]{notificacion.getReferencia()}
        );

        db.close();

        return returnDelete;
    }

    public void actualizarNotificacionMarcada(Notificacion notificacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            Integer intBooleanValue = notificacion.getMarcada() ? 1 : 0;
            ContentValues cv = new ContentValues();
            cv.put(KEY_NOTIFICACION_MARCADA, intBooleanValue);
            db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_REFERENCIA + "= ?", new String[]{notificacion.getReferencia()});

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    private Notificacion mapearCursorANotificacion(Cursor cursor) {
        Notificacion notificacion = new Notificacion();

        int columna = cursor.getColumnIndex(KEY_NOTIFICACION_REFERENCIA);
        if (columna != -1) {
            notificacion.setReferencia(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NOMBRE);
        if (columna != -1) {
            notificacion.setNombre(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_DIRECCION);
        if (columna != -1) {
            notificacion.setDireccion(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_POBLACION);
        if (columna != -1) {
            notificacion.setPoblacion(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_CODIGO_POSTAL);
        if (columna != -1) {
            notificacion.setCodigoPostal(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FECHA_HORA_RES_1);
        if (columna != -1) {
            notificacion.setFechaHoraRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NOTIFICADOR_RES_1);
        if (columna != -1) {
            notificacion.setNotificadorRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1);
        if (columna != -1) {
            notificacion.setFirmaNotificadorRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_RESULTADO_2);
        if (columna != -1) {
            notificacion.setResultado2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FECHA_HORA_RES_2);
        if (columna != -1) {
            notificacion.setFechaHoraRes2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NOTIFICADOR_RES_2);
        if (columna != -1) {
            notificacion.setNotificadorRes2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2);
        if (columna != -1) {
            notificacion.setFirmaNotificadorRes2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_TIPO_DOC_RECEPTOR);
        if (columna != -1) {
            notificacion.setTipoDocReceptor(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NUM_DOC_RECEPTOR);
        if (columna != -1) {
            notificacion.setNumDocReceptor(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NOMBRE_RECEPTOR);
        if (columna != -1) {
            notificacion.setNombreReceptor(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FIRMA_RECEPTOR);
        if (columna != -1) {
            notificacion.setFirmaReceptor(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LONGITUD);
        if (columna != -1) {
            notificacion.setLongitud(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LATITUD);
        if (columna != -1) {
            notificacion.setLatitud(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_NOMBRE_FICHERO);
        if (columna != -1) {
            notificacion.setNombreFichero(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_MARCADA);
        if (columna != -1) {
            Integer marcada = cursor.getInt(columna);
            notificacion.setMarcada(marcada != null && marcada == 1 ? true : false);
        }

        return notificacion;
    }

    public void guardaResultadoNotificacion(Notificacion notificacion){
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();


            ContentValues cv = new ContentValues();
            if(notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()) {
                cv.put(KEY_NOTIFICACION_RESULTADO_1, notificacion.getResultado1());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_1, notificacion.getFechaHoraRes1());
                cv.put(KEY_NOTIFICACION_LONGITUD, notificacion.getLongitud());
                cv.put(KEY_NOTIFICACION_LATITUD, notificacion.getLatitud());

                db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_REFERENCIA + "= ?", new String[]{notificacion.getReferencia()});
            } else {
                cv.put(KEY_NOTIFICACION_RESULTADO_2, notificacion.getResultado2());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_2, notificacion.getFechaHoraRes2());
                cv.put(KEY_NOTIFICACION_LONGITUD, notificacion.getLongitud());
                cv.put(KEY_NOTIFICACION_LATITUD, notificacion.getLatitud());

                db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_REFERENCIA + "= ?", new String[]{notificacion.getReferencia()});
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }


    /************************
     * QUERIES NOTIFICADORES
     *******************************************/
    public void addNotificador(Notificador notificador) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICADOR_CODIGO, notificador.getCodigo());
        values.put(KEY_NOTIFICADOR_NOMBRE, notificador.getNombre());

        db.insert(TABLE_NOTIFICADOR, null, values);
        db.close();
    }

    public Notificador getNotificador(String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICADOR,
                new String[]{
                        KEY_NOTIFICADOR_CODIGO,
                        KEY_NOTIFICADOR_NOMBRE
                },
                KEY_NOTIFICADOR_CODIGO + " = ?",
                new String[]{codigo},
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Notificador notificador = new Notificador();
        notificador.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICADOR_CODIGO)));
        notificador.setNombre(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICADOR_NOMBRE)));

        return notificador;
    }

    public List<Notificador> getAllNotificadores() {
        List<Notificador> listaNotificadores = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NOTIFICADOR;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // loop thru rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    Notificador notificador = new Notificador();
                    notificador.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICADOR_CODIGO)));
                    notificador.setNombre(cursor.getString(cursor.getColumnIndex(KEY_NOTIFICADOR_NOMBRE)));
                    listaNotificadores.add(notificador);

                } while (cursor.moveToNext());
            }
        }

        return listaNotificadores;
    }

    public int deleteNotificador(Notificador notificador) {
        SQLiteDatabase db = this.getWritableDatabase();
        int returnDelete = db.delete(
                TABLE_NOTIFICADOR,
                KEY_NOTIFICADOR_CODIGO + " = ?",
                new String[]{notificador.getCodigo()}
        );

        db.close();

        return returnDelete;
    }


    /*******************************************************************/
    /******** CREACION DE LAS TABLAS DE LA BASE DE DATOS SQLITE ********/
    /*******************************************************************/
    private void crearTablaNotificador(SQLiteDatabase sqLiteDatabase) {

        String qry = "CREATE TABLE " + TABLE_NOTIFICADOR + "("
                + KEY_NOTIFICADOR_CODIGO + " TEXT, " + KEY_NOTIFICADOR_NOMBRE + " TEXT, "
                + KEY_NOTIFICADOR_DELEGACION + " TEXT);";

        sqLiteDatabase.execSQL(qry);
    }

    private void crearTablaFichero(SQLiteDatabase sqLiteDatabase) {
        String qry = "CREATE TABLE " + TABLE_FICHERO + "("
                + KEY_FICHERO_NOMBRE_FICHERO + " TEXT, " + KEY_FICHERO_CODIGO_CLIENTE + " TEXT, "
                + KEY_FICHERO_FECHA_FICHERO + " TEXT, " + KEY_FICHERO_NUMERO_REMESAS + " INTEGER, "
                + KEY_FICHERO_NUMERO_NOTIFICACIONES + " INTEGER);";

        sqLiteDatabase.execSQL(qry);
    }

    private void crearTablaNotificaciones(SQLiteDatabase sqLiteDatabase) {

        String qry = "CREATE TABLE " + TABLE_NOTIFICACION + "("
                + KEY_NOTIFICACION_REFERENCIA + " TEXT, " + KEY_NOTIFICACION_NOMBRE + " TEXT, "
                + KEY_NOTIFICACION_DIRECCION + " TEXT, " + KEY_NOTIFICACION_POBLACION + " TEXT, "
                + KEY_NOTIFICACION_CODIGO_POSTAL + " TEXT, " + KEY_NOTIFICACION_RESULTADO_1 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_1 + " TEXT, " + KEY_NOTIFICACION_NOTIFICADOR_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1 + " TEXT, " + KEY_NOTIFICACION_RESULTADO_2 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_2 + " TEXT, " + KEY_NOTIFICACION_NOTIFICADOR_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2 + " TEXT, " + KEY_NOTIFICACION_TIPO_DOC_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_NUM_DOC_RECEPTOR + " TEXT, " + KEY_NOTIFICACION_NOMBRE_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_RECEPTOR + " TEXT, " + KEY_NOTIFICACION_LONGITUD + " TEXT, "
                + KEY_NOTIFICACION_LATITUD + " TEXT, " + KEY_NOTIFICACION_MARCADA + " INTEGER, "
                + KEY_NOTIFICACION_SEGUNDO_INTENTO + " INTEGER, " + KEY_NOTIFICACION_NOMBRE_FICHERO + " TEXT); ";

        sqLiteDatabase.execSQL(qry);
    }

    private void crearTablaResultados(SQLiteDatabase sqLiteDatabase) {
        String qry = "CREATE TABLE " + TABLE_RESULTADO + "("
                + KEY_RESULTADO_CODIGO + " TEXT, " + KEY_RESULTADO_DESCRIPCION + " TEXT, "
                + KEY_RESULTADO_FINAL + " INTEGER);";

        sqLiteDatabase.execSQL(qry);
    }







}
