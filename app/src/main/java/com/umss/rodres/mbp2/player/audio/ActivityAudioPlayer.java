package com.umss.rodres.mbp2.player.audio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.media.music.MusicAdapter;
import com.umss.rodres.mbp2.media.music.MusicPresenter;
import com.umss.rodres.mbp2.model.Library;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class ActivityAudioPlayer extends AppCompatActivity {

    private SimpleExoPlayerView mSimpleExoPlayerView;
    private FrameLayout         mFrameLayout;
    private TextView            mTitleFile;
    private ProgressBar         mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        mSimpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player);
        mFrameLayout         = mSimpleExoPlayerView.findViewById(R.id.layout_player_fullscreen);
        mTitleFile           = mFrameLayout.findViewById(R.id.title_file);
        mProgressBar         = mSimpleExoPlayerView.findViewById(R.id.progressLaunchLink);


        mSimpleExoPlayerView.setPlayer(AudioPlayer.getSimpleExoPlayer());
        addEventListener();
        AudioPlayer.play();
        mProgressBar.setVisibility(View.VISIBLE);
        if(AudioPlayer.getSimpleExoPlayer().getPlaybackState() == Player.STATE_READY)
            mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTitleFile.setSelected(true);
        mTitleFile.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mSimpleExoPlayerView.showController();
        mTitleFile.setText(Library.mAudio.get(AudioPlayer.getCurrentPos()).getName());
    }

    private void addEventListener() {
        mSimpleExoPlayerView.getPlayer().addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) { }

            @Override
            public void onTracksChanged(TrackGroupArray     trackGroups,
                                        TrackSelectionArray trackSelections) { }

            @Override
            public void onLoadingChanged(boolean isLoading) { }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == STATE_READY)
                    mProgressBar.setVisibility(View.GONE);
                else if (playbackState == STATE_BUFFERING)
                    mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) { }

            @Override
            public void onPlayerError(ExoPlaybackException error) { }

            @Override
            public void onPositionDiscontinuity() {
                MusicPresenter.notifychange();
                mTitleFile.setText(Library.mAudio.get(AudioPlayer.getCurrentPos()).getName());
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}