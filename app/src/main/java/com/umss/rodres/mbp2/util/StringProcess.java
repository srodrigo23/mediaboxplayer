package com.umss.rodres.mbp2.util;

public class StringProcess {

    public static String procesPath(String path){
        String toSplit[] = path.split("/");
        String ans="";
        for(int i=1; i < toSplit.length - 1; i++){
            ans = ans + "/" +toSplit[i];
        }
        return ans;
    }
}
