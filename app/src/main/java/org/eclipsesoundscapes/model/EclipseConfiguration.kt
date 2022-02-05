package org.eclipsesoundscapes.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "eclipse_config")
data class EclipseConfiguration(
    @PrimaryKey
    val date: String,
    val elements: ArrayList<Double>,
    @SerializedName("central_lines")
    val centralLines: Array<List<Coordinate>>) : Parcelable

@Parcelize
data class Coordinate(
    val latitude: Double,
    val longitude: Double
) : Parcelable