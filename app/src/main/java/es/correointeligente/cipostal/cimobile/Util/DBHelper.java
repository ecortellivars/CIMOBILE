package es.correointeligente.cipostal.cimobile.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.Model.ResumenReparto;
import es.correointeligente.cipostal.cimobile.R;

public class DBHelper extends SQLiteOpenHelper {

    //Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "notificacionesManager";

    /*********************************************************/
    /************* TABLA NOTIFICACION ***********************/
    /*********************************************************/
    private static final String TABLE_NOTIFICACION = "notificacion";
    // Columnas tabla Notificacion
    private static final String KEY_NOTIFICACION_ID = "id";
    private static final String KEY_NOTIFICACION_REFERENCIA = "referencia";
    private static final String KEY_NOTIFICACION_REFERENCIA_SCB = "referenciaSCB";
    private static final String KEY_NOTIFICACION_NOMBRE = "nombre";
    private static final String KEY_NOTIFICACION_DIRECCION = "direccion";
    private static final String KEY_NOTIFICACION_POBLACION = "poblacion";
    private static final String KEY_NOTIFICACION_CODIGO_POSTAL = "codigoPostal";
    private static final String KEY_NOTIFICACION_RESULTADO_1 = "resultado1";
    private static final String KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1 = "descResultado1";
    private static final String KEY_NOTIFICACION_FECHA_HORA_RES_1 = "fechaHoraRes1";
    private static final String KEY_NOTIFICACION_NOTIFICADOR_RES_1 = "notificadorRes1";
    private static final String KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1 = "firmaNotificadorRes1";
    private static final String KEY_NOTIFICACION_LONGITUD_RES_1 = "longitudRes1";
    private static final String KEY_NOTIFICACION_LATITUD_RES_1 = "latitudRes1";
    private static final String KEY_NOTIFICACION_OBSERVACIONES_RES_1 = "observacionesRes1";
    private static final String KEY_NOTIFICACION_RESULTADO_2 = "resultado2";
    private static final String KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2 = "descResultado2";
    private static final String KEY_NOTIFICACION_FECHA_HORA_RES_2 = "fechaHoraRes2";
    private static final String KEY_NOTIFICACION_NOTIFICADOR_RES_2 = "notificadorRes2";
    private static final String KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2 = "firmaNotificadorRes2";
    private static final String KEY_NOTIFICACION_LONGITUD_RES_2 = "longitudRes2";
    private static final String KEY_NOTIFICACION_LATITUD_RES_2 = "latitudRes2";
    private static final String KEY_NOTIFICACION_OBSERVACIONES_RES_2 = "observacionesRes2";
    private static final String KEY_NOTIFICACION_TIPO_DOC_RECEPTOR = "tipoDocReceptor";
    private static final String KEY_NOTIFICACION_NUM_DOC_RECEPTOR = "numDocReceptor";
    private static final String KEY_NOTIFICACION_NOMBRE_RECEPTOR = "nombreReceptor";
    private static final String KEY_NOTIFICACION_FIRMA_RECEPTOR = "firmaReceptor";
    private static final String KEY_NOTIFICACION_NOMBRE_FICHERO = "nombreFichero";
    private static final String KEY_NOTIFICACION_MARCADA = "marcada";
    private static final String KEY_NOTIFICACION_TIMESTAMP_MARCADA = "timestampMarcada";
    private static final String KEY_NOTIFICACION_SEGUNDO_INTENTO = "segundoIntento";


    /*********************************************************/
    /************* TABLA RESULTADO ***************************/
    /*********************************************************/
    private static final String TABLE_RESULTADO = "resultado";
    // Columnas tabla Resultado
    private static final String KEY_RESULTADO_CODIGO = "codigo";
    private static final String KEY_RESULTADO_DESCRIPCION = "descripcion";
    private static final String KEY_RESULTADO_FINAL = "esFinal";
    private static final String KEY_RESULTADO_RESULTADO_OFICINA = "esResultadoOficina";
    private static final String KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO = "codigoSegundoIntento";
    private static final String KEY_RESULTADO_NOTIFICA = "notifica";

    public DBHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.crearTablaNotificaciones(sqLiteDatabase);
        this.crearTablaResultados(sqLiteDatabase);
        this.crearResultadosPorDefecto(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionAnterior, int versionNueva) {
        // Borrar las tabla antiguas si existe
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICACION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTADO);

