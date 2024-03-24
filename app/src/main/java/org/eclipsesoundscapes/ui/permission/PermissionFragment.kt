package org.eclipsesoundscapes.ui.permission

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.FragmentPermissionBinding
import org.eclipsesoundscapes.util.PermissionUtils

private const val ARG_PERMISSION_TYPE = "permission_type"

class PermissionFragment : Fragment() {
    private var permissionType: String? = null
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private val permissionViewModel: PermissionViewModel by activityViewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        permissionViewModel.completeRequest(PERMISSION_TYPE_LOCATION)
    }

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        permissionViewModel.completeRequest(PERMISSION_TYPE_NOTIFICATIONS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            permissionType = it.getString(ARG_PERMISSION_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(permissionType) {
            PERMISSION_TYPE_LOCATION -> {
                binding.logo.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_location))
                binding.title.text = getString(R.string.location_access_title)
                binding.message.text = getString(R.string.location_permission_description)
                binding.buttonPermission.setOnClickListener {
                    (activity?.application as EclipseSoundscapesApp).dataManager.requestedLocation = true
                    locationPermissionRequest.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
                }
                binding.buttonAskLater.setOnClickListener {
                    (activity?.application as EclipseSoundscapesApp).dataManager.skippedLocationsPermission = true
                    permissionViewModel.completeRequest(PERMISSION_TYPE_LOCATION)
                }
            }

            PERMISSION_TYPE_NOTIFICATIONS -> {
                binding.logo.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_notification_bell))
                binding.title.text = getString(R.string.notifications_access_title)
                binding.message.text = getString(R.string.notifications_permission_description)
                binding.buttonPermission.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        (activity?.application as EclipseSoundscapesApp).dataManager.requestedNotification = true
                        notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                binding.buttonAskLater.setOnClickListener {
                    (activity?.application as EclipseSoundscapesApp).dataManager.skippedNotificationsPermission = true
                    permissionViewModel.completeRequest(PERMISSION_TYPE_NOTIFICATIONS)
                }
            }

            PERMISSION_TYPE_ALARM -> {
                binding.logo.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_alarm))
                binding.title.text = getString(R.string.alarm_access_title)
                binding.message.text = getString(R.string.alarm_permission_description)
                binding.buttonPermission.setOnClickListener {
                    requestExactAlarmPermission()
                }
                binding.buttonAskLater.setOnClickListener {
                    (activity?.application as EclipseSoundscapesApp).dataManager.skippedAlarmsPermission = true
                    permissionViewModel.completeRequest(PERMISSION_TYPE_ALARM)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionType == PERMISSION_TYPE_ALARM && PermissionUtils.hasAlarmPermission(requireContext())) {
            permissionViewModel.completeRequest(PERMISSION_TYPE_ALARM)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        context?.let {
            val alarmManager = ContextCompat.getSystemService(it, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    intent.data = Uri.fromParts("package", it.packageName, null)
                    it.startActivity(intent)
                }
            } else {
                permissionViewModel.completeRequest(PERMISSION_TYPE_NOTIFICATIONS)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(permissionType: String) =
            PermissionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PERMISSION_TYPE, permissionType)
                }
            }
    }
}