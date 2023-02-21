package com.example.aankh.ui.fragments.application

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aankh.R
import com.example.aankh.adapters.CheckPointsAdapter
import com.example.aankh.dataModels.CheckPointsDataModel
import com.example.aankh.databinding.FragmentTrackingBinding
import com.example.aankh.service.Tracker
import com.example.aankh.utils.Constants.ACTION_SERVICE_START
import com.example.aankh.utils.Constants.ACTION_SERVICE_STOP
import com.example.aankh.utils.Constants.BACKGROUND_PERMISSION_REQUEST_CODE
import com.example.aankh.utils.Constants.NEW_CHECK_POINT_NOTIFICATION_CHANNEL_ID
import com.example.aankh.utils.Constants.NEW_CHECK_POINT_NOTIFICATION_ID
import com.example.aankh.utils.Constants.NEW_CHECK_POINT_NOTIFICATION_NAME
import com.example.aankh.utils.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.example.aankh.utils.Extension.hide
import com.example.aankh.utils.Extension.show
import com.example.aankh.utils.MapUtil.setCameraPosition
import com.example.aankh.utils.NotificationModule.provideNewCheckPointPendingIntent
import com.example.aankh.utils.Permissions.hasBackgroundPermission
import com.example.aankh.utils.Permissions.hasLocationPermission
import com.example.aankh.utils.Permissions.requestBackgroundPermission
import com.example.aankh.utils.Permissions.requestLocationPermission
import com.example.aankh.viewModels.uiViewModels.TrackingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class TrackingFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    EasyPermissions.PermissionCallbacks {


    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private var locationList = mutableListOf<LatLng>()
    private var starTime = 0L
    private var stopTime = 0L
    private var started = false
    private var polylineList = mutableListOf<Polyline>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerList = mutableListOf<Marker>()
    private var checkPointMarkerList = mutableListOf<Marker>()
    private val viewModel: TrackingViewModel by activityViewModels()
    private lateinit var preferences: SharedPreferences

    private lateinit var id: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
//

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        try {
            preferences =
                activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)!!
            id = preferences?.getString("id", "").toString()
        } catch (e: Exception) {

        }
        Log.d("tracking user id", id)


