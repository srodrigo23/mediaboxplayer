package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.auth.DbxUserAuthRequests;
import com.umss.rodres.mbp2.model.DropBoxClient;
import com.umss.rodres.mbp2.model.Session;

public class SetDisconnectServer extends AsyncTask<Void, Void, Void>{

    private Callback mCallback;
    private Exception mException;

    public SetDisconnectServer(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public interface Callback{
        void onDisconnectedServer();
        void onError(Exception e);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DbxUserAuthRequests req = DropBoxClient.getAuthRequests();
        try {
            req.tokenRevoke();
            DropBoxClient.resetDbxClient();
            Session.clearInfoAccount();
        } catch (DbxException e) {
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
            mCallback.onDisconnectedServer();
        }
    }
}
