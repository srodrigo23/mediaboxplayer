package com.umss.rodres.mbp2.files;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.media.image.ImageViewHolder;
import com.umss.rodres.mbp2.media.music.MusicViewHolder;
import com.umss.rodres.mbp2.media.video.VideoViewHolder;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

    protected Context mContext;
    protected List<File> mFiles;
    protected final Picasso mPicasso;

    public MyAdapter(List<File> files, Picasso picasso){
        this.mFiles   = files;
        this.mPicasso = picasso;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof MusicViewHolder){
            ((MusicViewHolder)holder).bindMusicFile(mFiles.get(position), position);
        }else{
            if(holder instanceof ImageViewHolder){
                ((ImageViewHolder)holder).bindImageFile(mFiles.get(position), position);
            }else if(holder instanceof VideoViewHolder){
                ((VideoViewHolder)holder).bindVideoFile(mFiles.get(position), position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }

    public void replaceData(List<File> files) {
        mFiles = files;
        notifyDataSetChanged();
    }

    public List<File> getFiles(){
        return mFiles;
    }
}