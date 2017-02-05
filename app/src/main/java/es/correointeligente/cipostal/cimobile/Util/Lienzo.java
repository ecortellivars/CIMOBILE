package es.correointeligente.cipostal.cimobile.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Lienzo extends View {
    static float TamanyoPunto;
    //Paint de dibujar y Paint de Canvas
    private static Paint drawPaint;
    //Color Inicial
    private static int paintColor = 0xFF000000;
    private static boolean borrado = false;
    //Path que utilizaré para ir pintando las lineas
    private Path drawPath;
    private Paint canvasPaint;
    //canvas
    private Canvas drawCanvas;
    //canvas para guardar
    private Bitmap canvasBitmap;


    public Lienzo(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    //Poner tamaño del punto
    public static void setTamanyoPunto(float nuevoTamanyo) {


        //float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        //        nuevoTamanyo, getResources().getDisplayMetrics());

        //TamanyoPunto=pixel;
        drawPaint.setStrokeWidth(nuevoTamanyo);
    }

    //set borrado true or false
    public static void setBorrado(boolean estaborrado) {
        borrado = estaborrado;
        if (borrado) {

            drawPaint.setColor(Color.WHITE);
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        } else {
            drawPaint.setColor(paintColor);
            //drawPaint.setXfermode(null);
        }
    }

    private void setupDrawing() {
        //Configuración del area sobre la que pintar

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);

        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);


    }

    //Tamaño asignado a la vista
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //Pinta la vista. Será llamado desde el OnTouchEvent
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    //Registra los touch de usuario
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //repintar
        invalidate();
        return true;

    }

    //Actualiza color
    public void setColor(String newColor) {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void NuevoDibujo() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();

    }
}
