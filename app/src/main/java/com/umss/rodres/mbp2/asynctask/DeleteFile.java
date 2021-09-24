package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.umss.rodres.mbp2.model.DropBoxClient;

public class DeleteFile extends AsyncTask<String, Void, Void>{

    private final Callback mCallback;
    private Exception mException;

    public DeleteFile(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public interface Callback{
        void onFileDeleted();
        void onError(Exception e);
    }

    @Override
    protected Void doInBackground(String... path) {
        try {
            DropBoxClient.getDbxClient().files().delete(path[0]);
        }
        catch (DbxException dbxe) {
            mException = dbxe;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onFileDeleted();
        }
    }
}
