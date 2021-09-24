package com.umss.rodres.mbp2.media.music;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.asynctask.DeleteFile;
import com.umss.rodres.mbp2.asynctask.GetAllFiles;
import com.umss.rodres.mbp2.asynctask.GetLinkFile;
import com.umss.rodres.mbp2.asynctask.GetSharedLink;
import com.umss.rodres.mbp2.asynctask.GetUserInfo;
import com.umss.rodres.mbp2.helper.PicassoClient;
import com.umss.rodres.mbp2.main.MainActivity;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.model.Library;
import com.umss.rodres.mbp2.model.Session;
import com.umss.rodres.mbp2.player.audio.ActivityAudioPlayer;
import com.umss.rodres.mbp2.player.audio.AudioPlayer;
import com.umss.rodres.mbp2.util.Connection;
import com.umss.rodres.mbp2.util.Settings;

import java.util.List;

public class MusicPresenter implements MusicContract.UserActionsListener {

    private static MusicAdapter       mMusicAdapter;
    private static MusicContract.View mMusicView;
    private Context                   mContext;

    private AsyncTask          mProcessAudioLinks[];

    private boolean isUpdating;

    public MusicPresenter(MusicContract.View musicView) {
        this.mMusicView     = musicView;
        this.mMusicAdapter  = new MusicAdapter(
                PicassoClient.getPicasso(),
                Library.mAudio,
                this);
        this.mContext       = mMusicView.getContext();
        isUpdating          = false;
        putlinksAsyncAudio();
    }

    @Override
    public void notifyDataSetChanged(){
        mMusicAdapter.notifyDataSetChanged();
    }

    @Override
    public void replaceData(List<File> files) {
        mMusicAdapter.replaceData(files);
    }

    @Override
    public MusicAdapter getMusicAdapter() {
        return mMusicAdapter;
    }

    @Override
    public void play() {
        if(!Connection.isConnected(mContext)){
            mMusicView.showNoConnectionSnackBar();
        }else{
            if(Library.mAudio.size()==0){
                mMusicView.showSnackBarMessage(mContext.getString(R.string.nothing_to_play));
            }else{
                if(isPlaying()){
                    AudioPlayer.pause();
                    mMusicView.switchIcon(R.drawable.ic_play_black_18dp);
                } else{
                    AudioPlayer.prepare();
                    AudioPlayer.play();
                    mMusicView.switchIcon(R.drawable.ic_pause);
                }
            }
        }
    }

    @Override
    public void update() {
        mMusicView.hideProgressBar();
        isUpdating = true;
        new GetUserInfo(new GetUserInfo.Callback() {
            @Override
            public void onDataLoaded() {
                MainActivity.mMainPresenter.setCapacityAccount();
            }
            @Override
            public void onError(Exception e){
                mMusicView.showToastMessage(mContext.getString(R.string.error_loading_account_info));
            }
        }).execute();

        if(Connection.isConnected(mContext)){
            stopGetLink();
            AudioPlayer.resetPlayer();
            AudioPlayer.initPlayer(mContext);
            setFloatingButtonIcon();
            new GetAllFiles(new GetAllFiles.Callback() {
                @Override
                public void onDataLoaded(List<FileMetadata> list) {
                    mMusicView.setProgressIndicator(false);
                    isUpdating = false;
                    Library.initAudioLib(list);
                    Library.mergeList();
                    replaceData(Library.mAudio);
                    showHideElements();
                    putlinksAsyncAudio();
                    mMusicView.showSnackBarMessage(mContext.getString(R.string.updated_music));
                }
                @Override
                public void onError(Exception e) {
                    mMusicView.setProgressIndicator(false);
                    Toast.makeText(mContext, "Error al actualizar los archivos de Audio",
                            Toast.LENGTH_SHORT).show();
                }
            }).execute(mContext.getString(R.string.path_music));
        }else{
            mMusicView.setProgressIndicator(false);
            mMusicView.showNoConnectionSnackBar();
        }
    }

    @Override
    public void showHideElements(){
        if(Library.mAudio.size()==0){
            mMusicView.showNoImageFounded();
            mMusicView.hideRecyclerView();
        }else{
            mMusicView.hideNoImageFounded();
            mMusicView.showRecyclerView();
        }
    }

    @Override
    public void singleTouchRecyclerItem(int posPressed) {
        if(!Connection.isConnected(mContext))
            mMusicView.showNoConnectionSnackBar();
        else {
            if(isUpdating){
                mMusicView.showSnackBarMessage(mContext.getString(R.string.updating));
            }else{
                if(!haveLink(posPressed)){
                    mMusicView.showSnackBarMessage(getContext().getString(R.string.link_not_disponible));
                }else{
                    mMusicAdapter.notifyItemChanged(posPressed);
                    AudioPlayer.prepare();
                    if(posPressed != AudioPlayer.getCurrentPos())
                        AudioPlayer.seekTo(posPressed);
                    mContext.startActivity(new Intent(mContext, ActivityAudioPlayer.class));
                }
            }
        }
    }

    @Override
    public void initPlayer() {
        AudioPlayer.initPlayer(mContext);
    }

    public Context getContext(){
        return mContext;
    }

    private boolean haveLink(int pos){
        return Library.mAudio.get(pos).getTempLink() != "";
    }

