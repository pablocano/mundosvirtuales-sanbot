package com.mundos_virtuales.sanbotmv.models;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModelArticles {

    public static final List<Article> ITEMS = new ArrayList<Article>();

    public static final Map<String, Article> MAP_ITEMS = new HashMap<String, Article>();

    static {
        // Añadir elementos de ejemplo
        agregarItem(new Article(generarId(),
                "Oficina Atención al Cliente",
                "Atención personalizada para nuestros clientes ...",
                "23 de Agosto", "@drawable/office_mini_2", "@drawable/office_2", false));
        agregarItem(new Article(generarId(),
                "Recepción",
                "Bienvenida y entrega de orientación general acerca de...",
                "23 de Agosto", "@drawable/office_mini_3", "@drawable/office_3", false));
        agregarItem(new Article(generarId(),
                "Atención al Cliente",
                "Atención general a todos nuestros clientes...",
                "23 de Agosto", "@drawable/office_mini_4", "@drawable/office_4", false));
        agregarItem(new Article(generarId(),
                "Sala de Reuniones",
                "Sala para presentación de productos para todos nuestros clientes ...",
                "23 de Agosto", "@drawable/office_mini_5", "@drawable/office_5", false));
    }

    @NonNull
    private static String generarId() {
        return UUID.randomUUID().toString();
    }


    private static void agregarItem(Article item) {
        ITEMS.add(item);
        MAP_ITEMS.put(item.id, item);
    }


    public static class Article {

        public final String id;

        public final String titulo;

        public final String descripcion;

        public final String fecha;

        public final String urlMiniatura;

        public final String urlImage;

        public final boolean isRemoteData;

        public Article(String id, String titulo, String descripcion,
                       String fecha, String urlMiniatura, String urlImage, boolean isRemoteData) {
            this.id = id;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.fecha = fecha;
            this.urlMiniatura = urlMiniatura;
            this.urlImage = urlImage;
            this.isRemoteData = isRemoteData;
        }
    }
}