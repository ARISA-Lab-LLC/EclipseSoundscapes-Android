package org.eclipsesoundscapes.util;

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


import static java.lang.Math.max;

import android.content.Context;
import android.media.MediaPlayer;

import org.eclipsesoundscapes.R;

import java.util.concurrent.TimeUnit;

/**
 * @author Joel Goncalves
 *
 * Helper to handle media / audio player progress
 */

public class MediaUtils {

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration current time lapsed
     * @param totalDuration total audio time
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress current time lapsed
     * @param totalDuration total audio time
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    /**
     * Retrieves the duration of a audio file in milliseconds
     * @param context context to access resource
     * @param audioId the resource id of the audio
     * @return the duration in milliseconds or -1 if operation failed
     */
    public static long getAudioDuration(Context context, int audioId) {
        long duration = -1;
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, audioId);
            if (mediaPlayer != null) {
                duration = mediaPlayer.getDuration();
                mediaPlayer.release();
            }
        } catch (Exception exception) {
            //TODO: log error
        }

        return duration;
    }

    public static int getPhaseStartOffset(Context context) {
        long mediaDuration = MediaUtils.getAudioDuration(context, R.raw.annular_eclipse_phase_start_short);
        return (int) TimeUnit.MILLISECONDS.toSeconds(max(0, mediaDuration)) + 30;
    }
}