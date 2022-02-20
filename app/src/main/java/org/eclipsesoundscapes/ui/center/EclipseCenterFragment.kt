package org.eclipsesoundscapes.ui.center

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.data.EclipseExplorer
import org.eclipsesoundscapes.databinding.FragmentEclipseCenterBinding
import org.eclipsesoundscapes.databinding.LayoutEclipseEventRowBinding
import org.eclipsesoundscapes.model.EclipseType
import org.eclipsesoundscapes.model.Event
import org.eclipsesoundscapes.service.NotificationScheduler
import org.eclipsesoundscapes.ui.about.SettingsActivity
import org.eclipsesoundscapes.ui.main.MainActivity
import org.eclipsesoundscapes.util.DateTimeUtils
import org.joda.time.Interval
import java.text.ParseException
import java.util.*

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */

/**
 * @author Joel Goncalves
 *
 * Generates a countdown until eclipse and provides a list view of related information by
 * location.
 * @see EclipseExplorer
 */

@AndroidEntryPoint
class EclipseCenterFragment : Fragment(), LifecycleObserver {
    private var _binding: FragmentEclipseCenterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EclipseCenterViewModel by viewModels()
    private var dataManager: DataManager? = null
        get() {
            if (field == null) {
                field = (activity as? MainActivity)?.dataManager
            }

            return field
        }

    private var eclipseExplorer: EclipseExplorer? = null
        set(value) {
            field = value

            value?.let {
                viewModel.saveEclipseEventDates(value)
                updateView()
                showEclipseDetails()
                setupNotifications()
                startCountdown()
            }
        }

