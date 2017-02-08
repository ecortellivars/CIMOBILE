package es.correointeligente.cipostal.cimobile.Holders;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.correointeligente.cipostal.cimobile.Activities.ListaNotificacionesActivity;
import es.correointeligente.cipostal.cimobile.Activities.NuevaNotificacionActivity;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.R;


public class NotificacionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView referencia, nombre, direccion, resultado;
    CheckBox marcada;
    LinearLayout ll_contentItems;
    CardView cardView;
    Notificacion notificacion;
    Context context;
    ListaNotificacionesActivity listaNotificacionesActivity;
    View itemView;
    public NotificacionViewHolder(View itemView) {
        super(itemView);
    }

    public NotificacionViewHolder(View itemView, Context context, ListaNotificacionesActivity listaNotificacionesActivity) {
        super(itemView);
        this.itemView = itemView;
        this.context = context;
        referencia = (TextView) itemView.findViewById(R.id.textView_cardView_referencia);
        nombre = (TextView) itemView.findViewById(R.id.textView_cardView_nombre);
        direccion = (TextView) itemView.findViewById(R.id.textView_cardView_direccion);
        resultado = (TextView) itemView.findViewById(R.id.textView_cardView_resutado);
        marcada = (CheckBox) itemView.findViewById(R.id.checkBox_cardView_marcada);
        ll_contentItems = (LinearLayout) itemView.findViewById(R.id.linearLayout_cardView_layout);
        cardView = (CardView) itemView.findViewById(R.id.cardView_notificacion);
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
            resultado.setText(notificacion.getResultado2() + " " + notificacion.getDescResultado2());
        } else {
            resultado.setText(notificacion.getResultado1() + " " + notificacion.getDescResultado1());
        }
        marcada.setChecked(notificacion.getMarcada());

        int visible = notificacion.getResultado1() != null ? View.VISIBLE : View.INVISIBLE;
        resultado.setVisibility(visible);
        ll_contentItems.setBackgroundColor(ContextCompat.getColor(context, notificacion.getBackgroundColor()));
//      this.cardView.setCardBackgroundColor(ContextCompat.getColor(context, notificacion.getBackgroundColor()));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.checkBox_cardView_marcada) {
            notificacion.setMarcada(((CheckBox) view).isChecked());
            listaNotificacionesActivity.actualizarNotificacionMarcada(notificacion);
        } else {
            Intent intent = new Intent(context, NuevaNotificacionActivity.class);
            intent.putExtra("idNotificacion", notificacion.getId());
            context.startActivity(intent);
        }
    }

    public CardView getCardView() {
        return this.cardView;
    }
}
