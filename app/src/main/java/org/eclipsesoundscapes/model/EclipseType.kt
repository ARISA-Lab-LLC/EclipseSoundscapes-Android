package org.eclipsesoundscapes.model

import com.google.gson.annotations.SerializedName

enum class EclipseType {
    @SerializedName("partial")
    PARTIAL,
    @SerializedName("annular")
    ANNULAR,
    @SerializedName("total")
    TOTAL,
    NONE
}