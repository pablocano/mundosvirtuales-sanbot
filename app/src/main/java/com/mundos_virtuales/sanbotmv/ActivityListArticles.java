package com.mundos_virtuales.sanbotmv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.mundos_virtuales.sanbotmv.models.ModelArticles;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SystemManager;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class ActivityListArticles extends TopBaseActivity
        implements FragmentListArticles.EscuchaFragmento {

    private boolean mTwoPanels;

    @OnClick(R.id.btnFragDetailArticleCancel)
    public void onCancel() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, MainActivity.class.getName());
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_articles);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ((SystemManager) getUnitManager(FuncConstant.SYSTEM_MANAGER)).switchFloatBar(false, ActivityListArticles.class.getName());

        ButterKnife.bind(this);

        ((Toolbar) findViewById(R.id.toolbar)).setTitle(getTitle());

        if (findViewById(R.id.contenedor_detalle_articulo) != null) {
            mTwoPanels = true;
            cargarFragmentoDetalle(ModelArticles.ITEMS.get(0).id);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_lista, FragmentListArticles.crear())
                .commit();

    }

    @Override
    protected void onMainServiceConnected() {

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
        if (mTwoPanels) {
            cargarFragmentoDetalle(idArticulo);
        } else {
            Intent intent = new Intent(this, ActivityDetailsArticle.class);
            intent.putExtra(FragmentDetailsArticle.ID_ARTICULO, idArticulo);

            startActivity(intent);
        }
    }
}
