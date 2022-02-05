package org.eclipsesoundscapes.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EclipseConfigurationRepository @Inject constructor(
    private val eclipseConfigurationDao: EclipseConfigurationDao
) {
    fun nextEclipseConfiguration(date: String) = eclipseConfigurationDao.getNextEclipse(date)

    fun eclipseConfiguration(date: String) = eclipseConfigurationDao.getNextEclipse(date)
}