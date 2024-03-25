package org.eclipsesoundscapes.util

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.eclipsesoundscapes.EclipseSoundscapesApp
import javax.annotation.Nonnull

object PermissionUtils {

    private fun requestLocationPermission(activity: AppCompatActivity, permission: String): Boolean {
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

    fun showLocationScreen(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return !dataManager.requestedLocation
                && !dataManager.skippedLocationsPermission
                && (requestLocationPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    || requestLocationPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun showNotificationsScreen(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !dataManager.requestedNotification
                && !dataManager.skippedNotificationsPermission
                && requestNotificationsPermission(activity)
    }

    fun showAlarmScreen(activity: AppCompatActivity): Boolean {
        val dataManager = (activity.application as EclipseSoundscapesApp).dataManager
        return !dataManager.skippedNotificationsPermission && !hasAlarmPermission(activity)
    }

    fun hasAlarmPermission(@Nonnull context: Context): Boolean {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() == true
        } else {
            true
        }
    }
}