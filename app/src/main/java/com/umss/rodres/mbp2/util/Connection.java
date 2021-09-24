package com.umss.rodres.mbp2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {

    static ConnectivityManager cm;
    static NetworkInfo activeNetwork;

    public static boolean isConnected (Context c) {
        cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}