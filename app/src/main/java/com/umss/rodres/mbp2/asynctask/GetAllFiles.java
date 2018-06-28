package com.umss.rodres.mbp2.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.umss.rodres.mbp2.model.DropBoxClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GetAllFiles extends AsyncTask<String, Void, List<FileMetadata>>{

    private final Callback mCallback;
    private Exception mException;

    public GetAllFiles(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public interface Callback{
        void onDataLoaded(List<FileMetadata>list);
        void onError(Exception e);
    }

    /**
     * Proceso para poder obtener todos los archivos en un determinado directorio si no encuentra
     * el directorio
     * @param path : El directorio para trabajar
     * @return Una lista con los metadatos que tiene DropBox sobre los archivos en ese directorio
     */
    @Override
    protected List<FileMetadata> doInBackground(String... path) {
        String main  = "", pathObject = path[0];
        boolean finded = false;
        List<Metadata> folderMetadata;
        Iterator it;
        try {
            folderMetadata = DropBoxClient.getDbxClient().files().listFolder(main).getEntries();
            it  = folderMetadata.iterator();
            while(it.hasNext()){
                Metadata m = (Metadata) it.next();
                if( m instanceof FolderMetadata){
                    if(m.getPathLower().equals(pathObject)){
                        finded = true;
                        break;
                    }
                }
            }
            if(!finded){
                CreateFolderResult createFolderResult =  DropBoxClient.getDbxClient().files().createFolderV2(pathObject);
                Log.d("DropBox", "tuve que crear la carpeta");
            }

        } catch (DbxException e) {
            e.printStackTrace();
        }


        try {
            List<FileMetadata> files  = new ArrayList<>();
            ListFolderBuilder listFolderBuilder = DropBoxClient.getDbxClient()
                    .files()
                    .listFolderBuilder(path[0]);
            ListFolderResult result = listFolderBuilder.withRecursive(true).start();
            while(true){
                if(result!= null){
                    for ( Metadata entry : result.getEntries()) {
                        if (entry instanceof FileMetadata) files.add((FileMetadata) entry);
                    }
                    if(!result.getHasMore()){
                        break;
                    }
                    result = DropBoxClient.getDbxClient().files().listFolderContinue(result.getCursor());
                }
            }
            return files;
        } catch (DbxException e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<FileMetadata> l) {
        super.onPostExecute(l);
        if (mException != null){
            mCallback.onError(mException);
        }else{
            mCallback.onDataLoaded(l);
        }
    }
}
