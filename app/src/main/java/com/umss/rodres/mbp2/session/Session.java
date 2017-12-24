package com.umss.rodres.mbp2.session;

import android.content.SharedPreferences;
import com.dropbox.core.android.Auth;

/**
 * @author Sergio Rodrigo
 * @date 20.10.2017
 * @task 1.1
 *
 * Clase para los datos de la session
 */

public class Session {

    private SharedPreferences pref;

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     * Metodo constructor que
     */
    public Session(SharedPreferences p){
        pref = p;
    }

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     *
     */
    public void conectar() {
        String accessToken = pref.getString("access-token", null);
        if (accessToken == null){
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                pref.edit().putString("access-token", accessToken).apply();
            }
        }
        String uid = Auth.getUid();
        String storedUid = pref.getString("user-id", null);
        if (uid != null && !uid.equals(storedUid)) {
            pref.edit().putString("user-id", uid).apply();
        }
    }

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     * Metodo que verifica si el token de acceso ya se obtuvo y esta almacenado en memoria
     * gracias al objeto SharedPreferences.
     */
    public boolean poseeToken(){
        return pref.getString("access-token", null) != null;
    }

    public void clearToken(){
        pref.edit().clear().commit();
    }

    public String getToken(){
        return pref.getString("access-token", null);
    }
}