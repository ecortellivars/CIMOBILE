package es.correointeligente.cipostal.cimobile.Holders;

import android.view.View;
import android.widget.ProgressBar;
import es.correointeligente.cipostal.cimobile.R;


public class ProgressViewHolder extends NotificacionViewHolder {
    public ProgressBar progressBar;

    public ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
    }
}
