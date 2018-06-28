package com.umss.rodres.mbp2.media.video;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import com.umss.rodres.mbp2.player.audio.AudioPlayer;
import com.umss.rodres.mbp2.player.video.ActivityVideoPlayer;
import com.umss.rodres.mbp2.player.video.VideoPlayer;
import com.umss.rodres.mbp2.util.Connection;

import java.util.List;

public class VideoPresenter implements VideoContract.UserActionsListener{

    private VideoAdapter       mVideoAdapter;
    private VideoContract.View mVideoView;
    private Context            mContext;
    private boolean            isUpdating;
    private AsyncTask          processVideoLinks[];

    public VideoPresenter(VideoContract.View videoView) {
        this.mVideoView    = videoView;
        this.mVideoAdapter = new VideoAdapter(PicassoClient.getPicasso(),
                Library.mVideo,
                this);
        this.mContext      = mVideoView.getContext();
        isUpdating = false;
        putlinksAsyncVideo();
    }

    @Override
    public void notifyDataSetChanged() {
        mVideoAdapter.notifyDataSetChanged();
    }

    @Override
    public void replaceData(List<File> files) {
        mVideoAdapter.replaceData(files);
    }

    @Override
    public void showHideElements() {
        if(Library.mVideo.size()==0){
            mVideoView.showNoImageFounded();
            mVideoView.hideRecyclerView();
        }else{
            mVideoView.hideNoImageFounded();
            mVideoView.showRecyclerView();
        }
    }

    @Override
    public VideoAdapter getVideoAdapter() {
        return mVideoAdapter;
    }

    @Override
    public void initPlayer() {
        VideoPlayer.initPlayer(mContext);
    }

    public Context getContext() {
        return mContext;
    }

    private boolean haveLink(int pos){
        if(pos!= -1){
            return !Library.mVideo.get(pos).getTempLink().equals("");
        }
        return false;
    }

    private void stopGetLink(){
        for(int i=0; i<Library.mVideo.size(); i++){
            if(processVideoLinks[i] != null)
                processVideoLinks[i].cancel(true);
        }
    }


    @Override
    public void update() {
        isUpdating = true;
        new GetUserInfo(new GetUserInfo.Callback() {
            @Override
            public void onDataLoaded() {
                MainActivity.mMainPresenter.setCapacityAccount();
            }
            @Override
            public void onError(Exception e){
                mVideoView.showToastMessage(mContext.getString(R.string.error_loading_account_info));
            }
        }).execute();

        if(Connection.isConnected(mContext)){
            stopGetLink();
            VideoPlayer.resetPlayer();
            initPlayer();
            new GetAllFiles(new GetAllFiles.Callback() {
                @Override
                public void onDataLoaded(List<FileMetadata> list) {
                    mVideoView.setProgressIndicator(false);
                    isUpdating = false;
                    Library.initVideoLib(list);
                    Library.mergeList();
                    replaceData(Library.mVideo);
                    showHideElements();
                    putlinksAsyncVideo();
                    mVideoView.showSnackBarMessage(mContext.getString(R.string.updated_video));
                }
                @Override
                public void onError(Exception e) {
                    mVideoView.setProgressIndicator(false);
                    Toast.makeText(mContext, "Error al actualizar los archivos de Audio",
                            Toast.LENGTH_SHORT).show();
                }
            }).execute(mContext.getString(R.string.path_video));
        }else{
            mVideoView.setProgressIndicator(false);
            mVideoView.showNoConnectionSnackBar();
        }
    }

    @Override
    public void singleTouchRecyclerItem(int posPressed) {
        Log.d("pos", posPressed+"");
        if(!Connection.isConnected(mContext))
            mVideoView.showNoConnectionSnackBar();
        else{
            if(isUpdating){
                mVideoView.showToastMessage(mContext.getString(R.string.updating));
            }else{
                if(!haveLink(posPressed)){
                    mVideoView.showSnackBarMessage(mContext.getString(R.string.link_not_disponible));
                }else{
                    if(AudioPlayer.isPlaying()){AudioPlayer.pause();}
                    if(VideoPlayer.getSimpleExoPlayer() ==null){
                        initPlayer();
                    }
                    VideoPlayer.mToPlay = posPressed;
                    mVideoAdapter.notifyItemChanged(posPressed);
                    mContext.startActivity(new Intent(mContext, ActivityVideoPlayer.class));
                }
            }
        }
    }

