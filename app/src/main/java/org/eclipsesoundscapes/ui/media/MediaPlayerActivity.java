package org.eclipsesoundscapes.ui.media;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.util.MediaHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
 * Audio player class for verbal and text description of an eclipseImageView
 * Also launched during first and second contact of eclipseImageView from notifications for live audio
 * See {@link MediaFragment}
 */

public class MediaPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_IMG = "img_id";
    public static final String EXTRA_AUDIO = "audio_id";
    public static final String EXTRA_LIVE = "is_live";

    private int audioId;
    private boolean liveExperience; // live audio feed during eclipseImageView

    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private MediaHelper utils;

    // views
    @BindView(R.id.eclipse_img) ImageView eclipseImg;
    @BindView(R.id.eclipse_title) TextView eclipseTitle;
    @BindView(R.id.eclipse_description) TextView eclipseDescription;
    @BindView(R.id.back_button) ImageButton backButton;
    @BindView(R.id.play_button) ImageButton playButton;
    @BindView(R.id.audio_progress) SeekBar audioProgressBar;
    @BindView(R.id.time_lapsed) TextView timeLapsed;
    @BindView(R.id.time_total) TextView timeTotal;

    @OnClick(R.id.play_button) void updateMediaStatus(){
        if (mp != null){
            if(mp.isPlaying()){
                mp.pause();
                // play button
                playButton.setImageResource(R.drawable.ic_play);
                playButton.setContentDescription(getString(R.string.playButton));
            } else {
                mp.start();
                // pause button
                playButton.setImageResource(R.drawable.ic_pause);
                playButton.setContentDescription(getString(R.string.pauseButton));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);

        if (getIntent().getExtras() == null) {
            finish();
            return;
        }

        // extras
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        int description = getIntent().getIntExtra(EXTRA_DESCRIPTION, 0);
        int imgId = getIntent().getIntExtra(EXTRA_IMG, 0);
        audioId = getIntent().getIntExtra(EXTRA_AUDIO, 0);
        liveExperience = getIntent().getBooleanExtra(EXTRA_LIVE, false);

        // views
        eclipseTitle.setText(title);
        eclipseDescription.setText(getString(description));
        eclipseImg.setImageResource(imgId);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
        mp = null;
    }

    @Override
    @OnClick(R.id.back_button)
    public void onBackPressed() {
        if (!isLive()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
            finish();
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

    /**************************************************************************************************
     * Media player
     *************************************************************************************************/

    /**
     * Create media player and start audio from provided resource
     * @param audioSrc Raw resource id for audio
     */
    public void playAudio(int audioSrc){
        try {
            mp = MediaPlayer.create(this, audioSrc);
            mp.start();

            audioProgressBar.setOnSeekBarChangeListener(this);
            mp.setOnCompletionListener(this);

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
                total = getString(R.string.duration_desc_seconds, time[1]);
                timeTotal.setContentDescription(total);
            } else if (Integer.valueOf(time[0]) == 1) {
                total = getString(R.string.duration_desc_minute, time[1]);
                timeTotal.setContentDescription(total);
            } else {
                total = getString(R.string.duration_desc_minutes, time[0], time[1]);
                timeTotal.setContentDescription(total);
            }

            // update time lapsed
            timeLapsed.setText(String.valueOf(utils.milliSecondsToTimer(currentDuration)));
            String curr;
            String[] lapse = timeLapsed.getText().toString().split(":");

            if (Integer.valueOf(lapse[0]) == 0) {
                curr = getString(R.string.duration_desc_seconds, lapse[1]);
            } else if (Integer.valueOf(lapse[0]) == 1) {
                curr = getString(R.string.duration_desc_minute, lapse[1]);
            } else {
                curr = getString(R.string.duration_desc_minutes, lapse[0], lapse[1]);
            }

            timeLapsed.setContentDescription(getString(R.string.duration_of_total, curr, total));

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            audioProgressBar.setProgress(progress);
            audioProgressBar.setContentDescription(getString(R.string.duration_of_total, curr, total));

            // only update live event during second contact
            if (isLive() && !eclipseTitle.getText().toString().equals(getString(R.string.first_contact_title)))
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
     * Fast forward/rewind progress handler
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);
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

    /**************************************************************************************************
     * Live
     *************************************************************************************************/

    // disable seek bar, play/pause control and accessibility during live audio of eclipseImageView event
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

    /**
     * Updates UI based on current audio description of eclipseImageView event
     * Currently setup for the eclipseImageView August 21st
     */
    public void updateLiveContent(){
        if (mp.getCurrentPosition() < 120000){ // baily'ss beads < 2:01
            if (!eclipseTitle.getText().toString().equals(getString(R.string.bailys_beads_title)))
                updateDescription(R.string.bailys_beads_title, R.string.bailys_beads_description, R.drawable.eclipse_bailys_beads);

        } else if (mp.getCurrentPosition() >= 120000 && mp.getCurrentPosition() < 200000) { // totality >= 2:01 < 5:21
            if (!eclipseTitle.getText().toString().equals(getString(R.string.totality_title)))
                updateDescription(R.string.totality_title, R.string.totality_description, R.drawable.eclipse_totality);

        } else if (mp.getCurrentPosition() >= 200500 && mp.getCurrentPosition() < 320000){ // diamond ring >= 3:21
            if (!eclipseTitle.getText().toString().equals(getString(R.string.diamond_ring_title)))
                updateDescription(R.string.diamond_ring_title, R.string.diamond_ring_description, R.drawable.eclipse_diamond_ring);

        } else if (mp.getCurrentPosition() >= 320500){ // sun as a star 5:21
            if (!eclipseTitle.getText().toString().equals(getString(R.string.sun_as_star_title)))
                updateDescription(R.string.sun_as_star_title, R.string.sun_as_star_description, R.drawable.sun_as_a_star);
        }
    }

    // resume user control over media player
    public void liveOver(){
        audioProgressBar.setOnTouchListener(null);
        playButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        eclipseTitle.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        eclipseDescription.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        liveExperience = false;
    }

    public boolean isLive(){
        return liveExperience;
    }
}