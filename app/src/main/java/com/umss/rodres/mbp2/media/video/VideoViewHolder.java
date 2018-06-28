package com.umss.rodres.mbp2.media.video;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.ViewHolder;
import com.umss.rodres.mbp2.helper.FileThumbnailRequestHandler;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.player.video.VideoPlayer;
import com.umss.rodres.mbp2.util.Conversor;
import com.umss.rodres.mbp2.util.StringProcess;

public class VideoViewHolder extends ViewHolder implements View.OnClickListener,
                                                           View.OnCreateContextMenuListener {

    private VideoPresenter mVideoPresenter;
    private int            mPosPressed;
    private Picasso        mPicasso;
    private Context        mContext;

    public VideoViewHolder(View           itemView,
                           VideoPresenter videoPresenter,
                           Picasso        picasso) {
        super(itemView);
        this.mPicasso        = picasso;
        this.mPosPressed     = -1;
        this.mVideoPresenter = videoPresenter;
        this.mContext        = mVideoPresenter.getContext();

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {
        mPosPressed = getAdapterPosition();
        mVideoPresenter.singleTouchRecyclerItem(mPosPressed);
    }


    public void bindVideoFile(File file, int pos) {
        fileName.setText(file.getName());
        filePath.setText(StringProcess.procesPath(file.getPath()));
        fileSize.setText(Conversor.toStringSize(file.getSize()));

        if(pos == VideoPlayer.mToPlay)
            mPicasso.load(R.drawable.video)
                    .noFade()
                    .into(fileIcon);
        else
            mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri(file))
                    .placeholder(R.drawable.ic_file_video)
                    .error(R.drawable.ic_file_video)
                    .fit()
                    .centerCrop()
                    .into(fileIcon);

        if (!file.getTempLink().equals("")) {
            fileChecked.setText(mContext.getString(R.string.link_disponible));
            fileChecked.setTextColor(mContext.getResources().getColor(R.color.especial_green));
        }else{
            fileChecked.setText(mContext.getString(R.string.link_not_disponible));
            fileChecked.setTextColor(mContext.getResources().getColor(R.color.especial_red));
        }
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
                    mVideoPresenter.download(mPosPressed);
                    break;
                case 2:
                    mVideoPresenter.sharelink(mPosPressed);
                    break;
                case 3:
                    mVideoPresenter.delete(mPosPressed);
                    break;
            }
            return true;
        }
    };
}