    @Override
    public void putlinksAsyncVideo() {
        processVideoLinks = new AsyncTask[Library.mVideo.size()];
        for(int i=0; i< Library.mVideo.size(); i++ ) {
            final int finalI = i;
            if(Library.mVideo.get(i).getTempLink().isEmpty()){
                processVideoLinks[i] = new GetLinkFile(new GetLinkFile.Callback() {
                    @Override
                    public void onLinkLoaded(String link) {
                        Library.mVideo.get(finalI).setTempLink(link);
                        mVideoAdapter.notifyItemChanged(finalI);
                    }
                    @Override
                    public void onError(Exception e) {

                    }
                }).execute(Library.mVideo.get(finalI).getPath());
            }
        }
    }

    public void download(int pos) {
        if(!Connection.isConnected(mContext))
            mVideoView.showNoConnectionSnackBar();
        else{
            if(Library.mVideo.get(pos).getTempLink().isEmpty()){
                mVideoView.showToastMessage(mContext.getString(R.string.cant_download));
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Library.mVideo.get(pos).getTempLink()));
                mContext.startActivity(browserIntent);
            }
        }
    }

    public void sharelink(final int pos) {
        if (!Connection.isConnected(mContext))
            mVideoView.showNoConnectionSnackBar();
        else {
            if (Library.mVideo.get(pos).getTempLink().isEmpty()) {
                mVideoView.showToastMessage(mContext.getString(R.string.cant_share));
            } else {
                if (Library.mVideo.get(pos).getShareLink().isEmpty()) {
                    mVideoView.showProgressDialog(mContext.getString(R.string.get_shared_link));
                    new GetSharedLink(new GetSharedLink.Callback() {
                        @Override
                        public void onLinkLoaded(String link) {
                            Library.mVideo.get(pos).setShareLink(link);
                            mVideoView.hideProgressDialog();
                            shareElement(link);
                        }

                        @Override
                        public void onError(Exception e) {
                            mVideoView.hideProgressDialog();
                            mVideoView.showSnackBarMessage(mContext.getString(R.string.error_sharing_file));
                        }
                    }).execute(Library.mVideo.get(pos).getPath());
                } else {
                    String toShareFile = Library.mVideo.get(pos).getShareLink();
                    shareElement(toShareFile);
                }
            }
        }
    }

    private void shareElement(String toShare) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, toShare);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void delete(int pos) {
        if(!Connection.isConnected(mContext))
            mVideoView.showNoConnectionSnackBar();
        else{
            if(AudioPlayer.getCurrentPos() == pos && AudioPlayer.isPlaying()){
                mVideoView.showToastMessage(mContext.getString(R.string.cant_delete));
            }else{
                if(Library.mVideo.get(pos).getTempLink().isEmpty()){
                    mVideoView.showToastMessage(mContext.getString(R.string.cant_delete));
                }else{
                    String nameFile, path;
                    nameFile = Library.mVideo.get(pos).getName();
                    path     = Library.mVideo.get(pos).getPath();
                    deleteFileDialog(nameFile, path, pos);
                }
            }
        }
    }

    private void deleteFile(final String fileName, String path, final int pos){
        mVideoView.showProgressDialog(mContext.getString(R.string.deleting));
        new DeleteFile(new DeleteFile.Callback() {
            @Override
            public void onFileDeleted() {
                mVideoView.hideProgressDialog();
                long espMemory = Library.mVideo.get(pos).getSize();
                Session.setUsedSpace(Session.getUsedSpace() - espMemory);
                MainActivity.mMainPresenter.setCapacityAccount();
                Library.mVideo.remove(pos);
                mVideoAdapter.notifyItemRemoved(pos);
                mVideoView.showToastMessage(fileName + " " + mContext.getString(R.string.has_been_deleted));
            }

            @Override
            public void onError(Exception e) {
                mVideoView.hideProgressDialog();
                mVideoView.showToastMessage(mContext.getString(R.string.error_deleting_file) + fileName);
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
}