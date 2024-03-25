package org.eclipsesoundscapes.util

import android.content.Context
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.model.EclipseVisibility
import org.joda.time.DateTime

object EclipseUtils {

    private enum class Contact {
        FIRST,
        SECOND,
        MID,
        THIRD,
        FOURTH
    }

    fun getCurrentEvent(context: Context?, eclipseExplorer: EclipseExplorer?): Eclipse? {
        if (context == null || eclipseExplorer == null) {
            return null
        }

        var event: Eclipse? = null

        DateTimeUtils.eventLocalTime(eclipseExplorer.contact1())?.let {
            val contactEvent = eclipseForContact(Contact.FIRST, eclipseExplorer)
            val offset = context.resources.getInteger(R.integer.first_contact_offset_sec)
            if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                event = contactEvent
            }
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            DateTimeUtils.eventLocalTime(eclipseExplorer.contact2())?.let {
                val contactEvent = eclipseForContact(Contact.SECOND, eclipseExplorer)
                var offset = 0
                if (eclipseExplorer.eclipseType == EclipseType.ANNULAR) {
                    offset = MediaUtils.getPhaseStartOffset(context)
                }

                if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                    event = contactEvent
                }
            }
        }

        DateTimeUtils.eventLocalTime(eclipseExplorer.contactMid())?.let {
            val contactEvent = eclipseForContact(Contact.MID, eclipseExplorer)
            val offset = context.resources.getInteger(R.integer.mid_contact_offset_sec)
            if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                event = contactEvent
            }
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            DateTimeUtils.eventLocalTime(eclipseExplorer.contact3())?.let {
                val contactEvent = eclipseForContact(Contact.THIRD, eclipseExplorer)
                if (shouldShowCurrentEvent(context, contactEvent, it)) {
                    event = contactEvent
                }
            }
        }

        DateTimeUtils.eventLocalTime(eclipseExplorer.contact4())?.let {
            val contactEvent = eclipseForContact(Contact.FOURTH, eclipseExplorer)
            if (shouldShowCurrentEvent(context, contactEvent, it)) {
                event = contactEvent
            }
        }

        return event
    }

    fun getNextEventDate(context: Context?, eclipseExplorer: EclipseExplorer?) : DateTime? {
        val events = getNextEvents(context, eclipseExplorer)
        return if (events.isNotEmpty()) {
            events.first().second
        } else {
            null
        }
    }

    fun getNextEvent(context: Context?, eclipseExplorer: EclipseExplorer?) : Pair<Eclipse, DateTime>? {
        val events = getNextEvents(context, eclipseExplorer)
        return if (events.isNotEmpty()) {
            events.first()
        } else {
            null
        }
    }
    private fun getNextEvents(context: Context?, eclipseExplorer: EclipseExplorer?): ArrayList<Pair<Eclipse, DateTime>> {
        val upcomingEvents = ArrayList<Pair<Eclipse, DateTime>>()

        eclipseExplorer?.let {
            var contactOneDate = DateTimeUtils.eventLocalTime(eclipseExplorer.contact1())
            val contactOneOffset = context?.resources?.getInteger(R.integer.first_contact_offset_sec) ?: 0
            contactOneDate = contactOneDate?.minusSeconds(contactOneOffset)

            if (contactOneDate?.isAfterNow == true) {
                val contactEvent = eclipseForContact(Contact.FIRST, eclipseExplorer)
                upcomingEvents.add(Pair(contactEvent, contactOneDate))
            }

            if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
                var contactTwoDate = DateTimeUtils.eventLocalTime(eclipseExplorer.contact2())
                var contactTwoOffset = 0

                if (eclipseExplorer.eclipseType == EclipseType.ANNULAR) {
                    contactTwoOffset = MediaUtils.getPhaseStartOffset(context)
                }

                contactTwoDate = contactTwoDate?.minusSeconds(contactTwoOffset)
                if (contactTwoDate?.isAfterNow == true) {
                    val contactEvent = eclipseForContact(Contact.SECOND, eclipseExplorer)
                    upcomingEvents.add(Pair(contactEvent, contactTwoDate))
                }
            }

            var contactMidDate = DateTimeUtils.eventLocalTime(eclipseExplorer.contactMid())
            val contactMidOffset = context?.resources?.getInteger(R.integer.mid_contact_offset_sec) ?: 0
            contactMidDate = contactMidDate?.minusSeconds(contactMidOffset)

            if (contactMidDate?.isAfterNow == true) {
                val contactEvent = eclipseForContact(Contact.MID, eclipseExplorer)
                upcomingEvents.add(Pair(contactEvent, contactMidDate))
            }

            if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
                val contactThreeDate = DateTimeUtils.eventLocalTime(eclipseExplorer.contact3())
                if (contactThreeDate?.isAfterNow == true) {
                    val contactEvent = eclipseForContact(Contact.THIRD, eclipseExplorer)
                    upcomingEvents.add(Pair(contactEvent, contactThreeDate))
                }
            }

            val contactFourDate = DateTimeUtils.eventLocalTime(eclipseExplorer.contact4())
            if (contactFourDate?.isAfterNow == true) {
                val contactEvent = eclipseForContact(Contact.FOURTH, eclipseExplorer)
                upcomingEvents.add(Pair(contactEvent, contactFourDate))
            }
        }

        return upcomingEvents
    }

    private fun eclipseForContact(contact: Contact, eclipseExplorer: EclipseExplorer) : Eclipse {
        return if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
            when (contact) {
                Contact.FIRST -> Eclipse.FIRST_CONTACT
                Contact.SECOND -> Eclipse.BAILYS_BEADS
                Contact.MID -> Eclipse.TOTALITY
                Contact.THIRD -> Eclipse.DIAMOND_RING
                Contact.FOURTH -> Eclipse.SUN_AS_STAR
            }
        } else {
            when (contact) {
                Contact.FIRST -> Eclipse.ANNULAR_START
                Contact.SECOND -> Eclipse.ANNULAR_PHASE_START
                Contact.MID -> Eclipse.ANNULARITY
                Contact.THIRD -> Eclipse.ANNULAR_PHASE_END
                Contact.FOURTH -> Eclipse.ANNULAR_END
            }
        }
    }

    private fun shouldShowCurrentEvent(context: Context, event: Eclipse?, date: DateTime, offset: Int = 0) : Boolean {
        val eventDate = date.minusSeconds(offset)
        val audioLength = MediaUtils.getAudioDuration(context, event?.shortAudio() ?: -1)
        return eventDate.isBeforeNow && date.plusMillis(audioLength.toInt()).isAfterNow
    }
}