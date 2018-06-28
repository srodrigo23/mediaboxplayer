package com.umss.rodres.mbp2.media.music;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.util.MyDividerItemDecoration;

public class MusicFragment extends Fragment implements MusicContract.View {

    private MusicContract.UserActionsListener mMusicPresenter;
    private SwipeRefreshLayout                mSwipeRefreshMusicLayout;
    private RecyclerView                      mRecyclerMusicView;
    private FloatingActionButton              mFloatingPlayButton;
    private ProgressBar                       mProgressBar;
    private ProgressDialog     mDialog;

    private FrameLayout        mFrameLayout;

    public MusicFragment() { }

    public static MusicFragment newInstance(){
        return new MusicFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicPresenter = new MusicPresenter(this);
        mDialog         = new ProgressDialog(getContext());
        mMusicPresenter.initPlayer();
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
        mMusicPresenter.notifyDataSetChanged();
        mMusicPresenter.setFloatingButtonIcon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup      container,
                             Bundle         savedInstanceState) {
        View view                = inflater.inflate(R.layout.fragment_music, container, false);
        mSwipeRefreshMusicLayout = view.findViewById(R.id.refresh_music_layout);
        mProgressBar             = view.findViewById(R.id.progres_music_launch);
        mFrameLayout             = view.findViewById(R.id.noMusic);
        setRecyclerView(view);
        setSwipeRefreshLayout();
        setFloatingButton(view);
        return view;
    }

    @Override
    public void showRecyclerView(){
        mRecyclerMusicView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecyclerView(){
        mRecyclerMusicView.setVisibility(View.GONE);
    }

    @Override
    public void showNoImageFounded(){
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoImageFounded(){
        mFrameLayout.setVisibility(View.GONE);
    }

    private void setRecyclerView(View v){
        mRecyclerMusicView = v.findViewById(R.id.music_list);
        mRecyclerMusicView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerMusicView.addItemDecoration(
                new MyDividerItemDecoration(getContext(),
                        DividerItemDecoration.VERTICAL, 36));
        mRecyclerMusicView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerMusicView.setAdapter(mMusicPresenter.getMusicAdapter());
        mMusicPresenter.showHideElements();
    }

    private void setFloatingButton(View v){
        mFloatingPlayButton = v.findViewById(R.id.playerPauseButton);
        mFloatingPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicPresenter.play();
            }
        });
    }

    private void setSwipeRefreshLayout(){
        mSwipeRefreshMusicLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.primaryDarkColor),
                ContextCompat.getColor(getContext(), R.color.primaryLightColor),
                ContextCompat.getColor(getContext(), R.color.primaryColor)
        );
        mSwipeRefreshMusicLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setProgressIndicator(true);
                mMusicPresenter.update();
            }
        });
    }

    @Override
    public void switchIcon(int idResource) {
        mFloatingPlayButton.setImageResource(idResource);
    }

    @Override
    public void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }

    public MusicPresenter getPresenter(){
        return (MusicPresenter) mMusicPresenter;
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

    @Override
    public void setProgressIndicator(final boolean active) {
        mSwipeRefreshMusicLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshMusicLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showSnackBarMessage(String message) {
        Snackbar.make(mFloatingPlayButton, message, Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void showNoConnectionSnackBar() {
        Snackbar.make(
                mFloatingPlayButton,
                getString(R.string.disconnected), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.connect), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                    }
                }).setActionTextColor(Color.RED).show();
    }
}