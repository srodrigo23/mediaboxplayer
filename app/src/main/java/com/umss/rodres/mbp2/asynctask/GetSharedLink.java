package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.umss.rodres.mbp2.model.DropBoxClient;

public class GetSharedLink extends AsyncTask<String, Void, String> {

    private final Callback mCallback;
    private Exception mException;

    public interface Callback{
        void onLinkLoaded(String link);
        void onError(Exception e);
    }

    public GetSharedLink(Callback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     *
     * @param path
     * @return
     */
    @Override
    protected String doInBackground(String... path) {
        try {
            return DropBoxClient.getDbxClient().sharing().createSharedLink(path[0]).getUrl();
        } catch (DbxException e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onLinkLoaded(path);
        }
    }
}