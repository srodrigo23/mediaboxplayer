package com.umss.rodres.mbp2.conection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Coneccion_Internet {

    static ConnectivityManager cm;
    static NetworkInfo activeNetwork;

    public static boolean hayConeccion(Context c) {

        cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
