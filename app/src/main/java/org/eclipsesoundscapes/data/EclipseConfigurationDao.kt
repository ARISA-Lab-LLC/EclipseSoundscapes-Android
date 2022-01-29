package org.eclipsesoundscapes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.eclipsesoundscapes.model.EclipseConfiguration

/**
 * The Data Access Object for the [EclipseConfiguration] class.
 */
@Dao
interface EclipseConfigurationDao {
    @Query("SELECT * FROM eclipse_config")
    fun getEclipseConfigurations(): Flow<List<EclipseConfiguration>>

    @Query("SELECT * FROM eclipse_config ORDER BY date ASC")
    fun getOrderedEclipseConfigurations(): Flow<List<EclipseConfiguration>>

    @Query("SELECT * FROM eclipse_config WHERE strftime('%Y-%m-%d', date) >= strftime('%Y-%m-%d',:date)")
    fun getNextEclipse(date: String): Flow<EclipseConfiguration>


    @Query("SELECT * FROM eclipse_config WHERE strftime('%Y-%m-%d', date) = strftime('%Y-%m-%d',:date)")
    fun getEclipseByDate(date: String): Flow<EclipseConfiguration>

    @Insert
    suspend fun insertConfiguration(eclipseConfiguration: EclipseConfiguration): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configurations: List<EclipseConfiguration>)

    @Delete
    suspend fun deleteConfiguration(eclipseConfiguration: EclipseConfiguration)
}