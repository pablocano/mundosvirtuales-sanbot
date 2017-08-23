package com.mundos_virtuales.sanbotmv;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mundos_virtuales.sanbotmv.models.ModelArticles;


/**
 * Fragmento especializado para la lista de artículos
 */

public class FragmentListArticles extends Fragment
        implements AdapterArticles.OnItemClickListener {

    private EscuchaFragmento escucha;

    public FragmentListArticles() {

    }

    public static FragmentListArticles crear() {
        return new FragmentListArticles();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Manejo de argumentos
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_articles, container, false);

        View recyclerView = v.findViewById(R.id.reciclador);

        assert recyclerView != null;
        prepararLista((RecyclerView) recyclerView);

        return v;
    }


    private void prepararLista(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new AdapterArticles(ModelArticles.ITEMS, this));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EscuchaFragmento) {
            escucha = (EscuchaFragmento) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debes implementar EscuchaFragmento");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        escucha = null;
    }

    public void cargarDetalle(String idArticulo) {
        if (escucha != null) {
            escucha.alSeleccionarItem(idArticulo);
        }
    }

    @Override
    public void onClick(AdapterArticles.ViewHolder viewHolder, String idArticulo) {
        cargarDetalle(idArticulo);
    }

    public interface EscuchaFragmento {
        void alSeleccionarItem(String idArticulo);
    }
}
