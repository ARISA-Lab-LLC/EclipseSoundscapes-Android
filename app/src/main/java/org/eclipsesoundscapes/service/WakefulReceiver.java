package org.eclipsesoundscapes.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.eclipsesoundscapes.activity.MediaPlayerActivity;
import org.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.util.Constants;

import java.util.Calendar;
import java.util.Date;

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
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then posts the notification for first and second contact.
 */

public class WakefulReceiver extends WakefulBroadcastReceiver {
    // provides access to the system alarm services.
    private AlarmManager mAlarmManager;

    public void onReceive(Context context, Intent intent) {
        boolean firstContact = intent.getBooleanExtra("first_contact", false);
        String type = intent.getStringExtra("type");
        boolean totality = intent.getBooleanExtra("totality", false);

        Intent mediaIntent = null;
        if (type.equals("first_contact")) {
            if (firstContact) {
                mediaIntent = new Intent(context, MediaPlayerActivity.class);
                mediaIntent.putExtra("title", "First Contact");
                mediaIntent.putExtra("img", org.eclipsesoundscapes.R.drawable.eclipse_first_contact);
                mediaIntent.putExtra("audio", org.eclipsesoundscapes.R.raw.first_contact_short);
                mediaIntent.putExtra("description", org.eclipsesoundscapes.R.string.first_contact_description);
                mediaIntent.putExtra("live", true);
                mediaIntent.putExtra("firstContact", true);
                mediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);

                createNotification(context, "Eclipse Soundscapes", "The Solar Eclipse has begun! Press to listen now", mediaIntent);
            } else {
                mediaIntent = new Intent(context, MainActivity.class);
                createNotification(context, "Eclipse Soundscapes", "The Solar Eclipse is going to begin soon", mediaIntent);
            }
        } else if (type.equals("totality")){
            if (totality){
                mediaIntent = new Intent(context, MediaPlayerActivity.class);
                mediaIntent.putExtra("title", "Baily's Beads");
                mediaIntent.putExtra("img", org.eclipsesoundscapes.R.drawable.eclipse_bailys_beads);
                mediaIntent.putExtra("audio", org.eclipsesoundscapes.R.raw.realtime_eclipse_shorts_saas);
                mediaIntent.putExtra("description", org.eclipsesoundscapes.R.string.bailys_beads_description);
                mediaIntent.putExtra("live", true);
                mediaIntent.putExtra("firstContact", false);
                mediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);

                createNotification(context, "Eclipse Soundscapes", "The Total Solar Eclipse has begun! Press to listen now", mediaIntent);
            } else {
                mediaIntent = new Intent(context, MainActivity.class);
                createNotification(context, "Eclipse Soundscapes", "The Total Solar Eclipse is going to begin soon", mediaIntent);
            }

        }

        WakefulReceiver.completeWakefulIntent(intent);
    }

    /**
     * Create a notification that will launch the media player
     * @see MediaPlayerActivity
     * @param context Activitys context
     * @param title Notification title
     * @param content Notification description
     * @param mediaIntent intent to launch media player with required extras
     */
    public void createNotification(Context context, String title, String content, Intent mediaIntent){
        long[] pattern = {0, 300, 0};
        PendingIntent pi = PendingIntent.getActivity(context, 0, mediaIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(org.eclipsesoundscapes.R.drawable.ic_logo_white)
                .setContentTitle(title)
                .setContentText(content)
                .setVibrate(pattern)
                .setAutoCancel(true);

        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(47, mBuilder.build());
    }

    /**
     * Sets the first contact alarm to run. When the alarm fires,
     * the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setFirstContactAlarm(Context context, Calendar calendar) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentPrimary = new Intent(context, WakefulReceiver.class);
        intentPrimary.putExtra("type", "first_contact");
        intentPrimary.putExtra("first_contact", false);
        Intent intentSecondary = new Intent(context, WakefulReceiver.class);
        intentSecondary.putExtra("type", "first_contact");
        intentSecondary.putExtra("first_contact", true); // launches media player

        PendingIntent alarmIntentPrimary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_FIRST_CONTACT_PRIMARY, intentPrimary, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent alarmIntentSecondary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_FIRST_CONTACT_SECONDARY, intentSecondary, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar current = Calendar.getInstance();
        Calendar calendarPrimary = (Calendar) calendar.clone();
        Calendar calendarSecondary = (Calendar) calendar.clone();
        calendarPrimary.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 2); // 4 min before first contact
        calendarSecondary.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 10); // 10 sec before


        if (current.before(calendarPrimary)) {
            Date datePrimary = calendarPrimary.getTime();
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, datePrimary.getTime(), alarmIntentPrimary);
        }

        if (current.before(calendarSecondary)) {
            Date dateSecondary = calendarSecondary.getTime();
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, dateSecondary.getTime(), alarmIntentSecondary);
        }

        // Enable {@code BootReceiver} to automatically restart when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels first contact alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity
     */
    public void cancelFirstContactAlarm(Context context) {

        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);
        PendingIntent alarmIntentPrimary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_FIRST_CONTACT_PRIMARY, intent, 0);
        PendingIntent alarmIntentSecondary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_FIRST_CONTACT_SECONDARY, intent, 0);

        mAlarmManager.cancel(alarmIntentPrimary);
        mAlarmManager.cancel(alarmIntentSecondary);

        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Sets the second contact alarm to run. When the alarm fires,
     * the app broadcasts an Intent to this WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setTotalityAlarm(Context context, Calendar calendar) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentPrimary = new Intent(context, WakefulReceiver.class);
        intentPrimary.putExtra("type", "totality");
        intentPrimary.putExtra("totality", false);
        Intent intentSecondary = new Intent(context, WakefulReceiver.class);
        intentSecondary.putExtra("type", "totality");
        intentSecondary.putExtra("totality", true); // launches media player

        PendingIntent alarmIntentPrimary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_TOTALITY_PRIMARY, intentPrimary, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent alarmIntentSecondary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_TOTALITY_SECONDARY, intentSecondary, PendingIntent.FLAG_UPDATE_CURRENT);


        Calendar current = Calendar.getInstance();
        Calendar calendarPrimary = (Calendar) calendar.clone();
        Calendar calendarSecondary = (Calendar) calendar.clone();
        calendarPrimary.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 4); // 4 min before first contact
        calendarSecondary.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 2); // 10 sec before
        calendarSecondary.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 10); // 10 sec before


        if (current.before(calendarPrimary)) {
            Date datePrimary = calendarPrimary.getTime();
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, datePrimary.getTime(), alarmIntentPrimary);
        }

        if (current.before(calendarSecondary)) {
            Date dateSecondary = calendarSecondary.getTime();
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, dateSecondary.getTime(), alarmIntentSecondary);
        }

        // Enable {@code BootReceiver} to automatically restart when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Cancels the second contact alarm from running. Removes any intents set by this
     * WakefulBroadcastReceiver.
     *
     * @param context the context of the app's Activity
     */
    public void cancelTotalityAlarm(Context context) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakefulReceiver.class);

        PendingIntent alarmIntentPrimary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_TOTALITY_PRIMARY, intent, 0);
        PendingIntent alarmIntentSecondary = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_TOTALITY_SECONDARY, intent, 0);

        mAlarmManager.cancel(alarmIntentPrimary);
        mAlarmManager.cancel(alarmIntentSecondary);

        // Disable {@code BootReceiver} so that it doesn't automatically restart when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


}