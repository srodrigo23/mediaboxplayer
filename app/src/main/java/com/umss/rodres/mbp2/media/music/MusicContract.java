package com.umss.rodres.mbp2.media.music;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;

import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.util.Connection;

import java.util.List;

public interface MusicContract {

    interface View
    {
        Context getContext();
        void showNoConnectionSnackBar();
        void setProgressIndicator(boolean active);
        void showSnackBarMessage(String message);
        void switchIcon(int idResource);
        void showProgressBar();
        void hideProgressBar();
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
        void initPlayer();
        void play();
        void update();
        void singleTouchRecyclerItem(int pos);
        void notifyDataSetChanged();
        void replaceData(List<File> files);
        void putlinksAsyncAudio();
        void setFloatingButtonIcon();
        MusicAdapter getMusicAdapter();
        void showHideElements();
    }
}