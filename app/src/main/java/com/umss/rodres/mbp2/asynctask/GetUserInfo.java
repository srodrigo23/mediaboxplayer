package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.users.DbxUserUsersRequests;
import com.umss.rodres.mbp2.model.DropBoxClient;
import com.umss.rodres.mbp2.model.Session;

public class GetUserInfo extends AsyncTask<Void, Void, Void> {

    private Callback mCallback;
    private Exception mException;

    public interface Callback{
        void onDataLoaded();
        void onError(Exception e);
    }

    public GetUserInfo(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * Proceso encargado de obtener la informacion de la cuenta en DropBox
     * @param voids
     * @return
     */

    @Override
    protected Void doInBackground(Void... voids) {
        DbxUserUsersRequests req = DropBoxClient.getDbxClient().users();
            try {
                Session.setUserName(req.getCurrentAccount().getName().getDisplayName());
                Session.setEmail(req.getCurrentAccount().getEmail());
                Session.setTypeAccount(req.getCurrentAccount().getAccountType().name());
                Session.setUsedSpace(req.getSpaceUsage().getUsed());
                Session.setTotalSpace(req.getSpaceUsage().getAllocation().getIndividualValue().getAllocated());
                Session.setTypeAccount(req.getCurrentAccount().getAccountType().name());
            } catch (DbxException e) {
                e.printStackTrace();
                mException = e;
                Log.d("error", e.getMessage());
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onDataLoaded();
        }
    }

}