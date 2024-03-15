package org.eclipsesoundscapes.ui.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    private val mutableRequestedPermission = MutableLiveData<String>()
    val requestedPermission: LiveData<String> get() = mutableRequestedPermission

    fun completeRequest(requestType: String) {
        mutableRequestedPermission.value = requestType
    }
}
