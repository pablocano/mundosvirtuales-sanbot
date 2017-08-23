package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.mundos_virtuales.sanbotmv.models.ModelArticles;

/**
 * Actividad con la lista de artículos. Si el ancho del dispositivo es mayor o igual a 900dp, entonces
 * se incrusta el fragmento de detalle {@link FragmentDetailsArticle} para generar el patrón
 * Master-detail
 */
public class ActivityListArticles extends AppCompatActivity
        implements FragmentListArticles.EscuchaFragmento {

    // ¿Hay dos paneles?
    private boolean dosPaneles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_articles);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((Toolbar) findViewById(R.id.toolbar)).setTitle(getTitle());

        // Verificación: ¿Existe el detalle en el layout?
        if (findViewById(R.id.contenedor_detalle_articulo) != null) {
            // Si es asi, entonces confirmar modo Master-Detail
            dosPaneles = true;

            cargarFragmentoDetalle(ModelArticles.ITEMS.get(0).id);
        }

        // Agregar fragmento de lista
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_lista, FragmentListArticles.crear())
                .commit();

    }

    private void cargarFragmentoDetalle(String id) {
        Bundle arguments = new Bundle();
        arguments.putString(FragmentDetailsArticle.ID_ARTICULO, id);
        FragmentDetailsArticle fragment = new FragmentDetailsArticle();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_detalle_articulo, fragment)
                .commit();
    }


    @Override
    public void alSeleccionarItem(String idArticulo) {
        if (dosPaneles) {
            cargarFragmentoDetalle(idArticulo);
        } else {
            Intent intent = new Intent(this, ActivityDetailsArticle.class);
            intent.putExtra(FragmentDetailsArticle.ID_ARTICULO, idArticulo);

            startActivity(intent);
        }
    }
}
