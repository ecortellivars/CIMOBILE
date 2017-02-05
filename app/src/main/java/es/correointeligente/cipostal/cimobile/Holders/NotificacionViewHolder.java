package es.correointeligente.cipostal.cimobile.Holders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import es.correointeligente.cipostal.cimobile.Activities.ListaNotificacionesActivity;
import es.correointeligente.cipostal.cimobile.Activities.NuevaNotificacionActivity;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;


public class NotificacionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView referencia, nombre, direccion, resultado;
    CheckBox marcada;
    Notificacion notificacion;
    Context context;
    ListaNotificacionesActivity listaNotificacionesActivity;

    public NotificacionViewHolder(View itemView) {
        super(itemView);
    }

    public NotificacionViewHolder(View itemView, Context context, ListaNotificacionesActivity listaNotificacionesActivity) {
        super(itemView);

        this.context = context;

        referencia = (TextView) itemView.findViewById(R.id.textView_cardView_referencia);
        nombre = (TextView) itemView.findViewById(R.id.textView_cardView_nombre);
        direccion = (TextView) itemView.findViewById(R.id.textView_cardView_direccion);
        resultado = (TextView) itemView.findViewById(R.id.textView_cardView_resutado);
        marcada = (CheckBox) itemView.findViewById(R.id.checkBox_cardView_marcada);
        itemView.setOnClickListener(this);
        marcada.setOnClickListener(this);
        this.listaNotificacionesActivity = listaNotificacionesActivity;
    }


    public void bindData(Notificacion notificacion) {
        this.notificacion = notificacion;

        referencia.setText(notificacion.getReferencia());
        nombre.setText(notificacion.getNombre());
        direccion.setText(notificacion.getDireccion());
        if (notificacion.getSegundoIntento() != null && notificacion.getSegundoIntento()) {
            resultado.setText(notificacion.getResultado2());
        } else {
            resultado.setText(notificacion.getResultado1());
        }

        marcada.setChecked(notificacion.getMarcada());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.checkBox_cardView_marcada) {
            notificacion.setMarcada(((CheckBox) view).isChecked());
            listaNotificacionesActivity.actualizarNotificacionMarcada(notificacion);
        } else {

            Intent intent = new Intent(context, NuevaNotificacionActivity.class);
            intent.putExtra("refPostal", notificacion.getReferencia());
            context.startActivity(intent);

        }
    }
}
