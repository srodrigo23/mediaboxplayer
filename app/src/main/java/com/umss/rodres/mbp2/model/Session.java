package com.umss.rodres.mbp2.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.core.android.Auth;
import com.umss.rodres.mbp2.R;

import static android.content.Context.MODE_PRIVATE;

public class Session  {

    private static SharedPreferences sp;

    public static void  initSession(Context context){
        if (sp == null) {
            sp = context.getSharedPreferences(context.getString(R.string.info_dropbox), MODE_PRIVATE);
        }
    }

    public static void saveToken() {
        String accessToken = sp.getString("acces-token", null);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) sp.edit().putString("access-token", accessToken).apply();
        }
        String uid = Auth.getUid();
        String storedUid = sp.getString("user-id", null);
        if (uid != null && !uid.equals(storedUid)) sp.edit().putString("user-id", uid).apply();
    }

    public static void setUserName(String userName){
        sp.edit().putString("userName", userName).apply();
    }

    public static void setEmail(String email){
        sp.edit().putString("email", email).apply();
    }

    public static void setTypeAccount(String typeAccount){
        sp.edit().putString("type", typeAccount).apply();
    }

    public static void setUsedSpace(long usedSpace){
        sp.edit().putLong("usedEsp", usedSpace).apply();
    }

    public static void setTotalSpace(long totalSpace){ sp.edit().putLong("totalEsp", totalSpace).apply(); }

    /*
    public static void initDropboxClient() {
        DropBoxClient.init(getToken());
    }
    */

    public static boolean isSessionStarted() {
        return sp.getString("access-token", null) != null;
    }

    public static String getUserName() { return sp.getString("userName", null); }

    public static String getEmail() {
        return sp.getString("email", null);
    }

    public static String getToken(){
        return sp.getString("access-token", null);
    }

    public static long getUsedSpace() {
        return sp.getLong("usedEsp", 0);
    }

    public static long getTotalSpace() {
        return sp.getLong("totalEsp", 0);
    }

    public static String getTypeAccount(){
        return sp.getString("type", null);
    }

    public static void clearInfoAccount() {
        sp.edit().clear().commit();
    }
}