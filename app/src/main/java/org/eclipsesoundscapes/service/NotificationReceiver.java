package org.eclipsesoundscapes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.data.DataManager;
import org.eclipsesoundscapes.data.SharedPrefsHelper;
import org.eclipsesoundscapes.model.MediaItem;
import org.eclipsesoundscapes.ui.main.MainActivity;
import org.eclipsesoundscapes.ui.media.MediaPlayerActivity;

import static org.eclipsesoundscapes.service.NotificationScheduler.ECLIPSE_FIRST_CONTACT;
import static org.eclipsesoundscapes.service.NotificationScheduler.ECLIPSE_LAUNCH_MEDIA;
import static org.eclipsesoundscapes.service.NotificationScheduler.ECLIPSE_NOTIFICATION_TYPE;
import static org.eclipsesoundscapes.service.NotificationScheduler.showNotification;

/*
 * Displays any scheduled notification or re-schedules them after system reboot
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) return;

        final boolean isLiveEnabled = context.getResources().getBoolean(R.bool.live_experience_enabled);
        if (!isLiveEnabled) {
            return;
        }

        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(context);
            DataManager dataManager = new DataManager(sharedPrefsHelper);

            // Reset reminders.
            NotificationScheduler.scheduleNotifications(context,
                    dataManager.firstContactDate(),
                    dataManager.totalityDate());
            return;
        }

        // Trigger notification
        if (intent.hasExtra(ECLIPSE_NOTIFICATION_TYPE)) {
            final String type = intent.getStringExtra(ECLIPSE_NOTIFICATION_TYPE);
            final boolean launchMedia = intent.getBooleanExtra(ECLIPSE_LAUNCH_MEDIA, false);
            final boolean isFirstContact = type.equals(ECLIPSE_FIRST_CONTACT);

            // reminder
            if (!launchMedia){
                final String description = isFirstContact ?
                        context.getString(R.string.first_contact_reminder) : context.getString(R.string.totality_reminder);
                Intent mediaIntent = new Intent(context, MainActivity.class);
                showNotification(context, MainActivity.class, type, false,
                        context.getString(R.string.app_name), description, mediaIntent);
                return;
            }

            // launch media player on notification press
            Intent mediaIntent = new Intent(context, MediaPlayerActivity.class);
            mediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            if (isFirstContact){
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_MEDIA, new MediaItem(R.drawable.eclipse_first_contact, R.string.first_contact, R.string.audio_first_contact_full, R.raw.first_contact_full));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_LIVE, true);
                showNotification(context, MediaPlayerActivity.class, type, true,
                        context.getString(R.string.first_contact_begun), context.getString(R.string.tap_listen), mediaIntent);
            } else {
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_MEDIA, new MediaItem(R.drawable.eclipse_bailys_beads, R.string.bailys_beads, R.string.bailys_beads_short, R.raw.realtime_eclipse_shorts_saas));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_LIVE, true);
                showNotification(context, MediaPlayerActivity.class, type, true,
                        context.getString(R.string.totality_begun), context.getString(R.string.tap_listen), mediaIntent);
            }
        }
    }
}