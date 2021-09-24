package com.umss.rodres.mbp2.media.image;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.files.MyAdapter;
import com.umss.rodres.mbp2.helper.PicassoClient;
import com.umss.rodres.mbp2.media.music.MusicContract;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.model.Library;
import com.umss.rodres.mbp2.util.GridSpacingItemDecoration;
import com.umss.rodres.mbp2.util.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ImageFragment extends Fragment implements ImageContract.View {

    private ImageContract.UserActionsListener mImagePresenter;
    private SwipeRefreshLayout                mSwipeRefreshImageLayout;
    private RecyclerView                      mRecyclerImageView;

    private FrameLayout                       mFrameLayout;
    private ProgressDialog mDialog;

    public ImageFragment() { }

    public static ImageFragment newInstance(){
        return new ImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePresenter = new ImagePresenter(this);
        mDialog         = new ProgressDialog(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_imag, container, false);
        mSwipeRefreshImageLayout = view.findViewById(R.id.refresh_image_layout);
        mFrameLayout             = view.findViewById(R.id.noImage);
        setRecyclerView(view);
        getResources();
        setSwipeRefreshImageLayout();
        return view;
    }

    @Override
    public void showRecyclerView(){
        mRecyclerImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView(){
        mRecyclerImageView.setVisibility(View.GONE);
    }

    @Override
    public void showNoImageFounded(){
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoImageFounded(){
        mFrameLayout.setVisibility(View.GONE);
    }

    private void setRecyclerView(View view){
        mRecyclerImageView = view.findViewById(R.id.image_list);
        mRecyclerImageView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerImageView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        mRecyclerImageView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerImageView.setAdapter(mImagePresenter.getImageAdapter());

        mImagePresenter.showHideElements();
    }

    private void setSwipeRefreshImageLayout(){
        mSwipeRefreshImageLayout.setEnabled(true);
        mSwipeRefreshImageLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDarkColor),
                ContextCompat.getColor(getContext(), R.color.primaryLightColor),
                ContextCompat.getColor(getContext(), R.color.primaryColor)
        );

        mSwipeRefreshImageLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mImagePresenter.update();
            }
        });
    }

    @Override
    public void showNoConnectionSnackBar() {
        Snackbar.make(
                mSwipeRefreshImageLayout,
                getString(R.string.disconnected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.connect), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED).show();
    }

    @Override
    public void showSnackBarMessage(String message) {
        Snackbar.make(mSwipeRefreshImageLayout, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        mSwipeRefreshImageLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshImageLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setMessage(message);
        mDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        mDialog.dismiss();
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
