package com.umss.rodres.mbp2;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

/**
 * Created by sergiorodrigo on 2/11/17.
 */

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Adaptador_Archivos extends
        RecyclerView.Adapter<Adaptador_Archivos.Metadata_ViewHolder> {

    private List<Metadata> mFiles;
    private final Callback mCallback;

    public interface Callback{
        void onFolderClicked(FolderMetadata folder);
        void onFileClicked(FileMetadata file);
    }

    public Adaptador_Archivos(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public void setmFiles(List<Metadata> list){
        this.mFiles = Collections.unmodifiableList(new ArrayList<Metadata>(list));
        notifyDataSetChanged();
    }

    @Override
    public Metadata_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.files_item, parent, false);
        return new Metadata_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Metadata_ViewHolder holder, int position) {
        holder.bind(mFiles.get(position));
    }

    public long getItemId(int pos){
        return mFiles.get(pos).getPathLower().hashCode();
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public class Metadata_ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{

        private final TextView nombreArchivo;
        private final ImageView iconArchivo;

        private Metadata mItem;

        public Metadata_ViewHolder(View itemView) {
            super(itemView);
            this.nombreArchivo = itemView.findViewById(R.id.idNombreArchivo);
            this.iconArchivo = itemView.findViewById(R.id.idIconElemento);

            itemView.setOnClickListener(this);
        }

        /**
         * Tomar especial atencion aqui es la accion que se realizara segun archivo
         * @param v
         */
        @Override

        public void onClick(View v) {
            if(mItem instanceof FolderMetadata){
                mCallback.onFolderClicked((FolderMetadata)mItem);
            }else if (mItem instanceof FileMetadata){
                mCallback.onFileClicked((FileMetadata) mItem);
            }
        }

        public void bind(Metadata item){
            mItem = item;
            String file  = item.getName();
            nombreArchivo.setText(file);
            if(file.endsWith(".mp3")){
                iconArchivo.setImageResource(R.mipmap.ic_mp3_file);
            }else if(file.endsWith(".mp4")){
                iconArchivo.setImageResource(R.mipmap.ic_file_mp4);
            }else if(file.endsWith(".jpg")){
                iconArchivo.setImageResource(R.mipmap.ic_file_jpg);
            }
        }
    }

}
