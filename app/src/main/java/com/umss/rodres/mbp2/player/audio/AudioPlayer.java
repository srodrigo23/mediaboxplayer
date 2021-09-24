package com.umss.rodres.mbp2.player.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.media.music.MusicPresenter;
import com.umss.rodres.mbp2.model.Library;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class AudioPlayer {

    private static Context                  mContext;
    private static Handler                  mMainHandler;

    private static DefaultBandwidthMeter    mBandwidthMeter;
    private static TrackSelection.Factory   mTrackSelectionFactory;
    private static TrackSelector            mTrackSelector;

    private static SimpleExoPlayer          mExoPlayer;

    private static DefaultDataSourceFactory mDataSourceFactory;
    private static ExtractorsFactory        mExtractorsFactory;
    private static boolean                  mIsPrepared;

    private static DynamicConcatenatingMediaSource mPlaylist;

    private static PlaybackStateCompat.Builder mStateBuilder;
    private static NotificationManager mNotificationManager;

    private static MediaSessionCompat mMediaSession;
    private static MediaSessionCompat.Token token;
    private static final String CHANNEL_ID = "media_playback_channel";

    /**
     * Inicializa el objeto que permite la reproduccion de los archivos de audio
     * @param context
     */
    public static void initPlayer(Context context) {
        if (mExoPlayer == null){
            mContext               = context;
            mBandwidthMeter        = new DefaultBandwidthMeter();
            mTrackSelectionFactory = new AdaptiveTrackSelection.Factory(mBandwidthMeter);
            mTrackSelector         = new DefaultTrackSelector(mTrackSelectionFactory);
            mExoPlayer             = ExoPlayerFactory.newSimpleInstance(context, mTrackSelector,
                                     new DefaultLoadControl());
            mPlaylist              = new DynamicConcatenatingMediaSource();
            mIsPrepared            = false;
        }
        setMediaSession();
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == STATE_READY & playWhenReady){
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer.getCurrentPosition(), 1f);
                    MusicPresenter.hideProgess();
                } else if((playbackState == ExoPlayer.STATE_READY)){
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer.getCurrentPosition(), 1f);
                } else if (playbackState == STATE_BUFFERING)
                    MusicPresenter.showProgess();

                mMediaSession.setPlaybackState(mStateBuilder.build());
                showNotification(mStateBuilder.build());
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                //showErrorMessage(error.getSourceException().getMessage());
            }

            @Override
            public void onPositionDiscontinuity() {
                MusicPresenter.notifychange();
                mMediaSession.setPlaybackState(mStateBuilder.build());
                showNotification(mStateBuilder.build());
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    private static void showErrorMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private static void setMediaSession(){
        mMediaSession = new MediaSessionCompat(mContext, mContext.getClass().getSimpleName());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                               MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallBack());
        mMediaSession.setActive(true);
    }

    /**
     *
     * @param url
     */
    public static void addMediaSource(int pos, String url){
        mPlaylist.addMediaSource(pos, getMediaSourceFromURL(url));
    }

    /**
     * Metodo que devuelve un objeto MediaSource que representa el objeto a reproducir
     * @param url
     * @return MediaSource
     */
    private static MediaSource getMediaSourceFromURL(String url) {
        mDataSourceFactory      = new DefaultDataSourceFactory (mContext,Util.getUserAgent(mContext,
                "mbp"), mBandwidthMeter);
        mExtractorsFactory      = new DefaultExtractorsFactory();
        mMainHandler            = new Handler();

        MediaSource audioSource = new ExtractorMediaSource(
                Uri.parse(url),
                mDataSourceFactory,
                mExtractorsFactory,
                mMainHandler, null);
        return audioSource;
    }

    public static SimpleExoPlayer getSimpleExoPlayer(){
        return mExoPlayer;
    }

    /**
     *   Devuelve la posicicion actual de reproduccion en la PlayList
     */
    public static int getCurrentPos(){
        return mExoPlayer.getCurrentWindowIndex();
    }

    public static void seekTo(int pos){
        mExoPlayer.seekTo(pos, 0);
    }

    /**
     * Se pone en reproduccion la reproduccion
     */
    public static void play(){
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * Se interrumpe la reproduccion
     */
    public static void pause(){ mExoPlayer.setPlayWhenReady(false);}

    /**
     * Estado de reproduccion
     * @return
     */
    public static boolean isPlaying(){return mExoPlayer.getPlayWhenReady() && mIsPrepared;}

    public static void prepare(){
        if (!mIsPrepared){
            mIsPrepared = true;
            mExoPlayer.prepare(mPlaylist);
        }
    }
    /**
     * Estado de reproduccion
     * @return
     */
    public static boolean isReady(){return mExoPlayer.getPlaybackState() == Player.STATE_READY;}

    public static void resetPlayer(){
        if(mNotificationManager != null){
            mNotificationManager.cancelAll();
        }
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    public static void removeMediaSource(int pos){
        mPlaylist.removeMediaSource(pos);
    }

    public static int getAmountMediaSource(){
        return mPlaylist.getSize();
    }


    private static class MySessionCallBack extends MediaSessionCompat.Callback{
        @Override
        public void onPlay() {
            play();
        }

        @Override
        public void onPause() {
            pause();
        }

        @Override
        public void onSkipToPrevious() {
            int pos = getCurrentPos() - 1;
            if(pos > 0){
                mExoPlayer.seekTo(pos);
            }
        }

        @Override
        public void onSkipToNext() {
            int pos = getCurrentPos() + 1;
            if(pos < Library.mAudio.size()){
                mExoPlayer.seekTo(pos, 0);
            }
        }
    }


    private static void showNotification(PlaybackStateCompat state){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);

        int icon;
        String play_pause;

        if(state.getState() == PlaybackStateCompat.STATE_PLAYING){
            icon = R.drawable.exo_controls_pause;
            play_pause = "Pause";
        }else{
            icon = R.drawable.exo_controls_play;
            play_pause = "Play";
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action backAction = new NotificationCompat.Action(
                R.drawable.exo_controls_previous, "back",
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        NotificationCompat.Action nextAction = new NotificationCompat.Action(
                R.drawable.exo_controls_next, "next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (mContext, 0, new Intent(mContext, ActivityAudioPlayer.class), 0);

        token = mMediaSession.getSessionToken();

        builder.setContentTitle(mContext.getString(R.string.app_name))
                //.setContentText(mContext.getString(R.string.playing))
                .setContentText(Library.mAudio.get(getCurrentPos()).getName())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_album)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(backAction)
                .addAction(playPauseAction)
                .addAction(nextAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(token)
                        .setShowActionsInCompactView(0, 1));

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() { }
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /**
     * The NotificationCompat class does not create a channel for you. You still have to create a channel yourself
     */

    @RequiresApi(Build.VERSION_CODES.O)
    private static void createChannel() {
        NotificationManager mNotificationManager = (NotificationManager)
                mContext.getSystemService(NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = "Media playback";
        // The user-visible description of the channel.
        String description = "Media playback controls";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mNotificationManager.createNotificationChannel(mChannel);
    }

}