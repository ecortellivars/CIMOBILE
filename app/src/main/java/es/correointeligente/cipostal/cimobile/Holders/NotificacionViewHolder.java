package es.correointeligente.cipostal.cimobile.Holders;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Calendar;

import es.correointeligente.cipostal.cimobile.Activities.DetalleNotificacionActivity;
import es.correointeligente.cipostal.cimobile.Activities.ListaNotificacionesActivity;
import es.correointeligente.cipostal.cimobile.Activities.NuevaNotificacionActivity;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;
import es.correointeligente.cipostal.cimobile.Util.Util;


public class NotificacionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView referencia, nombre, direccion, resultado1, resultado2;
    CheckBox marcada;
    LinearLayout ll_contentItems;
    TableLayout tbl_resultados;
    CardView cardView;
    Notificacion notificacion;
    Context context;
    ListaNotificacionesActivity listaNotificacionesActivity;

    public NotificacionViewHolder(View itemView) {
        super(itemView);
    }

    public NotificacionViewHolder(View itemView, Context context, ListaNotificacionesActivity listaNotificacionesActivity) {
        super(itemView);


        this.context = context;
        this.listaNotificacionesActivity = listaNotificacionesActivity;

        referencia = (TextView) itemView.findViewById(R.id.textView_cardView_referencia);
        nombre = (TextView) itemView.findViewById(R.id.textView_cardView_nombre);
        direccion = (TextView) itemView.findViewById(R.id.textView_cardView_direccion);
        resultado1 = (TextView) itemView.findViewById(R.id.textView_cardView_resutado_1);
        resultado2 = (TextView) itemView.findViewById(R.id.textView_cardView_resutado_2);
        marcada = (CheckBox) itemView.findViewById(R.id.checkBox_cardView_marcada);
        ll_contentItems = (LinearLayout) itemView.findViewById(R.id.linearLayout_cardView_layout);
        cardView = (CardView) itemView.findViewById(R.id.cardView_notificacion);
        tbl_resultados = (TableLayout) itemView.findViewById(R.id.tableLayout_cardView_resultados);

        itemView.setOnClickListener(this);
        marcada.setOnClickListener(this);
    }


    public void bindData(Notificacion notificacion) {

        this.notificacion = notificacion;

        String textoResultado1 = notificacion.getResultado1() != null && notificacion.getResultado1().trim().length() > 0 ? notificacion.getResultado1() + " " + notificacion.getDescResultado1() : "";
        String textoResultado2 = notificacion.getResultado2() != null && notificacion.getResultado2().trim().length() > 0 ? notificacion.getResultado2() + " " + notificacion.getDescResultado2() : "";


        referencia.setText(notificacion.getReferencia());
        nombre.setText(notificacion.getNombre());
        direccion.setText(notificacion.getDireccion());
        resultado1.setText(textoResultado1.toUpperCase());
        resultado2.setText(textoResultado2.toUpperCase());
        marcada.setChecked(notificacion.getMarcada());
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, notificacion.getBackgroundColor()));
        cardView.setUseCompatPadding(true);
        cardView.setCardElevation(10);
        cardView.setRadius(10);
    }

    @Override
    public void onClick(View view) {

        Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        v.vibrate(20);

        if (view.getId() == R.id.checkBox_cardView_marcada) {
            notificacion.setMarcada(((CheckBox) view).isChecked());
            notificacion.setTimestampMarcada(null);
            if(notificacion.getMarcada()) {
                Long timestamp = Calendar.getInstance().getTimeInMillis();
                notificacion.setTimestampMarcada(timestamp.toString());
            }

            // Se llama a la actividad principal para persistir los datos
            listaNotificacionesActivity.actualizarNotificacionMarcada(notificacion);

        } else {
            // Dependiendo si es una notificacion a gestionar o una notificacion ya gestionada se llama a una pantalla u otra
            Intent intent = null;
            Integer request = null;
            if((notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento() && notificacion.getResultado2() == null) ||
                    ((notificacion.getSegundoIntento() == null || !notificacion.getSegundoIntento())) && notificacion.getResultado1() == null) {
                intent = new Intent(context, NuevaNotificacionActivity.class);
                request = Util.REQUEST_CODE_NOTIFICATION_RESULT;
            } else {
                intent = new Intent(context, DetalleNotificacionActivity.class);
                request = Util.REQUEST_CODE_NOTIFICATION_DELETE_RESULT;
            }

            intent.putExtra("idNotificacion", notificacion.getId());
            intent.putExtra("posicionAdapter", getAdapterPosition());
            listaNotificacionesActivity.startActivityForResult(intent, request);
        }
    }


}
