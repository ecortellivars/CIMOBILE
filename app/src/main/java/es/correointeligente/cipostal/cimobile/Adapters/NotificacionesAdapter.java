package es.correointeligente.cipostal.cimobile.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.correointeligente.cipostal.cimobile.Activities.ListaNotificacionesActivity;
import es.correointeligente.cipostal.cimobile.Holders.NotificacionViewHolder;
import es.correointeligente.cipostal.cimobile.Holders.ProgressViewHolder;
import es.correointeligente.cipostal.cimobile.Model.Notificacion;
import es.correointeligente.cipostal.cimobile.Model.NotificacionProgressbar;
import es.correointeligente.cipostal.cimobile.R;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionViewHolder> {

    private static final int TYPE_NOTIFICACION = 0;
    private static final int TYPE_FOOTER = 1;
    private List<Notificacion> items;
    private Context mainContext;
    private ListaNotificacionesActivity listaNotificacionesActivity;

    public NotificacionesAdapter(Context context, List<Notificacion> items) {
        this.mainContext = context;
        this.listaNotificacionesActivity = (ListaNotificacionesActivity) mainContext;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        Integer type = TYPE_NOTIFICACION;

        if (items.get(position) instanceof NotificacionProgressbar) {
            type = TYPE_FOOTER;
        }

        return type;
    }

    @Override
    public NotificacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotificacionViewHolder viewHolder = null;

        if (viewType == TYPE_NOTIFICACION) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_notificacion, parent, false);
            viewHolder = new NotificacionViewHolder(itemView, mainContext, listaNotificacionesActivity);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(itemView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificacionViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == TYPE_NOTIFICACION) {
            Notificacion notificacion = items.get(position);
            viewHolder.bindData(notificacion);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
