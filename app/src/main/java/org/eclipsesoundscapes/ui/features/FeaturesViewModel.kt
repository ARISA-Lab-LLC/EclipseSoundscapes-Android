package org.eclipsesoundscapes.ui.features

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.eclipsesoundscapes.data.DataManager
import javax.inject.Inject

@HiltViewModel
class FeaturesViewModel @Inject internal constructor(
    val dataManager: DataManager,
) : ViewModel()