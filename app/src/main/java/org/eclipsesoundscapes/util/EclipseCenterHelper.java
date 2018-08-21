package org.eclipsesoundscapes.util;

import android.content.Context;
import org.eclipsesoundscapes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EclipseCenterHelper {

    private Context mContext;

    public EclipseCenterHelper(Context context){
        this.mContext = context;
    }

    /**
     * @param event type of visible eclipse (none, partial or full)
     * @param localTime local time of first contact
     * @param uniTime universal time of first contact
     */
    public String generateContentDescription(String event, String localTime, String uniTime){
        return mContext.getString(R.string.event_prefix).concat(",").concat(event)
                .concat(",").concat(mContext.getString(R.string.local_time_prefix)).concat(",").concat(localTime)
                .concat(",").concat(mContext.getString(R.string.ut_time_prefix)).concat(",").concat(uniTime);
    }

    /**
     * Convert universal time to local time, takes into account daylight savings
     * @param time date/time for conversion
     */
    public String convertLocalTime(String time) {
        final float ONE_HOUR_MILLIS = 60 * 60 * 1000;

        SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss.S");
        dtf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = dtf.parse(time);

            TimeZone timeZone = TimeZone.getDefault();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm:ss a", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getDefault());

            String localDate;
            localDate = simpleDateFormat.format(date);

            // daylight saving time
            if (timeZone.useDaylightTime()) {
                float dstOffset = timeZone.getDSTSavings() / ONE_HOUR_MILLIS;
                if (timeZone.inDaylightTime(new Date())) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, (int) dstOffset);
                    return simpleDateFormat.format(calendar.getTime());
                }
            }

            return localDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}
