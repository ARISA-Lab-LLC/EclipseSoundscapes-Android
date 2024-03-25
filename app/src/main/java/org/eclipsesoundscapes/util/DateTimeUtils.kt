package org.eclipsesoundscapes.util

import org.eclipsesoundscapes.model.Event
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

object DateTimeUtils {

    private val eclipseDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    private val eclipseEventDateFormatter = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm:ss.S")
        .withZone(DateTimeZone.UTC)

    private fun formatEclipseDate(event: Event) : String = "${event.date} ${event.time}"

    fun eclipseEventDate(event: Event): DateTime? {
        return try {
            eclipseEventDateFormatter.parseDateTime(formatEclipseDate(event))
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Converts eclipse event time from UTC to local
     * @param event the eclipse [Event]
     */
    fun convertLocalTime(event: Event): String {
        return eclipseEventDate(event)?.let {
            try {
                val outputFormatter: DateTimeFormatter =
                    DateTimeFormat.forPattern("h:mm:ss a")
                        .withZone(DateTimeZone.getDefault())

                val local = it.withZone(DateTimeZone.getDefault())
                return outputFormatter.print(local)
            } catch (e: Exception) {
                ""
            }
        } ?: ""
    }

    /**
     * Converts eclipse event time from UTC to local
     * @param event the eclipse [Event]
     */
    fun eventLocalTime(event: Event): DateTime? {
        return eclipseEventDate(event)?.withZone(DateTimeZone.getDefault())
    }

    fun dateToEclipseDateFormat() : String {
        return eclipseDateFormatter.print(DateTime.now())
    }

    fun eclipseDateFormatToDate(date: String) : Date? {
        return try {
            eclipseDateFormatter.parseDateTime(date).toDate()
        } catch (e: Exception) {
            null
        }
    }
}