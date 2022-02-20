package org.eclipsesoundscapes.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.eclipsesoundscapes.model.Coordinate
import org.eclipsesoundscapes.model.EclipseType
import java.util.*

/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {

    @TypeConverter
    fun elementsFromJson(json: String): ArrayList<Double> {
        val listType = object : TypeToken<List<Double>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun elementsToJson(elements: ArrayList<Double>): String {
        return Gson().toJson(elements)
    }

    @TypeConverter
    fun centralLinesFromJson(json: String): Array<List<Coordinate>> {
        val listType = object : TypeToken<Array<List<Coordinate>>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun centralLinesToJson(centralLines: Array<List<Coordinate>>): String {
        return Gson().toJson(centralLines)
    }

    @TypeConverter
    fun eclipseTypeFromString(eclipseTypeString: String) = enumValueOf<EclipseType>(eclipseTypeString)

    @TypeConverter
    fun eclipseTypeToString(eclipseType: EclipseType) = eclipseType.name
}