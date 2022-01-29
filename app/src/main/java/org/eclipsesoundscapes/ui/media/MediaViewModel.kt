package org.eclipsesoundscapes.ui.media

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.eclipsesoundscapes.data.DataManager
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject internal constructor(
    val dataManager: DataManager,
) : ViewModel() {

    fun afterFirstContact() : Boolean {
        return dataManager.isAfterFirstContact()
    }

    fun afterTotality() : Boolean {
        return dataManager.isAfterTotality()
    }
}