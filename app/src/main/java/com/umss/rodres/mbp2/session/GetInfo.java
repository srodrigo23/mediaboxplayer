package com.umss.rodres.mbp2.session;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

public class GetInfo extends AsyncTask<Void, Void, Boolean> {

    private final DbxClientV2 mDbxClient;
    private final CallBack mCallBack;

    private Exception mException;
    private SharedPreferences prefs;
    private Boolean flag;

    public GetInfo(DbxClientV2 dbxClient, SharedPreferences p, CallBack mCallBack){
        this.mDbxClient = dbxClient;
        this.mCallBack  = mCallBack;
        this.prefs      = p;
    }

    public interface CallBack{
        void onComplete(Boolean flag);
        void onError(Exception e);
    }

    @Override
    protected void onPostExecute(Boolean flag) {
        super.onPostExecute(flag);
        if(mException != null){
            mCallBack.onError(mException);
        }else{
            mCallBack.onComplete(flag);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            prefs.edit().putString("name", mDbxClient.users().
                                                            getCurrentAccount().
                                                            getName().getDisplayName()).apply();

            prefs.edit().putString("email", mDbxClient.users().
                                                            getCurrentAccount().getEmail()).apply();

            prefs.edit().putString("type", mDbxClient.users().
                                                            getCurrentAccount().
                                                            getAccountType().name());
            prefs.edit().putLong("espUsado", mDbxClient.users().getSpaceUsage().getUsed()).apply();
            prefs.edit().putLong("espTotal", mDbxClient.users().
                                        getSpaceUsage().
                                        getAllocation().
                                        getIndividualValue().getAllocated()).apply();
            flag = true;
            prefs.edit().putBoolean("flag", flag).apply();
        } catch (DbxException e) {
            mException = e;
            flag = false;
            prefs.edit().putBoolean("flag", flag).apply();
        }
        return flag;
    }
}