//        TODO make a get request for today's check points
////        TODO get location permissions before moving to fragment

        binding.startButtonMapsActivity.setOnClickListener {
            onStartButtonClicked()
        }

        binding.stopButtonMapsActivity.setOnClickListener {
            onStopButtonClicked()
        }

        val adapter = CheckPointsAdapter()
        binding.checkPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.checkPointsRecyclerView.adapter = adapter


        viewModel.getCheckPointsData().observe(viewLifecycleOwner, Observer {
            adapter.updateCheckPoints(it)
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                addCheckPointsMarkers(it)
            }
        })


        viewModel.getEmergencyCheckPointData().observe(viewLifecycleOwner, Observer {
            val list = ArrayList<CheckPointsDataModel>()
            viewModel.getCheckPointsData()?.value?.let { it1 -> list.addAll(it1) }
            list.addAll(it)
            adapter.updateCheckPoints(list)
//            TODO here we have error change this check points addition feature
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                addCheckPointsMarkers(list)
            }

//TODO send the notification of changes
// TODO update the list for notification fragment

            createNotificationChannel(list.last().description)

        })


        return binding.root
    }


    private fun createNotificationChannel(title: String) {
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NEW_CHECK_POINT_NOTIFICATION_CHANNEL_ID,
                NEW_CHECK_POINT_NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val sound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            notificationManager.createNotificationChannel(channel)
            val notification =
                NotificationCompat.Builder(
                    requireContext(),
                    NEW_CHECK_POINT_NOTIFICATION_CHANNEL_ID
                )
                    .setAutoCancel(true).setContentTitle(title)
                    .setOngoing(false).setSmallIcon(R.drawable.logo)
                    .setContentIntent(provideNewCheckPointPendingIntent(requireContext()))
                    .setSound(sound).build()
            notificationManager.notify(NEW_CHECK_POINT_NOTIFICATION_ID, notification)
        }

    }


    fun addCheckPointsMarkers(checkPoints: ArrayList<CheckPointsDataModel>) {


        if (checkPointMarkerList.isNotEmpty()) {
            for (marker in checkPointMarkerList) {
                marker.remove()
            }
            checkPointMarkerList.clear()
        }


        for (checkPoint in checkPoints) {
            val location = LatLng(checkPoint.latitude, checkPoint.longitude)
            val marker = map.addMarker(
                MarkerOptions().position(location).title(checkPoint.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            marker?.let { checkPointMarkerList.add(it) }


        }
    }


    private fun onStartButtonClicked() {
        if (hasBackgroundPermission(requireContext()) && hasLocationPermission(requireContext())) {
            onStartCommands()
        } else if (hasBackgroundPermission(requireContext())) {
            requestLocationPermission(this)
        } else {
            requestBackgroundPermission(this)
            requestLocationPermission(this)
        }

    }

    @SuppressLint("MissingPermission")
    private fun onStartCommands() {
        binding.startButtonMapsActivity.hide()
        binding.stopButtonMapsActivity.show()
        sendActionCommandtoService(ACTION_SERVICE_START)
    }


    private fun onStopButtonClicked() {
        AlertDialog.Builder(requireContext()).setTitle("Stop Tracking")
            .setMessage("We will send your today's report to authorities once you stop tracking your activity. Are you sure that you want to stop tracking?")
            .setPositiveButton("No") { dialog, _ ->
                dialog.dismiss()
            }.setNegativeButton("Yes") { _, _ ->
                binding.stopButtonMapsActivity.hide()
                binding.startButtonMapsActivity.show()
                viewModel.postStartAndStopTime(id, starTime, stopTime)


                sendActionCommandtoService(ACTION_SERVICE_STOP)
                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)
                    mapReset()
                    starTime = 0L
                    stopTime = 0L
                }

            }.show()
    }


    private fun sendActionCommandtoService(action: String) {
        Intent(requireContext(), Tracker::class.java).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        viewModel.fetchCheckPoints(id)


        map = googleMap
        map.isMyLocationEnabled = true
        map.setMaxZoomPreference(14F)
        map.setOnMyLocationButtonClickListener(this)
        map.uiSettings.apply {
            isZoomGesturesEnabled = true
            isCompassEnabled = true
            isZoomControlsEnabled = true
            isScrollGesturesEnabled = true
            isMapToolbarEnabled = false
        }
        observeTrackerService()
        if (started) {
            binding.startButtonMapsActivity.hide()
            binding.stopButtonMapsActivity.show()
            map.isMyLocationEnabled = true
        }

    }

    @SuppressLint("MissingPermission")
    private fun setMainCameraPosition() {

        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(LatLng(23.027133233707815, 72.56050555260956))
                    .zoom(15f).build()
            )
        )

    }


    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    @SuppressLint("MissingPermission")
    private fun mapReset() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val lastKnownLocation = LatLng(
                it.result.latitude, it.result.longitude
            )
            for (polyline in polylineList) {
                polyline.remove()
            }
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    setCameraPosition(lastKnownLocation)
                )
            )
            locationList.clear()
            for (marker in markerList) {
                marker.remove()
            }
            markerList.clear()

        }
    }

    private fun observeTrackerService() {
        Tracker.locationLiveData.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                locationList = it
                if (locationList.size > 1) {
                    viewModel.postCurrentLocation(id, locationList.last())
                    viewModel.getEmergencyCheckPoint(id)
                }
                drawPolyline()
//                followPolyline()
            }
        }
        Tracker.started.observe(viewLifecycleOwner) {
            started = it
        }

        Tracker.startTime.observe(viewLifecycleOwner) {
            starTime = it
        }
        Tracker.stopTime.observe(viewLifecycleOwner) {
            stopTime = it
            if (stopTime != 0L) {
                postEndRoute()
                showBiggerPicture()
            }
        }
    }


    private fun postEndRoute() {

        viewModel.postEndRoute(id, locationList)

    }


    private fun showBiggerPicture() {
        if (locationList.size > 2) {
            Log.d("tracking", "showBiggerPicture: if started")
            val bounds = LatLngBounds.Builder()
            for (location in locationList) {
                bounds.include(location)
            }
            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds.build(), 100), 2000, null
            )
            addMarker(locationList.first())
            addMarker(locationList.last())
        } else if (stopTime != 0L) {
            Log.d("tracking", "showBiggerPicture: else started")
            Toast.makeText(
                requireContext(), "The movement of device is very less", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addMarker(position: LatLng) {
        val marker = map.addMarker(MarkerOptions().position(position))
        marker?.let { markerList.add(it) }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        onStartCommands()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
                requestLocationPermission(this)
            } else if (requestCode == BACKGROUND_PERMISSION_REQUEST_CODE) {
                requestBackgroundPermission(this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun followPolyline() {
        if (locationList.isNotEmpty()) {
            map.animateCamera(
                (CameraUpdateFactory.newCameraPosition(
                    setCameraPosition(
                        locationList.last()
                    )
                )), 100, null
            )
        }
    }

    private fun drawPolyline() {
        val polyline = map.addPolyline(PolylineOptions().apply {
            width(10f)
            color(Color.BLUE)
            startCap(ButtCap())
            endCap(ButtCap())
            jointType(JointType.ROUND)
            addAll(locationList)
        })
        polylineList.add(polyline)
    }
}