package com.example.aankh.ui.fragments.application

import android.annotation.SuppressLint

import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aankh.R
import com.example.aankh.adapters.CheckPointsAdapter
import com.example.aankh.dataModels.CheckPointsDataModel
import com.example.aankh.databinding.FragmentTrackingBinding

import com.example.aankh.service.Tracker
import com.example.aankh.utils.Constants.ACTION_SERVICE_START
import com.example.aankh.utils.Constants.ACTION_SERVICE_STOP
import com.example.aankh.utils.Constants.BACKGROUND_PERMISSION_REQUEST_CODE
import com.example.aankh.utils.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.example.aankh.utils.Extension.hide
import com.example.aankh.utils.Extension.show
import com.example.aankh.utils.MapUtil.setCameraPosition
import com.example.aankh.utils.Permissions.hasBackgroundPermission
import com.example.aankh.utils.Permissions.hasLocationPermission
import com.example.aankh.utils.Permissions.requestBackgroundPermission
import com.example.aankh.utils.Permissions.requestLocationPermission
import com.example.aankh.viewModels.uiViewModels.TrackingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import pub.devrel.easypermissions.AppSettingsDialog

import pub.devrel.easypermissions.EasyPermissions

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

    private val viewModel: TrackingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
//
//        trackingViewModel = ViewModelProvider


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
            addCheckPointsMarkers(it)
        })


        return binding.root
    }


    fun addCheckPointsMarkers(checkPoints: ArrayList<CheckPointsDataModel>) {
        for (checkPoint in checkPoints) {
            val location = LatLng(checkPoint.latitude, checkPoint.longitude)
            Log.d("user location", location.toString())
            map.addMarker(
                MarkerOptions().position(location).title(checkPoint.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )

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
                sendActionCommandtoService(ACTION_SERVICE_STOP)
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
//        TODO make a get request
        val preferences =
            activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)
        val id = preferences?.getString("id", "")
//        it will not fetch the data if the id is empty
        id?.let {
            if (id.equals("")) {
                Toast.makeText(
                    requireContext(),
                    "ID is empty log out and then log in again!!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.fetchCheckPoints(it)
            }
        }



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
//            locationList.clear()
            for (marker in markerList) {
                marker.remove()
            }
            markerList.clear()
        }
    }

    private fun observeTrackerService() {
        Tracker.locationLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                locationList = it
                if (locationList.size > 1) {
//                   TODO send the last location post request
                    viewModel.postCurrentLocation(locationList.last())
                }
                drawPolyline()
                followPolyline()
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
                showBiggerPicture()
//               TODO show the time and distance to user
            }
        }
    }


    private fun showBiggerPicture() {
        if (locationList.size > 5) {
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
        } else {
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