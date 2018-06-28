package com.umss.rodres.mbp2.media.image;

import android.content.Context;

import com.umss.rodres.mbp2.media.music.MusicAdapter;
import com.umss.rodres.mbp2.model.File;

import java.util.List;

public interface ImageContract {

    interface View
    {
        Context getContext();
        void showNoConnectionSnackBar();
        void showSnackBarMessage(String message);
        void setProgressIndicator(boolean active);
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
        void showHideElements();
        void singleTouchRecyclerItem(int pos);
        void notifyDataSetChanged();
        void replaceData(List<File> files);
        ImageAdapter getImageAdapter();
    }
}
