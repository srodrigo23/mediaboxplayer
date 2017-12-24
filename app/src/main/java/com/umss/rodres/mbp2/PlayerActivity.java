package com.umss.rodres.mbp2;

import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;

    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playerView = (SimpleExoPlayerView) findViewById(R.id.player);
    }
    @Override
    public void onResume() {
        super.onResume();
        //if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        //}
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());

            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }

        Uri uri  = Uri.parse("https://dl.dropboxusercontent.com/apitl/1/AAAtq2pScLo35IyZskRmh5caXNinISIelzcSx-KUjj5O7Y1c30lGRDCveg7O7RjNjLV7Orv318YyrwCy1kttCv1eXhgbO8vDgv_Y2u1NDusqP2AsbOPVK3LTo_y867NE1a8Shm4sSr1H_n_1F_pf_O8q8XNEKS3lMRyeayqdinCIryQhn5ZKvu91XcS3hnUp90upSkHKhEHu7NtZA7VD3k77tCxCBK_MEHcgKAVfkoPynpPmmMMA3ssQndOZqY1iPiwZBIVjjcB_ucN3y_HC2Qa5dPzWOsZrnUvKtG93kJ30xQ");

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);

    }

    private MediaSource buildMediaSource(Uri uri){

        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory audioTrackSelectionFac = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(audioTrackSelectionFac);

        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        Handler mainHandler = new Handler();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mbp"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        //Uri mp3 = Uri.parse("https://dl.dropboxusercontent.com/apitl/1/AACFxfdwi5Ojj3Tw1WjYcU6bR4DuCojF-agnXSQXuk66Z97G_aKe3-p6bLakC_W7ErBVHxhomnvNsS4KOgHVbQkfCI_LLHj0Y-yDh2LFzKGkEHceXsZ_0KSsCCyGLV_xFSjQXmmNZKvnxn-PDdFeHLMLokJ49l-srTdRJ9Z1CWckc77CQ0ULcnXO7ZJawW86d4rbn_mygiEN8F-Wl4haZywiENhsaFotNNHTHWtiWOrl_DciAHyTkEkfBDPdUvEOVLhWBuSiWnch_MTA9eZASNZPoLErq-AXdG6Jv7tJyU8Tkg");

        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, mainHandler, null);

        return audioSource;
        //player.prepare(audioSource);
        //player.setPlayWhenReady(true);
    }
}
