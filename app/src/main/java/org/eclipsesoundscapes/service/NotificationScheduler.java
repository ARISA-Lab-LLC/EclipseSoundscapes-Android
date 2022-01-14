package org.eclipsesoundscapes.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.util.DateTimeUtils;
import org.eclipsesoundscapes.util.NotificationUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.ALARM_SERVICE;
import static org.eclipsesoundscapes.util.NotificationUtils.NOTIFICATION_CHANNEL_ID;

/*
 * Schedules notification reminder for upcoming eclipse during first contact and totality.
 */
public class NotificationScheduler {

    private static final int FIRST_CONTACT_REMINDER_REQUEST_CODE = 110;
    private static final int FIRST_CONTACT_REQUEST_CODE = 111;

    private static final int TOTALITY_REMINDER_REQUEST_CODE = 112;
    private static final int TOTALITY_REQUEST_CODE = 113;

    // notification extras
    static final String ECLIPSE_NOTIFICATION_TYPE = "type";
    static final String ECLIPSE_LAUNCH_MEDIA = "launch_media";

    static final String ECLIPSE_FIRST_CONTACT = "first_contact";
    private static final String ECLIPSE_TOTALITY = "totality";


    public static void scheduleNotifications(Context context, String contactOne, String totality) {

        final boolean isLiveEnabled = context.getResources().getBoolean(R.bool.live_experience_enabled);
        if (!isLiveEnabled) {
            return;
        }

        Date contactDate = DateTimeUtils.INSTANCE.eclipseEventDate(contactOne);
        Date totalityDate = DateTimeUtils.INSTANCE.eclipseEventDate(totality);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (contactDate != null) {
            calendar.setTime(contactDate);
            setFirstContactReminder(context, NotificationReceiver.class, calendar);
        }

        if (totalityDate != null){
            calendar.clear();
            calendar.setTime(totalityDate);
            setTotalityReminder(context, NotificationReceiver.class, calendar);
        }
    }


    /**************************************************************************************************
     * First Contact
     *************************************************************************************************/

    private static void setFirstContactReminder(Context context, Class<?> cls, Calendar calendar){

        // cancel already scheduled reminders
        cancelFirstContactReminder(context,cls);

        // enable receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent firstIntent = new Intent(context, cls);
        firstIntent.putExtra(ECLIPSE_NOTIFICATION_TYPE, ECLIPSE_FIRST_CONTACT);
        firstIntent.putExtra(ECLIPSE_LAUNCH_MEDIA, false);

        Intent secondIntent = new Intent(context, cls);
        secondIntent.putExtra(ECLIPSE_NOTIFICATION_TYPE, ECLIPSE_FIRST_CONTACT);
        secondIntent.putExtra(ECLIPSE_LAUNCH_MEDIA, true);

        PendingIntent firstReminder = PendingIntent.getBroadcast(context,
                FIRST_CONTACT_REMINDER_REQUEST_CODE, firstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent secondReminder = PendingIntent.getBroadcast(context,
                FIRST_CONTACT_REQUEST_CODE, secondIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar current = Calendar.getInstance();
        Calendar firstCal = (Calendar) calendar.clone();
        Calendar secondCal = (Calendar) calendar.clone();
        firstCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 2); // 2 min before first contact
        secondCal.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 10); // 10 sec before

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            if (current.before(firstCal)) {
                am.setExact(AlarmManager.RTC_WAKEUP, firstCal.getTimeInMillis(), firstReminder);
            }

            if (current.before(secondCal)) {
                am.setExact(AlarmManager.RTC_WAKEUP, secondCal.getTimeInMillis(), secondReminder);
            }
        }
    }

    private static void cancelFirstContactReminder(Context context, Class<?> cls){

        // disable receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent firstIntent = PendingIntent.getBroadcast(context, FIRST_CONTACT_REMINDER_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent secondIntent = PendingIntent.getBroadcast(context, FIRST_CONTACT_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.cancel(firstIntent);
            am.cancel(secondIntent);

            firstIntent.cancel();
            secondIntent.cancel();
        }
    }

    /**************************************************************************************************
     * Totality
     *************************************************************************************************/

    private static void setTotalityReminder(Context context, Class<?> cls, Calendar calendar){

        // cancel already scheduled reminders
        cancelTotalityReminder(context,cls);

        // enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent firstIntent = new Intent(context, cls);
        firstIntent.putExtra(ECLIPSE_NOTIFICATION_TYPE, ECLIPSE_TOTALITY);
        firstIntent.putExtra(ECLIPSE_LAUNCH_MEDIA, false);

        Intent secondIntent = new Intent(context, cls);
        secondIntent.putExtra(ECLIPSE_NOTIFICATION_TYPE, ECLIPSE_TOTALITY);
        secondIntent.putExtra(ECLIPSE_LAUNCH_MEDIA, true); // launches media player

        PendingIntent alarmIntentPrimary = PendingIntent.getBroadcast(context, TOTALITY_REMINDER_REQUEST_CODE,
                firstIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent alarmIntentSecondary = PendingIntent.getBroadcast(context, TOTALITY_REQUEST_CODE,
                secondIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar current = Calendar.getInstance();
        Calendar firstCal = (Calendar) calendar.clone();
        Calendar secondCal = (Calendar) calendar.clone();
        firstCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 4); // 4 min before totality

        secondCal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 2); // 2 min
        secondCal.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - 10); // 10 sec before

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            if (current.before(firstCal)) {
                am.setExact(AlarmManager.RTC_WAKEUP, firstCal.getTimeInMillis(), alarmIntentPrimary);
            }

            if (current.before(secondCal)) {
                am.setExact(AlarmManager.RTC_WAKEUP, secondCal.getTimeInMillis(), alarmIntentSecondary);
            }
        }
    }

    private static void cancelTotalityReminder(Context context, Class<?> cls){

        // disable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(context, cls);
        PendingIntent firstIntent = PendingIntent.getBroadcast(context, TOTALITY_REMINDER_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent secondIntent = PendingIntent.getBroadcast(context, TOTALITY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.cancel(firstIntent);
            am.cancel(secondIntent);

            firstIntent.cancel();
            secondIntent.cancel();
        }
    }

    /**************************************************************************************************
     * Show notification
     *************************************************************************************************/

    static void showNotification(Context context, Class<?> cls, String type, boolean launchMedia,
                                 String title, String description, Intent intent) {

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(intent);

        int REQUEST_CODE = 0;
        switch (type){
            case ECLIPSE_FIRST_CONTACT:
                REQUEST_CODE = launchMedia ? FIRST_CONTACT_REQUEST_CODE : FIRST_CONTACT_REMINDER_REQUEST_CODE;
                break;
            case ECLIPSE_TOTALITY:
                REQUEST_CODE = launchMedia ? TOTALITY_REQUEST_CODE: TOTALITY_REMINDER_REQUEST_CODE;
                break;
        }

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(REQUEST_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationUtils.createNotificationChannel(context);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.ic_stat_ic_waves)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(description)
                // accessibility
                .setTicker(description);

        notificationManager.notify(REQUEST_CODE, notificationBuilder.build());
    }
}
