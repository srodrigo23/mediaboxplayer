package com.umss.rodres.mbp2;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

public class Player {

    private SimpleExoPlayer player;

    private long playbackPosition;
    private boolean playWhenReady;

    public Player(Context c){
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(c),
                                                    new DefaultTrackSelector(),
                                                    new DefaultLoadControl());
        playWhenReady = false;
        playbackPosition = 0;
    }

    public void play(){
        player.setPlayWhenReady(true);
    }

    public  void pause(){
        player.setPlayWhenReady(false);
    }

    private MediaSource buildMediaSource(Uri uri){
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("user-agent");

        ExtractorMediaSource  mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);

        return null;
    }

}
