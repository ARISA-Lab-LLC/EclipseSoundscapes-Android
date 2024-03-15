package org.eclipsesoundscapes.ui.permission

import android.Manifest
import android.os.Build
import android.os.Bundle
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
                binding.title.text = getString(R.string.location_access_title)
                binding.title.contentDescription = getString(R.string.location_permission_content_description)
                binding.message.text = getString(R.string.location_permission_description)
                binding.buttonPermission.contentDescription = getString(R.string.location_permission)
                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_map_marker)
                binding.buttonPermission.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
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
                binding.title.text = getString(R.string.notifications_access_title)
                binding.title.contentDescription = getString(R.string.notifications_permission_content_description)
                binding.message.text = getString(R.string.notifications_permission_description)
                binding.buttonPermission.contentDescription = getString(R.string.notifications_permission)
                val drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_notification_bell)
                binding.buttonPermission.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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