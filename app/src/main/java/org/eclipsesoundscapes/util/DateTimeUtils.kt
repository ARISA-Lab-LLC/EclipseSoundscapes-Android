package org.eclipsesoundscapes.util

import android.annotation.SuppressLint
import org.eclipsesoundscapes.model.Event
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    @SuppressLint("ConstantLocale")
    private val formatter: DateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }

    fun formatEclipseDate(event: Event) : String = "${event.date} ${event.time}"

    fun eclipseEventDate(event: Event): Date? {
        return try {
            formatter.parse(formatEclipseDate(event))
        } catch (e: ParseException) {
            return null
        }
    }

    fun eclipseEventDate(dateString: String): Date? {
        return try {
            formatter.parse(dateString)
        } catch (e: ParseException) {
            return null
        }
    }

    fun convertLocalTime(time: String): String {
        val dtf = SimpleDateFormat("HH:mm:ss.S")
            .apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

        try {
            val date = dtf.parse(time) ?: return ""

            val timeZone = TimeZone.getDefault()
            val simpleDateFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getDefault()

            val localDate: String = simpleDateFormat.format(date)

            // daylight saving time
            if (timeZone.useDaylightTime()) {
                val oneHourMillis = (60 * 60 * 1000).toFloat()
                val dstOffset = timeZone.dstSavings / oneHourMillis

                if (timeZone.inDaylightTime(Date())) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    calendar.add(Calendar.HOUR, dstOffset.toInt())
                    return simpleDateFormat.format(calendar.time)
                }
            }
            return localDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }
}