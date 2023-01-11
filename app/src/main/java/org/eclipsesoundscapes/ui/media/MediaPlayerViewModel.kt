package org.eclipsesoundscapes.ui.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.data.EclipseConfigurationRepository
import org.eclipsesoundscapes.model.EclipseConfiguration
import javax.inject.Inject

@HiltViewModel
class MediaPlayerViewModel @Inject internal constructor(
    application: Application,
    private val eclipseConfigurationRepository: EclipseConfigurationRepository,
    val dataManager: DataManager,
) : AndroidViewModel(application)  {

    private val _eclipseConfiguration = MutableLiveData<EclipseConfiguration?>()

    val eclipseConfiguration
        get() = _eclipseConfiguration

    init {
        fetchEclipseConfiguration()
    }

    private fun fetchEclipseConfiguration() {
        dataManager.currentEclipseDate?.let { date ->
            viewModelScope.launch {
                eclipseConfigurationRepository.eclipseConfiguration(date).collect { config ->
                    _eclipseConfiguration.value = config
                }
            }
        }
    }
}