    private boolean isPlaying() {
        return AudioPlayer.isPlaying();
    }

    private void stopGetLink(){
        for(int i=0; i<Library.mAudio.size(); i++){
            if(mProcessAudioLinks[i] != null)
                mProcessAudioLinks[i].cancel(true);
        }
    }

    @Override
    public void putlinksAsyncAudio(){
        mProcessAudioLinks = new AsyncTask[Library.mAudio.size()];
        for(int i=0; i< Library.mAudio.size(); i++ ){
            if(Library.mAudio.get(i).getTempLink().isEmpty()){
                final int finalI = i;
                mProcessAudioLinks[i] = new GetLinkFile(new GetLinkFile.Callback() {
                    @Override
                    public void onLinkLoaded(String link) {
                        Library.mAudio.get(finalI).setTempLink(link);
                        AudioPlayer.addMediaSource(finalI, link);
                        mMusicAdapter.notifyItemChanged(finalI);
                    }
                    @Override
                    public void onError(Exception e) {

                    }
                }).execute(Library.mAudio.get(finalI).getPath());
            }
        }
    }

    public static void notifychange(){
        mMusicAdapter.notifyDataSetChanged();
    }

    public static void showProgess(){
        mMusicView.showProgressBar();
    }

    public static void hideProgess(){
        mMusicView.hideProgressBar();
    }

    public void download(int pos){
        if(!Connection.isConnected(mContext))
            mMusicView.showNoConnectionSnackBar();
        else{
            if(Library.mAudio.get(pos).getTempLink().isEmpty()){
                mMusicView.showToastMessage(mContext.getString(R.string.cant_download));
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Library.mAudio.get(pos).getTempLink()));
                mContext.startActivity(browserIntent);
            }
        }
    }

    public void sharelink(final int pos){
        if(!Connection.isConnected(mContext))
            mMusicView.showNoConnectionSnackBar();
        else{
            if(Library.mAudio.get(pos).getTempLink().isEmpty()){
                mMusicView.showToastMessage(mContext.getString(R.string.cant_share));
            }else{
                if(Library.mAudio.get(pos).getShareLink().isEmpty()){
                    mMusicView.showProgressDialog(mContext.getString(R.string.get_shared_link));
                    new GetSharedLink(new GetSharedLink.Callback() {
                        @Override
                        public void onLinkLoaded(String link) {
                            Library.mAudio.get(pos).setShareLink(link);
                            mMusicView.hideProgressDialog();
                            shareElement(link);
                        }
                        @Override
                        public void onError(Exception e) {
                            mMusicView.hideProgressDialog();
                            mMusicView.showSnackBarMessage(mContext.getString(R.string.error_sharing_file));
                        }
                    }).execute(Library.mAudio.get(pos).getPath());

                }else{
                    String toShareFile = Library.mAudio.get(pos).getShareLink();
                    shareElement(toShareFile);
                }
            }
        }
    }

    private void shareElement(String toShare){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, toShare);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void delete(int pos){
        if(!Connection.isConnected(mContext))
            mMusicView.showNoConnectionSnackBar();
        else{
            if((AudioPlayer.getCurrentPos() == pos && AudioPlayer.isPlaying())) {
                mMusicView.showToastMessage(mContext.getString(R.string.cant_delete));
            }else{
                if(Library.mAudio.get(pos).getTempLink().isEmpty()){
                    mMusicView.showToastMessage(mContext.getString(R.string.cant_delete));
                }else{
                    String nameFile, path;
                    nameFile = Library.mAudio.get(pos).getName();
                    path     = Library.mAudio.get(pos).getPath();
                    deleteFileDialog(nameFile, path, pos);
                }
            }
        }
    }

    private void deleteFile(final String fileName, String path, final int pos){
        mMusicView.showProgressDialog(mContext.getString(R.string.deleting));
        new DeleteFile(new DeleteFile.Callback() {
            @Override
            public void onFileDeleted() {
                mMusicView.hideProgressDialog();
                Library.mAudio.remove(pos);
                long espMemory = Library.mAudio.get(pos).getSize();

                Session.setUsedSpace(Session.getUsedSpace() - espMemory);
                MainActivity.mMainPresenter.setCapacityAccount();

                if(AudioPlayer.getAmountMediaSource() >= pos){
                    AudioPlayer.removeMediaSource(pos);
                }else{
                    mProcessAudioLinks[pos].cancel(true);
                }
                mMusicAdapter.notifyItemRemoved(pos);
                mMusicView.showToastMessage(fileName + " " + mContext.getString(R.string.has_been_deleted));
            }

            @Override
            public void onError(Exception e) {
                mMusicView.hideProgressDialog();
                mMusicView.showToastMessage(mContext.getString(R.string.error_deleting_file) + fileName);
            }
        }).execute(path);
    }

    private void deleteFileDialog(final String nameFile, final String path, final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.are_you_sure_to_delete) + "\n" + nameFile);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                deleteFile(nameFile, path, pos);
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

    @Override
    public void setFloatingButtonIcon() {
        if (AudioPlayer.isPlaying())
            mMusicView.switchIcon(R.drawable.ic_pause);
        else
            mMusicView.switchIcon(R.drawable.ic_play_black_18dp);

    }
}