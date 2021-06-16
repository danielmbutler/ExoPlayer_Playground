package com.dbtechprojects.exoplayerplayground;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class PlayerFragment extends Fragment implements Player.Listener, View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_CODE = 1;
    SimpleExoPlayer player;
    PlayerView playerView;
    ProgressBar progressBar;
    TextView titleTv;
    FloatingActionButton navFab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findIds(view);
        setupPlayer(view);
        addMP3();
        addMP4Files();


        Log.d(TAG, "onCreate: savedInstance" + savedInstanceState);
        // restore playState on Rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("mediaItem") != 0) {
                int restoredMediaItem = savedInstanceState.getInt("mediaItem");
                long seekTime  = savedInstanceState.getLong("SeekTime");
                player.seekTo(restoredMediaItem, seekTime);
                player.play();
            }

        }

    }


    private void findIds(final View view) {
        progressBar = view.findViewById(R.id.progressBar);
        titleTv = view.findViewById(R.id.title);
        navFab = view.findViewById(R.id.fab);
        navFab.setOnClickListener(this);
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

    private void setupPlayer(final View view) {
        player = new SimpleExoPlayer.Builder(requireContext()).build();
        playerView = view.findViewById(R.id.video_view);
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
                // show progress bar
                progressBar.setVisibility(View.VISIBLE);
            }
            case Player.STATE_READY: {
                // hide progress bar
                progressBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onPlaybackStateChanged: ready");
            }
            case Player.STATE_ENDED:
            case Player.STATE_IDLE:
                break;
        }
    }
    //get Title from metadata
    @Override
    public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
        if (mediaMetadata.title != null) {
            titleTv.setText(mediaMetadata.title.toString());

        }
    }



    // save details if Activity is destroyed

    @Override
    public void onSaveInstanceState(@NonNull @org.jetbrains.annotations.NotNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState: " + player.getCurrentPosition());
        // current play position
        outState.putLong("SeekTime", player.getCurrentPosition());
        // current mediaItem
        outState.putInt("mediaItem", player.getCurrentWindowIndex());


    }



    @Override
    public void onClick(View v) {
        if (v.getId() == navFab.getId()) {
            // navigate to scan fragment
            Log.d(TAG, "onClick: called");
            Fragment mFragment;
            mFragment = new ScanFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(this)
                    .add(R.id.container, mFragment).commit();
        }
    }
}