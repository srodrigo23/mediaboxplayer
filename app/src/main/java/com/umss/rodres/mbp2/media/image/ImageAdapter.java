package com.umss.rodres.mbp2.media.image;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.MyAdapter;
import com.umss.rodres.mbp2.search.FilterHelper;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public class ImageAdapter extends MyAdapter {

    private ImagePresenter mImagePresenter;

    public ImageAdapter(Picasso picasso, List<File> mImageFiles, ImagePresenter imagePresenter) {
        super(mImageFiles, picasso);
        this.mImagePresenter = imagePresenter;
    }

    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view, mImagePresenter, mPicasso);
    }
}