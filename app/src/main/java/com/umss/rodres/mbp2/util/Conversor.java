package com.umss.rodres.mbp2.util;

import java.text.DecimalFormat;

public class Conversor {

    public static String truncar(double valor) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(valor);
    }

    public static double toGB(long val) {
        return val / (double)(1024 * 1024 * 1024);
    }

    public static double toMB(long val) {
        return val /(double)(1024 * 1024);
    }

    public static String toStringSize(long size){
        if(toMB(size) > 1024.0)
            return truncar(toGB(size)) + " GB";
        else
            return truncar(toMB(size)) + " MB";
    }
}