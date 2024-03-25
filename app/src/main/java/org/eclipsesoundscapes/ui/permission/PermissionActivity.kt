package org.eclipsesoundscapes.ui.permission

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.ui.main.MainActivity
import org.eclipsesoundscapes.util.PermissionUtils

const val PERMISSION_TYPE_LOCATION = "location"
const val PERMISSION_TYPE_NOTIFICATIONS = "notifications"
const val PERMISSION_TYPE_ALARM = "alarm"

class PermissionActivity : BaseActivity() {

    lateinit var dataManager: DataManager
        private set

    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        dataManager = (application as EclipseSoundscapesApp).dataManager

        viewModel.requestedPermission.observe(this) { _ ->
            if (getNextPermission() == "") {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return@observe
            }

            showFragment(getNextPermission())
        }

        if (getNextPermission().isBlank()) {
            finish()
            return
        }

        showFragment(getNextPermission())
    }

    private fun showFragment(permissionType: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.permission_container, PermissionFragment.newInstance(
                permissionType))
            .commit()
    }

    private fun getNextPermission(): String {
        return if (PermissionUtils.showLocationScreen(this)) {
            PERMISSION_TYPE_LOCATION
        } else if (PermissionUtils.showNotificationsScreen(this)) {
            PERMISSION_TYPE_NOTIFICATIONS
        } else if (PermissionUtils.showAlarmScreen(this)) {
            PERMISSION_TYPE_ALARM
        } else {
            ""
        }
    }
}