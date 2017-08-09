package org.eclipsesoundscapes.eclipsesoundscapes.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.util.MediaHelper;

import java.io.IOException;

/**
 * Created by horus on 8/8/17.
 */

public class MediaPlayerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    // bundle
    private String title;
    private int descriptionId;
    private int imgId;
    private int audioId;

    private Context mContext;
    private ImageView eclipseImg;
    private TextView eclipseTitle;
    private TextView eclipseDescription;
    private ImageButton backButton;
    private ImageButton playButton;
    private TextView timeLapsed;
    private TextView timeTotal;
    private SeekBar audioProgressBar;
    private  MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private MediaHelper utils;

    public static MediaPlayerFragment newInstance(String title, int imgId, int audioId, int description ) {
        MediaPlayerFragment f = new MediaPlayerFragment();
        Bundle args = new Bundle();
        args.putInt("img", imgId);
        args.putInt("audio", audioId);
        args.putString("title", title);
        args.putInt("description", description);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo);

        // get extras
        title = getArguments().getString("title");
        descriptionId = getArguments().getInt("description");
        imgId = getArguments().getInt("img");
        audioId = getArguments().getInt("audio");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media_player, container, false);

        // views
        eclipseImg = (ImageView) v.findViewById(R.id.eclipse_img);
        eclipseImg.setImageResource(imgId);
        eclipseTitle = (TextView) v.findViewById(R.id.eclipse_title);
        eclipseTitle.setText(title);
        eclipseDescription = (TextView) v.findViewById(R.id.eclipse_description);
        eclipseDescription.setText(getString(descriptionId));
        backButton = (ImageButton) v.findViewById(R.id.back_button);
        playButton = (ImageButton) v.findViewById(R.id.play_button);
        audioProgressBar = (SeekBar) v.findViewById(R.id.audio_progress);
        timeLapsed = (TextView) v.findViewById(R.id.time_lapsed);
        timeTotal = (TextView) v.findViewById(R.id.time_total);

        // Media player
        mp = new MediaPlayer();
        utils = new MediaHelper();

        // listeners
        audioProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        playAudio(audioId);

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.ic_pause);
                    }
                }

            }
        });
        return v;
    }

    public void playAudio(int audioSrc){
        // Play song
        try {
            mp = MediaPlayer.create(mContext, audioSrc);
            mp.start();

            // Changing Button Image to pause image
            playButton.setImageResource(R.drawable.ic_pause);

            // set Progress bar values
            audioProgressBar.setProgress(0);
            audioProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mp == null)
                return;

            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            timeTotal.setText(""+utils.milliSecondsToTimer(totalDuration));

            // Displaying time completed playing
            timeLapsed.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            audioProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }
}
