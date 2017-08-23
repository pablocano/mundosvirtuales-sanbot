package com.mundos_virtuales.sanbotmv;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.mundos_virtuales.sanbotmv.models.ModelArticles;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} que alimenta la lista con
 * instancias {@link ModelArticles.Article}
 */
public class AdapterArticles extends RecyclerView.Adapter<AdapterArticles.ViewHolder> {

    private final List<ModelArticles.Article> valores;

    public AdapterArticles(List<ModelArticles.Article> items,
                           OnItemClickListener escuchaClicksExterna) {
        valores = items;
        this.escuchaClicksExterna = escuchaClicksExterna;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_articles, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = valores.get(position);
        holder.viewTitulo.setText(valores.get(position).titulo);
        holder.viewResumen.setText(valores.get(position).descripcion);
        holder.viewFecha.setText(valores.get(position).fecha);
        if(valores.get(position).isRemoteData){
            Glide.with(holder.itemView.getContext())
                    .load(holder.item.urlMiniatura)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(holder.viewMiniatura);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load("")
                    .placeholder(holder.itemView.getResources().getIdentifier(
                            holder.item.urlMiniatura, null,
                            "com.mundos_virtuales.sanbotmv"))
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(holder.viewMiniatura);
        }

    }

    @Override
    public int getItemCount() {
        if (valores != null) {
            return valores.size() > 0 ? valores.size() : 0;
        } else {
            return 0;
        }
    }


    private String obtenerIdArticulo(int posicion) {
        if (posicion != RecyclerView.NO_POSITION) {
            return valores.get(posicion).id;
        } else {
            return null;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView viewTitulo;
        public final TextView viewResumen;
        public final TextView viewFecha;
        public final ImageView viewMiniatura;

        public ModelArticles.Article item;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            viewTitulo = (TextView) view.findViewById(R.id.titulo);
            viewResumen = (TextView) view.findViewById(R.id.resumen);
            viewFecha = (TextView) view.findViewById(R.id.fecha);
            viewMiniatura = (ImageView) view.findViewById(R.id.miniatura);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            escuchaClicksExterna.onClick(this, obtenerIdArticulo(getAdapterPosition()));
        }
    }


    public interface OnItemClickListener {
        public void onClick(ViewHolder viewHolder, String idArticulo);
    }

    private OnItemClickListener escuchaClicksExterna;
}
