package com.umss.rodres.mbp2.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.asynctask.SetDisconnectServer;
import com.umss.rodres.mbp2.helper.PicassoClient;
import com.umss.rodres.mbp2.login.LogInActivity;
import com.umss.rodres.mbp2.media.image.ImageFragment;

import com.umss.rodres.mbp2.media.music.MusicFragment;

import com.umss.rodres.mbp2.media.video.VideoFragment;
import com.umss.rodres.mbp2.model.DropBoxClient;
import com.umss.rodres.mbp2.model.Session;
import com.umss.rodres.mbp2.player.audio.AudioPlayer;
import com.umss.rodres.mbp2.util.Connection;
import com.umss.rodres.mbp2.util.Conversor;

public class MainPresenter implements MainContract.UserActionsListener{

    private MainContract.View mMainView;
    private Context           mContext;

    private MusicFragment  mMusicFragment;
    private ImageFragment  mImageFragment;
    private VideoFragment  mVideoFragment;


    public MainPresenter(@NonNull MainContract.View view){
        this.mMainView    = view;
        this.mContext = mMainView.getContext();
        initImagePicassoAgent();
        initFragments();
    }

    private void initFragments(){
        mMusicFragment = mMusicFragment.newInstance();
        mImageFragment = mImageFragment.newInstance();
        mVideoFragment = mVideoFragment.newInstance();
    }

    public MainContract.View getView(){
        return mMainView;
    }

    @Override
    public void launchMusicFragment(){
        mMainView.setToolBarInfo(mContext.getString(R.string.musica), R.mipmap.ic_folder_music);
        mMainView.showFragment(mMusicFragment);
    }

    @Override
    public void launchImageFragment(){
        mMainView.setToolBarInfo(mContext.getString(R.string.imagen), R.mipmap.ic_folder_picture);
        mMainView.showFragment(mImageFragment);
    }

    @Override
    public void launchVideoFragment(){
        mMainView.setToolBarInfo(mContext.getString(R.string.video), R.mipmap.ic_folder_video);
        mMainView.showFragment(mVideoFragment);
    }

    private void initImagePicassoAgent(){
        PicassoClient.init(mContext, DropBoxClient.getDbxClient());
    }

    @Override
    public boolean isConected() {
        return Connection.isConnected(mContext);
    }

    @Override
    public void exitApp() {
        exitAppDialog();
    }

    @Override
    public void closeSession() {
        if (!isConected()){
            mMainView.showNoConnectionMesagge();
        }else{
           closeSessionDialog();
        }
    }

    public Context getContext() {
        return mContext;
    }

    private void closeSessionAction(){
        mMainView.showProgressDialog(mContext.getString(R.string.closing_session));
        new SetDisconnectServer(new SetDisconnectServer.Callback() {
            @Override
            public void onDisconnectedServer() {
                AudioPlayer.resetPlayer();
                mMainView.hideProgressDialog();
                mMainView.finishActivity();
                mContext.startActivity(new Intent(mContext, LogInActivity.class));
                mMainView.animation();
            }
            @Override
            public void onError(Exception e) {
                mMainView.hideProgressDialog();
            }
        }).execute();
    }

    private void closeSessionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.exit_session);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                closeSessionAction();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.preg_para_salir);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                exitAppAction();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void setCapacityAccount()
    {
        long total = Session.getTotalSpace();
        long used  = Session.getUsedSpace();
        String etiqTotalEsp   = Conversor.toStringSize(total);
        String etiqEspUtiliz  = Conversor.toStringSize(used);
        mMainView.setProgressBar((int)Conversor.toMB(total), (int)Conversor.toMB(used));
        String toShowCapacity = etiqEspUtiliz + " de " + etiqTotalEsp;
        mMainView.showCapacity(toShowCapacity);
    }

    private void exitAppAction(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mMainView.animation();
        mMainView.getContext().startActivity(intent);
        System.exit(0);
    }
}