package com.umss.rodres.mbp2;

import android.app.Application;

import com.umss.rodres.mbp2.util.NetworkChangeReceiver;

public class MyApplication extends Application{

    public static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(NetworkChangeReceiver.ConnectivityReceiverListener listener) {
        NetworkChangeReceiver.connectivityReceiverListener = listener;
    }

}
