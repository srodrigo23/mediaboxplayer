package com.umss.rodres.mbp2.session;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.widget.Toast;
import android.view.View;

import com.umss.rodres.mbp2.media.HeaderSession;

public class LoadInfo {

    public static void load(final Context context, final SharedPreferences p, final View v){

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Cargando...");
        dialog.show();

        new GetInfo(Client.getDbxCliente(), p, new GetInfo.CallBack() {
            @Override
            public void onComplete(Boolean flag) {
                dialog.dismiss();
                if(flag){
                    HeaderSession.show(v, p);
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