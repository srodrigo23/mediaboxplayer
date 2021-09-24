package com.umss.rodres.mbp2.player.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class VideoPlayer {

    private static Context                  mContext;
    private static Handler                  mMainHandler;

    private static DefaultBandwidthMeter    mBandwidthMeter;
    private static TrackSelection.Factory   mTrackSelectionFactory;
    private static TrackSelector            mTrackSelector;

    private static SimpleExoPlayer          mExoPlayer;

    private static DefaultDataSourceFactory mDataSourceFactory;
    private static ExtractorsFactory        mExtractorsFactory;

    public static int                       mPosPlaying = -1;
    public static int                       mToPlay     = -1;

    public static void initPlayer(Context context) {
        if (mExoPlayer == null){
            mContext               = context;
            mMainHandler           = new Handler();
            mBandwidthMeter        = new DefaultBandwidthMeter();
            mTrackSelectionFactory = new AdaptiveTrackSelection.Factory(mBandwidthMeter);
            mTrackSelector         = new DefaultTrackSelector(mTrackSelectionFactory);
            mExoPlayer             = ExoPlayerFactory.newSimpleInstance(
                    context,
                    mTrackSelector,
                    new DefaultLoadControl());
            mDataSourceFactory     = new DefaultDataSourceFactory (mContext,
                    Util.getUserAgent(mContext, "mbp"),
                    mBandwidthMeter);
            mExtractorsFactory     = new DefaultExtractorsFactory();
        }
    }

    public static void addMediaSource(String url){
        mExoPlayer.prepare(getMediaSourceFromURL(url));
    }

    public static MediaSource getMediaSourceFromURL(String url) {
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(url),
                mDataSourceFactory,
                mExtractorsFactory,
                mMainHandler, new ExtractorMediaSource.EventListener() {
            @Override
            public void onLoadError(IOException error) {
                Log.e("ERROR!", error.getMessage());
            }
        });
        return videoSource;
    }

    public static SimpleExoPlayer getSimpleExoPlayer(){
        return mExoPlayer;
    }

    public static void play(){
        mExoPlayer.setPlayWhenReady(true);
    }

    public static void pause(){
        mExoPlayer.setPlayWhenReady(false);
    }

    public static void resetPlayer(){
        //mExoPlayer.stop();
        mExoPlayer = null;
        mPosPlaying = -1;
        mToPlay     = -1;
    }
}
