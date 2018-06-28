package com.umss.rodres.mbp2.media.video;

import android.content.Context;

import com.umss.rodres.mbp2.media.music.MusicAdapter;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public interface VideoContract {

    interface View
    {
        Context getContext();
        void showNoConnectionSnackBar();
        void setProgressIndicator(boolean active);
        void showSnackBarMessage(String message);
        void showProgressDialog(String message);
        void hideProgressDialog();
        void showToastMessage(String message);

        void showRecyclerView();
        void hideRecyclerView();
        void showNoImageFounded();
        void hideNoImageFounded();
    }

    interface UserActionsListener
    {
        void update();
        void initPlayer();
        void singleTouchRecyclerItem(int pos);
        void notifyDataSetChanged();
        void putlinksAsyncVideo();
        VideoAdapter getVideoAdapter();
        void replaceData(List<File> files);
        void showHideElements();
    }
}
