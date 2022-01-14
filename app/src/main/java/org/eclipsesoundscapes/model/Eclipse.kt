package org.eclipsesoundscapes.model

import org.eclipsesoundscapes.R

enum class Eclipse {
    FIRST_CONTACT,
    BAILYS_BEADS,
    BAILYS_BEADS_CLOSEUP,
    CORONA,
    DIAMOND_RING,
    HELMET_STREAMER,
    HELMET_STREAMER_CLOSEUP,
    PROMINENCE,
    PROMINENCE_CLOSEUP,
    TOTALITY;

    companion object {
        /**
         * Returns all [Eclipse] which are not close up images
         */
        fun photoCreditEclipses(): ArrayList<Eclipse> {
            return values().filter { !it.isCloseUpEclipse() }.toCollection(ArrayList())
        }
    }

    private fun isCloseUpEclipse() : Boolean {
        return this == BAILYS_BEADS_CLOSEUP
                || this == HELMET_STREAMER_CLOSEUP
                || this == PROMINENCE_CLOSEUP;
    }

    fun imageResource(): Int = when (this) {
        FIRST_CONTACT -> R.drawable.eclipse_first_contact
        BAILYS_BEADS -> R.drawable.eclipse_bailys_beads
        BAILYS_BEADS_CLOSEUP -> R.drawable.bailys_beads_close_up
        CORONA -> R.drawable.eclipse_corona
        DIAMOND_RING -> R.drawable.eclipse_diamond_ring
        HELMET_STREAMER -> R.drawable.helmet_streamers
        HELMET_STREAMER_CLOSEUP -> R.drawable.helmet_streamer_closeup
        PROMINENCE -> R.drawable.eclipse_prominence
        PROMINENCE_CLOSEUP -> R.drawable.prominence_closeup
        TOTALITY -> R.drawable.eclipse_totality
    }

    fun title(): Int = when (this) {
        FIRST_CONTACT -> R.string.first_contact
        BAILYS_BEADS -> R.string.bailys_beads
        BAILYS_BEADS_CLOSEUP -> R.string.bailys_beads_closeup
        CORONA -> R.string.corona
        DIAMOND_RING -> R.string.diamond_ring
        HELMET_STREAMER -> R.string.helmet_streamers
        HELMET_STREAMER_CLOSEUP -> R.string.helmet_streamers_closeup
        PROMINENCE -> R.string.prominence
        PROMINENCE_CLOSEUP -> R.string.prominence_closeup
        TOTALITY -> R.string.totality
    }

    fun description(): Int = when (this) {
        FIRST_CONTACT -> R.string.first_contact_description
        BAILYS_BEADS -> R.string.bailys_beads_description
        BAILYS_BEADS_CLOSEUP -> R.string.bailys_beads_description
        CORONA -> R.string.corona_description
        DIAMOND_RING -> R.string.diamond_ring_description
        HELMET_STREAMER -> R.string.helmet_streamers_description
        HELMET_STREAMER_CLOSEUP -> R.string.helmet_streamers_description
        PROMINENCE -> R.string.prominence_description
        PROMINENCE_CLOSEUP -> R.string.prominence_description
        TOTALITY -> R.string.totality_description
    }

    fun audioDescription(): Int = when (this) {
        FIRST_CONTACT -> R.string.audio_first_contact_full
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.string.audio_bailys_beads_full
        CORONA -> R.string.audio_corona_full
        DIAMOND_RING -> R.string.audio_diamond_ring_full
        HELMET_STREAMER, HELMET_STREAMER_CLOSEUP -> R.string.audio_helmet_streamers_full
        PROMINENCE, PROMINENCE_CLOSEUP -> R.string.audio_prominence_full
        TOTALITY -> R.string.audio_totality_full
    }

    fun audio(): Int = when (this) {
        FIRST_CONTACT -> R.raw.first_contact_full
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.raw.bailys_beads_full
        CORONA -> R.raw.corona_full
        DIAMOND_RING -> R.raw.diamond_ring_full
        HELMET_STREAMER, HELMET_STREAMER_CLOSEUP -> R.raw.helmet_streamers_full
        PROMINENCE, PROMINENCE_CLOSEUP -> R.raw.prominence_full
        TOTALITY -> R.raw.totality_full
    }
}