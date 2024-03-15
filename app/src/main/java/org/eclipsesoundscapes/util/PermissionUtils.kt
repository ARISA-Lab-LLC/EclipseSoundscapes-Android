package org.eclipsesoundscapes.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.eclipsesoundscapes.EclipseSoundscapesApp

object PermissionUtils {

    fun requestLocationPermission(activity: AppCompatActivity, permission: String): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED
                && (activity.shouldShowRequestPermissionRationale(permission)
                || (!activity.shouldShowRequestPermissionRationale(permission) && !dataManager.requestedLocation))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationsPermission(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
                && (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                || (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) && !dataManager.requestedNotification))
    }

    fun showLocationLockout(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return !dataManager.requestedLocation
                && !dataManager.skippedLocationsPermission
                && (requestLocationPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    || requestLocationPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun showNotificationsLockout(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !dataManager.requestedNotification
                && !dataManager.skippedNotificationsPermission
                && requestNotificationsPermission(activity)
    }
}