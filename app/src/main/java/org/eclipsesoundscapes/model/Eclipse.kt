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
    TOTALITY,
    ANNULAR_START,
    ANNULAR_PHASE_START,
    ANNULARITY,
    ANNULAR_PHASE_END,
    ANNULAR_END;

    companion object {
        /**
         * Returns all [Eclipse] which are not close up
         */
        fun mediaEclipses(): ArrayList<Eclipse> {
            return values().filter { !it.isCloseUpEclipse() }.toCollection(ArrayList())
        }

        fun totalEclipseMedia(): ArrayList<Eclipse> {
            return values().filter { !it.isCloseUpEclipse() && !it.isAnnularEclipse() }.toCollection(ArrayList())
        }

        fun annularEclipseMedia(): ArrayList<Eclipse> {
            return values().filter { it.isAnnularEclipse() }.toCollection(ArrayList())
        }
    }

    private fun isCloseUpEclipse() : Boolean {
        return this == BAILYS_BEADS_CLOSEUP
                || this == HELMET_STREAMER_CLOSEUP
                || this == PROMINENCE_CLOSEUP
    }

    fun isAnnularEclipse() : Boolean {
        return this == ANNULAR_START
                || this == ANNULAR_PHASE_START
                || this == ANNULARITY
                || this == ANNULAR_PHASE_END
                || this == ANNULAR_END
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
        ANNULAR_START -> R.drawable.annular_eclipse_start
        ANNULAR_PHASE_START -> R.drawable.annular_eclipse_phase_start
        ANNULARITY -> R.drawable.annularity
        ANNULAR_PHASE_END -> R.drawable.annular_eclipse_phase_end
        ANNULAR_END -> R.drawable.annular_eclipse_end
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
        ANNULAR_START -> R.string.annular_start
        ANNULAR_PHASE_START -> R.string.annular_phase_start
        ANNULARITY -> R.string.annularity
        ANNULAR_PHASE_END -> R.string.annular_phase_end
        ANNULAR_END -> R.string.annular_end
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
        ANNULAR_START -> R.string.annular_start_description
        ANNULAR_PHASE_START -> R.string.annular_phase_start_description
        ANNULARITY -> R.string.annularity_description
        ANNULAR_PHASE_END -> R.string.annular_phase_end_description
        ANNULAR_END -> R.string.annular_end_description
    }

    fun audioDescription(): Int = when (this) {
        FIRST_CONTACT -> R.string.audio_first_contact_full
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.string.audio_bailys_beads_full
        CORONA -> R.string.audio_corona_full
        DIAMOND_RING -> R.string.audio_diamond_ring_full
        HELMET_STREAMER, HELMET_STREAMER_CLOSEUP -> R.string.audio_helmet_streamers_full
        PROMINENCE, PROMINENCE_CLOSEUP -> R.string.audio_prominence_full
        TOTALITY -> R.string.audio_totality_full
        ANNULAR_START -> R.string.annular_start_description
        ANNULAR_PHASE_START -> R.string.annular_phase_start_description
        ANNULARITY -> R.string.annularity_description
        ANNULAR_PHASE_END -> R.string.annular_phase_end_description
        ANNULAR_END -> R.string.annular_end_description
    }

    fun audio(): Int = when (this) {
        FIRST_CONTACT -> R.raw.first_contact_full
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.raw.bailys_beads_full
        CORONA -> R.raw.corona_full
        DIAMOND_RING -> R.raw.diamond_ring_full
        HELMET_STREAMER, HELMET_STREAMER_CLOSEUP -> R.raw.helmet_streamers_full
        PROMINENCE, PROMINENCE_CLOSEUP -> R.raw.prominence_full
        TOTALITY -> R.raw.totality_full
        ANNULAR_START -> R.raw.annular_eclipse_start_long
        ANNULAR_PHASE_START -> R.raw.annular_eclipse_phase_start_long
        ANNULARITY -> R.raw.annularity_long
        ANNULAR_PHASE_END -> R.raw.annular_eclipse_phase_end_long
        ANNULAR_END -> R.raw.annular_eclipse_end_long
    }

    fun shortAudioDescription(): Int = when (this) {
        FIRST_CONTACT -> R.string.audio_first_contact_short
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.string.audio_bailys_beads_short
        TOTALITY -> R.string.audio_totality_short
        DIAMOND_RING -> R.string.audio_diamond_ring_short
        ANNULAR_START -> R.string.audio_annular_start_short
        ANNULAR_PHASE_START -> R.string.audio_annular_phase_start_short
        ANNULARITY -> R.string.audio_annularity_short
        ANNULAR_PHASE_END -> R.string.audio_annular_phase_end_short
        ANNULAR_END -> R.string.audio_annular_end_short
        else -> audioDescription()
    }

    fun shortAudio(): Int = when (this) {
        FIRST_CONTACT -> R.raw.first_contact_short
        BAILYS_BEADS, BAILYS_BEADS_CLOSEUP -> R.raw.bailys_beads_short
        DIAMOND_RING -> R.raw.diamond_ring_short
        TOTALITY -> R.raw.totality_short
        ANNULAR_START -> R.raw.annular_eclipse_start_short
        ANNULAR_PHASE_START -> R.raw.annular_eclipse_phase_start_short
        ANNULARITY -> R.raw.annularity_short
        ANNULAR_PHASE_END -> R.raw.annular_eclipse_phase_end_short
        ANNULAR_END -> R.raw.annular_eclipse_end_short
        else -> audio()
    }
}