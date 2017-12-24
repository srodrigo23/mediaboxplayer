package com.umss.rodres.mbp2;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

public class GetListFolder extends AsyncTask<String, Void, ListFolderResult> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;

    private Exception mException;
    private String folder;

    public GetListFolder(DbxClientV2 mDbxClient, Callback mCallback) {
        this.mDbxClient = mDbxClient;
        this.mCallback = mCallback;
        //this.folder = folder;
    }

    public interface Callback{
        void onDataLoaded(ListFolderResult list);
        void onError(Exception e);
    }

    @Override
    protected void onPostExecute(ListFolderResult listFolderResult) {
        super.onPostExecute(listFolderResult);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onDataLoaded(listFolderResult);
        }
    }

    @Override
    protected ListFolderResult doInBackground(String... params) {
        try {
            return mDbxClient.files().listFolder(params[0]);
        } catch (DbxException e) {
            mException = e;
        }
        return null;
    }
}