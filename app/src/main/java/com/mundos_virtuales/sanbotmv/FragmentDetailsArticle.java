package com.mundos_virtuales.sanbotmv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mundos_virtuales.sanbotmv.models.ModelArticles;


/**
 * Fragmento que representa el panel del detalle de un artículo.
 */
public class FragmentDetailsArticle extends Fragment {

    // EXTRA
    public static final String ID_ARTICULO = "extra.idArticulo";

    // Articulo al cuál está ligado la UI
    private ModelArticles.Article itemDetallado;

    public FragmentDetailsArticle() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ID_ARTICULO)) {
            // Cargar modelo según el identificador
            itemDetallado = ModelArticles.MAP_ITEMS.get(getArguments().getString(ID_ARTICULO));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details_article, container, false);

        if (itemDetallado != null) {
            // Toolbar en master-detail
            Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar_detalle);
            if (toolbar != null)
                toolbar.inflateMenu(R.menu.menu_detalle_articulo);

            ((TextView) v.findViewById(R.id.titulo)).setText(itemDetallado.titulo);
            ((TextView) v.findViewById(R.id.fecha)).setText(itemDetallado.fecha);
            ((TextView) v.findViewById(R.id.contenido)).setText(getText(R.string.lorem));

            if(itemDetallado.isRemoteData){
                Glide.with(this)
                        .load(itemDetallado.urlMiniatura)
                        .into((ImageView) v.findViewById(R.id.imagen));
            }
            else {
                Glide.with(this)
                        .load("")
                        .placeholder(getResources().getIdentifier(
                                itemDetallado.urlImage, null,
                                "com.mundos_virtuales.sanbotmv"))
                        .into((ImageView) v.findViewById(R.id.imagen));
            }
        }

        return v;
    }
}
