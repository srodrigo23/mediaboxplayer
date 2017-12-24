package com.umss.rodres.mbp2.media;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import com.umss.rodres.mbp2.R;

public class HeaderSession {

    public static void show(View v, SharedPreferences p){

        TextView txt = v.findViewById(R.id.idNombreUsuario);
        txt.setText(p.getString("name", "Ningun nombre"));
        txt = v.findViewById(R.id.idEMailUsuario);
        txt.setText(p.getString("email", "Ningun email"));
        int max, prog;
        max  = (int) (p.getLong("espTotal", 0)/(1024*1024));
        prog = (int) (p.getLong("espUsado", 0)/(1024*1024));
        double totalEspacio = Conversor.convertirGB(p.getLong("espTotal", 0));
        double espacioUtilizado = Conversor.convertirMB(p.getLong("espUsado", 0));
        String etiqTotalEsp = totalEspacio + " GB";
        String etiqEspUtiliz;
        if(espacioUtilizado > 1024.0){
            espacioUtilizado = Conversor.convertirGB(p.getLong("espUsado", 0));
            etiqEspUtiliz = espacioUtilizado + " GB";
        }else{
            etiqEspUtiliz = espacioUtilizado + " MB";
        }
        ProgressBar progressBar = v.findViewById(R.id.idProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(prog, true);
            progressBar.setBackgroundColor(Color.BLACK);
        }else{
            progressBar.setProgress(prog);
        }
        ((TextView)v.findViewById(R.id.idCapacidad)).setText(etiqEspUtiliz + " de " + etiqTotalEsp);
    }

    static class Conversor{
        public static double round(double valor, int lugares) {
            if (lugares < 0) throw new IllegalArgumentException();
            long factor = (long) Math.pow(10, lugares);
            valor = valor * factor;
            long tmp = Math.round(valor);
            return (double) tmp / factor;
        }
        private static double convertirGB(long val) {
            return round((val / (1024.0*1024.0*1024.0)), 2);
        }
        private static double convertirMB(long val) {
            return round((val / (1024*1024)), 2);
        }
    }
}
