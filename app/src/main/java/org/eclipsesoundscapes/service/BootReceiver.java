package org.eclipsesoundscapes.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.eclipsesoundscapes.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
 * Resets eclipse notifications for first and second contact when device has been restarted
 */

public class BootReceiver extends BroadcastReceiver {

    public AlarmManager mAlarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            // fetch date the contacts
            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String firstContact = preference.getString("first_contact", "");
            String secondContact = preference.getString("second_contact", "");

            Date date;
            Date date2;

            if (!firstContact.isEmpty()) {
                try{
                    date = dateFormat.parse(firstContact);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                    calendar.setTime(date);
                    setFirstContactAlarm(context, calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (!secondContact.isEmpty()) {
                try{
                    date2 = dateFormat.parse(secondContact);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                    calendar.setTime(date2);
                    setTotalityAlarm(context, calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set an alarm that will trigger the notification for the second contact
     * @param context Applications context
     * @param calendar Calendar date for the first contact
     */
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
    }

    /**
     * Set an alarm that will trigger the notification for the second contact
     * @param context Applications context
     * @param calendar Calendar date for the second contact
     */
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
    }
}