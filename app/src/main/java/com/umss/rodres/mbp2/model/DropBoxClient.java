package com.umss.rodres.mbp2.model;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.auth.DbxUserAuthRequests;

/**
 * @author Sergio Rodrigo
 * @date 20.10.2017
 * @task 1.1
 *
 * Objeto que permite instanciar un Cliente DropBox
 */
public class DropBoxClient{

    private static DbxClientV2 GDbxClient;
    private static String NOT_INITIALIZE_MESSAGE = "Cliente No Inicializado";

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     * Esta metodo inicializa el objeto GDbxClient con peticion de configuracion al servidor y un
     * token de acceso.
     *
     * @param accessToken
     */
    public static void init(String accessToken) {
        DbxRequestConfig requestConfig;
        if(GDbxClient == null) {
            requestConfig = DbxRequestConfig.newBuilder("login-v2-mbp")
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();
            GDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
    }

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     * Esta metodo devuelve el objeto GDbxClient inicializado en el anterior metodo.
     *
     * @return GDbxClient
     */
    public static DbxClientV2 getDbxClient(){
        if (GDbxClient == null) { throw new IllegalStateException(NOT_INITIALIZE_MESSAGE); }
        return GDbxClient;
    }

    /**
     * @author Sergio Rodrigo
     * @date 20.10.2017
     * @task 1.1
     * changes
     *
     * Metodo estatico que devuelve el objeto  de autorizacion para peticiones que permite
     * cerrar la sesion y revocar el token de acceso
     * @return
     */
    public static DbxUserAuthRequests getAuthRequests(){
        if (GDbxClient == null) { throw new IllegalStateException(NOT_INITIALIZE_MESSAGE); }
        return GDbxClient.auth();
    }

    /*
    public static ListFolderResult getListFolderResult(String path) throws DbxException{
        return DropBoxClient.getDbxClient().files().listFolder(path);
    }
    */

    public static void resetDbxClient(){
        GDbxClient = null;
    }
}