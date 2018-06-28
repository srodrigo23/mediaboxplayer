package com.umss.rodres.mbp2.media.music;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.MyAdapter;
import com.umss.rodres.mbp2.search.FilterHelper;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public class MusicAdapter extends MyAdapter{

    private MusicPresenter mMusicPresenter;

    public MusicAdapter(Picasso picasso, List<File> mMusicFiles, MusicPresenter musicPresenter) {
        super(mMusicFiles, picasso);
        this.mMusicPresenter = musicPresenter;
    }

    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.item_file, parent, false);
        return new MusicViewHolder(view, mMusicPresenter, mPicasso);
    }
}