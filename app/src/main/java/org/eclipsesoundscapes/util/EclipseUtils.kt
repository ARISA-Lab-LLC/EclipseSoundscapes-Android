package org.eclipsesoundscapes.util

import android.content.Context
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.model.EclipseVisibility
import org.joda.time.DateTime

object EclipseUtils {

    fun getCurrentEvent(context: Context?, eclipseExplorer: EclipseExplorer?): Eclipse? {
        var event: Eclipse? = null
        if (context == null || eclipseExplorer == null) {
            return event
        }

        DateTimeUtils.eventLocalTime(eclipseExplorer.contact1())?.let {
            val contactEvent = if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
                Eclipse.FIRST_CONTACT
            } else {
                Eclipse.ANNULAR_START
            }

            val offset = context.resources.getInteger(R.integer.first_contact_offset_sec)
            if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                event = contactEvent
            }
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            DateTimeUtils.eventLocalTime(eclipseExplorer.contact2())?.let {

                var offset = 0
                val contactEvent = if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
                    Eclipse.BAILYS_BEADS
                } else {
                    offset = MediaUtils.getPhaseStartOffset(context)
                    Eclipse.ANNULAR_PHASE_START
                }

                if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                    event = contactEvent
                }
            }
        }

        DateTimeUtils.eventLocalTime(eclipseExplorer.contactMid())?.let {
            val contactEvent = if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
                Eclipse.TOTALITY
            } else {
                Eclipse.ANNULARITY
            }

            val offset = context.resources.getInteger(R.integer.mid_contact_offset_sec)
            if (shouldShowCurrentEvent(context, contactEvent, it, offset)) {
                event = contactEvent
            }
        }

        if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
            DateTimeUtils.eventLocalTime(eclipseExplorer.contact3())?.let {
                val contactEvent = if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
                    Eclipse.DIAMOND_RING
                } else {
                    Eclipse.ANNULAR_PHASE_END
                }

                if (shouldShowCurrentEvent(context, contactEvent, it)) {
                    event = contactEvent
                }
            }
        }

        DateTimeUtils.eventLocalTime(eclipseExplorer.contact4())?.let {
            val contactEvent = if (eclipseExplorer.eclipseType == EclipseType.TOTAL) {
                Eclipse.SUN_AS_STAR
            } else {
                Eclipse.ANNULAR_END
            }

            if (shouldShowCurrentEvent(context, contactEvent, it)) {
                event = contactEvent
            }
        }

        return event
    }

    fun getNextEventDate(context: Context?, eclipseExplorer: EclipseExplorer?): DateTime? {
        eclipseExplorer?.let {
            var contactOneDate = DateTimeUtils.eclipseEventDate(eclipseExplorer.contact1())
            val contactOneOffset = context?.resources?.getInteger(R.integer.first_contact_offset_sec) ?: 0
            contactOneDate = contactOneDate?.minusSeconds(contactOneOffset)
            if (contactOneDate?.isAfterNow == true) {
                return contactOneDate
            }

            if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
                var contactTwoDate = DateTimeUtils.eclipseEventDate(eclipseExplorer.contact2())
                var contactTwoOffset = 0
                if (eclipseExplorer.eclipseType == EclipseType.ANNULAR) {
                    contactTwoOffset = MediaUtils.getPhaseStartOffset(context)
                }

                contactTwoDate = contactTwoDate?.minusSeconds(contactTwoOffset)
                if (contactTwoDate?.isAfterNow == true) {
                    return contactTwoDate
                }
            }

            var contactMidDate = DateTimeUtils.eclipseEventDate(eclipseExplorer.contactMid())
            val contactMidOffset = context?.resources?.getInteger(R.integer.mid_contact_offset_sec) ?: 0
            contactMidDate = contactMidDate?.minusSeconds(contactMidOffset)
            if (contactMidDate?.isAfterNow == true) {
                return contactMidDate
            }

            if (eclipseExplorer.eclipseVisibility == EclipseVisibility.FULL) {
                val contactThreeDate = DateTimeUtils.eclipseEventDate(eclipseExplorer.contact3())
                if (contactThreeDate?.isAfterNow == true) {
                    return contactThreeDate
                }
            }

            val contactFourDate = DateTimeUtils.eclipseEventDate(eclipseExplorer.contact4())
            if (contactFourDate?.isAfterNow == true) {
                return contactFourDate
            }
        }

        return null
    }

    private fun shouldShowCurrentEvent(context: Context, event: Eclipse?, date: DateTime, offset: Int = 0) : Boolean {
        val eventDate = date.minusSeconds(offset)
        val audioLength = MediaUtils.getAudioDuration(context, event?.shortAudio() ?: -1)
        return eventDate.isBeforeNow && date.plusMillis(audioLength.toInt()).isAfterNow
    }
}