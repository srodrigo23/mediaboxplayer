package com.umss.rodres.mbp2.main;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;

public interface MainContract {

    interface View
    {
        void showNoConnectionMesagge();
        void showToastMessage(String fileName);
        void showSnackBarMessage(String message);
        Context getContext();
        void setToolBarInfo(String tittle, int imagRes);
        void animation();
        void showProgressDialog(String message);
        void hideProgressDialog();
        void finishActivity();
        void showFragment(Fragment fragment);
        void showCapacity(String text);
        void setProgressBar(int max, int progress);
    }

    interface UserActionsListener
    {
        boolean isConected();
        void launchMusicFragment();
        void launchImageFragment();
        void launchVideoFragment();
        void setCapacityAccount();
        void exitApp();
        void closeSession();
    }
}
