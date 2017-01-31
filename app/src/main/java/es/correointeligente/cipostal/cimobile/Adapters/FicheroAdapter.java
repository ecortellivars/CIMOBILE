package es.correointeligente.cipostal.cimobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.correointeligente.cipostal.cimobile.Holders.FicheroViewHolder;
import es.correointeligente.cipostal.cimobile.R;


public class FicheroAdapter extends ArrayAdapter<FicheroViewHolder> {

    private Context c;
    private int id;
    private List<FicheroViewHolder> items;

    public FicheroAdapter(Context context, int textViewResourceId, List<FicheroViewHolder> objects) {
        super(context, textViewResourceId, objects);
        items = objects;
        c = context;
        id = textViewResourceId;
    }

    public FicheroViewHolder getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(id, null);
        }
        FicheroViewHolder o = items.get(position);
        if (o != null) {
            TextView t1 = (TextView) convertView.findViewById(R.id.textView_item_fichero_nombre);
            t1.setText(o.getNombreFichero());
            TextView t2 = (TextView) convertView.findViewById(R.id.textView_item_fichero_fecha);
            t2.setText(o.getFecha());
            TextView t3 = (TextView) convertView.findViewById(R.id.textView_item_fichero_tamanyo);
            t3.setText(o.getTamanyo());

        }

        return convertView;
    }
}
