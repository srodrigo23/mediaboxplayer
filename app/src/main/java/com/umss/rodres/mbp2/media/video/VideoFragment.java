package com.umss.rodres.mbp2.media.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.util.MyDividerItemDecoration;

public class VideoFragment extends Fragment implements VideoContract.View {

    private VideoContract.UserActionsListener mVideoPresenter;
    private SwipeRefreshLayout                mSwipeRefreshVideoLayout;
    private RecyclerView                      mRecyclerVideoView;
    private ProgressDialog     mDialog;

    private FrameLayout        mFrameLayout;
    public VideoFragment() { }

    public static VideoFragment newInstance(){
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVideoPresenter     = new VideoPresenter(this);
        mDialog         = new ProgressDialog(getContext());
        mVideoPresenter.initPlayer();
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
    public void onResume() {
        super.onResume();
        mVideoPresenter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mSwipeRefreshVideoLayout = view.findViewById(R.id.refresh_video_layout);
        mFrameLayout             = view.findViewById(R.id.noVideo);
        setRecyclerVideoView(view);
        setSwipeRefreshImageLayout(view);
        return view;
    }

    @Override
    public void showRecyclerView(){
        mRecyclerVideoView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView(){
        mRecyclerVideoView.setVisibility(View.GONE);
    }

    @Override
    public void showNoImageFounded(){
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoImageFounded(){
        mFrameLayout.setVisibility(View.GONE);
    }

    private void setRecyclerVideoView(View view){
        mRecyclerVideoView = view.findViewById(R.id.video_list);
        mRecyclerVideoView.setHasFixedSize(true);
        mRecyclerVideoView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerVideoView.addItemDecoration(
                new MyDividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL, 36));
        mRecyclerVideoView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerVideoView.setAdapter(mVideoPresenter.getVideoAdapter());
        mVideoPresenter.showHideElements();
    }

    private void setSwipeRefreshImageLayout(View view) {
        mSwipeRefreshVideoLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDarkColor),
                ContextCompat.getColor(getContext(), R.color.primaryLightColor),
                ContextCompat.getColor(getContext(), R.color.primaryColor)
        );
        mSwipeRefreshVideoLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setProgressIndicator(true);
                mVideoPresenter.update();
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

    public VideoPresenter getPresenter(){
        return (VideoPresenter) mVideoPresenter;
    }

    @Override
    public void showSnackBarMessage(String message) {
        Snackbar.make(mSwipeRefreshVideoLayout, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void showNoConnectionSnackBar() {
        Snackbar.make(
                mSwipeRefreshVideoLayout,
                getString(R.string.disconnected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.connect), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED).show();
    }

    @Override
    public void setProgressIndicator(final boolean active) {
        mSwipeRefreshVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshVideoLayout.setRefreshing(active);
            }
        });
    }
}