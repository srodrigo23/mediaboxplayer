package com.umss.rodres.mbp2.session;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

public class LoadLogOut {

    public static void load(final Context context){

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Cerrando Sesion en el Servidor...");
        dialog.show();

        LogOut.CallBack log = new LogOut.CallBack() {
            @Override
            public void onComplete(Boolean vb) {

            }

            @Override
            public void onError(Exception e) {

            }
        };

        new LogOut(Client.getAuthRequests(), new LogOut.CallBack() {
            @Override
            public void onComplete(Boolean flag) {
                dialog.dismiss();
                if(flag){
                    Toast.makeText(context, "Desconectado del Servidor", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(context, "Error de conexion!!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast.makeText(context, "Un error ha ocurrido!!!!", Toast.LENGTH_LONG).show();
            }
        }).execute();
    }
}
