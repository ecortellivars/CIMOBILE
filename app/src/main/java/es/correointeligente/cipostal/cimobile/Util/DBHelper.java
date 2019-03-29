package es.correointeligente.cipostal.cimobile.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.Resultado;
import es.correointeligente.cipostal.cimobile.Model.ResumenReparto;
import es.correointeligente.cipostal.cimobile.R;



public class DBHelper extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "notificacionesManager";

    private Context context;

    /******************************************************************************************/
    /****************************ESTRUCTURA TABLA NOTIFICACION ********************************/
    /******************************************************************************************/
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
    private static final String KEY_NOTIFICACION_FOTO_ACUSE_RES_1 = "fotoAcuse1";
    private static final String KEY_NOTIFICACION_FOTO_ACUSE_RES_2 = "fotoAcuse2";


    /******************************************************************************************/
    /*****************************ESTRUCTURA TABLA RESULTADO **********************************/
    /******************************************************************************************/
    private static final String TABLE_RESULTADO = "resultado";
    // Columnas tabla Resultado
    private static final String KEY_RESULTADO_CODIGO = "codigo";
    private static final String KEY_RESULTADO_DESCRIPCION = "descripcion";
    private static final String KEY_RESULTADO_FINAL = "esFinal";
    private static final String KEY_RESULTADO_RESULTADO_OFICINA = "esResultadoOficina";
    private static final String KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO = "codigoSegundoIntento";
    private static final String KEY_RESULTADO_NOTIFICA = "notifica";

    // Constructor
    public DBHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION); this.context = context;}

    // CREATEs
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.crearTablaNotificaciones(sqLiteDatabase);
        this.crearTablaResultados(sqLiteDatabase);
        this.crearResultadosPorDefecto(sqLiteDatabase);
    }

    // UPDATE
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionAnterior, int versionNueva) {
        // Borrar las tabla antiguas si existe
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICACION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTADO);

        // Crea las tablas de nuevo
        onCreate(sqLiteDatabase);
    }

    /******************************************************************************************/
    /******************************** QUERIES RESULTADOS **************************************/
    /******************************************************************************************/

    /**INSERT INTO
     * Guarda el tipo de resultado en la base de datos
     * @param resultado
     */
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

    /** SELECT
     * Obtiene el resultado por su código
     * @param codigo
     * @return Resultado
     */
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

    /**
     * Crea los resultados por defecto de la aplicación
     * @param db
     */
    private void crearResultadosPorDefecto(SQLiteDatabase db) {
        //Resultado = codigo, descripcion, esFinal, codigoSegundoIntento,  esResultadoOficina, notifica
        List<Resultado> listaResultados = new ArrayList<>();
        // Resultados FINALES
        listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO, "Notificado en Domicilio", true, null, false, true));
        //listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO_SIN_FIRMA, "Notificado en domicilio sin firma", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_DIR_INCORRECTA, "Dirección Incorrecta", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_DESCONOCIDO, "Desconocido", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_FALLECIDO, "Fallecido", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_REHUSADO, "Rehusado", true, null, false, false));
        listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO_OFICINA, "Entregado en oficina", true, null, false, true));
        //listaResultados.add(new Resultado(Util.RESULTADO_ENTREGADO_OFICINA_CON_FIRMA, "Entregado en oficina con Firma", true, null, true, true));
        listaResultados.add(new Resultado(Util.RESULTADO_NO_ENTREGADO_OFICINA, "NO Entregado en oficina", true, null, true, false));

        // Resultado NO FINAL
        listaResultados.add(new Resultado(Util.RESULTADO_AUSENTE, "Ausente (1ª Visita)", false, "32", false, false));
        // Resultado FINAL
        listaResultados.add(new Resultado(Util.RESULTADO_AUSENTE_SEGUNDO, "Ausente (2ª Visita)", true, null, false, false));
        // Resultado NO FINAL
        listaResultados.add(new Resultado(Util.RESULTADO_NADIE_SE_HACE_CARGO, "Nadie se hace cargo (1ª Visita)", false, "33", false, false));
        // Resultado FINAL
        listaResultados.add(new Resultado(Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO, "Nadie se hace cargo (2ª Visita)", true, null, false, false));


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
    /** SELECT
     * Obtiene los posibles 2º intentos
     * @return List<Resultado>
     */
    public List<Resultado> obtenerResultadosFinales() {

        List<Resultado> listaResultados = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RESULTADO +
                      " WHERE " + KEY_RESULTADO_FINAL + " = 1 " +
                      //" AND " + KEY_RESULTADO_NOTIFICA + " = 1 " +
                      " AND " + KEY_RESULTADO_RESULTADO_OFICINA + " = 0 ";

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

        db.close();

        return listaResultados;
    }

    /** SELECT
     * Obtiene aquellos tipos de resultado que NO finales
     * @return
     */
    public List<Resultado> obtenerResultadosNoFinales() {
        List<Resultado> listaResultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RESULTADO +
                       " WHERE " + KEY_RESULTADO_DESCRIPCION + " IN ('Notificado en Domicilio','Dirección Incorrecta','Desconocido','Fallecido', 'Rehusado', 'Ausente (1ª Visita)', 'Nadie se hace cargo (1ª Visita)') " +
                       " ORDER BY " + KEY_RESULTADO_CODIGO;

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

        db.close();

        return listaResultados;
    }

    /** SELECT
     * Obtiene aquellos tipos de resultado que NO finales
     * @return
     */
    public List<Resultado> obtenerResultadosNoFinalesPEE() {
        List<Resultado> listaResultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RESULTADO +
                " WHERE " + KEY_RESULTADO_DESCRIPCION + " IN ('Dirección Incorrecta','Desconocido','Fallecido', 'Rehusado', 'Ausente (1ª Visita)', 'Nadie se hace cargo (1ª Visita)') " +
                " ORDER BY " + KEY_RESULTADO_CODIGO;

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

        db.close();

        return listaResultados;
    }

    /** SELECT
     * Obtiene los posibles 2º intentos
     * @return List<Resultado>
     */
    public List<Resultado> obtenerResultadosEnOficina() {

        List<Resultado> listaResultados = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RESULTADO +
                " WHERE " + KEY_RESULTADO_DESCRIPCION + " IN ('NO Entregado en oficina') " +
                " ORDER BY " + KEY_RESULTADO_CODIGO;

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

        db.close();

        return listaResultados;
    }


    /******************************************************************************************/
    /******************************* QUERIES PERSONALIZADAS ***********************************/
    /******************************************************************************************/

    /**
     * Valida si ya se ha incluido el fichero a cargar anteriormente
     * @param nombreFichero
     * @return Boolean
     */
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

        db.close();

        return existe;
    }

    /**
     * Obtiene solo las notificaciones que han sido gestionadas por el notificador, es decir aquellas
     * que se les haya incluido algún resultado postal
     * @return List<Notificacion>
     */
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
                        KEY_NOTIFICACION_SEGUNDO_INTENTO,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_1,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_2
                },
                "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0 AND " + KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL) OR "+
                "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_2 + " IS NOT NULL)",
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

    /**
     * Obtiene un detalle global de como estan las notificaciones
     * @return ResumenReparto
     */
    public ResumenReparto obtenerResumenReparto() {
        SQLiteDatabase db = this.getReadableDatabase();
        ResumenReparto resumenReparto = new ResumenReparto();

        Cursor cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_NOMBRE_FICHERO}, null, null, null, null, null, null);
        resumenReparto.setTotFicheros(cursor.getCount());

        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID}, null, null, null, null, null, null);
        resumenReparto.setTotNotificaciones(cursor.getCount());

        // Gestionada
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0 AND " + KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL) OR " +
                         "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_2 + " IS NOT NULL)" ,
                null, null, null, null, null);
        resumenReparto.setTotNotifGestionadas(cursor.getCount());

        // Resultados
        cursor = db.query(false, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " IS NOT NULL) OR " +
                         "(" + KEY_NOTIFICACION_RESULTADO_2 + " IS NOT NULL)" ,
                null, null, null, null, null);
        resumenReparto.setTotResultados(cursor.getCount());

        // Los segundos intentos que se pueden hacer hoy
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_1 + " = ?) OR " +
                         "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1 AND " + KEY_NOTIFICACION_RESULTADO_1 + " = ? )" ,
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO, Util.RESULTADO_AUSENTE},
                null, null, null, null, null);
        resumenReparto.setTotNotifPendientesSegundoHoy(cursor.getCount());

        // Los segundos intentos que NO se pueden hacer hoy
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0 AND " + KEY_NOTIFICACION_RESULTADO_2 + " IS NULL) AND " +
                         "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? OR " + KEY_NOTIFICACION_RESULTADO_1 + " = ?) " ,
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO, Util.RESULTADO_AUSENTE}, null, null, null, null);
        resumenReparto.setTotNotifPendientesSegundoOtroDia(cursor.getCount());

        cursor = db.    query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID}, KEY_NOTIFICACION_MARCADA + " = ?", new String[]{"1"}, null, null, null, null);
        resumenReparto.setTotNotifMarcadas(cursor.getCount());

        // Lista
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? " + "OR " + KEY_NOTIFICACION_RESULTADO_1 + " = ? )",
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO,Util.RESULTADO_AUSENTE_SEGUNDO}, null, null, null, null);
        resumenReparto.setTotNumLista(cursor.getCount());


        // Detalle de las notificaciones

        // SELECT DISTINCT id
        // FROM notificacion
        // WHERE (resultado1 = ? AND segundoIntento = ?) OR (resultado2 = ? AND segundoIntento = ?)

        // (boolean distinct,
        // String table,
        // String[] columns,
        // String selection,
        // String[] selectionArgs,
        // String groupBy,
        // String having,
        // String orderBy,
        // String limit)

        // Entregado
        cursor = db.query(
                          // @param distinct true if you want each row to be unique, false otherwise.
                          true,
                          //  @param table The table name to compile the query against.
                          TABLE_NOTIFICACION,
                          //  @param columns A list of which columns to return. Passing null will
                          //  return all columns, which is discouraged to prevent reading
                          //  data from storage that isn't going to be used.
                          new String[]{KEY_NOTIFICACION_ID},
                          // @param selection A filter declaring which rows to return, formatted as an
                          // SQL WHERE clause (excluding the WHERE itself). Passing null
                          // will return all rows for the given table.
                          "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                                "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                          // @param selectionArgs You may include ?s in selection, which will be
                          // replaced by the values from selectionArgs, in order that they
                          // appear in the selection. The values will be bound as Strings.
                          new String[]{Util.RESULTADO_ENTREGADO, "0", Util.RESULTADO_ENTREGADO, "1"},
                          null,
                          null,
                          null,
                          null);
        resumenReparto.setNumEntregados(cursor.getCount());

        // Entregado en oficina
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
               "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) ",
        new String[]{Util.RESULTADO_ENTREGADO_OFICINA, "1", Util.RESULTADO_ENTREGADO_OFICINA, "1"},null,null,null,null);
        resumenReparto.setNumEntregadosEnOficina(cursor.getCount());

        // No Entregado en oficina
        cursor = db.query(true,TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) ",
                new String[]{Util.RESULTADO_NO_ENTREGADO_OFICINA, "1"},null,null,null,null);
        resumenReparto.setNumNoEntregadosEnOficna(cursor.getCount());

        // Dir. Incorrecta
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = ? AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = ?)",
                new String[]{Util.RESULTADO_DIR_INCORRECTA, "0", Util.RESULTADO_DIR_INCORRECTA, "1"}, null, null, null, null);
        resumenReparto.setNumDirIncorrectas(cursor.getCount());

        // Ausente FINAL
        cursor = db.query(// DISTINCT
                          true,
                          // TABLA
                          TABLE_NOTIFICACION,
                          // COLUMNAS
                          new String[]{KEY_NOTIFICACION_ID},
                          // WHERE
                             "(" + KEY_NOTIFICACION_RESULTADO_2 + " = ?" + ")" ,
                          // VALORES DE LOS ? añadidos de forma que aparecen en el ARRAY
                          new String[]{Util.RESULTADO_AUSENTE_SEGUNDO}, null, null, null, null);
        resumenReparto.setNumAusentes(cursor.getCount());

        // Ausente PENDIENTE
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? " +
                      "AND " + KEY_NOTIFICACION_RESULTADO_2 + " ISNULL "  + ")" ,
                new String[]{Util.RESULTADO_AUSENTE}, null, null, null, null);
        resumenReparto.setNumAusentesPendientes(cursor.getCount());

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

        // No se hace cargo FINAL
        cursor = db.query(// DISTINCT
                true,
                // TABLA
                TABLE_NOTIFICACION,
                // COLUMNAS
                new String[]{KEY_NOTIFICACION_ID},
                // WHERE
                "(" + KEY_NOTIFICACION_RESULTADO_2 + " = ?" + ")" ,
                // VALORES DE LOS ? añadidos de forma que aparecen en el ARRAY
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO_SEGUNDO}, null, null, null, null);
        resumenReparto.setNumNadieSeHaceCargo(cursor.getCount());

        // No se hace cargo PENDIENTE
        cursor = db.query(true, TABLE_NOTIFICACION, new String[]{KEY_NOTIFICACION_ID},
                "(" + KEY_NOTIFICACION_RESULTADO_1 + " = ? " +
                      "AND " + KEY_NOTIFICACION_RESULTADO_2 + " ISNULL "  + ")" ,
                new String[]{Util.RESULTADO_NADIE_SE_HACE_CARGO}, null, null, null, null);
        resumenReparto.setNumNadieSeHaceCargoPendientes(cursor.getCount());



        return resumenReparto;
    }

    /******************************************************************************************/
    /*************************** QUERIES NOTIFICACIONES ***************************************/
    /******************************************************************************************/
    /**
     * Realiza el guardado inicial de cuando se estan cargando las notificacion del fichero SICER
     * @param listaNotificaciones
     */
    public void guardarNotificacionesInicial(List<Notificacion> listaNotificaciones) throws CiMobileException {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean hayError = false;
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
            hayError = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        if(hayError) {
            throw new CiMobileException(context.getString(R.string.error_guardar_notificaciones));
        }
    }

    /**
     * Actualiza las notificaciones existentes en la base de datos que han sido cargadas previamente,
     * con los datos del primer intento para poder gestionar el segundo intento
     * @param listaNotificaciones
     */
    public void actualizarNotificacionesSegundoIntentoInicial(List<Notificacion> listaNotificaciones) throws CiMobileException {
        Boolean hayError = false;

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


                db.update(
                        // Tabla
                        TABLE_NOTIFICACION,
                        // Columnas
                        cv,
                        // Where
                        KEY_NOTIFICACION_ID + "= ?",
                        // Valores del Where
                        new String[]{notificacion.getId().toString()});
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            hayError = true;
        } finally {
            db.endTransaction();
        }

        db.close();

        if(hayError) {
            throw new CiMobileException(    context.getString(R.string.error_actualizar_notificaciones));
        }
    }


    /**
     * Obtiene una notificación en concreto a partir de su identificador de la BD
     * @param idNotificacion
     * @return Notificacion
     */
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
                        KEY_NOTIFICACION_SEGUNDO_INTENTO,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_1,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_2
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

    /**
     * Obtiene la notificacion por referencia y si tuviera segunda referencia, tambien por ella, para evitar duplicados
     * @param referencia
     * @param referenciaSCB
     * @return Notificacion
     */
    public Notificacion obtenerNotificacion(String referencia, String referenciaSCB) {
        Notificacion notificacion = null;

        int numParametros = StringUtils.isNotBlank(referenciaSCB) ? 2 : 1;
        String[] parametros = new String[numParametros];
        String whereClause = KEY_NOTIFICACION_REFERENCIA + " = ?";
        parametros[0] = referencia;
        if(StringUtils.isNotBlank(referenciaSCB)) {
            whereClause += " OR " + KEY_NOTIFICACION_REFERENCIA_SCB + " = ?";
            parametros[1] = referenciaSCB;
        }

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
                        KEY_NOTIFICACION_SEGUNDO_INTENTO,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_1,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_2
                },
                whereClause, parametros,
                null, null, null, null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                notificacion = this.mapearCursorANotificacion(cursor);
            }
        }

        return notificacion;
    }

    /**
     * Obtiene las notificaciones de la base de datos dado un criterio de busqueda que se especifica
     * en la clase FiltroNotificacion
     * @param filtroNotificacion
     * @return
     */
    public List<Notificacion> obtenerNotificacionesPorFiltro(FiltroNotificacion filtroNotificacion) {
        List<Notificacion> listaNotificaciones = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT * FROM " + TABLE_NOTIFICACION;
        query += " WHERE 1 = 1 ";

        if (filtroNotificacion.getReferencia() != null && filtroNotificacion.getReferencia().trim().length() > 0) {
            query += "AND (" + KEY_NOTIFICACION_REFERENCIA + " LIKE " + filtroNotificacion.getReferencia() + ") ";
        }
        if (filtroNotificacion.getEntregado()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_ENTREGADO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_ENTREGADO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getDirIncorrecta()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_DIR_INCORRECTA + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_DIR_INCORRECTA + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        // Teniendo en cuenta los cargados en segundoIntento.txt
        if (filtroNotificacion.getAusente()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_AUSENTE + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_AUSENTE + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_AUSENTE + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_AUSENTE + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) ";
        }
        if (filtroNotificacion.getDesconocido()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_DESCONOCIDO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_DESCONOCIDO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getFallecido()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_FALLECIDO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_FALLECIDO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        if (filtroNotificacion.getRehusado()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_REHUSADO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_REHUSADO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) ";
        }
        // Teniendo en cuenta los cargados en segundoIntento.txt
        if (filtroNotificacion.getNadieSeHaceCargo()) {
            query += "AND (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_NADIE_SE_HACE_CARGO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_NADIE_SE_HACE_CARGO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_1 + " = '" + Util.RESULTADO_NADIE_SE_HACE_CARGO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 1) " +
                      "OR (" + KEY_NOTIFICACION_RESULTADO_2 + " = '" + Util.RESULTADO_NADIE_SE_HACE_CARGO + "' AND " + KEY_NOTIFICACION_SEGUNDO_INTENTO + " = 0) ";
        }
        if (filtroNotificacion.getMarcadas()) {
            query += "AND " + KEY_NOTIFICACION_MARCADA + " = " + 1 + " ";
            query += "ORDER BY " + KEY_NOTIFICACION_TIMESTAMP_MARCADA + " ASC ";
        } else {
            query += "ORDER BY " + KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1 + " DESC ";
        }
        // SELECT DISTINCT * FROM notificacion WHERE 1 = 1 ORDER BY referencia ASC LIMIT 10
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

    /**
     * Obtiene todas las notificaciones de la base de datos sin ningún tipo de filtro
     * @return List<Notificacion>
     */
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
                        KEY_NOTIFICACION_TIMESTAMP_MARCADA,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_1,
                        KEY_NOTIFICACION_FOTO_ACUSE_RES_2

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

    /**
     * Actualiza en la base de datos si se ha marcado o no la notificacion como favorita
     * @param notificacion
     */
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

    /**
     * Es un método privado que se encarga de mapear correctamente el valor devuelto por la consulta
     * @param cursor
     * @return Notificacion
     */
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
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FOTO_ACUSE_RES_1);
        if (columna != -1) {
            notificacion.setFotoAcuseRes1(cursor.getString(columna));
        }
        columna = cursor.getColumnIndex(KEY_NOTIFICACION_FOTO_ACUSE_RES_2);
        if (columna != -1) {
            notificacion.setFotoAcuseRes2(cursor.getString(columna));
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
        // Inicialmente pongo el BLANCO
        Integer colorBackground = R.color.colorBackgroundSinGestionar;
        Resultado resultado = null;
        // Si hemos cargado el primer intento y existe resultado del segundo
        if(notificacion.getSegundoIntento()) {
            if (notificacion.getResultado2() != null && notificacion.getResultado2().trim().length() > 0) {
                resultado = this.obtenerResultado(notificacion.getResultado2());
            } else if (notificacion.getResultado1() != null && notificacion.getResultado1().trim().length() > 0) {
                colorBackground = R.color.colorGrisSuave;
            }
            // No hemos cargado el primero del fichero txt lo hemos hecho hoy
            } else {
                if(notificacion.getResultado1() != null && notificacion.getResultado1().trim().length() > 0) {
                    resultado = this.obtenerResultado(notificacion.getResultado1());
                }
            }

        if (resultado != null && colorBackground != R.color.colorGrisSuave) {
            // Entregado
            if (resultado.getNotifica()) {
                colorBackground = R.color.colorBackgroundEntregado;
                // Pendiente de segunda visita
                } else if (!resultado.getEsFinal()) {
                    colorBackground = R.color.colorBackgroundAusente;
                    // Acabado con otro resultado que no es ENTREGADO
                    } else if (resultado.getEsFinal() && !resultado.getNotifica()) {
                        colorBackground = R.color.colorBackgroundNoEntregado;
            }
        }

        notificacion.setBackgroundColor(colorBackground);

        return notificacion;
    }

    /**
     * Guarda el resultado postal de la notificacion disinguiendo que valores guardar dependiendo
     * de si es un primer o un sgundo intento
     * @param notificacion
     * @return Boolean
     */
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

            if(BooleanUtils.isFalse(notificacion.getSegundoIntento())) {
                cv.put(KEY_NOTIFICACION_RESULTADO_1, notificacion.getResultado1());
                cv.put(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1, notificacion.getDescResultado1());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_1, notificacion.getFechaHoraRes1());
                cv.put(KEY_NOTIFICACION_LONGITUD_RES_1, notificacion.getLongitudRes1());
                cv.put(KEY_NOTIFICACION_LATITUD_RES_1, notificacion.getLatitudRes1());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_1, notificacion.getObservacionesRes1());
                cv.put(KEY_NOTIFICACION_NOTIFICADOR_RES_1, notificacion.getNotificadorRes1());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_1, notificacion.getObservacionesRes1());
                cv.put(KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1, notificacion.getFirmaNotificadorRes1());
                cv.put(KEY_NOTIFICACION_FOTO_ACUSE_RES_1, notificacion.getFotoAcuseRes1());
                cv.put(KEY_NOTIFICACION_FOTO_ACUSE_RES_2, notificacion.getFotoAcuseRes2());

            } else {
                cv.put(KEY_NOTIFICACION_RESULTADO_2, notificacion.getResultado2());
                cv.put(KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2, notificacion.getDescResultado2());
                cv.put(KEY_NOTIFICACION_FECHA_HORA_RES_2, notificacion.getFechaHoraRes2());
                cv.put(KEY_NOTIFICACION_LONGITUD_RES_2, notificacion.getLongitudRes2());
                cv.put(KEY_NOTIFICACION_LATITUD_RES_2, notificacion.getLatitudRes2());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_2, notificacion.getObservacionesRes2());
                cv.put(KEY_NOTIFICACION_NOTIFICADOR_RES_2, notificacion.getNotificadorRes2());
                cv.put(KEY_NOTIFICACION_OBSERVACIONES_RES_2, notificacion.getObservacionesRes2());
                cv.put(KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2, notificacion.getFirmaNotificadorRes2());
                cv.put(KEY_NOTIFICACION_FOTO_ACUSE_RES_1, notificacion.getFotoAcuseRes1());
                cv.put(KEY_NOTIFICACION_FOTO_ACUSE_RES_2, notificacion.getFotoAcuseRes2());

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

    /**
     * Elimina un resultado de notificadion a partir de su id y si es primer o segundo resultado
     * @param idNotificacion
     * @param resultado
     * @return Boolean
     */
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

    /**
     * Borra todas las notificaciones de la base de datos
     * @return Boolean
     */
    public Boolean borrarNotificaciones() {
        Boolean eliminado = Boolean.FALSE;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_NOTIFICACION, null, null);
            db.setTransactionSuccessful();
            eliminado = Boolean.TRUE;
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
                + KEY_NOTIFICACION_REFERENCIA + " TEXT, "
                + KEY_NOTIFICACION_REFERENCIA_SCB + " TEXT, "
                + KEY_NOTIFICACION_NOMBRE + " TEXT, "
                + KEY_NOTIFICACION_DIRECCION + " TEXT, "
                + KEY_NOTIFICACION_POBLACION + " TEXT, "
                + KEY_NOTIFICACION_CODIGO_POSTAL + " TEXT, "
                + KEY_NOTIFICACION_RESULTADO_1 + " TEXT, "
                + KEY_NOTIFICACION_DESCRIPCION_RESULTADO_1 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_NOTIFICADOR_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_OBSERVACIONES_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_RESULTADO_2 + " TEXT, "
                + KEY_NOTIFICACION_DESCRIPCION_RESULTADO_2 + " TEXT, "
                + KEY_NOTIFICACION_FECHA_HORA_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_NOTIFICADOR_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_NOTIFICADOR_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_TIPO_DOC_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_NUM_DOC_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_NOMBRE_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_FIRMA_RECEPTOR + " TEXT, "
                + KEY_NOTIFICACION_LONGITUD_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_LATITUD_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_LONGITUD_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_LATITUD_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_OBSERVACIONES_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_MARCADA + " INTEGER, "
                + KEY_NOTIFICACION_TIMESTAMP_MARCADA + " TEXT,"
                + KEY_NOTIFICACION_SEGUNDO_INTENTO + " INTEGER, "
                + KEY_NOTIFICACION_FOTO_ACUSE_RES_1 + " TEXT, "
                + KEY_NOTIFICACION_FOTO_ACUSE_RES_2 + " TEXT, "
                + KEY_NOTIFICACION_NOMBRE_FICHERO + " TEXT); ";

        sqLiteDatabase.execSQL(qry);
    }

    private void crearTablaResultados(SQLiteDatabase sqLiteDatabase) {
        String qry = "CREATE TABLE " + TABLE_RESULTADO + "("
                + KEY_RESULTADO_CODIGO + " TEXT, "
                + KEY_RESULTADO_DESCRIPCION + " TEXT, "
                + KEY_RESULTADO_FINAL + " INTEGER, "
                + KEY_RESULTADO_RESULTADO_OFICINA +" INTEGER, "
                + KEY_RESULTADO_CODIGO_SEGUNDO_INTENTO + " TEXT, "
                + KEY_RESULTADO_NOTIFICA + " INTEGER);";

        sqLiteDatabase.execSQL(qry);
    }


}
