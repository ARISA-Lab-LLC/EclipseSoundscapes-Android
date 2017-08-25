package org.eclipsesoundscapes.activity;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.util.MediaHelper;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */


/**
 * @author Joel Goncalves
 *
 * Audio player class for verbal and text description of an eclipse
 * Also launched during first and second contact of eclipse from notifications for live audio
 * See {@link org.eclipsesoundscapes.fragments.MediaFragment}
 * See also {@link org.eclipsesoundscapes.service.WakefulReceiver}
 */

public class MediaPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    // bundle
    private String title;
    private int descriptionId;
    private int imgId;
    private int audioId;
    private boolean liveExperience; // live audio feed during eclipse

    // views
    private ImageView eclipseImg;
    private TextView eclipseTitle;
    private TextView eclipseDescription;
    private ImageButton backButton;
    private ImageButton playButton;
    private TextView timeLapsed;
    private TextView timeTotal;
    private SeekBar audioProgressBar;
    private MediaPlayer mp;

    // Handler to update UI timer, progress bar etc..
    private Handler mHandler = new Handler();
    private MediaHelper utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        // get intent extras
        title = getIntent().getStringExtra("title");
        descriptionId = getIntent().getIntExtra("description", 0);
        imgId = getIntent().getIntExtra("img", 0);
        audioId = getIntent().getIntExtra("audio", 0);
        liveExperience = getIntent().getBooleanExtra("live", false);

        // views
        eclipseImg = (ImageView) findViewById(R.id.eclipse_img);
        eclipseImg.setImageResource(imgId);
        eclipseTitle = (TextView) findViewById(R.id.eclipse_title);
        eclipseTitle.setText(title);
        eclipseDescription = (TextView) findViewById(R.id.eclipse_description);
        eclipseDescription.setText(getString(descriptionId));
        backButton = (ImageButton) findViewById(R.id.back_button);
        playButton = (ImageButton) findViewById(R.id.play_button);
        audioProgressBar = (SeekBar) findViewById(R.id.audio_progress);
        timeLapsed = (TextView) findViewById(R.id.time_lapsed);
        timeTotal = (TextView) findViewById(R.id.time_total);

        // setup live audio UI
        if (isLive())
            setupLiveAudio();

        // Media player
        mp = new MediaPlayer();
        utils = new MediaHelper();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // allow time for accessibility to read label before playing audio
                playAudio(audioId);

            }
        }, 800);

        // listeners
        backButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
    }

    // disable seek bar, play/pause control and accessibility during live audio of eclipse event
    public void setupLiveAudio(){
        audioProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        playButton.setVisibility(View.GONE);
        eclipseTitle.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        eclipseDescription.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    // resume user control over media player
    public void liveOver(){
        audioProgressBar.setOnTouchListener(null);
        playButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        eclipseTitle.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        eclipseDescription.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public boolean isLive(){
        return liveExperience;
    }

    /**
     * Create media player and start audio from provided resource
     * @param audioSrc Raw resource id for audio
     */
    public void playAudio(int audioSrc){
        try {
            mp = MediaPlayer.create(this, audioSrc);
            mp.start();

            audioProgressBar.setOnSeekBarChangeListener(this);
            mp.setOnCompletionListener(this); // Important

            // Changing Button Image to pause image
            playButton.setImageResource(R.drawable.ic_pause);
            playButton.setContentDescription(getString(R.string.pauseButton));

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
     */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mp == null)
                return;

            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // total time of audio
            timeTotal.setText(String.valueOf(utils.milliSecondsToTimer(totalDuration)));
            String total;
            String[] time = timeTotal.getText().toString().split(":");
            if (Integer.valueOf(time[0]) == 0) {
                total = String.valueOf(Integer.valueOf(time[1])).concat(" seconds");
                timeTotal.setContentDescription(total);
            }else if (Integer.valueOf(time[0]) == 1) {
                total = "One minute, ".concat(String.valueOf(Integer.valueOf(time[1])).concat(" seconds"));
                timeTotal.setContentDescription(total);
            }else {
                total = String.valueOf(Integer.valueOf(time[0])).concat(" minutes, ").concat(String.valueOf(Integer.valueOf(time[1]))).concat(" seconds");
                timeTotal.setContentDescription(total);
            }

            // update time lapsed
            timeLapsed.setText(String.valueOf(utils.milliSecondsToTimer(currentDuration)));
            String curr;
            String[] lapse = timeLapsed.getText().toString().split(":");
            if (Integer.valueOf(lapse[0]) == 0) {
                curr = String.valueOf(Integer.valueOf(lapse[1])).concat("seconds");
                timeLapsed.setContentDescription(curr.concat(" of ").concat(total));
            }else if (Integer.valueOf(lapse[0]) == 1) {
                curr = "One minute, ".concat(String.valueOf(Integer.valueOf(lapse[1]))).concat(" seconds");
                timeLapsed.setContentDescription(curr.concat(" of ").concat(total));
            }else {
                curr = String.valueOf(Integer.valueOf(lapse[0])).concat(" minutes, ").concat(String.valueOf(Integer.valueOf(lapse[1]))).concat(" seconds");
                timeLapsed.setContentDescription(curr.concat(" of ").concat(total));
            }

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            audioProgressBar.setProgress(progress);
            audioProgressBar.setContentDescription(curr.concat(" of ".concat(total)));

            // only update live event during second contact
            if (isLive() && !eclipseTitle.getText().toString().equals("First Contact"))
                updateLiveContent();

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
     * Updates UI based on current audio description of eclipse event
     * Currently setup for the eclipse August 21st
     */
    public void updateLiveContent(){
        if (mp.getCurrentPosition() < 120000){ // baily'ss beads < 2:01
            if (!eclipseTitle.getText().toString().equals("Baily's Beads"))
                updateDescription(R.string.bailys_beads_title, R.string.bailys_beads_description, R.drawable.eclipse_bailys_beads);

        } else if (mp.getCurrentPosition() >= 120000 && mp.getCurrentPosition() < 200000) { // totality >= 2:01 < 5:21
            if (!eclipseTitle.getText().toString().equals("Totality"))
                updateDescription(R.string.totality_title, R.string.totality_description, R.drawable.eclipse_totality);

        } else if (mp.getCurrentPosition() >= 200500 && mp.getCurrentPosition() < 320000){ // diamond ring >= 3:21
            if (!eclipseTitle.getText().toString().equals("Diamond Ring"))
                updateDescription(R.string.diamond_ring_title, R.string.diamond_ring_description, R.drawable.eclipse_diamond_ring);

        } else if (mp.getCurrentPosition() >= 320500){ // sun as a star 5:21
            if (!eclipseTitle.getText().toString().equals("Sun as a Star"))
                updateDescription(R.string.sun_as_star_title, R.string.sun_as_star_description, R.drawable.sun_as_a_star);
        }
    }

    /**
     * Update title, description and image based on audio timing
     * @param titleId String resource id for title
     * @param descriptionId String resource id for description
     * @param imgId Drawable resource id for image
     */
    public void updateDescription(int titleId, int descriptionId, int imgId){
        eclipseTitle.setText(getString(titleId));
        eclipseDescription.setText(getString(descriptionId));
        eclipseImg.setImageResource(imgId);
    }

    /**
     * Fast forward/rewind progress handler
     */
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
        if (isLive()) {
            liveOver();
        }
        playButton.setImageResource(R.drawable.ic_play);
        playButton.setContentDescription(getString(R.string.playButton));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (mp != null) {
            if (mp.isPlaying() && eclipseDescription.isAccessibilityFocused()) {
                mp.pause();
                playButton.setImageResource(R.drawable.ic_play);
                playButton.setContentDescription(getString(R.string.playButton));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_button:
                finish();
                break;
            case R.id.play_button:
                updateMediaStatus();
                break;
        }
    }

    // player or pause audio
    public void updateMediaStatus(){
        // check for already playing
        if (mp != null){
            if(mp.isPlaying()){
                mp.pause();
                // Changing button image to play button
                playButton.setImageResource(R.drawable.ic_play);
                playButton.setContentDescription(getString(R.string.playButton));
            } else {
                // Resume song
                mp.start();
                // Changing button image to pause button
                playButton.setImageResource(R.drawable.ic_pause);
                playButton.setContentDescription(getString(R.string.pauseButton));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }

    @Override
    public void onBackPressed() {
        if (!isLive()) {
            super.onBackPressed();
            finish();
        }
    }
}