    private var lastKnownLocation: Location? = null
        set(value) {
            field = value
            dataManager?.lastLocation = value
        }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback : LocationCallback? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEclipseCenterBinding.inflate(inflater, container, false).apply {

            permissionView.root.setOnClickListener {
                handleLocationPermission()
            }

            simulationView.buttonSimulate.setOnClickListener {
                dataManager?.simulated = true
                simulationView.root.visibility = View.GONE
                simulateEclipseLocation()
            }

            locationDisabledView.buttonEnableLocation.setOnClickListener {
                activity?.let {
                    if (locationServicesEnabled()) {
                        // in case user enabled location services from quick settings menu
                        verifyLocationAccess()
                    } else {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        it.startActivity(intent)
                    }
                }
            }

            if (resources.getBoolean(R.bool.lockout_eclipse_center)) {
                lockoutView.root.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? MainActivity)?.let {
            dataManager = it.dataManager
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(it)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.eclipseConfiguration.observe(viewLifecycleOwner) { result ->
            result?.let {
                viewModel.saveEclipseDate(it)
                verifyLocationAccess()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun createEclipseGenerator(location: Location?) : EclipseExplorer? {
        return location?.let {
            viewModel.eclipseConfiguration.value?.let { eclipseConfig ->
                EclipseExplorer(
                    context,
                    eclipseConfig,
                    it.latitude,
                    it.longitude
                )
            }
        }
    }

    private fun onLocationDetermined(location: Location?) {
        binding.progressView.root.visibility = View.GONE
        location?.let {
            eclipseExplorer = createEclipseGenerator(it)
        }
    }

    /**
     * User is not in the path of eclipse. Simulate count down and details from nearest point
     * in the path of totality
     */
    private fun simulateEclipseLocation() {
        lastKnownLocation?.let {
            viewModel.closestPointOnPath(it)?.let { point ->
                eclipseExplorer = createEclipseGenerator(point)
            }
        }
    }

    private fun updateView() {
        binding.eclipseCenterLayout.root.visibility = View.VISIBLE

        eclipseExplorer?.let {

            binding.eclipseCenterLayout.latitude.text = getString(
                R.string.lat_lng_format, it.latitude,
                getString(
                    if (it.latitude > 0) {
                        R.string.north
                    } else {
                        R.string.south
                    }
                )
            )

            binding.eclipseCenterLayout.longitude.text = getString(
                R.string.lat_lng_format, it.longitude,
                getString(
                    if (it.longitude > 0) {
                        R.string.east
                    } else {
                        R.string.west
                    }
                )
            )

            binding.eclipseCenterLayout.percentEclipse.text = eclipseExplorer?.coverage

            // set date of first contact
            if (eclipseExplorer?.type != EclipseType.NONE) {
                binding.eclipseCenterLayout.date.text = eclipseExplorer?.contact1()?.date
            }

            binding.eclipseCenterLayout.eclipseType.text = when (eclipseExplorer?.type) {
                EclipseType.ANNULAR -> getString(R.string.eclipse_type_annular)
                EclipseType.PARTIAL -> getString(R.string.eclipse_type_partial)
                EclipseType.TOTAL -> getString(R.string.eclipse_type_full)
                EclipseType.NONE -> getString(R.string.eclipse_type_none)
                null -> getString(R.string.eclipse_type_unkown)
            }
        }
    }

    private fun showEclipseDetails() {
        eclipseExplorer?.type?.let {
            when (it) {
                EclipseType.NONE -> {
                    val simulated = dataManager?.simulated ?: false
                    if (!simulated) {
                        // show user option to simulate location within the eclipse path
                        binding.simulationView.root.visibility = View.VISIBLE
                    } else {
                        simulateEclipseLocation()
                    }
                }

                EclipseType.PARTIAL -> showPartialEclipse()
                EclipseType.ANNULAR,
                EclipseType.TOTAL -> showFullEclipse()
            }
        }
    }

    private fun showPartialEclipse() {
        fillEventView(
            eclipseExplorer?.contact1(),
            binding.eclipseCenterLayout.stubContactOne
        )
        fillEventView(
            eclipseExplorer?.contactMid(),
            binding.eclipseCenterLayout.stubContactMid
        )
        fillEventView(
            eclipseExplorer?.contact4(),
            binding.eclipseCenterLayout.stubContactFour
        )

        binding.eclipseCenterLayout.stubContactTwo.root.visibility = View.GONE
        binding.eclipseCenterLayout.stubContactThree.root.visibility = View.GONE
    }

    private fun showFullEclipse() {
        fillEventView(
            eclipseExplorer?.contact1(),
            binding.eclipseCenterLayout.stubContactOne
        )
        fillEventView(
            eclipseExplorer?.contact2(),
            binding.eclipseCenterLayout.stubContactTwo
        )
        fillEventView(
            eclipseExplorer?.contactMid(),
            binding.eclipseCenterLayout.stubContactMid
        )
        fillEventView(
            eclipseExplorer?.contact3(),
            binding.eclipseCenterLayout.stubContactThree
        )
        fillEventView(
            eclipseExplorer?.contact4(),
            binding.eclipseCenterLayout.stubContactFour
        )

        binding.eclipseCenterLayout.durationTotalityLayout.visibility = View.VISIBLE
        binding.eclipseCenterLayout.durationTotality.text = eclipseExplorer?.formattedDuration

        eclipseExplorer?.duration?.let {
            val seconds = String.format(
                Locale.getDefault(),
                "%d.%d",
                it.secondOfMinute,
                it.millisOfSecond
            )

            binding.eclipseCenterLayout.durationTotality.contentDescription =
                getString(R.string.duration_min_sec, it.minuteOfHour.toString(), seconds)
        }
    }

    private fun fillEventView(event: Event?, layout: LayoutEclipseEventRowBinding) {
        event?.let {
            val localTime = DateTimeUtils.convertLocalTime(it)
            layout.eclipseEvent.text = getString(R.string.eclipse_center_title_format, it.name)
            layout.eclipseTimeLocal.text = localTime
            layout.eclipseTimeUt.text = it.time

            layout.root.contentDescription = getString(R.string.event_desc_format,
                it.name,
                localTime,
                it.time)

            layout.root.visibility = View.VISIBLE
        }
    }

    private fun setupNotifications() {
        if (eclipseExplorer?.type == EclipseType.NONE) {
            return
        }

        // user has disabled notifications in the app settings
        if (dataManager?.notifications != true) {
            return
        }

        activity?.let { activity ->
            eclipseExplorer?.let {
                NotificationScheduler.scheduleNotifications(activity, it)
            }
        }
    }

    private fun startCountdown() {
        if (eclipseExplorer?.type == EclipseType.NONE
            || viewModel.afterTotality()) {
            return
        }

        val firstContactEvent = eclipseExplorer?.contact1() ?: return

        try {
            val date = DateTimeUtils.eclipseEventDate(firstContactEvent) ?: return
            val startTimeMillis = Date().time
            val endTimeMillis = date.millis
            val millisDif = endTimeMillis - startTimeMillis

            binding.eclipseCenterLayout.eclipseCountdown.visibility = View.VISIBLE

            // cancel previous timer
            countDownTimer?.cancel()

            countDownTimer = object : CountDownTimer(millisDif, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val interval = Interval(Date().time, date.millis)
                    binding.eclipseCenterLayout.eclipseCountdown.update(interval.toPeriod())
                }

                override fun onFinish() {
                    // no-op
                }
            }

            countDownTimer?.start()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun verifyLocationAccess() {
        activity?.let {
            if (!locationPermissionGranted(it)) {
                showPermissionView(true)
            } else {
                onPermissionGranted()
            }
        }
    }

    private fun handleLocationPermission() {
        dataManager?.let { dataManager ->
            activity?.let { context ->
                if (!dataManager.locationAccess) {
                    // user disabled access to location in app settings
                    val settingsIntent = Intent(activity, SettingsActivity::class.java)
                    settingsIntent.putExtra(
                        SettingsActivity.EXTRA_SETTINGS_MODE,
                        SettingsActivity.MODE_SETTINGS
                    )

                    showSettingsDialog(getString(R.string.app_settings_permission), settingsIntent)
                    return
                }

                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                // user has checked 'Never ask again'
                // request user to grant permissions from the device settings
                if (dataManager.requestedLocation && !showRationale) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    showSettingsDialog(getString(R.string.device_settings_permission), intent)
                    return
                }

                if (!dataManager.requestedLocation) {
                    dataManager.requestedLocation = true
                }

                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun locationPermissionGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onPermissionGranted()
            }
        }

    private fun showPermissionView(show: Boolean) {
        if (show) {
            binding.eclipseCenterLayout.rlEclipseCenter.visibility = View.GONE
            binding.permissionView.root.visibility = View.VISIBLE
        } else {
            binding.eclipseCenterLayout.rlEclipseCenter.visibility = View.VISIBLE
            binding.permissionView.root.visibility = View.GONE
        }
    }

    /**
     * User has granted us permission, handled by parent activity
     * @see MainActivity
     */
    private fun onPermissionGranted() {
        showPermissionView(false)
        getDeviceLocation()
    }

    private fun locationServicesEnabled() : Boolean = activity?.let {
        val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        LocationManagerCompat.isLocationEnabled(locationManager)
    } ?: false

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        binding.locationDisabledView.root.visibility = View.GONE
        binding.locationSearchFailed.root.visibility = View.GONE

        if (lastKnownLocation != null) {
            onLocationDetermined(lastKnownLocation)
            return
        }

        val context = activity ?: return

        if (!locationServicesEnabled()) {
            binding.locationDisabledView.root.visibility = View.VISIBLE
            return
        }

        binding.progressView.root.visibility = View.VISIBLE

        try {
            if (locationPermissionGranted(context)) {
                val locationResult = fusedLocationProviderClient?.lastLocation
                locationResult?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        onLocationDetermined(lastKnownLocation)
                    } else {
                        requestLocationUpdates()
                    }
                }
            }
        } catch (e: SecurityException) {
            // TODO: log error
            onLocationRetrievalFailed()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    onLocationRetrievalFailed()
                    return
                }

                onLocationDetermined(locationResult.locations.last())
                stopLocationUpdates()
            }
        }

        activity?.let {
            if (locationPermissionGranted(it)) {
                fusedLocationProviderClient?.requestLocationUpdates(
                    LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            }
        }
    }

    private fun stopLocationUpdates() = locationCallback?.let {
        fusedLocationProviderClient?.removeLocationUpdates(it)
    }

    // Re-direct user to device or app settings to enable location access
    private fun showSettingsDialog(message: String, intent: Intent) {
        activity?.let {
            AlertDialog.Builder(it).setTitle(it.getString(R.string.permission_denied))
                .setMessage(message)
                .setPositiveButton(it.getString(R.string.open_settings)) { dialog, _ ->
                    dialog.dismiss()
                    it.startActivity(intent)
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .create().show()
        }
    }

    private fun onLocationRetrievalFailed() {
        binding.locationSearchFailed.root.visibility = View.VISIBLE
    }

    companion object {
        const val TAG = "EclipseCenterFragment"
        private const val KEY_LOCATION = "location"

        @JvmStatic
        fun newInstance(): EclipseCenterFragment {
            return EclipseCenterFragment()
        }
    }
}