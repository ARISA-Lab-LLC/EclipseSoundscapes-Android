package org.eclipsesoundscapes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Representation of an entity credited for an [Eclipse] image.
 */
@Parcelize
data class PhotoCredit(val eclipse: Eclipse, val copyright: String, val link: String) : Parcelable