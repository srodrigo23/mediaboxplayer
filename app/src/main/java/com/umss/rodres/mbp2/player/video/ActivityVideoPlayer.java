package com.umss.rodres.mbp2.player.video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.model.Library;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class ActivityVideoPlayer extends AppCompatActivity{

    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ProgressBar         mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mSimpleExoPlayerView = findViewById(R.id.videoPlayer);
        mProgressBar         = findViewById(R.id.progressLaunch);

        mSimpleExoPlayerView.setPlayer(VideoPlayer.getSimpleExoPlayer());
        addEventListener();
    }

    private void addEventListener(){
        mSimpleExoPlayerView.getPlayer().addListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline,
//                                          Object manifest) {}

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {}

            @Override
            public void onLoadingChanged(boolean isLoading) {
                    /*
                        isLoading ---> true  : esta cargando en buffer
                                  ---> false : se acabo de cargar completamente en buffer
                     */
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady,
                                             int     playbackState) {

                if(playbackState == STATE_READY)
                    mProgressBar.setVisibility(View.GONE);
                else if (playbackState == STATE_BUFFERING)
                    mProgressBar.setVisibility(View.VISIBLE);
                else if (playbackState == STATE_ENDED){
                    VideoPlayer.resetPlayer();
                    finish();
                }else if(playbackState == STATE_IDLE){
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {}

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                showMessageError(error.getSourceException().toString());
            }

//            @Override
//            public void onPositionDiscontinuity() { }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VideoPlayer.pause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(VideoPlayer.mPosPlaying == VideoPlayer.mToPlay){
            VideoPlayer.play();
        }else{
            VideoPlayer.mPosPlaying = VideoPlayer.mToPlay;
            VideoPlayer.addMediaSource(Library.mVideo.get(VideoPlayer.mPosPlaying).getTempLink());
            VideoPlayer.play();
        }
    }

    private void showMessageError(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}