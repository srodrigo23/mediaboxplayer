package com.umss.rodres.mbp2.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.asynctask.GetAllFiles;
import com.umss.rodres.mbp2.asynctask.GetUserInfo;
import com.umss.rodres.mbp2.asynctask.SetDisconnectServer;
import com.umss.rodres.mbp2.main.MainActivity;
import com.umss.rodres.mbp2.model.DropBoxClient;
import com.umss.rodres.mbp2.model.Library;
import com.umss.rodres.mbp2.model.Session;
import com.umss.rodres.mbp2.util.Connection;

import java.util.List;

public class LoginPresenter implements LoginContract.UserActionsListener{

    private LoginContract.View mView;
    private Context            mContext;
    private Boolean            mReq;

    public LoginPresenter(@NonNull LoginContract.View view){
        this.mView    = view;
        this.mContext = mView.getContext();
        this.mReq     = false;
    }

    @Override
    public void login() {
        if (!isConected()){
            mView.showNoConnectionError();
        }else{
            if(!Session.isSessionStarted()){
                mReq = true;
                Auth.startOAuth2Authentication(mContext, mContext.getString(R.string.app_key));
            }else{
                launchMainActivity();
            }
        }
    }

    private boolean isConected() {
        return Connection.isConnected(mContext);
    }

    private void launchMainActivity() {
        mView.finishActivity();
        mView.getContext().startActivity(new Intent(mContext, MainActivity.class));
        mView.animationTraslation();
    }

    @Override
    public void setOnResume() {
        if(!isConected()){
            mView.hideLoginButton();
            mView.showTryAgainButton();
        }else{
            mView.hideTryAgainButton();
            if(mReq)
                saveToken();
            if(Session.isSessionStarted()) {
                initContent();
            }else{
                mView.showLoginButton();
                mView.setTextLoginButton(mContext.getString(R.string.start_session));
                mView.hideLogoutButton();
            }
        }
    }

    private void initContent(){
        DropBoxClient.init(Session.getToken());
        //Session.initDropboxClient();
        loadInfoAccount();
        loadAccountContent();
    }

    private void loadInfoAccount() {
        mView.showProgress();
        mView.hideLoginButton();
        mView.hideLogoutButton();
        new GetUserInfo(new GetUserInfo.Callback() {
            @Override
            public void onDataLoaded() {
                mView.setTextLoginButton(mView.getContext().getString(R.string.continue_how) + "\n    "
                        +  Session.getUserName() + "    ");
            }
            @Override
            public void onError(Exception e) {
                Log.d("Error!", e.getMessage());
            }
        }).execute();
    }
    private void loadAccountContent() {
        mView.hideLoginButton();
        mView.hideLogoutButton();
        initAudioLib();
        initImagLib();
        initVideoLib();
    }

    private void initAudioLib() {
        mView.showProgress();
        new GetAllFiles(new GetAllFiles.Callback() {
            @Override
            public void onDataLoaded(List<FileMetadata> list) {
                Library.initAudioLib(list);
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(mContext, "Un error  con el audio a ocurrido", Toast.LENGTH_SHORT).show();
            }
        }).execute(mContext.getString(R.string.path_music));
    }

    private void initImagLib() {
        new GetAllFiles(new GetAllFiles.Callback() {
            @Override
            public void onDataLoaded(List<FileMetadata> list) {
                Library.initImageLib(list);
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(mContext, "Un error con las imagenes a ocurrido", Toast.LENGTH_SHORT).show();
            }
        }).execute(mContext.getString(R.string.path_image));
    }

    private void initVideoLib() {
        new GetAllFiles(new GetAllFiles.Callback() {
            @Override
            public void onDataLoaded(List<FileMetadata> list) {
                mView.hideProgress();
                Library.initVideoLib(list);
                /* para iniciar la lista para buscar!!!!! */
                Library.mergeList();
                mView.showLoginButton();
                mView.showLogoutButton();
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(mContext, "Un error con los videos a ocurrido", Toast.LENGTH_SHORT).show();
            }
        }).execute(mContext.getString(R.string.path_video));
    }

    @Override
    public void logOut() {
        if (!isConected()){
            mView.showNoConnectionError();
        }else{
            mView.showProgress();
            mView.hideLoginButton();
            mView.hideLogoutButton();
            new SetDisconnectServer(new SetDisconnectServer.Callback() {
                @Override
                public void onDisconnectedServer() {
                    Session.clearInfoAccount();
                    mView.hideProgress();
                    mView.setTextLoginButton(mView.getContext().getString(R.string.start_session));
                    mView.showLoginButton();
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(mContext, "No se pudo desconectar con el Servidor",
                            Toast.LENGTH_LONG).show();
                }
            }).execute();
        }
    }

    @Override
    public void initSession() {
        Session.initSession(mContext);
    }

    private void saveToken() {
        Session.saveToken();
    }

    @Override
    public void tryAgain() {
        if (!isConected()){
            mView.showNoConnectionError();
        }else{
            if(Session.isSessionStarted()){
                mView.showProgress();
                mView.hideTryAgainButton();
                initContent();
            }else{
                mView.hideTryAgainButton();
                mView.showLoginButton();
                mView.showLoginButton();
                mView.setTextLoginButton(mContext.getString(R.string.start_session));
            }
        }
    }
}