        // Crea las tablas de nuevo
        onCreate(sqLiteDatabase);
    }

    /*********************************************************/
    /************* QUERIES RESULTADOS ************************/
    /*********************************************************/
    public void guardarResultado(Resultado resultado) {
        SQLiteDatabase db = this.getWritableDatabase();

        Integer esFinal = resultado.getEsFinal() ? 1 : 0;
        Integer esResultadoOficina = resultado.getEsResultadoOficina() ? 1 : 0;
        Integer notifica = resultado.getNotifica() ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(KEY_RESULTADO_CODIGO, resultado.getCodigo());
        values.put(KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO, resultado.getCodigoSegundoIntento());
        values.put(KEY_RESULTADO_DESCRIPCION, resultado.getDescripcion());
        values.put(KEY_RESULTADO_FINAL, esFinal);
        values.put(KEY_RESULTADO_RESULTADO_OFICINA, esResultadoOficina);
        values.put(KEY_RESULTADO_NOTIFICA, notifica);


        db.insert(TABLE_RESULTADO, null, values);
        db.close();
    }

    public Resultado obtenerResultado(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RESULTADO,
                new String[]{
                        KEY_RESULTADO_CODIGO,
                        KEY_RESULTADO_DESCRIPCION,
                        KEY_RESULTADO_FINAL,
                        KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO,
                        KEY_RESULTADO_RESULTADO_OFICINA,
                        KEY_RESULTADO_NOTIFICA
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
        resultado.setCodigoSegundoIntento(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO)));
        resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_DESCRIPCION)));
        Integer intEsFinal = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_FINAL));
        resultado.setEsFinal(intEsFinal == 1 ? true : false);
        Integer intEsResultadoOficina = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_RESULTADO_OFICINA));
        resultado.setEsResultadoOficina(intEsResultadoOficina == 1 ? true : false);
        Integer intNotifica = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_NOTIFICA));
        resultado.setNotifica(intNotifica == 1 ? true : false);

        return resultado;
    }

    public List<Resultado> obtenerResultados() {
        List<Resultado> listaResultados = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RESULTADO;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    Resultado resultado = new Resultado();
                    resultado.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO)));
                    resultado.setCodigoSegundoIntento(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO)));
                    resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_DESCRIPCION)));
                    Integer intEsFinal = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_FINAL));
                    resultado.setEsFinal(intEsFinal == 1 ? true : false);
                    Integer intEsResultadoOficina = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_RESULTADO_OFICINA));
                    resultado.setEsResultadoOficina(intEsResultadoOficina == 1 ? true : false);
                    Integer intNotifica = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_NOTIFICA));
                    resultado.setNotifica(intNotifica == 1 ? true : false);

                    listaResultados.add(resultado);

                } while (cursor.moveToNext());
            }
        }

        return listaResultados;
    }

    public List<Resultado> obtenerResultadosNoNotifican() {
        List<Resultado> listaResultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RESULTADO +" WHERE "+KEY_RESULTADO_NOTIFICA +" = 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    Resultado resultado = new Resultado();
                    resultado.setCodigo(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO)));
                    resultado.setCodigoSegundoIntento(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO)));
                    resultado.setDescripcion(cursor.getString(cursor.getColumnIndex(KEY_RESULTADO_DESCRIPCION)));
                    Integer intEsFinal = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_FINAL));
                    resultado.setEsFinal(intEsFinal == 1 ? true : false);
                    Integer intEsResultadoOficina = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_RESULTADO_OFICINA));
                    resultado.setEsResultadoOficina(intEsResultadoOficina == 1 ? true : false);
                    Integer intNotifica = cursor.getInt(cursor.getColumnIndex(KEY_RESULTADO_NOTIFICA));
                    resultado.setNotifica(intNotifica == 1 ? true : false);

                    listaResultados.add(resultado);

                } while (cursor.moveToNext());
            }
        }

        return listaResultados;
    }

    /********************************************/
    /************ QUERIES PERSONALIZADAS ********/
    /********************************************/
    public Boolean existeFichero(String nombreFichero) {
        Boolean existe = Boolean.FALSE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_NOMBRE_FICHERO
                },
                KEY_NOTIFICACION_NOMBRE_FICHERO + " = ?", new String[]{nombreFichero},
                null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                existe = Boolean.TRUE;
            }
        }

        return existe;
    }

    public List<Notificacion> obtenerNotificacionesGestionadas() {
        List<Notificacion> listaNotificaciones = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_ID,
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_REFERENCIA_SCB,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD_RES_1,
                        KEY_NOTIFICACION_LATITUD_RES_1,
                        KEY_NOTIFICACION_LONGITUD_RES_2,
                        KEY_NOTIFICACION_LATITUD_RES_2,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_1,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_2,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA,
                        KEY_NOTIFICACION_TIMESTAMP_MARCADA,
                        KEY_NOTIFICACION_SEGUNDO_INTENTO
                },
                "("+KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0 AND " + KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL) OR "+
                "("+KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_2 + " IS NOT NULL)",
                null, null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    Notificacion notificacion = this.mapearCursorANotificacion(cursor);
                    listaNotificaciones.add(notificacion);

                } while (cursor.moveToNext());
            }
        }

        db.close();

        return listaNotificaciones;
    }

    public ResumenReparto obtenerResumenReparto() {
        SQLiteDatabase db = this.getReadableDatabase();
        ResumenReparto resumenReparto = new ResumenReparto();

        Cursor cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_NOMBRE_FICHERO}, null, null, null, null, null, null);
        resumenReparto.setTotFicheros(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID}, null, null, null, null, null, null);
        resumenReparto.setTotNotificaciones(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "("+KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0 AND " + KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL) OR "+
                "("+KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_2 + " IS NOT NULL)",
                null, null, null, null, null);
        resumenReparto.setTotNotifGestionadas(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID}, KEY_NOTIFICACION_MARCADA + " = ?", new String[]{"1"}, null, null, null, null);
        resumenReparto.setTotNotifMarcadas(cursor.getCount());

        // Detalle de las notificaciones

        // Entregado
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_ENTREGADO, "0", Util.RESULTADO_ENTREGADO, "1"}, null, null, null, null);
        resumenReparto.setNumEntregados(cursor.getCount());

        // Dir. Incorrecta
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_DIR_INCORRECTA, "0", Util.RESULTADO_DIR_INCORRECTA, "1"}, null, null, null, null);
        resumenReparto.setNumDirIncorrectas(cursor.getCount());

        // Ausente
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_AUSENTE, "0", Util.RESULTADO_AUSENTE, "1"}, null, null, null, null);
        resumenReparto.setNumAusentes(cursor.getCount());

        // Desconocido
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_DESCONOCIDO, "0", Util.RESULTADO_DESCONOCIDO, "1"}, null, null, null, null);
        resumenReparto.setNumDesconocidos(cursor.getCount());

        // Fallecido
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_FALLECIDO, "0", Util.RESULTADO_FALLECIDO, "1"}, null, null, null, null);
        resumenReparto.setNumFallecidos(cursor.getCount());

        // Rehusado
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_REHUSADO, "0", Util.RESULTADO_REHUSADO, "1"}, null, null, null, null);
        resumenReparto.setNumRehusados(cursor.getCount());

        // Nadie se hace cargo
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                        "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO, "0", Util.RESULTADO_NADIE_SE_HACE_CARGO, "1"}, null, null, null, null);
        resumenReparto.setNumNadieSeHaceCargo(cursor.getCount());


        return resumenReparto;
    }

    /********************************************/
    /****** QUERIES NOTIFICACIONES **************/
    /********************************************/
    public void guardarNotificacionesInicial(List<Notificacion> listaNotificaciones) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            Integer id = 1;
            for (Notificacion notificacion : listaNotificaciones) {

                ContentValues values = new ContentValues();
                values.put(KEY_NOTIFICACION_ID, id);
                values.put(KEY_NOTIFICACION_REFERENCIA, notificacion.getReferencia());
                values.put(KEY_NOTIFICACION_REFERENCIA_SCB, notificacion.getReferenciaSCB());
                values.put(KEY_NOTIFICACION_NOMBRE, notificacion.getNombre());
                values.put(KEY_NOTIFICACION_DIRECCION, notificacion.getDireccion());
                values.put(KEY_NOTIFICACION_CODIGO_POSTAL, notificacion.getCodigoPostal());
                values.put(KEY_NOTIFICACION_MARCADA, notificacion.getMarcada());
                values.put(KEY_NOTIFICACION_SEGUNDO_INTENTO, notificacion.getSegundoIntento());
                values.put(KEY_NOTIFICACION_NOMBRE_FICHERO, notificacion.getNombreFichero());

                db.insert(TABLE_NOTIFICACION, null, values);

                id ++;
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public Boolean actualizarNotificacionesSegundoIntentoInicial(List<Notificacion> listaNotificaciones) {
        Boolean guardadoOk = Boolean.TRUE;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            for (Notificacion notificacion : listaNotificaciones) {

                ContentValues cv = new ContentValues();
                cv.put(KEY_NOTIFICACION_RESULTADO_1, notificacion.getResultado1());
                cv.put(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1, notificacion.getDescResultado1());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_1, notificacion.getFechaHoraRes1());
                cv.put(KEY_NOTIFICACION_LONGITUD_RES_1, notificacion.getLongitudRes1());
                cv.put(KEY_NOTIFICACION_LATITUD_RES_1, notificacion.getLatitudRes1());
                cv.put(KEY_NOTIFICACION_NOTIFICADOR_RES_1, notificacion.getNotificadorRes1());
                cv.put(KEY_NOTIFICACION_SEGUNDO_INTENTO, 1);

                db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_ID + "= ?", new String[]{notificacion.getId().toString()});
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            guardadoOk = Boolean.FALSE;
        } finally {
            db.endTransaction();
        }

        db.close();

        return guardadoOk;
    }

    public Notificacion obtenerNotificacion(Integer idNotificacion) {
        Notificacion notificacion = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_ID,
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_REFERENCIA_SCB,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD_RES_1,
                        KEY_NOTIFICACION_LATITUD_RES_1,
                        KEY_NOTIFICACION_LONGITUD_RES_2,
                        KEY_NOTIFICACION_LATITUD_RES_2,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_1,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_2,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA,
                        KEY_NOTIFICACION_TIMESTAMP_MARCADA,
                        KEY_NOTIFICACION_SEGUNDO_INTENTO
                },
                KEY_NOTIFICACION_ID + " = ?", new String[]{idNotificacion.toString()},
                null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                notificacion = this.mapearCursorANotificacion(cursor);
            }
        }

        db.close();

        return notificacion;
    }

    public Notificacion obtenerNotificacion(String referencia) {
        Notificacion notificacion = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_ID,
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_REFERENCIA_SCB,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD_RES_1,
                        KEY_NOTIFICACION_LATITUD_RES_1,
                        KEY_NOTIFICACION_LONGITUD_RES_2,
                        KEY_NOTIFICACION_LATITUD_RES_2,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_1,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_2,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA,
                        KEY_NOTIFICACION_TIMESTAMP_MARCADA,
                        KEY_NOTIFICACION_SEGUNDO_INTENTO
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
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_ENTREGADO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_ENTREGADO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getDirIncorrecta()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_DIR_INCORRECTA+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_DIR_INCORRECTA+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getAusente()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_AUSENTE+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_AUSENTE+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getDesconocido()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_DESCONOCIDO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_DESCONOCIDO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getFallecido()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_FALLECIDO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_FALLECIDO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getRehusado()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_REHUSADO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_REHUSADO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getNadieSeHaceCargo()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '"+Util.RESULTADO_NADIE_SE_HACE_CARGO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                     "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '"+Util.RESULTADO_NADIE_SE_HACE_CARGO+"' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getMarcadas()) {
            query += "AND " + KEY_NOTIFICACION_MARCADA + " = "+1+" ";
            query += "ORDER BY "+KEY_NOTIFICACION_TIMESTAMP_MARCADA+" ASC ";
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

        db.close();

        return listaNotificaciones;
    }

    public List<Notificacion> obtenerTodasLasNotificaciones() {
        List<Notificacion> listaNotificaciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true,
                TABLE_NOTIFICACION,
                new String[]{
                        KEY_NOTIFICACION_ID,
                        KEY_NOTIFICACION_REFERENCIA,
                        KEY_NOTIFICACION_REFERENCIA_SCB,
                        KEY_NOTIFICACION_NOMBRE,
                        KEY_NOTIFICACION_DIRECCION,
                        KEY_NOTIFICACION_POBLACION,
                        KEY_NOTIFICACION_CODIGO_POSTAL,
                        KEY_NOTIFICACION_RESULTADO_1,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1,
                        KEY_NOTIFICACION_FECHA_HORA_RES_1,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1,
                        KEY_NOTIFICACION_RESULTADO_2,
                        KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2,
                        KEY_NOTIFICACION_FECHA_HORA_RES_2,
                        KEY_NOTIFICACION_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2,
                        KEY_NOTIFICACION_TIPO_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NUM_DOC_RECEPTOR,
                        KEY_NOTIFICACION_NOMBRE_RECEPTOR,
                        KEY_NOTIFICACION_FIRMA_RECEPTOR,
                        KEY_NOTIFICACION_LONGITUD_RES_1,
                        KEY_NOTIFICACION_LATITUD_RES_1,
                        KEY_NOTIFICACION_LONGITUD_RES_2,
                        KEY_NOTIFICACION_LATITUD_RES_2,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_1,
                        KEY_NOTIFICACION_OBSERVACIONES_RES_2,
                        KEY_NOTIFICACION_NOMBRE_FICHERO,
                        KEY_NOTIFICACION_MARCADA,
                        KEY_NOTIFICACION_TIMESTAMP_MARCADA
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

    public void actualizarNotificacionMarcada(Notificacion notificacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            Integer intBooleanValue = notificacion.getMarcada() ? 1 : 0;
            ContentValues cv = new ContentValues();
            cv.put(KEY_NOTIFICACION_MARCADA, intBooleanValue);
            cv.put(KEY_NOTIFICACION_TIMESTAMP_MARCADA, notificacion.getTimestampMarcada());
            db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_ID + "= ?", new String[]{notificacion.getId().toString()});

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

        int columna = cursor.getColumnIndex(KEY_NOTIFICACION_ID);
        if (columna != -1) {
            notificacion.setId(cursor.getInt(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_REFERENCIA);
        if (columna != -1) {
            notificacion.setReferencia(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_REFERENCIA_SCB);
        if (columna != -1) {
            notificacion.setReferenciaSCB(cursor.getString(columna));
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
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_RESULTADO_1);
        if (columna != -1) {
            notificacion.setResultado1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1);
        if (columna != -1) {
            notificacion.setDescResultado1(cursor.getString(columna));
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
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2);
        if (columna != -1) {
            notificacion.setDescResultado2(cursor.getString(columna));
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
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LONGITUD_RES_1);
        if (columna != -1) {
            notificacion.setLongitudRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LATITUD_RES_1);
        if (columna != -1) {
            notificacion.setLatitudRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LONGITUD_RES_2);
        if (columna != -1) {
            notificacion.setLongitudRes2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_LATITUD_RES_2);
        if (columna != -1) {
            notificacion.setLatitudRes2(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_OBSERVACIONES_RES_1);
        if (columna != -1) {
            notificacion.setObservacionesRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_OBSERVACIONES_RES_2);
        if (columna != -1) {
            notificacion.setObservacionesRes2(cursor.getString(columna));
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
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_TIMESTAMP_MARCADA);
        if (columna != -1) {
            notificacion.setTimestampMarcada(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_SEGUNDO_INTENTO);
        if (columna != -1) {
            Integer segundoIntento = cursor.getInt(columna);
            notificacion.setSegundoIntento(segundoIntento != null && segundoIntento == 1 ? true : false);
        }


        // Se mapea el backgroundcolor segun valores del resultado
        Integer colorBackground = R.color.colorBackgroundSinGestionar;
        Resultado resultado = null;
        if(notificacion.getSegundoIntento()) {
            if (notificacion.getResultado2() != null && notificacion.getResultado2().trim().length() > 0) {
                resultado = this.obtenerResultado(notificacion.getResultado2());
            }
        } else {
            if(notificacion.getResultado1() != null && notificacion.getResultado1().trim().length() > 0) {
                resultado = this.obtenerResultado(notificacion.getResultado1());
            }
        }

        if (resultado != null) {
            if (resultado.getNotifica()) {
                colorBackground = R.color.colorBackgroundEntregado;
            } else if (!resultado.getEsFinal()) {
                colorBackground = R.color.colorBackgroundAusente;
            } else if (resultado.getEsFinal() && !resultado.getNotifica()) {
                colorBackground = R.color.colorBackgroundNoEntregado;
            }
        }

        notificacion.setBackgroundColor(colorBackground);

        return notificacion;
    }

    public Boolean guardaResultadoNotificacion(Notificacion notificacion){
        Boolean guardadoOk = Boolean.TRUE;
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put(KEY_NOTIFICACION_FIRMA_RECEPTOR, notificacion.getFirmaReceptor());
            cv.put(KEY_NOTIFICACION_NOMBRE_RECEPTOR, notificacion.getNombreReceptor());
            cv.put(KEY_NOTIFICACION_NUM_DOC_RECEPTOR, notificacion.getNumDocReceptor());
            cv.put(KEY_NOTIFICACION_TIPO_DOC_RECEPTOR, notificacion.getTipoDocReceptor());

            if(notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento()) {
                cv.put(KEY_NOTIFICACION_RESULTADO_1, notificacion.getResultado1());
                cv.put(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1, notificacion.getDescResultado1());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_1, notificacion.getFechaHoraRes1());
                cv.put(KEY_NOTIFICACION_LONGITUD_RES_1, notificacion.getLongitudRes1());
                cv.put(KEY_NOTIFICACION_LATITUD_RES_1, notificacion.getLatitudRes1());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_1, notificacion.getObservacionesRes1());
                cv.put(KEY_NOTIFICACION_NOTIFICADOR_RES_1, notificacion.getNotificadorRes1());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_1, notificacion.getObservacionesRes1());

            } else {
                cv.put(KEY_NOTIFICACION_RESULTADO_2, notificacion.getResultado2());
                cv.put(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2, notificacion.getDescResultado2());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_2, notificacion.getFechaHoraRes2());
                cv.put(KEY_NOTIFICACION_LONGITUD_RES_2, notificacion.getLongitudRes2());
                cv.put(KEY_NOTIFICACION_LATITUD_RES_2, notificacion.getLatitudRes2());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_2, notificacion.getObservacionesRes2());
                cv.put(KEY_NOTIFICACION_NOTIFICADOR_RES_2, notificacion.getNotificadorRes2());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_2, notificacion.getObservacionesRes2());

            }

            db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_ID + "= ?", new String[]{notificacion.getId().toString()});

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            guardadoOk = Boolean.FALSE;
        } finally {
            db.endTransaction();
        }
        db.close();

        return guardadoOk;
    }

    public Boolean eliminarResultadoNotificacion(Integer idNotificacion, int resultado) {
        Boolean eliminado = Boolean.TRUE;

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues cv = new ContentValues();
            if(resultado == 1) {
                // Se eliminan todos los campos referentes al resultado 1
                cv.putNull(KEY_NOTIFICACION_RESULTADO_1);
                cv.putNull(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1);
                cv.putNull(KEY_NOTIFICACION_FECHA_HORA_RES_1);
                cv.putNull(KEY_NOTIFICACION_LONGITUD_RES_1);
                cv.putNull(KEY_NOTIFICACION_LATITUD_RES_1);
                cv.putNull(KEY_NOTIFICACION_OBSERVACIONES_RES_1);
            } else if(resultado == 2) {
                cv.putNull(KEY_NOTIFICACION_RESULTADO_2);
                cv.putNull(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2);
                cv.putNull(KEY_NOTIFICACION_FECHA_HORA_RES_2);
                cv.putNull(KEY_NOTIFICACION_LONGITUD_RES_2);
                cv.putNull(KEY_NOTIFICACION_LATITUD_RES_2);
                cv.putNull(KEY_NOTIFICACION_OBSERVACIONES_RES_2);
            }

            cv.putNull(KEY_NOTIFICACION_FIRMA_RECEPTOR);
            cv.putNull(KEY_NOTIFICACION_NOMBRE_RECEPTOR);
            cv.putNull(KEY_NOTIFICACION_NUM_DOC_RECEPTOR);
            cv.putNull(KEY_NOTIFICACION_TIPO_DOC_RECEPTOR);

            db.update(TABLE_NOTIFICACION, cv, KEY_NOTIFICACION_ID + "= ?", new String[]{idNotificacion.toString()});

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();


        return eliminado;
    }

    /*******************************************************************/
    /******** CREACION DE LAS TABLAS DE LA BASE DE DATOS SQLITE ********/
    /*******************************************************************/

    private void crearTablaNotificaciones(SQLiteDatabase sqLiteDatabase) {

        String qry = "CREATE TABLE " + TABLE_NOTIFICACION + "(" + KEY_NOTIFICACION_ID + " INT, "
                + KEY_NOTIFICACION_REFERENCIA + " TEXT, " + KEY_NOTIFICACION_REFERENCIA_SCB + " TEXT, "
                + KEY_NOTIFICACION_NOMBRE + " TEXT, "
                + KEY_NOTIFICACION_DIRECCION + " TEXT, " + KEY_NOTIFICACION_POBLACION + " TEXT, "
                + KEY_NOTIFICACION_CODIGO_POSTAL + " TEXT, " + KEY_NOTIFICACION_RESULTADO_1 + " TEXT, "
                + KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_1 + " TEXT, " + KEY_NOTIFICACION_NOTIFICADOR_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_OBSERVACIONES_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1 + " TEXT, " + KEY_NOTIFICACION_RESULTADO_2 + " TEXT, "
                + KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_2 + " TEXT, " + KEY_NOTIFICACION_NOTIFICADOR_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2 + " TEXT, " + KEY_NOTIFICACION_TIPO_DOC_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_NUM_DOC_RECEPTOR + " TEXT, " + KEY_NOTIFICACION_NOMBRE_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_RECEPTOR + " TEXT, " + KEY_NOTIFICACION_LONGITUD_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_LATITUD_RES_1 + " TEXT, " + KEY_NOTIFICACION_LONGITUD_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_LATITUD_RES_2 + " TEXT, " + KEY_NOTIFICACION_OBSERVACIONES_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_MARCADA + " INTEGER, "
                + KEY_NOTIFICACION_TIMESTAMP_MARCADA + " TEXT," + KEY_NOTIFICACION_SEGUNDO_INTENTO + " INTEGER, "
                + KEY_NOTIFICACION_NOMBRE_FICHERO + " TEXT); ";

        sqLiteDatabase.execSQL(qry);
    }

    private void crearTablaResultados(SQLiteDatabase sqLiteDatabase) {
        String qry = "CREATE TABLE " + TABLE_RESULTADO + "("
                + KEY_RESULTADO_CODIGO + " TEXT, " + KEY_RESULTADO_DESCRIPCION + " TEXT, "
                + KEY_RESULTADO_FINAL + " INTEGER, "+KEY_RESULTADO_RESULTADO_OFICINA +" INTEGER, "
                + KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO + " TEXT, " + KEY_RESULTADO_NOTIFICA + " INTEGER);";

        sqLiteDatabase.execSQL(qry);
    }


    private void crearResultadosPorDefecto(SQLiteDatabase db) {

        List<Resultado> listaResultados = new ArrayList<>();
        listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO, "Notificado", true, null, false, true));
        listaResultados.add(new Resultado(Util.RESULTADO_DIR_INCORRECTA, "Dirección Incorrecta", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_AUSENTE, "Ausente", false, "32", false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_DESCONOCIDO, "Desconocido", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_FALLECIDO, "Fallecido", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_REHUSADO, "Rehusado", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_NADIE_SE_HACE_CARGO, "Nadie se hace cargo", false, "33", false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO_OFICINA, "Entregado oficina", true, null, true, true));

        for (Resultado resultado: listaResultados) {
            Integer esFinal = resultado.getEsFinal() ? 1 : 0;
            Integer esResultadoOficina = resultado.getEsResultadoOficina() ? 1 : 0;
            Integer notifica = resultado.getNotifica() ? 1 : 0;

            ContentValues values = new ContentValues();
            values.put(KEY_RESULTADO_CODIGO, resultado.getCodigo());
            values.put(KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO, resultado.getCodigoSegundoIntento());
            values.put(KEY_RESULTADO_DESCRIPCION, resultado.getDescripcion());
            values.put(KEY_RESULTADO_FINAL, esFinal);
            values.put(KEY_RESULTADO_RESULTADO_OFICINA, esResultadoOficina);
            values.put(KEY_RESULTADO_NOTIFICA, notifica);

            db.insert(TABLE_RESULTADO, null, values);
        }
    }

}
