package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.umss.rodres.mbp2.model.DropBoxClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UploadFile extends AsyncTask<String, Void, Void> {

    private final Callback mCallback;
    private Exception mException;

    public interface Callback{
        void onUploadedFile();
        void onError(Exception e);
    }

    public  UploadFile( Callback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     *
     * @param
     * @return
     */
    @Override
    protected Void doInBackground(String... param) {

        try {
            InputStream in = new FileInputStream(param[0]);
            DropBoxClient.getDbxClient().files().uploadBuilder(param[1]).uploadAndFinish(in);
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (DbxException dbxe) {
            dbxe.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onUploadedFile();
        }
    }
}
