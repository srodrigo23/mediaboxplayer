package com.umss.rodres.mbp2.media.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.MyAdapter;
import com.umss.rodres.mbp2.media.music.MusicPresenter;
import com.umss.rodres.mbp2.search.FilterHelper;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public class VideoAdapter extends MyAdapter{

    private VideoPresenter mVideoPresenter;

    public VideoAdapter(Picasso picasso, List<File> mVideofiles, VideoPresenter videoPresenter) {
        super(mVideofiles, picasso);
        this.mVideoPresenter = videoPresenter;
    }

    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.item_file, parent, false);
        return new VideoViewHolder(view, mVideoPresenter, mPicasso);
    }
}