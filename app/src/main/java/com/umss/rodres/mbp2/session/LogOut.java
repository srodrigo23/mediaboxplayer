package com.umss.rodres.mbp2.session;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.auth.DbxUserAuthRequests;

public class LogOut extends AsyncTask<Void, Void, Boolean> {

    private final DbxUserAuthRequests mDbxAuthRequests;
    private final CallBack mCallBack;

    private Exception mException;

    public LogOut(DbxUserAuthRequests mDbxAuthRequests, CallBack mCallBack) {
        this.mDbxAuthRequests = mDbxAuthRequests;
        this.mCallBack = mCallBack;
    }

    public interface CallBack{
        void onComplete(Boolean vb);
        void onError(Exception e);
    }

    @Override
     protected void onPostExecute(Boolean vb) {
        super.onPostExecute(vb);
        if(mException != null){
            mCallBack.onError(mException);
        }else{
            mCallBack.onComplete(vb);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean flag;
        try {
            mDbxAuthRequests.tokenRevoke();
            flag = true;
        } catch (DbxException e) {
            flag = false;
            mException = e;
        }
        return flag;
    }
}