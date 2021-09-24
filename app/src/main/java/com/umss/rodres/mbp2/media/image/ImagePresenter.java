package com.umss.rodres.mbp2.media.image;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import com.umss.rodres.mbp2.player.image.ImageActivity;
import com.umss.rodres.mbp2.util.Connection;

import java.util.List;

public class ImagePresenter implements ImageContract.UserActionsListener {

    private ImageAdapter       mImageAdapter;
    private ImageContract.View mImageView;
    private Context            mContext;
    private boolean            isUpdating;

    public ImagePresenter(ImageContract.View imageView) {
        this.mImageView    = imageView;
        this.mImageAdapter = new ImageAdapter(
                PicassoClient.getPicasso(),
                Library.mImage,
                this);
        this.mContext      = mImageView.getContext();
        isUpdating         = false;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void update() {
        isUpdating = true;
        if(Connection.isConnected(mContext)){
            new GetUserInfo(new GetUserInfo.Callback() {
                @Override
                public void onDataLoaded() {
                    MainActivity.mMainPresenter.setCapacityAccount();
                }
                @Override
                public void onError(Exception e){
                    mImageView.showToastMessage(mContext.getString(R.string.error_loading_account_info));
                }
            }).execute();

            new GetAllFiles(new GetAllFiles.Callback() {
                @Override
                public void onDataLoaded(List<FileMetadata> list) {
                    mImageView.setProgressIndicator(false);
                    isUpdating = false;
                    Library.initImageLib(list);
                    Library.mergeList();
                    replaceData(Library.mImage);
                    showHideElements();
                    mImageView.showSnackBarMessage(mContext.getString(R.string.updated_image));
                }
                @Override
                public void onError(Exception e) {
                    mImageView.setProgressIndicator(false);
                    Toast.makeText(mContext, "Error al actualizar los archivos de Audio",
                            Toast.LENGTH_SHORT).show();
                }
            }).execute(mContext.getString(R.string.path_image));
        }else{
            mImageView.setProgressIndicator(false);
            mImageView.showNoConnectionSnackBar();
        }
    }

    @Override
    public void showHideElements(){
        if(Library.mImage.size()==0){
            mImageView.showNoImageFounded();
            mImageView.hideRecyclerView();
        }else{
            mImageView.hideNoImageFounded();
            mImageView.showRecyclerView();
        }
    }


    @Override
    public void singleTouchRecyclerItem(int pos) {
        if(!Connection.isConnected(mContext)){
            mImageView.showNoConnectionSnackBar();
        }else{
            if(isUpdating){
                mImageView.showSnackBarMessage(mContext.getString(R.string.updating));
            }else{
                Intent intent = new Intent(mContext, ImageActivity.class);
                intent.putExtra("posPressed", pos);
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void replaceData(List<File> files) {
        mImageAdapter.replaceData(files);
    }

    @Override
    public ImageAdapter getImageAdapter() {
        return mImageAdapter;
    }

    public void download(final int pos) {
        if(!Connection.isConnected(mContext))
            mImageView.showNoConnectionSnackBar();
        else {
            if(Library.mImage.get(pos).getTempLink().isEmpty()){
                mImageView.showProgressDialog(mContext.getString(R.string.prepare_download));
                new GetLinkFile(new GetLinkFile.Callback() {
                    @Override
                    public void onLinkLoaded(String link) {
                        Library.mImage.get(pos).setTempLink(link);
                        mImageView.hideProgressDialog();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        mContext.startActivity(browserIntent);
                    }
                    @Override
                    public void onError(Exception e) {
                        mImageView.showToastMessage(mContext.getString(R.string.error_prepare_download));
                        mImageView.hideProgressDialog();
                    }
                }).execute(Library.mImage.get(pos).getPath());
            }else{
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Library.mImage.get(pos).getTempLink()));
                mContext.startActivity(browserIntent);
            }
        }
    }

    public void sharelink(final int pos) {
        if(!Connection.isConnected(mContext))
            mImageView.showNoConnectionSnackBar();
        else{
            if(Library.mImage.get(pos).getShareLink().isEmpty()){
                mImageView.showProgressDialog(mContext.getString(R.string.get_shared_link));
                new GetSharedLink(new GetSharedLink.Callback() {
                    @Override
                    public void onLinkLoaded(String link) {
                        Library.mImage.get(pos).setShareLink(link);
                        mImageView.hideProgressDialog();
                        shareElement(link);
                    }
                    @Override
                    public void onError(Exception e) {
                        mImageView.hideProgressDialog();
                        mImageView.showSnackBarMessage(mContext.getString(R.string.error_sharing_file));
                    }
                }).execute(Library.mImage.get(pos).getPath());

            }else{
                String toShareFile = Library.mImage.get(pos).getShareLink();
                shareElement(toShareFile);
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
            mImageView.showNoConnectionSnackBar();
        else{
            String nameFile, path;
            nameFile = Library.mImage.get(pos).getName();
            path     = Library.mImage.get(pos).getPath();
            deleteFileDialog(nameFile, path, pos);
        }
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

    private void deleteFile(final String nameFile, String path, final int pos) {
        mImageView.showProgressDialog(mContext.getString(R.string.deleting));
        new DeleteFile(new DeleteFile.Callback() {
            @Override
            public void onFileDeleted() {
                mImageView.hideProgressDialog();
                long espMemory = Library.mImage.get(pos).getSize();
                Session.setUsedSpace(Session.getUsedSpace() - espMemory);
                MainActivity.mMainPresenter.setCapacityAccount();
                Library.mImage.remove(pos);
                mImageAdapter.notifyItemRemoved(pos);
                mImageView.showToastMessage(nameFile + " " + mContext.getString(R.string.has_been_deleted));
            }

            @Override
            public void onError(Exception e) {
                mImageView.hideProgressDialog();
                mImageView.showToastMessage(mContext.getString(R.string.error_deleting_file) + nameFile);
            }
        }).execute(path);
    }
}