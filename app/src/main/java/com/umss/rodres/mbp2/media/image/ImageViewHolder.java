package com.umss.rodres.mbp2.media.image;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.ViewHolder;
import com.umss.rodres.mbp2.helper.FileThumbnailRequestHandler;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.util.StringProcess;

public class ImageViewHolder extends ViewHolder implements View.OnClickListener,
                                                            View.OnCreateContextMenuListener
{
    private ImagePresenter mImagePresenter;
    private int            mPosPressed;
    private Context        mContext;
    private Picasso        mPicasso;

    public ImageViewHolder(View itemView,
                           ImagePresenter imagePresenter,
                           Picasso picasso) {
        super(itemView);
        this.mPicasso        = picasso;
        this.mImagePresenter = imagePresenter;
        this.mContext        = mImagePresenter.getContext();
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {
        mPosPressed = getAdapterPosition();
        mImagePresenter.singleTouchRecyclerItem(mPosPressed);
    }

    public void bindImageFile(File file, int pos) {
        fileName.setText(file.getName());
        filePath.setText(StringProcess.procesPath(file.getPath()));
        mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri(file))
                .placeholder(R.drawable.ic_file_image_grey600_36dp)
                .error(R.drawable.ic_file_image_grey600_36dp)
                .fit()
                .centerCrop()
                .into(fileIcon);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(mContext.getString(R.string.action));
        MenuItem download = menu.add(0, 1, 1, mContext.getString(R.string.download));
        MenuItem share    = menu.add(0, 2, 2, mContext.getString(R.string.shareFile));
        MenuItem delete   = menu.add(0, 3, 3, mContext.getString(R.string.delete));
        download.setOnMenuItemClickListener(onEditMenu);
        share.setOnMenuItemClickListener(onEditMenu);
        delete.setOnMenuItemClickListener(onEditMenu);
    }

    private MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mPosPressed = getAdapterPosition();
            switch (item.getItemId()) {
                case 1:
                    mImagePresenter.download(mPosPressed);
                    break;
                case 2:
                    mImagePresenter.sharelink(mPosPressed);
                    break;
                case 3:
                    mImagePresenter.delete(mPosPressed);
                    break;
            }
            return true;
        }
    };
}