package com.umss.rodres.mbp2.login;

import android.content.Context;

public interface LoginContract {

    interface View
    {
        Context getContext();
        void showNoConnectionError();
        void showProgress();
        void hideProgress();
        void showLoginButton();
        void hideLoginButton();
        void showLogoutButton();
        void hideLogoutButton();
        void showTryAgainButton();
        void hideTryAgainButton();
        void finishActivity();
        void setTextLoginButton(String userName);
        void animationTraslation();
    }

    interface UserActionsListener
    {
        void setOnResume();
        void login();
        void logOut();
        void initSession();
        void tryAgain();
    }
}