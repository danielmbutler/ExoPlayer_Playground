package com.dbtechprojects.exoplayerplayground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Player.Listener {
    private static final String TAG = "MainActivity";
    SimpleExoPlayer player;
    PlayerView playerView;
    ProgressBar progressBar;
    TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        titleTv = findViewById(R.id.title);


        setupPlayer();
        addMP3();
        addMP4Files();


        Log.d(TAG, "onCreate: savedInstance" + savedInstanceState);
        // restore playstate on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                int restoredMediaItem = savedInstanceState.getInt("mediaItem");
                Long seekTime  = savedInstanceState.getLong("SeekTime");
                player.seekTo(restoredMediaItem, seekTime);
                player.play();
            }

        }
    }

    private void addMP4Files() {
        MediaItem mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4));
        MediaItem mediaItem2 = MediaItem.fromUri(getString(R.string.myTestMp4));


        List<MediaItem> newItems = ImmutableList.of(
                mediaItem,
                mediaItem2);

        player.addMediaItems(newItems);
        player.prepare();
    }

    private void setupPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.video_view);
        playerView.setPlayer(player);
        player.addListener(this);
    }

    private void addMP3() {
        // Build the media item.

        MediaItem mediaItem = MediaItem.fromUri(getString(R.string.test_mp3));
        player.setMediaItem(mediaItem);
        // Set the media item to be played.
        player.setMediaItem(mediaItem);
        // Prepare the player.
        player.prepare();

    }

    // release resources

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            player.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            player.release();
        }
    }


    // handle loading
    @Override
    public void onPlaybackStateChanged(int state) {
        switch (state) {
            case Player.STATE_BUFFERING: {
                Log.d(TAG, "onPlaybackStateChanged: buffering");
                // show progrees bar
                progressBar.setVisibility(View.VISIBLE);
            }
            case Player.STATE_READY: {
                Log.d(TAG, "onPlaybackStateChanged: ready");
            }
        }
    }
    //get Title from metadata
    @Override
    public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
        if (mediaMetadata.title != null) {
            titleTv.setText(mediaMetadata.title.toString());

        }
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        // hide progress bar
        progressBar.setVisibility(View.INVISIBLE);
    }

    // save details if Activity is destroyed
    @Override
    protected void onSaveInstanceState(@NonNull @org.jetbrains.annotations.NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState: " + player.getCurrentPosition());
        // current play position
        outState.putLong("SeekTime", player.getCurrentPosition());
        // current mediaItem
        outState.putInt("mediaItem", player.getCurrentWindowIndex());


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onSaveInstanceState: " + player.getCurrentPosition());
    }
}