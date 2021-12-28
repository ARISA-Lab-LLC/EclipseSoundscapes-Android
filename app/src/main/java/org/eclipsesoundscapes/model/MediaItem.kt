package org.eclipsesoundscapes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    val imageResId: Int,
    val titleResId: Int,
    val descriptionResId: Int,
    val audioResId: Int
) : Parcelable